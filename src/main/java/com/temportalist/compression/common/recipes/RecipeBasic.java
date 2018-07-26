package com.temportalist.compression.common.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RecipeBasic implements IRecipe
{

    private final ItemStack input;
    private final ItemStack output;
    private ResourceLocation registryName;

    public RecipeBasic(ResourceLocation registryName, ItemStack input, ItemStack output)
    {
        this.input = input;
        this.output = output;
        this.setRegistryName(registryName);
    }

    @Override
    public RecipeBasic setRegistryName(ResourceLocation resourceLocation)
    {
        this.registryName = resourceLocation;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName()
    {
        return this.registryName;
    }

    @Override
    public Class<IRecipe> getRegistryType()
    {
        return IRecipe.class;
    }

    @Override
    public boolean canFit(int i, int i1)
    {
        return false;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world)
    {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return  NonNullList.from(Ingredient.fromStacks(this.input));
    }

    public ItemStack getRecipeInput()
    {
        return this.input;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return this.output;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting)
    {
        return this.output.copy();
    }
}
