package com.mrzak34.thunderhack.util.ffp;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import io.netty.buffer.ByteBuf;

/* A class listening for network packets, register a listener on NetworkHandler */

@SideOnly(Side.CLIENT)
public interface PacketListener {

    public Packet<?> packetReceived(EnumPacketDirection direction, int id, Packet<?> packet, ByteBuf in);

}