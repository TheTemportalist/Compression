package com.temportalist.compression.common.init;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.items.ItemCompressed;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    @GameRegistry.ObjectHolder("compression:compressedItem")
    public static ItemCompressed compressed;

    public void initPre() {
        ModItems.compressed = new ItemCompressed();
        ModItems.compressed.setCreativeTab(Compression.main.tabCompression);
    }

    public void registerItems(IForgeRegistry<Item> registry) {
        ModItems.compressed.registerItem(registry);
    }

}
