package temportalist.compression.main.common.init

import net.minecraft.item.crafting.{CraftingManager, ShapedRecipes}
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.compression.main.common.item.ItemCompressed
import temportalist.compression.main.common.recipe.RecipeCompressClassic
import temportalist.origin.foundation.common.registers.ItemRegister

import scala.collection.JavaConversions
import scala.util.control.Breaks._

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
object ModItems extends ItemRegister {

	var item: ItemCompressed = _

	override def register(): Unit = {
		this.item = new ItemCompressed()

	}

	override def registerCrafting(): Unit = {

		for (item <- JavaConversions.asScalaIterator(Item.itemRegistry.iterator())) {
			val itemStack = new ItemStack(item)
			if (Compressed.canCompressItem(itemStack)) {
				if (!this.hasCompressedRecipe(itemStack)) {
					GameRegistry.addRecipe(new RecipeCompressClassic(itemStack))
				}
			}
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
