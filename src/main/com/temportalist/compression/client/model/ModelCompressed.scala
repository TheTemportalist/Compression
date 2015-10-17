package com.temportalist.compression.client.model

import java.util

import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.origin.library.client.utility.Rendering
import com.temportalist.origin.library.common.lib.{BlockProps, NameParser}
import com.temportalist.origin.library.common.utility.WorldHelper
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.{BakedQuad, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.{IBakedModel, SimpleBakedModel}
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, EnumWorldBlockLayer}
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.client.model.{ISmartBlockModel, ISmartItemModel}
import net.minecraftforge.common.property.IExtendedBlockState

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class ModelCompressed extends ISmartBlockModel with ISmartItemModel {

	// todo there is no alpha in the overlay texture for either block or item model, or is there?

	private def getLayer(): EnumWorldBlockLayer = MinecraftForgeClient.getRenderLayer

	private def getModel(inner: ItemStack, size: Long, layer: EnumWorldBlockLayer,
			isItem: Boolean): IBakedModel = {
		if (inner == null)
			Rendering.blockShapes.getModelManager.getMissingModel
		else {
			val baked: IBakedModel = Rendering.getModel(inner, isItem)
			if (layer == EnumWorldBlockLayer.SOLID) {
				baked
			}
			else {
				new SimpleBakedModel.Builder(
					new IBakedModel {
						override def isBuiltInRenderer: Boolean = baked.isBuiltInRenderer
						override def getItemCameraTransforms: ItemCameraTransforms =
							baked.getItemCameraTransforms
						override def getTexture: TextureAtlasSprite = {
							if (baked.getTexture == null)
								Rendering.mc.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/slime")
							else baked.getTexture
						}
						override def isAmbientOcclusion: Boolean = baked.isAmbientOcclusion
						override def getGeneralQuads: util.List[_] = {
							if (baked.getGeneralQuads == null)
								new util.ArrayList[BakedQuad]()
							else baked.getGeneralQuads
						}
						override def isGui3d: Boolean = baked.isGui3d
						override def getFaceQuads(face : EnumFacing): util.List[_] = {
							if (baked.getFaceQuads(face) == null)
								new util.ArrayList[BakedQuad]()
							else baked.getFaceQuads(face)
						}
					}, Tupla.getSprite(size)).makeBakedModel()
			}
		}
	}

	override def handleBlockState(state: IBlockState): IBakedModel = {
		state match {
			case extended: IExtendedBlockState =>
				val stack: ItemStack = extended.getValue(BlockProps.ITEMSTACK)
				val value = extended.getUnlistedProperties.get(CBlocks.LONG).orNull()
				val long: Long = Long.unbox(value.asInstanceOf[Object])
				return this.getModel(
					stack,
					long,
					this.getLayer(), false
				)
			case _ =>
		}
		Rendering.blockShapes.getModelManager.getMissingModel
	}

	override def handleItemState(itemStack: ItemStack): IBakedModel = {
		if (itemStack.hasTagCompound) {
			val innerStack: ItemStack = NameParser
					.getItemStack(itemStack.getTagCompound.getString("inner"))
			val size: Long = itemStack.getTagCompound.getLong("stackSize")
			val fullQuadList: util.List[BakedQuad] = new util.ArrayList[BakedQuad]()
			var tex: TextureAtlasSprite = null
			var camera: ItemCameraTransforms = null

			for (layer: EnumWorldBlockLayer <- EnumWorldBlockLayer.values()) {
				//CBlocks.validRenderLayers) {
				val model: IBakedModel = this.getModel(innerStack, size, layer, true)
				if (layer == EnumWorldBlockLayer.SOLID) {
					tex = model.getTexture
					camera = model.getItemCameraTransforms
				}
				if (model.getGeneralQuads != null)
					fullQuadList.addAll(model.getGeneralQuads.asInstanceOf[util.List[BakedQuad]])
				for (face <- EnumFacing.values()) if (model.getFaceQuads(face) != null)
					fullQuadList.addAll(model.getFaceQuads(face).asInstanceOf[util.List[BakedQuad]])
			}
			new IBakedModel {
				override def isBuiltInRenderer: Boolean = false

				override def getItemCameraTransforms: ItemCameraTransforms = camera

				override def getTexture: TextureAtlasSprite = tex

				override def isAmbientOcclusion: Boolean = true

				override def getGeneralQuads: util.List[_] = fullQuadList

				override def isGui3d: Boolean = WorldHelper.isBlock(innerStack.getItem)

				override def getFaceQuads(p_177551_1_ : EnumFacing): util.List[_] = new
								util.ArrayList[Nothing]()
			}
		}
		else Rendering.blockShapes.getModelManager.getMissingModel
	}

	override def getFaceQuads(enumFacing: EnumFacing): util.List[BakedQuad] =
		new util.ArrayList[BakedQuad]()

	override def getGeneralQuads: util.List[BakedQuad] = new util.ArrayList[BakedQuad]()

	override def getTexture: TextureAtlasSprite =
	Rendering.mc.getTextureMapBlocks.getAtlasSprite("compression:blocks/overlay_18")

	override def isAmbientOcclusion: Boolean = true

	override def isBuiltInRenderer: Boolean = false

	override def isGui3d: Boolean = true

	override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

}