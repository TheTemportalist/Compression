package com.temportalist.compression.common.items;

import com.temportalist.compression.client.ModelLoaderCompressed;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.init.ModItems;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCompressed extends ItemBase implements ICompressed {

    public static final NonNullList<ItemStack> SUBTYPES = NonNullList.create();

    public ItemCompressed() {
        super("compressedItem");
    }

    @Override
    public void registerModel()
    {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelLoaderCompressed.fakeRL);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.addAll(SUBTYPES);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return this.getDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        this.addInfo(stack, tooltip);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return this.hasEntityCompressed();
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return this.createEntityCompressed(world, location, itemstack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        this.onUpdate(worldIn, entityIn, stack);
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        return CompressedStack.getMinimizedTag(stack);
    }

}
