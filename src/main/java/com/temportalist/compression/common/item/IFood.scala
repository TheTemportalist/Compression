package com.temportalist.compression.common.item

import scala.collection.mutable

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumAction, Item, ItemStack}
import net.minecraft.potion.PotionEffect
import net.minecraft.stats.StatList
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
trait IFood extends Item {

	private var heal: Int = 0
	private var saturation: Float = 0.6f
	private var alwaysEdible: Boolean = false
	private val potions: mutable.Map[PotionEffect, Float] = mutable.Map[PotionEffect, Float]()

	override def getMaxItemUseDuration(stack: ItemStack): Int = 32

	override def getItemUseAction(stack: ItemStack): EnumAction = EnumAction.EAT

	override def onItemUseFinish(stack: ItemStack, worldIn: World,
			playerIn: EntityPlayer): ItemStack = {
		println ("finish")
		this.decrementStack(stack)
		playerIn.getFoodStats.addStats(this.getHealAmount(stack), this.getSaturationAmount(stack))
		worldIn.playSoundAtEntity(
			playerIn, "random.burp", 0.5F, worldIn.rand.nextFloat * 0.1F + 0.9F
		)
		this.onFoodEaten(stack, worldIn, playerIn)
		playerIn.triggerAchievement(StatList.objectUseStats(Item.getIdFromItem(this)))
		stack
	}

	def decrementStack(stack: ItemStack): Unit = stack.stackSize -= 1

	def setHealAmount(amount: Int): Unit = this.heal = amount

	def setSaturation(amount: Float): Unit = this.saturation = amount

	def getHealAmount(stack: ItemStack): Int = this.heal

	def getSaturationAmount(stack: ItemStack): Float = this.saturation

	def setAlwaysEdible(): Unit = this.alwaysEdible = true

	def addPotionEffect(id: Int, duration: Int, amplifier: Int, probability: Float): Unit = {
		this.potions(new PotionEffect(id, duration, amplifier)) = probability
	}

	protected def onFoodEaten(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
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
		if (eatable){//} && playerIn.canEat(this.alwaysEdible)) { todo
			playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn))
		}
		itemStackIn
	}

}
