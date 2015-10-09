package com.temportalist.compression.nei

import codechicken.nei.api.{API, IConfigureNEI}
import com.temportalist.compression.common.Compression
import cpw.mods.fml.common.Optional

/**
 * Created by TheTemportalist on 10/9/2015.
 */
@Optional.Interface(
	iface = "codechicken.nei.api.IConfigureNEI", modid = NEICompressionConfig.neiModid)
object NEICompressionConfig extends IConfigureNEI {

	final val neiModid = "nei"

	override def getName: String = Compression.getModName

	override def getVersion: String = Compression.getModVersion

	@Optional.Method(modid = NEICompressionConfig.neiModid)
	override def loadConfig(): Unit = {

		API.registerRecipeHandler(RecipeCompressionHandler)

	}

}
