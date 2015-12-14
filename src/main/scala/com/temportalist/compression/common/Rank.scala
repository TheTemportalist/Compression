package com.temportalist.compression.common

import com.temportalist.compression.common.entity.EntityItemCompressed
import com.temportalist.compression.common.item.ICompressed
import com.temportalist.origin.api.common.lib.{Crash, V3O}
import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.api.common.utility.Scala
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.event.entity.player.EntityItemPickupEvent

import scala.collection.mutable.ListBuffer

/**
 * Created by TheTemportalist on 12/9/2015.
 */
object Rank {

	private val names = Array[String](
		"Null",
		"Single", "Double", "Triple", "Quadruple", "Quintuple",
		"Hextuple", "Septuple", "Octuple", "Nonuple", "Decuple",
		"Undecuple", "Duodecuple", "Tredecuple", "Quattuordecuple", "Quindecouple",
		"Sedecouple", "Septendecouple", "Duodevdecouple"
	)
	private val caps = Array[Long](
		1L,
		9L, 81L, 729L, 6561L, 59049L,
		531441L, 4782969L, 43046721L, 387420489L, 3486784401L,
		31381059609L, 282429536481L, 2541865828329L, 22876792454961L, 205891132094649L,
		1853020188851841L, 16677181699666570L, 150094635296999136L
	)
	private val ranks = ListBuffer[Rank]()

	/**
	 * /dev/null functionality
	 */
	var absorbalof: Int = 3
	/**
	 * while item is an entity, sucks in other entity items within a growing radius
	 */
	var atractor: Int = 4
	/**
	 * while item is inventory, sucks in other entity items within a growing radius
	 */
	var magnet: Int = 5
	/**
	 * sucks in items and entities when in world
	 */
	var blackHole: Int = 9

	def loadRanks(): Unit = {
		if (this.names.length != this.caps.length) {
			val className = classOf[Rank].getCanonicalName
			new Crash(Compression.MODNAME, "Length of " + className +
					".caps does not equal length of " + className + ".names", "")
			return
		}
		for (i <- this.names.indices) {
			this.ranks += new Rank(i, this.names(i),
				if (i > 0) this.caps(i - 1) + 1 else 0, this.caps(i))
		}
		Registry.registerHandler(this)
	}

	def getHighestRank: Rank = this.ranks.last

	def getRank(stack: ItemStack): Rank = {
		if (stack != null && CompressedStack.isCompressedStack(stack))
			this.getRank(CompressedStack.getCompressedSize(stack))
		else this.ranks.head
	}

	def getRank(size: Long): Rank = {
		var highestRank: Rank = null
		this.ranks.foreach(rank => {
			if (highestRank == null || (rank.minimum <= size && size <= rank.maximum))
				highestRank = rank
		})
		highestRank
	}

	def indexOf(i: Int): Rank = this.ranks(i)

	@SubscribeEvent
	def onItemPickUp(event: EntityItemPickupEvent): Unit = {
		val player = event.entityPlayer
		val entStack = event.item.getEntityItem
		if (player == null || entStack == null) return

		// iterate over the inventory
		Scala.foreach(player.inventory, (slot: Int, stack: ItemStack) => {
			// find compressed stacks
			if (stack != null) stack.getItem match {
				case compressed: ICompressed =>
					// try pickup effects
					if (this.getRank(stack).onPickupItem(event, player, stack.copy(), entStack)) {
						// if worked, cancel event & exit loop
						event.setCanceled(true)
						event.item.setDead()
						return
					}
			}
		})

	}

}

class Rank(private val index: Int, private val name: String,
		private val minimum: Long, private val maximum: Long) {

	def onPickupItem(event: EntityItemPickupEvent,
			player: EntityPlayer, compressedStack: ItemStack, entityStack: ItemStack): Boolean = {
		// make sure this rank can do effect
		if (this.index < Rank.absorbalof) return false

		// if the type of the stack in slot and the picked up stack
		if (CompressedStack.doStackTypesMatch(compressedStack, entityStack)) {
			// pull all stacks of type in inventory together (not including hotbar stacks)
			val totalOfTypeInInventory =
				CompressedStack.removeAllOfType(player, compressedStack, false, entityStack)
			// splits into stacks divisible by 9
			val list = CompressedStack.divideIntoClassicCompressions(
				compressedStack, totalOfTypeInInventory)
			list.foreach(stack => Temp.addToInventoryWithDrop(player, stack))
			return true
		}

		false
	}

	def inWorldTick(entity: EntityItemCompressed, stack: ItemStack): Unit = {
		if (this.index < Rank.atractor) return
		Temp.tryToPullCloser(this.index - Rank.atractor,
			entity,
			entity.boundingBox, entity.worldObj, new V3O(entity),
			new V3O(entity.motionX, entity.motionY, entity.motionZ),
			(otherEntity: Entity) => {
				if (this.index >= Rank.blackHole) true
				else CompressedStack.shouldAttractEntity(entity, otherEntity)
			},
			(otherEntity: Entity) => CompressedStack.onAttraction(entity, otherEntity)
		)
	}

	def onInventoryTick(stack: ItemStack, player: EntityPlayer): Unit = {
		if (this.index < Rank.magnet) return
		Temp.tryToPullCloser(this.index - Rank.magnet, player,
			player.boundingBox.expand(1, 0.5D, 1), player.getEntityWorld, new V3O(player),
			new V3O(player.motionX, player.motionY, player.motionZ),
			(entity: Entity) => entity.isInstanceOf[EntityItem],
			(entity: Entity) => {
				if (player.inventory.addItemStackToInventory(
					entity.asInstanceOf[EntityItem].getEntityItem))
					entity.setDead()
			}
		)
	}

	def -=(amt: Int): Rank = {
		if (this.index - amt >= 0) Rank.ranks(this.index - amt)
		else Rank.ranks.head
	}

	def +=(amt: Int): Rank = {
		if (this.index + amt < Rank.ranks.size - 1) Rank.ranks(this.index + amt)
		else Rank.ranks.last
	}

	def getIndex: Int = this.index

	def getMaximum: Long = this.maximum

	def getMinimum: Long = this.minimum

	def getName: String = this.name

}
