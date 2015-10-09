package com.temportalist.compression.common.recipe

import com.temportalist.origin.api.common.utility.{Scala, Stacks}
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

import scala.collection.mutable

/**
 *
 *
 * @author TheTemportalist
 */
class RecipeCompressClassic(
		val width: Int, val height: Int,
		val consumed: Map[Int, ItemStack], val kept: Map[Int, ItemStack], val output: ItemStack
		) extends IRecipe {

	RecipeCompressClassic.recipes(output) = this

	var useNBT: Boolean = true

	def this(width: Int, height: Int, filler: ItemStack, output: ItemStack) {
		this(width, height, Scala.fill(width * height, filler), Map(), output)
	}

	def setUseNBT(doUse: Boolean): RecipeCompressClassic = {
		this.useNBT = doUse
		this
	}

	override def getRecipeOutput: ItemStack = this.output

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getRecipeOutput.copy()

	override def getRecipeSize: Int = this.width * this.height

	/* todo getRemainingItems
	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = {
		val remaining: Array[ItemStack] = new Array[ItemStack](inv.getSizeInventory)
		for (i <- 0 until remaining.length) {
			remaining(i) = if (kept.contains(i)) this.kept(i).copy() else null
		}
		remaining
	}
	*/

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
				if (!Stacks.doStacksMatch(invStack, expected, meta = true, size = false,
					nbt = this.useNBT, nil = true)) return false
			}
		}
		true
	}

}

object RecipeCompressClassic {
	val recipes = mutable.Map[ItemStack, RecipeCompressClassic]()
}
