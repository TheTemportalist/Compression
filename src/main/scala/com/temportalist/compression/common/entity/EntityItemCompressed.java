package com.temportalist.compression.common.entity;

import com.temportalist.compression.common.Compression;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author TheTemportalist  6/23/15
 */
public class EntityItemCompressed extends EntityItem {

	public EntityItemCompressed(World world) {
		super(world);
	}

	public EntityItemCompressed(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityItemCompressed(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		Compression.onCompressedEntityUpdate(this, this.getEntityItem());
	}

}
