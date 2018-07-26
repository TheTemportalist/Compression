package com.temportalist.compression.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RecipeNToN
{

    public static RecipeCompress createNToOne(ResourceLocation registry, ItemStack sampleIn, ItemStack sampleOut, int countIn) {
        ItemStack input = sampleIn.copy();
        ItemStack output = sampleOut.copy();
        input.setCount(countIn);
        return RecipeCompress.register(new RecipeCompress(registry, input, output));
    }

    public static RecipeDecompress createOneToN(ResourceLocation registry, ItemStack sampleIn, ItemStack sampleOut, int countOut) {
        ItemStack input = sampleIn.copy();
        ItemStack output = sampleOut.copy();
        output.setCount(countOut);
        return RecipeDecompress.register(new RecipeDecompress(registry, input, output));
    }

}
