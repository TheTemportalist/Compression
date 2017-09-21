package com.temportalist.compression.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {

    void initPre(FMLPreInitializationEvent event);
    void init(FMLInitializationEvent event);
    void initPost(FMLPostInitializationEvent event);

}
