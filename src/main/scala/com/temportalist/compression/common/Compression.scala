package com.temportalist.compression.common

import com.temportalist.compression.common.init.{CBlocks, CEntity, CItems}
import com.temportalist.compression.common.recipe.{RecipeCompress, RecipeCompressClassic, RecipeDeCompressClassic, RecipeRefill}
import com.temportalist.origin.api.common.resource.{EnumResource, IModDetails, IModResource}
import com.temportalist.origin.api.common.utility.Stacks
import com.temportalist.origin.foundation.common.IMod
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.{Mod, SidedProxy}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.oredict.RecipeSorter
import net.minecraftforge.oredict.RecipeSorter.Category

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
@Mod(modid = Compression.MODID, name = Compression.MODNAME, version = Compression.VERSION,
	modLanguage = "scala",
	guiFactory = Compression.clientProxy,
	dependencies = ""//required-after:origin@[6,);"
)
object Compression extends IMod with IModResource {

	final val MODID = "compression"
	final val MODNAME = "Compression"
	final val VERSION = "@MOD_VERSION@"
	final val clientProxy = "com.temportalist.compression.client.ProxyClient"
	final val serverProxy = "com.temportalist.compression.server.ProxyServer"

	override def getDetails: IModDetails = this

	override def getModid: String = this.MODID

	override def getModName: String = this.MODNAME

	override def getModVersion: String = this.VERSION

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: ProxyCommon = null

	/**
	 * The tab for all the Compressed blocks
	 * Entries are added based on a selector and fetches all
	 * possible entries from the registered blocks/items
	 */
	val tab = new CreativeTabs(Compression.MODID) {
		override def getTabIconItem: Item = Items.stick
	}

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		Rank.loadRanks()
		super.preInitialize(this, event, this.proxy, Options, CBlocks, CItems, CEntity)

		RecipeSorter.register("compress", classOf[RecipeCompress], Category.SHAPELESS, "")
		RecipeSorter.register("decompress", classOf[RecipeDeCompressClassic], Category.SHAPELESS, "")
		RecipeSorter.register("dynamic", classOf[RecipeCompressClassic], Category.SHAPELESS, "")
		RecipeSorter.register("refill", classOf[RecipeRefill], Category.SHAPELESS, "")

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = super.initialize(event, this.proxy)

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
		// construct blocks & items compressed
		CBlocks.constructCompressables(true)
		CBlocks.constructCompressables(false)
		// resources
		this.loadResource("gui_creative", (EnumResource.GUI, "stack_creative.png"))
		this.loadResource("gui_survival", (EnumResource.GUI, "stack_survival.png"))
		this.loadResource("compressor", (EnumResource.GUI, "compressor.png"))
		this.loadResource("compressorWorld", (EnumResource.TEXTURE_BLOCK, "compressor.png"))

	}

}
