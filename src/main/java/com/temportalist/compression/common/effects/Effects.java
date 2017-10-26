package com.temportalist.compression.common.effects;

import com.temportalist.compression.common.entity.EntityCompressed;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber
public class Effects {

    public static ResourceLocation portalSound = new ResourceLocation("entity.endermen.teleport");

    public static boolean canUseAny(ItemStack stack, EnumEffect... effects) {
        if (!CompressedStack.isCompressed(stack)) return false;
        for (EnumEffect effect : effects) {
            if (effect != null && effect.canDoEffect(stack)) return true; // effects tier set via config
        }
        return false;
    }

    public static boolean doTickCompressedBlock(ItemStack stack) {
        return Effects.canUseAny(stack, EnumEffect.BLACK_HOLE);
    }

    @SubscribeEvent
    public static void itemPickup(EntityItemPickupEvent event){
        EntityPlayer player = event.getEntityPlayer();
        EntityItem entity = event.getItem();
        if (player == null || entity == null) return;

        ItemStack entityStack = entity.getItem();
        ItemStack entityStackSample = null;
        EnumTier entityTier = null;
        if (CompressedStack.isCompressed(entityStack)) {
            entityStackSample = CompressedStack.createSampleStack(entityStack);
            entityTier = CompressedStack.getTier(entityStack);
        }
        else {
            entityStackSample = entityStack.copy();
            // tier stays null
        }

        int indexCompressor = Effects.matchItemStack(player, entityStackSample, true,
                (Tuple<ItemStack, Integer> invStack, Boolean isCompressed) ->
                        isCompressed && canUseAny(invStack.getFirst(), EnumEffect.COMPRESSOR)
        );

        if (indexCompressor >= 0) {
            event.setCanceled(true);
            event.getItem().setDead();
            Effects.compressInventory(player, entityStackSample, entityTier);
        }

    }

    public static boolean doesMatch(ItemStack a, ItemStack b) {
        boolean sameItem = a.getItem() == b.getItem();
        boolean sameDamage = a.getItemDamage() == b.getItemDamage();
        boolean sameTag =  ItemStack.areItemStackTagsEqual(a, b);
        return sameItem && sameDamage && sameTag;
    }

    public static int matchItemStack(EntityPlayer player, ItemStack sampleIn, boolean includeHotbar,
                                     @Nullable BiFunction<Tuple<ItemStack, Integer>, Boolean, Boolean> onFound) {
        int slotMin = 0, slotMax =
                        player.inventory.mainInventory.size() +
                        player.inventory.armorInventory.size() +
                        player.inventory.offHandInventory.size();

        for (int slot = slotMin; slot < slotMax; slot++) {
            if (!includeHotbar && InventoryPlayer.isHotbar(slot)) continue;
            ItemStack stackInSlot = player.inventory.getStackInSlot(slot);
            if (stackInSlot != ItemStack.EMPTY) {
                boolean isCompressed = CompressedStack.isCompressed(stackInSlot);
                ItemStack sample = isCompressed ? CompressedStack.createSampleStack(stackInSlot) : stackInSlot.copy();
                if (sample != null && Effects.doesMatch(sample, sampleIn) &&
                        (onFound == null || onFound.apply(new Tuple<>(stackInSlot, slot), isCompressed))
                ) {
                    return slot;
                }
            }
        }
        return -1;
    }

    public static void compressInventory(EntityPlayer player, ItemStack stackIn, EnumTier tierIn) {

        ArrayList<ItemStack> alikeStacks = new ArrayList<>();

        Effects.matchItemStack(player, stackIn, false,
                (Tuple<ItemStack, Integer> invStack, Boolean isCompressed) -> {
                    alikeStacks.add(player.inventory.removeStackFromSlot(invStack.getSecond()));
                    return false;
                }
        );

        long totalCount = (tierIn == null ? 1 : tierIn.getSizeMax()) * stackIn.getCount();
        for (ItemStack stack : alikeStacks) {
            EnumTier tier = CompressedStack.getTier(stack);
            totalCount += stack.getCount() * (tier != null ? tier.getSizeMax() : 1);
        }

        List<ItemStack> stackList = CompressedStack.createStackList(
                CompressedStack.createSampleStack(stackIn), totalCount
        );
        int slot;
        for (ItemStack stack : stackList) {
            slot = Effects.getFirstEmptyStackNonHotbar(player.inventory);
            if (!player.inventory.add(slot, stack)) {
                player.dropItem(stack, true, false);
            }
        }

        player.inventory.markDirty();
    }

    public static int getFirstEmptyStackNonHotbar(InventoryPlayer player) {
        for (int i = InventoryPlayer.getHotbarSize(); i < player.mainInventory.size(); ++i)
        {
            if ((player.mainInventory.get(i)).isEmpty())
            {
                return i;
            }
        }
        return -1;
    }

