package com.temportalist.compression.client;

import com.temportalist.compression.common.Compression;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class ModelCompressed implements IModel {

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Arrays.asList();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ArrayList<ResourceLocation> overlays = new ArrayList<>();

        for (int i = 1; i <= 18; i++) {
            overlays.add(new ResourceLocation(Compression.MOD_ID, "overlays/overlay_" + i));
        }

        return overlays;
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        TextureAtlasSprite[] overlayList = new TextureAtlasSprite[18];
        for (int i = 0; i < overlayList.length; i++) {
            overlayList[i - 1] = bakedTextureGetter.apply(new ResourceLocation(
                    Compression.MOD_ID, "overlays/overlay_" + i
            ));
        }
        return new BakedCompressed(overlayList);
    }

}
