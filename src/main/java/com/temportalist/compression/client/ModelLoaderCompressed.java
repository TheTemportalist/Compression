package com.temportalist.compression.client;

import com.temportalist.compression.common.Compression;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class ModelLoaderCompressed implements ICustomModelLoader {

    public static final ModelResourceLocation fakeRL = new ModelResourceLocation(Compression.MOD_ID, "models/compressed");

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation == fakeRL;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new ModelCompressed();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

}
