package com.temportalist.compression.common.packets

import com.temportalist.compression.common.init.CBlocks
import com.temportalist.origin.foundation.common.network.IPacket
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
class PacketUpdateHeldSize() extends IPacket {

	def this(size: Long) {
		this()
		this.add(size)
	}

	override def handle(player: EntityPlayer, side: Side): Unit = {
		val compressed = player.getHeldItem.copy()
		CBlocks.setStackSize(compressed, this.get[Long])
		player.setCurrentItemOrArmor(0, compressed)
	}

}
