package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.EventEntityMove;
import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
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
        super("BackTrack", "BackTrack", Category.COMBAT);
    }


    public Setting<Integer> btticks = register(new Setting("TrackTicks", 5, 1, 15));
    public Setting<Boolean> hlaura = register(new Setting<>("HighLightAura", true));


    public final Setting<ColorSetting> color1 = this.register(new Setting<>("Color", new ColorSetting(-2009289807)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("HighLightColor", new ColorSetting(-2009289807)));



    public  Map<EntityPlayer, List<Box> > entAndTrail = new HashMap<>();
    private Setting<RenderMode> renderMode = register(new Setting("RenderMode", RenderMode.Chams));

    public enum RenderMode {
        Box, Chams;
    }

    @SubscribeEvent
    public void onPreRenderEvent(PreRenderEvent event) {
        for (EntityPlayer entity : mc.world.playerEntities) {
            if(entity == mc.player){
                continue;
            }
            List<Box> trails22 = new ArrayList<>();

            entAndTrail.putIfAbsent(entity, trails22);

            if (entAndTrail.get(entity).size() > 0) {
                    for (int i = 0; i < entAndTrail.get(entity).size(); i++) {
                        GlStateManager.pushMatrix();
                        if(Aura.bestBtBox != entAndTrail.get(entity).get(i) && hlaura.getValue()) {
                            if(renderMode.getValue() == RenderMode.Box) {
                                drawBoundingBox(entAndTrail.get(entity).get(i), 1, color1.getValue().getColorObject());
                            } else {
                                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                                GlStateManager.glLineWidth(1.5F);
                                GlStateManager.disableTexture2D();
                                GlStateManager.enableBlend();
                                GlStateManager.disableLighting();
                                GlStateManager.enableAlpha();
                                entAndTrail.get(entity).get(i).modelPlayer.bipedLeftLegwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedRightLegwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedLeftArmwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedRightArmwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedBodyWear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedHead.showModel = true;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedHeadwear.showModel = false;
                                GlStateManager.color(color1.getValue().getRed() / 255f, color1.getValue().getGreen() / 255f, color1.getValue().getBlue() / 255f, color1.getValue().getAlpha() / 255f );
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                                renderEntity(entAndTrail.get(entity).get(i), entAndTrail.get(entity).get(i).modelPlayer, entAndTrail.get(entity).get(i).limbSwing, entAndTrail.get(entity).get(i).limbSwingAmount, 20, entAndTrail.get(entity).get(i).Yaw, entAndTrail.get(entity).get(i).Pitch, 1, entAndTrail.get(entity).get(i).ent);
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                                renderEntity(entAndTrail.get(entity).get(i), entAndTrail.get(entity).get(i).modelPlayer, entAndTrail.get(entity).get(i).limbSwing, entAndTrail.get(entity).get(i).limbSwingAmount, 20, entAndTrail.get(entity).get(i).Yaw, entAndTrail.get(entity).get(i).Pitch, 1, entAndTrail.get(entity).get(i).ent);
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                                GlStateManager.enableTexture2D();
                                GlStateManager.enableBlend();
                            }
                        } else {
                            if(renderMode.getValue() == RenderMode.Box) {
                                drawBoundingBox(entAndTrail.get(entity).get(i), 1, color2.getValue().getColorObject());
                            } else {
                                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                                GlStateManager.glLineWidth(1.5F);
                                GlStateManager.disableTexture2D();
                                GlStateManager.enableBlend();
                                GlStateManager.disableLighting();
                                GlStateManager.enableAlpha();
                                entAndTrail.get(entity).get(i).modelPlayer.bipedLeftLegwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedRightLegwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedLeftArmwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedRightArmwear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedBodyWear.showModel = false;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedHead.showModel = true;
                                entAndTrail.get(entity).get(i).modelPlayer.bipedHeadwear.showModel = false;
                                GlStateManager.color(color2.getValue().getRed() / 255f, color2.getValue().getGreen() / 255f, color2.getValue().getBlue() / 255f, color2.getValue().getAlpha() / 255f);
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                                renderEntity(entAndTrail.get(entity).get(i), entAndTrail.get(entity).get(i).modelPlayer, entAndTrail.get(entity).get(i).limbSwing, entAndTrail.get(entity).get(i).limbSwingAmount, 20, entAndTrail.get(entity).get(i).Yaw, entAndTrail.get(entity).get(i).Pitch, 1, entAndTrail.get(entity).get(i).ent);
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                                renderEntity(entAndTrail.get(entity).get(i), entAndTrail.get(entity).get(i).modelPlayer, entAndTrail.get(entity).get(i).limbSwing, entAndTrail.get(entity).get(i).limbSwingAmount, 20, entAndTrail.get(entity).get(i).Yaw, entAndTrail.get(entity).get(i).Pitch, 1, entAndTrail.get(entity).get(i).ent);
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                                GlStateManager.enableTexture2D();
                                GlStateManager.enableBlend();
                            }
                        }
                        GlStateManager.popMatrix();
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
                        entAndTrail.get(a).add(new Box(e.ctx().getPositionVector(), btticks.getValue(), a.limbSwing,a.limbSwingAmount,a.rotationYaw,a.rotationPitch,(EntityPlayer) e.ctx()));
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
        private float limbSwing, limbSwingAmount,  Yaw,  Pitch;
        private final ModelPlayer modelPlayer;
        private EntityPlayer ent;

        public int getTicks() {
            return ticks;
        }

        private int ticks;

        public Box(Vec3d position,int ticks, float limbswing, float limbSwingAmount, float  Yaw,float Pitch, EntityPlayer ent) {
            this.position = position;
            this.ticks = ticks;
            this.modelPlayer = new ModelPlayer( 0, false );
            this.limbSwing = limbswing;
            this.limbSwingAmount = limbSwingAmount;
            this.Pitch  = Pitch;
            this.Yaw = Yaw;
            this.ent = ent;
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


    public static void renderEntity (Box entity, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityLivingBase entityIn ) {
        if ( modelBase instanceof ModelPlayer) {
            ModelPlayer modelPlayer = ( ( ModelPlayer ) modelBase );
            modelPlayer.bipedBodyWear.showModel = false;
            modelPlayer.bipedLeftLegwear.showModel = false;
            modelPlayer.bipedRightLegwear.showModel = false;
            modelPlayer.bipedLeftArmwear.showModel = false;
            modelPlayer.bipedRightArmwear.showModel = false;
            modelPlayer.bipedHeadwear.showModel = true;
            modelPlayer.bipedHead.showModel = false;
        }

        float partialTicks = mc.getRenderPartialTicks( );
        double x = entity.position.x - mc.getRenderManager( ).viewerPosX;
        double y = entity.position.y - mc.getRenderManager( ).viewerPosY;
        double z = entity.position.z - mc.getRenderManager( ).viewerPosZ;

        GlStateManager.pushMatrix( );

        GlStateManager.translate( ( float ) x, ( float ) y, ( float ) z );
        GlStateManager.rotate( 180 - entity.Yaw, 0, 1, 0 );
        float f4 = prepareScale(scale );
        float yaw = entity.Yaw;

        boolean alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        GlStateManager.enableAlpha( );
        modelBase.setLivingAnimations( entityIn, limbSwing, limbSwingAmount, partialTicks );
        modelBase.setRotationAngles( limbSwing, limbSwingAmount, 0, yaw, entity.Pitch, f4, entityIn );
        modelBase.render( entityIn, limbSwing, limbSwingAmount, 0, yaw, entity.Pitch, f4 );

        if(!alpha)
            GlStateManager.disableAlpha();
        GlStateManager.popMatrix( );
    }

    private static float prepareScale (float scale ) {
        GlStateManager.enableRescaleNormal( );
        GlStateManager.scale( -1.0F, -1.0F, 1.0F );
        double widthX = 0.6f;
        double widthZ = 0.6f;

        GlStateManager.scale( scale + widthX, scale * 1.8f, scale + widthZ );
        float f = 0.0625F;

        GlStateManager.translate( 0.0F, -1.501F, 0.0F );
        return f;
    }

}
