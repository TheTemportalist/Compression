package temportalist.compression.main.common.recipe

import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.RecipeSorter
import temportalist.compression.main.common.recipe.classic.{RecipeCompress, RecipeDecompress}

/**
  *
  * Created by TheTemportalist on 4/15/2016.
  *
  * @author TheTemportalist
  */
object Recipes {

	def registerOtherRecipes(): Unit = {

		RecipeSorter.register("ClassicCompress", classOf[RecipeCompress], RecipeSorter.Category.SHAPED, "")
		RecipeSorter.register("ClassicDecompress", classOf[RecipeDecompress], RecipeSorter.Category.SHAPELESS, "")
		GameRegistry.addRecipe(new RecipeCompress)
		GameRegistry.addRecipe(new RecipeDecompress)

	}

}
