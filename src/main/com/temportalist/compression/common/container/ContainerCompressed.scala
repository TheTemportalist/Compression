package com.temportalist.compression.common.container

import com.temportalist.origin.wrapper.common.inventory.ContainerWrapper
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author TheTemportalist
 */
class ContainerCompressed(p: EntityPlayer) extends ContainerWrapper(p, p.inventory) {

	override protected def registerSlots(): Unit = {

	}

}
