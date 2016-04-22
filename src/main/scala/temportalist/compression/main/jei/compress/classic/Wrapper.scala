package temportalist.compression.main.jei.compress.classic

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
class Wrapper(private val sample: ItemStack, private val tierTarget: EnumTier)
		extends BlankRecipeWrapper with IShapedCraftingRecipeWrapper {

	val tierInput = if (tierTarget.ordinal() > 0) EnumTier.values()(tierTarget.ordinal() - 1) else null

	val inputs = new util.ArrayList[ItemStack](9)
	val inputStack = if (tierInput == null) sample.copy() else Compressed.create(sample, tier = tierInput)
	for (i <- 0 until 9) inputs.add(inputStack)

	val outputs = Collections.singletonList(Compressed.create(sample, tier = tierTarget))

	override def getHeight: Int = 3

	override def getWidth: Int = 3

	override def getOutputs: util.List[ItemStack] = this.outputs

	override def getInputs: util.List[ItemStack] = this.inputs

}
