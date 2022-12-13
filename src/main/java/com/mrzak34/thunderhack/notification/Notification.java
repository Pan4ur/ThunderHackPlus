package com.mrzak34.thunderhack.notification;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import com.mrzak34.thunderhack.util.ScreenHelper;
import com.mrzak34.thunderhack.util.Util;

public class Notification {

    private final ScreenHelper screenHelper;
    private final FontRenderer fontRenderer;
    private final String title;
    private final String content;
    private final int time;
    private final NotificationType type;
    private final NotificationManager.TimerHelper timer;

    public Notification(String title, String content, NotificationType type, int second) {
        this.title = title;
        this.content = content;
        this.time = second;
        this.type = type;
        this.timer = new NotificationManager.TimerHelper();
        ScaledResolution sr = new ScaledResolution(Util.mc);
        this.screenHelper = new ScreenHelper((sr.getScaledWidth()), (sr.getScaledHeight() - 60));
        fontRenderer = Util.fr;
    }



    public int getWidth() {
        return Math.max(100, Math.max(this.fontRenderer.getStringWidth(this.title), this.fontRenderer.getStringWidth(this.content)) + 40);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return this.content;
    }

    public int getTime() {
        return this.time;
    }

    public NotificationType getType() {
        return this.type;
    }

    public NotificationManager.TimerHelper getTimer() {
        return this.timer;
    }

    public ScreenHelper getTranslate() {
        return screenHelper;
    }
}
