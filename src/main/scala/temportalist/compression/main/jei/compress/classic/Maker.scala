package temportalist.compression.main.jei.compress.classic

import mezz.jei.api.IModRegistry
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.lib.EnumTier

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 4/22/2016.
  *
  * @author TheTemportalist
  */
object Maker {

	def getRecipes(registry: IModRegistry): ListBuffer[Wrapper] = {
		val list = ListBuffer[Wrapper]()

		val allItems = registry.getItemRegistry.getItemList
		val buffer = JavaConversions.asScalaBuffer(allItems)
		for (sample <- buffer) {
			if (Compressed.canCompressItem(sample)) {
				for (tier <- EnumTier.values()) {
					list += new Wrapper(sample, tier)
				}
			}
		}

		list
	}

}
