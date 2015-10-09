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
		if (player.getHeldItem == null) return
		// todo look at Compression.tossItem and compare code to move into function
		var newInvStack = player.getHeldItem.copy()
		val dropStack = newInvStack.copy()
		newInvStack.stackSize -= 1
		if (newInvStack.stackSize < 1) newInvStack = null
		dropStack.stackSize = 1

		Compression.splitAndDropCompressedStack(player, dropStack, dropMaxStack = true)
		if (newInvStack != null)
			player.inventory.setInventorySlotContents(player.inventory.currentItem, newInvStack)

	}

}
