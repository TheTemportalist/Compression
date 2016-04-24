package temportalist.compression.main.common.recipe.classic

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.lib.EnumTier

/**
  *
  * Created by TheTemportalist on 4/24/2016.
  *
  * @author TheTemportalist
  */
class RecipeCompress extends RecipeClassic {

	override def getRecipeSize: Int = 9

	def isValidInventoryStack(stack: ItemStack, isSample: Boolean): Boolean = {
		!isSample || Compressed.canCompressItem(stack)
	}

	def isValidSampleAndTier(sample: ItemStack, tier: EnumTier): Boolean = {
		tier != EnumTier.getTail
	}

	override def matches(inv: InventoryCrafting, world: World): Boolean = {
		var stackSample: ItemStack = null
		var tier: EnumTier = null

		val size = 0 until 3
		for (row <- size ; col <- size) {
			inv.getStackInSlot(row * 3 + col) match {
				case stackInv: ItemStack =>

					val isSample = !Compressed.isCompressed(stackInv)
					val stackSampleInv =
						if (isSample) stackInv
						else Compressed.getSampleStack(stackInv)
					val tierInv = if (!isSample) Compressed.getTier(stackInv) else null

					if (!this.isValidInventoryStack(stackInv, isSample)) return false

					// Sample is has been found
					// Tier is found (can be null for uncompressed)
					if (stackSample != null) {

						// Check if the tiers match
						if (tier != tierInv) return false

						// Check if items match
						if (stackSample.getItem != stackSampleInv.getItem) return false

						// Check if damage matches
						if (stackSample.getItemDamage != stackSampleInv.getItemDamage) return false

					}
					// Sample must be created
					// Tier must be created (can be null if uncompressed)
					else {
						stackSample = stackSampleInv.copy()
						tier = tierInv
					}

				case _ => // null stack - cannot compress
					return false
			}
		}

		this.isValidSampleAndTier(stackSample, tier)
	}

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = {
		var stackSample: ItemStack = null
		var tier: EnumTier = null

		val size = 0 until 3
		for (row <- size ; col <- size) {
			inv.getStackInSlot(row * 3 + col) match {
				case stackInv: ItemStack =>

					val isSample = !Compressed.isCompressed(stackInv)
					val stackSampleInv =
						if (isSample) stackInv
						else Compressed.getSampleStack(stackInv)
					val tierInv = if (!isSample) Compressed.getTier(stackInv) else null

					if (!this.isValidInventoryStack(stackInv, isSample)) return null

					// Sample is has been found
					// Tier is found (can be null for uncompressed)
					if (stackSample != null) {

						// Check if the tiers match
						if (tier != tierInv) return null

						// Check if items match
						if (stackSample.getItem != stackSampleInv.getItem) return null

						// Check if damage matches
						if (stackSample.getItemDamage != stackSampleInv.getItemDamage) return null

					}
					// Sample must be created
					// Tier must be created (can be null if uncompressed)
					else {
						stackSample = stackSampleInv.copy()
						tier = tierInv
					}

				case _ => // null stack - cannot compress
					return null
			}
		}

		if (!this.isValidSampleAndTier(stackSample, tier)) return null

		if (tier != null) Compressed.create(stackSample, tier = tier.getNext)
		else Compressed.create(stackSample, tier = EnumTier.SINGLE)
	}

}
