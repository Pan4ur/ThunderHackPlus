
package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Trajectories extends Module {
    private final Setting<ColorSetting> ncolor = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    public Setting<Boolean> landed = register(new Setting("Landed", true));
    public Setting<ColorSetting> circleColor = this.register(new Setting<>("Color", new ColorSetting(0x33da6464, true)));
    public Setting<Float> circleWidth = this.register(new Setting<>("Width", 2.5F, 0.1f, 5F));

    public Trajectories() {
        super("Trajectories", "Draws trajectories.", Category.RENDER);
    }

    public static double getRenderPosX() {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosX();
    }

    public static double getRenderPosY() {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosY();
    }

    public static void startRender() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void endRender() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static double getRenderPosZ() {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
    }

    protected boolean isThrowable(Item item) {
        return item instanceof ItemEnderPearl
                || item instanceof ItemExpBottle
                || item instanceof ItemSnowball
                || item instanceof ItemEgg
                || item instanceof ItemSplashPotion
                || item instanceof ItemLingeringPotion;
    }

    protected float getDistance(Item item) {
        return item instanceof ItemBow ? 1.0f : 0.4f;
    }

    protected float getThrowVelocity(Item item) {
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion) {
            return 0.5f;
        }
        if (item instanceof ItemExpBottle) {
            return 0.59f;
        }
        return 1.5f;
    }

    protected int getThrowPitch(Item item) {
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 20;
        }
        return 0;
    }

    protected float getGravity(Item item) {
        if (item instanceof ItemBow || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 0.05f;
        }
        return 0.03f;
    }

    protected List<Entity> getEntitiesWithinAABB(AxisAlignedBB bb) {
        final ArrayList<Entity> list = new ArrayList<>();
        final int chunkMinX = MathHelper.floor((bb.minX - 2.0) / 16.0);
        final int chunkMaxX = MathHelper.floor((bb.maxX + 2.0) / 16.0);
        final int chunkMinZ = MathHelper.floor((bb.minZ - 2.0) / 16.0);
        final int chunkMaxZ = MathHelper.floor((bb.maxZ + 2.0) / 16.0);
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (mc.world.getChunkProvider().getLoadedChunk(x, z) != null) {
                    mc.world.getChunk(x, z).getEntitiesWithinAABBForEntity(mc.player, bb, list, EntitySelectors.NOT_SPECTATING);
                }
            }
        }
        return list;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.world == null || mc.gameSettings.thirdPersonView != 0)
            return;
        if (!((mc.player.getHeldItemMainhand() != ItemStack.EMPTY && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) || (mc.player.getHeldItemMainhand() != ItemStack.EMPTY && isThrowable(mc.player.getHeldItemMainhand().getItem())) || (mc.player.getHeldItemOffhand() != ItemStack.EMPTY && isThrowable(mc.player.getHeldItemOffhand().getItem()))))
            return;
        final double renderPosX = getRenderPosX();
        final double renderPosY = getRenderPosY();
        final double renderPosZ = getRenderPosZ();
        Item item = null;
        if (mc.player.getHeldItemMainhand() != ItemStack.EMPTY && (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow || isThrowable(mc.player.getHeldItemMainhand().getItem()))) {
            item = mc.player.getHeldItemMainhand().getItem();
        } else if (mc.player.getHeldItemOffhand() != ItemStack.EMPTY && isThrowable(mc.player.getHeldItemOffhand().getItem())) {
            item = mc.player.getHeldItemOffhand().getItem();
        }
        if (item == null) return;
        startRender();
        double posX = renderPosX - MathHelper.cos(mc.player.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        double posY = renderPosY + mc.player.getEyeHeight() - 0.1000000014901161;
        double posZ = renderPosZ - MathHelper.sin(mc.player.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        final float maxDist = getDistance(item);
        double motionX = -MathHelper.sin(mc.player.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.player.rotationPitch / 180.0f * 3.1415927f) * maxDist;
        double motionY = -MathHelper.sin((mc.player.rotationPitch - getThrowPitch(item)) / 180.0f * 3.141593f) * maxDist;
        double motionZ = MathHelper.cos(mc.player.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(mc.player.rotationPitch / 180.0f * 3.1415927f) * maxDist;
        int var6 = 72000 - mc.player.getItemInUseCount();
        float power = var6 / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (power > 1.0f) {
            power = 1.0f;
        }
        final float distance = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;

        final float pow = (item instanceof ItemBow ? (power * 2.0f) : 1.0f) * getThrowVelocity(item);
        motionX *= pow;
        motionY *= pow;
        motionZ *= pow;
        if (!mc.player.onGround)
            motionY += mc.player.motionY;

        GlStateManager.color(ncolor.getValue().getRed() / 255.f, ncolor.getValue().getGreen() / 255.f, ncolor.getValue().getBlue() / 255.f, ncolor.getValue().getAlpha() / 255.f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        final float size = (float) ((item instanceof ItemBow) ? 0.3 : 0.25);
        boolean hasLanded = false;
        Entity landingOnEntity = null;
        RayTraceResult landingPosition = null;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        while (!hasLanded && posY > 0.0) {
            Vec3d present = new Vec3d(posX, posY, posZ);
            Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult possibleLandingStrip = mc.world.rayTraceBlocks(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != RayTraceResult.Type.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
            List<Entity> entities = getEntitiesWithinAABB(arrowBox.offset(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (Object entity : entities) {
                Entity boundingBox = (Entity) entity;
                if (boundingBox.canBeCollidedWith() && boundingBox != mc.player) {
                    float var7 = 0.3f;
                    AxisAlignedBB var8 = boundingBox.getEntityBoundingBox().expand(var7, var7, var7);
                    RayTraceResult possibleEntityLanding = var8.calculateIntercept(present, future);
                    if (possibleEntityLanding == null) {
                        continue;
                    }
                    hasLanded = true;
                    landingOnEntity = boundingBox;
                    landingPosition = possibleEntityLanding;
                }
            }
            if (landingOnEntity != null) {
                GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            final float motionAdjustment = 0.99f;
            motionX *= motionAdjustment;
            motionY *= motionAdjustment;
            motionZ *= motionAdjustment;
            motionY -= getGravity(item);
            drawLine3D(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
        }
        GL11.glEnd();
        if (landed.getValue() && landingPosition != null && landingPosition.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.translate(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
            final int side = landingPosition.sideHit.getIndex();
            if (side == 2) {
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            } else if (side == 3) {
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            } else if (side == 4) {
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            } else if (side == 5) {
                GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
            }
            if (landingOnEntity != null) {
                circle();
            }
            circle();
        }
        endRender();
    }

    public void circle() {
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
        double x = 0;
        double y = 0;
        double z = 0;
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
                GL11.glColor4f(red / 255F, green / 255F, blue / 255F, 1F);
            } else {
                GL11.glColor4f(circleColor.getValue().getRed() / 255F, circleColor.getValue().getGreen() / 255F, circleColor.getValue().getBlue() / 255F, 1F);
            }
            GL11.glVertex3d(vecs.get(j).x, vecs.get(j).y, vecs.get(j).z);
            GL11.glVertex3d(vecs.get(j + 1).x, vecs.get(j + 1).y, vecs.get(j + 1).z);
            hue += (1F / 360F);
            rgb = Color.getHSBColor(hue, hsb[1], hsb[2]).getRGB();
        }
        GL11.glEnd();

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

    public void drawLine3D(double var1, double var2, double var3) {
        GL11.glVertex3d(var1, var2, var3);
    }
}
