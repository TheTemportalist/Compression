package com.temportalist.compression.common.init;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.items.ItemCompressed;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static ItemCompressed compressed;

    public void initPre() {
        ModItems.compressed = new ItemCompressed();
        ModItems.compressed.setCreativeTab(Compression.main.tabCompression);
    }

    public void registerItems(IForgeRegistry<Item> registry) {
        ModItems.compressed.registerItem(registry);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModItems.compressed.registerModel();
    }

}
