package com.temportalist.compression.common.item

import java.util

import com.temportalist.compression.common.Tiers
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.origin.api.client.utility.Keys
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.{WorldHelper, Generic}
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class ItemCompressed(block: Block) extends ItemBlock(block) with IFood {

	this.setHasSubtypes(true)

	override def getItemStackDisplayName(stack: ItemStack): String = {
		if (stack.hasTagCompound)
			Tiers.getName(CBlocks.getInnerSize(stack)) + " Compressed " +
					CBlocks.getDisplayName(stack)
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

	override def requiresMultipleRenderPasses(): Boolean = true

	override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		this.onItemUse(stack, player, world, new V3O(x, y, z), new V3O(hitX, hitY, hitZ),
			ForgeDirection.getOrientation(side))
	}

	def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, vecCoord: V3O, vecHit: V3O,
			sideIn: ForgeDirection): Boolean = {
		var side = sideIn
		val block = vecCoord.getBlock(world)

		if (block == Blocks.snow_layer && (vecCoord.getBlockMeta(world) & 7) < 1) {
			side = ForgeDirection.UP
		}
		else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush &&
				!block.isReplaceable(world,	vecCoord.x_i(), vecCoord.y_i(), vecCoord.z_i())) {
			vecCoord.add(side, 1)
		}

		if (stack.stackSize == 0) false
		else if (!player.canPlayerEdit(
			vecCoord.x_i(), vecCoord.y_i(), vecCoord.z_i(), side.ordinal(), stack)) false
		else if (vecCoord.y_i() == 255 && this.field_150939_a.getMaterial.isSolid) false
		else if (world.canPlaceEntityOnSide(this.field_150939_a,
			vecCoord.x_i(), vecCoord.y_i(), vecCoord.z_i(), false, side.ordinal(), player, stack)) {
			val meta2 = this.field_150939_a.onBlockPlaced(world,
				vecCoord.x_i(), vecCoord.y_i(), vecCoord.z_i(), side.ordinal(),
				vecHit.x_f(), vecHit.y_f(), vecHit.z_f(), this.getMetadata(stack.getItemDamage))
			if (this.placeBlockAt(stack, player, world, vecCoord, vecHit, side, meta2)) {
				world.playSoundEffect((vecCoord.x_f() + 0.5F).toDouble,
					(vecCoord.y_f() + 0.5F).toDouble, (vecCoord.z_f() + 0.5F).toDouble,
					this.field_150939_a.stepSound.func_150496_b,
					(this.field_150939_a.stepSound.getVolume + 1.0F) / 2.0F,
					this.field_150939_a.stepSound.getPitch * 0.8F)
				stack.stackSize -= 1
			}
			true
		}
		else false
	}

	def placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World,
			vecCoord: V3O, vecHit: V3O, side: ForgeDirection, metadata: Int): Boolean = {
		val innerStack = CBlocks.getInnerStack(stack)
		if (innerStack != null && WorldHelper.isBlock(innerStack.getItem)) {
			super.placeBlockAt(stack, player, world,
				vecCoord.x_i(), vecCoord.y_i(), vecCoord.z_i(), side.ordinal(),
				vecHit.x_f(), vecHit.y_f(), vecHit.z_f(), metadata)
		}
		else false
	}

}
