package com.temportalist.compression.client.model

import java.util

import com.temportalist.compression.common.init.CBlocks
import com.temportalist.origin.library.client.utility.Rendering
import com.temportalist.origin.library.common.lib.{BlockProps, NameParser}
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

	private def getModel(innerBlock: IBlockState, tier: Int): IBakedModel = {
		if (innerBlock == null) return Rendering.blockShapes.getModelManager.getMissingModel
		val model: IBakedModel = Rendering.blockShapes.getModelForState(innerBlock)
		val originalTex: TextureAtlasSprite = Rendering.blockShapes.getTexture(innerBlock)
		val newTex: TextureAtlasSprite = originalTex // todo, this is the overlay texture
		new SimpleBakedModel.Builder(model, newTex).makeBakedModel()
	}

	override def handleBlockState(state: IBlockState): IBakedModel = {
		println ("block")
		state match {
			case extended: IExtendedBlockState =>
				val tier: Int = extended.getValue(CBlocks.INT)
				val innerBlock: IBlockState = extended.getValue(BlockProps.STATE)
				println (this.getModel(innerBlock, tier))
				this.getModel(innerBlock, tier)
			case _ =>
				println ("missing")
				Rendering.blockShapes.getModelManager.getMissingModel
		}
	}

	override def handleItemState(itemStack: ItemStack): IBakedModel = {
		//println ("item")
		if (itemStack.hasTagCompound) {
			val tier: Int = itemStack.getTagCompound.getInteger("tier")
			val innerBlock: IBlockState = NameParser.getState(
				itemStack.getTagCompound.getString("blockName")
			)
			this.getModel(innerBlock, tier)
		}
		else Rendering.blockShapes.getModelManager.getMissingModel
	}

	override def getFaceQuads(enumFacing: EnumFacing): util.List[BakedQuad] = null

	override def getGeneralQuads: util.List[BakedQuad] = null

	override def getTexture: TextureAtlasSprite = null

	override def isAmbientOcclusion: Boolean = true

	override def isBuiltInRenderer: Boolean = false

	override def isGui3d: Boolean = true

	override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

}
