package temportalist.compression.main.common.init

import net.minecraftforge.fml.common.registry.EntityRegistry
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.entity.EntityItemCompressed
import temportalist.origin.foundation.common.registers.EntityRegister

/**
  *
  * Created by TheTemportalist on 4/16/2016.
  *
  * @author TheTemportalist
  */
object ModEntities extends EntityRegister {

	override def register(): Unit = {

		EntityRegistry.registerModEntity(classOf[EntityItemCompressed], "ItemCompressed", 0,
			Compression, 64, 20, true)

	}

}
