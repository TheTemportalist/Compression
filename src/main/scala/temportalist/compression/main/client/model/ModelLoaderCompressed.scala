package temportalist.compression.main.client.model

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.{ICustomModelLoader, IModel}
import temportalist.compression.main.common.Compression

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class ModelLoaderCompressed extends ICustomModelLoader {

	override def accepts(modelLocation: ResourceLocation): Boolean = {
		modelLocation.equals(ModelLoaderCompressed.fakeRL)
	}

	override def loadModel(modelLocation: ResourceLocation): IModel = {
		new ModelCompressed
	}

	override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {}

}
object ModelLoaderCompressed {

	val fakeRL = new ModelResourceLocation(Compression.getModId, "models/fake")

}
