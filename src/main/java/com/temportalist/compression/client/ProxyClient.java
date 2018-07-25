package com.temportalist.compression.client;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.ProxyCommon;
import com.temportalist.compression.common.blocks.TileCompressor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
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

        Compression.main.blocks.registerModels();
        Compression.main.items.registerModels();

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
