package com.temportalist.compression.common.blocks

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.item.ItemBlockCompressor
import com.temportalist.origin.api.common.block.BlockTile
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.BlockPistonBase
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 9/8/2015.
 */
class BlockCompress(name: String, te: Class[_ <: TileEntity]) extends BlockTile(
	Material.ground, Compression.getModid, name, classOf[ItemBlockCompressor], te) {

	override def onBlockActivated(worldIn: World, x: Int, y: Int, z: Int, player: EntityPlayer,
			side: Int, subX: Float, subY: Float, subZ: Float): Boolean = {
		if (!player.isSneaking) {
			player.openGui(Compression, 0, worldIn, x, y, z)
			true
		}
		else super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ)
	}

	//override def renderAsNormalBlock(): Boolean = false

	//override def isOpaqueCube: Boolean = false

	//override def getRenderType: Int = -1

	@SideOnly(Side.CLIENT)
	var icons: Array[IIcon] = _

	@SideOnly(Side.CLIENT)
	override def registerBlockIcons(reg: IIconRegister): Unit = {
		this.icons = Array[IIcon](
			reg.registerIcon(this.getCompoundName + "_verticalFace"),
			reg.registerIcon(this.getCompoundName + "_sideOpen"),
			reg.registerIcon(this.getCompoundName + "_sideClosed"),
			BlockPistonBase.getPistonBaseIcon("piston_top_normal")
		)
		this.blockIcon = this.icons(0)
	}

}
