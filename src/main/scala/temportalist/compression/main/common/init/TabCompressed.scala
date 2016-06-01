package temportalist.compression.main.common.init

import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.compression.main.common.lib.EnumTier
import temportalist.compression.main.common.{Compression, Options}

/**
  *
  * Created by TheTemportalist on 6/1/2016.
  *
  * @author TheTemportalist
  */
object TabCompressed {

	private var tab: CreativeTabs = null

	def hasTab: Boolean = Options.showTab

	def getTab: CreativeTabs = this.tab

	def createTab(): Unit = {
		if (!this.hasTab) return
		this.tab = new CreativeTabs(Compression.getModId + ".general") {

			private var stack: ItemStack = null

			override def getTabIconItem: Item = null

			@SideOnly(Side.CLIENT)
			override def getIconItemStack: ItemStack = {
				if (this.stack == null) {
					this.stack = Compressed.create(
						CreativeTabs.BUILDING_BLOCKS.getIconItemStack,
						tier = EnumTier.NONUPLE)
				}
				this.stack
			}

		}
	}

	def add(block: Block): Unit = {
		if (this.hasTab) block.setCreativeTab(this.getTab)
	}

	def add(item: Item): Unit = {
		if (this.hasTab) item.setCreativeTab(this.getTab)
	}

}
