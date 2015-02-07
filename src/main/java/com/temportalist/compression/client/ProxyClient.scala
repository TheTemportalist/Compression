package com.temportalist.compression.client

import com.temportalist.compression.common.ProxyCommon
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist 2/6/15
 */
class ProxyClient extends ProxyCommon {

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

	override def registerRender(): Unit = {}

}
