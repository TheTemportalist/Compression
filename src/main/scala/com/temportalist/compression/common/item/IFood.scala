package com.temportalist.compression.common.item

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumAction, Item, ItemStack}
import net.minecraft.potion.PotionEffect
import net.minecraft.stats.StatList
import net.minecraft.util.FoodStats
import net.minecraft.world.World

import scala.collection.mutable

/**
 *
 *
 * @author TheTemportalist
 */
trait IFood extends Item {

	private var food: Int = 0
	private var saturation: Float = 0.6f
	private var alwaysEdible: Boolean = false
	private val potions: mutable.Map[PotionEffect, Float] = mutable.Map[PotionEffect, Float]()

	override def getMaxItemUseDuration(stack: ItemStack): Int = 32

	override def getItemUseAction(stack: ItemStack): EnumAction = EnumAction.eat

	override def onEaten(stack: ItemStack, worldIn: World,
			playerIn: EntityPlayer): ItemStack = {
		// duplicate the stack, because vanilla mc does not check if the tag compound was changed
		var foodStack: ItemStack = stack.copy()
		val multiple: Float = this.decrementStack(foodStack, playerIn.getFoodStats)
		if (multiple > 0f) {
			playerIn.getFoodStats.addStats(
				(this.getFoodAmount(foodStack).toFloat * multiple).toInt,
				this.getSaturationAmount(foodStack) * multiple
			)
			worldIn.playSoundAtEntity(
				playerIn, "random.burp", 0.5F, worldIn.rand.nextFloat * 0.1F + 0.9F
			)
			this.onFoodEaten(foodStack, worldIn, playerIn, multiple)
			playerIn.triggerAchievement(StatList.objectUseStats(Item.getIdFromItem(this)))
			if (!ItemStack.areItemStacksEqual(stack, foodStack) ||
					!ItemStack.areItemStackTagsEqual(stack, foodStack)) {
				if (foodStack != null && foodStack.stackSize <= 0) foodStack = null
				playerIn.inventory.setInventorySlotContents(
					playerIn.inventory.currentItem, foodStack
				)
			}
		}
		stack // returning the sent causes there to be no reset
	}

	def decrementStack(stack: ItemStack, stats: FoodStats): Float = {
		stack.stackSize -= 1
		1.0f
	}

	def setHealAmount(amount: Int): Unit = this.food = amount

	def setSaturation(amount: Float): Unit = this.saturation = amount

	def getFoodAmount(stack: ItemStack): Int = this.food

	def getSaturationAmount(stack: ItemStack): Float = this.saturation

	def setAlwaysEdible(): Unit = this.alwaysEdible = true

	def addPotionEffect(id: Int, duration: Int, amplifier: Int, probability: Float): Unit = {
		this.potions(new PotionEffect(id, duration, amplifier)) = probability
	}

	protected def onFoodEaten(
			stack: ItemStack, worldIn: World, player: EntityPlayer, multiple: Float): Unit = {
		if (!worldIn.isRemote) this.potions.foreach {
			case (potion, prob) =>
				if (worldIn.rand.nextFloat() < prob)
					player.addPotionEffect(potion)
		}
	}

	override def onItemRightClick(itemStackIn: ItemStack, worldIn: World,
			playerIn: EntityPlayer): ItemStack = {
		val eatable: Boolean = !itemStackIn.getTagCompound.hasKey("canEat") ||
				itemStackIn.getTagCompound.getBoolean("canEat")
		if (eatable && playerIn.canEat(this.alwaysEdible)) {
			playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn))
		}
		itemStackIn
	}

}
