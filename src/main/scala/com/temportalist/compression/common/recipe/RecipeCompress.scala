package com.temportalist.compression.common.recipe

import com.temportalist.compression.common.{Rank, CompressedStack}
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.ItemBlockCompressed
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
class RecipeCompress(inner: ItemStack) extends IRecipe {

	RecipeCompress.recipes(inner) = this

	override def getRecipeSize: Int = 9

	override def getRecipeOutput: ItemStack = null // todo what does this do

	def getResult(inv: InventoryCrafting): ItemStack = {
		var inputs: Int = 0
		var compressedSize: Long = 0
		for (slot <- 0 until inv.getSizeInventory) {
			val slotStack: ItemStack = inv.getStackInSlot(slot)
			if (slotStack != null) {
				val toAdd: Long = if (slotStack.getItem == this.inner.getItem) {
					1
				}
				else if (this.isValidCompressed(slotStack)) {
					slotStack.getTagCompound.getLong("stackSize")
				}
				else 0L
				if (toAdd > 0) {
					inputs += 1
					if (compressedSize + toAdd <= Rank.getHighestRank.getMaximum)
						compressedSize += toAdd
					else return null
				}
			}
		}
		if (inputs > 1 && compressedSize > 1)
			CBlocks.wrapInnerStack(this.inner, compressedSize)
		else null
	}

	private final def isValidCompressed(slotStack: ItemStack): Boolean = {
		slotStack.getItem.isInstanceOf[ItemBlockCompressed] &&
				CompressedStack.getStackType(slotStack).getItem == this.inner.getItem
	}

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		this.getResult(inv) != null
	}

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getResult(inv)

}

object RecipeCompress {
	val recipes = mutable.Map[ItemStack, RecipeCompress]()
}
