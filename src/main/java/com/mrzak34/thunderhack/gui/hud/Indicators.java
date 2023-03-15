package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.misc.Timer;
import com.mrzak34.thunderhack.modules.movement.DMGFly;
import com.mrzak34.thunderhack.modules.movement.MSTSpeed;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.AstolfoAnimation;
import com.mrzak34.thunderhack.util.math.DynamicAnimation;
import com.mrzak34.thunderhack.util.render.DrawHelper;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Indicators extends Module {
    public static AstolfoAnimation astolfo = new AstolfoAnimation();
    private static final List<Indicator> indicators = new java.util.ArrayList();
    private final Setting<ColorSetting> cc = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> cs = this.register(new Setting<>("RectColor", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));
    public Setting<Boolean> dmgflyy = register(new Setting<>("DMGFly", true));
    public Setting<Boolean> Memoryy = register(new Setting<>("Memory", true));
    public Setting<Boolean> Timerr = register(new Setting<>("Timer", true));
    public Setting<Boolean> TPS = register(new Setting<>("TPS", true));
    public Setting<Boolean> dmgspeed = register(new Setting<>("DMGSpeed", true));
    public Setting<Boolean> blur = register(new Setting<>("Blur", true));
    public Setting<Float> grange = register(new Setting("GlowRange", 3.6f, 0.0f, 10.0f));
    public Setting<Float> gmult = register(new Setting("GlowMultiplier", 3.6f, 0.0f, 10.0f));
    public Setting<Float> range = register(new Setting("RangeBetween", 46.0f, 46.0f, 100.0f));
    boolean once = false;
    int dragX, dragY = 0;
    boolean mousestate = false;
    float posX, posY = 0;
    private final Setting<mode2> colorType = register(new Setting("Mode", mode2.Astolfo));

    public Indicators() {
        super("WexIndicators", "Индикаторы как в вексайде-(из вексайда)", Category.HUD);
    }

    public static float[] getRG(double input) {
        return new float[]{255 - 255 * (float) input, 255 * (float) input, 100 * (float) input};
    }

    protected void once() {
        indicators.add(new Indicator() {

            @Override
            boolean enabled() {
                return Timerr.getValue();
            }

            @Override
            String getName() {
                return "Timer";
            }

            @Override
            double getProgress() {
                return (10 - Timer.value) / (Math.abs(Thunderhack.moduleManager.getModuleByClass(Timer.class).getMin()) + 10);
            }
        });
        indicators.add(new Indicator() {

            @Override
            boolean enabled() {
                return Memoryy.getValue();
            }

            @Override
            String getName() {
                return "Memory";
            }

            @Override
            double getProgress() {
                long total = Runtime.getRuntime().totalMemory();
                long free = Runtime.getRuntime().freeMemory();
                long delta = total - free;
                return (delta / (double) Runtime.getRuntime().maxMemory());
            }
        });
        indicators.add(new Indicator() {

            @Override
            boolean enabled() {
                return dmgflyy.getValue();
            }

            @Override
            String getName() {
                return "DMG Fly";
            }

            @Override
            double getProgress() {
                return DMGFly.getProgress();
            }
        });
        indicators.add(new Indicator() {

            @Override
            boolean enabled() {
                return dmgspeed.getValue();
            }

            @Override
            String getName() {
                return "DMG Speed";
            }

            @Override
            double getProgress() {
                return MSTSpeed.getProgress();
            }
        });
        indicators.add(new Indicator() {

            @Override
            boolean enabled() {
                return TPS.getValue();
            }

            @Override
            String getName() {
                return "TPS";
            }

            @Override
            double getProgress() {
                return Thunderhack.serverManager.getTPS() / 20f;
            }
        });
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        posX = e.scaledResolution.getScaledWidth() * pos.getValue().getX();
        posY = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ThunderGui2) {
            if (isHovering(e.scaledResolution)) {
                if (Mouse.isButtonDown(0) && mousestate) {
                    pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                    pos.getValue().setY((float) (normaliseY(e.scaledResolution) - dragY) / e.scaledResolution.getScaledHeight());
                }
            }
        }

        if (Mouse.isButtonDown(0) && isHovering(e.scaledResolution)) {
            if (!mousestate) {
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY(e.scaledResolution) - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }

        draw(e.scaledResolution);
    }

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY(ScaledResolution sr) {
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering(ScaledResolution sr) {
        return normaliseX() > posX && normaliseX() < posX + 150 && normaliseY(sr) > posY && normaliseY(sr) < posY + 50;
    }

    @Override
    public void onUpdate() {
        if (!once) {
            once();
            once = true;
            return;
        }
        astolfo.update();
        indicators.forEach(indicator -> indicator.update());
    }

    public void draw(ScaledResolution sr) {
        GL11.glPushMatrix();
        GL11.glTranslated(pos.getValue().x * sr.getScaledWidth(), pos.getValue().y * sr.getScaledHeight(), 0);

        List<Indicator> enabledIndicators = new ArrayList();
        for (Indicator indicator : indicators) {
            if (indicator.enabled())
                enabledIndicators.add(indicator);
        }
        int enabledCount = enabledIndicators.size();
        if (enabledCount > 0) {
            for (int i = 0; i < enabledCount; i++) {
                GL11.glPushMatrix();
                GL11.glTranslated(range.getValue() * i, 0, 0);
                Indicator ind = enabledIndicators.get(i);
                //   renderShadow(0, 0, 40, 40, ColorShell.rgba(25, 25, 25, 180), 3);
                if (!blur.getValue()) {
                    RenderUtil.drawSmoothRect(0, 0, 44, 44, new Color(25, 25, 25, 180).getRGB());
                } else {
                    DrawHelper.drawRectWithGlow(0, 0, 44, 44, grange.getValue(), gmult.getValue(), cs.getValue().getColorObject());
                }


                GL11.glTranslated(22, 26, 0);
                drawCircle(ind.getName(), ind.progress());
                GL11.glPopMatrix();
            }
        }
        GL11.glPopMatrix();
    }


    public void drawCircle(String name, double offset) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        boolean oldState = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(5.5f);
        GL11.glColor4f(0.1f, 0.1f, 0.1f, 0.5f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i < 360; i++) {
            double x = Math.cos(Math.toRadians(i)) * 11;
            double z = Math.sin(Math.toRadians(i)) * 11;
            GL11.glVertex2d(x, z);
        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = -90; i < -90 + (360 * offset); i++) {

            float red = cc.getValue().getRed();
            float green = cc.getValue().getGreen();
            float blue = cc.getValue().getBlue();
            if (colorType.getValue() == mode2.StateBased) {
                float[] buffer = getRG(offset);
                red = buffer[0];
                green = buffer[1];
                blue = buffer[2];
            } else if (colorType.getValue() == mode2.Astolfo) {
                double stage = (i + 90) / 360.;
                int clr = astolfo.getColor(stage);
                red = ((clr >> 16) & 255);
                green = ((clr >> 8) & 255);
                blue = ((clr & 255));
            }
            GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1);
            double x = Math.cos(Math.toRadians(i)) * 11;
            double z = Math.sin(Math.toRadians(i)) * 11;
            GL11.glVertex2d(x, z);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        if (!oldState)
            GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        //  GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glColor4f(1, 1, 1, 1);
        if (!Objects.equals(name, "TPS")) {
            FontRender.drawCentString6((int) (offset * 100) + "%", 0.3f, -0.8f, new Color(200, 200, 200, 255).getRGB());
            FontRender.drawCentString6(name, 0, -20f, new Color(200, 200, 200, 255).getRGB());
        } else {
            FontRender.drawCentString6(String.valueOf((int) (offset * 20)), 0f, -0.8f, new Color(200, 200, 200, 255).getRGB());
            FontRender.drawCentString6(name, 0f, -20f, new Color(200, 200, 200, 255).getRGB());
        }
    }

    public enum mode2 {
        Static, StateBased, Astolfo
    }

    public static abstract class Indicator {
        DynamicAnimation animation = new DynamicAnimation();

        void update() {
            this.animation.setValue(Math.max(getProgress(), 0));
            this.animation.update();
        }

        double progress() {
            return this.animation.getValue();
        }

        abstract boolean enabled();

        abstract String getName();

        abstract double getProgress();
    }
}
