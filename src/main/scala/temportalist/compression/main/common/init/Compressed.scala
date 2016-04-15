package temportalist.compression.main.common.init

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import temportalist.compression.main.common.item.{ItemBlockCompressed, ItemCompressed}
import temportalist.origin.api.common.helper.Names

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
object Compressed {

	def create(itemStack: ItemStack, withSize: Boolean = false): ItemStack = {
		val compressed = new ItemStack(ModItems.item, 1, 0)
		val tagCom = new NBTTagCompound
		tagCom.setString("name", Names.getName(itemStack))
		tagCom.setString("display", itemStack.getItem.getItemStackDisplayName(itemStack))
		tagCom.setLong("size", if (withSize) itemStack.stackSize else 1)
		compressed.setTagCompound(tagCom)
		compressed
	}

	def canCompressItem(itemStack: ItemStack): Boolean = {
		itemStack.getItem match {
			case compressed: ItemCompressed => false
			case compressed: ItemBlockCompressed => false
			case _ =>

				itemStack.getItem.getItemStackLimit(itemStack) > 1
		}
	}

	def getDisplayName(itemStack: ItemStack): String = {
		"_" + " Compressed " + itemStack.getTagCompound.getString("display")
	}

	def createSampleStack(itemStack: ItemStack): ItemStack = {
		Names.getItemStack(itemStack.getTagCompound.getString("name"))
	}

	def getSampleState(itemStack: ItemStack): IBlockState = {
		Names.getState(itemStack.getTagCompound.getString("name"))
	}

	def getSize(itemStack: ItemStack): Long = itemStack.getTagCompound.getLong("size")

}
