package com.temportalist.compression.common.lib

import com.temportalist.origin.library.client.utility.Rendering
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
object Tupla {

	// todo to origin
	val ITEMSTACK: IUnlistedProperty[ItemStack] = new IUnlistedProperty[ItemStack] {
		override def getType: Class[ItemStack] = classOf[ItemStack]

		override def getName: String = "ItemStack"

		override def valueToString(value: ItemStack): String = value.getDisplayName

		override def isValid(value: ItemStack): Boolean = true
	}

	// todo to origin
	def isBlock(item: Item): Boolean = Block.getBlockFromItem(item) != null

	// todo to origin
	def toState(stack: ItemStack): IBlockState = {
		if (this.isBlock(stack.getItem))
			Block.getBlockFromItem(stack.getItem).getStateFromMeta(stack.getMetadata)
		else null
	}

	// todo to origin
	def getModel(stack: ItemStack): IBakedModel = {
		if (this.isBlock(stack.getItem))
			Rendering.blockShapes.getModelForState(this.toState(stack))
		else
			Rendering.itemMesher.getItemModel(stack)
	}

	/* http://blogs.transparent.com/latin/latin-numbers-1-100/ */
	val tiers: Array[String] = Array[String](
		"Null",
		"Single", "Double", "Triple", "Quadruple", "Quintuple",
		"Hextuple", "Septuple", "Octuple", "Nonuple", "Decuple",
		"Undecuple", "Duodecuple", "Tredecuple", "Quattuordecuple", "Quindecouple",
		"Sedecouple", "Septendecouple", "Duodevdecouple"
	)
	val caps: Array[Long] = Array[Long](
		1L,
		9L, 81L, 729L, 6561L, 59049L,
		531441L, 4782969L, 43046721L, 387420489L, 3486784401L,
		31381059609L, 282429536481L, 2541865828329L, 22876792454961L, 205891132094649L,
		1853020188851841L, 16677181699666570L, 150094635296999136L
	)
	/*
	for (i <- 0 until 100) {
        print (new java.math.BigDecimal(Math.pow(9, i)).toPlainString())
        println ("D, ")
	}
	*/

	def getTierFromSize(size: Long): Int = {
		if (size <= 1 || size > this.caps(this.caps.length - 1)) return -1
		var tier: Int = 0
		while (size > this.caps(tier)) {
			tier += 1
		}
		tier
	}

	def getName(size: Long): String = {
		val tier: Int = this.getTierFromSize(size)
		if (tier >= 0)
			this.tiers(tier)
		else "Un"
	}

	@SideOnly(Side.CLIENT)
	def getSprite(size: Long): TextureAtlasSprite = {
		Rendering.mc.getTextureMapBlocks.getAtlasSprite(
			"compression:blocks/overlay_" + this.getTierFromSize(size)
		)
	}

}
