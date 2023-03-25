package com.mrzak34.thunderhack.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.mixin.mixins.ICPacketPlayer;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.BackTrack;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TeleportBack extends Module {
    public TeleportBack() {
        super("TeleportBack", "включил отошел прыгнул-тепнуло туда где включал","Matrix only", Category.PLAYER);
    }
    private final Setting<Integer> reset = register(new Setting("ResetDistance", 30, 1, 256));
    private final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(-2009289807)));
    BackTrack.Box prev_pos;

    @SubscribeEvent
    public void onSync(EventSync event) {
        if(fullNullCheck()) return;
        mc.player.setSprinting(false);
        if(mc.gameSettings.keyBindJump.isKeyDown()){
            mc.player.motionY = 0.42f;
        }
        if(prev_pos != null && mc.player.getDistanceSq(prev_pos.getPosition().x,prev_pos.getPosition().y,prev_pos.getPosition().z) > reset.getValue() * reset.getValue()){
            NotificationManager.publicity("TeleportBack Ты отошел слишком далеко! сбрасываю позицию...",5, Notification.Type.ERROR);
            Command.sendMessage(ChatFormatting.RED + "TeleportBack Ты отошел слишком далеко! сбрасываю позицию...");
            prev_pos =  new BackTrack.Box(mc.player.getPositionVector(), 20, mc.player.limbSwing, mc.player.limbSwingAmount, mc.player.rotationYaw, mc.player.rotationPitch, mc.player);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer player = event.getPacket();
            ((ICPacketPlayer)player).setOnGround(false);
        }
    }

    @Override
    public void onEnable(){
        prev_pos =  new BackTrack.Box(mc.player.getPositionVector(), 20, mc.player.limbSwing, mc.player.limbSwingAmount, mc.player.rotationYaw, mc.player.rotationPitch, mc.player);
    }

    @SubscribeEvent
    public void onPreRenderEvent(PreRenderEvent event) {
        if(prev_pos == null) return;
        GlStateManager.pushMatrix();
        RenderUtil.renderEntity(prev_pos, prev_pos.getModelPlayer(), prev_pos.getLimbSwing(), prev_pos.getLimbSwingAmount(), prev_pos.getYaw(), prev_pos.getPitch(), prev_pos.getEnt(), color.getValue().getColorObject());
        GlStateManager.popMatrix();
    }
}
