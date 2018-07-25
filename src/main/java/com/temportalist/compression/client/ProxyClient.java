package com.temportalist.compression.client;

import com.google.common.collect.ImmutableList;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.ProxyCommon;
import com.temportalist.compression.common.blocks.BlockBase;
import com.temportalist.compression.common.blocks.TileCompressor;
import com.temportalist.compression.common.init.ModBlocks;
import com.temportalist.compression.common.init.ModItems;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ProxyClient extends ProxyCommon {

    @Override
    public void initPre(FMLPreInitializationEvent e) {
        super.initPre(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(Compression.main, this);

    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {

        ModelLoaderRegistry.registerLoader(new ModelLoaderCompressed());

        ModelLoader.setCustomModelResourceLocation(ModBlocks.compressed.item, 0, ModelLoaderCompressed.fakeRL);
        ModelLoader.setCustomStateMapper(ModBlocks.compressed, new StateMapperBase() {

            @Override
            protected ModelResourceLocation getModelResourceLocation (IBlockState state){
                return ModelLoaderCompressed.fakeRL;
            }

        });
        ModelLoader.setCustomModelResourceLocation(ModItems.compressed, 0, ModelLoaderCompressed.fakeRL);

        ModelLoader.setCustomModelResourceLocation(ModBlocks.compressor.item, 0, new ModelResourceLocation(
                new ResourceLocation(Compression.MOD_ID, "compressor"),  "compress"
        ));
        /*
        ModelLoader.setCustomModelResourceLocation(ModBlocks.compressor.item, 1, new ModelResourceLocation(
                new ResourceLocation(Compression.MOD_ID, "compressor"),  "decompress"
        ));
        //*/

    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Compression.guidIdCompressor)
        {
            return new GuiCompressor(player.inventory, (TileCompressor)world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}
