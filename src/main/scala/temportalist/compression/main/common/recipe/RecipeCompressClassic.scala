package temportalist.compression.main.common.recipe

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import temportalist.compression.main.common.init.Compressed

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class RecipeCompressClassic(private val stackIn: ItemStack) extends IRecipe {

	this.stackIn.stackSize = 1
	private val stackOut = Compressed.create(stackIn)

	override def getRecipeSize: Int = 9

	override def getRecipeOutput: ItemStack = this.stackOut

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getRecipeOutput.copy()

	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = Array[ItemStack]()

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		for (row <- 0 until 2) for (col <- 0 until 2) {
			inv.getStackInSlot(col + row * 3) match {
				case invStack: ItemStack =>
					val sameItem = this.stackIn.getItem == invStack.getItem
					val sameMeta = this.stackIn.getItemDamage == invStack.getItemDamage
					val sameTag = this.stackIn.getTagCompound == invStack.getTagCompound
					return sameItem && sameMeta && sameTag
				case _ =>
			}
		}
		false
	}


}
