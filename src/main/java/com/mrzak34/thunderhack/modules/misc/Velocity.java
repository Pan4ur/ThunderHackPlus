package com.mrzak34.thunderhack.modules.misc;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.PushEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity
        extends Module {

    public Setting<Boolean> onlyAura = register(new Setting<>("OnlyAura", false));
    public Setting<Boolean> ice = this.register(new Setting<>("Ice", false));
    public Setting<Boolean> autoDisable = this.register(new Setting<>("DisableOnVerify", false));
    private final Setting<modeEn> mode = register(new Setting("Mode", modeEn.Matrix));
    public Setting<Float> horizontal = this.register(new Setting<>("Horizontal", 0.0f, 0.0f, 100.0f, v -> mode.getValue() == modeEn.Custom));
    public Setting<Float> vertical = this.register(new Setting<>("Vertical", 0.0f, 0.0f, 100.0f, v -> mode.getValue() == modeEn.Custom));
    private boolean flag;


    public Velocity() {
        super("Velocity", "акэбэшка", Module.Category.MOVEMENT);
    }


    @Override
    public void onUpdate() {
        if (this.ice.getValue()) {
            Blocks.ICE.slipperiness = 0.6f;
            Blocks.PACKED_ICE.slipperiness = 0.6f;
            Blocks.FROSTED_ICE.slipperiness = 0.6f;
        }
    }

    @Override
    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {

        if (fullNullCheck()) return;
        Entity entity;
        SPacketEntityStatus packet;

        if (event.getPacket() instanceof SPacketChat && autoDisable.getValue()) {
            String text = ((SPacketChat) event.getPacket()).getChatComponent().getFormattedText();
            if (text.contains("Тебя проверяют на чит АКБ, ник хелпера - ")) {
                toggle();
            }
        }

        if (event.getPacket() instanceof SPacketEntityStatus && (packet = event.getPacket()).getOpCode() == 31 && (entity = packet.getEntity(Velocity.mc.world)) instanceof EntityFishHook) {
            EntityFishHook fishHook = (EntityFishHook) entity;
            if (fishHook.caughtEntity == Velocity.mc.player) {
                event.setCanceled(true);
            }
        }

        if (event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion velocity_ = event.getPacket();
            if (mode.getValue() == modeEn.Custom) {
                velocity_.motionX *= this.horizontal.getValue() / 100f;
                velocity_.motionY *= this.vertical.getValue() / 100f;
                velocity_.motionZ *= this.horizontal.getValue() / 100f;
            } else if (mode.getValue() == modeEn.Cancel) {
                velocity_.motionX = 0;
                velocity_.motionZ = 0;
                velocity_.motionY = 0;
            }
        }


        if (onlyAura.getValue() && Thunderhack.moduleManager.getModuleByClass(Aura.class).isDisabled()) {
            return;
        }

        if (mode.getValue() == modeEn.Cancel && event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity pac = event.getPacket();
            if (pac.getEntityID() == mc.player.entityId) {
                event.setCanceled(true);
                return;
            }
        }
        if (mode.getValue() == modeEn.Custom) {
            SPacketEntityVelocity velocity;
            if (event.getPacket() instanceof SPacketEntityVelocity && (velocity = event.getPacket()).getEntityID() == Velocity.mc.player.entityId) {
                velocity.motionX = (int) ((float) velocity.motionX * this.horizontal.getValue() / 100f);
                velocity.motionY = (int) ((float) velocity.motionY * this.vertical.getValue() / 100f);
                velocity.motionZ = (int) ((float) velocity.motionZ * this.horizontal.getValue() / 100f);
            }
        }
        if (mode.getValue() == modeEn.Matrix) {
            if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus var9 = event.getPacket();
                if (var9.getOpCode() == 2 && var9.getEntity(mc.world) == mc.player) {
                    flag = true;
                }
            }

            if (event.getPacket() instanceof SPacketEntityVelocity) {
                SPacketEntityVelocity var4 = event.getPacket();
                if (var4.getEntityID() == mc.player.entityId) {
                    if (!flag) {
                        event.setCanceled(true);
                    } else {
                        flag = false;
                        var4.motionX = ((int) ((double) var4.motionX * -0.1));
                        var4.motionZ = ((int) ((double) var4.motionZ * -0.1));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPreMotion(EventPreMotion var1) {
        if (mode.getValue() == modeEn.Matrix) {
            if (mc.player.hurtTime > 0 && !mc.player.onGround) {
                double var3 = mc.player.rotationYaw * 0.017453292F;
                double var5 = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                mc.player.motionX = -Math.sin(var3) * var5;
                mc.player.motionZ = Math.cos(var3) * var5;
                mc.player.setSprinting(mc.player.ticksExisted % 2 != 0);
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        event.setCanceled(true);
    }


    public enum modeEn {
        Matrix, Cancel, Custom
    }
}

