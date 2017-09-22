package com.temportalist.compression.common.blocks;

import com.google.common.collect.Lists;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.items.ItemCompressedBlock;
import com.temportalist.compression.common.lib.BlockProperties;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;

public class BlockCompressed extends BlockBase {

    public BlockCompressed() {
        super(Material.GROUND, "compressed");
    }

    @Override
    public ItemBlock createItemBlock() {
        return new ItemCompressedBlock(this);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
            new IProperty[]{},
            new IUnlistedProperty[] {
                BlockProperties.ITEMSTACK_UN, BlockProperties.LONG_UN
            }
        );
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileCompressed) {
                TileCompressed tileCompressed = (TileCompressed)tile;
                ItemStack tileStack = tileCompressed.getItemStack() != null ? tileCompressed.getItemStack().copy() : null;
                return ((IExtendedBlockState)state).withProperty(BlockProperties.ITEMSTACK_UN, tileStack).withProperty(BlockProperties.LONG_UN, tileCompressed.getSize());
            }
        }
        return state;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCompressed();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!stack.hasTagCompound()) return;

        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileCompressed) {
            TileCompressed tileCompressed = (TileCompressed)tile;
            tileCompressed.setTypeFrom(stack);
        }

    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (ItemStack stack : new ItemStack[]{
                new ItemStack(Blocks.COBBLESTONE),
                new ItemStack(Blocks.GRAVEL),
                new ItemStack(Blocks.SAND),
                new ItemStack(Blocks.DIRT)
        }) {
            for (EnumTier tier : EnumTier.values()) {
                items.add(CompressedStack.create(stack, false, tier));
            }
        }
    }

}
