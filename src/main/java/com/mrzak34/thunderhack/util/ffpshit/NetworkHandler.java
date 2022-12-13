package com.mrzak34.thunderhack.util.ffpshit;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.NettyPacketEncoder;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.util.List;



/* Handles network listeners */

@SideOnly(Side.CLIENT)
public class NetworkHandler {

    private boolean isConnected;
    private NetworkManager networkManager;

    private ReadWriteLock[] outbound_lock;
    private ReadWriteLock[] inbound_lock;
    private List<PacketListener>[] outbound_listeners;
    private List<PacketListener>[] inbound_listeners;

    public NetworkHandler() {
        this.isConnected = false;
        this.networkManager = null;
        this.outbound_listeners = new List[33];
        this.outbound_lock = new ReadWriteLock[33];

        for(int i = 0; i < 33; i ++) {
            this.outbound_lock[i] = new ReentrantReadWriteLock();
        }

        this.inbound_listeners = new List[80];
        this.inbound_lock = new ReadWriteLock[80];

        for(int i = 0; i < 80; i ++) {
            this.inbound_lock[i] = new ReentrantReadWriteLock();
        }
    }

    public Packet<?> packetReceived(EnumPacketDirection direction, int id, Packet<?> packet, ByteBuf buf) {
        List<PacketListener> listeners;
        ReadWriteLock lock;

        if(direction == EnumPacketDirection.CLIENTBOUND) {
            listeners = this.inbound_listeners[id];
            lock = this.inbound_lock[id];
        } else {
            listeners = this.outbound_listeners[id];
            lock = this.outbound_lock[id];
        }

        if(listeners != null) {
            int buff_start = 0;
            if(buf != null) buff_start = buf.readerIndex();

            lock.readLock().lock();
            int size = listeners.size(); // Get starting size, we assume that a listener can unregister itself & only itself
            lock.readLock().unlock();

            for(int i = 0; i < size; i ++) {
                lock.readLock().lock();
                PacketListener l = listeners.get(i - (size - listeners.size()));
                lock.readLock().unlock();

                if(buf != null) buf.readerIndex(buff_start);
                if((packet = l.packetReceived(direction, id, packet, buf)) == null) return null;
            }
        }

        return packet;
    }

    public void registerListener(EnumPacketDirection direction, PacketListener listener, int ... ids) {
        List<PacketListener>[] listeners;
        ReadWriteLock[] locks;

        if(direction == EnumPacketDirection.CLIENTBOUND) {
            listeners = this.inbound_listeners;
            locks = this.inbound_lock;
        } else {
            listeners = this.outbound_listeners;
            locks = this.outbound_lock;
        }

        for(int id : ids) {
            try {
                locks[id].writeLock().lock();

                if(listeners[id] == null) listeners[id] = new ArrayList<PacketListener>();
                if(! listeners[id].contains(listener)) { // Not twice
                    listeners[id].add(listener);
                }
            } finally {
                locks[id].writeLock().unlock();
            }
        }
    }

    public void unregisterListener(EnumPacketDirection direction, PacketListener listener) {
        List<PacketListener>[] listeners;
        ReadWriteLock[] locks;

        if(direction == EnumPacketDirection.CLIENTBOUND) {
            listeners = this.inbound_listeners;
            locks = this.inbound_lock;
        } else {
            listeners = this.outbound_listeners;
            locks = this.outbound_lock;
        }

        for(int i = 0; i < listeners.length; i ++) {
            try {
                locks[i].writeLock().lock();
                if(listeners[i] != null) {
                    listeners[i].remove(listener);
                    if(listeners[i].size() == 0) listeners[i] = null;
                }
            } finally {
                locks[i].writeLock().unlock();
            }
        }
    }

    public void unregisterListener(EnumPacketDirection direction, PacketListener listener, int ... ids) {
        List<PacketListener>[] listeners;
        ReadWriteLock[] locks;

        if(direction == EnumPacketDirection.CLIENTBOUND) {
            listeners = this.inbound_listeners;
            locks = this.inbound_lock;
        } else {
            listeners = this.outbound_listeners;
            locks = this.outbound_lock;
        }

        for(int id : ids) {
            try {
                locks[id].writeLock().lock();
                if(listeners[id] != null) {
                    listeners[id].remove(listener);
                    if(listeners[id].size() == 0) listeners[id] = null;
                }
            } finally {
                locks[id].writeLock().unlock();
            }
        }
    }

    public void sendPacket(Packet<?> packet) {
        if(this.networkManager != null) {
            this.networkManager.sendPacket(packet);
        }
    }

    public INetHandler getNetHandler() {
        if(this.networkManager != null) {
            return this.networkManager.getNetHandler();
        }
        return null;
    }

    public void disconnect() {
        if(this.networkManager != null) {
            this.networkManager.closeChannel(new TextComponentString("You have been successfully disconnected from server"));
        }
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if(!this.isConnected) {

            ChannelPipeline pipeline = event.getManager().channel().pipeline();

            try {
                // Install receive interception
                ChannelHandler old = pipeline.get("decoder");
                if(old != null && old instanceof NettyPacketDecoder) {
                    InboundInterceptor spoof = new InboundInterceptor(this, EnumPacketDirection.CLIENTBOUND);
                    pipeline.replace("decoder", "decoder", spoof);
                }

                // Install send interception
                old = pipeline.get("encoder");
                if(old != null && old instanceof NettyPacketEncoder) {
                    OutboundInterceptor spoof = new OutboundInterceptor(this, EnumPacketDirection.SERVERBOUND);
                    pipeline.replace("encoder", "encoder", spoof);
                }

                // Record NetworkManager
                this.networkManager = event.getManager();
                this.isConnected = true;
            } catch (java.util.NoSuchElementException e) {}
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.isConnected = false;
       // FamilyFunPack.getModules().onDisconnect();
       // FamilyFunPack.getMainGui().reset();
        this.networkManager = null;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

}