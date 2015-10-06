package com.temportalist.compression.client

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.tile.TECompressed
import com.temportalist.origin.api.client.utility.{Rendering, TessRenderer}
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.{TextureUtil, TextureMap}
import net.minecraft.client.renderer.{ItemRenderer, OpenGlHelper, RenderBlocks, RenderHelper}
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import org.lwjgl.opengl.{GL12, GL11}

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
@SideOnly(Side.CLIENT)
object RenderBlockCompressed extends ISimpleBlockRenderingHandler {

	override def getRenderId: Int = Compression.proxy.compressedRenderID

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
				renderer.setRenderBounds(0.0001, 0.0001, 0.0001, 0.9999, 0.9999, 0.9999)
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

	def renderItem(renderType: ItemRenderType, stack: ItemStack, isCompressedItem: Boolean,
			data: Array[AnyRef]): Unit = {
		val innerStack = CBlocks.getInnerStack(stack)
		val tier = CBlocks.getStackTier(stack)
		if (tier < 0) return
		val icon = CBlocks.compressed.icons(tier)

		// Start block and item stack rendering
		// render block start
		Rendering.Gl.push()
		Rendering.Gl.color(1.0F, 1.0F, 1.0F, 1.0F)
		if (!isCompressedItem) {
			RenderHelper.enableStandardItemLighting()
			RenderHelper.enableGUIStandardItemLighting()
		}
		renderType match {
			case ItemRenderType.INVENTORY =>
				///*
				// render things in the inventory
				// render the opaque standard block or item
				Rendering.getRenderItem.renderItemAndEffectIntoGUI(
					Rendering.mc.fontRenderer, Rendering.mc.getTextureManager, innerStack, 0, 0
				)
				if (!isCompressedItem) {
					// render the block overlay as a block
					this.drawBlockOverlay(stack, CBlocks.compressed, Rendering.getRenderBlocks,
						icon, isInGui = true)
				}
				else {
					// render the item overlay
					Rendering.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
					GL11.glEnable(GL11.GL_ALPHA_TEST)

					GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F)
					GL11.glEnable(GL11.GL_BLEND)
					OpenGlHelper.glBlendFunc(770, 771, 1, 0)
					GL11.glTranslated(0, 0, 100)
					GL11.glScalef(16, 16, 16)

					TessRenderer.startQuads()
					TessRenderer.setNormal(0, 0, -1)
					Rendering.getRenderBlocks.renderFaceZNeg(CBlocks.compressed, 0, 0, 0, icon)
					TessRenderer.draw()
				}
				//*/
			case _ =>
				///*
				// rendering for entities and equipped POVs
				if (isCompressedItem) {
					// push full item render matrix
					Rendering.Gl.push()
					// tranform if the rendering is and entity
					if (renderType == ItemRenderType.ENTITY) {
						GL11.glScaled(0.65, 0.65, 0.65)
						GL11.glRotated(25, 1, 0, 1)
						GL11.glRotated(2, 0, 0, 0)
						GL11.glTranslated(0.5, 0, -0.5)
					}

					// render matrix for opaque render
					Rendering.Gl.push()
					val k = innerStack.getItem.getColorFromItemStack(innerStack, 0)
					val red = (k >> 16 & 255).toFloat / 255.0F
					val green = (k >> 8 & 255).toFloat / 255.0F
					val blue = (k & 255).toFloat / 255.0F
					GL11.glColor4f(red, green, blue, 1.0F)

					// render the opaque
					RenderManager.instance.itemRenderer.renderItem(
						Rendering.mc.thePlayer, innerStack, 0, renderType)
					// close matrix for opaque render
					Rendering.Gl.pop()

					// open matrix for overlay
					Rendering.Gl.push()
					Rendering.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)

					GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F)
					GL11.glEnable(GL11.GL_BLEND)
					OpenGlHelper.glBlendFunc(770, 771, 1, 0)

