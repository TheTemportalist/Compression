package com.temportalist.compression.common.item

import java.util

import com.temportalist.compression.common.lib.Tupla
import com.temportalist.origin.library.client.utility.Keys
import com.temportalist.origin.library.common.utility.Generic
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, ItemBlock}
import net.minecraft.block.Block

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class ItemBlockCompressed(block: Block) extends ItemBlock(block) {

	this.setHasSubtypes(true)

	override def getItemStackDisplayName(stack: ItemStack): String = {
		if (stack.hasTagCompound) {
			Tupla.getName(stack.getTagCompound.getLong("stackSize")) + " Compressed " +
					stack.getTagCompound.getString("display")
		}
		else super.getItemStackDisplayName(stack)
	}

	override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[_],
			advanced: Boolean): Unit = {
		if (stack.hasTagCompound) {
			if (!Keys.isShiftKeyDown) Generic.addToList(tooltip, "Hold SHIFT for stats")
			else {
				Generic.addToList(tooltip, "Size: " + stack.getTagCompound.getLong("stackSize"))
			}
		}
	}

}
