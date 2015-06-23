package com.temportalist.compression.common.init

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.item.ItemCompressed
import com.temportalist.origin.foundation.common.register.ItemRegister

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
object CItems extends ItemRegister {

	var compressed: ItemCompressed = null

	override def register(): Unit = {

		this.compressed = new ItemCompressed("compressedItem")
		this.compressed.setCreativeTab(Compression.tab)

	}

}
