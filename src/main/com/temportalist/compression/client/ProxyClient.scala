package com.temportalist.compression.client

import com.temportalist.compression.client.gui.GuiCompressed
import com.temportalist.compression.client.model.ModelCompressed
import com.temportalist.compression.common.{Compression, ProxyCommon}
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.origin.library.client.utility.Rendering
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.event.{ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 *
 *
 * @author TheTemportalist 2/6/15
 */
class ProxyClient extends ProxyCommon {

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == 0) {
			return new GuiCompressed(player)
		}
		null
	}

	// todo these are temportary and should be automated somehow by origin
	def compressedBlock: ModelResourceLocation = new ModelResourceLocation(CBlocks.compressed.getCompoundName(), "normal")
	def compressedItem: ModelResourceLocation = new ModelResourceLocation(CBlocks.compressed.getCompoundName(), "inventory")

	override def registerRender(): Unit = {

		// Register the item & metadata to a model location
		// same effect of IRenderingObject.registerRendering
		ModelLoader.setCustomModelResourceLocation(
			Item.getItemFromBlock(CBlocks.compressed), 0, this.compressedItem
		)
		// Register a custom state for a block (which redirects to the model location)
		ModelLoader.setCustomStateMapper(CBlocks.compressed, new StateMapperBase {
			override def getModelResourceLocation(
					iBlockState: IBlockState): ModelResourceLocation = compressedBlock
		})

	}

	@SubscribeEvent
	def bake(event: ModelBakeEvent): Unit = {
		// make the model to both the block and item forms
		val model = new ModelCompressed()
		event.modelRegistry.putObject(this.compressedBlock, model)
		event.modelRegistry.putObject(this.compressedItem, model)
	}

	@SubscribeEvent
	def pre_Sprites(event: TextureStitchEvent.Pre): Unit = {
		// register custom sprites which are not mapped to a specific block
		if (event.map == Rendering.mc.getTextureMapBlocks)
			for (i <- 1 to 18)
				event.map.registerSprite(new ResourceLocation(Compression.MODID, "blocks/overlay_" + i))
	}

}
