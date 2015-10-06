package com.temportalist.compression.client

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.blocks.BlockCompress
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.tile.TECompress
import com.temportalist.origin.api.client.render.TERenderer
import com.temportalist.origin.api.client.utility.{Rendering, TessRenderer}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.{ItemRenderType, ItemRendererHelper}
import org.lwjgl.opengl.GL11

/**
 * Created by TheTemportalist on 9/9/2015.
 */
@SideOnly(Side.CLIENT)
object RenderCompressor extends TERenderer with IItemRenderer {

	override def shouldUseRenderHelper(`type`: ItemRenderType, item: ItemStack,
			helper: ItemRendererHelper): Boolean = {
		`type` == ItemRenderType.ENTITY ||
				(`type` == ItemRenderType.INVENTORY && helper == ItemRendererHelper.BLOCK_3D)
		true
	}

	override def handleRenderType(item: ItemStack, `type`: ItemRenderType): Boolean = true

	override def renderItem(`type`: ItemRenderType, item: ItemStack, data: AnyRef*): Unit = {
		Rendering.Gl.push()

		if (`type` == ItemRenderType.EQUIPPED_FIRST_PERSON) GL11.glTranslated(0.5, 0.5, 0.5)
		else if (`type` == ItemRenderType.EQUIPPED) GL11.glTranslated(0.5, 0.5, 0.5)

		this.render(null, 0, 0.0625F)

		Rendering.Gl.pop()
	}

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {
		Rendering.Gl.push()
		Rendering.Gl.colorFull()

		val block: BlockCompress = CBlocks.compressor
		val isClosed: Boolean = tileEntity != null ||
				(tileEntity.isInstanceOf[TECompress] &&
						tileEntity.asInstanceOf[TECompress].getTime <= 7)
		val sideIcon = block.icons(if (isClosed) 2 else 1)

		//Rendering.getRenderBlocks.setRenderBounds(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5)


		// Bottom
		Rendering.bindResource(new ResourceLocation("minecraft:textures/blocks/cobblestone.png"))
		//Rendering.getRenderBlocks.renderFaceYNeg(block, 0, 0, 0, block.icons(0))
		val max = 0.5
		val min = -0.5
		TessRenderer.startQuads()

		TessRenderer.addVertex(max, max, 0, 5, 1)
		TessRenderer.addVertex(max, min, 0, 5, 0)
		TessRenderer.addVertex(min, min, 0, 0, 0)
		TessRenderer.addVertex(min, max, 0, 0, 1)

		TessRenderer.draw()

		// Top
		//TessRenderer.setNormal(0, 1, 0)
		//Rendering.getRenderBlocks.renderFaceYPos(block, 0, 0, 0, block.icons(0))

		// North -Z
		//TessRenderer.setNormal(0, 0, -1)
		//Rendering.getRenderBlocks.renderFaceZNeg(block, 0, 0, 0, sideIcon)

		// South +Z
		//TessRenderer.setNormal(0, 0, -1)
		//Rendering.getRenderBlocks.renderFaceZPos(block, 0, 0, 0, sideIcon)

		// West -X
		//TessRenderer.setNormal(-1, 0, 0)
		//Rendering.getRenderBlocks.renderFaceXNeg(block, 0, 0, 0, sideIcon)

		// East +X
		//TessRenderer.setNormal(1, 0, 0)
		//Rendering.getRenderBlocks.renderFaceXPos(block, 0, 0, 0, sideIcon)

		// Piston face
		/*
		if (!isClosed) {
			val y = 0.13
			Rendering.getRenderBlocks.setRenderBounds(-0.5, -y, -0.5, 0.5, -y, 0.5)
			// Bottom facing Top
			TessRenderer.setNormal(0, 1, 0)
			Rendering.getRenderBlocks.renderFaceYPos(block, 0, 0, 0, block.icons(3))

			Rendering.getRenderBlocks.setRenderBounds(-0.5, y, -0.5, 0.5, y, 0.5)
			// Top facing Bottom
			TessRenderer.setNormal(0, -1, 0)
			Rendering.getRenderBlocks.renderFaceYNeg(block, 0, 0, 0, block.icons(3))
		}
		*/

		Rendering.Gl.pop()
	}

}
