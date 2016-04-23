package temportalist.compression.main.common

import java.util

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{ResourceLocation, SoundCategory, SoundEvent}
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.item.ItemDenseArmor
import temportalist.compression.main.common.lib.EnumTier
import temportalist.origin.api.common.lib.Vect

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

/**
  *
  * Created by TheTemportalist on 4/15/2016.
  *
  * @author TheTemportalist
  */
object Effects {

	def shouldUseTickingTile(itemStack: ItemStack): Boolean = {
		this.canUseBlackHole(itemStack)
	}

	val portalSound = new ResourceLocation("entity.endermen.teleport")

	def getTierNumber(itemStack: ItemStack): Int = Compressed.getTier(itemStack).ordinal()

	def canUse(itemStack: ItemStack, tierOrdinal: Int): Boolean =
		this.getTierNumber(itemStack) >= tierOrdinal - 1

	def canUseCompressor(itemStack: ItemStack): Boolean = this.canUse(itemStack, Options.compressor)

	def canUseMagnetI(itemStack: ItemStack): Boolean = this.canUse(itemStack, Options.magnetI)

	def canUseAttractionI(itemStack: ItemStack): Boolean = this.canUse(itemStack, Options.attractionI)

	def canUseMagnetII(itemStack: ItemStack): Boolean = this.canUse(itemStack, Options.magnetII)

	def canUseAttractionII(itemStack: ItemStack): Boolean = this.canUse(itemStack, Options.attractionII)

	def canUseAttractionIII(itemStack: ItemStack): Boolean = this.canUse(itemStack, Options.attractionIII)

	def canUseBlackHole(itemStack: ItemStack): Boolean = this.canUse(itemStack, Options.blackHole)

	def canUseAny(itemStack: ItemStack, set: Int*): Boolean = {
		set.foreach(tierOrdinalPlus => if (this.canUse(itemStack, tierOrdinalPlus)) return true)
		false
	}

	def getLowestTier(tiers: Int*): Int = {
		var lowest: Int = -1
		tiers.foreach(tier => if (tier >= 0 && (lowest < 0 || tier < lowest)) lowest = tier)
		lowest
	}

	def getLowestTierMagnet: Int = this.getLowestTier(Options.magnetI, Options.magnetI)

	def iterateOverPlayerInventoryForSample(player: EntityPlayer, sample: ItemStack,
			onSampleFound: (Int, ItemStack, Boolean) => Boolean, min: Int = 9, max: Int = 36): Unit = {
		breakable {
			for (index <- min until max) {
				// just the storage part
				val stackInSlot = player.inventory.getStackInSlot(index)
				if (stackInSlot != null) {
					val slotSample = Compressed.getSampleFromUnknown(stackInSlot)
					if (slotSample.getItem == sample.getItem &&
							slotSample.getItemDamage == sample.getItemDamage) {
						if (onSampleFound(index, stackInSlot, Compressed.isCompressed(stackInSlot)))
							break()
					}
				}
			}
		}
	}

	def appendToInventory(player: EntityPlayer, stack: ItemStack, min: Int = 9, max: Int = 36): Boolean = {
		for (index <- min until max) {
			val stackInSlot = player.inventory.getStackInSlot(index)
			if (stackInSlot == null) {
				player.inventory.setInventorySlotContents(index, stack)
				return true
			}
		}
		false
	}

	@SubscribeEvent
	def onItemPickup(event: EntityItemPickupEvent): Unit = {
		val player = event.getEntityPlayer
		val entityItem = event.getItem
		if (player == null || entityItem == null) return

		val entityStack = entityItem.getEntityItem
		var entityStackSample: ItemStack = null
		var entityStackSize: Long = entityStack.stackSize
		if (Compressed.isCompressed(entityStack)) {
			entityStackSample = Compressed.getSampleStack(entityStack)
			entityStackSize *= Compressed.getSize(entityStack)
		}
		else {
			entityStackSample = entityStack.copy()
			entityStackSample.stackSize = 1
		}

		var indexCompressor: Int = -1
		var stackCompressor: ItemStack = null
		this.iterateOverPlayerInventoryForSample(player, entityStackSample,
			(index: Int, stack: ItemStack, isCompressed: Boolean) => {
				if (isCompressed && this.canUseCompressor(stack)) {
					indexCompressor = index
					stackCompressor = stack
					true
				}
				else false
			}: Boolean, min = 0, max = 36 + 4 + 1 // 36 = main, 4 = armor, 1 = offhand
		)

		if (indexCompressor >= 0) {
			event.setCanceled(true)
			event.getItem.setDead()
			this.addToAndCompressInventory(player, entityStackSize, entityStackSample)
			player.inventory.markDirty()
		}

	}

