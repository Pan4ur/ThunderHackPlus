package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Rotation;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class PyroRadar extends Module {

    public Setting<Float> scalex = register(new Setting("XXX", 1.0F, -5.0F, 5.08F));
    public Setting<Float> scaley = register(new Setting("YYY", 1.0F, -5.0F, 5.08F));
    public Setting<Float> scalez = register(new Setting("ZZZ", 1.0F, -5.0F, 5.08F));
    public Setting<Float> scale = register(new Setting("Scale", 1.0F, 0.0F, 10.08F));
    public Setting<Float> distance = register(new Setting("distance", 1.0F, 0.0F, 10.0F));
    public Setting<Boolean> unlockTilt = register(new Setting<>("Unlock Tilt", false));
    public Setting<Integer> tilt = register(new Setting("Tilt", 50, -90, 90));
    public Setting<Boolean> items = register(new Setting<>("items", false));
    public Setting<Boolean> players = register(new Setting<>("players", false));
    public Setting<Boolean> hidefrustum = register(new Setting<>("HideInFrustrum", false));
    public Setting<Boolean> other = register(new Setting<>("other", false));
    public Setting<Boolean> bosses = register(new Setting<>("bosses", false));
    public Setting<Boolean> hostiles = register(new Setting<>("hostiles", false));
    public Setting<Boolean> friends = register(new Setting<>("friends", false));
    public Setting<Integer> redP = register(new Setting("RedFRIENDS", 0, 0, 255));
    public Setting<Integer> greenP = register(new Setting("GreenFRIENDS", 200, 0, 255));
    public Setting<Integer> blueP = register(new Setting("BlueFRIENDS", 60, 0, 255));
    public Setting<Integer> redF = register(new Setting("Red", 200, 0, 255));
    public Setting<Integer> greenF = register(new Setting("Green", 120, 0, 255));
    public Setting<Integer> blueF = register(new Setting("Blue", 0, 0, 255));
    public PyroRadar() {
        super("Radar", "радар из пайро", Category.HUD);

    }

    public Rotation getRotation(Vec3d vec3d, Vec3d vec3d2) {
        double d = vec3d2.x - vec3d.x;
        double d2 = vec3d2.y - vec3d.y;
        double d3 = vec3d2.z - vec3d.z;
        double d4 = MathHelper.sqrt((d * d + d3 * d3));
        return new Rotation(MathHelper.wrapDegrees(((float) Math.toDegrees(MathHelper.atan2(d3, d)) - 90.0f)), MathHelper.wrapDegrees(((float) (-Math.toDegrees(MathHelper.atan2(d2, d4))))));
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        GL11.glPushMatrix();
        GlStateManager.translate(scalex.getValue(), scaley.getValue(), scalez.getValue());
        RenderUtil.camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        GlStateManager.pushMatrix();
        WorldClient worldClient = (Util.mc).world;

        for (Entity entity4 : worldClient.loadedEntityList) {
            block16:
            {
                block15:
                {
                    if (entity4 == mc.getRenderViewEntity()) continue;
                    if (hidefrustum.getValue()) {
                        if (RenderUtil.camera.isBoundingBoxInFrustum(entity4.getEntityBoundingBox())) continue;
                    }
                    if (entity4 instanceof EntityPlayer) {
                        if (friends.getValue() && Thunderhack.friendManager.isFriend((EntityPlayer) entity4)) {
                            this.drawFriendArrow(entity4);
                            continue;
                        }
                        if (!players.getValue() && Thunderhack.friendManager.isFriend((EntityPlayer) entity4)) continue;
                        this.drawArrow(entity4);
                        continue;
                    }
                    if (entity4 instanceof EntityDragon) break block15;
                    if (!(entity4 instanceof EntityWither)) break block16;
                }
                if (!bosses.getValue()) continue;
                this.drawArrow(entity4);
                continue;
            }
            if (entity4.isCreatureType(EnumCreatureType.MONSTER, false)) {
                if (!hostiles.getValue()) continue;
                this.drawArrow(entity4);
                continue;
            }
            if (entity4 instanceof EntityItem) {
                if (!items.getValue()) continue;
                this.drawArrow(entity4);
                continue;
            }
            if (!other.getValue()) continue;
            this.drawArrow(entity4);
        }
        GlStateManager.popMatrix();
        GlStateManager.translate(1f, 1f, 1f);
        GlStateManager.popMatrix();
    }

    public Vec3d getEntityVector(Entity entity) {
        double d = this.c(entity.posX, entity.lastTickPosX) - this.c(mc.player.posX, mc.player.lastTickPosX);
        double d2 = this.c(entity.posY, entity.lastTickPosY) - this.c(mc.player.posY, mc.player.lastTickPosY);
        double d3 = this.c(entity.posZ, entity.lastTickPosZ) - this.c(mc.player.posZ, mc.player.lastTickPosZ);
        return new Vec3d(d, d2, d3);
    }


    public void arrow(float f, float f2, float f3, float f4) {
        GlStateManager.glBegin(6);
        GlStateManager.glVertex3f(f, f2, f3);
        GlStateManager.glVertex3f((f + 0.1f * f4), f2, (f3 - 0.2f * f4));
        GlStateManager.glVertex3f(f, f2, (f3 - 0.12f * f4));
        GlStateManager.glVertex3f((f - 0.1f * f4), f2, (f3 - 0.2f * f4));
        GlStateManager.glEnd();
    }

    public void drawArrow(Entity var1x) {
        if (mc.entityRenderer != null) {
            Rotation var8 = getRotation(Vec3d.ZERO, getEntityVector(var1x));
            float var6 = var8.meth2();
            float var16 = (float) 180 - var6;
            Entity var10001 = mc.getRenderViewEntity();
            var6 = var16 + Objects.requireNonNull(mc.getRenderViewEntity()).rotationYaw;
            Vec3d var14 = (new Vec3d(0.0D, 0.0D, 1.0D)).rotateYaw((float) Math.toRadians(var6)).rotatePitch((float) Math.toRadians(180.0D));
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.color(redF.getValue(), greenF.getValue(), blueF.getValue());
            GlStateManager.disableLighting();
            GlStateManager.loadIdentity();
            EntityRenderer var17 = mc.entityRenderer;
            if (var17 != null) {
                ((IEntityRenderer) var17).orientCam(mc.getRenderPartialTicks());
                float var10 = (float) ((Number) this.scale.getValue()).doubleValue() * 0.2F;
                float var11 = (float) ((Number) this.distance.getValue()).doubleValue() * 0.2F;
                var10001 = mc.getRenderViewEntity();

                GlStateManager.translate(0.0F, var10001.getEyeHeight(), 0.0F);
                Entity var18 = mc.getRenderViewEntity();

                GlStateManager.rotate(-var18.rotationYaw, 0.0F, 1.0F, 0.0F);
                var18 = mc.getRenderViewEntity();

                GlStateManager.rotate(var18.rotationPitch, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 1.0F);
                float var12 = (float) ((Number) this.tilt.getValue()).intValue();
                if (unlockTilt.getValue()) {
                    var16 = 90f;
                    var10001 = mc.getRenderViewEntity();

                    if (var16 - var10001.rotationPitch < var12) {
                        var10001 = mc.getRenderViewEntity();

                        var12 = var16 - var10001.rotationPitch;
                    }
                }

                GlStateManager.rotate(var12, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(var6, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, var11);

                float var13 = scale.getValue() * var10 * 2.0f;

                arrow((float) var14.x, (float) var14.y, (float) var14.z, var13);
                GlStateManager.enableTexture2D();
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableLighting();
            }
        }
    }

    public void drawFriendArrow(Entity var1x) {
        if (mc.entityRenderer != null) {
            Entity var10001 = mc.getRenderViewEntity();
            Rotation var8 = getRotation(Vec3d.ZERO, getEntityVector(var1x));
            float var6 = var8.meth2();
            float var16 = (float) 180 - var6;
            var6 = var16 + Objects.requireNonNull(mc.getRenderViewEntity()).rotationYaw;
            Vec3d var14 = (new Vec3d(0.0D, 0.0D, 1.0D)).rotateYaw((float) Math.toRadians(var6)).rotatePitch((float) Math.toRadians(180.0D));
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.color(redP.getValue(), greenP.getValue(), blueP.getValue());
            GlStateManager.disableLighting();
            GlStateManager.loadIdentity();
            EntityRenderer var17 = mc.entityRenderer;
            if (var17 != null) {
                ((IEntityRenderer) var17).orientCam(mc.getRenderPartialTicks());
                float var10 = (float) ((Number) this.scale.getValue()).doubleValue() * 0.2F;
                float var11 = (float) ((Number) this.distance.getValue()).doubleValue() * 0.2F;
                var10001 = mc.getRenderViewEntity();

                GlStateManager.translate(0.0F, var10001.getEyeHeight(), 0.0F);
                Entity var18 = mc.getRenderViewEntity();

                GlStateManager.rotate(-var18.rotationYaw, 0.0F, 1.0F, 0.0F);
                var18 = mc.getRenderViewEntity();

                GlStateManager.rotate(var18.rotationPitch, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 1.0F);
                float var12 = (float) ((Number) this.tilt.getValue()).intValue();
                if (unlockTilt.getValue()) {
                    var16 = (float) 90;
                    var10001 = mc.getRenderViewEntity();

                    if (var16 - var10001.rotationPitch < var12) {
                        var10001 = mc.getRenderViewEntity();

                        var12 = var16 - var10001.rotationPitch;
                    }
                }

                GlStateManager.rotate(var12, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(var6, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, var11);

                arrow((float) var14.x, (float) var14.y, (float) var14.z, scale.getValue() * var10 * 2.0f);
                GlStateManager.enableTexture2D();
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableLighting();
            }
        }
    }

    public double c(double d, double d2) {
        return d2 + (d - d2) * (double) mc.getRenderPartialTicks();
    }


}
