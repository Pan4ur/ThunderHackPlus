package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.EventEntityMove;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.mixin.ducks.IEntity;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.render.LogoutSpots;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.phobos.ThreadUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class BackTrack extends Module {

    private final Setting<RenderMode> renderMode = register(new Setting<>("RenderMode", RenderMode.Chams));
    private final Setting<ColorSetting> color1 = this.register(new Setting<>("Color", new ColorSetting(-2009289807)));
    private final Setting<ColorSetting> color2 = this.register(new Setting<>("HighLightColor", new ColorSetting(-2009289807)));
    private final Setting<Integer> btticks = register(new Setting("TrackTicks", 5, 1, 15));
    private final Setting<Boolean> hlaura = register(new Setting<>("HighLightAura", true));
    private final Setting<Boolean> holdPackets = register(new Setting<>("ServerSync", true));

    long skip_packet_ka, skip_packet_ct, skip_packet_cwt;


    public BackTrack() {
        super("BackTrack", "откатывает позицию-врагов", "rolls back the-position of enemies", Category.COMBAT);
    }


    @SubscribeEvent
    public void onPreRenderEvent(PreRenderEvent event) {
        synchronized (this) {
            for (EntityPlayer entity : mc.world.playerEntities) {
                if (entity == mc.player) {
                    continue;
                }
                if (((IEntity) entity).getPosition_history().size() > 0) {
                    for (int i = 0; i < ((IEntity) entity).getPosition_history().size(); i++) {
                        GlStateManager.pushMatrix();
                        if (Aura.bestBtBox != ((IEntity) entity).getPosition_history().get(i) && hlaura.getValue()) {
                            if (renderMode.getValue() == RenderMode.Box) {
                                RenderUtil.drawBoundingBox(((IEntity) entity).getPosition_history().get(i), 1, color1.getValue().getColorObject());
                            } else if (renderMode.getValue() == RenderMode.Chams) {
                                RenderUtil.renderEntity(
                                        ((IEntity) entity).getPosition_history().get(i),
                                        ((IEntity) entity).getPosition_history().get(i).modelPlayer,
                                        ((IEntity) entity).getPosition_history().get(i).limbSwing,
                                        ((IEntity) entity).getPosition_history().get(i).limbSwingAmount,
                                        ((IEntity) entity).getPosition_history().get(i).Yaw,
                                        ((IEntity) entity).getPosition_history().get(i).Pitch,
                                        ((IEntity) entity).getPosition_history().get(i).ent,
                                        color1.getValue().getColorObject());
                            } else if (renderMode.getValue() == RenderMode.Ghost) {

                                GlStateManager.pushMatrix();

                                boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
                                boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
                                boolean depthtest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);

                                GlStateManager.enableLighting();
                                GlStateManager.enableBlend();
                                GlStateManager.enableDepth();
                                GlStateManager.color(1, 1, 1, 1);
                                try {
                                    mc.getRenderManager().renderEntity(entity,
                                            ((IEntity) entity).getPosition_history().get(i).position.x - ((IRenderManager) Util.mc.getRenderManager()).getRenderPosX(),
                                            ((IEntity) entity).getPosition_history().get(i).position.y - ((IRenderManager) Util.mc.getRenderManager()).getRenderPosY(),
                                            ((IEntity) entity).getPosition_history().get(i).position.z - ((IRenderManager) Util.mc.getRenderManager()).getRenderPosZ(),

                                            ((IEntity) entity).getPosition_history().get(i).Yaw, mc.getRenderPartialTicks(), false);
                                } catch (Exception ignored) {
                                }

                                if (!depthtest)
                                    GlStateManager.disableDepth();
                                if (!lighting)
                                    GlStateManager.disableLighting();
                                if (!blend)
                                    GlStateManager.disableBlend();

                                GlStateManager.popMatrix();
                            }
                        } else {
                            if (renderMode.getValue() == RenderMode.Box) {
                                RenderUtil.drawBoundingBox(((IEntity) entity).getPosition_history().get(i), 1, color2.getValue().getColorObject());
                            } else if (renderMode.getValue() == RenderMode.Chams) {
                                RenderUtil.renderEntity(
                                        ((IEntity) entity).getPosition_history().get(i),
                                        ((IEntity) entity).getPosition_history().get(i).modelPlayer,
                                        ((IEntity) entity).getPosition_history().get(i).limbSwing,
                                        ((IEntity) entity).getPosition_history().get(i).limbSwingAmount,
                                        ((IEntity) entity).getPosition_history().get(i).Yaw,
                                        ((IEntity) entity).getPosition_history().get(i).Pitch,
                                        ((IEntity) entity).getPosition_history().get(i).ent,
                                        color2.getValue().getColorObject()
                                );
                            } else if (renderMode.getValue() == RenderMode.Ghost) {

                                GlStateManager.pushMatrix();

                                boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
                                boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
                                boolean depthtest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);

                                GlStateManager.enableLighting();
                                GlStateManager.enableBlend();
                                GlStateManager.enableDepth();
                                GlStateManager.color(1, 1, 1, 0.1f);
                                try {
                                    mc.getRenderManager().renderEntity(entity,
                                            ((IEntity) entity).getPosition_history().get(i).position.x,
                                            ((IEntity) entity).getPosition_history().get(i).position.y,
                                            ((IEntity) entity).getPosition_history().get(i).position.z,

                                            ((IEntity) entity).getPosition_history().get(i).Yaw, mc.getRenderPartialTicks(), false);
                                } catch (Exception ignored) {
                                }

                                if (!depthtest)
                                    GlStateManager.disableDepth();
                                if (!lighting)
                                    GlStateManager.disableLighting();
                                if (!blend)
                                    GlStateManager.disableBlend();

                                GlStateManager.popMatrix();
                            }
                        }
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!holdPackets.getValue() || fullNullCheck()) return;
        if (event.getPacket() instanceof CPacketKeepAlive) {
            if (((CPacketKeepAlive) event.getPacket()).getKey() == skip_packet_ka) {
                return;
            }
            event.setCanceled(true);
            ThreadUtil.run(() -> {
                skip_packet_ka = ((CPacketKeepAlive) event.getPacket()).getKey();
                mc.player.connection.sendPacket(event.getPacket());
            }, (long) btticks.getValue() * 50);

        }
        if (event.getPacket() instanceof CPacketConfirmTransaction) {
            if (((CPacketConfirmTransaction) event.getPacket()).getUid() == skip_packet_ct) {
                return;
            }
            if (((CPacketConfirmTransaction) event.getPacket()).getWindowId() == skip_packet_cwt) {
                return;
            }
            event.setCanceled(true);
            ThreadUtil.run(() -> {
                skip_packet_ct = ((CPacketConfirmTransaction) event.getPacket()).getUid();
                skip_packet_cwt = ((CPacketConfirmTransaction) event.getPacket()).getWindowId();
                mc.player.connection.sendPacket(event.getPacket());
            }, (long) btticks.getValue() * 50);
        }
    }

    @SubscribeEvent
    public void onEntityMove(EventEntityMove e) {
        if (e.ctx() == mc.player) {
            return;
        }
        if (e.ctx() instanceof EntityPlayer) {
            if (e.ctx() != null) {
                EntityPlayer a = (EntityPlayer) e.ctx();
                ((IEntity)a).getPosition_history().add(new Box(e.ctx().getPositionVector(), btticks.getValue(), a.limbSwing, a.limbSwingAmount, a.rotationYaw, a.rotationPitch, (EntityPlayer) e.ctx()));
            }
        }
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            ((IEntity)player).getPosition_history().removeIf(Box::update);
        }
    }


    public enum RenderMode {
        Box, Chams, Ghost, None
    }

    public static class Box {
        private final ModelPlayer modelPlayer;
        private final Vec3d position;
        private final float limbSwing;
        private final float limbSwingAmount;
        private final float Yaw;
        private final float Pitch;
        private final EntityPlayer ent;
        private int ticks;

        public Box(Vec3d position, int ticks, float limbswing, float limbSwingAmount, float Yaw, float Pitch, EntityPlayer ent) {
            this.position = position;
            this.ticks = ticks;
            this.modelPlayer = new ModelPlayer(0, false);
            this.limbSwing = limbswing;
            this.limbSwingAmount = limbSwingAmount;
            this.Pitch = Pitch;
            this.Yaw = Yaw;
            this.ent = ent;
        }

        public int getTicks() {
            return ticks;
        }

        public boolean update() {
            return this.ticks-- <= 0;
        }

        public Vec3d getPosition() {
            return position;
        }

        public float getLimbSwing() {
            return limbSwing;
        }

        public float getLimbSwingAmount() {
            return limbSwingAmount;
        }

        public float getYaw() {
            return Yaw;
        }

        public float getPitch() {
            return Pitch;
        }
    }

}
