package temportalist.compression.main.common

import temportalist.origin.foundation.common.registers.OptionRegister

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
object Options extends OptionRegister {

	/**
	  * If [[hasDefaultConfig]] returns true, this is used to determine the config's file extension
	  *
	  * @return The extension for the file. 'cfg' and 'json' are supported.
	  */
	override def getExtension: String = "json"

	override def register(): Unit = {

	}

}
