package temportalist.compression.main.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import temportalist.compression.main.common.Effects;
import temportalist.origin.api.common.lib.Vect;

/**
 * Created by TheTemportalist on 4/16/2016.
 *
 * @author TheTemportalist
 */
public class EntityItemCompressed extends EntityItem {

	public EntityItemCompressed(World worldIn) {
		super(worldIn);
	}

	public EntityItemCompressed(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityItemCompressed(World worldIn, double x, double y, double z,
			ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}

	public EntityItemCompressed(World worldIn, Vect pos, Vect motion, ItemStack stack) {
		super(worldIn, pos.x(), pos.y(), pos.z(), stack);
		this.motionX = motion.x();
		this.motionY = motion.y();
		this.motionZ = motion.z();
		this.setDefaultPickupDelay();
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!this.getEntityItem().hasTagCompound()) this.setDead();
		Effects.onEntityUpdateCompressed(this.getEntityWorld(), this, this.getEntityItem());
	}

}
