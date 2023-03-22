package com.mrzak34.thunderhack.gui.hud.elements;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
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

import java.awt.*;
import java.util.Objects;

public class PyroRadar extends Module {

    public Setting<Float> scale = register(new Setting("Scale", 0.2F, 0.0F, 1));
    public Setting<Float> distance = register(new Setting("distance", 0.2F, 0.0F, 2f));
    public Setting<Boolean> unlockTilt = register(new Setting<>("Unlock Tilt", false));
    public Setting<Integer> tilt = register(new Setting("Tilt", 50, -90, 90));
    public Setting<Boolean> items = register(new Setting<>("items", false));
    public Setting<Boolean> players = register(new Setting<>("players", false));
    public Setting<Boolean> hidefrustum = register(new Setting<>("HideInFrustrum", false));
    public Setting<Boolean> other = register(new Setting<>("other", false));
    public Setting<Boolean> bosses = register(new Setting<>("bosses", false));
    public Setting<Boolean> hostiles = register(new Setting<>("hostiles", false));
    public Setting<Boolean> friends = register(new Setting<>("friends", false));

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    public final Setting<ColorSetting> fcolor = this.register(new Setting<>("FriendColor", new ColorSetting(0x8800FF00)));

    public PyroRadar() {
        super("PyroRadar", "радар из пайро", Category.HUD);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        // RenderUtil.camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        for (Entity ent : mc.world.loadedEntityList) {
            if (ent == mc.player) continue;
            if (hidefrustum.getValue())
                if (RenderUtil.camera.isBoundingBoxInFrustum(ent.getEntityBoundingBox())) continue;

            if (ent instanceof EntityPlayer) {
                if (friends.getValue() && Thunderhack.friendManager.isFriend((EntityPlayer) ent)) {
                    drawArrow(ent,fcolor.getValue().getColorObject());
                    continue;
                }
                if (!players.getValue() && Thunderhack.friendManager.isFriend((EntityPlayer) ent)) continue;
                drawArrow(ent, color.getValue().getColorObject());
                continue;
            }
            if (ent instanceof EntityWither){
                if (!bosses.getValue()) continue;
                drawArrow(ent, color.getValue().getColorObject());
                continue;
            }
            if (ent instanceof EntityDragon){
                if (!bosses.getValue()) continue;
                drawArrow(ent, color.getValue().getColorObject());
                continue;
            }
            if (ent.isCreatureType(EnumCreatureType.MONSTER, false)) {
                if (!hostiles.getValue()) continue;
                drawArrow(ent, color.getValue().getColorObject());
                continue;
            }
            if (ent instanceof EntityItem) {
                if (!items.getValue()) continue;
                drawArrow(ent, color.getValue().getColorObject());
                continue;
            }
            if (!other.getValue()) continue;
            drawArrow(ent, color.getValue().getColorObject());
        }
    }

    private float getYaw(Entity entity) {
        double delta_x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX) - RenderUtil.interpolate(mc.player.posX, mc.player.lastTickPosX);
        double delta_z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ) - RenderUtil.interpolate(mc.player.posZ, mc.player.lastTickPosZ);
        return MathHelper.wrapDegrees(((float) Math.toDegrees(MathHelper.atan2(delta_x, delta_z)) + 180.0f));
    }

    public void arrow(float f, float f2, float f3, float f4) {
        GlStateManager.glBegin(6);
        GlStateManager.glVertex3f(f, f2, f3);
        GlStateManager.glVertex3f((f + 0.1f * f4), f2, (f3 - 0.2f * f4));
        GlStateManager.glVertex3f(f, f2, (f3 - 0.12f * f4));
        GlStateManager.glVertex3f((f - 0.1f * f4), f2, (f3 - 0.2f * f4));
        GlStateManager.glEnd();
    }

    public void drawArrow(Entity ent, Color color) {
        if (mc.entityRenderer != null) {

            Vec3d var14 = (new Vec3d(0.0D, 0.0D, 1.0D)).rotateYaw((float) Math.toRadians( getYaw(ent) + mc.player.rotationYaw)).rotatePitch((float) Math.PI);

            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(),color.getAlpha());
            GlStateManager.disableLighting();
            GlStateManager.loadIdentity();

            ((IEntityRenderer) mc.entityRenderer).orientCam(mc.getRenderPartialTicks());
            GlStateManager.translate(0.0F, mc.player.getEyeHeight(), 0.0F);
            GlStateManager.rotate(-mc.player.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.player.rotationPitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
            float tilt_value = tilt.getValue();
            if (unlockTilt.getValue())
                if (90f - mc.player.rotationPitch < tilt_value)
                    tilt_value = 90f - mc.player.rotationPitch;

            GlStateManager.rotate(tilt_value, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(getYaw(ent) + mc.player.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, distance.getValue() * 0.2F);
            arrow((float) var14.x, (float) var14.y, (float) var14.z, scale.getValue());

            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
        }
    }
}