    public static void attractEntities(AxisAlignedBB boundingBox, float radius, World world, Entity entity,
                                Vec3d position,
                                @Nullable Function<Entity, Boolean> isValid,
                                @Nullable Function<Entity, Float> getSpeed,
                                @Nullable Consumer<Entity> onPull
                                ) {
        boundingBox = boundingBox.grow(radius, radius, radius);
        List<Entity> ents = world.getEntitiesWithinAABBExcludingEntity(entity, boundingBox);
        for (Entity ent : ents ) {
            if (isValid == null || isValid.apply(ent)) {
                float speed  = getSpeed == null ? 1f : getSpeed.apply(ent);
                if (speed <= 0) continue;
                Effects.pullEntityTowards(ent, position, speed);
                if (onPull != null) onPull.accept(ent);
            }
        }
    }

    public static void pullEntityTowards(Entity ent, Vec3d position, float speed) {
        // Vector from player to entity position: position - entity.position
        Vec3d diff = position.subtract(ent.getPositionVector());
        diff = diff.normalize();//.scale(speed);
        diff = diff.scale(speed);

        ent.setVelocity(diff.x, diff.y, diff.z);

        if (ent.isCollidedHorizontally) {
            ent.motionY += 1;
        }

        ent.fallDistance = 0f;

        if (ent.getEntityWorld().rand.nextInt(20) == 0) {
            SoundEvent sound = SoundEvent.REGISTRY.getObject(Effects.portalSound);
            float pitch = 0.85f - ent.getEntityWorld().rand.nextFloat() * 3f / 10f;
            ent.getEntityWorld().playSound(
                    ent.posX, ent.posY, ent.posZ,
                    sound, SoundCategory.BLOCKS, 0.6f, pitch, false
            );
        }
    }

    public static void onUpdateEntityInventory(World world, Entity entity, ItemStack stack) {
        if (entity.isSneaking()) return;
        if (!Effects.canUseAny(stack, EnumEffect.MAGNET_I, EnumEffect.MAGNET_II)) return;
        boolean validMagnetII = Effects.canUseAny(stack, EnumEffect.getLowestTier(EnumEffect.MAGNET_I, EnumEffect.MAGNET_II));

        ItemStack sample = CompressedStack.createSampleStack(stack);
        // radiusFactor is the tier of the stack - the lowest possible tier for a magnet
        float radiusFactor = CompressedStack.getTier(stack).ordinal() - EnumEffect.MAGNET_I.getTier().ordinal();
        float radius = 1.5f + radiusFactor;

        AxisAlignedBB boundingBox = entity.getEntityBoundingBox().expand(1, 0.5, 1);
        Effects.attractEntities(boundingBox, radius, world, entity,
                entity.getPositionVector(),// new Vec3d(entity.motionX, entity.motionY, entity.motionZ),
                // isValid: should entity be pulled
                (Entity ent) -> (ent instanceof EntityItem) &&
                            (validMagnetII || Effects.doesMatch(sample,
                                    CompressedStack.createSampleStack(((EntityItem) ent).getItem())))
                ,
                // getSpeed: speed of the attraction
                null,
                // Post: When an entity is pulled
                null
        );

    }

    public static void onUpdateEntityItem(EntityCompressed entity, World world, ItemStack stack) {
        if (!CompressedStack.isCompressed(stack)) return;
        if (!Effects.canUseAny(stack, EnumEffect.ATTRACTION_I, EnumEffect.ATTRACTION_II, EnumEffect.ATTRACTION_III)) return;

        boolean canUseI = Effects.canUseAny(stack, EnumEffect.ATTRACTION_I);
        boolean canUseII = Effects.canUseAny(stack, EnumEffect.ATTRACTION_II);
        boolean canUseIII = Effects.canUseAny(stack, EnumEffect.ATTRACTION_III);

        ItemStack sample = CompressedStack.createSampleStack(stack);

        // radiusFactor is the tier of the stack - the lowest possible tier for a magnet
        float radiusFactor = CompressedStack.getTier(stack).ordinal() - EnumEffect.MAGNET_I.getTier().ordinal();
        float radius = 1.5f + radiusFactor;

        AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
        AxisAlignedBB boundingBoxConsume = boundingBox.grow(0.7f);
        AxisAlignedBB boundingBoxEnts = boundingBoxConsume.grow(1f);
        Effects.attractEntities(boundingBox, radius, world, entity,
                entity.getPositionVector(),// new Vec3d(entity.motionX, entity.motionY, entity.motionZ),
                // isValid: should entity be pulled
                (Entity ent) -> {
                    if (ent.getEntityBoundingBox().intersects(boundingBoxConsume)) return false;
                    // Attraction I and II: attracting items
                    if (ent instanceof EntityItem) {
                        return canUseII || (canUseI && Effects.doesMatch(sample,
                                CompressedStack.createSampleStack(((EntityItem)ent).getItem())));
                    }
                    else {
                        if (ent.getEntityBoundingBox().intersects(boundingBoxEnts)) return false;
                        else if (ent instanceof EntityPlayer) {
                            return canUseIII && !ent.isSneaking() && !((EntityPlayer)ent).isCreative();
                        }
                        else {
                            return canUseIII;
                        }
                    }
                },
                // getSpeed: speed of the attraction
                null,
                // Post: When an entity is pulled
                null
        );

    }

}
