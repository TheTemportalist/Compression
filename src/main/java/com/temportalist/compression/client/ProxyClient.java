package com.temportalist.compression.client;

import com.temportalist.compression.common.ProxyCommon;
import com.temportalist.compression.common.init.ModBlocks;
import com.temportalist.compression.common.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ProxyClient extends ProxyCommon {

    @Override
    public void initPre(FMLPreInitializationEvent e) {
        super.initPre(e);

        ModelLoaderRegistry.registerLoader(new ModelLoaderCompressed());

        //ModelLoader.setCustomModelResourceLocation(ModItems.compressed, 0, ModelLoaderCompressed.fakeRL);
        ModelLoader.setCustomModelResourceLocation(ModBlocks.compressed.item, 0, ModelLoaderCompressed.fakeRL);
        ModelLoader.setCustomStateMapper(ModBlocks.compressed, new StateMapperBase() {

            @Override
            protected ModelResourceLocation getModelResourceLocation (IBlockState state){
                return ModelLoaderCompressed.fakeRL;
            }

        });

    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {

    }

}