	private def addToAndCompressInventory(player: EntityPlayer,
			excessSize: Long, sample: ItemStack): Unit = {
		var size: Long = excessSize

		this.iterateOverPlayerInventoryForSample(player, sample,
			(index: Int, stack: ItemStack, isCompressed: Boolean) => {
				size += stack.stackSize * (if (isCompressed) Compressed.getSize(stack) else 1)
				player.inventory.removeStackFromSlot(index)
				false
			}: Boolean
		)

		val stackList = this.getStackListFromSize(size, sample)

		for (stack <- stackList) {
			if (!this.appendToInventory(player, stack))
				player.dropItem(stack, false, false)
		}

	}

	def getStackListFromSize(sizeIn: Long, sample: ItemStack): ListBuffer[ItemStack] = {
		var size = sizeIn

		val sizeMap = new util.EnumMap[EnumTier, Int](classOf[EnumTier])
		def append(tier: EnumTier, quantity: Int): Unit =
			sizeMap.put(tier, quantity + (if (sizeMap.containsKey(tier)) sizeMap.get(tier) else 0))

		while (size > EnumTier.getTail.getSizeMax) {
			append(EnumTier.getTail, 1)
			size -= EnumTier.getTail.getSizeMax
		}

		while (size > 8) {
			var tier = EnumTier.getTierForSize(size)
			if (size - tier.getSizeMax < 0)
				tier = EnumTier.getTier(tier.ordinal() - 1)
			append(tier, 1)
			size -= tier.getSizeMax
		}

		val stackList = ListBuffer[ItemStack]()
		for (entry <- JavaConversions.asScalaSet(sizeMap.entrySet())) {
			val stack = Compressed.create(sample, tier = entry.getKey)
			stack.stackSize = entry.getValue
			stackList += stack
		}
		if (size > 0) {
			val stack = sample.copy()
			stack.stackSize = size.toInt
			stackList += stack
		}

		stackList
	}

	def onInvUpdateCompressed(world: World, player: EntityPlayer, stack: ItemStack): Unit = {
		this.onInvUpdateCompressed_Magnet(world, player, stack)
	}

	def onInvUpdateCompressed_Magnet(world: World, player: EntityPlayer, stack: ItemStack): Unit = {
		if (!(this.canUseMagnetI(stack) || this.canUseMagnetII(stack))) return
		val lowestTier = this.getLowestTierMagnet
		if (lowestTier < 0) return

		val stackTierNumber = Compressed.getTier(stack).ordinal() + 1
		val stackSample = Compressed.getSampleStack(stack)

		val radiusFactor = stackTierNumber - lowestTier
		val position = new Vect(player)
		val motion = new Vect(player.motionX, player.motionY, player.motionZ)
		val boundingBoxBase = player.getEntityBoundingBox.expand(1, 0.5, 1)

		val radius = radiusFactor + 1.5D

		this.iterateEntitiesAround(boundingBoxBase, radius, world, player, position, motion,
			// Filter: Should the entity passed by pulled
			(entity: Entity) => {
				entity match {
					case entityItem: EntityItem =>
						if (this.canUseMagnetII(stack)) true
						else this.doesMatchSample(stackSample, entityItem)
					case _ => false
				}
			}: Boolean, null,
			// Post effect: When an entity is pulled
			null
		)
	}

