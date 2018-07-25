package com.temportalist.compression.common.blocks;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;

public class BlockCompressor extends BlockBase {

    public static final PropertyBool PROPERTY_COMPRESS = PropertyBool.create("compress");

    public class ItemBlockCompressor extends ItemBlock {

        public ItemBlockCompressor(Block block)
        {
            super(block);
        }

        @Override
        public int getMetadata(int damage)
        {
            return damage;
        }

        @Override
        public String getUnlocalizedName(ItemStack stack)
        {
            return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
        }

    }

    public BlockCompressor() {
        super(Material.IRON, "compressor");
        this.setDefaultState(
                this.getBlockState().getBaseState()
                        .withProperty(PROPERTY_COMPRESS, Boolean.valueOf(true))
        );
    }

    @Override
    public Item createItemBlock()
    {
        return new ItemBlockCompressor(this);
    }

    @Override
    public void registerModel()
    {
        this.getBlockState().getValidStates().forEach(state -> {
            StringBuilder stringbuilder = new StringBuilder();
            Joiner.on(',').appendTo(stringbuilder, Iterables.transform(state.getProperties().entrySet(),
                    set -> set.getKey().getName() + "=" + set.getValue().toString()));

            ModelLoader.setCustomModelResourceLocation(
                    ModBlocks.compressor.item,
                    this.getMetaFromState(state),
                    new ModelResourceLocation(this.registryName, stringbuilder.toString())
            );
        });
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPERTY_COMPRESS);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PROPERTY_COMPRESS) ? 0 : 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PROPERTY_COMPRESS, meta == 0);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCompressor();
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityFurnace)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityFurnace)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileCompressor)
            {
                playerIn.openGui(Compression.main, Compression.guidIdCompressor, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }

            return true;
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this.item, 1, 0));
        items.add(new ItemStack(this.item, 1, 1));
    }

}
