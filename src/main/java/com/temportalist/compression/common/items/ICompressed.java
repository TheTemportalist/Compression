package com.temportalist.compression.common.items;

import com.temportalist.compression.common.effects.Effects;
import com.temportalist.compression.common.entity.EntityCompressed;
import com.temportalist.compression.common.init.CompressedStack;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public interface ICompressed {

    default String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) return CompressedStack.getDisplayNameFor(stack);
        else return null;
    }

    default void addInfo(ItemStack stack, List<String> tooltip) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(CompressedStack.getStackName(stack));
            tooltip.add(Long.toString(CompressedStack.getTier(stack).getSizeMax()));
        }
    }

    default boolean hasEntityCompressed() {
        return true;
    }

    default Entity createEntityCompressed(World world, Entity entity, ItemStack stack) {
        return new EntityCompressed(world,
                entity.getPositionVector(),
                new Vec3d(entity.motionX, entity.motionY, entity.motionZ),
                stack
        );
    }

    default void onUpdate(World world, Entity entity, ItemStack stack) {
        Effects.onUpdateEntityInventory(world, entity, stack);
    }

}
