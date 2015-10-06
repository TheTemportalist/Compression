package com.temportalist.compression.common

import com.temportalist.compression.common.container.ContainerCompressor
import com.temportalist.compression.common.tile.TECompress
import com.temportalist.origin.api.common.proxy.IProxy
import cpw.mods.fml.client.registry.RenderingRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
class ProxyCommon extends IProxy {


	val compressedRenderID: Int = RenderingRegistry.getNextAvailableRenderId

	override def register(): Unit = {}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		tileEntity match {
			case compressor: TECompress =>
				if (ID == 0) {
					new ContainerCompressor(player, compressor)
				}
				else null
			case _ =>
				null
		}
	}

}
