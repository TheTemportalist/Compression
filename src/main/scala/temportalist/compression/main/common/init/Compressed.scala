package temportalist.compression.main.common.init

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import temportalist.compression.main.common.item.{ICompressed, ItemBlockCompressed, ItemCompressed}
import temportalist.compression.main.common.lib.EnumTier
import temportalist.origin.api.common.helper.Names

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
object Compressed {

	def create(itemStack: ItemStack, withSize: Boolean = false, tier: EnumTier = null): ItemStack = {
		val isBlock = itemStack.getItem.isInstanceOf[ItemBlock]
		val compressed = new ItemStack(if (isBlock) ModBlocks.blockItem else ModItems.item, 1, 0)
		val tagCom = new NBTTagCompound
		tagCom.setString("name", Names.getName(itemStack))
		tagCom.setString("display", itemStack.getItem.getItemStackDisplayName(itemStack))

		var size: Long = if (withSize) itemStack.stackSize else 1
		if (tier != null) size = tier.getSizeMax
		tagCom.setLong("size", size)

		compressed.setTagCompound(tagCom)
		compressed
	}

	def createWithSize(itemStack: ItemStack, size: Long): ItemStack = {
		val stack = this.create(itemStack)
		stack.getTagCompound.setLong("size", size)
		stack
	}

	def canCompressItem(itemStack: ItemStack): Boolean = {
		itemStack.getItem match {
			case compressed: ItemCompressed => false
			case compressed: ItemBlockCompressed => false
			case item: ItemBlock =>
				val block = Block.getBlockFromItem(item)
				val state = block.getStateFromMeta(itemStack.getItemDamage)

				block.isOpaqueCube(state) && block.isFullCube(state) &&
						block.getMaterial(state).blocksMovement() &&
						!block.hasTileEntity(state) &&
						item.getItemStackLimit(itemStack) > 1
			case _ =>

				itemStack.getItem.getItemStackLimit(itemStack) > 1
		}
	}

	def isCompressed(itemStack: ItemStack): Boolean = itemStack.getItem.isInstanceOf[ICompressed]

	def getSampleFromUnknown(itemStack: ItemStack): ItemStack = {
		if (this.isCompressed(itemStack)) {
			if (itemStack.hasTagCompound) this.getSampleStack(itemStack)
			else null
		}
		else {
			val stack = itemStack.copy()
			stack.stackSize = 1
			stack
		}
	}

	def getTotalSizeForUnknown(itemStack: ItemStack): Long = {
		itemStack.stackSize * (if (this.isCompressed(itemStack)) this.getSize(itemStack) else 1)
	}

	def getDisplayName(itemStack: ItemStack): String = {
		this.getTier(itemStack).getName + " Compressed " + itemStack.getTagCompound.getString("display")
	}

	def getStackName(itemStack: ItemStack): String = {
		itemStack.getTagCompound.getString("name")
	}

	def getSampleStack(itemStack: ItemStack): ItemStack = {
		Names.getItemStack(this.getStackName(itemStack))
	}

	def getSampleState(itemStack: ItemStack): IBlockState = {
		Names.getState(this.getStackName(itemStack))
	}

	def getSize(itemStack: ItemStack): Long = itemStack.getTagCompound.getLong("size")

	def getTier(itemStack: ItemStack): EnumTier = EnumTier.getTierForSize(this.getSize(itemStack))

}
