package com.temportalist.compression.common.item

import java.util

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.origin.api.common.item.ItemBase
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{ItemStack, Item}

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
class ItemCompressed(n: String) extends ItemBase(Compression.getModid, n) with ICompressed with IFood {

	this.setHasSubtypes(true)

	@SideOnly(Side.CLIENT)
	override def getSubItems(itemIn: Item, tab: CreativeTabs, list: util.List[_]): Unit = {
		// return a list of ALL compressable blocks
		list.asInstanceOf[util.List[ItemStack]].addAll(CBlocks.compressedItems)
	}

	override def requiresMultipleRenderPasses(): Boolean = true



}
