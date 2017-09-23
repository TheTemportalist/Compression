package com.temportalist.compression.common.items;

import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCompressed extends ItemBase implements ICompressed {

    public ItemCompressed() {
        super("compressedItem");
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != this.getCreativeTab()) return;
        for (ItemStack stack : new ItemStack[]{
                new ItemStack(Items.APPLE),
                new ItemStack(Items.POTATO),
                new ItemStack(Items.BOOK),
                new ItemStack(Items.COOKIE)
        }) {
            for (EnumTier tier : EnumTier.values()) {
                items.add(CompressedStack.create(stack, tier));
            }
        }
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

}
