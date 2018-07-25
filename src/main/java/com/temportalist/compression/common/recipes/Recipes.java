package com.temportalist.compression.common.recipes;

import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Recipes {

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        //event.getRegistry().register(new RecipeCompress().setRegistryName(new ResourceLocation(Compression.MOD_ID, "compress")));
        //event.getRegistry().register(new RecipeDeCompress().setRegistryName(new ResourceLocation(Compression.MOD_ID, "decompress")));
    }

    static abstract class RecipeBase implements IRecipe {

        private ResourceLocation registry;

        @Override
        public IRecipe setRegistryName(ResourceLocation name) {
            this.registry = name;
            return this;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return this.registry;
        }

        @Override
        public Class<IRecipe> getRegistryType() {
            return IRecipe.class;
        }

    }

    static abstract class RecipeClassic extends RecipeBase {

        @Override
        public boolean canFit(int width, int height) {
            return width == 3 && height == 3;
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            return NonNullList.withSize(9, ItemStack.EMPTY);
        }

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return NonNullList.create();
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public String getGroup() {
            return "Compression";
        }

        @Override
        public ItemStack getRecipeOutput() {
            return CompressedStack.create(new ItemStack(Blocks.COBBLESTONE), EnumTier.SINGLE);
        }

    }

    static class RecipeDeCompress extends RecipeClassic {

        @Override
        public boolean canFit(int width, int height) {
            return width == 1 && height == 1;
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            boolean foundValid = false;
            for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                ItemStack stackInv = inv.getStackInSlot(slot);
                if (stackInv != ItemStack.EMPTY && stackInv.getItem() != Items.AIR) {
                    if (foundValid || !CompressedStack.isCompressed(stackInv)) return false;
                    foundValid = true;
                }
            }
            return foundValid;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack sample = null;
            EnumTier tier = null;
            for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                ItemStack stackInv = inv.getStackInSlot(slot);
                if (CompressedStack.isCompressed(stackInv)) {
                    sample = CompressedStack.createSampleStack(stackInv);
                    tier = CompressedStack.getTier(stackInv);
                    break;
                }
            }

            if (sample == null) return null;
            if (tier == null) return null;

            int outTierOrdinal = tier.ordinal() - 1;
            tier = outTierOrdinal >= 0 ? EnumTier.getTier(outTierOrdinal) : null;
            ItemStack out = CompressedStack.create(sample, tier);
            if (out != null) out.setCount(9);
            return out;
        }

    }

}
