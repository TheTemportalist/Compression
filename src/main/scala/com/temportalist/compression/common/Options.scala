package com.temportalist.compression.common

import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.origin.api.common.lib.{Crash, NameParser}
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

	var useTraditionalRecipes: Boolean = true

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

		/*
		this.useTraditionalRecipes = this.getAndComment(
			"general", "Use Traditional Recipes",
			"Use traditional 9x9 recipes", this.useTraditionalRecipes
		)
		*/

		val blackList_Block: Array[String] = this.getAndComment(
			"general", "Block Class BlackList", "", this.blackList("block")
		)
		this.blackList_Block_Class = new Array[Class[_]](blackList_Block.length)
		for (i <- blackList_Block.indices)
			this.blackList_Block_Class(i) = Class.forName(blackList_Block(i))

		val blackList_Item: Array[String] = this.getAndComment(
			"general", "Item Class BlackList", "", this.blackList("item")
		)
		this.blackList_Item_Class = new Array[Class[_]](blackList_Item.length)
		for (i <- blackList_Item.indices)
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

		Rank.absorbalof = this.getAndComment("compressed objects", "Pool Functionality",
			"When players pick up blocks or items that match the same type as the first compressed " +
				"block or item with a minimum tier as this value, the block or item will be " +
				"inserted into the first compressed stack (-1 to disable).", Rank.absorbalof)

		Rank.atractor = this.getAndComment("compressed objects", "Attractor",
			"When a compressed block or item is on the ground, others of the same type will be " +
				"sucked into it, as long as it is this minimum tier (-1 to disable).",
			Rank.atractor)

		Rank.magnet = this.getAndComment("compressed objects", "Magnet",
			"When a compressed block or item of this minimum tier is in your inventory, " +
					"all blocks and items of the same type will be attracted to you " +
					"(-1 to disable).",
			Rank.magnet)

		Rank.blackHole = this.getAndComment("compressed objects", "Black Hole",
			"When a compressed block or item of this minimum tier is in the world, " +
				"all entities will be attracted to it (-1 to disable).", Rank.blackHole)

		if (Rank.atractor < Rank.absorbalof)
			new Crash(Compression.MODNAME, "Attractor tier less than Pool Functionality tier.", "")
		if (Rank.magnet < Rank.atractor)
			new Crash(Compression.MODNAME, "Magnet tier less than Attractor tier.", "")
		if (Rank.blackHole < Rank.magnet)
			new Crash(Compression.MODNAME, "Black Hole tier less than Magnet tier.", "")

	}

}
