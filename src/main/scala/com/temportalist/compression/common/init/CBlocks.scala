package com.temportalist.compression.common.init

import java.util

import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.compression.common.item.IFood
import com.temportalist.compression.common.recipe.{RecipeCompress, RecipeDynamic}
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.compression.common.{Compression, Options, Tiers}
import com.temportalist.origin.api.common.lib.NameParser
import com.temportalist.origin.api.common.utility.WorldHelper
import com.temportalist.origin.foundation.common.register.BlockRegister
import cpw.mods.fml.client.registry.RenderingRegistry
import cpw.mods.fml.common.registry.{GameData, GameRegistry}
import net.minecraft.block.Block
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

	def compressedItem: Item = Item.getItemFromBlock(this.compressed)

	val compressedRenderID: Int = RenderingRegistry.getNextAvailableRenderId

	/**
	 * This method is used to register TileEntities.
	 * Recommendation: Use GameRegistry.registerTileEntity
	 */
	override def registerTileEntities(): Unit = {
		this.register(Compression.getModid + ":compressed", classOf[TECompressed])
	}

	override def register(): Unit = {

		this.compressed = new BlockCompressed("compressedBlock", classOf[TECompressed])
		this.compressed.setCreativeTab(Compression.tab)

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
		if (bvi) {
			// blocks
			val blocks: java.lang.Iterable[Block] = GameData.getBlockRegistry.typeSafeIterable()
			for (block: Block <- JavaConversions.asScalaIterator(blocks.iterator())) {
				if (this.canStackBeCompressed(new ItemStack(block), bvi)) {
					val subBlocks: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					block.getSubBlocks(Item.getItemFromBlock(block), null, subBlocks)
					for (i <- 0 until subBlocks.size()) {
						if (this.canStackBeCompressed(subBlocks.get(i), bvi)) {
							val stack: ItemStack = this.wrapInnerStack(subBlocks.get(i))
							this.compressedBlocks.add(stack)
							this.makeRecipe(subBlocks.get(i), stack)
						}
					}
				}
			}
		}
		else {
			// items
			val items: java.lang.Iterable[Item] = GameData.getItemRegistry.typeSafeIterable()
			for (item: Item <- JavaConversions.asScalaIterator(items.iterator())) {
				if (this.canStackBeCompressed(new ItemStack(item), bvi)) {
					val subItems: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					item.getSubItems(item, null, subItems)
					for (i <- 0 until subItems.size()) {
						if (this.canStackBeCompressed(subItems.get(i), bvi)) {
							val stack: ItemStack = this.wrapInnerStack(subItems.get(i))
							this.compressedItems.add(stack)
							this.makeRecipe(subItems.get(i), stack)
						}
					}
				}
			}
		}
	}

	def canStackBeCompressed(stack: ItemStack): Boolean = {
		this.canStackBeCompressed(stack, bvi = true) ||
				this.canStackBeCompressed(stack, bvi = false)
	}

	def canStackBeCompressed(stack: ItemStack, bvi: Boolean): Boolean = {
		if (stack == null || stack.getItem == null || stack.hasEffect(0)) return false
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
		val piston: ItemStack = new ItemStack(Blocks.piston)
		GameRegistry.addRecipe(new RecipeDynamic(3, 3,
			Map(1 -> inner, 3 -> inner, 4 -> inner, 5 -> inner, 7 -> inner),
			Map(0 -> piston, 2 -> piston, 6 -> piston, 8 -> piston),
			output
		))
		if (Options.useTraditionalRecipes) {
			for (tier: Int <- 1 to Tiers.getMaxTier()) {
				val last: ItemStack =
					if (tier == 1) inner
					else this.wrapInnerStack(inner, Tiers.getMaxCap(tier - 1))
				val next: ItemStack = this.wrapInnerStack(inner, Tiers.getMaxCap(tier))
				GameRegistry.addRecipe(new RecipeDynamic(3, 3, last, next))
			}
		}
		else {
			GameRegistry.addRecipe(new RecipeCompress(inner))
		}
		//GameRegistry.addRecipe(new RecipeDeCompress(inner))
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
			tile.setStack(this.getInnerStack(stack))
			tile.setSize(this.getInnerSize(stack))
		}
	}

	def writeTileToCompressed(tile: TECompressed, stack: ItemStack): Unit = {
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
		stack.getTagCompound.setString("inner", tile.getStackString)
		stack.getTagCompound.setString("display", tile.getStackDisplay)
		stack.getTagCompound.setLong("stackSize", tile.getSize)
	}

	def getInnerSize(stack: ItemStack): Long = {
		stack.getTagCompound.getLong("stackSize")
	}

	def canAddToStack(stack: ItemStack): Boolean = this.canAddToStack(stack, 1)

	def canAddToStack(stack: ItemStack, amount: Long): Boolean = {
		CBlocks.getInnerSize(stack) + amount <= Tiers.getMaxCap()
	}

	def addToInnerSize(stack: ItemStack): Unit = this.addToInnerSize(stack, 1)

	def addToInnerSize(stack: ItemStack, amount: Long): Unit = {
		this.setStackSize(stack, this.getInnerSize(stack) + amount)
	}

	def setStackSize(stack: ItemStack, size: Long): Unit = {
		stack.getTagCompound.setLong("stackSize", size)
	}

	def decrStackSize(stack: ItemStack, amt: Long): Unit = {
		this.setStackSize(stack, this.getInnerSize(stack) - amt)
	}

	def getDisplayName(stack: ItemStack): String = {
		stack.getTagCompound.getString("display")
	}

	def getInnerString(stack: ItemStack): String = {
		stack.getTagCompound.getString("inner")
	}

	def getInnerStack(stack: ItemStack): ItemStack = {
		NameParser.getItemStack(CBlocks.getInnerString(stack))
	}

	def doesStackHaveInner(stack: ItemStack): Boolean =
		stack.hasTagCompound && !this.getInnerString(stack).isEmpty

	def getStackTier(stack: ItemStack): Int = {
		Tiers.getTierFromSize(CBlocks.getInnerSize(stack))
	}

}
