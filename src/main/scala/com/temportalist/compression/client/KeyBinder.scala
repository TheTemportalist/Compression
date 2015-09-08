package com.temportalist.compression.client

import com.temportalist.compression.common.Compression
import com.temportalist.compression.common.packets.PacketDropFullStack
import com.temportalist.origin.api.common.resource.IModDetails
import com.temportalist.origin.foundation.client.{EnumKeyCategory, IKeyBinder}
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

/**
 * Created by TheTemportalist on 9/7/2015.
 */
object KeyBinder extends IKeyBinder {

	override def getMod: IModDetails = Compression

	val dropFullStack = this.makeKeyBinding(
		"dropFullStack", Keyboard.KEY_Q, EnumKeyCategory.GAMEPLAY)

	override def onKeyPressed(keyBinding: KeyBinding): Unit = {
		keyBinding match {
			case this.dropFullStack => new PacketDropFullStack().sendToServer()
			case _ =>
		}
	}

}
