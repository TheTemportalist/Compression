package com.temportalist.compression.client.integration.jei;

import com.temportalist.compression.common.recipes.RecipeCompress;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class RecipeFactoryCompress implements IRecipeWrapperFactory<RecipeCompress>
{
    @Override
    public IRecipeWrapper getRecipeWrapper(RecipeCompress recipe)
    {
        return new RecipeWrapperCompress(recipe);
    }
}
