package temportalist.compression.main.common.item

import java.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.compression.main.common.init.Compressed
import temportalist.origin.api.client.Keys

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

		if (Keys.isShiftKeyDown) {
			tooltip.add(Compressed.getTier(stack).toString)
			tooltip.add(Compressed.getSize(stack).toString)
			tooltip.add(this.getClass.getSimpleName)
		}

	}

}
