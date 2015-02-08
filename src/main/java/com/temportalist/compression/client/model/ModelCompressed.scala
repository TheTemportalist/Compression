package com.temportalist.compression.client.model

import java.util

import com.temportalist.compression.common.lib.Tupla
import com.temportalist.origin.library.client.utility.Rendering
import com.temportalist.origin.library.common.lib.NameParser
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.{BakedQuad, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.{IBakedModel, SimpleBakedModel}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.{ISmartBlockModel, ISmartItemModel}
import net.minecraftforge.common.property.IExtendedBlockState

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
class ModelCompressed extends ISmartBlockModel with ISmartItemModel {

	var currentSprite: TextureAtlasSprite = null

	private def getModel(inner: ItemStack, size: Long): IBakedModel = {
		var baked: IBakedModel = Rendering.blockShapes.getModelManager.getMissingModel
		if (inner != null) {
			val model = Tupla.getModel(inner)
			val newTex: TextureAtlasSprite = Tupla.getSprite(size)
			baked = new SimpleBakedModel.Builder(model, model.getTexture).makeBakedModel()
		}
		this.currentSprite = baked.getTexture
		baked
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
