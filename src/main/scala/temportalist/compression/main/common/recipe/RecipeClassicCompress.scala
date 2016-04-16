package temportalist.compression.main.common.recipe

import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.item.ICompressed
import temportalist.compression.main.common.lib.EnumTier

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
class RecipeClassicCompress(private val stackIn: ItemStack, private val tierTarget: EnumTier) extends IRecipe {

	this.stackIn.stackSize = 1
	private val stackOut = Compressed.create(stackIn, tier = tierTarget)

	override def getRecipeSize: Int = 9

	override def getRecipeOutput: ItemStack = this.stackOut

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getRecipeOutput.copy()

	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = new Array[ItemStack](9)

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		for (row <- 0 until 3) for (col <- 0 until 3) {
			inv.getStackInSlot(col + row * 3) match {
				case invStack: ItemStack =>
					val isSample = !Compressed.isCompressed(invStack)
					val invStackSample =
						if (isSample) invStack
						else Compressed.getSampleStack(invStack)

					val sameItem = this.stackIn.getItem == invStackSample.getItem
					val sameMeta = this.stackIn.getItemDamage == invStackSample.getItemDamage
					val sameTag = this.stackIn.getTagCompound == invStackSample.getTagCompound
					var isValid = false
					if (sameItem && sameMeta && sameTag) {
						isValid =
								if (isSample) this.tierTarget == EnumTier.SINGLE
								else this.tierTarget.ordinal() == Compressed.getTier(invStack).ordinal() + 1
					}
					if (!isValid) return false
				case _ => return false
			}
		}
		true
	}


}
