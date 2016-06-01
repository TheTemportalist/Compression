package temportalist.compression.main.common.item

import java.util

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.init.{Compressed, TabCompressed}
import temportalist.compression.main.common.lib.EnumTier
import temportalist.origin.api.common.item.ItemBase

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class ItemCompressed extends ItemBase(Compression) with ICompressed {

	TabCompressed.add(this)

	override def getSubItems(itemIn: Item, tab: CreativeTabs, list: util.List[ItemStack]): Unit = {
		for {
			sample <- Seq[ItemStack](
				new ItemStack(Items.APPLE),
				new ItemStack(Items.POTATO),
				new ItemStack(Items.BOOK),
				new ItemStack(Items.BOAT)
			)
			tier <- EnumTier.values()
		} {
			list.add(Compressed.create(sample, tier = tier))
		}
	}

}
