package com.temportalist.compression.client

import com.temportalist.compression.client.model.ModelCompressed
import com.temportalist.compression.common.ProxyCommon
import com.temportalist.compression.common.init.CBlocks
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.{ModelLoader, ISmartBlockModel}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 *
 *
 * @author TheTemportalist 2/6/15
 */
class ProxyClient extends ProxyCommon {

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

	def compressedBlock: ModelResourceLocation = new ModelResourceLocation(CBlocks.compressed.getCompoundName())
	def compressedItem: ModelResourceLocation = new ModelResourceLocation(CBlocks.compressed.getCompoundName(), "inventory")

	override def registerRender(): Unit = {

		ModelLoader.setCustomModelResourceLocation(
			Item.getItemFromBlock(CBlocks.compressed), 0,
			compressedItem
		)
		ModelLoader.setCustomStateMapper(CBlocks.compressed, new StateMapperBase {
			override def getModelResourceLocation(iBlockState: IBlockState): ModelResourceLocation =
				compressedBlock
		})

	}

	@SubscribeEvent
	def bake(event: ModelBakeEvent): Unit = {
		val model: ISmartBlockModel = new ModelCompressed()
		event.modelRegistry.putObject(this.compressedBlock, model)
		event.modelRegistry.putObject(this.compressedItem, model)
	}

}
