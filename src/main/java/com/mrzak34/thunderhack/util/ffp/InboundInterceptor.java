package com.mrzak34.thunderhack.util.ffp;

import net.minecraft.network.Packet;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.EnumConnectionState;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;

import java.lang.Exception;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.io.IOException;
import java.util.List;

/* Intercept packet sent by server to client */

@SideOnly(Side.CLIENT)
public class InboundInterceptor extends NettyPacketDecoder {

    private final EnumPacketDirection direction;
    private NetworkHandler handler;
    private boolean isPlay;

    public InboundInterceptor(NetworkHandler handler, EnumPacketDirection direction) {
        super(direction);
        this.handler = handler;
        this.direction = direction; // let's save it twice
        this.isPlay = false;
    }

    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws IOException, InstantiationException, IllegalAccessException, Exception {
        if (in.readableBytes() != 0) {

            int start_index = in.readerIndex(); // Mark start index
            super.decode(context, in, out); // Computer packet

            if(! this.isPlay) { // don't go fetch the attr every time
                EnumConnectionState state = (EnumConnectionState)(context.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get());
                this.isPlay = (state == EnumConnectionState.PLAY);
            }

            if(this.isPlay && out.size() > 0) {
                Packet packet = (Packet)out.get(0);
                int id = ((EnumConnectionState)context.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get()).getPacketId(this.direction, packet);
                int end_index = in.readerIndex();

                in.readerIndex(start_index);
                packet = this.handler.packetReceived(this.direction, id, packet, in);
                in.readerIndex(end_index);

                if(packet == null) out.clear();
                else out.set(0, packet);
            }
        }
    }

}