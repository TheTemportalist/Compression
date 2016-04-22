package temportalist.compression.main.common.recipe

import net.minecraft.item.crafting.{CraftingManager, IRecipe, ShapedRecipes}
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.lib.EnumTier

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

/**
  *
  * Created by TheTemportalist on 4/15/2016.
  *
  * @author TheTemportalist
  */
object Recipes {

	def registerOtherRecipes(): Unit = {

	}

	def tryAddRecipes(itemStack: ItemStack) = {
		if (Compressed.canCompressItem(itemStack) &&
				!Recipes.hasCompressedRecipe(itemStack)) {
			Compression.log("Constructing recipes for (isBlock: " +
					itemStack.getItem.isInstanceOf[ItemBlock] + ") " +
					itemStack.getDisplayName)

			val stackRecipes = ListBuffer[IRecipe]()
			for (tier <- EnumTier.values()) {
				stackRecipes += new RecipeClassicCompress(itemStack, tier)

				if (tier != EnumTier.getTail)
					stackRecipes += new RecipeClassicDecompress(itemStack, tier)
			}
			stackRecipes += new RecipeClassicDecompress(itemStack, null)

			for (recipe <- stackRecipes) GameRegistry.addRecipe(recipe)
		}
	}

	def hasCompressedRecipe(itemStack: ItemStack): Boolean = {
		val recipeList = JavaConversions.asScalaBuffer(CraftingManager.getInstance().getRecipeList)
		for (recipe <- recipeList) {
			recipe match {
				case shaped: ShapedRecipes =>
					val recipeItems = shaped.recipeItems
					if (recipeItems.length == 9) {
						var allMatch = true
						breakable {
							for (recipeStack <- recipeItems) {
								if (!ItemStack.areItemStacksEqual(itemStack, recipeStack)) {
									allMatch = false
									break()
								}
							}
						}
						if (allMatch) return true
					}
				case _ =>
			}
		}
		false
	}

}
