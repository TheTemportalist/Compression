package com.temportalist.compression.client.model

import java.util

import com.temportalist.compression.common.lib.Tupla
import com.temportalist.origin.library.client.utility.Rendering
import com.temportalist.origin.library.common.lib.NameParser
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.{BakedQuad, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.{SimpleBakedModel, IBakedModel}
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

	var currentSprite: TextureAtlasSprite = null

	private def getLayer(): EnumWorldBlockLayer = MinecraftForgeClient.getRenderLayer

	private def getModel(inner: ItemStack, size: Long): IBakedModel = {
		var baked: IBakedModel = Rendering.blockShapes.getModelManager.getMissingModel
		if (inner == null) baked
		else {
			baked = Tupla.getModel(inner)
			this.currentSprite = baked.getTexture
			if (this.getLayer() == EnumWorldBlockLayer.SOLID)
				baked
			else
				new SimpleBakedModel.Builder(model, Tupla.getSprite(size)).makeBakedModel()
		}
	}

	override def handleBlockState(state: IBlockState): IBakedModel = {
		state match {
			case extended: IExtendedBlockState =>
				this.getModel(extended.getValue(Tupla.ITEMSTACK), 90L)
			case _ =>
				Rendering.blockShapes.getModelManager.getMissingModel
		}
	}

	override def handleItemState(itemStack: ItemStack): IBakedModel = {
		if (itemStack.hasTagCompound) {
			this.getModel(
				NameParser.getItemStack(itemStack.getTagCompound.getString("inner")),
				itemStack.getTagCompound.getLong("stackSize")
			)
		}
		else Rendering.blockShapes.getModelManager.getMissingModel
	}

	override def getFaceQuads(enumFacing: EnumFacing): util.List[BakedQuad] = null

	override def getGeneralQuads: util.List[BakedQuad] = null

	override def getTexture: TextureAtlasSprite = this.currentSprite

	override def isAmbientOcclusion: Boolean = true

	override def isBuiltInRenderer: Boolean = false

	override def isGui3d: Boolean = true

	override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

}
