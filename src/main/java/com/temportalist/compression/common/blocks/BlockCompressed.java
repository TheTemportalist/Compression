package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.effects.Effects;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.items.ItemCompressedBlock;
import com.temportalist.compression.common.lib.BlockProperties;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

public class BlockCompressed extends BlockBase {

    public BlockCompressed() {
        super(Material.AIR, "compressedBlock");
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
                // a property for the ItemStack
                // and for the tier
                BlockProperties.ITEMSTACK_UN, BlockProperties.TIER_UN
            }
        );
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        // Check to see if the state is valid
        if (state instanceof IExtendedBlockState) {
            // Get the tile entity
            TileEntity tile = world.getTileEntity(pos);
            // Check to see if the tile is valid
            if (tile != null && tile instanceof TileCompressed) {
                // Get the tile
                TileCompressed tileCompressed = (TileCompressed)tile;
                // Get the ItemStack of the tile
                ItemStack tileStack = tileCompressed.getSampleStack() != null ? tileCompressed.getSampleStack().copy() : null;
                // Return the proper state with ItemStack and tier
                return ((IExtendedBlockState)state)
                        .withProperty(BlockProperties.ITEMSTACK_UN, tileStack)
                        .withProperty(BlockProperties.TIER_UN, tileCompressed.getTier().ordinal());
            }
        }
        // Return the bad state
        return state;
    }

    @Override
    public Material getMaterial(IBlockState state) {
        // Check to see if the state is valid
        if (state instanceof IExtendedBlockState) {
            IBlockState sampleState = CompressedStack.createState(
                    CompressedStack.getStackName(
                            ((IExtendedBlockState) state).getValue(BlockProperties.ITEMSTACK_UN)
                    )
            );
            if (sampleState != null) return sampleState.getMaterial();
        }
        return Material.AIR;
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

        if (Effects.doTickCompressedBlock(stack)) {
            worldIn.removeTileEntity(pos);
            worldIn.setTileEntity(pos, new TileCompressedTickable());
        }

        // Get the tile entity
        TileEntity tile = worldIn.getTileEntity(pos);
        // Check if valid tile
        if (tile != null && tile instanceof TileCompressed) {
            // Get the tile
            TileCompressed tileCompressed = (TileCompressed)tile;
            // Set the type
            tileCompressed.setTypeFrom(stack); // stack is assumed to be a Compressed ItemStack (which this block is)

            EnumTier tier = CompressedStack.getTier(stack);
            int multiplier = tier.ordinal() + 1;
            IBlockState sampleState = CompressedStack.createSampleState(stack);

            // Check state valid
            if (sampleState != null) {
                // Get block
                Block block = sampleState.getBlock();
                // Set information about block
                String tool = block.getHarvestTool(sampleState);
                if (tool != null) {
                    this.setHarvestLevel(tool, block.getHarvestLevel(sampleState));
                }
                this.setHardness(sampleState.getBlockHardness(worldIn, pos) * multiplier);
                this.setLightLevel(sampleState.getLightValue(worldIn, pos) * multiplier);
                this.setLightOpacity(sampleState.getLightOpacity(worldIn, pos) * multiplier);
            }

        }

    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != this.getCreativeTabToDisplayOn()) return;
        for (ItemStack stack : new ItemStack[]{
                new ItemStack(Blocks.COBBLESTONE),
                new ItemStack(Blocks.GRAVEL),
                new ItemStack(Blocks.SAND),
                new ItemStack(Blocks.DIRT)
        }) {
            for (EnumTier tier : EnumTier.values()) {
                items.add(CompressedStack.create(stack, tier));
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        // Get tile entity
        TileEntity tileEntity = world.getTileEntity(pos);
        // Check if valid tile entity
        if (tileEntity != null && tileEntity instanceof TileCompressed) {
            TileCompressed tile = (TileCompressed)tileEntity;
            return CompressedStack.create(tile.getSampleStack(), tile.getTier());
        }
        else {
            return CompressedStack.create(new ItemStack(Blocks.STONE), EnumTier.SINGLE);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        // Do not use fortune effects on this block
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (te != null && te instanceof TileCompressed) {
            TileCompressed tile = (TileCompressed)te;
            Block.spawnAsEntity(worldIn, pos, CompressedStack.create(tile.getSampleStack(), tile.getTier()));
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

}
