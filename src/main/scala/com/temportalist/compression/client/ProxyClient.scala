package com.temportalist.compression.client

import com.temportalist.compression.common.ProxyCommon
import com.temportalist.compression.common.container.ContainerCompressor
import com.temportalist.compression.common.init.{CItems, CBlocks}
import com.temportalist.compression.common.tile.TECompress
import com.temportalist.origin.api.common.register.Registry
import cpw.mods.fml.client.registry.{ClientRegistry, RenderingRegistry}
import modwarriors.notenoughkeys.api.Api
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.MinecraftForgeClient

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
class ProxyClient extends ProxyCommon {

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

}
