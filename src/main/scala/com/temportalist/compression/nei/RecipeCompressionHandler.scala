package com.temportalist.compression.nei

import java.util

import codechicken.nei.PositionedStack
import codechicken.nei.recipe.TemplateRecipeHandler
import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.recipe.{RecipeCompress, RecipeCompressClassic}
import com.temportalist.origin.api.common.utility.Stacks
import net.minecraft.item.ItemStack

/**
 * Created by TheTemportalist on 10/9/2015.
 */
object RecipeCompressionHandler extends TemplateRecipeHandler {

	class ClassicCachedRecipe(val recipe: RecipeCompressClassic) extends CachedRecipe {

		val outputPosStack = new PositionedStack(recipe.output, 10, 0)
		val innerStack = CBlocks.getInnerStack(recipe.output)
		val ingredients = new util.ArrayList[PositionedStack]()
		for (i <- 0 until 8)
			ingredients.add(new PositionedStack(innerStack, (i % 3) * 18, (i / 3) * 18))

		override def getResult: PositionedStack = this.outputPosStack

		override def getIngredients: util.List[PositionedStack] = this.ingredients

	}

	override def getGuiTexture: String = "nei:textures/gui/recipebg.png"

	override def getRecipeName: String = Compression.getModName

	override def loadCraftingRecipes(result: ItemStack): Unit = {
		if (Compression.isCompressedStack(result)) {
			RecipeCompressClassic.recipes.values.foreach(recipe =>
				if (Stacks.doStacksMatch(result, recipe.getRecipeOutput, nbt = true))
					this.arecipes.add(
						new ClassicCachedRecipe(RecipeCompressClassic.recipes(result)))
			)
		}
	}

}
