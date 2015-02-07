package com.temportalist.compression.common.item

import com.temportalist.compression.common.lib.Langauge
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
			Langauge.tiers(stack.getTagCompound.getInteger("tier")) + " Compressed " +
					stack.getTagCompound.getString("blockDisplay")
		}
		else super.getItemStackDisplayName(stack)
	}

}
