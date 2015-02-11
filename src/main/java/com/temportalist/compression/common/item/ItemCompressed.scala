package com.temportalist.compression.common.item

import java.util

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.origin.library.client.utility.Keys
import com.temportalist.origin.library.common.lib.NameParser
import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.origin.library.common.utility.{WorldHelper, Generic}
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{Block, BlockSnow}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class ItemCompressed(block: Block) extends ItemBlock(block) with IFood {

	this.setHasSubtypes(true)

	override def getItemStackDisplayName(stack: ItemStack): String = {
		if (stack.hasTagCompound) {
			Tupla.getName(stack.getTagCompound.getLong("stackSize")) + " Compressed " +
					stack.getTagCompound.getString("display")
		}
		else super.getItemStackDisplayName(stack)
	}

	override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[_],
			advanced: Boolean): Unit = {
		if (stack.hasTagCompound) {
			if (!Keys.isShiftKeyDown) Generic.addToList(tooltip, "Hold SHIFT for stats")
			else {
				Generic.addToList(tooltip, "Size: " + stack.getTagCompound.getLong("stackSize"))
			}
		}
	}

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World,
			posIn: BlockPos,
			sideIn: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		var side: EnumFacing = sideIn
		var pos: BlockPos = posIn

		val stateHit: IBlockState = worldIn.getBlockState(pos)
		val blockHit: Block = stateHit.getBlock
		if (blockHit == Blocks.snow_layer &&
				stateHit.getValue(BlockSnow.LAYERS).asInstanceOf[Int] < 1) {
			side = EnumFacing.UP
		}
		else if (!blockHit.isReplaceable(worldIn, pos)) pos = pos.offset(side)

		if (
			stack.stackSize == 0 ||
					!playerIn.canPlayerEdit(pos, side, stack) ||
					(pos.getY == 255 && this.block.getMaterial.isSolid)
		) {
			false
		}
		else
			this.tryPlace(worldIn, pos, side, stack, new V3O(hitX, hitY, hitZ), playerIn)
	}

	def tryPlace(world: World, pos: BlockPos, side: EnumFacing, stackIn: ItemStack,
			hitVec: V3O, player: EntityPlayer): Boolean = {
		val placementStack: ItemStack = this.getPlacementStack(stackIn)
		if (placementStack != null) {
			val block: Block = Block.getBlockFromItem(placementStack.getItem)
			if (world.canBlockBePlaced(block, pos, false, side, null, placementStack)) {
				val meta: Int = placementStack.getItem.getMetadata(placementStack.getMetadata)
				val stateToSet: IBlockState = block.onBlockPlaced(
					world, pos, side, hitVec.x_f(), hitVec.y_f(), hitVec.z_f(), meta, player
				)
				if (this.placeBlockAt(
					placementStack, player, world, pos, side,
					hitVec.x_f(), hitVec.y_f(), hitVec.z_f(), stateToSet
				)) {
					world.playSoundEffect(
						(pos.getX.asInstanceOf[Float] + 0.5F).asInstanceOf[Double],
						(pos.getY.asInstanceOf[Float] + 0.5F).asInstanceOf[Double],
						(pos.getZ.asInstanceOf[Float] + 0.5F).asInstanceOf[Double],
						block.stepSound.getPlaceSound,
						(block.stepSound.getVolume + 1.0F) / 2.0F,
						block.stepSound.getFrequency * 0.8F
					)
					this.decrementStack(stackIn, placementStack)
				}
				return true
			}
		}
		false
	}

	def getPlacementStack(stackIn: ItemStack): ItemStack = {
		if (stackIn.hasTagCompound &&
				!WorldHelper.isBlock(
					NameParser.getItemStack(stackIn.getTagCompound.getString("inner")).getItem
				))
			null
		else
			stackIn
	}

	def decrementStack(actualStack: ItemStack, placedStack: ItemStack): Unit = {
		actualStack.stackSize -= 1
	}

	override def onItemRightClick(itemStackIn: ItemStack, worldIn: World,
			playerIn: EntityPlayer): ItemStack = {
		if (playerIn.isSneaking) {
			val pos: BlockPos = playerIn.getPosition
			playerIn.openGui(Compression, 0, worldIn, pos.getX, pos.getY, pos.getZ)
			itemStackIn
		}
		else {
			super.onItemRightClick(itemStackIn, worldIn, playerIn)
		}
	}

	override def decrementStack(stack: ItemStack): Unit = {
		super.decrementStack(stack) // todo compressed eatings
	}

}
