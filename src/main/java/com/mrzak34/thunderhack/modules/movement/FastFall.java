package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.PostPlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastFall extends Module {
    public FastFall() {
        super("FastFall", "FastFall", Category.MOVEMENT);
    }

    /**
     * @author linustouchtips
     * @since 04/18/2022
     */

    private Setting<Mode> mode = this.register (new Setting<>("Mode", Mode.MOTION));
    public Setting<Float> speed = register(new Setting("Speed", 1F, 3f, 5f));
    public Setting<Float> shiftTicks = register(new Setting("Height", 2.0F, 1F, 2.5F));
    public Setting<Float> height = register(new Setting("Height", 0.0F, 2F, 10F));
    public Setting<Boolean> webs = this.register(new Setting<>("Webs", false));


    private boolean previousOnGround;

    private final Timer rubberbandTimer = new Timer();
    private final Timer strictTimer = new Timer();

    private int ticks;
    private boolean stop;

    @Override
    public void onTick() {
        previousOnGround = mc.player.onGround;
    }

    @Override
    public void onUpdate() {
        if (Jesus.isInLiquid() || mc.player.isOverWater() || mc.player.capabilities.isFlying || mc.player.isElytraFlying() || mc.player.isOnLadder()) {
            return;
        }
        if (mc.player.isInWeb && !webs.getValue()) {
            return;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
            return;
        }
        if (!rubberbandTimer.passedMs(1000)) {
            return;
        }
        if (mc.player.onGround) {
            if (mode.getValue().equals(Mode.MOTION)) {
                for (double fallHeight = 0; fallHeight < height.getValue() + 0.5; fallHeight += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, -fallHeight, 0)).isEmpty()) {
                        mc.player.motionY = -speed.getValue();
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPostPlayerUpdate(PostPlayerUpdateEvent event) {
        if (Jesus.isInLiquid() || mc.player.isOverWater() || mc.player.capabilities.isFlying || mc.player.isElytraFlying() || mc.player.isOnLadder()) {
            return;
        }
        if (mc.player.isInWeb && !webs.getValue()) {
            return;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (!rubberbandTimer.passedMs(1000)) {
            return;
        }
        if (mode.getValue().equals(Mode.PACKET)) {
            event.setCanceled(true);
            if (mc.player.motionY < 0 && (previousOnGround && !mc.player.onGround)) {
                for (double fallHeight = 0; fallHeight < height.getValue() + 0.5; fallHeight += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, -fallHeight, 0)).isEmpty()) {
                        if (strictTimer.passedMs(1000)) {
                            mc.player.motionX = 0;
                            mc.player.motionZ = 0;
                            event.setIterations(shiftTicks.getValue().intValue());
                            stop = true;
                            ticks = 0;
                            strictTimer.reset();
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMotion(EventMove event) {
        if (mode.getValue().equals(Mode.PACKET) && stop) {
            event.setCanceled(true);
            event.setX(0);
            event.setZ(0);
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
            ticks++;
            if (ticks > shiftTicks.getValue()) {
                stop = false;
                ticks = 0;
            }
        }
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if(fullNullCheck()) return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            rubberbandTimer.reset();
        }
    }

    public enum Mode {
        MOTION,
        PACKET,
    }
}
