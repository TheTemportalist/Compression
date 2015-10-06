package com.temportalist.compression.client

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.container.ContainerCompressor
import com.temportalist.origin.api.client.gui.GuiContainerBase

/**
 * Created by TheTemportalist on 9/8/2015.
 */
class GuiCompressor(c: ContainerCompressor) extends GuiContainerBase(c) {

	this.setupGui("Compressor", Compression.getResource("compressor"))

}
