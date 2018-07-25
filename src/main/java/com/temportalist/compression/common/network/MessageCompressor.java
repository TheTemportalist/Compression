package com.temportalist.compression.common.network;

import com.temportalist.compression.common.blocks.TileCompressor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCompressor implements IMessage {

    private BlockPos _pos;

    public MessageCompressor(BlockPos pos) {
        this._pos = pos;
    }

    public MessageCompressor() {
        this(new BlockPos(0, 0, 0));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this._pos.getX());
        buf.writeInt(this._pos.getY());
        buf.writeInt(this._pos.getZ());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this._pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static class Handler implements IMessageHandler<MessageCompressor, IMessage>
    {
        @Override
        public IMessage onMessage(MessageCompressor message, MessageContext ctx) {
            WorldServer world = ctx.getServerHandler().player.getServerWorld();
            BlockPos pos = message._pos;
            world.addScheduledTask(() -> {
                TileCompressor tile = (TileCompressor)world.getTileEntity(pos);
                if (tile != null) {
                    tile.onChangeMode();
                }
            });
            // No response packet
            return null;
        }
    }

}
