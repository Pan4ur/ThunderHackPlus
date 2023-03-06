package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.EventEntityMove;
import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.AstolfoAnimation;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.List;


import com.mrzak34.thunderhack.events.Render3DEvent;

public class PlayerTrails extends Module{

    public PlayerTrails() {
        super("PlayerTrails", "трейлы позади-игроков", Category.RENDER);
    }


    public Setting<Float> down = register(new Setting("Down", 0.5F, 0.0F, 2.0F));
    public Setting<Float> width = register(new Setting("Height", 1.3F, 0.1F, 2.0F));
    private final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    Map<EntityPlayer, List<Trail> > entAndTrail = new HashMap<>();
    private  final Setting<Boolean> shfix =this.register( new Setting<>("ShaderFix", false));

    public Setting<modeEn> mode = register(new Setting<>("ColorMode", modeEn.Ukraine));

    public enum modeEn{
        Default,
        Ukraine,
        RUSSIA
    }
    public static AstolfoAnimation astolfo = new AstolfoAnimation();

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if(shfix.getValue()){
            return;
        }

        for (EntityPlayer entity : mc.world.playerEntities) {
            List<Trail> trails22 = new ArrayList<>();

            entAndTrail.putIfAbsent(entity, trails22);

            if (entity instanceof EntityPlayerSP && mc.gameSettings.thirdPersonView == 0) {
                continue;
            }


            float alpha = color.getValue().getAlpha() / 255f;
            mc.entityRenderer.setupCameraTransform(mc.getRenderPartialTicks(), 2);
            if (entAndTrail.get(entity).size() > 0) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBegin(GL11.GL_QUAD_STRIP);

                if(mode.getValue() == modeEn.Default) {
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(c.x, c.y, c.z, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glLineWidth(1f);


                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(c.x, c.y, c.z, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);


                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(c.x, c.y, c.z, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                    }
                } else if(mode.getValue() == modeEn.Ukraine ){
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(245f/255f, 227f/255f, 66f/255f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/2f), pos.z);
                    }
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_QUAD_STRIP);

                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(66f/255f, 102f/255f, 245f/255f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/2f), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + width.getValue(), pos.z);
                    }

                    GL11.glEnd();
                    GL11.glLineWidth(1f);


                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(66f/255f, 102f/255f, 245f/255f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);


                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(245f/255f, 227f/255f, 66f/255f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                    }
                } else if(mode.getValue() == modeEn.RUSSIA ){
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 0f, 0f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/3f), pos.z);
                    }
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_QUAD_STRIP);

                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(0f, 0f, 1f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/3f), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue() * (2f/3f)), pos.z);
                    }

                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_QUAD_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 1f, 1f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue() * (2f/3f)), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()), pos.z);
                    }

                    GL11.glEnd();
                    GL11.glLineWidth(1f);


                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 1f, 1f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);


                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 0f, 0f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                    }
                }



                GL11.glEnd();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glShadeModel(GL11.GL_FLAT);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glPopMatrix();
            }
            GlStateManager.resetColor();

        }

    }







    @SubscribeEvent
    public void onRenderPost(PreRenderEvent event) {
        if(!shfix.getValue()){
            return;
        }

        for (EntityPlayer entity : mc.world.playerEntities) {
            List<Trail> trails22 = new ArrayList<>();

            entAndTrail.putIfAbsent(entity, trails22);

            if (entity instanceof EntityPlayerSP && mc.gameSettings.thirdPersonView == 0) {
                continue;
            }


            float alpha = color.getValue().getAlpha() / 255f;
            mc.entityRenderer.setupCameraTransform(mc.getRenderPartialTicks(), 2);
            if (entAndTrail.get(entity).size() > 0) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBegin(GL11.GL_QUAD_STRIP);

                if(mode.getValue() == modeEn.Default) {
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(c.x, c.y, c.z, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glLineWidth(1f);


                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(c.x, c.y, c.z, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);


                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(c.x, c.y, c.z, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                    }
                } else if(mode.getValue() == modeEn.Ukraine ){
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(245f/255f, 227f/255f, 66f/255f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/2f), pos.z);
                    }
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_QUAD_STRIP);

                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(66f/255f, 102f/255f, 245f/255f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/2f), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + width.getValue(), pos.z);
                    }

                    GL11.glEnd();
                    GL11.glLineWidth(1f);


                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(66f/255f, 102f/255f, 245f/255f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);


                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Vec3d c = entAndTrail.get(entity).get(i).color();
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(245f/255f, 227f/255f, 66f/255f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                    }
                } else if(mode.getValue() == modeEn.RUSSIA ){
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 0f, 0f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/3f), pos.z);
                    }
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_QUAD_STRIP);

                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(0f, 0f, 1f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()/3f), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue() * (2f/3f)), pos.z);
                    }

                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_QUAD_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 1f, 1f, alpha * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue() * (2f/3f)), pos.z);
                        GL11.glVertex3d(pos.x, pos.y + down.getValue() + (width.getValue()), pos.z);
                    }

                    GL11.glEnd();
                    GL11.glLineWidth(1f);


                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 1f, 1f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + width.getValue() + down.getValue(), pos.z);
                    }


                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_STRIP);


                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        Trail ctx = entAndTrail.get(entity).get(i);
                        Vec3d pos = ctx.interpolate(mc.getRenderPartialTicks());
                        GL11.glColor4d(1f, 0f, 0f, (alpha + 0.15f) * ctx.animation(mc.getRenderPartialTicks()));
                        GL11.glVertex3d(pos.x, pos.y + down.getValue(), pos.z);
                    }
                }
                GL11.glEnd();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glShadeModel(GL11.GL_FLAT);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glPopMatrix();
            }
            GlStateManager.resetColor();
        }
    }

    @SubscribeEvent
    public void onEntityMove(EventEntityMove e){
        try {
            if (e.ctx() instanceof EntityPlayer) {
                float red = color.getValue().getRed() / 255f, green = color.getValue().getGreen() / 255f, blue = color.getValue().getBlue() / 255f;
                EntityPlayer a = (EntityPlayer) e.ctx();
                entAndTrail.get(a).add(new Trail(e.from(), e.ctx().getPositionVector(), new Vec3d(red, green, blue)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpdate(){
        astolfo.update();

        for (EntityPlayer player : mc.world.playerEntities) {
            if(entAndTrail.get(player) == null) return;
            entAndTrail.get(player).removeIf(Trail::update);
        }
    }

    public static class Trail {
        private Vec3d from, to, color;
        private int ticks, prevTicks;

        public Trail(Vec3d from, Vec3d to, Vec3d color) {
            this.from = from;
            this.to = to;
            this.ticks = 10;
            this.color = color;
        }

        public Vec3d interpolate(float pt) {
            double x = from.x + ((to.x - from.x) * pt) - mc.getRenderManager().renderPosX;
            double y = from.y + ((to.y - from.y) * pt) - mc.getRenderManager().renderPosY;
            double z = from.z + ((to.z - from.z) * pt) - mc.getRenderManager().renderPosZ;
            return new Vec3d(x, y, z);
        }

        public double animation(float pt) {
            return (this.prevTicks + (this.ticks - this.prevTicks) * pt) / 10.;
        }

        public boolean update() {
            this.prevTicks = this.ticks;
            return this.ticks-- <= 0;
        }

        public Vec3d color() {
            return color;
        }
    }


}