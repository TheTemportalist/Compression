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

	override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
		val tag = super.writeToNBT(compound)
		if (this.itemStack != null)
			tag.setString("stack", Names.getName(this.itemStack, hasID = true, hasMeta = true))
		tag.setLong("size", this.size)
		tag
	}

	override def readFromNBT(compound: NBTTagCompound): Unit = {
		super.readFromNBT(compound)
		this.itemStack = Names.getItemStack(compound.getString("stack"))
		this.size = compound.getLong("size")
	}

}
