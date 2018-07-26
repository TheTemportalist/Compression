package com.temportalist.compression.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

public class SlotCompressorOutput extends SlotFurnaceOutput {

    public SlotCompressorOutput(EntityPlayer player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
        super(player, inventoryIn, slotIndex, xPosition, yPosition);
    }

    @Override
    protected void onCrafting(ItemStack stack) {
    }

}
