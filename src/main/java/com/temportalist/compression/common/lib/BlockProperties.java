package com.temportalist.compression.common.lib;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by TheTemportalist on 4/15/2016.
 *
 * @author TheTemportalist
 */
public class BlockProperties {

    public static class UnlistedInteger implements IUnlistedProperty<Integer> {

        private String name;
        private int min, max;

        public UnlistedInteger(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isValid(Integer value) {
            return value >= min && value <= max;
        }

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public String valueToString(Integer value) {
            return String.valueOf(value);
        }

    }

    public static final IUnlistedProperty<Long> LONG_UN = new IUnlistedProperty<Long>() {

        @Override
        public String getName() {
            return "Long";
        }

        @Override
        public boolean isValid(Long value) {
            return true;
        }

        @Override
        public Class<Long> getType() {
            return Long.class;
        }

        @Override
        public String valueToString(Long value) {
            return value.toString();
        }

    };

    public static final IUnlistedProperty<ItemStack> ITEMSTACK_UN = new IUnlistedProperty<ItemStack>() {

        @Override
        public String getName() {
            return "ItemStack";
        }

        @Override
        public boolean isValid(ItemStack value) {
            return true;
        }

        @Override
        public Class<ItemStack> getType() {
            return ItemStack.class;
        }

        @Override
        public String valueToString(ItemStack value) {
            return value.getItem().getRegistryName().toString() + ":" + value.getItemDamage();
        }

    };

    public static final IUnlistedProperty<Integer> TIER_UN = new UnlistedInteger("EnumTier", EnumTier.getHead().ordinal(), EnumTier.getTail().ordinal());

}
