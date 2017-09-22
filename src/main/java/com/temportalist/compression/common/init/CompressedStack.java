package com.temportalist.compression.common.init;

import com.temportalist.compression.common.items.ICompressed;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.oredict.OreDictionary;

public class CompressedStack {

    public static ItemStack create(ItemStack itemStack, boolean withSize, EnumTier tier) {
        boolean isBlock = itemStack.getItem() instanceof ItemBlock;
        ItemStack compressed = new ItemStack(isBlock ? ModBlocks.compressed.item : ModItems.compressed, 1, 0);

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
        ItemStack stack = CompressedStack.create(itemStack, false, EnumTier.SINGLE);
        stack.getTagCompound().setLong("size", size);
        return stack;
    }

    public static boolean canCompressItem(ItemStack itemStack) {
        if (CompressedStack.isCompressed(itemStack)) {
            return false;
        }
        else if (itemStack.getItem() instanceof ItemBlock) {
            if (CompressedStack.isInBlackList(itemStack)) return false;
            Block block = Block.getBlockFromItem(itemStack.getItem());
            IBlockState state = block.getStateFromMeta(itemStack.getItemDamage());
            return state.getMaterial().isOpaque() && state.isFullCube() && !state.canProvidePower() &&
                    !block.hasTileEntity(state) && itemStack.getItem().getItemStackLimit(itemStack) > 1;
        }
        else {
            if (CompressedStack.isInBlackList(itemStack)) return false;
            return itemStack.getItem().getItemStackLimit(itemStack) > 1;
        }
    }

    public static boolean isInBlackList(ItemStack itemStack) {
        String registry = itemStack.getItem().getRegistryName().toString();
        return true;
        //return Options.blackList.contains(registry) ||
        //        Options.blackList.contains(registry + ":" + itemStack.getItemDamage());
    }

    public static boolean isCompressed(ItemStack itemStack) {
        return itemStack.getItem() instanceof ICompressed;
    }

    public static String getCompressedStackName(ItemStack itemStack) {
        return itemStack.getTagCompound().getString("name");
    }

    public static ItemStack createSampleStack(ItemStack itemStack) {
        return CompressedStack.createItemStack(CompressedStack.getCompressedStackName(itemStack));
    }

    public static IBlockState createSampleState(ItemStack itemStack) {
        return CompressedStack.createState(CompressedStack.getCompressedStackName(itemStack));
    }

    public static long getSize(ItemStack itemStack) {
        return itemStack.getTagCompound().getLong("size");
    }

    public static String getNameOf(ItemStack itemStack, boolean withModid, boolean withMeta) {
        ResourceLocation registry = itemStack.getItem().getRegistryName();
        String name = "";
        if (withModid) {
            name += registry.getResourceDomain() + ":";
        }
        name += registry.getResourcePath();
        if (withMeta) {
            name += ":" + itemStack.getItemDamage();
        }
        return name;
    }

    private static Tuple<ResourceLocation, Integer> getQualifiers(String name) {
        if (!name.matches("(.*):(.*)")) return null;
        int endNameIndex = name.length();
        int meta = OreDictionary.WILDCARD_VALUE;
        if (name.matches("(.*):(.*):(.*)")) {
            endNameIndex = name.lastIndexOf(':');
            meta = Integer.parseInt(name.substring(endNameIndex + 1, name.length()));
        }
        String modid = name.substring(0, name.indexOf(':'));
        String itemName = name.substring(name.indexOf(':') + 1, endNameIndex);
        return new Tuple<>(new ResourceLocation(modid, itemName), meta);
    }

    public static ItemStack createItemStack(String name) {
        Tuple<ResourceLocation, Integer> qualifiers = CompressedStack.getQualifiers(name);
        if (qualifiers == null) return null;

        Block block = Block.REGISTRY.getObject(qualifiers.getFirst());
        Item item = Item.REGISTRY.getObject(qualifiers.getFirst());

        if (block != null && Item.getItemFromBlock(block) != null) {
            return new ItemStack(block, 1, qualifiers.getSecond());
        }
        else if (item != null) {
            return new ItemStack(item, 1, qualifiers.getSecond());
        }
        else {
            return null;
        }
    }

    public static IBlockState createState(String name) {
        Tuple<ResourceLocation, Integer> qualifiers = CompressedStack.getQualifiers(name);
        if (qualifiers == null) return null;

        Block block = Block.REGISTRY.getObject(qualifiers.getFirst());
        if (block != null) {
            return block.getStateFromMeta(qualifiers.getSecond());
        }
        else {
            return null;
        }
    }

    public static String getDisplayNameFor(ItemStack itemStack) {
        return CompressedStack.getTier(itemStack).getName() + " Compressed " + itemStack.getTagCompound().getString("display");
    }

    public static EnumTier getTier(ItemStack stack) {
        return EnumTier.getTierForSize(CompressedStack.getSize(stack));
    }

    /*

    def getSampleFromUnknown(itemStack: ItemStack): ItemStack = {
        if (this.isCompressed(itemStack)) {
            if (itemStack.hasTagCompound) this.createSampleStack(itemStack)
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

    def getSize(itemStack: ItemStack): Long = itemStack.getTagCompound.getLong("size")

    */

}
