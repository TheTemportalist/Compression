package com.temportalist.compression.common.network

import com.temportalist.origin.library.common.nethandler.IPacket
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 *
 *
 * @author TheTemportalist
 */
class PacketUpdateCompressed() extends IPacket {

	def this(invIndex: Int, size: Long) {
		this()
		this.add(invIndex)
		this.add(size)
	}

	override def handle(player: EntityPlayer, isServer: Boolean): Unit = {
		val stack: ItemStack = player.inventory.getStackInSlot(this.get[Int])
		stack.getTagCompound.setLong("stackSize", this.get[Long])
	}

}
