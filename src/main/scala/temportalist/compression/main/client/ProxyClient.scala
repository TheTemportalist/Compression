package temportalist.compression.main.client

import java.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.model.{ModelLoader, ModelLoaderRegistry}
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import temportalist.compression.main.client.model.ModelLoaderCompressed
import temportalist.compression.main.common.ProxyCommon
import temportalist.compression.main.common.init.ModItems

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class ProxyClient extends ProxyCommon with IModGuiFactory {

	override def preInit(): Unit = {

		ModelLoaderRegistry.registerLoader(new ModelLoaderCompressed())

		ModelLoader.setCustomModelResourceLocation(ModItems.item, 0, ModelLoaderCompressed.fakeRL)

	}

	override def register(): Unit = {

	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, tileEntity: TileEntity): AnyRef = {
		null
	}

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = classOf[GuiConfig]

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = null

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = null

}
