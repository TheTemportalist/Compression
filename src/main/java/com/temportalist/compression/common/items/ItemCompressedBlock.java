package com.temportalist.compression.common.items;

import com.temportalist.compression.common.init.CompressedStack;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemCompressedBlock extends ItemBlock implements ICompressed {

    public ItemCompressedBlock(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) return CompressedStack.getDisplayNameFor(stack);
        else return super.getItemStackDisplayName(stack);
    }

}
