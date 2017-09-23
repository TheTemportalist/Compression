package com.temportalist.compression.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

public class ItemListCompressed extends ItemOverrideList {

    TextureAtlasSprite[] overlays;

    public ItemListCompressed(TextureAtlasSprite[] overlays) {
        super(Lists.newArrayList());
        this.overlays = overlays;
    }

    public static IBakedModel getMissingModel() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        if (!stack.hasTagCompound()) return originalModel;

        ItemStack sampleStack = CompressedStack.createSampleStack(stack);
        boolean isBlock = stack.getItem() instanceof ItemBlock;

        IBakedModel sampleModel = isBlock ?
                Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(CompressedStack.createSampleState(stack)) :
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(sampleStack);

        int i = CompressedStack.getTier(stack).ordinal();
        TextureAtlasSprite overlay = overlays[i];

        return new IBakedModel() {

            @Override
            public TextureAtlasSprite getParticleTexture() {
                return sampleModel.getParticleTexture();
            }

            @Override
            public boolean isBuiltInRenderer() {
                return false;
            }

            @Override
            public boolean isAmbientOcclusion() {
                return true;
            }

            @Override
            public boolean isGui3d() {
                return isBlock;
            }

            @Override
            public ItemOverrideList getOverrides() {
                return sampleModel.getOverrides();
            }

            @Override
            public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
                ArrayList<BakedQuad> quadList = new ArrayList<>();

                try {
                    List<BakedQuad> quads = sampleModel.getQuads(state, side, rand);
                    if (quads != null) quadList.addAll(quads);
                    if (overlay != null) {
                        IBakedModel overlayModel = new Builder(state, sampleModel, overlay, BlockPos.ORIGIN).makeBakedModel();
                        quads = overlayModel.getQuads(state, side, rand);
                        if (quads != null) quadList.addAll(quads);
                    }
                }
                catch(Exception e) {
                    Compression.LOGGER.error("Error merging render models. " +
                            "Please report this to https://github.com/TheTemportalist/Compression/issues. " +
                            "As a temporary fix, you can consider adding \'" +
                            (sampleStack.getItem().getRegistryName().toString()) +
                            "\' or \'" +
                            (sampleStack.getItem().getRegistryName().toString() + ":" + sampleStack.getItemDamage()) +
                            "\' to the blacklist configuration option.");
                    e.printStackTrace();
                }

                return quadList;
            }

            @Override
            public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
                ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> map =
                        isBlock ? TransformHelper.BLOCK : TransformHelper.ITEM;
                return PerspectiveMapWrapper.handlePerspective(
                        this, map, cameraTransformType
                );
            }

        };
    }

}
