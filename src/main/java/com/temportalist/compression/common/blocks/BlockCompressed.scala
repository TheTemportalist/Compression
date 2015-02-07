package com.temportalist.compression.common.blocks

import java.util

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.ItemBlockCompressed
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.origin.library.common.lib.{BlockProps, NameParser}
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

	override def initRendering(): Unit = {}

	@SideOnly(Side.CLIENT)
	override def getSubBlocks(itemIn: Item, tab: CreativeTabs, list: util.List[_]): Unit = {
		list.asInstanceOf[util.List[ItemStack]].addAll(Compression.compressables)
	}

	override def createBlockState(): BlockState = {
		new ExtendedBlockState(
			this, Array[IProperty](), Array[IUnlistedProperty[_]](BlockProps.STATE, CBlocks.INT)
		)
	}

	override def getActualState(
			state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState = {
		state match {
			case extended: IExtendedBlockState =>
				worldIn.getTileEntity(pos) match {
					case compressed: TECompressed =>
						//println (compressed.blockState)
						//println (compressed.tier)
						extended.withProperty(
							BlockProps.STATE, compressed.getBlockState()
						).withProperty(
							CBlocks.INT, compressed.getTier()
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
			val blockName: String = stack.getTagCompound.getString("blockName")
			if (!blockName.isEmpty) {
				worldIn.getTileEntity(pos) match {
					case compressed: TECompressed =>
						compressed.setBlock(NameParser.getState(blockName))
						compressed.setTier(stack.getTagCompound.getInteger("tier"))
					case _ =>
				}
			}
		}
	}

}
