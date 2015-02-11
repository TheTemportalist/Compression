package com.temportalist.compression.common.tile

import com.temportalist.origin.library.common.lib.NameParser
import com.temportalist.origin.wrapper.common.tile.TEWrapper
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class TECompressed extends TEWrapper("Compressed") {

	private var innerState: ItemStack = null
	private var size: Long = 0

	def setState(state: IBlockState): Unit = {
		this.setState(new ItemStack(state.getBlock, 1, state.getBlock.getMetaFromState(state)))
	}

	def setState(state: ItemStack): Unit = {
		this.innerState = state
		//this.markDirty()
	}

	def getState(): ItemStack = this.innerState

	def setSize(size: Long): Unit = {
		this.size = size
		//this.markDirty()
	}

	def getSize(): Long = this.size

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)
		if (this.innerState != null) {
			tagCom.setString("inner",
				NameParser.getName(this.innerState, hasID = true, hasMeta = true)
			)
		}
		tagCom.setLong("stackSize", this.size)
	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)
		println ("reading " + tagCom.toString)
		this.innerState = NameParser.getItemStack(tagCom.getString("inner"))
		this.size = tagCom.getLong("stackSize")
	}

}
