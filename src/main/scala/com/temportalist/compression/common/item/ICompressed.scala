package com.temportalist.compression.common.item

import java.util

import com.temportalist.compression.common.Tiers
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.origin.api.client.utility.Keys
import com.temportalist.origin.api.common.utility.Generic
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, Item}

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
trait ICompressed extends Item {

	override def getItemStackDisplayName(stack: ItemStack): String = {
		if (stack.hasTagCompound)
			Tiers.getName(CBlocks.getInnerSize(stack)) + " Compressed " +
					CBlocks.getDisplayName(stack)
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
