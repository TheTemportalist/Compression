package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.ContainerCompressor;
import com.temportalist.compression.common.init.CompressedStack;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

import static com.temportalist.compression.common.blocks.BlockCompressor.PROPERTY_COMPRESS;

public class TileCompressor extends TileEntityLockable implements ITickable, ICapabilityProvider
{

    private ItemStackHandler _inventory;
    private int cookTime;
    private int totalCookTime;
    private String customName;

    public TileCompressor() {
        this._inventory = new ItemStackHandler(2);
    }

    public boolean isDecompressing() {
        return !((BlockCompressor)this.getBlockType()).getStateFromMeta(this.getBlockMetadata()).getValue(PROPERTY_COMPRESS);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this._inventory.getSlots();
    }

    public boolean isEmpty() {
        for (int slot = 0; slot < this._inventory.getSlots(); slot++) {
            if (!this._inventory.getStackInSlot(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        return this._inventory.getStackInSlot(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        // take from ItemStackHelper.getAndSplit
        if (index >= 0 && index < this._inventory.getSlots()) {
            if (!this._inventory.getStackInSlot(index).isEmpty() && count > 0)
            {
                return this._inventory.getStackInSlot(index).splitStack(count);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack stack = this._inventory.getStackInSlot(index);
        return this._inventory.extractItem(index, stack.getCount(), false);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack itemstack = this._inventory.getStackInSlot(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this._inventory.setStackInSlot(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag)
        {
            this.totalCookTime = this.getCookTime(stack);
            this.cookTime = 0;
            this.markDirty();
        }
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : Compression.MOD_ID + ":container.compressor." + this.getBlockMetadata();
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this._inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.cookTime = compound.getInteger("CookTime");
        this.totalCookTime = compound.getInteger("CookTimeTotal");

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("inventory", this._inventory.serializeNBT());
        compound.setInteger("CookTime", (short)this.cookTime);
        compound.setInteger("CookTimeTotal", (short)this.totalCookTime);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }

        return compound;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Furnace isBurning
     */
    public boolean isBurning()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static boolean isBurning(IInventory inventory)
    {
        return inventory.getField(0) > 0;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        boolean hasUpdates = false;

        if (!this.world.isRemote)
        {
            if (this.canProcess())
            {
                ++this.cookTime;

                if (this.cookTime == this.totalCookTime)
                {
                    this.cookTime = 0;
                    this.totalCookTime = this.getCookTime(this._inventory.getStackInSlot(0));
                    this.processInput();
                    hasUpdates = true;
                }
            }
            else
            {
                this.cookTime = 0;
            }
        }

        if (hasUpdates)
        {
            this.markDirty();
        }
    }

    public int getCookTime(ItemStack stack)
    {
        return 200;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canProcess()
    {
        ItemStack input = this._inventory.getStackInSlot(0);
        if (input.isEmpty()) return false;

        int outCount;

        if (!this.isDecompressing())
        {
            outCount = 1;
            if (input.getCount() < 9
                    || !CompressedStack.canCompressedStack(input))
            {
                return false;
            }
        }
        else {
            outCount = 9;
            if (!CompressedStack.isCompressed(input))
            {
                return false;
            }
        }

        ItemStack nextCompression = this.isDecompressing()
                ? CompressedStack.getPrevCompression(input)
                : CompressedStack.getNextCompression(input);
        if (nextCompression.isEmpty())
        {
            return false;
        }

        ItemStack outStack = this._inventory.getStackInSlot(1);
        if (outStack.isEmpty())
        {
            return true;
        }

        // Sample stacks match (same type of compressed)
        if (!ItemStack.areItemStacksEqual(
                CompressedStack.createSampleStack(nextCompression),
                CompressedStack.createSampleStack(outStack)
        )) return false;

        // Tiers match
        if (CompressedStack.getTier(nextCompression) != CompressedStack.getTier(outStack)) return false;

        // Has room for the items generated
        if (outStack.getCount() + outCount <= this.getInventoryStackLimit() &&
                outStack.getCount() + outCount <= outStack.getMaxStackSize()) return true;

        return outStack.getCount() + outCount <= nextCompression.getMaxStackSize();
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void processInput()
    {
        if (this.canProcess())
        {
            ItemStack input = this._inventory.getStackInSlot(0);
            ItemStack output = this.isDecompressing()
                    ? CompressedStack.getPrevCompression(input)
                    : CompressedStack.getNextCompression(input);
            output.setCount(this.isDecompressing() ? 9 : 1);
            ItemStack outStack = this._inventory.getStackInSlot(1);

            if (outStack.isEmpty())
            {
                this._inventory.setStackInSlot(1, output.copy());
            }
            else
            {
                outStack.grow(output.getCount());
            }

            input.shrink(this.isDecompressing() ? 1 : 9);
        }
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index != 1 && CompressedStack.canCompressItem(stack);
    }

    public String getGuiID()
    {
        return Compression.MOD_ID + ":compressor";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerCompressor(playerInventory, this);
    }

    public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.cookTime;
            case 1:
                return this.totalCookTime;
            default:
                return 0;
        }
    }

    public void setField(int id, int value)
    {
        switch (id)
        {
            case 0:
                this.cookTime = value;
                break;
            case 1:
                this.totalCookTime = value;
                break;
            default:
                break;
        }
    }

    public int getFieldCount()
    {
        return 2;
    }

    public void clear()
    {
        this._inventory = new ItemStackHandler(this._inventory.getSlots());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T)this._inventory;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }
}