package com.temportalist.compression.client

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.origin.api.client.utility.{Rendering, TessRenderer}
import com.temportalist.origin.api.common.utility.WorldHelper
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.{RenderBlocks, RenderHelper}
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
@SideOnly(Side.CLIENT)
object RenderBlockCompressed extends ISimpleBlockRenderingHandler {

	override def getRenderId: Int = CBlocks.compressedRenderID

	override def shouldRender3DInInventory(modelId: Int): Boolean = false

	override def renderInventoryBlock(block: Block, metadata: Int, modelId: Int,
			renderer: RenderBlocks): Unit = {
		Compression.log("THIS SHOULD NOT BE BEING CALLED!!! (" +
				this.getClass.getCanonicalName + ")")
	}

	override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block,
			modelId: Int, renderer: RenderBlocks): Boolean = {
		var tier = 0
		world.getTileEntity(x, y, z) match {
			case tile: TECompressed =>
				tier = tile.getTier
				//renderer.renderBlockByRenderType(tile.getStackBlock, x, y, z)
				renderer.setRenderBounds(0.01, 0.01, 0.01, 0.99, 0.99, 0.99)
				renderer.renderStandardBlock(tile.getStackBlock, x, y, z)
			case _ =>
		}

		Rendering.Gl.push()
		Rendering.Gl.pushAttribute(GL11.GL_ENABLE_BIT)
		Rendering.Gl.blend(isOn = true)
		Rendering.Gl.blendSrcAlpha()
		renderer.renderBlockUsingTexture(Blocks.stone, x, y, z, CBlocks.compressed.icons(tier))
		Rendering.Gl.popAttribute()
		Rendering.Gl.pop()

		true
	}

	def renderItem(renderType: ItemRenderType, stack: ItemStack, data: Array[AnyRef]): Unit = {
		val innerStack = CBlocks.getInnerStack(stack)
		val tier = CBlocks.getStackTier(stack)
		val isBlock = WorldHelper.isBlock(innerStack.getItem)

		// Start block and item stack rendering
		///*
		Rendering.Gl.push()
		Rendering.Gl.color(1.0F, 1.0F, 1.0F, 1.0F)
		RenderHelper.enableStandardItemLighting()
		RenderHelper.enableGUIStandardItemLighting()
		renderType match {
			case ItemRenderType.INVENTORY =>
				Rendering.getRenderItem.renderItemAndEffectIntoGUI(
					Rendering.mc.fontRenderer, Rendering.mc.getTextureManager, innerStack, 0, 0
				)
			case ItemRenderType.ENTITY =>
				if (!isBlock) {
					Rendering.Gl.push()
					GL11.glScaled(0.65, 0.65, 0.65)
					GL11.glRotated(25, 1, 0, 1)
					GL11.glRotated(2, 0, 0, 0)
					GL11.glTranslated(0.5, 0, -0.5)

					val k = innerStack.getItem.getColorFromItemStack(innerStack, 0)
					val red = (k >> 16 & 255).toFloat / 255.0F
					val green = (k >> 8 & 255).toFloat / 255.0F
					val blue = (k & 255).toFloat / 255.0F
					GL11.glColor4f(red, green, blue, 1.0F)
				}
				RenderManager.instance.itemRenderer.renderItem(
					Rendering.mc.thePlayer, innerStack, 0, renderType)
				if (!isBlock) Rendering.Gl.pop()
			case _ =>
				RenderManager.instance.itemRenderer.renderItem(
					Rendering.mc.thePlayer, innerStack, 0, renderType)
		}
		if (isBlock) {

		}
		else {
			GL11.glScaled(0.06, 0.06, 0.06)
			GL11.glRotated(50, 0, 1, 0)
			GL11.glTranslated(0, -4, -100)

			Rendering.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
			val z = Rendering.getRenderItem.zLevel
			Rendering.getRenderItem.zLevel = 100
			Rendering.getRenderItem.renderIcon(0, 0, CBlocks.compressed.icons(tier), 16, 16)
			Rendering.getRenderItem.zLevel = z
		}
		Rendering.Gl.blend(isOn = false)
		Rendering.Gl.pop()
		//*/
		// end opaque render

		if (WorldHelper.isBlock(innerStack.getItem)) {
			Rendering.Gl.push()
			Rendering.Gl.pushAttribute(GL11.GL_ENABLE_BIT)
			Rendering.Gl.blend(isOn = true)
			Rendering.Gl.blendSrcAlpha()

			TessRenderer.startQuads()
			Rendering.getRenderBlocks.renderFaceYPos(Blocks.stone, 0.0D, 1D, 0.0D,
				CBlocks.compressed.icons(4))
			TessRenderer.draw()

			Rendering.Gl.popAttribute()
			Rendering.Gl.pop()
		}

	}

	def drawBlock(block: Block, meta: Int, renderer: RenderBlocks): Unit = {
		this.drawBlock(block, meta, renderer, null)
	}

	def drawBlock(block: Block, meta: Int, renderer: RenderBlocks, icon: IIcon): Unit = {
		TessRenderer.startQuads()
		TessRenderer.setNormal(0.0F, -1.0F, 0.0F)
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D,
			if (icon == null) block.getIcon(0, meta) else icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(0.0F, 1.0F, 0.0F)
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D,
			if (icon == null) block.getIcon(1, meta) else icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(0.0F, 0.0F, -1.0F)
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D,
			if (icon == null) block.getIcon(2, meta) else icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(0.0F, 0.0F, 1.0F)
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D,
			if (icon == null) block.getIcon(3, meta) else icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(-1.0F, 0.0F, 0.0F)
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D,
			if (icon == null) block.getIcon(4, meta) else icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(1.0F, 0.0F, 0.0F)
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D,
			if (icon == null) block.getIcon(5, meta) else icon)
		TessRenderer.draw()
	}

}
