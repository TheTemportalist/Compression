package temportalist.compression.main.client.model

import java.util

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemCameraTransforms, ItemOverrideList}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.{BlockRenderLayer, EnumFacing}
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.property.IExtendedBlockState
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.lib.{BlockProperties, EnumTier}

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class BakedCompressed(private val overlays: Array[TextureAtlasSprite]) extends IBakedModel {

	private val overrideList = new ItemListCompressed(overlays)

	override def getQuads(state: IBlockState, side: EnumFacing, rand: Long): util.List[BakedQuad] = {
		state match {
			case extended: IExtendedBlockState =>
				val sampleStack = extended.getValue(BlockProperties.ITEMSTACK_UN)
				if (sampleStack == null) {
					//Compression.log("ERROR: " + state.toString + " has null inner stack!")
					return new util.ArrayList[BakedQuad]()
				}
				val sampleBlock = Block.getBlockFromItem(sampleStack.getItem)
				val sampleState = sampleBlock.getStateFromMeta(sampleStack.getItemDamage)
				val sampleModel = Minecraft.getMinecraft.getBlockRendererDispatcher.
						getBlockModelShapes.getModelForState(sampleState)

				return (MinecraftForgeClient.getRenderLayer match {
					case BlockRenderLayer.SOLID => sampleModel
					case BlockRenderLayer.TRANSLUCENT =>
						val i = EnumTier.getTierForSize(extended.getValue(BlockProperties.LONG_UN)).ordinal()
						val overlayModel = new Builder(sampleState, sampleModel, overlays(i), BlockPos.ORIGIN).makeBakedModel()
						overlayModel
					case _ => null
				}).getQuads(state, side, rand)

			case _ =>
		}
		new util.ArrayList[BakedQuad]()
	}

	override def isAmbientOcclusion: Boolean = true

	override def isBuiltInRenderer: Boolean = false

	override def isGui3d: Boolean = true

	override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

	override def getParticleTexture: TextureAtlasSprite = overlays.last

	override def getOverrides: ItemOverrideList = this.overrideList

}
