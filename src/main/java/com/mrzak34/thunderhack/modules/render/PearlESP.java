package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.hud.RadarRewrite;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.TimeAnimation;
import com.mrzak34.thunderhack.util.Trace;
import com.mrzak34.thunderhack.util.math.AnimationMode;
import com.mrzak34.thunderhack.util.render.DrawHelper;
import com.mrzak34.thunderhack.util.render.Drawable;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mrzak34.thunderhack.gui.hud.RadarRewrite.hexColor;
import static org.lwjgl.opengl.GL11.*;

public class PearlESP extends Module {

    public static final Vec3d ORIGIN = new Vec3d(8.0, 64.0, 8.0);
    private final Setting<ColorSetting> color = this.register(new Setting<>("Color1", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> color2 = this.register(new Setting<>("Color2", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> TriangleColor = this.register(new Setting<>("TriangleColor", new ColorSetting(0x8800FF00)));
    public Setting<Float> width2 = register(new Setting("Width", 1.6F, 0.1F, 10.0F));
    public Setting<Boolean> arrows = register(new Setting("Arrows", false));
    public Setting<Boolean> pearls = register(new Setting("Pearls", false));
    public Setting<Boolean> snowballs = register(new Setting("Snowballs", false));
    public Setting<Integer> time = this.register(new Setting<>("Time", 1, 1, 10));
    public Map<Entity, List<PredictedPosition>> entAndTrail = new HashMap<>();
    protected Map<Integer, TimeAnimation> ids = new ConcurrentHashMap<>();
    protected Map<Integer, List<Trace>> traceLists = new ConcurrentHashMap<>();
    protected Map<Integer, Trace> traces = new ConcurrentHashMap<>();
    private final Setting<Boolean> triangleESP = this.register(new Setting<>("TriangleESP", true));
    private final Setting<Boolean> glow = this.register(new Setting<>("Glow", true));
    private final Setting<Float> width = register(new Setting<>("TracerHeight", 2.5f, 0.1f, 5f));
    private final Setting<Float> radius = register(new Setting<>("Radius", 50f, -50f, 50f));
    private final Setting<Float> rad22ius = register(new Setting<>("TracerDown", 3.0f, 0.1F, 20.0F));
    private final Setting<Float> tracerA = register(new Setting<>("TracerWidth", 0.50F, 0.0F, 8.0F));
    private final Setting<Integer> glowe = register(new Setting<>("GlowRadius", 10, 1, 20));
    private final Setting<Integer> glowa = register(new Setting<>("GlowAlpha", 150, 0, 255));
    private final Setting<Mode> mode = this.register(new Setting<>("LineMode", Mode.Mode1));

    public PearlESP() {
        super("Predictions", "Predictions", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mode.getValue() == Mode.Mode2) {
            PlayerToPearl(event);
        } else if (mode.getValue() == Mode.Mode1) {
            PearlToDest(event);
        } else if (mode.getValue() == Mode.Both) {
            PlayerToPearl(event);
            PearlToDest(event);
        }
    }


    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (!this.triangleESP.getValue()) {
            return;
        }
        final ScaledResolution sr = new ScaledResolution(PearlESP.mc);
        for (final Entity entity : PearlESP.mc.world.loadedEntityList) {
            if (entity != null) {
                if (!(entity instanceof EntityEnderPearl)) {
                    continue;
                }
                float xOffset = sr.getScaledWidth() / 2f;
                float yOffset = sr.getScaledHeight() / 2f;

                GlStateManager.pushMatrix();
                float yaw = RadarRewrite.getRotations(entity) - mc.player.rotationYaw;
                GL11.glTranslatef(xOffset, yOffset, 0.0F);
                GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-xOffset, -yOffset, 0.0F);
                drawTriangle(xOffset, yOffset - radius.getValue(), width.getValue() * 5F, TriangleColor.getValue().getColor());
                GL11.glTranslatef(xOffset, yOffset, 0.0F);
                GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-xOffset, -yOffset, 0.0F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GlStateManager.popMatrix();
            }
        }
    }


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
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }


    public void PlayerToPearl(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;
        for (Map.Entry<Integer, List<Trace>> entry : traceLists.entrySet()) {
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
            GL11.glLineWidth(width2.getValue());
            TimeAnimation animation = ids.get(entry.getKey());
            animation.add(event.getPartialTicks());

            GL11.glColor4f(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue(), MathHelper.clamp((float) (color.getValue().getAlpha() - animation.getCurrent() / 255.0f), 0, 255));


            entry.getValue().forEach(trace ->
            {
                GL11.glBegin(GL11.GL_LINE_STRIP);
                trace.getTrace().forEach(this::renderVec);
                GL11.glEnd();
            });

            GL11.glColor4f(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue(), MathHelper.clamp((float) (color.getValue().getAlpha() - animation.getCurrent() / 255.0f), 0, 255));


            GL11.glBegin(GL11.GL_LINE_STRIP);
            Trace trace = traces.get(entry.getKey());
            if (trace != null) {
                trace.getTrace().forEach(this::renderVec);
            }
            GL11.glEnd();
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
    }

    private void renderVec(Trace.TracePos tracePos) {
        double x = tracePos.getPos().x - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double y = tracePos.getPos().y - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double z = tracePos.getPos().z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
        GL11.glVertex3d(x, y, z);
    }


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
                if (vec.equals(ORIGIN)) {
                    continue;
                }

                if (!traces.containsKey(id) || idTrace == null) {
                    traces.put(id, new Trace(0, null, mc.world.provider.getDimensionType(), vec, new ArrayList<>()));
                    idTrace = traces.get(id);
                }

                List<Trace.TracePos> trace = idTrace.getTrace();
                Vec3d vec3d = trace.isEmpty() ? vec : trace.get(trace.size() - 1).getPos();
                if (!trace.isEmpty() && (vec.distanceTo(vec3d) > 100.0 || idTrace.getType() != mc.world.provider.getDimensionType())) {
                    traceLists.get(id).add(idTrace);
                    trace = new ArrayList<>();
                    traces.put(id, new Trace(traceLists.get(id).size() + 1, null, mc.world.provider.getDimensionType(), vec, new ArrayList<>()));
                }

                if (trace.isEmpty() || !vec.equals(vec3d)) {
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


    public void drawTriangle(float x, float y, float size, int color) {
        boolean blend = GL11.glIsEnabled(GL_BLEND);
        GL11.glEnable(GL_BLEND);
        boolean depth = GL11.glIsEnabled(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_TEST);

        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glPushMatrix();

        hexColor(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d((x - size * tracerA.getValue()), (y + size));
        GL11.glVertex2d(x, (y + size - rad22ius.getValue()));
        GL11.glVertex2d(x, y);
        GL11.glEnd();

        hexColor(ColorUtil.darker(new Color(color), 0.8f).getRGB());
        GL11.glBegin(7);
        GL11.glVertex2d(x, y); //top
        GL11.glVertex2d(x, (y + size - rad22ius.getValue())); //midle
        GL11.glVertex2d((x + size * tracerA.getValue()), (y + size)); // left right
        GL11.glVertex2d(x, y); //top
        GL11.glEnd();


        hexColor(ColorUtil.darker(new Color(color), 0.6f).getRGB());
        GL11.glBegin(7);
        GL11.glVertex2d((x - size * tracerA.getValue()), (y + size));
        GL11.glVertex2d((x + size * tracerA.getValue()), (y + size)); // left right
        GL11.glVertex2d(x, (y + size - rad22ius.getValue())); //midle
        GL11.glVertex2d((x - size * tracerA.getValue()), (y + size));
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL_TEXTURE_2D);
        if (!blend)
            GL11.glDisable(GL_BLEND);
        GL11.glDisable(GL_LINE_SMOOTH);
        if (glow.getValue())
            Drawable.drawBlurredShadow(x - size * tracerA.getValue(), y, (x + size * tracerA.getValue()) - (x - size * tracerA.getValue()), size, glowe.getValue(), DrawHelper.injectAlpha(new Color(color), glowa.getValue()));
        if (depth)
            glEnable(GL_DEPTH_TEST);
    }

    public void draw(List<PredictedPosition> list, Entity entity) {
        boolean first = true;
        boolean depth = GL11.glIsEnabled(GL_DEPTH_TEST);
        boolean texture = GL11.glIsEnabled(GL_TEXTURE_2D);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(color2.getValue().getRed() / 255.f, color2.getValue().getGreen() / 255.f, color2.getValue().getBlue() / 255.f, color2.getValue().getAlpha() / 255.f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glLineWidth(0.5f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i < list.size(); i++) {
            PredictedPosition pp = list.get(i);
            Vec3d v = new Vec3d(pp.pos.x, pp.pos.y, pp.pos.z);
            if (list.size() > 2 && first) {
                PredictedPosition next = list.get(i + 1);
                v = v.add((next.pos.x - v.x) * mc.getRenderPartialTicks(),
                        (next.pos.y - v.y) * mc.getRenderPartialTicks(),
                        (next.pos.z - v.z) * mc.getRenderPartialTicks());
            }
            GL11.glVertex3d(v.x - mc.getRenderManager().renderPosX, v.y - mc.getRenderManager().renderPosY,
                    v.z - mc.getRenderManager().renderPosZ);
            first = false;
        }
        list.removeIf(w -> w.tick < entity.ticksExisted);
        GL11.glEnd();

        if (depth)
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        if (texture)
            GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }

    public void PearlToDest(Render3DEvent event) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderPearl) {
                if (entAndTrail.get(entity) != null) {
                    draw(entAndTrail.get(entity), entity);
                }
            }
            if (entity instanceof EntityArrow) {
                if (entAndTrail.get(entity) != null) {
                    draw(entAndTrail.get(entity), entity);
                }
            }

        }
    }


    public enum Mode {
        NONE, Mode1, Mode2, Both
    }

    public static class PredictedPosition {
        public Color color;
        public Vec3d pos;
        public int tick;
    }


}
