package com.temportalist.compression.common.recipe

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.item.ItemBlockCompressed
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.origin.library.common.lib.NameParser
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class RecipeCompress(inner: ItemStack) extends IRecipe {

	// just used to determine order of which to check recipes
	override def getRecipeSize: Int = 9

	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] =
		new Array[ItemStack](inv.getSizeInventory)

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
					if (compressedSize + toAdd <= Tupla.getMaxCap())
						compressedSize += toAdd
					else return null
				}
			}
		}
		if (inputs > 1 && compressedSize > 1)
			Compression.constructCompressed(this.inner, compressedSize)
		else null
	}

	private final def isValidCompressed(slotStack: ItemStack): Boolean = {
		slotStack.getItem.isInstanceOf[ItemBlockCompressed] &&
			NameParser.getItemStack(slotStack.getTagCompound.getString("inner")).getItem == this.inner.getItem
	}

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		this.getResult(inv) != null
	}

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getResult(inv)

}
