package com.temportalist.compression.server

import com.temportalist.compression.common.ProxyCommon
import com.temportalist.compression.common.container.ContainerCompression
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
class ProxyServer extends ProxyCommon {

	override def register(): Unit = {}

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == 0) {
			new ContainerCompression(player)
		}
		else null
	}

}
