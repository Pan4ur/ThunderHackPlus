package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.*;

import static com.mrzak34.thunderhack.util.render.RenderUtil.interpolate;
import static org.lwjgl.opengl.GL11.*;


public class DMGParticles extends Module {
    public final Setting<ColorSetting> color1 = this.register(new Setting<>("HealColor", new ColorSetting(3142544)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("DamageColor", new ColorSetting(15811379)));
    private final Setting<Float> size = this.register(new Setting<>("size", 0.5f, 0.1f, 3.0f));
    private final Setting<Integer> ticks = this.register(new Setting<>("ticks", 35, 5f, 60));

    public DMGParticles() {
        super("DMGParticles", "партиклы урона", Category.RENDER);
    }

    private final HashMap<Integer, Float> healthMap = new HashMap<>();
    private final ArrayList<Marker> particles = new ArrayList<>();

    @Override
    public void onDisable() {
        this.particles.clear();
        this.healthMap.clear();
    }

    @Override
    public void onUpdate() {
        synchronized (this.particles) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity == null || mc.player.getDistance(entity) > 10.0f || entity.isDead || !(entity instanceof EntityLivingBase)) continue;
                float lastHealth = this.healthMap.getOrDefault(entity.getEntityId(), ((EntityLivingBase) entity).getMaxHealth());

                this.healthMap.put(entity.getEntityId(), EntityUtil.getHealth(entity));
                if (lastHealth == EntityUtil.getHealth(entity)) continue;
                this.particles.add(new Marker(entity, lastHealth - EntityUtil.getHealth(entity), entity.posX - 0.5 + (double)new Random(System.currentTimeMillis()).nextInt(5) * 0.1, entity.getEntityBoundingBox().minY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0, entity.posZ - 0.5 + (double)new Random(System.currentTimeMillis() + 1L).nextInt(5) * 0.1));
            }
            ArrayList<Marker> needRemove = new ArrayList<>();
            for (Marker marker : this.particles) {
                marker.ticks++;
                if (!((float)marker.ticks >= ticks.getValue()) && !marker.getEntity().isDead) continue;
                needRemove.add(marker);
            }
            for (Marker marker : needRemove) {
                this.particles.remove(marker);
            }
        }
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        synchronized (this.particles) {
            for (Marker marker : this.particles) {
                RenderManager renderManager = mc.getRenderManager();
                double size =  (double)(this.size.getValue() / marker.getScale() * 2.0f) * 0.1;
                size = MathHelper.clamp(size, 0.03, (double)this.size.getValue());
                double x = marker.posX - ((IRenderManager)renderManager).getRenderPosX();
                double y = marker.posY - ((IRenderManager)renderManager).getRenderPosY();
                double z = marker.posZ - ((IRenderManager)renderManager).getRenderPosZ();
                GlStateManager.pushMatrix();
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
                GlStateManager.translate(x, y, z);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL_BLEND);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
                double textY = mc.gameSettings.thirdPersonView == 2 ? -1.0 : 1.0;
                GlStateManager.rotate(renderManager.playerViewX, (float)textY, 0.0f, 0.0f);
                GlStateManager.scale(-size, -size, size);
                GL11.glDepthMask(false);
                int color = marker.getHp() > 0 ? color1.getValue().getColor() : color2.getValue().getColor();
                DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
                Util.fr.drawStringWithShadow(decimalFormat.format(marker.getHp()), -((float)mc.fontRenderer.getStringWidth(marker.getHp() + "") / 2.0f), -(mc.fontRenderer.FONT_HEIGHT - 1), color);
                GlStateManager.disableBlend();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glDepthMask(true);
                GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
                GlStateManager.disablePolygonOffset();
                GlStateManager.popMatrix();
            }
        }
    }

    private class Marker {
        private final Entity entity;
        private final float hp;
        private final double posX;
        private final double posY;
        private final double posZ;
        private int ticks = 0;

        public Marker(Entity entity, float hp, double posX, double posY, double posZ) {
            this.entity = entity;
            this.hp = hp;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
        }

        public float getScale() {
            return (float) interpolate(ticks,ticks - 1,mc.getRenderPartialTicks());
        }

        public Entity getEntity() {
            return this.entity;
        }

        public float getHp() {
            return -this.hp;
        }

        public double getPosX() {
            return this.posX;
        }

        public double getPosY() {
            return this.posY;
        }

        public double getPosZ() {
            return this.posZ;
        }

        public int getTicks() {
            return this.ticks;
        }
    }
}
