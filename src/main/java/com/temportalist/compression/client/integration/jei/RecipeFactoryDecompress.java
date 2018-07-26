package com.temportalist.compression.client.integration.jei;

import com.temportalist.compression.common.recipes.RecipeDecompress;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class RecipeFactoryDecompress implements IRecipeWrapperFactory<RecipeDecompress>
{
    @Override
    public IRecipeWrapper getRecipeWrapper(RecipeDecompress recipe)
    {
        return new RecipeWrapperDecompress(recipe);
    }
}
