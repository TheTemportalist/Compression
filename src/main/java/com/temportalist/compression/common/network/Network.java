package com.temportalist.compression.common.network;

import com.temportalist.compression.common.Compression;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Network {

    public final SimpleNetworkWrapper NETWORK;

    public Network() {
        this.NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Compression.MOD_ID);
        this.NETWORK.registerMessage(MessageCompressor.Handler.class, MessageCompressor.class, 0, Side.SERVER);
    }

}
