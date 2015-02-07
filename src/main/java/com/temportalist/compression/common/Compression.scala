package com.temportalist.compression.common

import com.temportalist.origin.wrapper.common.{ProxyWrapper, ModWrapper}
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.{SidedProxy, Mod}

/**
 *
 *
 * @author TheTemportalist 2/6/15
 */
@Mod(modid = Compression.MODID, name = Compression.MODNAME, version = "@PLUGIN_VERSION@",
	modLanguage = "scala",
	guiFactory = Compression.clientProxy,
	dependencies = "required-after:origin@[4,);"
)
object Compression extends ModWrapper {

	final val MODID = "@MODID@"
	final val MODNAME = "@MODNAME@"
	final val clientProxy = "com.temportalist.compression.client.ProxyClient"
	final val serverProxy = "com.temportalist.compression.server.ProxyServer"

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: ProxyWrapper = null

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this.MODID, this.MODNAME, event, this.proxy)

	}

}
