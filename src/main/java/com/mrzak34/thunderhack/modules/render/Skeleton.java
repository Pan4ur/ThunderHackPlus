package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.BlockUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class  Skeleton
        extends Module {
    private static final HashMap<EntityPlayer, float[][]> entities = new HashMap();
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Boolean> invisibles = this.register(new Setting<Boolean>("Invisibles", false));
    public final Setting<ColorSetting> Color3 = this.register(new Setting<>("FriendColor", new ColorSetting(0x8800FF00)));

    public Skeleton() {
        super("Skeleton", "скелетон есп-на игроков", Module.Category.RENDER);
    }

    public static void addEntity(EntityPlayer e, ModelPlayer model) {
        entities.put(e, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }

    private Vec3d getVec3(Render3DEvent event, EntityPlayer e) {
        float pt = event.getPartialTicks();
        double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) pt;
        double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) pt;
        double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) pt;
        return new Vec3d(x, y, z);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Skeleton.fullNullCheck()) {
            return;
        }
        this.startEnd(true);
        GL11.glEnable(2903);
        GL11.glDisable(2848);
        entities.keySet().removeIf(this::doesntContain);
        Skeleton.mc.world.playerEntities.forEach(e -> this.drawSkeleton(event, e));
        Gui.drawRect(0, 0, 0, 0, 0);
        this.startEnd(false);
    }

    private void drawSkeleton(Render3DEvent event, EntityPlayer e) {
        if (!BlockUtils.isPosInFov(new BlockPos(e.posX, e.posY, e.posZ))) {
            return;
        }
        if (e.isInvisible() && !this.invisibles.getValue().booleanValue()) {
            return;
        }
        float[][] entPos = entities.get(e);
        if (entPos != null && e.isEntityAlive() && !e.isDead && e != Skeleton.mc.player && !e.isPlayerSleeping()) {
            GL11.glPushMatrix();
            GL11.glEnable(2848);
            GL11.glLineWidth(this.lineWidth.getValue());
            if (Thunderhack.friendManager.isFriend(e.getName())) {
                GlStateManager.color(Color3.getValue().getRed()/255f,Color3.getValue().getGreen()/255f,Color3.getValue().getBlue()/255f,Color3.getValue().getAlpha()/255f);
            } else {
                GlStateManager.color(Color3.getValue().getRed()/255f,Color3.getValue().getGreen()/255f,Color3.getValue().getBlue()/255f,Color3.getValue().getAlpha()/255f);
            }
            Vec3d vec = this.getVec3(event, e);
            double x = vec.x - Skeleton.mc.getRenderManager().renderPosX;
            double y = vec.y - Skeleton.mc.getRenderManager().renderPosY;
            double z = vec.z - Skeleton.mc.getRenderManager().renderPosZ;
            GL11.glTranslated(x, y, z);
            float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
            GL11.glRotatef(-xOff, 0.0f, 1.0f, 0.0f);
            GL11.glTranslated(0.0, 0.0, e.isSneaking() ? -0.235 : 0.0);
            float yOff = e.isSneaking() ? 0.6f : 0.75f;
            GL11.glPushMatrix();
            GL11.glTranslated(-0.125, yOff, 0.0);
            if (entPos[3][0] != 0.0f) {
                GL11.glRotatef(entPos[3][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (entPos[3][1] != 0.0f) {
                GL11.glRotatef(entPos[3][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (entPos[3][2] != 0.0f) {
                GL11.glRotatef(entPos[3][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            GL11.glBegin(3);
            GL11.glVertex3d(0.0, 0.0, 0.0);
            GL11.glVertex3d(0.0, -yOff, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.125, yOff, 0.0);
            if (entPos[4][0] != 0.0f) {
                GL11.glRotatef(entPos[4][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (entPos[4][1] != 0.0f) {
                GL11.glRotatef(entPos[4][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (entPos[4][2] != 0.0f) {
                GL11.glRotatef(entPos[4][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            GL11.glBegin(3);
            GL11.glVertex3d(0.0, 0.0, 0.0);
            GL11.glVertex3d(0.0, -yOff, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glTranslated(0.0, 0.0, e.isSneaking() ? 0.25 : 0.0);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, e.isSneaking() ? -0.05 : 0.0, e.isSneaking() ? -0.01725 : 0.0);
            GL11.glPushMatrix();
            GL11.glTranslated(-0.375, (double) yOff + 0.55, 0.0);
            if (entPos[1][0] != 0.0f) {
                GL11.glRotatef(entPos[1][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (entPos[1][1] != 0.0f) {
                GL11.glRotatef(entPos[1][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (entPos[1][2] != 0.0f) {
                GL11.glRotatef(-entPos[1][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            GL11.glBegin(3);
            GL11.glVertex3d(0.0, 0.0, 0.0);
            GL11.glVertex3d(0.0, -0.5, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.375, (double) yOff + 0.55, 0.0);
            if (entPos[2][0] != 0.0f) {
                GL11.glRotatef(entPos[2][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (entPos[2][1] != 0.0f) {
                GL11.glRotatef(entPos[2][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (entPos[2][2] != 0.0f) {
                GL11.glRotatef(-entPos[2][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            GL11.glBegin(3);
            GL11.glVertex3d(0.0, 0.0, 0.0);
            GL11.glVertex3d(0.0, -0.5, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glRotatef(xOff - e.rotationYawHead, 0.0f, 1.0f, 0.0f);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, (double) yOff + 0.55, 0.0);
            if (entPos[0][0] != 0.0f) {
                GL11.glRotatef(entPos[0][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            GL11.glBegin(3);
            GL11.glVertex3d(0.0, 0.0, 0.0);
            GL11.glVertex3d(0.0, 0.3, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glRotatef(e.isSneaking() ? 25.0f : 0.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslated(0.0, e.isSneaking() ? -0.16175 : 0.0, e.isSneaking() ? -0.48025 : 0.0);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, yOff, 0.0);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.125, 0.0, 0.0);
            GL11.glVertex3d(0.125, 0.0, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, yOff, 0.0);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0, 0.0, 0.0);
            GL11.glVertex3d(0.0, 0.55, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, (double) yOff + 0.55, 0.0);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.375, 0.0, 0.0);
            GL11.glVertex3d(0.375, 0.0, 0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private void startEnd(boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask((!revert ? 1 : 0) != 0);
    }

    private boolean doesntContain(EntityPlayer entityPlayer) {
        return !Skeleton.mc.world.playerEntities.contains(entityPlayer);
    }
}

