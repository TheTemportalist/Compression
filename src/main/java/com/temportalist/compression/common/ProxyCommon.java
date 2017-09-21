package com.temportalist.compression.common;

import com.temportalist.compression.common.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ProxyCommon implements IProxy {

    @Override
    public void initPre(FMLPreInitializationEvent event) {

        Compression.main.tabCompression = new CreativeTabs(Compression.MOD_ID) {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(Items.APPLE);
            }
        };

        Compression.main.blocks = new ModBlocks();
        Compression.main.blocks.initPre();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void initPost(FMLPostInitializationEvent event){

    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Compression.main.blocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Compression.main.blocks.registerItems(event.getRegistry());
    }

}
