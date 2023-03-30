package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.EventJump;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.AstolfoAnimation;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mrzak34.thunderhack.util.render.RenderUtil.TwoColoreffect;

public class JumpCircle extends Module {


    public static AstolfoAnimation astolfo = new AstolfoAnimation();
    static List<Circle> circles = new ArrayList<>();
    public Setting<cmode> CMode = register(new Setting<>("ColorMode", cmode.Astolfo));
    public Setting<Float> range2 = register(new Setting<>("Radius", 1F, 0.1F, 3.0F));
    public Setting<Float> range = register(new Setting<>("Radius2", 3.0F, 0.1F, 3.0F));
    public Setting<Integer> lifetime = this.register(new Setting<>("live", 1000, 1, 10000));
    public Setting<mode> Mode = register(new Setting<>("Mode", mode.Jump));
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(3649978)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color2", new ColorSetting(3646789)));
    public final Setting<Integer> colorOffset1 = register(new Setting("ColorOffset", 10, 1, 20));

    public Timer timer = new Timer();
    boolean check = false;
    public JumpCircle() {
        super("JumpCircle", "JumpCircle", Category.RENDER);
    }

    @Override
    public void onUpdate() {
        if (mc.player.collidedVertically && Mode.getValue() == mode.Landing && check) {
            circles.add(new JumpCircle.Circle(new Vec3d(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ)));
            check = false;
        }
        astolfo.update();
        for (Circle circle : circles) {
            circle.update();
        }
        circles.removeIf(Circle::update);
    }

    @SubscribeEvent
    public void onJump(EventJump e) {
        if (Mode.getValue() == mode.Jump) {
            circles.add(new JumpCircle.Circle(new Vec3d(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ)));
        }
        check = true;
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {

        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.resetColor();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        double ix = -(mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double) mc.getRenderPartialTicks());
        double iy = -(mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double) mc.getRenderPartialTicks());
        double iz = -(mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double) mc.getRenderPartialTicks());
        GL11.glTranslated(ix, iy, iz);
        Collections.reverse(circles);
        try {
            for (Circle c : circles) {
                double x = c.position().x;
                double y = c.position().y;
                double z = c.position().z;
                float k = (float) c.timer.getPassedTimeMs() / (float) lifetime.getValue();
                float start = k * range.getValue();
                float end = k * range2.getValue();

                float middle = (start + end) / 2;
                GL11.glBegin(8);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));

                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) start, y, z + Math.sin(Math.toRadians(i)) * (double) start);
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, (1.0F - (float) c.timer.getPassedTimeMs() / (float) lifetime.getValue()));
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) middle, y, z + Math.sin(Math.toRadians(i)) * (double) middle);
                }
                GL11.glEnd();

                GL11.glBegin(8);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));

                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) (middle - 0.02), y, z + Math.sin(Math.toRadians(i)) * (double) (middle - 0.02));
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, (1.0F - (float) c.timer.getPassedTimeMs() / (float) lifetime.getValue()));
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) (middle + 0.02), y, z + Math.sin(Math.toRadians(i)) * (double) (middle + 0.02));
                }
                GL11.glEnd();

                GL11.glBegin(8);
                for (int i = 0; i <= 360; i += 5) {
                    int clr = getColor(i);
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, (1.0F - (float) c.timer.getPassedTimeMs() / (float) lifetime.getValue()));
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) middle, y, z + Math.sin(Math.toRadians(i)) * (double) middle);
                    GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double) end, y, z + Math.sin(Math.toRadians(i)) * (double) end);
                }
                GL11.glEnd();
            }
        } catch (Exception e) {

        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.resetColor();
        Collections.reverse(circles);
        GlStateManager.popMatrix();
        GL11.glShadeModel(GL11.GL_FLAT);

        //if(depth)
        //      GlStateManager.enableDepth();

    }


    public int getColor(int stage){
        if(CMode.getValue() == cmode.Astolfo){
            return astolfo.getColor(((stage + 90) / 360.));
        }
        else if(CMode.getValue() == cmode.Rainbow){
            return PaletteHelper.rainbow(stage,1f,1f).getRGB();
        }
        else if (CMode.getValue() == cmode.Custom){
            return color.getValue().getColorObject().getRGB();
        }
        else {
            return getColor2(color.getValue().getColorObject(),color2.getValue().getColorObject(),stage).getRGB();
        }
    }


    private Color getColor2(Color color1, Color color2, int offset){
        return TwoColoreffect(color1, color2, Math.abs(System.currentTimeMillis() / 10) / 100.0 + offset * ((20f - colorOffset1.getValue()) / 200) );
    }

    public enum mode {
        Jump, Landing
    }

    public enum cmode {
        Custom, Rainbow,TwoColor, Astolfo
    }

    class Circle {
        private final Vec3d vec;
        Timer timer = new Timer();

        Circle(Vec3d vec) {
            this.vec = vec;
            timer.reset();
        }

        Vec3d position() {
            return this.vec;
        }

        public boolean update() {
            return timer.passedMs(lifetime.getValue());
        }
    }
}
