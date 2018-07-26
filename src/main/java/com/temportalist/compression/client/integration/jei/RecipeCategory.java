package com.temportalist.compression.client.integration.jei;

import com.temportalist.compression.client.GuiCompressor;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.recipes.RecipeBasic;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class RecipeCategory<TRecipe extends RecipeBasic> implements IRecipeCategory<RecipeWrapper<TRecipe>>
{

    private final ResourceLocation registryName;
    private final IDrawable background;

    //Offsets from full size gui, makes it much easier to get the location correct
    private int xOff = 45;
    private int yOff = 3;

    public RecipeCategory(IGuiHelper guiHelper, ResourceLocation registryName)
    {
        this.registryName = registryName;
        this.background = guiHelper.createDrawable(GuiCompressor.GUI_TEXTURES, xOff, yOff, 100, 80);
    }

    @Override
    public String getUid()
    {
        return this.registryName.toString();
    }

    @Override
    public String getTitle()
    {
        return I18n.translateToLocal(this.getUid());
    }

    @Override
    public String getModName()
    {
        return Compression.MOD_NAME;
    }

    @Override
    public IDrawable getBackground()
    {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapper<TRecipe> recipeWrapper, IIngredients ingredients)
    {
        int x = 10;
        int y = 31;
        recipeLayout.getItemStacks().init(0, true, x, y);
        recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
        recipeLayout.getItemStacks().init(1, false, x + 60, y);
        recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));
    }

}
