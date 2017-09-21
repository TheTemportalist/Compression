package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.Compression;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBase extends Block {

    public ResourceLocation registryName;
    public ItemBlock item;

    public BlockBase(Material materialIn, String name) {
        super(materialIn);

        this.registryName = new ResourceLocation(Compression.MOD_ID, name);

        this.setUnlocalizedName(this.registryName.getResourceDomain() + "." + this.registryName.getResourcePath());
        this.setRegistryName(this.registryName);

        this.item = new ItemBlock(this);

    }

    @Override
    public Block setCreativeTab(CreativeTabs tab) {
        this.item.setCreativeTab(tab);
        return super.setCreativeTab(tab);
    }

    public void registerBlock(IForgeRegistry<Block> registry) {
        registry.register(this);
    }

    public void registerItem(IForgeRegistry<Item> registry) {
        registry.register(this.item.setRegistryName(this.registryName));
    }

}
