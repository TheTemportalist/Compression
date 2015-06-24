package com.temportalist.compression.common

import java.util

import com.temportalist.origin.api.common.proxy.IProxy
import com.temportalist.origin.api.common.resource.IModDetails
import com.temportalist.origin.api.common.utility.WorldHelper
import com.temportalist.origin.foundation.common.IMod
import com.temportalist.origin.internal.common.handlers.RegisterHelper
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.{GameRegistry, GameData}
import cpw.mods.fml.common.{SidedProxy, Mod}

import scala.collection.JavaConversions

import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.IFood
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.compression.common.network.PacketUpdateCompressed
import com.temportalist.compression.common.recipe.{RecipeCompress, RecipeDeCompress, RecipeDynamic}
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item._
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.event.entity.player.EntityItemPickupEvent

/**
 *
 *
 * @author TheTemportalist 2/6/15
 */
@Mod(modid = Compression.MODID, name = Compression.MODNAME, version = Compression.VERSION,
	modLanguage = "scala",
	//guiFactory = Compression.clientProxy,
	dependencies = "required-after:origin@[5,);"
)
object Compression extends IMod with IModDetails {

	final val MODID = "compression" //"@MODID@"
	final val MODNAME = "@MODNAME@"
	final val VERSION = "@PLUGIN_VERSION@"
	final val clientProxy = "com.temportalist.compression.client.ProxyClient"
	final val serverProxy = "com.temportalist.compression.server.ProxyServer"

	override def getDetails: IModDetails = this

	override def getModid: String = this.MODID

	override def getModName: String = this.MODNAME

	override def getModVersion: String = this.VERSION

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: IProxy = null

