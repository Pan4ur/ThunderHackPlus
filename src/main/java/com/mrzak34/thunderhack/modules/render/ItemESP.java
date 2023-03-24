package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.math.AstolfoAnimation;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import com.mrzak34.thunderhack.util.render.RenderHelper;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ItemESP extends Module {
    public static AstolfoAnimation astolfo2 = new AstolfoAnimation();
    private final Setting<Boolean> entityName = (Setting<Boolean>) this.register(new Setting("Name", true));
    private final Setting<Boolean> fullBox = (Setting<Boolean>) this.register(new Setting("Full Box", true));
    private final Setting<ColorSetting> cc = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> cc2 = this.register(new Setting<>("Color2", new ColorSetting(0x8800FF00)));
    private final int black = Color.BLACK.getRGB();
    public Setting<Float> scalefactor = register(new Setting("Raytrace", 2.0F, 0.1F, 4.0F));
    public Setting<Float> rads = register(new Setting("radius", 2.0F, 0.1F, 1.0F));
    private final Setting<mode> Mode = register(new Setting("Render Mode", mode.render2D));
    private final Setting<mode2> Mode2 = register(new Setting("Color Mode", mode2.Astolfo));

    public ItemESP() {
        super("ItemESP", "юспишки для вещей", Module.Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        for (Entity item : mc.world.loadedEntityList) {
            if (item instanceof EntityItem) {
                int color = 0;
                if (Mode2.getValue() == mode2.Custom) {
                    color = cc.getValue().getColor();
                }
                if (Mode2.getValue() == mode2.Astolfo) {
                    color = PaletteHelper.astolfo(false, (int) item.height).getRGB();
                }
                if (Mode.getValue() == mode.render3D) {
                    GlStateManager.pushMatrix();
                    RenderHelper.drawEntityBox(item, new Color(color), cc2.getValue().getColorObject(), fullBox.getValue(), fullBox.getValue() ? 0.15F : 0.90F);
                    GlStateManager.popMatrix();
                }
                if (Mode.getValue() == mode.Circle) {
                    RenderHelper.drawCircle3D(item, rads.getValue(), event.getPartialTicks(), 32, 2, color, Mode2.getValue() == mode2.Astolfo);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        Float scaleFactor = scalefactor.getValue();
        double scaling = scaleFactor / Math.pow(scaleFactor, 2);
        GlStateManager.scale(scaling, scaling, scaling);
        Color c = cc.getValue().getColorObject();
        int color = 0;

        if (Mode2.getValue() == mode2.Custom) {
            color = c.getRGB();
        }
        if (Mode2.getValue() == mode2.Astolfo) {
            color = PaletteHelper.astolfo(false, 1).getRGB();
        }
        float scale = 1;
        for (Entity entity : mc.world.loadedEntityList) {
            if (isValid(entity) && RenderHelper.isInViewFrustum(entity)) {
                EntityItem entityItem = (EntityItem) entity;
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks();
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks();
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks();
                AxisAlignedBB axisAlignedBB2 = entity.getEntityBoundingBox();
                AxisAlignedBB axisAlignedBB = new AxisAlignedBB(axisAlignedBB2.minX - entity.posX + x - 0.05, axisAlignedBB2.minY - entity.posY + y, axisAlignedBB2.minZ - entity.posZ + z - 0.05, axisAlignedBB2.maxX - entity.posX + x + 0.05, axisAlignedBB2.maxY - entity.posY + y + 0.15, axisAlignedBB2.maxZ - entity.posZ + z + 0.05);
                Vector3d[] vectors = new Vector3d[]{new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
                ((IEntityRenderer)mc.entityRenderer).invokeSetupCameraTransform(event.getPartialTicks(), 0);

                Vector4d position = null;
                for (Vector3d vector : vectors) {
                    vector = project2D(scaleFactor, vector.x - ((IRenderManager)Util.mc.getRenderManager()).getRenderPosX(), vector.y - ((IRenderManager)Util.mc.getRenderManager()).getRenderPosY(), vector.z - ((IRenderManager)Util.mc.getRenderManager()).getRenderPosZ());
                    if (vector != null && vector.z > 0 && vector.z < 1) {
                        if (position == null)
                            position = new Vector4d(vector.x, vector.y, vector.z, 0);
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

                    if (Mode.getValue() == mode.render2D) {
                        RenderUtil.drawRect(posX - 1F, posY, posX + 0.5, endPosY + 0.5, black);
                        RenderUtil.drawRect(posX - 1F, posY - 0.5, endPosX + 0.5, posY + 0.5 + 0.5, black);
                        RenderUtil.drawRect(endPosX - 0.5 - 0.5, posY, endPosX + 0.5, endPosY + 0.5, black);
                        RenderUtil.drawRect(posX - 1, endPosY - 0.5 - 0.5, endPosX + 0.5, endPosY + 0.5, black);
                        RenderUtil.drawRect(posX - 0.5, posY, posX + 0.5 - 0.5, endPosY, color);
                        RenderUtil.drawRect(posX, endPosY - 0.5, endPosX, endPosY, color);
                        RenderUtil.drawRect(posX - 0.5, posY, endPosX, posY + 0.5, color);
                        RenderUtil.drawRect(endPosX - 0.5, posY, endPosX, endPosY, color);
                    }

                    float diff = (float) (endPosX - posX) / 2;
                    float textWidth = (Util.fr.getStringWidth(entityItem.getItem().getDisplayName()) * scale);
                    float tagX = (float) ((posX + diff - textWidth / 2) * scale);
                    if (entityName.getValue()) {
                        Util.fr.drawStringWithShadow(entityItem.getItem().getDisplayName(), tagX, (float) posY - 10, -1);
                    }
                }
            }
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        mc.entityRenderer.setupOverlayRendering();
    }

    private Vector3d project2D(Float scaleFactor, double x, double y, double z) {
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

    private boolean isValid(Entity entity) {
        return entity instanceof EntityItem;
    }

    @Override
    public void onUpdate() {
        astolfo2.update();
    }


    public enum mode {
        render2D, render3D, Circle
    }

    public enum mode2 {
        Custom, Astolfo
    }


}
