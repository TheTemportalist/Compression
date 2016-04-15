package temportalist.compression.main.common.block

import net.minecraft.item.ItemBlock
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.item.ItemBlockCompressed
import temportalist.origin.api.common.block.BlockBase

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class BlockCompressed extends BlockBase(Compression) {

	override def createItemBlock(): ItemBlock = new ItemBlockCompressed(this)

}
