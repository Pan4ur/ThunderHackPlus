package com.mrzak34.thunderhack.notification;

import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.shaders.BetterAnimation;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

import static com.mrzak34.thunderhack.util.Util.mc;

public class Notification {
    private final String message;
    private final Timer timer;
    private final Type type;
    private final float height = 25;
    private final long stayTime;
    public BetterAnimation animation = new BetterAnimation();
    private float posY;
    private final float width;
    private float animationX;
    private boolean direction = false;
    private final Timer animationTimer = new Timer();

    public Notification(String message, Type type, int time) {
        stayTime = time;
        this.message = message;
        this.type = type;
        timer = new Timer();
        timer.reset();
        ScaledResolution sr = new ScaledResolution(mc);
        width = FontRender.getStringWidth5(message) + 34;
        animationX = width;
        posY = sr.getScaledHeight() - height;
    }

    public void render(float getY) {
        Color scolor = new Color(0xFF171717);
        Color icolor = new Color(scolor.getRed(), scolor.getGreen(), scolor.getBlue(), (int) MathUtil.clamp(255 * (1 - animation.getAnimationd()), 0, 255));
        Color icolor2 = new Color(255, 255, 255, (int) MathUtil.clamp((1 - animation.getAnimationd()), 0, 255));

        ScaledResolution resolution = new ScaledResolution(mc);

        direction = isFinished();

        animationX = (float) (width * animation.getAnimationd());

        posY = animate(posY, getY);

        int x1 = (int) ((resolution.getScaledWidth() - 6) - width + animationX);
        int y1 = (int) posY;

        RenderUtil.drawBlurredShadow(x1, y1, width, height, 20, icolor);
        RoundedShader.drawRound(x1, y1, width, height, 6f, icolor);

        FontRender.drawString5(type.getName(), (x1 + 6), y1 + 4, -1);
        FontRender.drawString5(message, x1 + 6, (int) (y1 + 4 + (height - FontRender.getFontHeight5()) / 2f), icolor2.getRGB());

        if (animationTimer.passedMs(50)) {
            animation.update(direction);
            animationTimer.reset();
        }
    }


    private boolean isFinished() {
        return timer.passedMs(stayTime);
    }

    public double getHeight() {
        return height;
    }

    public boolean shouldDelete() {
        return (isFinished()) && animationX >= width - 5;
    }

    public float animate(float value, float target) {
        return value + (target - value) / 8f;
    }


    public enum Type {
        SUCCESS("Success"),
        INFO("Information"),
        WARNING("Warning"),
        ERROR("Error"),
        ENABLED("Module enabled"),
        DISABLED("Module disabled");

        final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
