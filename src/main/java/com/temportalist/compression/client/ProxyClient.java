package com.temportalist.compression.client;

import com.temportalist.compression.common.ProxyCommon;
import com.temportalist.compression.common.init.ModBlocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ProxyClient extends ProxyCommon {

    @Override
    public void initPre(FMLPreInitializationEvent e) {
        super.initPre(e);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.compressed.initModel();
    }

}
