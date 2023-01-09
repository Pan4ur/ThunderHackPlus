package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EventEntityMove;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MathUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackTrack extends Module {



    public BackTrack() {
        super("BackTrack", "BackTrack", Category.COMBAT, true, false, false);
    }


    public Setting<Integer> btticks = register(new Setting("TrackTicks", 5, 1, 15));
    public Setting<Boolean> hlaura = register(new Setting<>("HighLightAura", true));
    public Setting<Integer> calpha = register(new Setting("Alpha", 200, 1, 255));

    public  Map<EntityPlayer, List<Box> > entAndTrail = new HashMap<>();


    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        for (EntityPlayer entity : mc.world.playerEntities) {
            if(entity == mc.player){
                continue;
            }
            List<Box> trails22 = new ArrayList<>();

            entAndTrail.putIfAbsent(entity, trails22);

            if (entAndTrail.get(entity).size() > 0) {
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        if(Aura.bestBtBox != entAndTrail.get(entity).get(i) && hlaura.getValue()) {
                            drawBoundingBox(entAndTrail.get(entity).get(i), 1, new Color(255, 255, 255, calpha.getValue()));
                        } else {
                            drawBoundingBox(entAndTrail.get(entity).get(i), 1, new Color(232, 8, 0, calpha.getValue()));
                        }
                    }

            }
        }
    }




    @SubscribeEvent
    public void onEntityMove(EventEntityMove e){
        try {
            if(e.ctx() == mc.player){
                return;
            }
            if (e.ctx() instanceof EntityPlayer) {
                if(e.ctx() != null) {
                    try {
                        EntityPlayer a = (EntityPlayer) e.ctx();
                        entAndTrail.get(a).add(new Box(e.ctx().getPositionVector(), btticks.getValue()));
                    } catch (Exception ignored){

                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpdate(){
        for (EntityPlayer player : mc.world.playerEntities) {
            if(entAndTrail.get(player) == null) continue;
            entAndTrail.get(player).removeIf(Box::update);
        }
    }

    public static class Box {
        private Vec3d position;

        public int getTicks() {
            return ticks;
        }

        private int ticks;

        public Box(Vec3d position,int ticks) {
            this.position = position;
            this.ticks = ticks;
        }

        public boolean update() {
            return this.ticks-- <= 0;
        }

        public Vec3d getPosition() {
            return position;
        }
    }


    public static void drawBoundingBox(Box box, double width, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth((float) width);
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y, box.getPosition().z - 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y, box.getPosition().z - 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y, box.getPosition().z - 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y + 1.8f, box.getPosition().z - 0.3, color,color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y + 1.8f, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y + 1.8f, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y + 1.8f, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y + 1.8f, box.getPosition().z + 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y + 1.8f, box.getPosition().z - 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y, box.getPosition().z - 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x + 0.3, box.getPosition().y + 1.8f, box.getPosition().z - 0.3, color, color.getAlpha(), bufferbuilder);
        colorVertex(box.getPosition().x - 0.3, box.getPosition().y + 1.8f, box.getPosition().z - 0.3, color, color.getAlpha(), bufferbuilder);
        tessellator.draw();
    }

    private static void colorVertex(double x, double y, double z, Color color, int alpha, BufferBuilder bufferbuilder) {
        bufferbuilder.pos(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
    }


   

}
