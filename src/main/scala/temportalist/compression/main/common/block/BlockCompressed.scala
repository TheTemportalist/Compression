package temportalist.compression.main.common.block

import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.{BlockPos, RayTraceResult}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState, IUnlistedProperty}
import temportalist.compression.main.common.block.tile.{TileCompressed, TileCompressedTickable}
import temportalist.compression.main.common.init.{Compressed, ModBlocks}
import temportalist.compression.main.common.item.ItemBlockCompressed
import temportalist.compression.main.common.lib.BlockProperties.{ITEMSTACK_UN, LONG_UN}
import temportalist.compression.main.common.{Compression, Effects}
import temportalist.origin.api.common.block.BlockTile

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class BlockCompressed extends BlockTile(Compression, classOf[TileCompressed]) {

	override def createItemBlock(): ItemBlock = {
		ModBlocks.blockItem = new ItemBlockCompressed(this)
		ModBlocks.blockItem
	}

	override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState,
			placer: EntityLivingBase, stack: ItemStack): Unit = {
		if (!stack.hasTagCompound) return

		if (Effects.shouldUseTickingTile(stack)) {
			worldIn.removeTileEntity(pos)
			worldIn.setTileEntity(pos, new TileCompressedTickable)
		}

		worldIn.getTileEntity(pos) match {
			case tile: TileCompressed =>
				tile.setStack(Compressed.getSampleStack(stack))
				tile.setSize(Compressed.getSize(stack))

				val tier = Compressed.getTier(stack)
				val mult = tier.ordinal() + 1
				val sampleState = Compressed.getSampleState(stack)
				val block = sampleState.getBlock
				this.setHarvestLevel(
					block.getHarvestTool(sampleState), block.getHarvestLevel(sampleState)
				)
				this.setHardness(block.getBlockHardness(sampleState, worldIn, pos) * mult)
				//this.setResistance(block.blockResistance * mult)
				//this.lightValue = block.lightValue * mult
				//this.lightOpacity = block.lightOpacity * mult
				//this.slipperiness = block.slipperiness * mult

				tile.markDirty()
			case _ =>
		}
	}

	override def canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean =
		layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT

	override protected def createBlockState(): BlockStateContainer = {
		new ExtendedBlockState(this, Array[IProperty[_]](

		), Array[IUnlistedProperty[_]](
			ITEMSTACK_UN, LONG_UN
		))
	}

	override def getExtendedState(state: IBlockState, world: IBlockAccess,
			pos: BlockPos): IBlockState = {
		state match {
			case extended: IExtendedBlockState =>
				world.getTileEntity(pos) match {
					case tile: TileCompressed =>
						return extended.withProperty(ITEMSTACK_UN, tile.getStack.copy()
						).withProperty(LONG_UN, Long.box(tile.getSize))
					case _ =>
				}
			case _ =>
		}
		null
	}

	override def getPickBlock(state: IBlockState, target: RayTraceResult, world: World,
			pos: BlockPos, player: EntityPlayer): ItemStack = {
		world.getTileEntity(pos) match {
			case tile: TileCompressed =>
				val sample = tile.getStack
				val size = tile.getSize
				Compressed.createWithSize(sample, size)
			case _ => Compressed.create(new ItemStack(Blocks.stone))
		}
	}

}
