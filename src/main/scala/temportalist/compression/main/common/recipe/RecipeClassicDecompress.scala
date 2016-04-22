package temportalist.compression.main.common.recipe

import net.minecraft.init.Blocks
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.item.ICompressed
import temportalist.compression.main.common.lib.EnumTier

import scala.util.control.Breaks._

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

	override def getCraftingResult(inv: InventoryCrafting): ItemStack = {
		var meta: Int = -1
		breakable {
			for (i <- 0 until inv.getSizeInventory) inv.getStackInSlot(i) match {
				case stack: ItemStack =>
					val sampleStack = Compressed.getSampleFromUnknown(stack)
					meta = sampleStack.getItemDamage
					break()
				case _ =>
			}
		}
		val stack = this.sample.copy()
		stack.setItemDamage(meta)
		val out = if (this.tierTarget != null) Compressed.create(stack, tier = tierTarget) else stack
		out.stackSize = 9
		out
	}

	override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = new Array[ItemStack](9)

	override def matches(inv: InventoryCrafting, worldIn: World): Boolean = {
		var foundValidStack = false
		for (row <- 0 until 3) for (col <- 0 until 3) {
			inv.getStackInSlot(col + row * 3) match {
				case invStack: ItemStack =>
					if (foundValidStack) return false
					if (!Compressed.isCompressed(invStack)) return false

					val invStackSample = Compressed.getSampleStack(invStack)

					val sameItem = this.sample.getItem == invStackSample.getItem
					if (sameItem) {
						foundValidStack = Compressed.getTier(invStack) == Compressed.getTier(this.stackIn)
					}
				case _ =>
			}
		}
		foundValidStack
	}

}
