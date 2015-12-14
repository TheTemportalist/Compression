package com.temportalist.compression.common.blocks

import java.util

import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.item.ItemBlockCompressed
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.compression.common.{Rank, CompressedStack, Compression}
import com.temportalist.origin.api.common.block.BlockTile
import com.temportalist.origin.api.common.lib.{BlockState, NameParser, V3O}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{IIcon, MovingObjectPosition}
import net.minecraft.world.{IBlockAccess, World}

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
class BlockCompressed(name: String, te: Class[_ <: TileEntity]) extends BlockTile(
	Material.ground, Compression.getModid, name, classOf[ItemBlockCompressed], te) {

	override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase,
			stack: ItemStack): Unit = {
		if (CompressedStack.isCompressedStack(stack)) world.getTileEntity(x, y, z) match {
			case compressed: TECompressed =>
				// write inner stats to tile
				CBlocks.writeCompressedToTile(stack, compressed)

				// set block details based on stats from tile
				val mult = Rank.getRank(compressed.getSize).getIndex
				val innerStack = NameParser.getItemStack(compressed.getStackString)
				val block = Block.getBlockFromItem(innerStack.getItem)

				this.setHarvestLevel(
					block.getHarvestTool(innerStack.getItemDamage),
					block.getHarvestLevel(innerStack.getItemDamage)
				)
				this.blockHardness = block.getBlockHardness(world, x, y, z) * mult
				this.blockResistance = block.getExplosionResistance(null) * 5 * mult
				this.lightValue = block.getLightValue
				this.lightOpacity = block.getLightOpacity
				this.slipperiness = block.slipperiness

				compressed.markDirty()
			case _ =>
		}
	}

	def getCompressedBlock(world: World, pos: V3O): ItemStack = {
		pos.getTile(world) match {
			case compressed: TECompressed =>
				CBlocks.wrapInnerStack(compressed.getStack, compressed.getSize)
			case _ => null
		}
	}

	/*
	override def onBlockHarvested(world: World, x: Int, y: Int, z: Int, meta: Int,
			player: EntityPlayer): Unit = {
		if (player.capabilities.isCreativeMode) return
		this.getCompressedBlock(world, new V3O(x, y, z)) match {
			case dropStack: ItemStack =>
				Stacks.spawnItemStack(world, new V3O(x, y, z), dropStack, world.rand, 10)
			case _ =>
		}
	}
	*/

	override def getDrops_Pre(world: World, pos: V3O, state: BlockState,
			tile: TileEntity): util.List[ItemStack] = {
		val list: util.List[ItemStack] = new util.ArrayList[ItemStack]()
		this.getCompressedBlock(world, pos) match {
			case stack: ItemStack => list.add(stack)
			case _ =>
		}
		list
	}

	override def getPickBlock(target: MovingObjectPosition, world: World, x: Int, y: Int, z: Int,
			player: EntityPlayer): ItemStack = {
		val stack: ItemStack = this.getCompressedBlock(world, new V3O(x, y, z))
		if (stack != null) stack
		else super.getPickBlock(target, world, x, y, z, player)
	}

	override def colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int = {
		world.getTileEntity(x, y, z) match {
			case compressed: TECompressed =>
				compressed.getStackBlock.colorMultiplier(world, x, y, z)
			case _ => super.colorMultiplier(world, x, y, z)
		}
	}

	@SideOnly(Side.CLIENT)
	override def getSubBlocks(itemIn: Item, tab: CreativeTabs, list: util.List[_]): Unit = {
		// return a list of ALL compressable blocks
		list.asInstanceOf[util.List[ItemStack]].addAll(CBlocks.compressedBlocks)
	}

	override def renderAsNormalBlock(): Boolean = false

	override def isOpaqueCube: Boolean = false

	override def getRenderType: Int = Compression.proxy.compressedRenderID

	override def canRenderInPass(pass: Int): Boolean = true

	override def getRenderBlockPass: Int = 1

	@SideOnly(Side.CLIENT)
	var icons: Array[IIcon] = _

	@SideOnly(Side.CLIENT)
	override def registerBlockIcons(reg: IIconRegister): Unit = {
		this.icons = new Array[IIcon](18)
		for (i <- 1 to 18) this.icons(i - 1) = reg.registerIcon(this.modid + ":overlay_" + i)
	}

}
