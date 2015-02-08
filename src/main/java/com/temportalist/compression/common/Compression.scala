package com.temportalist.compression.common

import java.util

import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.origin.library.common.helpers.RegisterHelper
import com.temportalist.origin.library.common.lib.NameParser
import com.temportalist.origin.wrapper.common.{ModWrapper, ProxyWrapper}
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.registry.GameData
import net.minecraftforge.fml.common.{Mod, SidedProxy}

import scala.collection.JavaConversions

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

	final val MODID = "compression"//"@MODID@"
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
		super.preInitialize(this.MODID, this.MODNAME, event, this.proxy, CBlocks)
		RegisterHelper.registerHandler(this.proxy)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = super.initialize(event, this.proxy)

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)
		this.constructCompressables()
	}

	val compressables: java.util.List[ItemStack] = new util.ArrayList[ItemStack]()

	private def constructCompressables(): Unit = {
		val blocks: java.lang.Iterable[Block] = GameData.getBlockRegistry.typeSafeIterable()
		for (block: Block <- JavaConversions.asScalaIterator(blocks.iterator())) {
			if (!block.isInstanceOf[BlockCompressed] && block.isSolidFullCube &&
					block.isVisuallyOpaque && Item.getItemFromBlock(block) != null) {
				val stack: ItemStack = new ItemStack(CBlocks.compressed)
				val tag: NBTTagCompound = new NBTTagCompound
				tag.setLong("stackSize", Tupla.caps(1))
				/*
				println (block)
				println (GameData.getBlockRegistry.getNameForObject(block))
				println (Item.getItemFromBlock(block))
				println (new ItemStack(block))
				println (new ItemStack(block).getItem)
				println (Block.getBlockFromItem(new ItemStack(block).getItem))
				println ("")
				*/
				val blockStack: ItemStack = new ItemStack(block)
				tag.setString("inner", NameParser.getName(
					blockStack, hasID = true, hasMeta = true
				))
				tag.setString("display", blockStack.getDisplayName)
				stack.setTagCompound(tag)
				this.compressables.add(stack)
				return
			}
		}
	}

}
