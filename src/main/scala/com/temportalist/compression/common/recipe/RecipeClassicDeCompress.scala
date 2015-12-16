package com.temportalist.compression.common.recipe

import com.temportalist.compression.common.{Rank, CompressedStack}
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.ItemBlockCompressed
import com.temportalist.origin.api.common.utility.Scala
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class RecipeClassicDeCompress(stackType: ItemStack, rankIndex: Int) extends IRecipe {

	// just used to determine order of which to check recipes
	override def getRecipeSize: Int = 1

	private final def getKeptAndOutput(inv: InventoryCrafting): ItemStack = {

		val compressedSize = Rank.indexOf(this.rankIndex).getMaximum

		var multiple = 0
		Scala.foreach(inv, (i: Int, slotStack: ItemStack) => {
			if (slotStack != null) {
				if (CompressedStack.isCompressedStack(slotStack) &&
						CompressedStack.doStackTypesMatch(slotStack, this.stackType)) {
					val slotCompressedSize = CompressedStack.getCompressedSize(slotStack)
					if (slotCompressedSize != compressedSize) return null
					else multiple += 1
				}
				else return null
			}
		})

		if (multiple < 1) return null

		val decompressedRank = Rank.indexOf(this.rankIndex - 1)
		if (this.rankIndex - 1 > 0) {
			val stack = CompressedStack.createCompressedStack(
				this.stackType, decompressedRank.getMaximum)
			stack.stackSize = multiple
			stack
		}
		else if (multiple <= 7) {
			val stack = this.stackType.copy()
			stack.stackSize = (compressedSize * multiple).toInt
			stack
		}
		else null
	}

	/* todo getRemainingItems
	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = {
		val keptAndOutput: Array[ItemStack] = this.getKeptAndOutput(inv)
		if (keptAndOutput.isEmpty)
			Array[ItemStack]()
		else {
			val leftover: Array[ItemStack] = new Array[ItemStack](inv.getSizeInventory)
			Scala.foreach(inv, (i: Int, slotStack: ItemStack) => {
				if (this.isValidCompressed(slotStack))
					leftover(i) = keptAndOutput(0)
			})
			leftover
		}
	}
	*/

	override def getRecipeOutput: ItemStack = null // todo what does this do

	def getResult(inv: InventoryCrafting): ItemStack = {
		this.getKeptAndOutput(inv)
	}

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		this.getResult(inv) != null
	}

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getResult(inv)

}
