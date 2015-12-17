package com.temportalist.compression.common.init

import java.util

import com.temportalist.compression.common.blocks.{BlockCompress, BlockCompressed}
import com.temportalist.compression.common.item.{ItemBlockCompressed, ItemCompressed, IFood}
import com.temportalist.compression.common.recipe.{RecipeDeCompressClassic, RecipeCompress, RecipeCompressClassic}
import com.temportalist.compression.common.tile.{TECompress, TECompressed}
import com.temportalist.compression.common.{Rank, CompressedStack, Compression, Options}
import com.temportalist.origin.api.common.lib.NameParser
import com.temportalist.origin.api.common.utility.{NBTHelper, WorldHelper}
import com.temportalist.origin.foundation.common.register.BlockRegister
import cpw.mods.fml.common.registry.{GameData, GameRegistry}
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemFood, ItemStack}
import net.minecraft.nbt.NBTTagCompound

import scala.collection.JavaConversions

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
object CBlocks extends BlockRegister {

	var compressed: BlockCompressed = null
	var compressor: BlockCompress = null

	def compressedItem: Item = Item.getItemFromBlock(this.compressed)

	/**
	 * This method is used to register TileEntities.
	 * Recommendation: Use GameRegistry.registerTileEntity
	 */
	override def registerTileEntities(): Unit = {
		this.register(Compression.getModid + ":compressed", classOf[TECompressed])
		this.register(Compression.getModid + ":compressor", classOf[TECompress])

	}

	override def register(): Unit = {

		this.compressed = new BlockCompressed("compressedBlock", classOf[TECompressed])
		this.compressed.setCreativeTab(Compression.tab)

		this.compressor = new BlockCompress("compressor", classOf[TECompress])
		this.compressor.setCreativeTab(CreativeTabs.tabRedstone)

	}

	/**
	 * This method is used to register crafting recipes
	 */
	override def registerCrafting(): Unit = {
		GameRegistry.addShapedRecipe(new ItemStack(this.compressor),
			"cpc", "c c", "cpc",
			Char.box('c'), this.wrapInnerStack(new ItemStack(Blocks.cobblestone), 9),
			Char.box('p'), Blocks.piston)
	}

	// Compressables ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * A list of ALL compressed blocks that were registered to minecraft
	 */
	val compressedBlocks: java.util.List[ItemStack] = new util.ArrayList[ItemStack]()

	/**
	 * A list of ALL compressed items that were registered to minecraft
	 */
	val compressedItems: java.util.List[ItemStack] = new util.ArrayList[ItemStack]()

	def constructCompressables(bvi: Boolean): Unit = {

		def tryToCompress[U](plainStack: ItemStack, f: (ItemStack, ItemStack) => U): Unit = {
			try {
				val compressedStack = this.wrapInnerStack(plainStack)
				f(plainStack, compressedStack)
			}
			catch {
				case e: Exception =>
					Compression.log("Could not compress stack with item " +
							plainStack.getUnlocalizedName + " and size of " + plainStack.stackSize
							+ " and meta of " + plainStack.getItemDamage + " and nbt of " +
							(if (plainStack.hasTagCompound) plainStack.getTagCompound.toString
							else "{}"))
			}
		}

		if (bvi) {
			// blocks
			val blocks: java.lang.Iterable[Block] = GameData.getBlockRegistry.typeSafeIterable()
			for (block: Block <- JavaConversions.asScalaIterator(blocks.iterator())) {
				if (this.canStackBeCompressed(new ItemStack(block), bvi)) {
					/*
					See item loop

					val subBlocks: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					try {
						block.getSubBlocks(Item.getItemFromBlock(block), null, subBlocks)
					}
					catch {
						case e: Exception =>
							Compression.log("Could not compress " + block.getUnlocalizedName)
					}
					for (i <- 0 until subBlocks.size()) {
						if (this.canStackBeCompressed(subBlocks.get(i), bvi)) {
							val stack: ItemStack = this.wrapInnerStack(subBlocks.get(i))
							this.compressedBlocks.add(stack)
							this.makeRecipe(subBlocks.get(i), stack)
						}
					}
					*/
					tryToCompress(new ItemStack(block),
						(regStack: ItemStack, compressed: ItemStack) => {
							this.compressedBlocks.add(compressed)
							this.makeRecipe(regStack, compressed)
						}
					)
				}
			}
		}
		else {
			// items
			val items: java.lang.Iterable[Item] = GameData.getItemRegistry.typeSafeIterable()
			for (item: Item <- JavaConversions.asScalaIterator(items.iterator())) {
				if (this.canStackBeCompressed(new ItemStack(item), bvi)) {
					/*
					We can use item.getHasSubtypes for both items and blocks, but
					there is no way to retrieve those subtypes without going clientside

					val subItems: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					try {
						item.getSubItems(item, null, subItems)
					}
					catch {
						case e: Exception =>
							Compression.log("Could not compress " + item.getUnlocalizedName)
					}
					for (i <- 0 until subItems.size()) {
						if (this.canStackBeCompressed(subItems.get(i), bvi)) {
							val stack: ItemStack = this.wrapInnerStack(subItems.get(i))
							this.compressedItems.add(stack)
							this.makeRecipe(subItems.get(i), stack)
						}
					}
					*/
					tryToCompress(new ItemStack(item),
						(regStack: ItemStack, compressed: ItemStack) => {
							this.compressedItems.add(compressed)
							this.makeRecipe(regStack, compressed)
						}
					)
				}
			}
		}
	}

