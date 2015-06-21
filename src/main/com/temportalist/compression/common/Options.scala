package com.temportalist.compression.common

import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.origin.api.common.lib.ConfigJson
import com.temportalist.origin.foundation.common.register.OptionRegister
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraft.item._

/**
 *
 *
 * @author TheTemportalist
 */
object Options extends OptionRegister {

	override def hasDefaultConfig: Boolean = false

	override def customizeConfiguration(event: FMLPreInitializationEvent): Unit = {
		if (this.config == null) {
			this.config = new ConfigJson(
				event.getModConfigurationDirectory, Compression.MODNAME + ".json"
			)
		}
	}

	var hasTraditionalRecipes: Boolean = true

	val blackList: Map[String, Array[String]] = Map[String, Array[String]](
		"block" -> Array[String](
			classOf[BlockCompressed].getName
		),
		"item" -> Array[String](
			classOf[ItemBlock].getName, classOf[ItemMonsterPlacer].getName,
			classOf[ItemSkull].getName, classOf[ItemFireworkCharge].getName,
			classOf[ItemDoor].getName, classOf[ItemEmptyMap].getName, classOf[ItemMap].getName
		)
	)
	var blackList_Block_Class: Array[Class[_]] = null
	var blackList_Item_Class: Array[Class[_]] = null

	override def register(): Unit = {

		this.hasTraditionalRecipes = this.getAndComment(
			"general", "Use Traditional Recipes",
			"Use traditional 9x9 recipes", this.hasTraditionalRecipes
		)

		val blackList_Block: Array[String] = this.getAndComment(
			"general", "Block BlackList", "", this.blackList("block")
		)
		this.blackList_Block_Class = new Array[Class[_]](blackList_Block.length)
		for (i <- 0 until blackList_Block.length)
			this.blackList_Block_Class(i) = Class.forName(blackList_Block(i))

		val blackList_Item: Array[String] = this.getAndComment(
			"general", "Item BlackList", "", this.blackList("item")
		)
		this.blackList_Item_Class = new Array[Class[_]](blackList_Item.length)
		for (i <- 0 until blackList_Item.length)
			this.blackList_Item_Class(i) = Class.forName(blackList_Item(i))

	}

}
