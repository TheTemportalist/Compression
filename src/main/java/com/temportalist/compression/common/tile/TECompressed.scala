package com.temportalist.compression.common.tile

import com.temportalist.origin.library.common.lib.NameParser
import com.temportalist.origin.wrapper.common.tile.TEWrapper
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class TECompressed extends TEWrapper("Compressed") {

	var blockState: IBlockState = null
	var tier: Int = 1

	def setBlock(state: IBlockState): Unit = {
		this.blockState = state
		this.markDirty()
		this.markforUpdate()
	}

	def getBlockState(): IBlockState = this.blockState

	def setTier(t: Int): Unit = {
		this.tier = t
		this.markDirty()
	}

	def getTier(): Int = this.tier

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)
		if (this.blockState != null)
			tagCom.setString("blockName",
				NameParser.getName(this.blockState, hasID = true, hasMeta = true)
			)
		tagCom.setInteger("tier", this.tier)
	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)
		this.blockState = NameParser.getState(tagCom.getString("blockName"))
		this.tier = tagCom.getInteger("tier")
	}

}
