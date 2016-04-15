package temportalist.compression.main.common.init

import net.minecraft.item.crafting.{CraftingManager, ShapedRecipes}
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.item.ItemCompressed
import temportalist.compression.main.common.lib.EnumTier
import temportalist.compression.main.common.recipe.{RecipeClassicCompress, Recipes}
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

		Compression.log("Loading compressed recipes for Items...")

		for (any <- JavaConversions.asScalaIterator(Item.itemRegistry.iterator())) {
			if (!any.isInstanceOf[ItemBlock]) Recipes.tryAddRecipes(new ItemStack(any))
		}

	}

}
