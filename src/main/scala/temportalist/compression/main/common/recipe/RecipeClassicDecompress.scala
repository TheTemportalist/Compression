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
  * Created by TheTemportalist on 4/15/2016.
  *
  * @author TheTemportalist
  */
class RecipeClassicDecompress(private val sample: ItemStack, private val tierTarget: EnumTier) extends IRecipe {

	private val stackIn =
		if (this.tierTarget != null) Compressed.create(sample, tier = tierTarget.getNext)
		else Compressed.create(sample, tier = EnumTier.getHead)
	private val stackOut =
		if (this.tierTarget != null) Compressed.create(sample, tier = tierTarget)
		else sample.copy()
	stackOut.stackSize = 9

	override def getRecipeSize: Int = 1

	override def getRecipeOutput: ItemStack = this.stackOut

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = this.getRecipeOutput.copy()

	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = new Array[ItemStack](9)

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		var foundValidStack = false
		for (row <- 0 until 3) for (col <- 0 until 3) {
			inv.getStackInSlot(col + row * 3) match {
				case invStack: ItemStack =>
					if (foundValidStack) return false

					val isSample = !Compressed.isCompressed(invStack)
					if (isSample) return false

					val invStackSample = Compressed.getSampleStack(invStack)

					val sameItem = this.sample.getItem == invStackSample.getItem
					val sameMeta = this.sample.getItemDamage == invStackSample.getItemDamage
					val sameTag = this.sample.getTagCompound == invStackSample.getTagCompound
					if (sameItem && sameMeta && sameTag) {
						foundValidStack = Compressed.getTier(invStack) == Compressed.getTier(this.stackIn)
					}
				case _ =>
			}
		}
		foundValidStack
	}

}
