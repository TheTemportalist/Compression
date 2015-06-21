package com.temportalist.compression.common.blocks

import java.util

import com.sun.deploy.panel.IProperty
import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.ItemCompressed
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.origin.api.common.block.BlockTile
import com.temportalist.origin.api.common.lib.{BlockState, BlockPos}
import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.{IBlockAccess, World}

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class BlockCompressed(name: String, te: Class[_ <: TileEntity]) extends BlockTile(
	Material.ground, Compression.MODID, name, classOf[ItemCompressed], te) {

	@SideOnly(Side.CLIENT)
	override def getSubBlocks(itemIn: Item, tab: CreativeTabs, list: util.List[_]): Unit = {
		// return a list of ALL compressable blocks
		list.asInstanceOf[util.List[ItemStack]].addAll(Compression.compressables)
	}

	override def createBlockState(): BlockState = {
		new ExtendedBlockState(
			this, Array[IProperty](),
			Array[IUnlistedProperty[_]](BlockProps.ITEMSTACK, CBlocks.LONG)
		)
	}

	override def getExtendedState(
			state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
		state match {
			case extended: IExtendedBlockState =>
				world.getTileEntity(pos) match {
					case compressed: TECompressed =>
						extended.withProperty(
							BlockProps.ITEMSTACK, compressed.getState()
						).withProperty(
						            CBlocks.LONG,
						            compressed.getSize() //Long.box(compressed.getSize())
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
						compressed.setStack(NameParser.getItemStack(innerName))
						compressed.setSize(stack.getTagCompound.getLong("stackSize"))

						val mult: Int = Tupla.getTierFromSize(compressed.getSize())
						val innerStack: ItemStack = compressed.getState()
						val innerState: IBlockState = States.getState(innerStack)
						val block: Block = Block.getBlockFromItem(innerStack.getItem)
						this.setHarvestLevel(
							block.getHarvestTool(innerState), block.getHarvestLevel(innerState)
						)
						this.blockHardness = block.getBlockHardness(worldIn, pos) * mult
						this.blockResistance = block.getExplosionResistance(null) * 5 * mult
						this.lightValue = block.getLightValue
						this.lightOpacity = block.getLightOpacity
						this.slipperiness = block.slipperiness

						compressed.markDirty()
					case _ =>
				}
			}
		}
	}

	override def canRenderInLayer(layer: EnumWorldBlockLayer): Boolean = {
		true //CBlocks.validRenderLayers.contains(layer)
	}

	override def canRenderInPass(pass: Int): Boolean = true

	override def getRenderColor(state: IBlockState): Int = {
		state match {
			case extended: IExtendedBlockState =>
				println(extended.getValue(BlockProps.ITEMSTACK))
			case _ =>
		}
		super.getRenderColor(state)
	}

	override def onBlockHarvested(
			worldIn: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {
		if (player.capabilities.isCreativeMode) return
		worldIn.getTileEntity(pos) match {
			case compressed: TECompressed =>
				if (compressed.getState() != null) {
					Block.spawnAsEntity(worldIn, pos, Compression.constructCompressed(
						compressed.getState()
					))
				}
			case _ =>
		}
	}

	def getCompressedBlock(world: World, pos: BlockPos): ItemStack = {
		world.getTileEntity(pos) match {
			case compressed: TECompressed =>
				if (compressed.getState() != null)
					return Compression.constructCompressed(compressed.getState(), compressed.getSize())
			case _ =>
		}
		null
	}

	override def getPickBlock(
			target: MovingObjectPosition, world: World, pos: BlockPos): ItemStack = {
		val stack: ItemStack = this.getCompressedBlock(world, pos)
		if (stack != null)
			stack
		else
			super.getPickBlock(target, world, pos)
	}

	override def getDrops_Pre(world: World, pos: BlockPos, state: IBlockState,
			tile: TileEntity): util.List[ItemStack] = {
		val list: util.List[ItemStack] = new util.ArrayList[ItemStack]()
		val stack: ItemStack = this.getCompressedBlock(world, pos)
		if (stack != null)
			list.add(stack)
		list
	}

	override def colorMultiplier(worldIn: IBlockAccess, pos: BlockPos, renderPass: Int): Int = {
		worldIn.getTileEntity(pos) match {
			case compressed: TECompressed =>
				if (compressed.getState() != null) {
					return States.getState(compressed.getState()).getBlock.colorMultiplier(
						worldIn, pos, renderPass
					)
				}
			case _ =>
		}
		super.colorMultiplier(worldIn, pos, renderPass)
	}

}
