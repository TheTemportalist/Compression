package com.temportalist.compression.common.blocks

import java.util

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.ItemBlockCompressed
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.origin.library.common.lib.NameParser
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class BlockCompressed(name: String, te: Class[_ <: TileEntity]) extends BlockWrapperTE(
	Material.ground, Compression.MODID, name, classOf[ItemBlockCompressed], te) {

	@SideOnly(Side.CLIENT)
	override def getSubBlocks(itemIn: Item, tab: CreativeTabs, list: util.List[_]): Unit = {
		list.asInstanceOf[util.List[ItemStack]].addAll(Compression.compressables)
	}

	override def createBlockState(): BlockState = {
		new ExtendedBlockState(
			this, Array[IProperty](), Array[IUnlistedProperty[_]](Tupla.ITEMSTACK, CBlocks.LONG)
		)
	}

	override def getExtendedState(
			state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
		state match {
			case extended: IExtendedBlockState =>
				world.getTileEntity(pos) match {
					case compressed: TECompressed =>
						extended.withProperty(
							Tupla.ITEMSTACK, compressed.getState()
						).withProperty(
							CBlocks.LONG, compressed.getSize()
						)
					case _ =>
						extended
				}
			case _ =>
				state
		}
	}

	override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState,
			placer: EntityLivingBase, stack: ItemStack): Unit = {
		if (stack.hasTagCompound) {
			val innerName: String = stack.getTagCompound.getString("inner")
			if (!innerName.isEmpty) {
				worldIn.getTileEntity(pos) match {
					case compressed: TECompressed =>
						compressed.setState(NameParser.getItemStack(innerName))
						compressed.setSize(stack.getTagCompound.getLong("stackSize"))
					case _ =>
				}
			}
		}
	}

}
