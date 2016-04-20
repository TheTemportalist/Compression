package temportalist.compression.main.common.init

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.block.BlockCompressed
import temportalist.compression.main.common.block.tile.{TileCompressed, TileCompressedTickable}
import temportalist.compression.main.common.item.ItemBlockCompressed
import temportalist.compression.main.common.recipe.Recipes
import temportalist.origin.foundation.common.registers.BlockRegister

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
object ModBlocks extends BlockRegister {

	var block: BlockCompressed = _
	var blockItem: ItemBlockCompressed = _

	/**
	  * This method is used to register TileEntities.
	  * Recommendation: Use GameRegistry.registerTileEntity
	  */
	override def registerTileEntities(): Unit = {
		this.register("Compressed", classOf[TileCompressed])
		this.register("CompressedTicker", classOf[TileCompressedTickable])
	}

	override def register(): Unit = {
		this.block = new BlockCompressed

	}

	/**
	  * This method is used to register crafting recipes
	  */
	override def registerCrafting(): Unit = {

		Compression.log("Loading compressed recipes for Blocks...")

		for (any <- JavaConversions.asScalaIterator(Block.REGISTRY.iterator())) {
			val state = any.getDefaultState
			if (this.canCompressBlock(state)) {
				Recipes.tryAddRecipes(new ItemStack(any, 1, any.getMetaFromState(state)))
			}
		}

	}

	private def canCompressBlock(state: IBlockState): Boolean = {
		val block = state.getBlock
		state.isFullCube && state.isOpaqueCube && state.isBlockNormalCube &&
			Item.getItemFromBlock(block) != null && !block.hasTileEntity(state)
	}

}