					TextureUtil.func_152777_a(false, false, 1.0F)

					val minU: Float = icon.getMinU
					val maxU: Float = icon.getMaxU
					val minV: Float = icon.getMinV
					val maxV: Float = icon.getMaxV
					GL11.glEnable(GL12.GL_RESCALE_NORMAL)
					GL11.glTranslatef(0, -0.3f, 0)
					val s: Float = 1.5F
					GL11.glScalef(s, s, s)
					GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F)
					GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F)
					GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F)
					// render overlay
					ItemRenderer.renderItemIn2D(TessRenderer.getTess(),
						maxU, minV, minU, maxV, icon.getIconWidth, icon.getIconHeight, 0.0625F)
					GL11.glDisable(GL12.GL_RESCALE_NORMAL)
					Rendering.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
					TextureUtil.func_147945_b()
					// close overlay matrix
					Rendering.Gl.pop()

					// close item render matrix
					Rendering.Gl.pop()
				}
				else {
					// open block render matrix
					Rendering.Gl.push()
					val s = 0.999D
					val s1 = 1 / s
					GL11.glScaled(s, s, s)
					// render opaue block
					RenderManager.instance.itemRenderer.renderItem(
						Rendering.mc.thePlayer, innerStack, 0, renderType)
					GL11.glScaled(s1, s1, s1)
					// close block render matrix
					Rendering.Gl.pop()

					// draw overlay as 3D block
					this.drawBlockOverlay(null, CBlocks.compressed, Rendering.getRenderBlocks,
						icon, isInGui = false)
				}
				//*/
		}
		Rendering.Gl.pop()
	}

	def drawBlockOverlay(stack: ItemStack, block: Block,
			renderer: RenderBlocks, icon: IIcon, isInGui: Boolean): Unit = {
		Rendering.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
		if (isInGui) GL11.glEnable(GL11.GL_ALPHA_TEST)

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F)
		GL11.glEnable(GL11.GL_BLEND)
		OpenGlHelper.glBlendFunc(770, 771, 1, 0)

		if (isInGui) {
			GL11.glPushMatrix()
			GL11.glTranslatef(-2, 3, 100)
			GL11.glScalef(10.0F, 10.0F, 10.0F)
			GL11.glTranslatef(1.0F, 0.5F, 1.0F)
			GL11.glScalef(1.0F, 1.0F, -1.0F)
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F)
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F)

			val l = stack.getItem.getColorFromItemStack(stack, 0)
			val red = (l >> 16 & 255).toFloat / 255.0F
			val green = (l >> 8 & 255).toFloat / 255.0F
			val blue = (l & 255).toFloat / 255.0F

			if (Rendering.getRenderItem.renderWithColor) {
				GL11.glColor4f(red, green, blue, 1.0F)
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F)
		}
		else GL11.glDepthMask(false)

		block.setBlockBoundsForItemRender()
		renderer.setRenderBoundsFromBlock(block)
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F)
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F)
		TessRenderer.startQuads()
		TessRenderer.setNormal(0, -1, 0)
		renderer.renderFaceYNeg(block, 0, 0, 0, icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(0, 1, 0)
		renderer.renderFaceYPos(block, 0, 0, 0, icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(0, 0, -1)
		renderer.renderFaceZNeg(block, 0, 0, 0, icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(0, 0, 1)
		renderer.renderFaceZPos(block, 0, 0, 0, icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(-1, 0, 0)
		renderer.renderFaceXNeg(block, 0, 0, 0, icon)
		TessRenderer.draw()
		TessRenderer.startQuads()
		TessRenderer.setNormal(1, 0, 0)
		renderer.renderFaceXPos(block, 0, 0, 0, icon)
		TessRenderer.draw()

		GL11.glTranslatef(0.5F, 0.5F, 0.5F)
		if (isInGui) GL11.glPopMatrix()
		else GL11.glDepthMask(true)
	}

}
