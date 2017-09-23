package com.temportalist.compression.common.items;

import com.temportalist.compression.common.Compression;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemBase extends Item {

    public ResourceLocation registryName;

    public ItemBase(String name) {
        super();

        this.registryName = new ResourceLocation(Compression.MOD_ID, name);

        this.setUnlocalizedName(this.registryName.getResourceDomain() + "." + this.registryName.getResourcePath());
        this.setRegistryName(name);

    }

    public void registerItem(IForgeRegistry<Item> registry) {
        registry.register(this);
    }

}
