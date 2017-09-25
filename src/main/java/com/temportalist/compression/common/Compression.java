package com.temportalist.compression.common;

import com.temportalist.compression.common.config.Config;
import com.temportalist.compression.common.init.ModBlocks;
import com.temportalist.compression.common.init.ModEntity;
import com.temportalist.compression.common.init.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Compression.MOD_ID, name = Compression.MOD_NAME, version = Compression.MOD_VERSION,
        guiFactory = Compression.guiFactory
)
public class Compression
{

    public static final String MOD_ID = "compression";
    public static final String MOD_NAME = "Compression";
    public static final String MOD_VERSION = "1.2.1";
    private static final String proxyClient = "com.temportalist.compression.client.ProxyClient";
    private static final String proxyServer = "com.temportalist.compression.server.ProxyServer";
    public static final String guiFactory = "com.temportalist.compression.client.config.FactoryConfig";

    public static Logger LOGGER;

    @Mod.Instance
    public static Compression main;

    @SidedProxy(clientSide = Compression.proxyClient, serverSide = Compression.proxyServer)
    private static IProxy proxy;

    public CreativeTabs tabCompression;

    public ModBlocks blocks;
    public ModItems items;
    public ModEntity entity;
    public Config config;

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
