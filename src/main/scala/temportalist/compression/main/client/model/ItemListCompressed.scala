package temportalist.compression.main.client.model

import java.util

import com.google.common.collect.Lists
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.lib.EnumTier
import temportalist.origin.api.common.helper.Names

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class ItemListCompressed(private val overlays: Array[TextureAtlasSprite])
		extends ItemOverrideList(Lists.newArrayList()) {

	def getMissingModel: IBakedModel =
		Minecraft.getMinecraft.getBlockRendererDispatcher.getBlockModelShapes.getModelManager.getMissingModel

	override def handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World,
			entity: EntityLivingBase): IBakedModel = {
		if (!stack.hasTagCompound) return this.getMissingModel

		val sampleStack = Compressed.getSampleStack(stack)
		val isBlock = stack.getItem.isInstanceOf[ItemBlock]

		val sampleModel =
			if (isBlock)
				Minecraft.getMinecraft.getBlockRendererDispatcher.getBlockModelShapes.
						getModelForState(Compressed.getSampleState(stack))
			else
				Minecraft.getMinecraft.getRenderItem.getItemModelMesher.getItemModel(sampleStack)

		val size = Compressed.getSize(stack)
		val i = EnumTier.getTierForSize(size).ordinal()
		val overlay = overlays(i)

		new IBakedModel {

			override def getParticleTexture: TextureAtlasSprite = sampleModel.getParticleTexture

			override def isBuiltInRenderer: Boolean = sampleModel.isBuiltInRenderer

			override def getItemCameraTransforms: ItemCameraTransforms = sampleModel.getItemCameraTransforms

			override def isAmbientOcclusion: Boolean = true

			override def isGui3d: Boolean = isBlock

			override def getOverrides: ItemOverrideList = sampleModel.getOverrides

			override def getQuads(state: IBlockState,
					side: EnumFacing,
					rand: Long): util.List[BakedQuad] = {

				val quadList = new util.ArrayList[BakedQuad]()

				try {
					quadList.addAll(sampleModel.getQuads(state, side, rand))
					if (overlay != null) {
						val overlayModel = new Builder(
							state, sampleModel, overlay, BlockPos.ORIGIN).makeBakedModel()
						quadList.addAll(overlayModel.getQuads(state, side, rand))
					}
				}
				catch {
					case e: Exception =>
						Compression.log("Error merging render models. " +
								"Please report this to https://github.com/TheTemportalist/Compression/issues. " +
								"As a temporary fix, you can consider adding \'" +
								Names.getName(stack, hasID = true, hasMeta = false) +
								"\' or \'" +
								Names.getName(stack, hasID = true, hasMeta = true) +
								"\' to the blacklist configuration option.")
						e.printStackTrace()
				}

				quadList
			}

		}
	}

}
