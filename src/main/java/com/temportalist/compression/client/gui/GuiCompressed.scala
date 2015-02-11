package com.temportalist.compression.client.gui

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.container.ContainerCompressed
import com.temportalist.compression.common.lib.Tupla
import com.temportalist.compression.common.network.PacketUpdateCompressed
import com.temportalist.origin.library.common.nethandler.PacketHandler
import com.temportalist.origin.wrapper.client.gui.GuiContainerWrapper
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiCompressed(p: EntityPlayer) extends GuiContainerWrapper(new ContainerCompressed(p)) {

	val currentStack: ItemStack = p.getCurrentEquippedItem
	val currentStackIndex: Int = p.inventory.currentItem
	var tierSlider: GuiSlider = null
	var quantitySlider: GuiSlider = null

	override def initGui(): Unit = {
		super.initGui()

		val centerX: Int = (this.getX() + this.width) / 2
		val guiY: Int = this.getY()

		this.tierSlider = new GuiSlider(0,
			centerX - 100, guiY + 10, this.getWidth(), 20, "Tier: "
		)
		this.addButton(this.tierSlider)
		this.quantitySlider = new GuiSlider(1,
			centerX - 100, guiY + 40, this.getWidth(), 20, "Quantity: "
		)
		this.addButton(this.quantitySlider)

		val currentSize: Long = this.currentStack.getTagCompound.getLong("stackSize")
		val tier: Int = Tupla.getTierFromSize(currentSize)

		this.tierSlider.minRealValue = 1
		if (this.p.capabilities.isCreativeMode) {
			this.tierSlider.maxRealValue = Tupla.getMaxTier()
		}
		else {
			this.tierSlider.maxRealValue = tier
		}
		this.tierSlider.setCurrentReal(tier)
		this.updateQuantityBounds()
		this.quantitySlider.setCurrentReal(currentSize)

	}

	private def updateQuantityBounds(): Unit = {
		val tier: Int = this.tierSlider.getRealValue().toInt
		val min: Long = if (tier > 1) Tupla.caps(tier - 1) else 2l
		val max: Long = Tupla.caps(tier)
		this.quantitySlider.minRealValue = min
		this.quantitySlider.maxRealValue = max
		//this.quantitySlider.setCurrentReal((min + max) / 2D)
	}

	override def mouseReleased(mouseX: Int, mouseY: Int, state: Int): Unit = {
		val isTier: Boolean = this.tierSlider != null && this.tierSlider.dragging
		super.mouseReleased(mouseX, mouseY, state)
		if (isTier) {
			val tier: Int = this.tierSlider.getRealValue().toInt
			val min: Long = Tupla.caps(tier - 1) + 1
			this.quantitySlider.minRealValue = min
			this.quantitySlider.maxRealValue = Tupla.caps(tier)
			this.quantitySlider.setCurrentReal(min)
		}
	}

	override def actionPerformed(button: GuiButton): Unit = {

	}

	override def onGuiClosed(): Unit = {
		super.onGuiClosed()
		PacketHandler.sendToServer(Compression.MODID, new PacketUpdateCompressed(
			this.currentStackIndex, this.quantitySlider.getRealValue().toLong
		))
	}


	// todo to origin
	def getCenterX(): Int = (this.getX() + this.width) / 2

	// todo to origin
	def getCenterY(): Int = (this.getY() + this.height) / 2

}
