package temportalist.compression.main.jei

import mezz.jei.api.{BlankModPlugin, IModRegistry, JEIPlugin}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 4/22/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
@JEIPlugin
class JEICompression extends BlankModPlugin {

	override def register(registry: IModRegistry): Unit = {

		//registry.addRecipeCategories(CategoryCompress)
		registry.addRecipeHandlers(compress.classic.Handler)
		registry.addRecipeHandlers(decompress.classic.Handler)

		registry.addRecipes(JavaConversions.bufferAsJavaList(compress.classic.Maker.getRecipes(registry)))
		registry.addRecipes(JavaConversions.bufferAsJavaList(decompress.classic.Maker.getRecipes(registry)))

	}

}
