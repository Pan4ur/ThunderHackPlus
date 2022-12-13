package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render3DEvent;


import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Trails extends Module{
    public Trails() {
        super("Trails", "viewmodle", Category.RENDER, true, false, false);
    }



    public Setting<Float> width = register(new Setting("Width", 1.6F, 0.1F, 10.0F));

    public Setting<Boolean> arrows = register(new Setting("Arrows", false));
    public Setting<Boolean> pearls = register(new Setting("Pearls", false));
    public Setting<Boolean> snowballs = register(new Setting("Snowballs", false));
    public Setting <Integer> time = this.register ( new Setting <> ( "Time", 1, 1, 10) );
    private Setting<mode2> Mode2 = register(new Setting("Color Mode", mode2.Custom));
    public enum mode2 {
        Custom, Rainbow, Astolfo;
    }

    private final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));



    protected Map<Integer, TimeAnimation> ids = new ConcurrentHashMap<>();
    protected Map<Integer, List<Trace>> traceLists = new ConcurrentHashMap<>();
    protected Map<Integer, Trace> traces = new ConcurrentHashMap<>();


    @Override
    public void onEnable() {
        ids = new ConcurrentHashMap<>();
        traces = new ConcurrentHashMap<>();
        traceLists = new ConcurrentHashMap<>();
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            for (int id : ((SPacketDestroyEntities) event.getPacket()).getEntityIDs()) {
                if (ids.containsKey(id)) {
                    ids.get(id).play();
                }
            }
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            if ((pearls.getValue() && ((SPacketSpawnObject) event.getPacket()).getType() == 65)
                    || (arrows.getValue() && ((SPacketSpawnObject) event.getPacket()).getType() == 60)
                    || (snowballs.getValue() && ((SPacketSpawnObject) event.getPacket()).getType() == 61)) {
                //Earthhack.getLogger().info(((SPacketSpawnObject) event.getPacket()).getEntityID());
                TimeAnimation animation = new TimeAnimation(time.getValue() * 1000, 0, color.getValue().getAlpha(), false, AnimationMode.LINEAR);
                animation.stop();
                ids.put(((SPacketSpawnObject) event.getPacket()).getEntityID(), animation);
                traceLists.put(((SPacketSpawnObject) event.getPacket()).getEntityID(), new ArrayList<>());
                try {
                    traces.put(((SPacketSpawnObject) event.getPacket()).getEntityID(), new Trace(0,
                            null,
                            mc.world.provider.getDimensionType(),
                            new Vec3d(((SPacketSpawnObject) event.getPacket()).getX(), ((SPacketSpawnObject) event.getPacket()).getY(), ((SPacketSpawnObject) event.getPacket()).getZ()),
                            new ArrayList<>()));
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {






        if (mc.world == null || mc.player == null) return;



        for (Map.Entry<Integer, List<Trace>> entry : traceLists.entrySet()) {
            startRender();
            GL11.glLineWidth(width.getValue());
            TimeAnimation animation = ids.get(entry.getKey());
            animation.add(event.getPartialTicks());

            if(Mode2.getValue() == mode2.Custom) {
                GL11.glColor4f(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue(), MathHelper.clamp((float) (color.getValue().getAlpha() - animation.getCurrent() / 255.0f), 0, 255));
            }
            if(Mode2.getValue() == mode2.Astolfo) {
                Color color;
                color = PaletteHelper.astolfo(false, 1);
                GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp((float) (color.getAlpha() - animation.getCurrent() / 255.0f), 0, 255));
            }
            if(Mode2.getValue() == mode2.Rainbow) {
                Color color;
                color = PaletteHelper.rainbow(300, 1, 1);
                GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp((float) (color.getAlpha() - animation.getCurrent() / 255.0f), 0, 255));
            }

            entry.getValue().forEach(trace ->
            {
                GL11.glBegin(GL11.GL_LINE_STRIP);
                trace.getTrace().forEach(this::renderVec);
                GL11.glEnd();
            });

            if(Mode2.getValue() == mode2.Custom) {
                GL11.glColor4f(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue(), MathHelper.clamp((float) (color.getValue().getAlpha() - animation.getCurrent() / 255.0f), 0, 255));
            }
            if(Mode2.getValue() == mode2.Astolfo) {
                Color color2;
                color2 = PaletteHelper.astolfo(false, 1);
                GL11.glColor4f(color2.getRed(), color2.getGreen(), color2.getBlue(), MathHelper.clamp((float) (color2.getAlpha() - animation.getCurrent() / 255.0f), 0, 255));
            }
            if(Mode2.getValue() == mode2.Astolfo) {
                Color color2;
                color2 = PaletteHelper.rainbow(300, 1, 1);
                GL11.glColor4f(color2.getRed(), color2.getGreen(), color2.getBlue(), MathHelper.clamp((float) (color2.getAlpha() - animation.getCurrent() / 255.0f), 0, 255));
            }


            GL11.glBegin(GL11.GL_LINE_STRIP);
            Trace trace = traces.get(entry.getKey());
            if (trace != null) {
                trace.getTrace().forEach(this::renderVec);
            }
            GL11.glEnd();
            endRender();
        }
    }

    private void renderVec(Trace.TracePos tracePos)
    {
        double x = tracePos.getPos().x - getRenderPosX();
        double y = tracePos.getPos().y - getRenderPosY();
        double z = tracePos.getPos().z - getRenderPosZ();
        GL11.glVertex3d(x, y, z);
    }


    public static final Vec3d ORIGIN = new Vec3d(8.0, 64.0, 8.0);

    @Override
    public void onUpdate() {


        if (mc.world == null) return;
        if (ids.keySet().isEmpty()) return;




        for (Integer id : ids.keySet()) {
            if (id == null) continue;
            if (mc.world.loadedEntityList == null) return;
            if (mc.world.loadedEntityList.isEmpty()) return;
            Trace idTrace = traces.get(id);
            Entity entity = mc.world.getEntityByID(id);
            if (entity != null) {
                Vec3d vec = entity.getPositionVector();
                if (vec == null) continue;
                if (vec.equals(ORIGIN))
                {
                    continue;
                }

                if (!traces.containsKey(id) || idTrace == null)
                {
                    traces.put(id, new Trace(0, null, mc.world.provider.getDimensionType(), vec, new ArrayList<>()));
                    idTrace = traces.get(id);
                }

                List<Trace.TracePos> trace = idTrace.getTrace();
                Vec3d vec3d = trace.isEmpty() ? vec : trace.get(trace.size() - 1).getPos();
                if (!trace.isEmpty() && (vec.distanceTo(vec3d) > 100.0 || idTrace.getType() != mc.world.provider.getDimensionType()))
                {
                    traceLists.get(id).add(idTrace);
                    trace = new ArrayList<>();
                    traces.put(id, new Trace(traceLists.get(id).size() + 1, null, mc.world.provider.getDimensionType(), vec, new ArrayList<>()));
                }

                if (trace.isEmpty() || !vec.equals(vec3d))
                {
                    trace.add(new Trace.TracePos(vec));
                }
            }

            TimeAnimation animation = ids.get(id);

            if (entity instanceof EntityArrow && (entity.onGround || entity.collided || !entity.isAirBorne)) {
                animation.play();
            }

            if (animation != null && color.getValue().getAlpha() - animation.getCurrent() <= 0/*animation.getCurrent() >= color.getAlpha()*/) {
                animation.stop();
                ids.remove(id);
                traceLists.remove(id);
                traces.remove(id);
            }
        }
    }

    public static double getRenderPosX()
    {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosX();
    }

    public static double getRenderPosY()
    {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosY();
    }
    public static void startRender()
    {
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

    public static void endRender()
    {
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
    public static double getRenderPosZ()
    {
        return ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
    }
}