package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.init.CompressedStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class TileCompressed extends TileEntity {

    private ItemStack itemStack;
    private long size;

    public void setTypeFrom(ItemStack stackCompressed) {
        this.itemStack = CompressedStack.createSampleStack(stackCompressed);
        this.size = CompressedStack.getSize(stackCompressed);
        this.markDirty();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public long getSize() {
        return size;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tagCom = new NBTTagCompound();
        this.writeToNBT(tagCom);
        return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tagCom);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCom = super.writeToNBT(compound);
        if (this.itemStack != null) {
            tagCom.setString("stack", CompressedStack.getNameOf(this.itemStack, true, true));
        }
        tagCom.setLong("size", this.size);
        return tagCom;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("stack")) {
            this.itemStack = CompressedStack.createItemStack(compound.getString("stack"));
        }
        this.size = compound.getLong("size");
    }

}
