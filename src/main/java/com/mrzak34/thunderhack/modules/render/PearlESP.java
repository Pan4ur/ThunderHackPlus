package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MathematicHelper;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PearlESP extends Module {

    public PearlESP() {
        super("PearlESP", "PearlESP", Category.RENDER, true, false, false);
    }





    public static List<PredictionLine> lines;
    public static EntityEnderPearl entityPearl;



    private Setting<Boolean> pearlPrediction = this.register(new Setting<Boolean>("PearlPrediction", true));
    private Setting<Boolean> triangleESP = this.register(new Setting<Boolean>("TriangleESP", true));


    //distance - prev dist
    /*

    public static void handleEntityPrediction(final Entity proj) {
        if (proj instanceof EntityEnderPearl) {
            final EntityEnderPearl ent = PearlESP.entityPearl = (EntityEnderPearl)proj;
            double sx = ent.posX;
            double sy = ent.posY;
            double sz = ent.posZ;
            double mx = ent.motionX;
            double my = ent.motionY;
            double mz = ent.motionZ;
            mx += ent.k().s;
            mx += ent.getLookVec().x;
            mz += ent.k().u;
            if (!ent.k().z) {
                my += ent.k().t;
            }
            final int maxUpdateTicks = 250;
            int updateTicks = 250;
            final ArrayList<PredictionPosition> positions = new ArrayList<PredictionPosition>();
            while (updateTicks > 0) {
                final Vec3d vec3d = new Vec3d(sx, sy, sz);
                if (--updateTicks != 250) {
                    final int cnt = updateTicks % 83;
                    final float p = cnt / 83.333336f;
                    float trg = 0.0f;
                    trg = ((p > 0.5f) ? (1.0f - p * 2.0f) : (p * 2.0f));
                    final Vec3d color = new Vec3d(0.3f + 0.4f * trg, 0.5f - 0.4f * trg, 0.8999999761581421);
                    final PredictionPosition pos = new PredictionPosition(vec3d, color);
                    positions.add(pos);
                }
                final Vec3d vec3d2 = new Vec3d(sx + mx, sy + my, sz + mz);
                final RayTraceResult raytraceresult = ent.l.a(vec3d, vec3d2);
                sx += mx;
                sy += my;
                sz += mz;
                final float f1 = 0.99f;
                final float f2 = ent.j();
                mx *= f1;
                my *= f1;
                mz *= f1;
                if (!ent.aj()) {
                    my -= f2;
                }
                if (raytraceresult == null) {
                    continue;
                }
                final Vec3d color2 = new Vec3d(1.0, 1.0, 1.0);
                final PredictionPosition pos2 = new PredictionPosition(new Vec3d(sx + mx, sy + my, sz + mz), color2);
                positions.add(pos2);
                break;
            }
            addLine(positions, ent);
        }
    }

     */

    @Override
    public void onUpdate() {
        if (!pearlPrediction.getValue()) {
            return;
        }
        PearlESP.lines.removeIf(PredictionLine::remove);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (!this.triangleESP.getValue()) {
            return;
        }
        final ScaledResolution sr = new ScaledResolution(PearlESP.mc);
        final float size = 50.0f;
        final float xOffset = sr.getScaledWidth() / 2.0f - 24.5f;
        final float yOffset = sr.getScaledHeight() / 2.0f - 25.2f;
        for (final Entity entity : PearlESP.mc.world.loadedEntityList) {
            if (entity != null) {
                if (!(entity instanceof EntityEnderPearl)) {
                    continue;
                }
                GlStateManager.pushMatrix();
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks() - Util.mc.getRenderManager().viewerPosX;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks() - Util.mc.getRenderManager().viewerPosZ;
                final double cos = Math.cos(PearlESP.mc.player.rotationYaw  * 0.017453292519943295);
                final double sin = Math.sin(PearlESP.mc.player.rotationYaw  * 0.017453292519943295);
                final double rotY = -(z * cos - x * sin);
                final double rotX = -(x * cos + z * sin);
                final float angle = (float)(Math.atan2(rotY - 0.0, rotX - 0.0) * 180.0 / 3.141592653589793);
                final double xPos = size / 2.0f * Math.cos(Math.toRadians(angle)) + xOffset + size / 2.0f;
                final double y = size / 2.0f * Math.sin(Math.toRadians(angle)) + yOffset + size / 2.0f;
                GlStateManager.translate(xPos, y, 0.0);
                GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
                final String distance = MathematicHelper.round(PearlESP.mc.player.getDistance(entity), 2) + "m";
                drawTriangle(5.0f, 1.0f, 7.0f, 90.0f, new Color(5, 5, 5, 150).getRGB());
                drawTriangle(5.0f, 1.0f, 6.0f, 90.0f, new Color(0xA9A9F1).getRGB());
                PearlESP.mc.fontRenderer.drawString(distance, (int) -2.0f, (int) 9.0f, -1);
                GlStateManager.popMatrix();
            }
        }
    }

    public static void drawTriangle(final float x, final float y, final float size, final float vector, final int color) {
        GL11.glTranslated((double)x, (double)y, 0.0);
        GL11.glRotatef(180.0f + vector, 0.0f, 0.0f, 1.0f);
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
        GL11.glBegin(6);

        GL11.glVertex2d(0.0, (double)size);
        GL11.glVertex2d((double)(1.0f * size), (double)(-size));
        GL11.glVertex2d((double)(-(1.0f * size)), (double)(-size));

        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glRotatef(-180.0f - vector, 0.0f, 0.0f, 1.0f);
        GL11.glTranslated((double)(-x), (double)(-y), 0.0);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (!pearlPrediction.getValue()) {
            return;
        }
        final double ix = -(PearlESP.mc.player.lastTickPosX + (PearlESP.mc.player.posX - PearlESP.mc.player.lastTickPosX) * event.getPartialTicks());
        final double iy = -(PearlESP.mc.player.lastTickPosY + (PearlESP.mc.player.posY- PearlESP.mc.player.lastTickPosY) * event.getPartialTicks());
        final double iz = -(PearlESP.mc.player.lastTickPosZ + (PearlESP.mc.player.posZ- PearlESP.mc.player.lastTickPosZ) * event.getPartialTicks());
        GL11.glPushMatrix();
        GL11.glTranslated(ix, iy, iz);
        GL11.glDisable(3008);
        GL11.glDisable(2884);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glLineWidth(1.0f);
        GL11.glBegin(1);
        for (final PredictionLine line : PearlESP.lines) {
            final List<PredictionPosition> positions = line.positions;
            for (int i = 0; i < positions.size(); ++i) {
                if (positions.size() > i + 1) {
                    final PredictionPosition c = positions.get(i);
                    final PredictionPosition n = positions.get(i + 1);
                    final int color = new Color(0xE1CFCF).getRGB();
                    GlStateManager.color(new Color(color).getRGB() / 255.0f, new Color(color).getGreen() / 255.0f, new Color(color).getBlue() / 255.0f, new Color(color).getAlpha() / 255.0f);
                    GL11.glVertex3d(c.vector.x, c.vector.y, c.vector.z);
                    GL11.glVertex3d(n.vector.z, n.vector.z, n.vector.z);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glShadeModel(7424);
        GL11.glEnable(2884);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    static void addLine(final List<PredictionPosition> positions, final EntityEnderPearl predictable) {
        PearlESP.lines.add(new PredictionLine(positions, predictable));
    }

    static {
        PearlESP.lines = new ArrayList<PredictionLine>();
    }

    static class PredictionPosition
    {
        Vec3d vector;
        Vec3d color;

        PredictionPosition(final Vec3d vector, final Vec3d color) {
            this.vector = vector;
            this.color = color;
        }
    }

    static class PredictionLine
    {
        List<PredictionPosition> positions;
        int ownerID;

        PredictionLine(final List<PredictionPosition> positions, final EntityEnderPearl predictable) {
            this.positions = positions;
            this.ownerID = predictable.getEntityId();
        }

        boolean remove() {
          //  final EntityEnderPearl target = bib.z().f.a(this.ownerID);
            final Entity target = mc.world.getEntityByID(ownerID);
            if (!this.positions.isEmpty()) {
                this.positions.remove(0);
            }
            return this.positions.isEmpty() || target == null;
        }
    }


}
