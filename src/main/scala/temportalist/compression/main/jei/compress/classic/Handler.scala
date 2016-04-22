package temportalist.compression.main.jei.compress.classic

import mezz.jei.api.recipe.{IRecipeHandler, IRecipeWrapper, VanillaRecipeCategoryUid}

/**
  *
  * Created by TheTemportalist on 4/22/2016.
  *
  * @author TheTemportalist
  */
object Handler extends IRecipeHandler[Wrapper] {

	override def getRecipeClass: Class[Wrapper] = classOf[Wrapper]

	override def getRecipeCategoryUid: String = VanillaRecipeCategoryUid.CRAFTING

	override def getRecipeWrapper(recipe: Wrapper): IRecipeWrapper = recipe

	override def isRecipeValid(recipe: Wrapper): Boolean = {

		true
	}

}
