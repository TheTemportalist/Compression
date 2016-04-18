package temportalist.compression.main.common.block.tile

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import temportalist.compression.main.common.init.ModBlocks
import temportalist.compression.main.common.item.ItemDenseArmor
import temportalist.compression.main.common.lib.{EnumTier, Ticks}
import temportalist.compression.main.common.{Compression, Effects, Options}
import temportalist.origin.api.common.lib.Vect

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 4/16/2016.
  *
  * @author TheTemportalist
  */
class TileCompressedTickable extends TileCompressed with ITickable {

	private val blockDestroyDelay_base = Ticks.getTicks(seconds = 20)
	private val blockDestroyDelay_deviation = Ticks.getTicks(seconds = 2)
	private var blockDestroyDelay_till = -1
	private var blockDestroyDelay_needsReset = true
	private val blockDestroy_radius = 5

	private val entityRadius = 10
	private var entityAABB: AxisAlignedBB = _
	private var posCenter: Vect = _

	private var storedEnergy: Double = 0

	override def update(): Unit = {
		if (EnumTier.getTierForSize(this.getSize).ordinal() + 1 < Options.blackHole) return

		this.updateBlockDestroyLogic()
		this.attractEntities()

	}

	private def updateBlockDestroyLogic(): Unit = {
		if (!this.blockDestroyDelay_needsReset && this.blockDestroyDelay_till <= 0) {
			this.destroyBlock()
			this.blockDestroyDelay_needsReset = true
		} else this.blockDestroyDelay_till -= 1
		this.resetDelay()
	}

	private def resetDelay(): Unit = {
		if (this.blockDestroyDelay_needsReset) {
			this.blockDestroyDelay_till = this.blockDestroyDelay_base +
					this.getWorld.rand.nextInt(this.blockDestroyDelay_deviation)
			this.blockDestroyDelay_till = 20
			this.blockDestroyDelay_needsReset = false
		}
	}

	private def destroyBlock(): Unit = {
		if (this.getWorld.isRemote) return

		val thisVect = new Vect(this)

		var vect: Vect = null
		var pos: BlockPos = null
		var state: IBlockState = null
		var loops = 0
		val loopMax = 100
		do {
			loops += 1
			vect = new Vect(this.getRandPosRadius, this.getRandPosRadius, this.getRandPosRadius)
			vect += thisVect
			pos = vect.toBlockPos
			state = this.getWorld.getBlockState(pos)
		}
		while (loops < loopMax && !this.isValidStateToDestroy(vect, pos, state))

		if (loops > loopMax) return

		this.getWorld.setBlockToAir(pos)

	}

	private def getRandPosRadius: Int =
		this.getWorld.rand.nextInt(blockDestroy_radius * 2 + 1) - this.blockDestroy_radius

	private def isValidStateToDestroy(vect: Vect, pos: BlockPos, state: IBlockState): Boolean = {
		pos != this.getPos && vect.length <= this.blockDestroy_radius &&
				!this.isInDestroyBlacklist(state) &&
				state.getMaterial != Material.air && !state.getMaterial.isLiquid
	}

	private def isInDestroyBlacklist(state: IBlockState): Boolean = {
		state.getBlock == ModBlocks.block && state.getBlock.hasTileEntity(state)
	}

	private def attractEntities(): Unit = {

		if (this.entityAABB == null) {
			val pos = this.getPos
			this.entityAABB = new AxisAlignedBB(
				pos.getX - this.entityRadius, pos.getY - this.entityRadius, pos.getZ - this.entityRadius,
				pos.getX + this.entityRadius, pos.getY + this.entityRadius, pos.getZ + this.entityRadius
			)
		}
		if (this.posCenter == null) this.posCenter = new Vect(this) + Vect.CENTER

		val entityList = this.getWorld.getEntitiesWithinAABB(classOf[Entity], this.entityAABB)
		for (entity <- JavaConversions.asScalaBuffer(entityList)) {
			if (this.shouldPullEntity(entity)) {
				var speed = 1D
				entity match {
					case player: EntityPlayer =>
						speed -= ItemDenseArmor.getClothedCount(player) * 0.25
					case _ =>
				}
				Effects.pullEntityTowards(entity, this.posCenter, Vect.ZERO, speed = speed)
			}
		}

	}

	private def shouldPullEntity(entity: Entity): Boolean = {
		entity match {
			case player: EntityPlayer => !player.capabilities.isCreativeMode
			case _ => true
		}
	}

	override def writeToNBT(compound: NBTTagCompound): Unit = {
		super.writeToNBT(compound)
		compound.setInteger("blockDestroyDelay_till", this.blockDestroyDelay_till)
		compound.setDouble("storedEnergy", this.storedEnergy)

	}

	override def readFromNBT(compound: NBTTagCompound): Unit = {
		super.readFromNBT(compound)
		this.blockDestroyDelay_till = compound.getInteger("blockDestroyDelay_till")
		this.storedEnergy = compound.getDouble("storedEnergy")

	}

}
