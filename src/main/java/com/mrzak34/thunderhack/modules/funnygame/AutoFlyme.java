package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.events.AttackEvent;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.movement.Flight;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFlyme extends Module {
    public final Setting<Boolean> space = register(new Setting<>("OnlySpace", true));
    public final Setting<Boolean> instantSpeed = register(new Setting<>("InstantSpeed", true));
    public final Setting<Boolean> criticals = register(new Setting<>("criticals", true));
    public final Setting<Boolean> hover = register(new Setting<>("hover", false));
    public Setting<Float> hoverY = this.register(new Setting("hoverY", 0.228f, 0.0f, 1.0f, v -> hover.getValue()));


    private final Timer timer = new Timer();

    public AutoFlyme() {
        super("AutoFlyme", "Автоматически пишет /flyme", Category.FUNNYGAME);
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) return;
        mc.player.sendChatMessage("/flyme");
    }

    @Override
    public void onUpdate() {
        if (!mc.player.capabilities.isFlying && timer.passedMs(1000) && !mc.player.onGround && (!space.getValue() || mc.gameSettings.keyBindJump.isKeyDown())) {
            mc.player.sendChatMessage("/flyme");
            timer.reset();
        }
        if(hover.getValue() && mc.player.capabilities.isFlying && !mc.player.onGround && mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -hoverY.getValue(), 0.0)).isEmpty()){
            mc.player.motionY = -0.05f;
        }
    }

    boolean cancelSomePackets = false;

    @SubscribeEvent
    public void onAttack(AttackEvent attackEvent){
        if (criticals.getValue()) {
            if (attackEvent.getStage() == 0) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.3579E-6, mc.player.posZ, false));
                cancelSomePackets = true;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketPlayer) {
            if (cancelSomePackets) {
                cancelSomePackets = false;
                e.setCanceled(true);
            }
        }
    }




    @SubscribeEvent
    public void onUpdateWalkingPlayer(final EventSync event) {
        if (!instantSpeed.getValue() || !mc.player.capabilities.isFlying) return;
        final double[] dir = MathUtil.directionSpeed(1.05f);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }
    }

}
