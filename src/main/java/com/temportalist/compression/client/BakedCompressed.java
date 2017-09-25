package com.temportalist.compression.client;

import com.google.common.collect.Lists;
import com.temportalist.compression.common.lib.BlockProperties;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.List;
import com.google.common.collect.ImmutableMap;

public class BakedCompressed implements IBakedModel {

    private TextureAtlasSprite[] overlays;
    private ItemOverrideList overrideList;

    public BakedCompressed(TextureAtlasSprite[] overlays) {
        this.overlays = overlays;
        this.overrideList = new ItemListCompressed(overlays);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrideList;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extended = (IExtendedBlockState)state;

            ItemStack sampleStack = extended.getValue(BlockProperties.ITEMSTACK_UN);
            if (sampleStack == null) {
                return Lists.newArrayList();
            }

            Block sampleBlock = Block.getBlockFromItem(sampleStack.getItem());
            IBlockState sampleState = sampleBlock.getStateFromMeta(sampleStack.getItemDamage());
            IBakedModel sampleModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(sampleState);

            IBakedModel layerModel;
            BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
            if (layer == null) layerModel = sampleModel;
            else
            switch (layer) {
                case SOLID:
                    layerModel = sampleModel;
                    break;
                case TRANSLUCENT:
                    int i = EnumTier.getTier(extended.getValue(BlockProperties.TIER_UN)).ordinal();
                    layerModel = new Builder(sampleState, sampleModel, overlays[i], BlockPos.ORIGIN).makeBakedModel();
                    break;
                default:
                    layerModel = sampleModel;
                    break;
            }

            return layerModel.getQuads(state, side, rand);
        }
        return Lists.newArrayList();
    }

    @Override
    public boolean isAmbientOcclusion() { return true; }

    @Override
    public boolean isBuiltInRenderer() { return false; }

    @Override
    public boolean isGui3d() { return true; }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.overlays[this.overlays.length - 1];
    }

}
