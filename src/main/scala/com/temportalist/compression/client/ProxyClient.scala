package com.temportalist.compression.client

import java.util

import com.temportalist.compression.common.ProxyCommon
import com.temportalist.compression.common.container.ContainerCompressor
import com.temportalist.compression.common.init.{CBlocks, CItems}
import com.temportalist.compression.common.tile.TECompress
import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.internal.client.gui.GuiConfig
import cpw.mods.fml.client.IModGuiFactory
import cpw.mods.fml.client.IModGuiFactory.{RuntimeOptionGuiHandler, RuntimeOptionCategoryElement}
import cpw.mods.fml.client.registry.{ClientRegistry, RenderingRegistry}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.MinecraftForgeClient

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
class ProxyClient extends ProxyCommon with IModGuiFactory {

	override def register(): Unit = {
		Registry.registerKeyBinder(KeyBinder)

		RenderingRegistry.registerBlockHandler(RenderBlockCompressed)
		MinecraftForgeClient.registerItemRenderer(CBlocks.compressedItem, new RenderItemCompressed(false))
		MinecraftForgeClient.registerItemRenderer(CItems.compressed, new RenderItemCompressed(true))
		MinecraftForgeClient.registerItemRenderer(
			Item.getItemFromBlock(CBlocks.compressor), RenderCompressor)
		ClientRegistry.bindTileEntitySpecialRenderer(classOf[TECompress], RenderCompressor)

	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		tileEntity match {
			case compressor: TECompress =>
				if (ID == 0) {
					new GuiCompressor(new ContainerCompressor(player, compressor))
				}
				else null
			case _ =>
				null
		}
	}
	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = {
		null
	}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = {
		null
	}

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = {
		classOf[GuiConfig]
	}
}
