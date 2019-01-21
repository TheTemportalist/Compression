package com.temportalist.compression.common;

import com.temportalist.compression.common.blocks.BlockCompressed;
import com.temportalist.compression.common.blocks.TileCompressor;
import com.temportalist.compression.common.config.Config;
import com.temportalist.compression.common.container.ContainerCompressor;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.init.ModBlocks;
import com.temportalist.compression.common.init.ModEntity;
import com.temportalist.compression.common.init.ModItems;
import com.temportalist.compression.common.items.ItemCompressed;
import com.temportalist.compression.common.lib.EnumTier;
import com.temportalist.compression.common.recipes.RecipeNToN;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.Map;

@Mod.EventBusSubscriber
public class ProxyCommon implements IProxy, IGuiHandler {

    @Override
    public void initPre(FMLPreInitializationEvent event) {

        Compression.main.config = new Config(event.getModConfigurationDirectory());
        Compression.main.config.initPre();

        Compression.main.tabCompression = new CreativeTabs(Compression.MOD_ID) {
            @Override
            public ItemStack getTabIconItem() {
                return CompressedStack.create(new ItemStack(Blocks.COBBLESTONE), EnumTier.QUADRUPLE);
            }
        };

        Compression.main.blocks = new ModBlocks();
        Compression.main.blocks.initPre();

        Compression.main.items = new ModItems();
        Compression.main.items.initPre();

        Compression.main.entity = new ModEntity();
        Compression.main.entity.initPre();

    }

    @Override
    public void init(FMLInitializationEvent event)
    {

        ForgeRegistries.RECIPES.getValue(new ResourceLocation(Compression.MOD_ID, "decompressor"));
        GameRegistry.addShapedRecipe(
                new ResourceLocation(Compression.MOD_ID, "decompressor"), null,
                new ItemStack(ModBlocks.compressor, 1, 1),
                "PIP",
                "ICI",
                "PIP",
                'I', Items.IRON_INGOT,
                'P', Blocks.PISTON,
                'C', CompressedStack.create(new ItemStack(Blocks.COAL_BLOCK), EnumTier.DOUBLE)
        );

    }

    @Override
    public void initPost(FMLPostInitializationEvent event)
    {

        Compression.main.config.compressableItems = NonNullList.create();
        Compression.main.config.compressableBlocks = NonNullList.create();

        for (Map.Entry<ResourceLocation, Item> entry : ForgeRegistries.ITEMS.getEntries())
        {
            Item entryItem = entry.getValue();

            NonNullList<ItemStack> entrySubtypes = NonNullList.create();
            entryItem.getSubItems(CreativeTabs.SEARCH, entrySubtypes);

            for (ItemStack itemStack : entrySubtypes)
            {
                if (!CompressedStack.canCompressStack(itemStack)) continue;
                if (entryItem instanceof ItemBlock)
                {
                    Compression.main.config.compressableBlocks.add(itemStack);
                }
                else {
                    Compression.main.config.compressableItems.add(itemStack);
                }
            }

        }

        Compression.LOGGER.info("[Compression] Loaded "
                + Compression.main.config.compressableItems.size() + " item subtypes.");
        Compression.LOGGER.info("[Compression] Loaded "
                + Compression.main.config.compressableBlocks.size() + " block subtypes.");

        for (ItemStack stack : Compression.main.config.compressableItems)
        {
            ProxyCommon.registerRecipesFor(stack, ItemCompressed.SUBTYPES);
        }
        Compression.LOGGER.info("[Compression] Loaded " + ItemCompressed.SUBTYPES.size() + " item subtypes variants ("
                + Compression.main.config.compressableItems.size() + " * " + EnumTier.values().length + " tiers).");

        for (ItemStack stack : Compression.main.config.compressableBlocks)
        {
            ProxyCommon.registerRecipesFor(stack, BlockCompressed.SUBTYPES);
        }
        Compression.LOGGER.info("[Compression] Loaded " + BlockCompressed.SUBTYPES.size() + " block subtypes variants ("
                + Compression.main.config.compressableBlocks.size() + " * " + EnumTier.values().length + " tiers).");

        Compression.main.config.save();
    }

    private static void registerRecipesFor(ItemStack stack, NonNullList<ItemStack> compressedStacks)
    {
        String stackName = CompressedStack.getNameOf(stack, true, true);
        ItemStack prevStack = stack;
        EnumTier prevTier = null;
        for (EnumTier tier : EnumTier.values()) {
            ItemStack compressedStack = CompressedStack.create(stack, tier);

            // add to subtypes
            compressedStacks.add(compressedStack);

            String prevTierStr = prevTier == null ? "NULL" : prevTier.getName();
            String prevToTier = String.format("%s>%s", prevTierStr, tier.getName());
            String tierToPrev = String.format("%s<%s", tier.getName(), prevTierStr);

            // add compress recipe
            RecipeNToN.createNToOne(new ResourceLocation(stackName, prevToTier), prevStack, compressedStack, 9);
            // add decompress recipe
            RecipeNToN.createOneToN(new ResourceLocation(stackName, tierToPrev), compressedStack, prevStack, 9);

            // mark previous
            prevStack = compressedStack;
            prevTier = tier;
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Compression.main.blocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Compression.main.blocks.registerItems(event.getRegistry());
        Compression.main.items.registerItems(event.getRegistry());
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Compression.guidIdCompressor)
        {
            return new ContainerCompressor(player.inventory, (TileCompressor)world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

}