	/**
	 * The tab for all the Compressed blocks
	 * Entries are added based on a selector and fetches all
	 * possible entries from the registered blocks/items
	 */
	val tab: CreativeTabs = new CreativeTabs(Compression.MODID) {
		override def getTabIconItem: Item = Items.stick
	}

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this, event, this.proxy, Options, CBlocks)
		// register this mod's packets
		this.registerPackets(classOf[PacketUpdateCompressed])

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = super.initialize(event, this.proxy)

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
		// construct blocks & items compressed
		this.constructCompressables(true)
		this.constructCompressables(false)
	}

	/**
	 * A list of ALL compressed blocks & items that were registered to minecraft
	 */
	val compressables: java.util.List[ItemStack] = new util.ArrayList[ItemStack]()

	def isValidStack(stack: ItemStack, bvi: Boolean): Boolean = {
		if (stack == null || stack.getItem == null) return false
		if (bvi && WorldHelper.isBlock(stack.getItem)) { // blocks
			val block: Block = Block.getBlockFromItem(stack.getItem)
			for (clazz <- Options.blackList_Block_Class) {
				if (clazz.isAssignableFrom(block.getClass)) return false
			}
			if (Item.getItemFromBlock(block) != null) {
				this.log("WARNING: Block " + block.getUnlocalizedName + " with metadata " +
						stack.getItemDamage + " can be identified from " +
						stack.getItem.getUnlocalizedName + " but not vice versa.")
				false
			}
			else block.isOpaqueCube && !block.hasTileEntity(stack.getItemDamage)
		}
		else { // items
			val item: Item = stack.getItem
			for (clazz <- Options.blackList_Item_Class) {
				if (clazz.isAssignableFrom(item.getClass)) return false
			}
			item.getItemStackLimit(stack) > 1
		}
	}

	private def constructCompressables(bvi: Boolean): Unit = {
		if (bvi) { // blocks
			val blocks: java.lang.Iterable[Block] = GameData.getBlockRegistry.typeSafeIterable()
			for (block: Block <- JavaConversions.asScalaIterator(blocks.iterator())) {
				if (this.isValidStack(new ItemStack(block), bvi)) {
					val subBlocks: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					block.getSubBlocks(Item.getItemFromBlock(block), null, subBlocks)
					for (i <- 0 until subBlocks.size()) {
						if (this.isValidStack(subBlocks.get(i), bvi)) {
							val stack: ItemStack = this.constructCompressed(subBlocks.get(i))
							this.compressables.add(stack)
							this.makeRecipe(subBlocks.get(i), stack)
						}
					}
				}
			}
		}
		else { // items
			val items: java.lang.Iterable[Item] = GameData.getItemRegistry.typeSafeIterable()
			for (item: Item <- JavaConversions.asScalaIterator(items.iterator())) {
				if (this.isValidStack(new ItemStack(item), bvi)) {
					val subItems: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					item.getSubItems(item, null, subItems)
					for (i <- 0 until subItems.size()) {
						if (this.isValidStack(subItems.get(i), bvi)) {
							val stack: ItemStack = this.constructCompressed(subItems.get(i))
							this.compressables.add(stack)
							this.makeRecipe(subItems.get(i), stack)
						}
					}
				}
			}
		}
	}

	def constructCompressed(block: Block): ItemStack = {
		this.constructCompressed(new ItemStack(block, 1, 0))
	}

	def constructCompressed(inner: ItemStack): ItemStack = this.constructCompressed(inner, 5)

	def constructCompressed(inner: ItemStack, size: Long): ItemStack = {
		val stack: ItemStack = new ItemStack(CBlocks.compressed)
		val tag: NBTTagCompound = new NBTTagCompound
		inner.getItem match {
			case food: ItemFood =>
				//println (inner.getDisplayName)
				tag.setBoolean("canEat", true)
				stack.getItem.asInstanceOf[IFood].setHealAmount(food.getHealAmount(inner))
				stack.getItem.asInstanceOf[IFood].setSaturation(food.getSaturationModifier(inner))
			case food: IFood =>
				tag.setBoolean("canEat", true)
				stack.getItem.asInstanceOf[IFood].setHealAmount(food.getFoodAmount(inner))
				stack.getItem.asInstanceOf[IFood].setSaturation(food.getSaturationAmount(inner))
			case _ =>
				tag.setBoolean("canEat", false)
		}
		tag.setString("inner", NameParser.getName(inner, hasID = true, hasMeta = true))
		tag.setString("display", inner.getDisplayName)
		tag.setLong("stackSize", size)
		stack.setTagCompound(tag)
		stack
	}

	def makeRecipe(inner: ItemStack, output: ItemStack): Unit = {
		val piston: ItemStack = new ItemStack(Blocks.piston)
		GameRegistry.addRecipe(new RecipeDynamic(3, 3,
			Map(1 -> inner, 3 -> inner, 4 -> inner, 5 -> inner, 7 -> inner),
			Map(0 -> piston, 2 -> piston, 6 -> piston, 8 -> piston),
			output
		))
		if (Options.useTraditionalRecipes) {
			for (tier: Int <- 1 to Tupla.getMaxTier()) {
				val last: ItemStack =
					if (tier == 1) inner
					else this.constructCompressed(inner, Tupla.getMaxCap(tier - 1))
				val next: ItemStack = this.constructCompressed(inner, Tupla.getMaxCap(tier))
				GameRegistry.addRecipe(new RecipeDynamic(3, 3, last, next))
			}
		}
		else {
			GameRegistry.addRecipe(new RecipeCompress(inner))
		}
		GameRegistry.addRecipe(new RecipeDeCompress(inner))
	}

	@SubscribeEvent
	def itemPickup(event: EntityItemPickupEvent): Unit = {
		val player: EntityPlayer = event.entityPlayer
		val eItem: EntityItem = event.item
		if (player != null && eItem != null) {
			val stack: ItemStack = eItem.getEntityItem
			var foundValidStack: Boolean = false
			for (i <- 0 until player.inventory.getSizeInventory) {
				val invStack: ItemStack = player.inventory.getStackInSlot(i)
				if (invStack != null && invStack.getItem == CBlocks.compressedItem) {
					if (!invStack.hasTagCompound) return
					val innerStack: ItemStack = NameParser.getItemStack(
						invStack.getTagCompound.getString("inner")
					)
					if (innerStack.getItem == stack.getItem &&
							innerStack.getMetadata == stack.getMetadata) {
						// check the size of the compressed stack
						// todo check the tier???
						if (Tupla.canHold(invStack, stack.stackSize)) {
							foundValidStack = true
							Tupla.absorb(invStack, stack)
							player.inventory.markDirty()
							return
						}
					}
				}
			}
		}
	}

}
