package com.temportalist.compression.common;

import com.temportalist.compression.common.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Compression.MOD_ID, name = Compression.MOD_NAME, version = Compression.MOD_VERSION)
public class Compression
{

    public static final String MOD_ID = "compression";
    public static final String MOD_NAME = "Compression";
    public static final String MOD_VERSION = "1.0";
    public static final String proxyClient = "com.temportalist.compression.client.ProxyClient";
    public static final String proxyServer = "com.temportalist.compression.server.ProxyServer";

    public static Logger LOGGER;

    @Mod.Instance
    public static Compression main;

    @SidedProxy(clientSide = Compression.proxyClient, serverSide = Compression.proxyServer)
    public static IProxy proxy;

    public CreativeTabs tabCompression;

    public ModBlocks blocks;

    @EventHandler
    public void initPre(FMLPreInitializationEvent event)
    {
        Compression.LOGGER = event.getModLog();
        Compression.proxy.initPre(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Compression.proxy.init(event);
    }

    @EventHandler
    public void initPost(FMLPostInitializationEvent event)
    {
        Compression.proxy.initPost(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {

    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {

    }

}
