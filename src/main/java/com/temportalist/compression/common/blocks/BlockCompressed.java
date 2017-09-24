package com.temportalist.compression.common.blocks;

import com.temportalist.compression.common.effects.Effects;
import com.temportalist.compression.common.effects.EnumEffect;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
        super(Material.GROUND, "compressedBlock");
    }

    @Override
    public ItemBlock createItemBlock() {
        return new ItemCompressedBlock(this);
    }

    // Called at init to create a default state
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

    /*
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = this.getDefaultState();
        // Check to see if the state is valid
        if (state instanceof IExtendedBlockState) {
            ItemStack stack = placer.getHeldItem(hand);
            // Return the proper state with ItemStack and tier
            return ((IExtendedBlockState)state)
                    .withProperty(BlockProperties.ITEMSTACK_UN, CompressedStack.createSampleStack(stack))
                    .withProperty(BlockProperties.TIER_UN, CompressedStack.getTier(stack).ordinal());
        }
        return state;
    }
    //*/

    // Return the extended state for rendering
    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extended = (IExtendedBlockState)state;

        // Get the tile entity
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileCompressed) {
            // Get the tile
            TileCompressed tile = (TileCompressed)tileEntity;
            return tile.writeExtendedBlockState(extended);
        }

        return super.getExtendedState(state, world, pos);
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

        // Get the tile entity
        TileEntity tile = worldIn.getTileEntity(pos);
        // Check if valid tile
        if (tile != null && tile instanceof TileCompressed) {
            // Get the tile
            TileCompressed tileCompressed = (TileCompressed)tile;
            // Set the type
            tileCompressed.setTypeFrom(stack); // stack is assumed to be a Compressed ItemStack (which this block is)

            EnumTier tier = CompressedStack.getTier(stack);
            this.updateBlockStats(worldIn, pos, tier, stack, 0);

        }

    }

    public void updateBlockStats(World worldIn, BlockPos pos, EnumTier tier, ItemStack stack, float energy) {

        float multiplier = tier.ordinal() + 1 + energy / 100f;
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
            this.setLightOpacity((int)(sampleState.getLightOpacity(worldIn, pos) * multiplier));
        }

    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileCompressedTickable) {
            TileCompressedTickable tile = (TileCompressedTickable)tileEntity;
            float energy = tile.getEnergyAmount();

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
