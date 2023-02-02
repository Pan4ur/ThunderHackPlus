package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.player.PacketRender;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mrzak34.thunderhack.modules.Feature.fullNullCheck;


@Mixin(value = {NetworkManager.class})
public class MixinNetworkManager {

    float PrevYaw, PrevPitch;

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        } else {
            if (event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation) {

                PacketRender.setYaw(((CPacketPlayer) event.getPacket()).getYaw(0));
                PacketRender.setPitch(((CPacketPlayer) event.getPacket()).getPitch(0));

                PacketRender.setPrevYaw(PrevYaw);
                PacketRender.setPrevYaw(PrevPitch);

                PrevYaw = ((CPacketPlayer) event.getPacket()).getYaw(0);
                PrevPitch = ((CPacketPlayer) event.getPacket()).getPitch(0);
            }
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
        if(!fullNullCheck()) {
            PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(packet);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                info.cancel();
            }
            if (!event.getPostEvents().isEmpty()) {
                for (Runnable runnable : event.getPostEvents()) {
                    Minecraft.getMinecraft().addScheduledTask(runnable);
                }
            }
        }
    }
    @Inject(method = {"channelRead0"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        if(!fullNullCheck()) {
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

