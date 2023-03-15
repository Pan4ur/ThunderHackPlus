package com.mrzak34.thunderhack.util.ffp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

/* Intercept packet sent by server to client */

@SideOnly(Side.CLIENT)
public class InboundInterceptor extends NettyPacketDecoder {

    private final EnumPacketDirection direction;
    private final NetworkHandler handler;
    private boolean isPlay;

    public InboundInterceptor(NetworkHandler handler, EnumPacketDirection direction) {
        super(direction);
        this.handler = handler;
        this.direction = direction; // let's save it twice
        this.isPlay = false;
    }

    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {

            int start_index = in.readerIndex(); // Mark start index
            super.decode(context, in, out); // Computer packet

            if (!this.isPlay) { // don't go fetch the attr every time
                EnumConnectionState state = context.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get();
                this.isPlay = (state == EnumConnectionState.PLAY);
            }

            if (this.isPlay && out.size() > 0) {
                Packet packet = (Packet) out.get(0);
                int id = context.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get().getPacketId(this.direction, packet);
                int end_index = in.readerIndex();

                in.readerIndex(start_index);
                packet = this.handler.packetReceived(this.direction, id, packet, in);
                in.readerIndex(end_index);

                if (packet == null) out.clear();
                else out.set(0, packet);
            }
        }
    }

}