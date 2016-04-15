package temportalist.compression.main.client.model

import java.util

import com.google.common.base.Function
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.{IModelState, TRSRTransformation}
import temportalist.compression.main.common.Compression

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class ModelCompressed extends IModel {

	override def getDependencies: util.Collection[ResourceLocation] = {
		util.Arrays.asList()
	}

	override def getTextures: util.Collection[ResourceLocation] = {
		val overlays = new util.ArrayList[ResourceLocation]()
		for (i <- 1 to 18) {
			overlays.add(new ResourceLocation(Compression.getModId, "overlays/overlay_" + i))
		}
		overlays
	}

	override def bake(state: IModelState, format: VertexFormat,
			bakedTextureGetter: Function[ResourceLocation, TextureAtlasSprite]): IBakedModel = {
		val overlayList = new Array[IBakedModel](18)
		for (i <- overlayList.indices) {
			val sprite = bakedTextureGetter.apply(new ResourceLocation(
				Compression.getModId, "overlays/overlay_" + (i + 1)))
			
		}


		new BakedCompressed
	}

	override def getDefaultState: IModelState = TRSRTransformation.identity()

}
