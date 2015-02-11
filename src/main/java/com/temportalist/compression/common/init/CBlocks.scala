package com.temportalist.compression.common.init

//import java.lang.Long
import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.blocks.BlockCompressed
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.origin.library.common.register.BlockRegister
import net.minecraft.item.Item
import net.minecraft.util.EnumWorldBlockLayer
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
object CBlocks extends BlockRegister {

	@SideOnly(Side.CLIENT)
	val validRenderLayers: List[EnumWorldBlockLayer] = List(
		EnumWorldBlockLayer.SOLID, EnumWorldBlockLayer.TRANSLUCENT
	)

	val INT: IUnlistedProperty[Int] = new IUnlistedProperty[Int] {
		override def getType: Class[Int] = classOf[Int]

		override def getName: String = "Integer"

		override def valueToString(v: Int): String = v.toString

		override def isValid(v: Int): Boolean = true
	}

	//val value = extended.getUnlistedProperties.get(CBlocks.LONG).orNull()
	//val long: Long = Long.unbox(value.asInstanceOf[Object])
	val LONG: IUnlistedProperty[Long] = new IUnlistedProperty[Long] {
		override def getType: Class[Long] = classOf[Long]

		override def getName: String = "Long"

		override def valueToString(v: Long): String = v.toString

		override def isValid(v: Long): Boolean = true
	}

	var compressed: BlockCompressed = null
	def compressedItem: Item = Item.getItemFromBlock(compressed)

	override def registerTileEntities(): Unit = {
		this.register(Compression.MODID + "_Compressed", classOf[TECompressed])
	}

	override def register(): Unit = {

		this.compressed = new BlockCompressed("compressed", classOf[TECompressed])
		this.compressed.setCreativeTab(Compression.tab)

	}

}
