package com.temportalist.compression.common

import com.temportalist.compression.common.entity.EntityItemCompressed
import com.temportalist.compression.common.init.{CItems, CBlocks}
import com.temportalist.compression.common.item.IFood
import com.temportalist.origin.api.common.lib.NameParser
import com.temportalist.origin.api.common.utility.{WorldHelper, Scala, Stacks}
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemFood, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

import scala.collection.mutable.ListBuffer

/**
 * Provides utility methods for dealing with compressed stacks
 * Created by TheTemportalist on 12/9/2015.
 */
object CompressedStack {

	/**
	 * Checks if the stack's item is a compressed block or item
	 * @param stack The stack being checked - can be null
	 * @return If the stack is compressed
	 */
	def isCompressedStack(stack: ItemStack): Boolean = {
		stack != null &&
				(stack.getItem == CBlocks.compressedItem || stack.getItem == CItems.compressed)
	}

	private def checkNBT(stack: ItemStack): Unit =
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)

	def getTypeString(stack: ItemStack): String = {
		this.checkNBT(stack)
		stack.getTagCompound.getString("inner")
	}

	/**
	 * Gets the type stack of the passed stack
	 * @param stack An ItemStack
	 * @return A stack representing the inner type of the passed stack.
	 *         A copy of the stack is the stack is not a compressed stack.
	 */
	def getStackType(stack: ItemStack): ItemStack = {
		if (!this.isCompressedStack(stack)) stack.copy()
		else {
			this.checkNBT(stack)
			NameParser.getItemStack(CompressedStack.getTypeString(stack))
		}
	}

	def getTypeName(stack: ItemStack) : String = {
		this.checkNBT(stack)
		stack.getTagCompound.getString("display")
	}

	/**
	 * Gets the full size of the stack.
	 * @param stack An ItemStack
	 * @return The stack's size * the compressed size
	 */
	def getTotalSize(stack: ItemStack): Long = {
		stack.stackSize * this.getCompressedSize(stack)
	}

	/**
	 * Gets the compressed size.
	 * @param stack An ItemStack
	 * @return 1 if not a compresses stack,
	 *         otherwise, the amount of blocks that have been compressed.
	 */
	def getCompressedSize(stack: ItemStack): Long = {
		if (this.isCompressedStack(stack)) {
			this.checkNBT(stack)
			stack.getTagCompound.getLong("stackSize")
		} else 1
	}

	def setCompressedSize(stack: ItemStack, size: Long): Unit = {
		this.checkNBT(stack)
		stack.getTagCompound.setLong("stacksize", size)
	}

	def -=(stack: ItemStack, amt: Long): Unit = {
		this.setCompressedSize(stack, CompressedStack.getCompressedSize(stack) - amt)
	}

	def simplifyStack(stack: ItemStack): ItemStack = {
		if (!CompressedStack.isCompressedStack(stack)) stack
		else {
			val size = CompressedStack.getCompressedSize(stack)
			if (size <= 0) null
			else if (size == 1) this.getStackType(stack)
			else stack
		}
	}

	/**
	 * Create a compressed stack with the passed size.
	 * @param stack An ItemStack
	 * @param size The desired size
	 * @return A stack with the same type stack as the passed stack
	 *         and a compressed size of the passed size.
	 */
	def createCompressedStack(stack: ItemStack, size: Long): ItemStack = {
		val stackType = this.getStackType(stack)

		val compressedStack =
			if (WorldHelper.isBlock(stackType.getItem)) new ItemStack(CBlocks.compressed)
			else new ItemStack(CItems.compressed)

		val tag = new NBTTagCompound
		stackType.getItem match {
			case food: ItemFood =>
				tag.setBoolean("canEat", true)
				compressedStack.getItem.asInstanceOf[IFood]
						.setHealAmount(food.func_150905_g(stackType))
				compressedStack.getItem.asInstanceOf[IFood]
						.setSaturation(food.func_150906_h(stackType))
			case food: IFood =>
				tag.setBoolean("canEat", true)
				stackType.getItem.asInstanceOf[IFood]
						.setHealAmount(food.getFoodAmount(stackType))
				stackType.getItem.asInstanceOf[IFood]
						.setSaturation(food.getSaturationAmount(stackType))
			case _ =>
				tag.setBoolean("canEat", false)
		}
		tag.setString("inner", NameParser.getName(stack, hasID = true, hasMeta = true))
		tag.setString("display", stack.getDisplayName)
		tag.setLong("stackSize", size)
		compressedStack.setTagCompound(tag)

		compressedStack
	}

	def createEntity(world: World, location: Entity, stack: ItemStack): Entity = {
		val ent = new EntityItemCompressed(
			world, location.posX, location.posY, location.posZ, stack)
		ent.motionX = location.motionX
		ent.motionY = location.motionY
		ent.motionZ = location.motionZ
		ent.delayBeforeCanPickup = 10
		ent
	}

	def doStackTypesMatch(a: ItemStack, b: ItemStack): Boolean = {
		if (this.isCompressedStack(a))
			if (this.isCompressedStack(b))
				Stacks.doStacksMatch(this.getStackType(a), this.getStackType(b))
			else Stacks.doStacksMatch(this.getStackType(a), b)
		else
			if (this.isCompressedStack(b)) Stacks.doStacksMatch(a, this.getStackType(b))
			else Stacks.doStacksMatch(a, b)
	}

	def removeAllOfType(player: EntityPlayer, stack: ItemStack,
			withHotbar: Boolean, extras: ItemStack*): Long = {
		val itemType: ItemStack = this.getStackType(stack)
		var total: Long = 0
		if (itemType != null) Scala.foreach(player.inventory, (slot: Int, stack: ItemStack) => {
			if ((withHotbar || slot >= 9) && this.doStackTypesMatch(itemType, stack)) {
				total += this.getTotalSize(stack)
				player.inventory.setInventorySlotContents(slot, null)
			}
		})
		extras.foreach(stack =>
			if (this.doStackTypesMatch(itemType, stack))
				total += this.getTotalSize(stack)
		)
		total
	}

	def divideIntoClassicCompressions(stack: ItemStack, total: Long): ListBuffer[ItemStack] = {
		val stackType = this.getStackType(stack)
		val list = ListBuffer[ItemStack]()

		var size = total
		while (size > 0) {
			val rank = Rank.getRank(size)
			val highestFullRank = rank -= 1
			val stackSize = Math.min(size, highestFullRank.getMaximum)
			size -= stackSize
			list += {
				var stack: ItemStack = null
				if (Rank.getRank(stackSize).getIndex <= 0) {
					stack = stackType.copy()
					stack.stackSize = stackSize.toInt
				}
				else stack = this.createCompressedStack(stackType, stackSize)
				stack
			}
		}

		list
	}

	def shouldAttractEntity(originEntity: EntityItemCompressed, otherEntity: Entity): Boolean = {
		otherEntity match {
			case entityItem: EntityItem =>
				val stack = originEntity.getEntityItem
				val otherEntStack = entityItem.getEntityItem
				entityItem.age > 10 &&
						CompressedStack.doStackTypesMatch(stack, otherEntStack) &&
						CompressedStack.getTotalSize(otherEntStack) <
								CompressedStack.getTotalSize(stack)
			case _ => false
		}
	}

	def onAttraction(originEntity: EntityItemCompressed, otherEntity: Entity): Unit = {
		otherEntity match {
			case entityItem: EntityItem =>
				val originStack = originEntity.getEntityItem
				val otherStack = entityItem.getEntityItem
				if (!CompressedStack.doStackTypesMatch(originStack, otherStack)) return
				if (originEntity.boundingBox != null && entityItem.boundingBox != null &&
						entityItem.boundingBox.intersectsWith(
							originEntity.boundingBox.expand(0.25, 0.25, 0.25))) {
					val totalAmount = CompressedStack.getTotalSize(originStack) +
							CompressedStack.getTotalSize(otherStack)
					val list = this.divideIntoClassicCompressions(originStack, totalAmount)

					entityItem.setDead()
					originEntity.setEntityItemStack(list.remove(0))
					list.foreach(stack => originEntity.worldObj.spawnEntityInWorld(
						this.createEntity(originEntity.worldObj, originEntity, stack)
					))

				}
			case _ =>
		}
	}

}
