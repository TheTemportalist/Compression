package com.temportalist.compression.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityCompressed extends EntityItem {

    public EntityCompressed(World worldIn) {
        super(worldIn);
    }

    public EntityCompressed(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCompressed(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public EntityCompressed(World worldIn, Vec3d position, Vec3d motion, ItemStack stack) {
        super(worldIn, position.x, position.y, position.z, stack);
        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;
        this.setDefaultPickupDelay();
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.getItem().hasTagCompound()) this.setDead();

    }

}
