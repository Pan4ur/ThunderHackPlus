package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Blink extends Module {
    public Blink() {
        super("Blink", "Отменяет пакеты движения", Category.MISC);
    }

    private Setting<Boolean> pulse = this.register(new Setting<>("Pulse", false));
    private Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    private Setting<Float> factor = this.register(new Setting<>("Factor", 1F, 0.1f, 10F));
    private  Setting<Boolean> render = this.register(new Setting<>("Render", true));
    private  Setting<Boolean> fill = this.register(new Setting<>("Fill", true));
    public  Setting<Float> circleWidth = this.register(new Setting<>("Width", 2.5F, 5F, 0.1F));
    public  Setting<ColorSetting> circleColor = this.register(new Setting<>("Color", new ColorSetting(0x33da6464, true)));




    private Queue<Packet> storedPackets = new LinkedList<>();

    private Vec3d lastPos = new Vec3d(BlockPos.ORIGIN);


    private AtomicBoolean sending = new AtomicBoolean(false);

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (render.getValue() && lastPos != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            IRenderManager renderManager = (IRenderManager) mc.getRenderManager();
            float[] hsb = Color.RGBtoHSB(circleColor.getValue().getRed(), circleColor.getValue().getGreen(), circleColor.getValue().getBlue(), null);
            float initialHue = (float) (System.currentTimeMillis() % 7200L) / 7200F;
            float hue = initialHue;
            int rgb = Color.getHSBColor(hue, hsb[1], hsb[2]).getRGB();
            ArrayList<Vec3d> vecs = new ArrayList<>();
            double x = lastPos.x - renderManager.getRenderPosX();
            double y = lastPos.y - renderManager.getRenderPosY();
            double z = lastPos.z - renderManager.getRenderPosZ();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableCull();
            GL11.glLineWidth(circleWidth.getValue());
            GL11.glBegin(1);
            for (int i = 0; i <= 360; ++i) {
                Vec3d vec = new Vec3d(x + Math.sin((double) i * Math.PI / 180.0) * 0.5D, y + 0.01, z + Math.cos((double) i * Math.PI / 180.0) * 0.5D);
                vecs.add(vec);
            }
            for (int j = 0; j < vecs.size() - 1; ++j) {
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;
                if (circleColor.getValue().isCycle()) {
                    GL11.glColor4f(red / 255F, green / 255F, blue / 255F, fill.getValue() ? 1F : circleColor.getValue().getAlpha() / 255F);
                } else {
                    GL11.glColor4f(circleColor.getValue().getRed() / 255F, circleColor.getValue().getGreen() / 255F, circleColor.getValue().getBlue() / 255F, fill.getValue() ? 1F : circleColor.getValue().getAlpha() / 255F);
                }
                GL11.glVertex3d(vecs.get(j).x, vecs.get(j).y, vecs.get(j).z);
                GL11.glVertex3d(vecs.get(j + 1).x, vecs.get(j + 1).y, vecs.get(j + 1).z);
                hue += (1F / 360F);
                rgb = Color.getHSBColor(hue, hsb[1], hsb[2]).getRGB();
            }
            GL11.glEnd();
            if (fill.getValue()) {
                hue = initialHue;
                GL11.glBegin(GL11.GL_POLYGON);
                for (int j = 0; j < vecs.size() - 1; ++j) {
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = (rgb) & 0xFF;
                    if (circleColor.getValue().isCycle()) {
                        GL11.glColor4f(red / 255F, green / 255F, blue / 255F, circleColor.getValue().getAlpha() / 255F);
                    } else {
                        GL11.glColor4f(circleColor.getValue().getRed() / 255F, circleColor.getValue().getGreen() / 255F, circleColor.getValue().getBlue() / 255F, circleColor.getValue().getAlpha() / 255F);
                    }
                    GL11.glVertex3d(vecs.get(j).x, vecs.get(j).y, vecs.get(j).z);
                    GL11.glVertex3d(vecs.get(j + 1).x, vecs.get(j + 1).y, vecs.get(j + 1).z);
                    hue += (1F / 360F);
                    rgb = Color.getHSBColor(hue, hsb[1], hsb[2]).getRGB();
                }
                GL11.glEnd();
            }
            GlStateManager.color(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GlStateManager.enableCull();
            GL11.glShadeModel(GL11.GL_FLAT);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        Packet packet = event.getPacket();
        if (sending.get()) return;
        if (pulse.getValue()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                if (strict.getValue() && !((CPacketPlayer) event.getPacket()).isOnGround()) {
                    sending.set(true);
                    while(!storedPackets.isEmpty()) {
                        Packet pckt = storedPackets.poll();
                        mc.player.connection.sendPacket(pckt);
                        if (pckt instanceof CPacketPlayer) {
                            lastPos = new Vec3d(((CPacketPlayer) pckt).getX(mc.player.posX), ((CPacketPlayer) pckt).getY(mc.player.posY), ((CPacketPlayer) pckt).getZ(mc.player.posZ));
                        }
                    }
                    sending.set(false);
                    storedPackets.clear();
                } else {
                    event.setCanceled(true);
                    storedPackets.add(event.getPacket());
                }
            }
        } else if (!(packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus)) {
            event.setCanceled(true);
            storedPackets.add(event.getPacket());
        }
    }

    @Override
    public void onUpdate() {
        if (pulse.getValue() && mc.player != null && mc.world != null) {
            if (storedPackets.size() >= factor.getValue() * 10F) {
                sending.set(true);
                while(!storedPackets.isEmpty()) {
                    Packet pckt = storedPackets.poll();
                    mc.player.connection.sendPacket(pckt);
                    if (pckt instanceof CPacketPlayer) {
                        lastPos = new Vec3d(((CPacketPlayer) pckt).getX(mc.player.posX), ((CPacketPlayer) pckt).getY(mc.player.posY), ((CPacketPlayer) pckt).getZ(mc.player.posZ));
                    }
                }
                sending.set(false);
                storedPackets.clear();
            }
        }
    }

    @Override
    public void onDisable() {
        if(mc.world == null || mc.player == null) return;
        while(!storedPackets.isEmpty()) {
            mc.player.connection.sendPacket(storedPackets.poll());
        }
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null || mc.isIntegratedServerRunning()) {
            this.toggle();
            return;
        }
        lastPos = mc.player.getPositionVector();
        sending.set(false);
        storedPackets.clear();
    }
}
