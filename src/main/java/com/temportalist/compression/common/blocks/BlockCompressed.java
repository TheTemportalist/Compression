package com.temportalist.compression.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCompressed extends BlockBase {

    public BlockCompressed() {
        super(Material.GROUND, "compressed");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this.item,
                0, new ModelResourceLocation(this.getRegistryName(), "inventory")
        );
    }

}
