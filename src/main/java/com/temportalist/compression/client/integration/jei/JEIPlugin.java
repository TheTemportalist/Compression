package com.temportalist.compression.client.integration.jei;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.blocks.TileCompressor;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.init.ModBlocks;
import com.temportalist.compression.common.init.ModItems;
import com.temportalist.compression.common.recipes.RecipeCompress;
import com.temportalist.compression.common.recipes.RecipeDecompress;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin
{

    public IRecipeCategory compress, decompress;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry)
    {
        subtypeRegistry.registerSubtypeInterpreter(ModItems.compressed, CompressedStack::getUniqueSubtypeString);
        subtypeRegistry.registerSubtypeInterpreter(ModBlocks.compressed.item, CompressedStack::getUniqueSubtypeString);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry)
    {

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.compress = new RecipeCategory<RecipeCompress>(guiHelper, TileCompressor.GetContainerName(0));
        this.decompress = new RecipeCategory<RecipeDecompress>(guiHelper,  TileCompressor.GetContainerName(1));
        registry.addRecipeCategories(this.compress, this.decompress);
    }

    @Override
    public void register(IModRegistry registry)
    {
        registry.handleRecipes(RecipeCompress.class, new RecipeFactoryCompress(), this.compress.getUid());
        registry.handleRecipes(RecipeDecompress.class, new RecipeFactoryDecompress(), this.decompress.getUid());
        registry.addRecipes(RecipeCompress.RECIPES.values(), this.compress.getUid());
        registry.addRecipes(RecipeDecompress.RECIPES.values(), this.decompress.getUid());
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.compressor, 1, 0), this.compress.getUid());
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.compressor, 1, 1), this.decompress.getUid());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
    {

    }

}
