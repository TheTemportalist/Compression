package com.temportalist.compression.common.container

import com.temportalist.origin.api.common.inventory.ContainerBase
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
class ContainerCompression(p: EntityPlayer) extends ContainerBase(p, new InventoryCompression(p)) {

	/**
	 * Used to register slots for this container
	 * Subclasses SHOULD use this method (that is the reason we have containers),
	 * however, subclasses do not NEED to use this method.
	 */
	override protected def registerSlots(): Unit = {

		val isCreative = this.player.capabilities.isCreativeMode
		/* todo reenable slot
		this.addSlotToContainer(new SlotOutput(this.getIInventory, 0,
			if (isCreative) 16 else 80, 30))
		*/
		this.registerPlayerSlots(-4, -9, Array[Int](p.inventory.currentItem))

	}

}
