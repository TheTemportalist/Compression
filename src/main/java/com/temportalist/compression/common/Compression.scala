package com.temportalist.compression.common

import java.util

import scala.collection.JavaConversions

import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.IFood
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.compression.common.network.PacketUpdateCompressed
import com.temportalist.compression.common.recipe.{RecipeDynamic, RecipeCompress, RecipeDeCompress}
import com.temportalist.origin.library.common.helpers.RegisterHelper
import com.temportalist.origin.library.common.lib.NameParser
import com.temportalist.origin.library.common.utility.States
import com.temportalist.origin.wrapper.common.{ModWrapper, ProxyWrapper}
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item._
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.{GameData, GameRegistry}
import net.minecraftforge.fml.common.{Mod, SidedProxy}

/**
 *
 *
 * @author TheTemportalist 2/6/15
 */
@Mod(modid = Compression.MODID, name = Compression.MODNAME, version = "@PLUGIN_VERSION@",
	modLanguage = "scala",
	//guiFactory = Compression.clientProxy,
	dependencies = "required-after:origin@[4,);"
)
object Compression extends ModWrapper {

	final val MODID = "compression"
	//"@MODID@"
	final val MODNAME = "@MODNAME@"
	final val clientProxy = "com.temportalist.compression.client.ProxyClient"
	final val serverProxy = "com.temportalist.compression.server.ProxyServer"

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: ProxyWrapper = null

	val tab: CreativeTabs = new CreativeTabs(Compression.MODNAME) {
		override def getTabIconItem: Item = Items.stick
	}

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this.MODID, this.MODNAME, event, this.proxy, CBlocks, Options)
		RegisterHelper.registerHandler(this.proxy)
		RegisterHelper.registerPacketHandler(this.MODID, classOf[PacketUpdateCompressed])

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = super.initialize(event, this.proxy)

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)
		this.constructCompressables(true)
		this.constructCompressables(false)
	}

	val compressables: java.util.List[ItemStack] = new util.ArrayList[ItemStack]()

	private def constructCompressables(bvi: Boolean): Unit = {
		if (bvi) {
			val blocks: java.lang.Iterable[Block] = GameData.getBlockRegistry.typeSafeIterable()
			for (block: Block <- JavaConversions.asScalaIterator(blocks.iterator())) {
				if (
					!block.isInstanceOf[BlockCompressed] &&
							block.isSolidFullCube &&
							block.isVisuallyOpaque &&
							block.isOpaqueCube &&
							Item.getItemFromBlock(block) != null &&
							!block.isInstanceOf[ITileEntityProvider]
				) {
					val subBlocks: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					// todo should this be on a per tab basis?
					block.getSubBlocks(Item.getItemFromBlock(block), null, subBlocks)
					for (i <- 0 until subBlocks.size()) {
						if (!block.hasTileEntity(States.getState(subBlocks.get(i)))) {
							val stack: ItemStack = this.constructCompressed(subBlocks.get(i))
							this.compressables.add(stack)
							this.makeRecipe(subBlocks.get(i), stack)
						}
					}
				}
			}
		}
		else {
			val items: java.lang.Iterable[Item] = GameData.getItemRegistry.typeSafeIterable()
			for (item: Item <- JavaConversions.asScalaIterator(items.iterator())) {
				if (!item.isInstanceOf[ItemBlock]) {
					val subItems: util.List[ItemStack] = new util.ArrayList[ItemStack]()
					if (item.getHasSubtypes) item.getSubItems(item, null, subItems)
					else subItems.add(new ItemStack(item))
					for (i <- 0 until subItems.size()) {
						if (item.getItemStackLimit(subItems.get(i)) > 1) {
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
		this.constructCompressed(block.getDefaultState)
	}

	def constructCompressed(state: IBlockState): ItemStack = {
		this.constructCompressed(States.getStack(state))
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
				stack.getItem.asInstanceOf[IFood].setHealAmount(food.getHealAmount(inner))
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
		if (Options.hasTraditionalRecipes) {
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
