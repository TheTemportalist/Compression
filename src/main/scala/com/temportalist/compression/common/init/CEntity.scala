package com.temportalist.compression.common.init

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.entity.EntityItemCompressed
import com.temportalist.origin.foundation.common.register.EntityRegister
import cpw.mods.fml.common.registry.EntityRegistry

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
object CEntity extends EntityRegister {

	override def register(): Unit = {
		EntityRegistry.registerModEntity(classOf[EntityItemCompressed], "ItemCompressed", 0,
			Compression, 64, 20, true)
	}

}
