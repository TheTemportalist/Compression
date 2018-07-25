package com.temportalist.compression.common.init;

import com.google.common.collect.ImmutableList;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    @GameRegistry.ObjectHolder("compression:compressedBlock")
    public static BlockCompressed compressed;

    @GameRegistry.ObjectHolder("compression:compressor")
    public static BlockCompressor compressor;

    public void initPre() {

        GameRegistry.registerTileEntity(TileCompressed.class, "Compressed");
        GameRegistry.registerTileEntity(TileCompressedTickable.class, "CompressedTick");

        GameRegistry.registerTileEntity(TileCompressor.class, "Compressor");

        ModBlocks.compressed = new BlockCompressed();
        ModBlocks.compressed.setCreativeTab(Compression.main.tabCompression);
        ModBlocks.compressor = new BlockCompressor();
        ModBlocks.compressor.setCreativeTab(Compression.main.tabCompression);

    }

    public void registerBlocks(IForgeRegistry<Block> registry) {
        ModBlocks.compressed.registerBlock(registry);
        ModBlocks.compressor.registerBlock(registry);
    }

    public void registerItems(IForgeRegistry<Item> registry) {
        ModBlocks.compressed.registerItem(registry);
        ModBlocks.compressor.registerItem(registry);
    }

}
