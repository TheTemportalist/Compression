package com.temportalist.compression.common.init;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.blocks.BlockCompressed;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    @GameRegistry.ObjectHolder("compression:compressed")
    public BlockCompressed compressed;

    public void initPre() {

        this.compressed = new BlockCompressed();
        this.compressed.setCreativeTab(Compression.main.tabCompression);

    }

    public void registerBlocks(IForgeRegistry<Block> registry) {
        this.compressed.registerBlock(registry);

    }

    public void registerItems(IForgeRegistry<Item> registry) {
        this.compressed.registerItem(registry);

    }



}
