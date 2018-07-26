package com.temportalist.compression.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RecipeDecompress extends RecipeBasic
{

    public static final Map<ResourceLocation, RecipeDecompress> RECIPES = new HashMap<>();

    public static RecipeDecompress register(RecipeDecompress recipe)
    {
        RECIPES.put(recipe.getRegistryName(), recipe);
        return recipe;
    }

    public RecipeDecompress(ResourceLocation registryName, ItemStack input, ItemStack output)
    {
        super(registryName, input, output);
    }

}
