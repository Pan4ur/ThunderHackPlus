package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.MathematicHelper;
import com.mrzak34.thunderhack.util.render.DrawHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

public class EntityESP extends Module {

    public final Setting<Boolean> border = this.register(new Setting<Boolean>("Border Rect", true));
    public final Setting<Boolean> fullBox = this.register(new Setting<Boolean>("Full Box", false));
    public final Setting<Boolean> heathPercentage = this.register(new Setting<Boolean>("HealthPercent", true));
    public final Setting<Boolean> healRect = this.register(new Setting<Boolean>("HealthRect", true));
    public final Setting<Boolean> ignoreInvisible = this.register(new Setting<Boolean>("IgnoreInvisible", true));
    private final int black = Color.BLACK.getRGB();
    private final Setting<ColorSetting> colorEsp = this.register(new Setting<>("ESPColor", new ColorSetting(0x2250b4b4)));
    private final Setting<ColorSetting> healColor = this.register(new Setting<>("HealthColor", new ColorSetting(0x2250b4b4)));
    private final Setting<healcolorModeEn> healcolorMode = register(new Setting("HealthMode", healcolorModeEn.Custom));
    private final Setting<colorModeEn> colorMode = register(new Setting("ColorBoxMode", colorModeEn.Custom));
    private final Setting<espModeEn> espMode = register(new Setting("espMode", espModeEn.Flat));
    private final Setting<rectModeEn> rectMode = register(new Setting("RectMode", rectModeEn.Default));
    private final Setting<csgoModeEn> csgoMode = register(new Setting("csgoMode", csgoModeEn.Box));
    public EntityESP() {
        super("EntityESP", "Ренднрит есп-сущностей", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event3D) {
        int color = 0;

        switch (colorMode.getValue()) {
            case Custom:
                color = colorEsp.getValue().getColor();
                break;
            case Astolfo:
                color = DrawHelper.astolfo(false, 10).getRGB();
                break;
            case Rainbow:
                color = DrawHelper.rainbow(300, 1, 1).getRGB();
                break;
        }

        if (espMode.getValue() == espModeEn.Box) {
            GlStateManager.pushMatrix();
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityPlayer && entity != mc.player) {
                    DrawHelper.drawEntityBox(entity, new Color(color), fullBox.getValue(), fullBox.getValue() ? 0.15F : 0.90F);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {


        float partialTicks = mc.timer.renderPartialTicks;
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        double scaling = scaleFactor / Math.pow(scaleFactor, 2);
        GL11.glPushMatrix();
        GlStateManager.scale(scaling, scaling, scaling);

        int color = 0;
        switch (colorMode.getValue()) {
            case Custom:
                color = colorEsp.getValue().getColor();
                break;
            case Astolfo:
                color = DrawHelper.astolfo(false, 1).getRGB();
                break;
            case Rainbow:
                color = DrawHelper.rainbow(300, 1, 1).getRGB();
                break;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.player) {


                if ((ignoreInvisible.getValue() && entity.isInvisible()))
                    continue;

                if (isValid(entity) && DrawHelper.isInViewFrustum(entity)) {
                    double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks();
                    double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks();
                    double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks();
                    double width = entity.width / 1.5;
                    double height = entity.height + ((entity.isSneaking() || (entity == mc.player && mc.player.isSneaking()) ? -0.3D : 0.2D));
                    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                    List<Vector3d> vectors = Arrays.asList(new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ),
                            new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ),
                            new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ),
                            new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ));

                    mc.entityRenderer.setupCameraTransform(partialTicks, 0);

                    Vector4d position = null;
                    for (Vector3d vector : vectors) {
                        vector = vectorRender2D(scaleFactor, vector.x - mc.getRenderManager().renderPosX, vector.y - mc.getRenderManager().renderPosY, vector.z - mc.getRenderManager().renderPosZ);
                        if (vector != null && vector.z > 0 && vector.z < 1) {

                            if (position == null) {
                                position = new Vector4d(vector.x, vector.y, vector.z, 0);
                            }

                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    if (position != null) {
                        mc.entityRenderer.setupOverlayRendering();
                        double posX = position.x;
                        double posY = position.y;
                        double endPosX = position.z;
                        double endPosY = position.w;
                        if (border.getValue()) {
                            if (espMode.getValue() == espModeEn.Flat && csgoMode.getValue() == csgoModeEn.Box && rectMode.getValue() == rectModeEn.Smooth) {

                                //top
                                DrawHelper.drawSmoothRect(posX - 0.5, posY - 0.5, endPosX + 0.5, posY + 0.5 + 1, black);

                                //button
                                DrawHelper.drawSmoothRect(posX - 0.5, endPosY - 0.5 - 1, endPosX + 0.5, endPosY + 0.5, black);

                                //left
                                DrawHelper.drawSmoothRect(posX - 1.5, posY, posX + 0.5, endPosY + 0.5, black);

                                //right
                                DrawHelper.drawSmoothRect(endPosX - 0.5 - 1, posY, endPosX + 0.5, endPosY + 0.5, black);

                                /* Main ESP */

                                //left
                                DrawHelper.drawSmoothRect(posX - 1, posY, posX + 0.5 - 0.5, endPosY, color);

                                //Button
                                DrawHelper.drawSmoothRect(posX, endPosY - 1, endPosX, endPosY, color);

                                //Top
                                DrawHelper.drawSmoothRect(posX - 1, posY, endPosX, posY + 1, color);

                                //Right
                                DrawHelper.drawSmoothRect(endPosX - 1, posY, endPosX, endPosY, color);
                            } else if (espMode.getValue() == espModeEn.Flat && csgoMode.getValue() == csgoModeEn.Corner && rectMode.getValue() == rectModeEn.Smooth) {

                                //Top Left
                                DrawHelper.drawSmoothRect(posX + 1, posY, posX - 1, posY + (endPosY - posY) / 4 + 0.5, black);

                                //Button Left
                                DrawHelper.drawSmoothRect(posX - 1, endPosY, posX + 1, endPosY - (endPosY - posY) / 4.0 - 0.5, black);

                                //Top Left Corner
                                DrawHelper.drawSmoothRect(posX - 1, posY - 0.5, posX + (endPosX - posX) / 3, posY + 1, black);

                                //Top Corner
                                DrawHelper.drawSmoothRect(endPosX - (endPosX - posX) / 3 - 0, posY - 0.5, endPosX, posY + 1.5, black);

                                //Top Right Corner
                                DrawHelper.drawSmoothRect(endPosX - 1.5, posY, endPosX + 0.5, posY + (endPosY - posY) / 4 + 0.5, black);

                                //Right Button Corner
                                DrawHelper.drawSmoothRect(endPosX - 1.5, endPosY, endPosX + 0.5, endPosY - (endPosY - posY) / 4 - 0.5, black);

                                //Left Button
                                DrawHelper.drawSmoothRect(posX - 1, endPosY - 1.5, posX + (endPosX - posX) / 3 + 0.5, endPosY + 0.5, black);

                                //Right Button
                                DrawHelper.drawSmoothRect(endPosX - (endPosX - posX) / 3 - 0.5, endPosY - 1.5, endPosX + 0.5, endPosY + 0.5, black);

                                DrawHelper.drawSmoothRect(posX + 0.5, posY, posX - 0.5, posY + (endPosY - posY) / 4, color);

                                DrawHelper.drawSmoothRect(posX + 0.5, endPosY, posX - 0.5, endPosY - (endPosY - posY) / 4, color);

                                DrawHelper.drawSmoothRect(posX - 0.5, posY, posX + (endPosX - posX) / 3, posY + 1, color);
                                DrawHelper.drawSmoothRect(endPosX - (endPosX - posX) / 3 + 0.5, posY, endPosX, posY + 1, color);
                                DrawHelper.drawSmoothRect(endPosX - 1, posY, endPosX, posY + (endPosY - posY) / 4 + 0.5, color);
                                DrawHelper.drawSmoothRect(endPosX - 1, endPosY, endPosX, endPosY - (endPosY - posY) / 4, color);
                                DrawHelper.drawSmoothRect(posX, endPosY - 1, posX + (endPosX - posX) / 3, endPosY, color);
                                DrawHelper.drawSmoothRect(endPosX - (endPosX - posX) / 3, endPosY - 1, endPosX - 0.5, endPosY, color);
                            } else if (espMode.getValue() == espModeEn.Flat && csgoMode.getValue() == csgoModeEn.Box && rectMode.getValue() == rectModeEn.Default) {

                                //top
                                DrawHelper.drawNewRect(posX - 0.5, posY - 0.5, endPosX + 0.5, posY + 0.5 + 1, black);

                                //button
                                DrawHelper.drawNewRect(posX - 0.5, endPosY - 0.5 - 1, endPosX + 0.5, endPosY + 0.5, black);

                                //left
                                DrawHelper.drawNewRect(posX - 1.5, posY, posX + 0.5, endPosY + 0.5, black);

                                //right
                                DrawHelper.drawNewRect(endPosX - 0.5 - 1, posY, endPosX + 0.5, endPosY + 0.5, black);

                                /* Main ESP */

                                //left
                                DrawHelper.drawNewRect(posX - 1, posY, posX + 0.5 - 0.5, endPosY, color);

                                //Button
                                DrawHelper.drawNewRect(posX, endPosY - 1, endPosX, endPosY, color);

                                //Top
                                DrawHelper.drawNewRect(posX - 1, posY, endPosX, posY + 1, color);

                                //Right
                                DrawHelper.drawNewRect(endPosX - 1, posY, endPosX, endPosY, color);
                            } else if (espMode.getValue() == espModeEn.Flat && csgoMode.getValue() == csgoModeEn.Corner && rectMode.getValue() == rectModeEn.Default) {

                                //Top Left
                                DrawHelper.drawNewRect(posX + 1, posY, posX - 1, posY + (endPosY - posY) / 4 + 0.5, black);

                                //Button Left
                                DrawHelper.drawNewRect(posX - 1, endPosY, posX + 1, endPosY - (endPosY - posY) / 4.0 - 0.5, black);

                                //Top Left Corner
                                DrawHelper.drawNewRect(posX - 1, posY - 0.5, posX + (endPosX - posX) / 3, posY + 1, black);

                                //Top Corner
                                DrawHelper.drawNewRect(endPosX - (endPosX - posX) / 3 - 0, posY - 0.5, endPosX, posY + 1.5, black);

                                //Top Right Corner
                                DrawHelper.drawNewRect(endPosX - 1.5, posY, endPosX + 0.5, posY + (endPosY - posY) / 4 + 0.5, black);

                                //Right Button Corner
                                DrawHelper.drawNewRect(endPosX - 1.5, endPosY, endPosX + 0.5, endPosY - (endPosY - posY) / 4 - 0.5, black);

                                //Left Button
                                DrawHelper.drawNewRect(posX - 1, endPosY - 1.5, posX + (endPosX - posX) / 3 + 0.5, endPosY + 0.5, black);

                                //Right Button
                                DrawHelper.drawNewRect(endPosX - (endPosX - posX) / 3 - 0.5, endPosY - 1.5, endPosX + 0.5, endPosY + 0.5, black);

                                DrawHelper.drawNewRect(posX + 0.5, posY, posX - 0.5, posY + (endPosY - posY) / 4, color);

                                DrawHelper.drawNewRect(posX + 0.5, endPosY, posX - 0.5, endPosY - (endPosY - posY) / 4, color);

                                DrawHelper.drawNewRect(posX - 0.5, posY, posX + (endPosX - posX) / 3, posY + 1, color);
                                DrawHelper.drawNewRect(endPosX - (endPosX - posX) / 3 + 0.5, posY, endPosX, posY + 1, color);
                                DrawHelper.drawNewRect(endPosX - 1, posY, endPosX, posY + (endPosY - posY) / 4 + 0.5, color);
                                DrawHelper.drawNewRect(endPosX - 1, endPosY, endPosX, endPosY - (endPosY - posY) / 4, color);
                                DrawHelper.drawNewRect(posX, endPosY - 1, posX + (endPosX - posX) / 3, endPosY, color);
                                DrawHelper.drawNewRect(endPosX - (endPosX - posX) / 3, endPosY - 1, endPosX - 0.5, endPosY, color);
                            }
                        }
                        boolean living = entity instanceof EntityLivingBase;
                        EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                        float targetHP = entityLivingBase.getHealth();
                        targetHP = MathHelper.clamp(targetHP, 0F, 24F);
                        float maxHealth = entityLivingBase.getMaxHealth();
                        double hpPercentage = (targetHP / maxHealth);
                        double hpHeight2 = (endPosY - posY) * hpPercentage;


                        if (living && healRect.getValue() && (!(espMode.getValue() == espModeEn.Box))) {
                            int colorHeal = 0;

                            switch (healcolorMode.getValue()) {
                                case Custom:
                                    colorHeal = healColor.getValue().getColor();
                                    break;
                                case Astolfo:
                                    colorHeal = DrawHelper.astolfo(false, (int) entity.height).getRGB();
                                    break;
                                case Rainbow:
                                    colorHeal = DrawHelper.rainbow(300, 1, 1).getRGB();
                                    break;
                                case Health:
                                    colorHeal = DrawHelper.getHealthColor(((EntityLivingBase) entity).getHealth(), ((EntityLivingBase) entity).getMaxHealth());
                                    break;
                            }
                            if (targetHP > 0) {
                                String string2 = "" + MathematicHelper.round(entityLivingBase.getHealth() / entityLivingBase.getMaxHealth() * 20, 1);
                                if (living && heathPercentage.getValue() && (!(espMode.getValue() == espModeEn.Box))) {
                                    if (heathPercentage.getValue()) {
                                        GlStateManager.pushMatrix();
                                        // mc.sfui18.drawStringWithOutline(string2, (float) posX - 30, (float) ((float) endPosY - hpHeight2), -1);
                                        mc.fontRenderer.drawStringWithShadow(string2, (float) posX - 30, (float) ((float) endPosY - hpHeight2), -1);
                                        GlStateManager.popMatrix();
                                    }
                                }
                                DrawHelper.drawRect(posX - 5, posY - 0.5, posX - 2.5, endPosY + 0.5, new Color(0, 0, 0, 125).getRGB());
                                DrawHelper.drawRect(posX - 4.5, endPosY, posX - 3, endPosY - hpHeight2, colorHeal);
                            }
                        }
                    }
                }
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.enableBlend();
        GL11.glPopMatrix();
        mc.entityRenderer.setupOverlayRendering();
    }

    private boolean isValid(Entity entity) {
        if (mc.gameSettings.thirdPersonView == 0 && entity == mc.player)
            return false;
        if (entity.isDead)
            return false;
        if ((entity instanceof net.minecraft.entity.passive.EntityAnimal))
            return false;
        if ((entity instanceof EntityPlayer))
            return true;
        if ((entity instanceof EntityArmorStand))
            return false;
        if ((entity instanceof IAnimals))
            return false;
        if ((entity instanceof EntityItemFrame))
            return false;
        if (entity instanceof EntityArrow)
            return false;
        if ((entity instanceof EntityMinecart))
            return false;
        if ((entity instanceof EntityBoat))
            return false;
        if ((entity instanceof EntityDragonFireball))
            return false;
        if ((entity instanceof EntityXPOrb))
            return false;
        if ((entity instanceof EntityTNTPrimed))
            return false;
        if ((entity instanceof EntityExpBottle))
            return false;
        if ((entity instanceof EntityLightningBolt))
            return false;
        if ((entity instanceof EntityPotion))
            return false;
        if ((entity instanceof Entity))
            return false;
        if (((entity instanceof net.minecraft.entity.monster.EntityMob || entity instanceof net.minecraft.entity.monster.EntitySlime || entity instanceof net.minecraft.entity.boss.EntityDragon
                || entity instanceof net.minecraft.entity.monster.EntityGolem)))
            return false;
        return entity != mc.player;
    }

    private Vector3d vectorRender2D(int scaleFactor, double x, double y, double z) {
        float xPos = (float) x;
        float yPos = (float) y;
        float zPos = (float) z;
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject(xPos, yPos, zPos, modelview, projection, viewport, vector))
            return new Vector3d((vector.get(0) / scaleFactor), ((Display.getHeight() - vector.get(1)) / scaleFactor), vector.get(2));
        return null;
    }
    public enum healcolorModeEn {
        Custom, Astolfo, Health, Rainbow, Client
    }


    public enum colorModeEn {
        Custom, Astolfo, Rainbow, Client
    }


    public enum espModeEn {
        Flat, Box
    }

    public enum rectModeEn {
        Default, Smooth
    }

    public enum csgoModeEn {
        Box, Corner
    }
}
