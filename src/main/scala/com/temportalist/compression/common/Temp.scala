package com.temportalist.compression.common

import java.util

import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.Stacks
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/13/2015.
 */
object Temp {

	/**
	 * Loops through surrounding EntityItem's
	 * @param boundingBox The bounding box to be expanded upon
	 * @param world The world
	 * @param pos A vector at the main point (the one things will be pulled towards)
	 * @param motion A vector showing where the pos vector is moving
	 */
	def tryToPullCloser[U](radiusFactor: Int, entity: Entity,
			boundingBox: AxisAlignedBB,
			world: World, pos: V3O, motion: V3O,
			shouldAttract: (Entity) => Boolean, onAttraction: (Entity) => U): Unit = {
		val radius = radiusFactor + 1.5D
		val radiusBounds: AxisAlignedBB = boundingBox.expand(radius, radius, radius)
		world.getEntitiesWithinAABBExcludingEntity(entity, radiusBounds) match {
			case list: util.List[_] =>
				for (i <- 0 until list.size()) {
					list.get(i) match {
						case otherEnt: Entity =>
							if (shouldAttract == null || shouldAttract(otherEnt)) {
								val intersetBB = otherEnt.boundingBox != null &&
										boundingBox != null &&
										otherEnt.boundingBox.intersectsWith(
											boundingBox.expand(0.25, 0.25, 0.25))
								if (!intersetBB) this.pullEntityTowards(otherEnt, pos, motion)
								if (intersetBB && onAttraction != null) onAttraction(otherEnt)
							}
						case _ =>
					}
				}
			case _ =>
		}

	}

	def addToInventoryWithDrop(player: EntityPlayer, stack: ItemStack): Unit = {
		if (!player.inventory.addItemStackToInventory(stack)) Stacks.tossItem(stack, player)
	}

	def pullEntityTowards(entityBeingPulled: Entity, pos: V3O, motion: V3O): Unit = {
		var distX = pos.x - entityBeingPulled.posX
		var distY = pos.y - entityBeingPulled.posY
		var distZ = pos.z - entityBeingPulled.posZ

		val distance = Math.sqrt(distX * distX + distY * distY + distZ * distZ) * 2

		distX = distX / distance + motion.x / 2
		distY = distY / distance + motion.y / 2
		distZ = distZ / distance + motion.z / 2

		entityBeingPulled.motionX = distX
		entityBeingPulled.motionY = distY
		entityBeingPulled.motionZ = distZ
		entityBeingPulled.isAirBorne = true

		if (entityBeingPulled.isCollidedHorizontally) {
			entityBeingPulled.motionY += 1
		}

		if (entityBeingPulled.worldObj.rand.nextInt(20) == 0) {
			val pitch = 0.85f - entityBeingPulled.worldObj.rand.nextFloat() * 3f / 10f
			entityBeingPulled.worldObj.playSoundEffect(
				entityBeingPulled.posX, entityBeingPulled.posY, entityBeingPulled.posZ,
				"mob.endermen.portal", 0.6f, pitch)
		}
	}

}
