package com.temportalist.compression.client

import com.temportalist.compression.common.container.ContainerCompression
import com.temportalist.compression.common.init.CBlocks
import com.temportalist.compression.common.packets.PacketUpdateHeldSize
import com.temportalist.compression.common.{Compression, Tiers}
import com.temportalist.origin.api.client.gui.GuiContainerBase
import com.temportalist.origin.foundation.client.gui.{ArrowButtonType, GuiButtonArrow}
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author  TheTemportalist  6/23/15
 */
@Deprecated
class GuiCompression(p: EntityPlayer) extends GuiContainerBase(new ContainerCompression(p)) {

	private val isCreative: Boolean = p.capabilities.isCreativeMode
	this.setupGui("", Compression.getResource("gui_" +
			(if (p.capabilities.isCreativeMode) "creative" else "survival")))
	private var tier: Int = CBlocks.getStackTier(p.getHeldItem)
	private var leftTier: GuiButtonArrow = null
	private var rightTier: GuiButtonArrow = null
	private var slider: GuiSlider = null
	private var save: GuiButton = null

	override def initGui(): Unit = {
		super.initGui()

		if (!this.isCreative) return

		val cx = this.getCenterX()
		val cy = this.getCenterY()
		this.leftTier = new GuiButtonArrow(0, cx + 6, cy - 66, ArrowButtonType.LEFT)
		this.addButton(this.leftTier)
		this.rightTier = new GuiButtonArrow(1, cx + 32, cy - 66, ArrowButtonType.RIGHT)
		this.addButton(this.rightTier)
		this.slider = new GuiSlider(2, cx + 18 - 50, cy - 40, 100, 20, "")
		this.updateSlider()
		this.addButton(this.slider)
		this.save = new GuiButton(3, cx + 54, cy - 79, 30, 20, "Save")
		this.addButton(this.save)

	}

	override def actionPerformed(button: GuiButton): Unit = {
		button.id match {
			case 0 =>
				if (this.tier > 1) this.tier -= 1
				this.updateSlider()
			case 1 =>
				if (this.tier < 17) this.tier += 1
				this.updateSlider()
			case 3 =>
				this.updateStackWithCreativeStats()
			case _ =>
		}
	}

	private def updateSlider(): Unit = {
		this.slider.minRealValue = Tiers.getMaxCap(this.tier - 1) + 1
		this.slider.maxRealValue = Tiers.getMaxCap(this.tier)
		this.slider.setCurrentReal((this.slider.minRealValue + this.slider.maxRealValue) / 2)
	}

	private def updateStackWithCreativeStats(): Unit = {
		new PacketUpdateHeldSize(this.slider.getRealValue.toLong).sendToBoth()
	}

	override protected def drawGuiForegroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		super.drawGuiForegroundLayer(mouseX, mouseY, renderPartialTicks)

		if (this.isCreative) {
			val tierStr = this.tier + ""
			val tierStrSize = this.getStringWidth(tierStr)
			this.drawString(tierStr, 112 - (tierStrSize / 2), 22)
		}
	}

}
