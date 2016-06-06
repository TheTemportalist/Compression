package temportalist.compression.main.common.block.tile

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import temportalist.origin.api.common.helper.Names
import temportalist.origin.api.common.tile.ITileSaver

/**
  *
  * Created by TheTemportalist on 4/15/2016.
  *
  * @author TheTemportalist
  */
class TileCompressed extends TileEntity with ITileSaver {

	private var itemStack: ItemStack = _
	private var size: Long = 0

	def setStack(state: ItemStack): Unit = {
		this.itemStack = state
		this.markDirty()
	}

	def getStack: ItemStack = this.itemStack

	def setSize(size: Long): Unit = {
		this.size = size
		this.markDirty()
	}

	def getSize: Long = this.size

	override def getUpdateTag: NBTTagCompound = {
		this.writeToNBT(new NBTTagCompound)
	}

	override def writeToNBT(nbt: NBTTagCompound): NBTTagCompound = {
		val tag = super.writeToNBT(nbt)
		if (this.itemStack != null)
			tag.setString("stack", Names.getName(this.itemStack, hasID = true, hasMeta = true))
		tag.setLong("size", this.size)
		tag
	}

	override def readFromNBT(nbt: NBTTagCompound): Unit = {
		super.readFromNBT(nbt)
		if (nbt.hasKey("stack"))
			this.itemStack = Names.getItemStack(nbt.getString("stack"))
		this.size = nbt.getLong("size")
	}

}
