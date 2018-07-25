package com.temportalist.compression.common;

import com.temportalist.compression.common.blocks.TileCompressor;
import com.temportalist.compression.common.config.Config;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.init.ModBlocks;
import com.temportalist.compression.common.init.ModEntity;
import com.temportalist.compression.common.init.ModItems;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ProxyCommon implements IProxy, IGuiHandler {

    @Override
    public void initPre(FMLPreInitializationEvent event) {

        Compression.main.config = new Config(event.getModConfigurationDirectory());
        Compression.main.config.initPre();

        Compression.main.tabCompression = new CreativeTabs(Compression.MOD_ID) {
            @Override
            public ItemStack getTabIconItem() {
                return CompressedStack.create(new ItemStack(Blocks.COBBLESTONE), EnumTier.QUADRUPLE);
            }
        };

        Compression.main.blocks = new ModBlocks();
        Compression.main.blocks.initPre();

        Compression.main.items = new ModItems();
        Compression.main.items.initPre();

        Compression.main.entity = new ModEntity();
        Compression.main.entity.initPre();

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
        Compression.main.items.registerItems(event.getRegistry());
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Compression.guidIdCompressor)
        {
            return new ContainerCompressor(player.inventory, (TileCompressor)world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

}
