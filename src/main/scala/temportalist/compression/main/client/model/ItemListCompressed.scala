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
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.world.World
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.init.Compressed

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class ItemListCompressed extends ItemOverrideList(Lists.newArrayList()) {

	def getMissingModel: IBakedModel =
		Minecraft.getMinecraft.getBlockRendererDispatcher.getBlockModelShapes.getModelManager.getMissingModel

	override def handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World,
			entity: EntityLivingBase): IBakedModel = {
		if (!stack.hasTagCompound) return this.getMissingModel

		val sampleStack = Compressed.createSampleStack(stack)
		val isBlock = sampleStack.getItem.isInstanceOf[ItemBlock]

		val sampleModel =
			if (isBlock)
				Minecraft.getMinecraft.getBlockRendererDispatcher.getBlockModelShapes.
						getModelForState(Compressed.getSampleState(stack))
			else
				Minecraft.getMinecraft.getRenderItem.getItemModelMesher.getItemModel(sampleStack)

		val size = Compressed.getSize(stack)
		val i = 0

		new IBakedModel {

			override def getParticleTexture: TextureAtlasSprite =
				Minecraft.getMinecraft.getTextureMapBlocks.getMissingSprite

			override def isBuiltInRenderer: Boolean = false

			override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

			override def isAmbientOcclusion: Boolean = true

			override def isGui3d: Boolean = isBlock

			override def getOverrides: ItemOverrideList = sampleModel.getOverrides

			override def getQuads(state: IBlockState,
					side: EnumFacing,
					rand: Long): util.List[BakedQuad] = {
				val quadList = new util.ArrayList[BakedQuad]()
				quadList.addAll(sampleModel.getQuads(state, side, rand))

				val textureLocation = new ResourceLocation(Compression.getModId, "textures/overlays/overlay_" + i + ".png")
				Compression.log(textureLocation.toString)

				val texture = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(textureLocation.toString)

				//Compression.log(if (texture == null) "NULL TEXTURE AT " + textureLocation else texture.toString)

				val overlayModel = new Builder(state, sampleModel, texture, BlockPos.ORIGIN).makeBakedModel()
				quadList.addAll(overlayModel.getQuads(state, side, rand))

				quadList
			}

		}
	}

}
