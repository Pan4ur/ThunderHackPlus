package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.combat.AutoTotem;
import com.mrzak34.thunderhack.util.Util;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mrzak34.thunderhack.util.Util.mc;


@Mixin(value = {NetworkManager.class})
public class MixinNetworkManager {

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "RETURN")}, cancellable = true)
    private void onSendPacketPost(Packet<?> packet, CallbackInfo info) {
        PacketEvent.SendPost event = new PacketEvent.SendPost(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"channelRead0"}, at = {@At(value = "RETURN")}, cancellable = true)
    private void onChannelReadPost(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        if (!(mc.player == null || mc.world == null)) {
            PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(packet);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                info.cancel();
            }
        }
    }

    @Inject(method = {"channelRead0"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        if (!(mc.player == null || mc.world == null)) {
            if (packet instanceof SPacketEntityStatus && ((SPacketEntityStatus) packet).getOpCode() == 35) {
                Entity entity = ((SPacketEntityStatus) packet).getEntity(Util.mc.world);
                if (entity != null && entity.equals(Util.mc.player)) {
                    AutoTotem.packet_latency_timer = System.currentTimeMillis();
                }
            }
            PacketEvent.Receive event = new PacketEvent.Receive(packet);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                info.cancel();
            } else if (!event.getPostEvents().isEmpty()) {
                for (Runnable runnable : event.getPostEvents()) {
                    Minecraft.getMinecraft().addScheduledTask(runnable);
                }
                info.cancel();
            }
        }
    }
}

