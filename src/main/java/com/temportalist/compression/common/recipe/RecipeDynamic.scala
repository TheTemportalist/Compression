package com.temportalist.compression.common.recipe

import scala.collection.mutable

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

/**
 *
 *
 * @author TheTemportalist
 */
class RecipeDynamic(
		val width: Int, val height: Int,
		val consumed: Map[Int, ItemStack], val kept: Map[Int, ItemStack], val output: ItemStack
		) extends IRecipe {

	var useNBT: Boolean = true

	def this(width: Int, height: Int, filler: ItemStack, output: ItemStack) {
		this(width, height, RecipeDynamic.map(width*height, filler), Map(), output)
	}

	def setUseNBT(doUse: Boolean): RecipeDynamic = {
		this.useNBT = doUse
		this
	}

	override def getRecipeOutput: ItemStack = this.output

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getRecipeOutput.copy()

	override def getRecipeSize: Int = this.width * this.height

	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = {
		val remaining: Array[ItemStack] = new Array[ItemStack](inv.getSizeInventory)
		for (i <- 0 until remaining.length) {
			remaining(i) = if (kept.contains(i)) this.kept(i).copy() else null
		}
		remaining
	}

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		for (x <- 0 until this.width) for (y <- 0 until this.height) {
			val slot: Int = x + y * this.width
			val invStack: ItemStack = inv.getStackInSlot(slot)
			if (invStack == null) {
				if (this.consumed.contains(slot) || this.kept.contains(slot)) return false
			}
			else {
				val expected: ItemStack =
					if (this.consumed.contains(slot)) this.consumed(slot)
					else if (this.kept.contains(slot)) this.kept(slot)
					else null
				if (expected == null) return false

				// item
				// meta if regarded
				// nbt if regarded
				if (
					invStack.getItem != expected.getItem ||
							(
									expected.getMetadata != OreDictionary.WILDCARD_VALUE &&
											invStack.getMetadata != expected.getMetadata
									) || (
							this.useNBT && !ItemStack.areItemStackTagsEqual(invStack, expected)
							)
				) return false
			}
		}
		true
	}

}

object RecipeDynamic {
	// todo to origin
	def map[B](size: Int, obj: B): Map[Int, B] = {
		val map: mutable.Map[Int, B] = mutable.Map[Int, B]()
		for(i <- 0 until size)
			map(i) = obj
		map.toMap
	}
}
