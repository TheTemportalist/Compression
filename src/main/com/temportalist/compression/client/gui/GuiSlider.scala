package com.temportalist.compression.client.gui

import java.math.BigDecimal

import com.temportalist.origin.library.client.utility.Rendering
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiSlider(id: Int, x: Int, y: Int, w: Int, h: Int, display: String)
		extends GuiButton(id, x, y, w, h, display) {

	private var currentValue: Double = 0d
	var dragging: Boolean = false
	private final val min: Double = 0d
	private final val max: Double = 1d
	var minRealValue: Double = 0d
	var maxRealValue: Double = 1d
	this.displayString = this.display + this.getRealString()

	override def getHoverState(mouseOver: Boolean): Int = 0

	override def mouseDragged(mc: Minecraft, mouseX: Int, mouseY: Int): Unit = {
		if (this.visible) {
			if (this.dragging) {
				this.currentValue = MathHelper.clamp_double(
					(mouseX - (this.xPosition + 4)).asInstanceOf[Double] /
							(this.width - 8).asInstanceOf[Double],
					this.min, this.max
				)
				this.displayString = this.display + this.getRealString()
			}

			Rendering.bindResource(GuiButton.buttonTextures)
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
			this.drawTexturedModalRect(
				this.xPosition +
						(this.currentValue * (this.width - 8).asInstanceOf[Double])
								.asInstanceOf[Int],
				this.yPosition, 0, 66, 4, 20
			)
			this.drawTexturedModalRect(
				this.xPosition + (this.currentValue * (this.width - 8).asInstanceOf[Double])
						.asInstanceOf[Int] + 4,
				this.yPosition, 196, 66, 4, 20
			)
		}
	}

	override def mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean = {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			this.currentValue = MathHelper.clamp_double(
				(mouseX - (this.xPosition + 4)).asInstanceOf[Double] /
						(this.width - 8).asInstanceOf[Double],
				this.min, this.max
			)
			this.displayString = this.display + this.getRealString()
			this.dragging = true
			true
		}
		else false
	}

	override def mouseReleased(mouseX: Int, mouseY: Int): Unit = this.dragging = false

	private def getRatio(): Double = this.currentValue / this.max

	def getRealValue(): Double = {
		// get the possible range from 0 to X which the real value can span
		// maxReal - minReal
		// multiply by ratio (current / max)
		// add minReal value to scale back to proper
		Math.floor(
			(this.maxRealValue - this.minRealValue) * this.getRatio() + this.minRealValue
		)
	}

	def getRealString(): String = {
		new BigDecimal(this.getRealValue()).toPlainString
	}

	def setCurrentReal(value: Double): Unit = {
		this.currentValue = value / this.maxRealValue * this.max
		this.displayString = this.display + this.getRealString()
	}

}
