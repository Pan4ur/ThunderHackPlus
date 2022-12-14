package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.misc.Timer;
import com.mrzak34.thunderhack.modules.player.PacketRender;
import com.mrzak34.thunderhack.util.MovementUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mrzak34.thunderhack.util.ItemUtil.mc;

@Mixin(value = {NetworkManager.class})
public class MixinNetworkManager {


    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if(packet instanceof CPacketPlayer.PositionRotation && chto()) {
            Thunderhack.moduleManager.getModuleByClass(Timer.class).m();
        }
        if (event.isCanceled()) {
            info.cancel();
        } else {
            if (event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                PacketRender.setYaw(((CPacketPlayer) event.getPacket()).getYaw(0));
                PacketRender.setPitch(((CPacketPlayer) event.getPacket()).getPitch(0));
            }
        }
    }

    @Inject(method = {"channelRead0"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.Receive event = new PacketEvent.Receive(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }else if (!event.getPostEvents().isEmpty())
        {

            for (Runnable runnable : event.getPostEvents())
            {
                Minecraft.getMinecraft().addScheduledTask(runnable);
            }

            info.cancel();
        }
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "RETURN")}, cancellable = true)
    private void onSendPacketPost(Packet<?> packet, CallbackInfo info) {
        PacketEvent.SendPost event = new PacketEvent.SendPost(1, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }


    @Inject(method = {"channelRead0"}, at = {@At(value = "RETURN")}, cancellable = true)
    private void onChannelReadPost(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(1, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onSendPacketPost2(Packet<?> packet, CallbackInfo info) {
        PacketEvent.SendPost event = new PacketEvent.SendPost(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }


    @Inject(method = {"channelRead0"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onChannelReadPost2(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }


    boolean chto(){
        return Thunderhack.moduleManager.getModuleByClass(Timer.class).isDisabled() && !MovementUtil.isMoving();
    }
}

