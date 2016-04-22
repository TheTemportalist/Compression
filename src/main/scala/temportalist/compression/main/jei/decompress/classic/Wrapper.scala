package temportalist.compression.main.jei.decompress.classic

import java.util
import java.util.Collections

import mezz.jei.api.recipe.BlankRecipeWrapper
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper
import net.minecraft.item.ItemStack
import temportalist.compression.main.common.init.Compressed
import temportalist.compression.main.common.lib.EnumTier

/**
  *
  * Created by TheTemportalist on 4/22/2016.
  *
  * @author TheTemportalist
  */
class Wrapper(private val sample: ItemStack, private val tierInput: EnumTier)
		extends BlankRecipeWrapper with IShapedCraftingRecipeWrapper {

	val tierTarget = if (tierInput.ordinal() > 0) EnumTier.values()(tierInput.ordinal() - 1) else null

	val inputs = Collections.singletonList(Compressed.create(sample, tier = tierInput))

	val outputs = Collections.singletonList({
		val stack = if (tierTarget == null) sample.copy() else Compressed.create(sample, tier = tierTarget)
		stack.stackSize = 9
		stack
	})

	override def getHeight: Int = 1

	override def getWidth: Int = 1

	override def getOutputs: util.List[ItemStack] = this.outputs

	override def getInputs: util.List[ItemStack] = this.inputs

}