	def canStackBeCompressed(stack: ItemStack): Boolean = {
		this.canStackBeCompressed(stack, bvi = true) ||
				this.canStackBeCompressed(stack, bvi = false)
	}

	def canStackBeCompressed(stack: ItemStack, bvi: Boolean): Boolean = {
		if (stack == null || stack.getItem == null || stack.isItemEnchanted) return false
		if (stack.getItem.isInstanceOf[ItemCompressed] ||
				stack.getItem.isInstanceOf[ItemBlockCompressed]) return false
		// blocks
		if (bvi && WorldHelper.isBlock(stack.getItem)) {
			val block: Block = Block.getBlockFromItem(stack.getItem)
			if (Options.blacklist_blocks.contains((block, stack.getItemDamage))) return false
			for (clazz <- Options.blackList_Block_Class) {
				if (clazz.isAssignableFrom(block.getClass)) return false
			}
			if (Item.getItemFromBlock(block) == null) {
				Compression.log("WARNING: Block " + block.getUnlocalizedName + " with metadata " +
						stack.getItemDamage + " can be identified from " +
						stack.getItem.getUnlocalizedName + " but not vice versa.")
				false
			}
			else block.isOpaqueCube && !block.hasTileEntity(stack.getItemDamage)
		}
		else {
			// items
			val item: Item = stack.getItem
			if (Options.blacklist_items.contains((item, stack.getItemDamage))) return false
			for (clazz <- Options.blackList_Item_Class) {
				if (clazz.isAssignableFrom(item.getClass)) return false
			}
			item.getItemStackLimit(stack) > 1
		}
	}

	def makeRecipe(inner: ItemStack, output: ItemStack): Unit = {
		if (Options.useTraditionalRecipes) {
			for (tier: Int <- 1 to Rank.getHighestRank.getIndex) {
				val last: ItemStack =
					if (tier == 1) inner
					else this.wrapInnerStack(inner, Rank.indexOf(tier - 1).getMaximum)
				val next: ItemStack = this.wrapInnerStack(inner, Rank.indexOf(tier).getMaximum)
				GameRegistry.addRecipe(new RecipeCompressClassic(3, 3, last, next))
				GameRegistry.addRecipe(new RecipeDeCompressClassic(inner, tier))
			}
		}
		else {
			GameRegistry.addRecipe(new RecipeCompress(inner))
		}

	}

	// Compressed block utility methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def wrapInnerStack(stack: ItemStack): ItemStack = this.wrapInnerStack(stack, 9)

	def wrapInnerStack(stack: ItemStack, size: Long): ItemStack = {
		val retStack = if (WorldHelper.isBlock(stack.getItem))
			new ItemStack(this.compressed)
		else new ItemStack(CItems.compressed)
		val tag = new NBTTagCompound
		stack.getItem match {
			case food: ItemFood =>
				tag.setBoolean("canEat", true)
				retStack.getItem.asInstanceOf[IFood].setHealAmount(food.func_150905_g(stack))
				retStack.getItem.asInstanceOf[IFood].setSaturation(food.func_150906_h(stack))
			case food: IFood =>
				tag.setBoolean("canEat", true)
				stack.getItem.asInstanceOf[IFood].setHealAmount(food.getFoodAmount(stack))
				stack.getItem.asInstanceOf[IFood].setSaturation(food.getSaturationAmount(stack))
			case _ =>
				tag.setBoolean("canEat", false)
		}
		tag.setString("inner", NameParser.getName(stack, hasID = true, hasMeta = true))
		tag.setString("display", stack.getDisplayName)
		tag.setLong("stackSize", size)
		retStack.setTagCompound(tag)
		retStack
	}

	// Stack accessor methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def writeCompressedToTile(stack: ItemStack, tile: TECompressed): Unit = {
		if (stack.hasTagCompound) {
			tile.setStack(CompressedStack.getStackType(stack))
			tile.setSize(CompressedStack.getCompressedSize(stack))
		}
	}

	def writeTileToCompressed(tile: TECompressed, stack: ItemStack): Unit = {
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
		stack.getTagCompound.setString("inner", tile.getStackString)
		stack.getTagCompound.setString("display", tile.getStackDisplay)
		stack.getTagCompound.setLong("stackSize", tile.getSize)
	}

}
