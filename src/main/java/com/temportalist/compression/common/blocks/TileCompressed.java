package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.init.ModBlocks;
import com.temportalist.compression.common.lib.BlockProperties;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;

public class TileCompressed extends TileEntity {

    private ItemStack sampleStack;
    private EnumTier tier;

    public void setTypeFrom(ItemStack stackCompressed) {
        this.sampleStack = CompressedStack.createSampleStack(stackCompressed);
        this.tier = CompressedStack.getTier(stackCompressed);
        this.markDirty();
    }

    public ItemStack getSampleStack() {
        return this.sampleStack;
    }

    public EnumTier getTier() {
        return tier;
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
        if (this.sampleStack != null) {
            tagCom.setString("stack", CompressedStack.getNameOf(this.sampleStack, true, true));
        }
        tagCom.setInteger("tier", this.tier.ordinal());
        return tagCom;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("stack")) {
            this.sampleStack = CompressedStack.createItemStack(compound.getString("stack"));
        }
        this.tier = EnumTier.getTier(compound.getInteger("tier"));
    }

    @Override
    public void onLoad() {
        // Tile Entity is loading, check if we need to be ticking
        ModBlocks.compressed.onTileLoaded(this.getWorld(), this.getPos(), this);
    }

    public IExtendedBlockState writeExtendedBlockState(IExtendedBlockState in) {
        return in
                .withProperty(BlockProperties.ITEMSTACK_UN, this.sampleStack)
                .withProperty(BlockProperties.TIER_UN, this.tier == null ? -1 : this.tier.ordinal());
    }

}
