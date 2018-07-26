package com.temportalist.compression.client.integration.jei;

import com.temportalist.compression.common.recipes.RecipeBasic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class RecipeWrapper<TRecipe extends RecipeBasic> implements IRecipeWrapper
{

    private final TRecipe recipe;

    public RecipeWrapper(TRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInput(ItemStack.class, this.getInput());
        ingredients.setOutput(ItemStack.class, this.getOutput());
    }

    public ItemStack getInput() {
        return this.recipe.getRecipeInput();
    }

    public ItemStack getOutput() {
        return this.recipe.getRecipeOutput();
    }

}
