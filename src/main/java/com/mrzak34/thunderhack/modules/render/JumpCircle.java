package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.event.events.EventJump;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;

import com.mrzak34.thunderhack.util.AstolfoAnimation;
import com.mrzak34.thunderhack.util.Timer;

import java.util.*;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class JumpCircle extends Module {


    public JumpCircle() {
        super("JumpCircle", "JumpCircle", Category.RENDER, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    public Setting<Float> range2 = register(new Setting<>("Radius", 3.8F, 0.1F, 3.0F));
    public Setting<Float> range = register(new Setting<>("Radius2", 3.8F, 0.1F, 3.0F));
    public Setting <Integer> lifetime = this.register( new Setting <> ( "live", 1000, 1, 10000) );
    public Setting<Float> ypos = register(new Setting<>("YPos", -1f, -2f, 2f));

    public Setting<mode> Mode = register(new Setting<>("Mode", mode.Jump));

    public enum mode {
        Jump, Landing;
    }

    boolean check = false;
    public Timer timer = new Timer();
    static List<Circle> circles = new ArrayList<>();
    int fix = 0;

    @Override
    public void onUpdate() {
        if(mc.player.collidedVertically && Mode.getValue() == mode.Landing && check){
            circles.add(new JumpCircle.Circle(new Vec3d(mc.player.posX, mc.player.posY + renderingFix() + ypos.getValue(), mc.player.posZ)));
            check = false;
        }


        astolfo.update();
        for(Circle circle : circles){
            circle.update();
        }
        circles.removeIf(Circle::update);
    }

    @SubscribeEvent
    public void onJump(EventJump e){
        if(Mode.getValue() == mode.Jump) {
            circles.add(new JumpCircle.Circle(new Vec3d(mc.player.posX, mc.player.posY + renderingFix() + ypos.getValue(), mc.player.posZ)));
        }
        check = true;
    }



    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        GlStateManager.pushMatrix();
        preRender();
        for (Circle c : circles) {
            double x = c.position().x;
            double y = c.position().y ;
            double z = c.position().z;
            float k = (float)c.timer.getPassedTimeMs() / (float)lifetime.getValue();
            float start = k * range.getValue();
            float end = k * range2.getValue();

            float middle = (start + end)/2;
            GL11.glBegin(8);
            for(int i = 0; i <= 360; i += 5) {

                double stage = (i + 90) / 360.;
                int clr = astolfo.getColor(stage);
                int red = ((clr >> 16) & 255);
                int green = ((clr >> 8) & 255);
                int blue = ((clr & 255));

                GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0);
                GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double)start, y, z + Math.sin(Math.toRadians(i)) * (double)start);
                GL11.glColor4f(red / 255f, green / 255f, blue / 255f, (1.0F - (float) c.timer.getPassedTimeMs() / (float) lifetime.getValue()));
                GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double)middle, y, z + Math.sin(Math.toRadians(i)) * (double)middle);
            }
            GL11.glEnd();
            GL11.glBegin(8);
            for(int i = 0; i <= 360; i += 5) {
                double stage = (i + 90) / 360.;
                int clr = astolfo.getColor(stage);
                int red = ((clr >> 16) & 255);
                int green = ((clr >> 8) & 255);
                int blue = ((clr & 255));
                GL11.glColor4f(red / 255f, green / 255f, blue / 255f, (1.0F - (float) c.timer.getPassedTimeMs() / (float) lifetime.getValue()));
                GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double)middle, y, z + Math.sin(Math.toRadians(i)) * (double)middle);
                GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 0);
                GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * (double)end, y, z + Math.sin(Math.toRadians(i)) * (double)end);
            }
            GL11.glEnd();
        }
        Collections.reverse(circles);
        postRender();
        GlStateManager.popMatrix();

    }
    public static AstolfoAnimation astolfo = new AstolfoAnimation();



    public void preRender(){
        double ix = -(mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)mc.getRenderPartialTicks());
        double iy = -(mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)mc.getRenderPartialTicks());
        double iz = -(mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)mc.getRenderPartialTicks());
        GL11.glTranslated(ix, iy, iz);

        GL11.glDisable(2884);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Collections.reverse(circles);
    }

    public void  postRender(){
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GL11.glEnable(2884);
        GL11.glEnable(3008);
    }

    float renderingFix(){
        if(fix > 20){
            fix = 0;
        }
        fix++;
        return -(fix / 100f);
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
