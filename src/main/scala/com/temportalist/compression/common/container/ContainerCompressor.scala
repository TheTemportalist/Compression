package com.temportalist.compression.common.container

import com.temportalist.compression.common.tile.TECompress
import com.temportalist.origin.api.common.container.SlotOutput
import com.temportalist.origin.api.common.inventory.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot

/**
 * Created by TheTemportalist on 9/8/2015.
 */
class ContainerCompressor(p: EntityPlayer, tile: TECompress) extends ContainerBase(p, tile) {

	/**
	 * Used to register slots for this container
	 * Subclasses SHOULD use this method (that is the reason we have containers),
	 * however, subclasses do not NEED to use this method.
	 */
	override protected def registerSlots(): Unit = {
		this.addSlotToContainer(new Slot(this.inventory, 0, 44, 30))
		this.addSlotToContainer(new SlotOutput(this.inventory, 1, 116, 30))

		this.registerPlayerSlots(-4, -9)
	}

	/**
	 * @return the number of slots that are present and connected to this inventory
	 */
	override protected def getInventorySlotSize: Int = 2

}
