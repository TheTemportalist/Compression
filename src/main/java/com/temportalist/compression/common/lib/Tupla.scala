package com.temportalist.compression.common.lib

import com.temportalist.origin.library.client.utility.Rendering
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist 2/7/15
 */
object Tupla {

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

	def getMaxTier(): Int = this.caps.length - 1

	def getMaxCap(tier: Int): Long = this.caps(tier)

	def getMaxCap(): Long = this.getMaxCap(this.getMaxTier())

	def getTierFromSize(size: Long): Int = {
		if (size <= 1 || size > this.getMaxCap()) return -1
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

	def canHold(compressed: ItemStack, size: Long): Boolean = {
		compressed.getTagCompound.getLong("stackSize") + size <= Tupla.getMaxCap()
	}

	def absorb(compressed: ItemStack, toAbsorb: ItemStack): Unit = {
		compressed.getTagCompound.setLong("stackSize",
			compressed.getTagCompound.getLong("stackSize") + toAbsorb.stackSize
		)
		toAbsorb.stackSize = 0
	}

}