	def onEntityUpdateCompressed(world: World, entity: EntityItem, itemStack: ItemStack): Unit = {
		if (!itemStack.hasTagCompound) return
		val tiers = Seq(Options.attractionI, Options.attractionII, Options.attractionIII)
		if (!this.canUseAny(itemStack, tiers:_*)) return
		val lowestTier = this.getLowestTier(tiers:_*)
		if (lowestTier < 0) return

		val stackTierNumber = Compressed.getTier(itemStack).ordinal() + 1
		val stackSample = Compressed.getSampleStack(itemStack)

		val radiusFactor = stackTierNumber - lowestTier
		val position = new Vect(entity)
		val motion = new Vect(entity.motionX, entity.motionY, entity.motionZ)
		val boundingBoxBase = entity.getEntityBoundingBox
		val radius = 1.5 + radiusFactor

		this.iterateEntitiesAround(boundingBoxBase, radius, world, entity, position, motion,
			// Filter: Should the entity passed by pulled
			(entity: Entity) => {
				entity match {
					case player: EntityPlayer =>
						this.canUseAttractionIII(itemStack) &&
								!player.capabilities.isCreativeMode && !player.isSneaking
					case entityItem: EntityItem =>
						this.canUseAttractionIII(itemStack) ||
								this.canUseAttractionII(itemStack) ||
								this.doesMatchSample(stackSample, entityItem)
					case _ => this.canUseAttractionIII(itemStack)
				}
			}: Boolean,
			(entity: Entity) => {
				entity match {
					case player: EntityPlayer => 0.25D * (4 - ItemDenseArmor.getClothedCount(player))
					case _ => 1
				}
			}: Double,
			// Post effect: When an entity is pulled
			{
				case entityItem: EntityItem =>
					/*
					if (this.doesMatchSample(stackSample, entityItem)) {
						val sizeTotal = Compressed.getSize(itemStack) +
								Compressed.getTotalSizeForUnknown(entityItem.getEntityItem)
						val stackList = this.getStackListFromSize(sizeTotal, stackSample)
						entity.setEntityItemStack(stackList.remove(0))
						if (stackList.nonEmpty) entityItem.setEntityItemStack(stackList.remove(0))
						while (stackList.nonEmpty) {
							val stack = stackList.remove(0)
							world.spawnEntityInWorld(
								if (Compressed.isCompressed(stack))
									new EntityItemCompressed(world, position, motion, stack)
								else {
									val ei = new EntityItem(
										world, position.x, position.y, position.z, stack)
									ei.motionX = motion.x
									ei.motionY = motion.y
									ei.motionZ = motion.z
									ei.setDefaultPickupDelay()
									ei
								}
							)
						}
					}
					*/
				case _ =>
			}
		)

	}

	def iterateEntitiesAround(boundingBoxBase: AxisAlignedBB, radius: Double,
			world: World, entityToExclude: Entity, position: Vect, motion: Vect,
			shouldPull: (Entity) => Boolean, getSpeed: (Entity) => Double,
			onPull: (Entity) => Unit): Unit = {
		val boundingBox = boundingBoxBase.expand(radius, radius, radius)
		val entList = world.getEntitiesWithinAABBExcludingEntity(entityToExclude, boundingBox)
		for (entityBB <- JavaConversions.asScalaBuffer(entList)) {
			if (shouldPull == null || shouldPull(entityBB)) {
				this.pullEntityTowards(entityBB, position, motion,
					speed = if (getSpeed == null) 1 else getSpeed(entityBB))
				if (onPull != null) onPull(entityBB)
			}
		}
	}

	def pullEntityTowards(entityToPull: Entity, pos: Vect, motion: Vect, speed: Double = 1): Unit = {
		if (speed == 0) return
		var distX = pos.x - entityToPull.posX
		var distY = pos.y - entityToPull.posY
		var distZ = pos.z - entityToPull.posZ

		val distance = Math.sqrt(distX * distX + distY * distY + distZ * distZ) * 2

		distX = distX / distance + motion.x * 0.5
		distY = distY / distance + motion.y * 0.5
		distZ = distZ / distance + motion.z * 0.5

		entityToPull.motionX = distX * speed
		entityToPull.motionY = distY * speed
		entityToPull.motionZ = distZ * speed
		entityToPull.isAirBorne = true

		if (entityToPull.isCollidedHorizontally) {
			entityToPull.motionY += 1
		}

		entityToPull.fallDistance = 0f

		if (entityToPull.worldObj.rand.nextInt(20) == 0) {
			val sound = SoundEvent.REGISTRY.getObject(this.portalSound)
			val pitch = 0.85f - entityToPull.worldObj.rand.nextFloat() * 3f / 10f
			entityToPull.getEntityWorld.playSound(
				entityToPull.posX, entityToPull.posY, entityToPull.posZ,
				sound, SoundCategory.BLOCKS, 0.6f, pitch, false
			)
		}
	}

	def doesMatchSample(sample: ItemStack, entityItem: EntityItem): Boolean = {
		val entityItemStack = entityItem.getEntityItem
		val entityItemSample = Compressed.getSampleFromUnknown(entityItemStack)
		sample.getItem == entityItemSample.getItem && sample.getItemDamage == entityItemSample.getItemDamage
	}

}
