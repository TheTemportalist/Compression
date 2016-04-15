package temportalist.compression.main.common.item

import net.minecraft.item.{Item, ItemStack}
import temportalist.compression.main.common.init.Compressed

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
trait ICompressed extends Item {

	override def getItemStackDisplayName(itemStack: ItemStack): String = {
		if (itemStack.hasTagCompound) Compressed.getDisplayName(itemStack)
		else super.getItemStackDisplayName(itemStack)
	}

}
