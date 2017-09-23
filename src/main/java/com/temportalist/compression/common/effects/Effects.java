package com.temportalist.compression.common.effects;

import com.temportalist.compression.common.entity.EntityCompressed;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class Effects {

    public static ResourceLocation portalSound = new ResourceLocation("entity.endermen.teleport");

    public static boolean canUse(ItemStack stack, EnumEffects... effects) {
        if (!CompressedStack.isCompressed(stack)) return false;
        for (EnumEffects effect : effects) {
            if (effect == null || !effect.canDoEffect(stack)) return false; // effects tier set via config
        }
        return true;
    }

    public static boolean doTickCompressedBlock(ItemStack stack) {
        return Effects.canUse(stack, EnumEffects.BLACK_HOLE);
    }

    public static void onUpdateEntityItem(EntityCompressed entity, World world, ItemStack stack) {

    }

    public static void onUpdateEntityInventory(World world, Entity entity, ItemStack stack) {
        if (!entity.isSneaking()) {

        }
    }

    @SubscribeEvent
    public void itemPickup(EntityItemPickupEvent event){
        EntityPlayer player = event.getEntityPlayer();
        EntityItem entity = event.getItem();
        if (player == null || entity == null) return;

        ItemStack entityStack = entity.getItem();
        ItemStack entityStackSample = null;
        EnumTier entityTier = null;
        if (CompressedStack.isCompressed(entityStack)) {
            entityStackSample = CompressedStack.createSampleStack(entityStack);
            entityTier = CompressedStack.getTier(entityStack);
        }
        else {
            entityStackSample = entityStack.copy();
            // tier stays null
        }

        int indexCompressor = this.matchItemStack(player, entityStackSample,
                (Tuple<ItemStack, Integer> invStack, Boolean isCompressed) ->
                        isCompressed && canUse(invStack.getFirst(), EnumEffects.COMPRESSOR)
        );

        if (indexCompressor >= 0) {
            event.setCanceled(true);
            event.getItem().setDead();
            this.compressInventory(player, entityStackSample, entityTier);
        }

    }

    public int matchItemStack(EntityPlayer player, ItemStack sampleIn, BiFunction<Tuple<ItemStack, Integer>, Boolean, Boolean> onFound) {
        int slotMin = 0, slotMax = 36 + 4 + 1; // 36 = main, 4 = armor, 1 = offhand
        for (int slot = slotMin; slot < slotMax; slot++) {
            ItemStack stackInSlot = player.inventory.getStackInSlot(slot);
            if (stackInSlot != ItemStack.EMPTY) {
                boolean isCompressed = CompressedStack.isCompressed(stackInSlot);
                ItemStack sample = isCompressed ? CompressedStack.createSampleStack(stackInSlot) : stackInSlot.copy();
                if (sample != null) {
                    boolean sameItem = sample.getItem() == sampleIn.getItem();
                    boolean sameDamage = sample.getItemDamage() == sampleIn.getItemDamage();
                    boolean sameTag =  ItemStack.areItemStackTagsEqual(sample, sampleIn);
                    if (sameItem && sameDamage && sameTag) {
                        if (onFound.apply(new Tuple<>(stackInSlot, slot), isCompressed)) {
                            return slot;
                        }
                    }
                }
            }
        }
        return -1;
    }

    public void compressInventory(EntityPlayer player, ItemStack stackIn, EnumTier tierIn) {

        ArrayList<ItemStack> alikeStacks = new ArrayList<>();

        this.matchItemStack(player, stackIn,
                (Tuple<ItemStack, Integer> invStack, Boolean isCompressed) -> {
                    alikeStacks.add(player.inventory.removeStackFromSlot(invStack.getSecond()));
                    return false;
                }
        );

        int totalCount = 0;
        for (ItemStack stack : alikeStacks) {
            EnumTier tier = CompressedStack.getTier(stack);
            totalCount += stack.getCount() * (tier != null ? tier.getSizeMax() : 1);
        }

        List<ItemStack> stackList = CompressedStack.createStackList(
                CompressedStack.createSampleStack(stackIn), totalCount
        );
        for (ItemStack stack : stackList) {
            if (!player.inventory.addItemStackToInventory(stack)) {
                player.dropItem(stack, true, false);
            }
        }

        player.inventory.markDirty();
    }

}
