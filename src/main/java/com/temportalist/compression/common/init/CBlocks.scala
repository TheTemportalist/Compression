package com.temportalist.compression.common.init

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.origin.library.common.register.BlockRegister
import net.minecraftforge.common.property.IUnlistedProperty

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
object CBlocks extends BlockRegister {

	val INT: IUnlistedProperty[Int] = new IUnlistedProperty[Int] {
		override def getType: Class[Int] = classOf[Int]

		override def getName: String = "Integer"

		override def valueToString(v: Int): String = v.toString

		override def isValid(v: Int): Boolean = true
	}

	var compressed: BlockCompressed = null

	override def registerTileEntities(): Unit = {
		this.register(Compression.MODID + "_Compressed", classOf[TECompressed])
	}

	override def register(): Unit = {

		this.compressed = new BlockCompressed("compressed", classOf[TECompressed])
		this.compressed.setCreativeTab(Compression.tab)

	}

}
