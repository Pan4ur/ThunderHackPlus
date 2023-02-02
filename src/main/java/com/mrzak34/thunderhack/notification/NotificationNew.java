package com.mrzak34.thunderhack.notification;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

import static com.mrzak34.thunderhack.util.Util.mc;

public class NotificationNew {
    private final String message;
    private  final Timer timer;
    private final Type type;

    private double posY;
    private double width;
    private double height;
    private double animationX;
    private final int imageWidth;
    private final long stayTime;


    private final Animation animation = new DecelerateAnimation(380, 1, Direction.BACKWARDS);
    private final Animation animationY = new DecelerateAnimation(380, 1);

    public NotificationNew(String message, Type type,int time) {
        stayTime = time;
        this.message = message;
        this.type = type;
        timer =  new Timer();
        timer.reset();
        ScaledResolution sr = new ScaledResolution(mc);
        width = FontRender.getStringWidth5(message) + 34;
        animationX = width;
        imageWidth = 9;
        height = 33;
        posY = sr.getScaledHeight() - height;
    }

    public void render(double getY) {
        Color scolor = new Color(0xFF171717);
        Color icolor = new Color(scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 200);
        ScaledResolution resolution = new ScaledResolution(mc);
        animationY.setDirection(isFinished() ? Direction.BACKWARDS : Direction.FORWARDS);
        animation.setDirection(isFinished() ? Direction.FORWARDS : Direction.BACKWARDS);
        animationX = width * animation.getOutput();
        posY = animate(posY, getY);
        int x1 = (int) ((resolution.getScaledWidth() - 6) - width + animationX), y1 = (int) posY;
        RenderUtil.drawSmoothRect((float) x1, y1, (float) width + (float) x1, (float) height + y1, icolor.getRGB());

        FontRender.drawString5(type.getName(), (x1 + 6), y1 + 4, -1);
      //  FontRender.drawString5(message, (int) (x1 + imageWidth + 16), (int) ((float) y1 + 4 + (height - FontRender.getFontHeight5()) / 2), -1);
        FontRender.drawString5(message, (int) (x1 + 6), (int) ((float) y1 + 4 + (height - FontRender.getFontHeight5()) / 2), -1);

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


    public enum Type {
        SUCCESS("Success"),
        INFO("Information"),
        WARNING("Warning"),
        ERROR("Error"),
        ENABLED("Module toggled"),
        DISABLED("Module toggled");

        final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public  double animate(double value, double target) {
        return value + (target - value) / (3 + 1 * Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).deltt.getValue());
    }
}
