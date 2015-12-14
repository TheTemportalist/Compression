package com.temportalist.compression.common.tile

import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.{Rank, CompressedStack, Compression}
import com.temportalist.origin.api.common.inventory.IInv
import com.temportalist.origin.api.common.tile.ITileSaver
import com.temportalist.origin.api.common.utility.Stacks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 * Created by TheTemportalist on 9/8/2015.
 */
class TECompress extends TileEntity with IInv with ITileSaver {

	private var maxTime = 40
	private var time: Int = 0

	this.setSlots(2, 64)

	override def getInventoryName: String = "compressor"

	override def updateEntity(): Unit = {
		val stack = this.getStackInSlot(0)
		if (stack != null) {
			val isCompressedInput = CompressedStack.isCompressedStack(stack)
			val compressedInput = if (isCompressedInput) stack.copy()
			else CBlocks.wrapInnerStack(stack.copy(), stack.stackSize)

			var doRun = false
			var newInput: ItemStack = null
			var newOutput: ItemStack = null

			val outputStack = this.getStackInSlot(1)

			val nullOutputStack = outputStack == null
			if (!isCompressedInput && stack.stackSize <= 1 && nullOutputStack) return
			if (nullOutputStack || Stacks.doStacksMatch(CompressedStack.getStackType(outputStack),
				CompressedStack.getStackType(compressedInput), nbt = true)) {

				val amountToCompress = Math.min(
					Rank.getHighestRank.getMaximum -
							(if (nullOutputStack) 0 else CompressedStack.getCompressedSize(outputStack)),
					if (isCompressedInput) CompressedStack.getCompressedSize(compressedInput)
					else Math.min(CompressedStack.getCompressedSize(compressedInput), 9)
				).toInt

				if (amountToCompress > 0) {
					newOutput = (if (nullOutputStack) compressedInput else outputStack).copy()
					CompressedStack.setCompressedSize(newOutput,
						(if (nullOutputStack) 0 else CompressedStack.getCompressedSize(newOutput)) +
								amountToCompress)

					newInput = stack.copy()
					if (isCompressedInput) {
						CompressedStack.setCompressedSize(newInput,
							CompressedStack.getCompressedSize(newInput) - amountToCompress)
						if (CompressedStack.getCompressedSize(newInput) <= 0) newInput = null
					}
					else {
						newInput.stackSize = newInput.stackSize - amountToCompress
						if (newInput.stackSize == 0) newInput = null
					}
					doRun = true
				}

			}


			if (doRun)
				if (this.time <= 0) {
					this.setInventorySlotContents(0, newInput)
					this.setInventorySlotContents(1, newOutput)
					this.time = this.maxTime
				}
				else this.time -= 1

		}
		else if (this.time != this.maxTime) this.time = this.maxTime
	}

	def getTime: Int = this.time

	override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = {
		// can only input in slot 0
		// inputting stack has to be compressable
		// if slot empty, good
		// if slot has things in, stacks must match and total size <= 9
		if (index == 0 && CBlocks.canStackBeCompressed(stack)) {
			val stackInSlot = this.getStackInSlot(0)
			stackInSlot == null || Stacks.doStacksMatch(stackInSlot, stack, nbt = true)
		}
		else false
	}

	override def getAccessibleSlotsFromSide(side: Int): Array[Int] = Array[Int](0, 1)

	override def writeToNBT(tagCompound: NBTTagCompound): Unit = {
		super.writeToNBT(tagCompound)
		tagCompound.setInteger("maxTime", this.maxTime)
		tagCompound.setInteger("time", this.time)

	}

	override def readFromNBT(tagCompound: NBTTagCompound): Unit = {
		super.readFromNBT(tagCompound)
		this.maxTime = tagCompound.getInteger("maxTime")
		this.time = tagCompound.getInteger("time")

	}

}
