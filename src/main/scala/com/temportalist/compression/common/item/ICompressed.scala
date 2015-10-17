package com.temportalist.compression.common.item

import java.util

import com.temportalist.compression.common.entity.EntityItemCompressed
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.{Compression, Options, Tiers}
import com.temportalist.origin.api.client.utility.Keys
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.Generic
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.MathHelper
import net.minecraft.world.World

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
trait ICompressed extends Item {

	//this.setMaxStackSize(1)

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

	override def onItemRightClick(itemStackIn: ItemStack, worldIn: World,
			playerIn: EntityPlayer): ItemStack = {
		// todo fix slot and open up for survival
		if (playerIn.isSneaking && playerIn.capabilities.isCreativeMode)
			this.onClick(itemStackIn, worldIn, playerIn)
		else itemStackIn
	}

	private def onClick(itemStackIn: ItemStack, worldIn: World,
			playerIn: EntityPlayer): ItemStack = {
		/*
		playerIn.openGui(Compression, 0, worldIn,
			MathHelper.floor_double(playerIn.posX),
			MathHelper.floor_double(playerIn.posY),
			MathHelper.floor_double(playerIn.posZ)
		)
		*/
		itemStackIn
	}

	override def hasCustomEntity(stack: ItemStack): Boolean = true

	override def createEntity(world: World, location: Entity, stack: ItemStack): Entity = {
		println("get entity")
		val ent = new EntityItemCompressed(
			world, location.posX, location.posY, location.posZ, stack)
		ent.motionX = location.motionX
		ent.motionY = location.motionY
		ent.motionZ = location.motionZ
		ent.delayBeforeCanPickup = 10
		ent
	}

	/**
	 * Also called, for this item, by the onArmorTick function (passing a -1 as the slot index).
	 * This is done because Tinker's Construct adds the ability to put ANY block on a player's head
	 */
	override def onUpdate(stack: ItemStack, world: World, player: Entity, slot: Int,
			isCurrentItem: Boolean): Unit = {
		if (!player.isSneaking) Compression.tryToPullItemsCloser(Options.magnetTier, player, stack,
			player.boundingBox.expand(1, 0.5D, 1), world, new V3O(player),
			new V3O(player.motionX, player.motionY, player.motionZ), null)
	}

	override def onArmorTick(world: World, player: EntityPlayer, stack: ItemStack): Unit = {
		this.onUpdate(stack, world, player, -1, isCurrentItem = false)
	}

}