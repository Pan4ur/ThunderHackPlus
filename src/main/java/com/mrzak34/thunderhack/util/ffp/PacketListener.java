package com.mrzak34.thunderhack.util.ffp;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/* A class listening for network packets, register a listener on NetworkHandler */

@SideOnly(Side.CLIENT)
public interface PacketListener {

    Packet<?> packetReceived(EnumPacketDirection direction, int id, Packet<?> packet, ByteBuf in);

}