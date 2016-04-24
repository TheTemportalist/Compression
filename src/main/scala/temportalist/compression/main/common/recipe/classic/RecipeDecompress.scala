package temportalist.compression.main.common.recipe.classic

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.lib.EnumTier

import scala.util.control.Breaks._

/**
  *
  * Created by TheTemportalist on 4/24/2016.
  *
  * @author TheTemportalist
  */
class RecipeDecompress extends RecipeClassic {

	override def getRecipeSize: Int = 1

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		var foundValidStack = false
		for (row <- 0 until 3) for (col <- 0 until 3) {
			inv.getStackInSlot(col + row * 3) match {
				case stackInv: ItemStack =>
					if (foundValidStack) return false
					if (!Compressed.isCompressed(stackInv)) return false
					foundValidStack = true
				case _ =>
			}
		}
		true
	}

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = {
		var sample: ItemStack = null
		var tier: EnumTier = null
		breakable {
			for (i <- 0 until inv.getSizeInventory) inv.getStackInSlot(i) match {
				case stackInv: ItemStack =>
					sample = Compressed.getSampleFromUnknown(stackInv)
					tier = if (Compressed.isCompressed(stackInv)) Compressed.getTier(stackInv) else null
					break()
				case _ =>
			}
		}
		if (sample == null) return null

		val outTierOrdinal = tier.ordinal() - 1
		tier = if (outTierOrdinal >= 0) EnumTier.getTier(outTierOrdinal) else null
		val out = if (tier != null) Compressed.create(sample, tier = tier) else sample.copy
		out.stackSize = 9
		out
	}

}
