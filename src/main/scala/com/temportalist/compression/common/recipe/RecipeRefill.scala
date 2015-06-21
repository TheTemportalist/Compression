package com.temportalist.compression.common.recipe

import com.temportalist.origin.api.common.utility.Stacks
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class RecipeRefill(
		container: ItemStack, fillables: Array[ItemStack],
		fillablesAtOnce: Int, output: ItemStack
		) extends IRecipe {

	var useNBT_Container: Boolean = true

	def dontUseContainerNBT(): RecipeRefill = {
		this.useNBT_Container = false
		this
	}

	override def getRecipeOutput: ItemStack = this.output

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getRecipeOutput.copy()

	override def getRecipeSize: Int = this.fillablesAtOnce + 1

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		var hasContainer: Boolean = false
		val fillablesCopy: Array[ItemStack] = fillables.clone()
		var currentFillablesUsed: Int = 0
		for (slot <- 0 until inv.getSizeInventory) {
			val slotStack: ItemStack = inv.getStackInSlot(slot)
			if (slotStack != null) {
				if (!hasContainer && this.isValidContainer(slotStack)) {
					hasContainer = true
				}
				else if (fillablesCopy.contains(slotStack) &&
						currentFillablesUsed < this.fillablesAtOnce) {
					fillablesCopy(fillablesCopy.indexOf(slotStack)) = null
					currentFillablesUsed += 1
				}
				else return false
			}
		}
		true
	}

	def isValidContainer(supposed: ItemStack): Boolean = {
		Stacks.doStacksMatch(supposed, this.container, meta = false, size = false,
			nbt = this.useNBT_Container, nil = true)
	}

}
