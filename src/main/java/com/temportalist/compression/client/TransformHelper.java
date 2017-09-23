package com.temportalist.compression.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

// Derived from
// https://github.com/InfinityRaider/InfinityLib/blob/fc989d0b4b0332ccf1b09da13a93d976b89b68ad/src/main/java/com/infinityraider/infinitylib/render/DefaultTransforms.java
public class TransformHelper {

    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    public static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> BLOCK = generateBlockTransform();
    public static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> ITEM = generateItemTransform();

    private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> generateBlockTransform() {
        final TRSRTransformation thirdperson = get(3.75f, 10f, 5f, 75, 45, 0, 0.375f);
        final ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> transform = new ImmutableMap.Builder<>();
        transform.put(ItemCameraTransforms.TransformType.GUI, get(15, 3.5f, 0, 30, 225, 0, 0.625f));
        transform.put(ItemCameraTransforms.TransformType.GROUND, get(6f, 6, 6, 0, 0, 0, 0.25f));
        transform.put(ItemCameraTransforms.TransformType.FIXED, get(0, 0, 0, 0, 0, 0, 0.5f));
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(3.5f, 4.8f, 8f, 0, 45, 0, 0.4f));
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(12.5f, 4.8f, 8f, 0, 225, 0, 0.4f));
        return transform.build();
    }

    private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> generateItemTransform() {
        final TRSRTransformation thirdperson = get(3.6f, 6.6f, 4.6f, 0, 0, 0, 0.55f);
        final TRSRTransformation firstperson = get(14.9f, 3.9f, 6.2f, 0, -90, 25, 0.68f);
        final ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> transform = new ImmutableMap.Builder<>();
        transform.put(ItemCameraTransforms.TransformType.GROUND, get(4, 6, 4, 0, 0, 0, 0.5f));
        transform.put(ItemCameraTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        transform.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
        transform.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
        return transform.build();
    }

    public static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
                new Vector3f(s, s, s),
                null
        );
    }

    private static TRSRTransformation leftify(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(
                flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX)
        );
    }

    public static final Matrix4f getBlockMatrix(ItemCameraTransforms.TransformType type) {
        if (BLOCK.containsKey(type)) {
            return BLOCK.get(type).getMatrix();
        }  else {
            return TRSRTransformation.identity().getMatrix();
        }
    }

    public static final Matrix4f getItemMatrix(ItemCameraTransforms.TransformType type) {
        if (ITEM.containsKey(type)) {
            return ITEM.get(type).getMatrix();
        } else {
            return TRSRTransformation.identity().getMatrix();
        }
    }

}
