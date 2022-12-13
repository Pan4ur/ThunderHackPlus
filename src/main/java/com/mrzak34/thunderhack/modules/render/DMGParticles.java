package com.mrzak34.thunderhack.modules.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.math.BigDecimal;
import com.mrzak34.thunderhack.event.events.DeathEvent;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;


public class DMGParticles extends Module {
    public DMGParticles() { super("DMGParticles", "партиклы урона", Category.RENDER, true, false, false);}

    public Setting<Integer> deleteAfter = this.register ( new Setting <> ( "Remove Ticks", 7, 1, 20 ) );



    private final Map<Integer, Float> hpData = Maps.newHashMap();
    private final List<Particle> particles = Lists.newArrayList();



    @SubscribeEvent
    public void onRespawn(DeathEvent event) {
        if(event.player == mc.player) {
            particles.clear();
        }
    }

    @Override
    public void onUpdate() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase ent = (EntityLivingBase) entity;
                final double lastHp = hpData.getOrDefault(ent.getEntityId(), ent.getMaxHealth());
                hpData.remove(entity.getEntityId());
                hpData.put(entity.getEntityId(), ent.getHealth());
                if (lastHp == ent.getHealth()) continue;
                Color color;
                if (lastHp > ent.getHealth()) {
                    color = Color.red;
                } else {
                    color = Color.GREEN;
                }
                Vec3d loc = new Vec3d(entity.posX + Math.random() * 0.5 * (Math.random() > 0.5 ? -1 : 1), entity.getEntityBoundingBox().minY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.5, entity.posZ + Math.random() * 0.5 * (Math.random() > 0.5 ? -1 : 1));
                double str = new BigDecimal(Math.abs(lastHp - ent.getHealth())).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                particles.add(new Particle("" + str, loc.x, loc.y, loc.z, color));
            }
        }
    }

    Timer timer = new Timer();

    @SubscribeEvent
    public void onRender3D(Render3DEvent e) {
        if (timer.passedMs(deleteAfter.getValue() * 300)) {
            particles.clear();
            timer.reset();
        }
        if (!particles.isEmpty()) {
            for (Particle p : particles) {
                if (p != null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enablePolygonOffset();
                    GlStateManager.doPolygonOffset(1, -1500000);
                    GlStateManager.translate(p.posX - mc.getRenderManager().renderPosX, p.posY - mc.getRenderManager().renderPosY, p.posZ - mc.getRenderManager().renderPosZ);
                    GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
                    GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1 : 1, 0, 0);
                    GlStateManager.scale(-0.03, -0.03, 0.03);
                    GL11.glDepthMask(false);
                    Util.fr.drawStringWithShadow(p.str, (float) (-Util.fr.getStringWidth(p.str) * 0.5), -Util.fr.FONT_HEIGHT + 1, p.color.getRGB());
                    GL11.glColor4f(1, 1, 1, 1);
                    GL11.glDepthMask(true);
                    GlStateManager.doPolygonOffset(1, 1500000);
                    GlStateManager.disablePolygonOffset();
                    GlStateManager.resetColor();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    class Particle {
        public String str;
        public double posX, posY, posZ;
        public Color color;
        public int ticks;

        public Particle(String str, double posX, double posY, double posZ, Color color) {
            this.str = str;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.color = color;
            this.ticks = 0;
        }
    }
}
