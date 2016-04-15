package temportalist.compression.main.client.model

import java.util

import com.google.common.collect.Lists
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemCameraTransforms, ItemOverrideList}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class BakedCompressed extends IBakedModel {

	private val overrideList = new ItemListCompressed

	//val overlayModel = ModelLoaderRegistry.getModel()
	override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
		Lists.newArrayList()
	}

	override def isAmbientOcclusion: Boolean = true

	override def isBuiltInRenderer: Boolean = false

	override def isGui3d: Boolean = true

	override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

	override def getParticleTexture: TextureAtlasSprite =
		Minecraft.getMinecraft.getTextureMapBlocks.getMissingSprite

	override def getOverrides: ItemOverrideList = this.overrideList

}
