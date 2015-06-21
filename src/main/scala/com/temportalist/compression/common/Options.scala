package com.temportalist.compression.common

import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.origin.api.common.lib.NameParser
import com.temportalist.origin.api.common.utility.WorldHelper
import com.temportalist.origin.foundation.common.register.OptionRegister
import net.minecraft.block.Block
import net.minecraft.item._

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
object Options extends OptionRegister {

	override def getExtension: String = "json"

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
	var blacklist_blocks = ListBuffer[(Block, Int)]()
	var blacklist_items = ListBuffer[(Item, Int)]()

	override def register(): Unit = {

		this.hasTraditionalRecipes = this.getAndComment(
			"general", "Use Traditional Recipes",
			"Use traditional 9x9 recipes", this.hasTraditionalRecipes
		)

		val blackList_Block: Array[String] = this.getAndComment(
			"general", "Block Class BlackList", "", this.blackList("block")
		)
		this.blackList_Block_Class = new Array[Class[_]](blackList_Block.length)
		for (i <- 0 until blackList_Block.length)
			this.blackList_Block_Class(i) = Class.forName(blackList_Block(i))

		val blackList_Item: Array[String] = this.getAndComment(
			"general", "Item Class BlackList", "", this.blackList("item")
		)
		this.blackList_Item_Class = new Array[Class[_]](blackList_Item.length)
		for (i <- 0 until blackList_Item.length)
			this.blackList_Item_Class(i) = Class.forName(blackList_Item(i))

		val blacklist_objects = this.getAndComment(
			"general", "Stack Blacklist", "", Array[String]("minecraft:bedrock"))
		this.blacklist_blocks.clear()
		this.blacklist_items.clear()
		blacklist_objects.foreach(str => {
			val stack = NameParser.getItemStack(str)
			if (WorldHelper.isBlock(stack.getItem))
				this.blacklist_blocks +=
						((Block.getBlockFromItem(stack.getItem), stack.getItemDamage))
			else this.blacklist_items += ((stack.getItem, stack.getItemDamage))
		})

	}

}
