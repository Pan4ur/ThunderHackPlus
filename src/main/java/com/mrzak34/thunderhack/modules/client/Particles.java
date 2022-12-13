package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.particles.Particle;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Particles extends Module{
    public Particles() {
        super("Particles", "рисует партиклы в гуи", Module.Category.CLIENT, true, false, false);
        setInstance();
    }

    public Setting<Integer> delta = this.register(new Setting<Object>("Speed", 1, 0, 60));
    public Setting<Integer> amount = this.register(new Setting<Object>("Amount ", 150, 0, 666));
    public Setting<Float> scale1 = register(new Setting("Scale", 5.0F, 0.1F, 30.0F));
    public Setting<Float> linet = register(new Setting("lineT", 1f, 0.1F, 10.0F));
    public Setting<Integer> dist = this.register(new Setting<Object>("Dist ", 50, 1, 500));


    private static final float SPEED = 0.1f;
    private List<Particle> particleList = new ArrayList<>();


    private static Particles INSTANCE = new Particles();

    public static Particles getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Particles();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate(){
        for (Particle particle : particleList) {
            particle.tick(delta.getValue(), SPEED);
        }
    }


    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event) {
        if (event.getGui() != null) {
            addParticles(amount.getValue());
        } else {
            particleList.clear();
        }
    }

    public void addParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            particleList.add(Particle.generateParticle(scale1.getValue()));
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        try {
            render();
        } catch (Exception ignored){

        }
    }

    public void render() {

        for (Particle particle : particleList) {
            float nearestDistance = 0;
            Particle nearestParticle = null;
            for (Particle particle1 : particleList) {
                float distance = particle.getDistanceTo(particle1);
                if (distance <= dist.getValue() && (nearestDistance <= 0 || distance <= nearestDistance)) {
                    nearestDistance = distance;
                    nearestParticle = particle1;

                }
            }

            if (nearestParticle != null) {
                drawGradientLine(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(),linet.getValue(),particle.getColor(),nearestParticle.getColor());
            }
            RenderHelper.drawCircle(particle.getX(), particle.getY(),particle.getSize(),true, particle.getColor());
        }
    }


    public static void drawGradientLine(float x1, float y1, float x2, float y2, float lineWidth, Color color1, Color color2)
    {
        GL11.glLineWidth(lineWidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glDisable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        bufferbuilder.pos(x1, y1, 0.0D).color(color1.getRed() / 255.0f, color1.getGreen() / 255.0f, color1.getBlue() / 255.0f, color1.getAlpha() / 255.0f).endVertex();
        bufferbuilder.pos(x2, y2, 0.0D).color(color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f, color2.getAlpha() / 255.0f).endVertex();

        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL_BLEND);
        GL11.glEnable(GL_TEXTURE_2D);
        GlStateManager.disableBlend();
    }


    public static double distance(float x, float y, float x1, float y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }
}
