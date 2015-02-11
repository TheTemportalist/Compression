package com.temportalist.compression.common

import com.temportalist.origin.library.common.register.OptionRegister

/**
 *
 *
 * @author TheTemportalist
 */
object Options extends OptionRegister {

	var hasTraditionalRecipes: Boolean = true

	override def register(): Unit = {

		this.hasTraditionalRecipes = this.getAndComment(
			"general", "Use Traditional Recipes",
			"Use traditional 9x9 recipes", this.hasTraditionalRecipes
		)

	}


}
