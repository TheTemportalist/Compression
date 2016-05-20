package temportalist.compression.main.common.block.tile

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase, SharedMonsterAttributes}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.{DamageSource, ITickable}
import temportalist.compression.main.common.init.ModBlocks
import temportalist.compression.main.common.item.ItemDenseArmor
import temportalist.compression.main.common.lib.{EnumTier, Ticks}
import temportalist.compression.main.common.{Effects, Options}
import temportalist.origin.api.common.lib.Vect

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 4/16/2016.
  *
  * @author TheTemportalist
  */
class TileCompressedTickable extends TileCompressed with ITickable {

	private var posCenter: Vect = _

	private val blockDestroyDelay_base = Ticks.getTicks(seconds = 20)
	private val blockDestroyDelay_deviation = Ticks.getTicks(seconds = 2)
	private var blockDestroyDelay_till = -1
	private var blockDestroyDelay_needsReset = true
	private val blockDestroy_radius = 5

	private val entityRadius = 10
	private var entityAABB: AxisAlignedBB = _

	private val checkEnergyBounds_base = Ticks.getTicks(minutes = 4)
	private var checkEnergyBounds_till = -1
	private var checkEnergyBounds_needsReset = true
	private var storedEnergy: Double = 0
	private val energyRadius = 1.5D
	private var energyAABB: AxisAlignedBB = _

	override def update(): Unit = {
		if (EnumTier.getTierForSize(this.getSize).ordinal() + 1 < Options.blackHole) return

		if (this.posCenter == null) this.posCenter = new Vect(this) + Vect.CENTER

		this.updateBlockDestroyLogic()
		this.attractEntities()

		if (Options.blackHolePotentialEnergy)
			this.checkEnergyBoundsLogic()

	}

	private def updateBlockDestroyLogic(): Unit = {
		if (!this.blockDestroyDelay_needsReset && this.blockDestroyDelay_till <= 0) {
			this.destroyBlock()
			this.blockDestroyDelay_needsReset = true
		} else this.blockDestroyDelay_till -= 1
		this.blockDestroy_resetDelay()
	}

	private def blockDestroy_resetDelay(): Unit = {
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
				state.getMaterial != Material.AIR && !state.getMaterial.isLiquid
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

	private def checkEnergyBoundsLogic(): Unit = {
		if (!this.checkEnergyBounds_needsReset && this.checkEnergyBounds_till <= 0) {
			this.checkEnergyBounds()
			this.checkEnergyBounds_needsReset = true
		} else this.checkEnergyBounds_till -= 1
		this.checkEnergy_resetDelay()
	}

	private def checkEnergy_resetDelay(): Unit = {
		if (this.checkEnergyBounds_needsReset) {
			this.checkEnergyBounds_till = this.checkEnergyBounds_base
			this.checkEnergyBounds_till = 20
			this.checkEnergyBounds_needsReset = false
		}
	}

	private def checkEnergyBounds(): Unit = {
		if (this.getWorld.isRemote) return

		if (this.energyAABB == null) {
			val pos = this.posCenter
			this.energyAABB = new AxisAlignedBB(
				pos.x - this.energyRadius, pos.y - this.energyRadius, pos.z - this.energyRadius,
				pos.x + this.energyRadius, pos.y + this.energyRadius, pos.z + this.energyRadius
			)
		}

		val entityList = this.getWorld.getEntitiesWithinAABB(classOf[Entity], this.energyAABB)
		for (entity <- JavaConversions.asScalaBuffer(entityList)) {
			entity match {
				case living: EntityLivingBase =>
					if (living.attackEntityFrom(DamageSource.outOfWorld, 1)) {
						if (living.getHealth <= 0) {
							living match {
								case player: EntityPlayer => this.giveEnergyTo(player)
								case _ => this.storedEnergy += living.getMaxHealth / 10
							}
						}
					}
				case entityItem: EntityItem =>
					this.storedEnergy += entityItem.getEntityItem.stackSize
					entityItem.setDead()
				case _ => entity.setDead()
			}
		}

	}

	private def giveEnergyTo(player: EntityPlayer): Unit = {
		val heartsToGrant = this.storedEnergy.toInt % 10
		player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(
			player.getMaxHealth + 2 * heartsToGrant
		)
		this.storedEnergy -= heartsToGrant * 10
	}

	override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
		val tag = super.writeToNBT(compound)
		tag.setInteger("blockDestroyDelay_till", this.blockDestroyDelay_till)
		tag.setInteger("checkEnergyBounds_till", this.checkEnergyBounds_till)
		tag.setDouble("storedEnergy", this.storedEnergy)
		tag
	}

	override def readFromNBT(compound: NBTTagCompound): Unit = {
		super.readFromNBT(compound)
		this.blockDestroyDelay_till = compound.getInteger("blockDestroyDelay_till")
		this.checkEnergyBounds_till = compound.getInteger("checkEnergyBounds_till")
		this.storedEnergy = compound.getDouble("storedEnergy")

	}

}
