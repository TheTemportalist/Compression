package temportalist.compression.main.common

import temportalist.compression.main.common.init.TabCompressed
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

	// compress like-items on player inventory entry
	var compressor: Int = 2
	// attract like-items when in player inventory
	var magnetI: Int = 3
	// attract like-items in world
	var attractionI: Int = 3
	// attract all items when in player inventory
	var magnetII: Int = 4
	// attract all items in world
	var attractionII: Int = 4
	// attract all entities when in world as entity
	var attractionIII: Int = 6
	// eat world when in world as block
	var blackHole: Int = 18
	// enable potential hearts from consumed energy
	var blackHolePotentialEnergy: Boolean = true

	var showTab = false
	var blackList = Array[String]()

	override def register(): Unit = {

		val compressed = "compressed effects"
		this.compressor = this.getAndComment(compressed, "Compressor",
			"Compress like-items as they enter the player's inventory when in the player's inventory. (-1 to disable)",
			this.compressor)
		this.magnetI = this.getAndComment(compressed, "Magnet I",
			"Attract like-items to the player when in the player's inventory. (-1 to disable)",
			this.magnetI)
		this.attractionI = this.getAndComment(compressed, "Attraction I",
			"Attract like-items to the entity when in the world as an entity. (-1 to disable)",
			this.attractionI)
		this.magnetII = this.getAndComment(compressed, "Magnet II",
			"Attract all items to the player when in the player's inventory. (-1 to disable)",
			this.magnetII)
		this.attractionII = this.getAndComment(compressed, "Attraction II",
			"Attract all items to the entity when in the world as an entity. (-1 to disable)",
			this.attractionII)
		this.attractionIII = this.getAndComment(compressed, "Attraction III",
			"Attract all entities to the entity when in the world as an entity. (-1 to disable)",
			this.attractionIII)
		this.blackHole = this.getAndComment(compressed, "Black Hole",
			"Destroy blocks and attract items/entities when in the world as a block. (-1 to disable)",
			this.blackHole)
		this.blackHolePotentialEnergy = this.getAndComment(compressed, "Potential Energy",
			"When a blackhole-tier block consumes matter, create potential energy for extral health",
			this.blackHolePotentialEnergy)

		this.showTab = this.getAndComment("general", "show tab",
			"Show the tab for all compressed (18 versions) of all compressable blocks and items",
			this.showTab
		)
		TabCompressed.createTab()

		this.blackList = this.getAndComment("general", "blacklist",
			"Add item names here which should NOT be compressed. " +
					"Format MUST by either \'modid:name\' or \'modid:name:metadata\'.",
			this.blackList)

	}

}
