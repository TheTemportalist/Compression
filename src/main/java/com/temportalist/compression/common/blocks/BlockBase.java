package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.Compression;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBase extends Block {

    public ResourceLocation registryName;
    public Item item;

    public BlockBase(Material materialIn, String name) {
        super(materialIn);

        this.registryName = new ResourceLocation(Compression.MOD_ID, name);

        this.setUnlocalizedName(this.registryName.getResourceDomain() + "." + this.registryName.getResourcePath());
        this.setRegistryName(name);

        this.item = this.createItemBlock().setRegistryName(this.getRegistryName());

    }

    public Item createItemBlock() {
        return new ItemBlock(this);
    }

    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        //this.item.setCreativeTab(tab);
        super.setCreativeTab(tab);
        return this;
    }

    public void registerBlock(IForgeRegistry<Block> registry) {
        registry.register(this);
    }

    public void registerItem(IForgeRegistry<Item> registry) {
        registry.register(this.item);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel() {
        ModelLoader.setCustomModelResourceLocation(this.item, 0, new ModelResourceLocation(this.registryName.toString(), "inventory"));
    }

}
