package com.temportalist.compression.server

import com.temportalist.compression.common.ProxyCommon
import com.temportalist.compression.common.container.ContainerCompressed
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist 2/6/15
 */
class ProxyServer extends ProxyCommon {

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == 0) {
			return new ContainerCompressed(player)
		}
		null
	}

}
