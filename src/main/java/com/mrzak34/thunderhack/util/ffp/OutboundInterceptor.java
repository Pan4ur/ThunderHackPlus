package com.mrzak34.thunderhack.util.ffp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

/* Intercept packet sent by client to server */

@SideOnly(Side.CLIENT)
public class OutboundInterceptor extends NettyPacketEncoder {

    private final EnumPacketDirection direction;
    private final NetworkHandler handler;
    private boolean isPlay;

    public OutboundInterceptor(NetworkHandler handler, EnumPacketDirection direction) {
        super(direction);
        this.handler = handler;
        this.direction = direction; // let's save it twice
        this.isPlay = false;
    }

    protected void encode(ChannelHandlerContext context, Packet<?> packet, ByteBuf out) throws Exception {

        if (!this.isPlay) {
            EnumConnectionState state = context.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get();
            this.isPlay = (state == EnumConnectionState.PLAY);
        }

        if (this.isPlay) {
            int id = context.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get().getPacketId(this.direction, packet);

            packet = this.handler.packetReceived(this.direction, id, packet, null);

            if (packet == null) return;
        }

        super.encode(context, packet, out);
    }

}