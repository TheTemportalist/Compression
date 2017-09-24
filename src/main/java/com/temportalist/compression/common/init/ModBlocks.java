package com.temportalist.compression.common.init;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.blocks.BlockCompressed;
import com.temportalist.compression.common.blocks.TileCompressed;
import com.temportalist.compression.common.blocks.TileCompressedTickable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    @GameRegistry.ObjectHolder("compression:compressedBlock")
    public static BlockCompressed compressed;

    public void initPre() {

        GameRegistry.registerTileEntity(TileCompressed.class, "Compressed");
        GameRegistry.registerTileEntity(TileCompressedTickable.class, "CompressedTick");

        ModBlocks.compressed = new BlockCompressed();
        ModBlocks.compressed.setCreativeTab(Compression.main.tabCompression);

    }

    public void registerBlocks(IForgeRegistry<Block> registry) {
        ModBlocks.compressed.registerBlock(registry);

    }

    public void registerItems(IForgeRegistry<Item> registry) {
        ModBlocks.compressed.registerItem(registry);

    }



}
