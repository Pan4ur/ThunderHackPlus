package com.mrzak34.thunderhack.modules.misc;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.PushEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity
        extends Module {
    private static Velocity INSTANCE = new Velocity();

    public Setting<Boolean> onlyAura = register(new Setting<>("OnlyAura", true));

    public Setting<Boolean> knockBack = this.register(new Setting<Boolean>("KnockBack", true));
    public Setting<Boolean> noPush = this.register(new Setting<Boolean>("NoPush", true));
    public Setting<Float> horizontal = this.register(new Setting<Float>("Horizontal", 0.0f, 0.0f, 100.0f));
    public Setting<Float> vertical = this.register(new Setting<Float>("Vertical", 0.0f, 0.0f, 100.0f));
    public Setting<Boolean> explosions = this.register(new Setting<Boolean>("Explosions", true));
    public Setting<Boolean> bobbers = this.register(new Setting<Boolean>("FishingRod", true));
    public Setting<Boolean> water = this.register(new Setting<Boolean>("Water", false));
    public Setting<Boolean> blocks = this.register(new Setting<Boolean>("Blocks", false));
    public Setting<Boolean> ice = this.register(new Setting<Boolean>("Ice", false));

    public Velocity() {
        super("Velocity", "акэбэшка", Module.Category.MOVEMENT, true, false, false);
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
        if (event.getStage() == 0 && Velocity.mc.player != null) {
            Entity entity;
            SPacketEntityStatus packet;
            SPacketEntityVelocity velocity;
            if (this.knockBack.getValue() && event.getPacket() instanceof SPacketEntityVelocity && (velocity = event.getPacket()).getEntityID() == Velocity.mc.player.entityId) {
                if(onlyAura.getValue() && Thunderhack.moduleManager.getModuleByClass(Aura.class).isDisabled()){
                    return;
                }

                if (this.horizontal.getValue() == 0.0f && this.vertical.getValue() == 0.0f) {
                    event.setCanceled(true);
                    return;
                }
                velocity.motionX = (int) ((float) velocity.motionX * this.horizontal.getValue() / 100f);
                velocity.motionY = (int) ((float) velocity.motionY * this.vertical.getValue() / 100f);
                velocity.motionZ = (int) ((float) velocity.motionZ * this.horizontal.getValue() / 100f);
            }
            if (event.getPacket() instanceof SPacketEntityStatus && this.bobbers.getValue() && (packet = event.getPacket()).getOpCode() == 31 && (entity = packet.getEntity(Velocity.mc.world)) instanceof EntityFishHook) {
                EntityFishHook fishHook = (EntityFishHook) entity;
                if (fishHook.caughtEntity == Velocity.mc.player) {
                    event.setCanceled(true);
                }
            }
            if (this.explosions.getValue() && event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion velocity_ = event.getPacket();
                velocity_.motionX *= this.horizontal.getValue() / 100f;
                velocity_.motionY *= this.vertical.getValue() / 100f;
                velocity_.motionZ *= this.horizontal.getValue() / 100f;
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        if (event.getStage() == 0 && this.noPush.getValue() && event.entity.equals(Velocity.mc.player)) {
            if (this.horizontal.getValue() == 0.0f && this.vertical.getValue() == 0.0f) {
                event.setCanceled(true);
                return;
            }
            event.x = -event.x * (double) this.horizontal.getValue() / 100f;
            event.y = -event.y * (double) this.vertical.getValue() / 100f;
            event.z = -event.z * (double) this.horizontal.getValue()/ 100f;
        } else if (event.getStage() == 1 && this.blocks.getValue()) {
            event.setCanceled(true);
        } else if (event.getStage() == 2 && this.water.getValue() && Velocity.mc.player != null && Velocity.mc.player.equals(event.entity)) {
            event.setCanceled(true);
        }
    }
}

