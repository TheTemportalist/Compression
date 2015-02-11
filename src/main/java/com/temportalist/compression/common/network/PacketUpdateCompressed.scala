package com.temportalist.compression.common.network

import com.temportalist.origin.library.common.nethandler.IPacket
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 *
 *
 * @author TheTemportalist
 */
class PacketUpdateCompressed(var invIndex: Int, var size: Long) extends IPacket {

	def this() {
		this(0, 0)
	}

	override def writeTo(buffer: ByteBuf): Unit = {
		buffer.writeInt(this.invIndex)
		buffer.writeLong(this.size)
	}

	override def readFrom(buffer: ByteBuf): Unit = {
		this.invIndex = buffer.readInt()
		this.size = buffer.readLong()
	}

	override def handle(player: EntityPlayer): Unit = {
		val stack: ItemStack = player.inventory.getStackInSlot(this.invIndex)
		stack.getTagCompound.setLong("stackSize", this.size)
	}

}
