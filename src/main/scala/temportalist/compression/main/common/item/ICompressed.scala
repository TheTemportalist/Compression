package temportalist.compression.main.common.item

import java.util

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.world.World
import net.minecraftforge.client.settings.KeyModifier
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.compression.main.common.Effects
import temportalist.compression.main.common.entity.EntityItemCompressed
import temportalist.compression.main.common.init.Compressed
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
trait ICompressed extends Item {

	override def getItemStackDisplayName(itemStack: ItemStack): String = {
		if (itemStack.hasTagCompound) Compressed.getDisplayName(itemStack)
		else super.getItemStackDisplayName(itemStack)
	}

	@SideOnly(Side.CLIENT)
	override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String],
			advanced: Boolean): Unit = {
		super.addInformation(stack, playerIn, tooltip, advanced)

		if (GuiScreen.isShiftKeyDown) {
			tooltip.add(Compressed.getTier(stack).toString)
			tooltip.add(Compressed.getSize(stack).toString)
			tooltip.add(this.getClass.getSimpleName)
			tooltip.add(Compressed.getStackName(stack))
		}

	}

	override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity,
			itemSlot: Int, isSelected: Boolean): Unit = {
		entityIn match {
			case player: EntityPlayer =>
				if (!player.isSneaking)
					Effects.onInvUpdateCompressed(worldIn, player, stack)
			case _ =>
		}
	}

	override def hasCustomEntity(itemStack: ItemStack): Boolean = true

	override def createEntity(world: World, oldEI: Entity, itemStack: ItemStack): Entity = {
		new EntityItemCompressed(world, new Vect(oldEI),
			new Vect(oldEI.motionX, oldEI.motionY, oldEI.motionZ), itemStack)
	}



}
