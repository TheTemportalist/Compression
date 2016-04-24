package temportalist.compression.main.common.recipe.classic

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe

/**
  *
  * Created by TheTemportalist on 4/24/2016.
  *
  * @author TheTemportalist
  */
abstract class RecipeClassic extends IRecipe {

	override def getRecipeOutput: ItemStack = null

	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = new Array[ItemStack](9)

}
