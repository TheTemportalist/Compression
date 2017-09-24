package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.effects.Effects;
import com.temportalist.compression.common.effects.EnumEffect;
import com.temportalist.compression.common.init.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class TileCompressedTickable extends TileCompressed implements ITickable {

    static abstract class Ticker {

        private int ticksUntil;
        private final int delayRangeMin, delayRangeMax; // max is exclusive

        public Ticker(int minDelay, int maxDelay) {
            this.delayRangeMin = minDelay;
            this.delayRangeMax = maxDelay;
            this.ticksUntil = maxDelay;
        }

        public Ticker(int maxDelayInTicks) {
            this(0, maxDelayInTicks);
        }

        public void update(Random random) {

            // Check to see if effect can occur
            if (this.ticksUntil <= 0) {
                // Do the effect
                this.doEffect();
                // Reset the tick counter
                this.ticksUntil = this.getNextTicksUntil(random);
            } else {
                // Decrement the ticks
                this.ticksUntil -= 1;
            }

        }

        private int getNextTicksUntil(Random random) {
            return this.delayRangeMin == 0 ? this.delayRangeMax : random.nextInt(this.delayRangeMax - this.delayRangeMin) + this.delayRangeMin;
        }

        abstract public void doEffect();

    }

    // The absolute center of this blocktile
    private Vec3d center;

    // The ticker structures to track timing
    private Ticker tickerDestroyBlocks, tickerDamageEntities;
    // The bounding boxes for attracting entities
    private AxisAlignedBB aoeAttract, aoeDamage;
    // The amount of energy that has been accumulated
    private float energyAmount;

    // The radius from this blocks that the black hole can consume
    private final int radiusBlockDestroy = 5;
    // The radius from this block that the block can pull entities
    private final float radiusAttractEntities = 10;
    // The radius from this block that the block can damage entities
    private final float radiusDamage = 1.5f;

    public TileCompressedTickable() {
        this.center = new Vec3d(this.getPos()).add(new Vec3d(0.5, 0.5, 0.5));
        this.tickerDestroyBlocks = this.createTickerDestroyBlocks();
        this.tickerDamageEntities = this.createTickerDamageEntities();
        this.aoeAttract = new AxisAlignedBB(this.getPos()).grow(this.radiusAttractEntities);
        this.aoeDamage = new AxisAlignedBB(this.getPos()).grow(this.radiusDamage);
        this.energyAmount = 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCom = super.writeToNBT(compound);
        tagCom.setFloat("energyAmount", this.energyAmount);
        return tagCom;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.energyAmount = compound.getFloat("energyAmount");
    }

    public float getEnergyAmount() {
        return energyAmount;
    }

    @Override
    public void update() {
        this.updateBlackHole();
    }

    private void updateBlackHole() {
        if (!EnumEffect.BLACK_HOLE.canDoEffect(this.getTier())) return;

        // Update ticking effects
        this.tickerDestroyBlocks.update(this.getWorld().rand);
        this.tickerDamageEntities.update(this.getWorld().rand);

        // Attract local entities
        this.attractEntities();

    }

    private Ticker createTickerDestroyBlocks() {

        final int maxLoops = 100;

        int delayBase = 20 * 20; // 20 seconds
        int deviation = 2 * 20; // 2 seconds
        return new Ticker(delayBase, delayBase + deviation) {

            @Override
            public void doEffect() {
                int loops = 0;
                BlockPos pos;
                IBlockState state;
                do {
                    pos = this.getRandBlockPos().add(getPos());
                    state = getWorld().getBlockState(pos);
                    loops++;
                } while (loops < maxLoops && !canBeDestroyed(state, pos));

                if (canBeDestroyed(state, pos)) {
                    consume(state, pos);
                    getWorld().setBlockToAir(pos);
                }

            }

            int getRandBlockCoord() {
                return getWorld().rand.nextInt(radiusBlockDestroy);
            }

            BlockPos getRandBlockPos() {
                //return BlockPos.ORIGIN.add(0, 1, 0);
                ////*
                return new BlockPos(
                        this.getRandBlockCoord(),
                        this.getRandBlockCoord(),
                        this.getRandBlockCoord()
                );
                //*/
            }

        };
    }

    private Ticker createTickerDamageEntities() {

        return new Ticker(30 * 20) { // do damage every 30 seconds
            @Override
            public void doEffect() {

                List<Entity> entities = getWorld().getEntitiesWithinAABB(Entity.class, aoeDamage,
                        (Entity entity) -> entity instanceof EntityItem || entity instanceof EntityLivingBase
                );
                for (Entity entity : entities) {
                    if (entity instanceof EntityItem) {
                        consume(((EntityItem) entity).getItem());
                    } else if (entity instanceof EntityLivingBase) {
                        EntityLivingBase living = (EntityLivingBase) entity;
                        if (living.attackEntityFrom(DamageSource.OUT_OF_WORLD, 1)) {
                            if (living.getHealth() <= 0) {
                                consume(living);
                            }
                        }
                    }
                }

            }
        };
    }

    public boolean canBeDestroyed(IBlockState state, BlockPos pos) {
        boolean thisPos = pos == this.getPos();
        boolean validDist = pos.distanceSq(this.getPos()) <= this.radiusBlockDestroy * this.radiusBlockDestroy;
        boolean validMaterial = state.getMaterial() != Material.AIR && !state.getMaterial().isLiquid();
        boolean validTile = !state.getBlock().hasTileEntity(state) && getWorld().getTileEntity(pos) == null;

        return !thisPos && validDist && validMaterial && validTile;
    }

    public void consume(IBlockState state, BlockPos pos) {
        if (!EnumEffect.POTENTIAL_ENERGY.canDoEffect(this.getTier())) return;
        this.gainEnergy(1);
    }

    public void consume(ItemStack stack) {
        if (!EnumEffect.POTENTIAL_ENERGY.canDoEffect(this.getTier())) return;
        this.gainEnergy(stack.getCount());
    }

    public void consume(EntityLivingBase entity) {
        if (!EnumEffect.POTENTIAL_ENERGY.canDoEffect(this.getTier())) return;
        this.gainEnergy(entity.getMaxHealth());
    }

    public void gainEnergy(float amount) {
        this.energyAmount += amount;
        ModBlocks.compressed.updateBlockStats(
                this.getWorld(), this.getPos(), this.getTier(), this.getSampleStack(), this.energyAmount);
    }

    public void attractEntities() {

        List<Entity> entities = this.getWorld().getEntitiesWithinAABB(Entity.class, this.aoeAttract,
                (Entity entity) -> {
                    if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) return false;
                    else return entity != null && !entity.getEntityBoundingBox().intersects(this.aoeDamage);
                }
        );
        for (Entity entity : entities) {
            Effects.pullEntityTowards(entity, this.center, 1);
        }

    }

}
