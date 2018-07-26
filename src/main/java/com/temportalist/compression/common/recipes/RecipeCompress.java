package com.temportalist.compression.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RecipeCompress extends RecipeBasic
{

    public static final Map<ResourceLocation, RecipeCompress> RECIPES = new HashMap<>();

    public static RecipeCompress register(RecipeCompress recipe)
    {
        RECIPES.put(recipe.getRegistryName(), recipe);
        return recipe;
    }

    public RecipeCompress(ResourceLocation registryName, ItemStack input, ItemStack output)
    {
        super(registryName, input, output);
    }

}
