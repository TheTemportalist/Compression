package com.thetemportalist.compression.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import com.thetemportalist.compression.common.blocks.Blocks;

@Mod(modid = Compression.MODID, version = Compression.VERSION)
public class Compression
{

    public static final String MODID = "compression";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static Compression main;

    public Blocks blocks;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        this.blocks.init();
    }

}
