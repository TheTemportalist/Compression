package com.temportalist.compression.client

import com.temportalist.compression.common.Compression
import com.temportalist.origin.foundation.client.gui.GuiConfigBase
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.GuiScreen

/**
 * Created by TheTemportalist on 10/9/2015.
 */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen) extends GuiConfigBase(
	guiScreen, Compression, Compression.getModid) {}
