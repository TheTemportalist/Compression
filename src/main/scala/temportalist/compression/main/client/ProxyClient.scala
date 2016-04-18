package temportalist.compression.main.client

import java.util

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.model.{ModelLoader, ModelLoaderRegistry}
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import temportalist.compression.main.client.model.ModelLoaderCompressed
import temportalist.compression.main.common.init.{ModBlocks, ModItems}
import temportalist.compression.main.common.{Compression, ProxyCommon}

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
		ModelLoader.setCustomModelResourceLocation(ModBlocks.blockItem, 0, ModelLoaderCompressed.fakeRL)
		ModelLoader.setCustomStateMapper(ModBlocks.block, new StateMapperBase {
			override protected def getModelResourceLocation(
					state: IBlockState): ModelResourceLocation = ModelLoaderCompressed.fakeRL
		})

		for (slot <- ModItems.armorTypes) {
			val location = new ModelResourceLocation(
				Compression.getModId+ ":ItemDenseArmor", "part=" + slot.getName
			)
			ModelLoader.setCustomModelResourceLocation(
				ModItems.leatherDenseArmor(slot.getIndex), 0, location)
		}

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
