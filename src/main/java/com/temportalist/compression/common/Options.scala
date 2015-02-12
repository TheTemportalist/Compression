package com.temportalist.compression.common

import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.origin.library.common.register.OptionRegister
import net.minecraft.item.ItemBlock

/**
 *
 *
 * @author TheTemportalist
 */
object Options extends OptionRegister {

	var hasTraditionalRecipes: Boolean = true

	val blackList_Block_Class: List[Class[_]] = List[Class[_]](
		classOf[BlockCompressed]
	)
	val blackList_Item_Class: List[Class[_]] = List[Class[_]](
		classOf[ItemBlock]
	)


	override def register(): Unit = {

		this.hasTraditionalRecipes = this.getAndComment(
			"general", "Use Traditional Recipes",
			"Use traditional 9x9 recipes", this.hasTraditionalRecipes
		)

	}


}
