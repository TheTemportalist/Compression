package temportalist.compression.main.common.block.tile

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import temportalist.compression.main.common.{Effects, Options}
import temportalist.compression.main.common.init.ModBlocks
import temportalist.compression.main.common.lib.{EnumTier, Ticks}
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

	private val entityRadius = 5
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
		if (this.blockDestroyDelay_needsReset)
			this.blockDestroyDelay_till = this.blockDestroyDelay_base +
				this.getWorld.rand.nextInt(this.blockDestroyDelay_deviation)
	}

	private def destroyBlock(): Unit = {

		val thisVect = new Vect(this)

		var vect: Vect = null
		var pos: BlockPos = null
		var state: IBlockState = null
		var loops = 0
		val loopMax = 5
		do {
			loops += 1
			vect = new Vect(this.getRandPosRadius, this.getRandPosRadius, this.getRandPosRadius)
			vect += thisVect
			pos = vect.toBlockPos
			state = this.getWorld.getBlockState(pos)
		} while(
			loops < loopMax &&
					(pos == this.getPos || vect.length > this.blockDestroy_radius ||
							this.isInDestroyBlacklist(state))
		)

		if (loops >= loopMax) return

		this.getWorld.setBlockToAir(pos)

	}

	private def getRandPosRadius: Int =
		this.getWorld.rand.nextInt(blockDestroy_radius * 2 + 1) + this.blockDestroy_radius

	private def isInDestroyBlacklist(state: IBlockState): Boolean = {
		state.getBlock == ModBlocks.block && state.getBlock.hasTileEntity(state)
	}

	def attractEntities(): Unit = {

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
			if (this.shouldPullEntity(entity))
				Effects.pullEntityTowards(entity, this.posCenter, Vect.ZERO)
		}

	}

	def shouldPullEntity(entity: Entity): Boolean = {
		true
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
