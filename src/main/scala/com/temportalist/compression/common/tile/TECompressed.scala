package com.temportalist.compression.common.tile

import com.temportalist.compression.common.Rank
import com.temportalist.origin.api.common.lib.NameParser
import com.temportalist.origin.api.common.tile.ITileSaver
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
class TECompressed extends TileEntity with ITileSaver {

	private var inner: ItemStack = null
	private var size: Long = 0

	override def canUpdate: Boolean = false

	def setStack(state: ItemStack): Unit = {
		this.inner = state
		this.markDirty()
	}

	def getStack: ItemStack = this.inner.copy()

	def getStackBlock: Block = Block.getBlockFromItem(this.inner.getItem)

	def getStackString: String = if (this.inner != null) NameParser.getName(this.inner) else null

	def getStackDisplay: String = if (this.inner != null) this.inner.getDisplayName else null

	def setSize(size: Long): Unit = {
		this.size = size
		this.markDirty()
	}

	def getSize: Long = this.size

	def getRank: Rank = Rank.getRank(this.size)

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)
		if (this.inner != null) {
			tagCom.setString("inner",
				NameParser.getName(this.inner, hasID = true, hasMeta = true)
			)
		}
		tagCom.setLong("stackSize", this.size)
	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)
		this.inner = NameParser.getItemStack(tagCom.getString("inner"))
		this.size = tagCom.getLong("stackSize")
	}

	def createBlackHole(): Unit = {

	}

}
