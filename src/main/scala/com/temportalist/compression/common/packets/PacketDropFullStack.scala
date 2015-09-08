package com.temportalist.compression.common.packets

import com.temportalist.compression.common.Compression
import com.temportalist.origin.foundation.common.network.IPacket
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer

/**
 * Created by TheTemportalist on 9/7/2015.
 */
class PacketDropFullStack extends IPacket {

	override def handle(player: EntityPlayer, side: Side): Unit = {
		Compression.splitAndDropCompressedStack(player, player.getHeldItem, dropMaxStack = true)
	}

}
