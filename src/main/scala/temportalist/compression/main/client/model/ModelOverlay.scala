package temportalist.compression.main.client.model

import java.util

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemCameraTransforms, ItemOverrideList}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
abstract class ModelOverlay extends IBakedModel {

	override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = ???

	override def getParticleTexture: TextureAtlasSprite = ???

	override def isBuiltInRenderer: Boolean = false

	override def isAmbientOcclusion: Boolean = true

	override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

	override def getOverrides: ItemOverrideList = ItemOverrideList.NONE

}
