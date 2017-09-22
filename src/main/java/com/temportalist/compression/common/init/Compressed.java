package com.temportalist.compression.common.init;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Compressed {

    public static ItemStack create(ItemStack itemStack, boolean withSize, EnumTier tier) {
        boolean isBlock = itemStack.getItem() instanceof ItemBlock;
        ItemStack compressed = new ItemStack(isBlock ? ModBlocks.compressed else ModItems.compressed, 1, 0);

        NBTTagCompound tagCom = new NBTTagCompound();
        tagCom.setString("name", itemStack.getItem().getRegistryName().toString());
        tagCom.setString("display", itemStack.getItem().getItemStackDisplayName(itemStack));

        long size = withSize ? itemStack.getCount() : 1;
        if (tier != null) {
            size = tier.getSizeMax();
        }
        tagCom.setLong("size", size);

        compressed.setTagCompound(tagCom);

        return compressed;
    }

    public static ItemStack createWithSize(ItemStack itemStack, long size) {
        ItemStack stack = Compression.create(itemStack);
        stack.getTagCompound().setLong("size", size);
        return stack;
    }

    public static boolean canCompressItem(ItemStack itemStack) {
        if (Compressed.isCompressed(itemStack)) {
            return false;
        }
        else if (itemStack.getItem() instanceof ItemBlock) {
            if (Compressed.isInBlackList(itemStack)) return false;
            Block block = Block.getBlockFromItem(itemStack.getItem());
            IBlockState state = block.getStateFromMeta(itemStack.getItemDamage());
            return state.getMaterial().isOpaque() && state.isFullCube() && !state.canProvidePower() &&
                    !block.hasTileEntity(state) && itemStack.getItem().getItemStackLimit(itemStack) > 1;
        }
        else {
            if (Compressed.isInBlackList(itemStack)) return false;
            return itemStack.getItem().getItemStackLimit(itemStack) > 1;
        }
    }

    public static boolean isInBlackList(ItemStack itemStack) {
        String registry = itemStack.getItem().getRegistryName().toString();
        return Options.blackList.contains(registry) ||
                Options.blackList.contains(registry + ":" + itemStack.getItemDamage());
    }

    public static boolean isCompressed(ItemStack itemStack) {
        return itemStack.getItem() instanceof ICompressed;
    }

    /*

    def getSampleFromUnknown(itemStack: ItemStack): ItemStack = {
        if (this.isCompressed(itemStack)) {
            if (itemStack.hasTagCompound) this.getSampleStack(itemStack)
            else null
        }
        else {
            val stack = itemStack.copy()
            stack.stackSize = 1
            stack
        }
    }

    def getTotalSizeForUnknown(itemStack: ItemStack): Long = {
        itemStack.stackSize * (if (this.isCompressed(itemStack)) this.getSize(itemStack) else 1)
    }

    def getDisplayName(itemStack: ItemStack): String = {
        this.getTier(itemStack).getName + " Compressed " +
                itemStack.getTagCompound.getString("display")
    }

    def getStackName(itemStack: ItemStack): String = {
        itemStack.getTagCompound.getString("name")
    }

    def getSampleStack(itemStack: ItemStack): ItemStack = {
        Names.getItemStack(this.getStackName(itemStack))
    }

    def getSampleState(itemStack: ItemStack): IBlockState = {
        Names.getState(this.getStackName(itemStack))
    }

    def getSize(itemStack: ItemStack): Long = itemStack.getTagCompound.getLong("size")

    def getTier(itemStack: ItemStack): EnumTier = EnumTier.getTierForSize(this.getSize(itemStack))

    */

}
