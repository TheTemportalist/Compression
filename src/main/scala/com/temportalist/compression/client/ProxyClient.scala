package com.temportalist.compression.client

import com.temportalist.compression.common.ProxyCommon
import com.temportalist.compression.common.init.{CItems, CBlocks}
import cpw.mods.fml.client.registry.RenderingRegistry
import net.minecraft.entity.player.EntityPlayer
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

		RenderingRegistry.registerBlockHandler(RenderBlockCompressed)
		MinecraftForgeClient.registerItemRenderer(CBlocks.compressedItem, new RenderItemCompressed(false))
		MinecraftForgeClient.registerItemRenderer(CItems.compressed, new RenderItemCompressed(true))

	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == 0) {
			new GuiCompression(player)
		}
		else null
	}

}
