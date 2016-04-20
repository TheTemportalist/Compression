package temportalist.compression.main.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import net.minecraftforge.oredict.RecipeSorter
import temportalist.compression.main.common.init.{ModBlocks, ModEntities, ModItems}
import temportalist.compression.main.common.recipe.{RecipeClassicCompress, RecipeClassicDecompress, Recipes}
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Compression.MOD_ID, name = Compression.MOD_NAME, version = Compression.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = Compression.proxyClient,
	dependencies = "required-after:Forge;required-after:Origin"
)
object Compression extends ModBase {

	final val MOD_ID = "compression"
	final val MOD_NAME = "Compression"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist.compression.main.client.ProxyClient"
	final val proxyServer = "temportalist.compression.main.server.ProxyServer"

	/**
	  *
	  * @return A mod's ID
	  */
	override def getModId: String = this.MOD_ID

	/**
	  *
	  * @return A mod's name
	  */
	override def getModName: String = this.MOD_NAME

	/**
	  *
	  * @return A mod's version
	  */
	override def getModVersion: String = this.MOD_VERSION

	@SidedProxy(clientSide = this.proxyClient, serverSide = this.proxyServer)
	var proxy: IProxy = _

	override def getProxy: IProxy = this.proxy

	override def getOptions: OptionRegister = Options

	override def getRegisters: Seq[Register] = Seq(ModBlocks, ModItems, ModEntities)

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		RecipeSorter.register("ClassicCompress", classOf[RecipeClassicCompress], RecipeSorter.Category.SHAPED, "")
		RecipeSorter.register("ClassicDecompress", classOf[RecipeClassicDecompress], RecipeSorter.Category.SHAPELESS, "")
		this.registerHandler(Effects)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)
		Recipes.registerOtherRecipes()

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

}
