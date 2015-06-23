package com.temportalist.compression.common.recipe

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
class RecipeDeCompress(inner: ItemStack) extends IRecipe {

	// just used to determine order of which to check recipes
	override def getRecipeSize: Int = 1

	private final def getKeptAndOutput(inv: InventoryCrafting): Array[ItemStack] = {
		val set: Array[ItemStack] = new Array[ItemStack](2)
		Scala.foreach(inv, (i: Int, slotStack: ItemStack) => {
			if (slotStack != null) if (this.isValidCompressed(slotStack)) {
				val inner: ItemStack = CBlocks.getInnerStack(slotStack)
				val size: Long = CBlocks.getInnerSize(slotStack)
				if (size <= 64) {
					inner.stackSize = size.toInt
				}
				else {
					inner.stackSize = 64
					set(0) = slotStack.copy()
					set(0).getTagCompound.setLong("stackSize", size - 64)
				}
				set(1) = inner
			}
			else return new Array[ItemStack](2)
		})
		set
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
		this.getKeptAndOutput(inv)(1)
	}

	private final def isValidCompressed(slotStack: ItemStack): Boolean = {
		slotStack != null && slotStack.getItem.isInstanceOf[ItemBlockCompressed] &&
				CBlocks.getInnerStack(slotStack).getItem == this.inner.getItem
	}

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		this.getResult(inv) != null
	}

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getResult(inv)

}
