package com.temportalist.compression.common.container

import com.temportalist.compression.common.init.CBlocks
import com.temportalist.origin.api.common.inventory.IInv
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
class InventoryCompression(private val player: EntityPlayer) extends IInv {

	this.setSlots(1)
	private val internalStack = CBlocks.getInnerStack(player.getHeldItem).copy()
	this.updateInternalStack(player.getHeldItem)
	this.setInventorySlotContents(0, internalStack.copy())

	override def getInventoryName: String = "Compressed Stack"

	override def markDirty(): Unit = {
		this.player.inventory.markDirty()
	}

	override def setInventorySlotContents(slot: Int, stack: ItemStack): Unit = {
		if (stack == null) {
			println("remove stack")
			// removing stack
			if (!this.player.capabilities.isCreativeMode)
				this.decrCompressedSize(this.internalStack.stackSize)
			//this.markDirty()
		}
		else super.setInventorySlotContents(slot, stack)
	}

	private def updateInternalStack(compressed: ItemStack): Unit = {
		val remainingSize = CBlocks.getInnerSize(compressed)
		this.internalStack.stackSize = 64
		this.slots(0) = null
		/*
		if (remainingSize <= 64)
			this.internalStack.stackSize = CBlocks.getInnerSize(compressed).toInt - 1
		if (this.internalStack.stackSize <= 0) this.slots(0) = null
		else this.slots(0) = this.internalStack.copy()
		*/
	}

	private def decrCompressedSize(amount: Int): Unit = {
		val compressed = player.getHeldItem.copy()
		CBlocks.addToInnerSize(compressed, -amount)
		println(compressed.getTagCompound)
		player.inventory.mainInventory(player.inventory.currentItem) = compressed
		this.markDirty()
		this.updateInternalStack(compressed)
	}

}
