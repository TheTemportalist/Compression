package com.temportalist.compression.common.container;

import com.temportalist.compression.common.init.CompressedStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCompressor extends Container
{
    private final IInventory tile;
    private int cookTime;
    private int totalCookTime;

    private int slotIn, slotOut, slotInvMainCount, slotInvHotbarCount;

    public ContainerCompressor(InventoryPlayer playerInventory, IInventory compressor)
    {
        this.tile = compressor;

        this.addSlotToContainer(new Slot(compressor, slotIn = 0, 56, 35));
        this.addSlotToContainer(new SlotCompressorOutput(
                playerInventory.player, compressor, slotOut = 1, 116, 35));

        slotInvMainCount = 0;
        slotInvHotbarCount = 0;

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                slotInvMainCount++;
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
            slotInvHotbarCount++;
        }
    }

    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tile);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = this.listeners.get(i);

            if (this.cookTime != this.tile.getField(0))
            {
                icontainerlistener.sendWindowProperty(this, 0, this.tile.getField(0));
            }

            if (this.totalCookTime != this.tile.getField(1))
            {
                icontainerlistener.sendWindowProperty(this, 1, this.tile.getField(1));
            }
        }

        this.cookTime = this.tile.getField(0);
        this.totalCookTime = this.tile.getField(1);

        //Compression.LOGGER.info("Container: " + this.cookTime + "/" + this.totalCookTime);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.tile.setField(id, data);
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.tile.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slotObject = this.inventorySlots.get(index);
        if(slotObject!=null&&slotObject.getHasStack())
        {
            ItemStack itemstack1 = slotObject.getStack();
            itemstack = itemstack1.copy();
            int slotCount = 2;
            if(index < slotCount)
            {
                if(!this.mergeItemStack(itemstack1, slotCount, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(itemstack1, 0, slotCount, false))
            {
                return ItemStack.EMPTY;
            }

            if(itemstack1.isEmpty())
            {
                slotObject.putStack(ItemStack.EMPTY);
            }
            else
            {
                slotObject.onSlotChanged();
            }
        }

        return itemstack;
    }
}