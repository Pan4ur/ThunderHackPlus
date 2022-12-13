package com.mrzak34.thunderhack.notification;

import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.ScreenHelper;


import com.mrzak34.thunderhack.util.RectHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager extends Module {
    public NotificationManager() {
super("Notifications", "aga", Category.CLIENT, true, false, false);
    }

    public Setting <Integer> fade = this.register ( new Setting <> ( "fade", 100, 0, 2048 ) );
    public Setting <Integer> fade2 = this.register ( new Setting <> ( "fade2", 500, 0, 2048 ) );
    public Setting <Integer> gggg = this.register ( new Setting <> ( "height", 100, 0, 2048 ) );
    public Setting<Float> deltt = this.register(new Setting<>("delta", 1.0f, 0f, 5.0f));


    private Setting<modeEn> mode = register(new Setting("Mode", modeEn.New));
    public enum modeEn {
        Old, New
    }


    private static final List<NotificationNew> notificationsnew = new ArrayList<>();

    private static final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public static void publicity(String title, String content, int second, NotificationType type) {
        if(aboba) {
            notifications.add(new Notification(title, content, type, second * 1000));
        } else {
            notificationsnew.add(new NotificationNew(title + " " +content,typeResolver(type),second * 1000));
        }
    }


    static NotificationNew.Type typeResolver(NotificationType type){
        switch (type){
            case INFO: return NotificationNew.Type.INFO;
            case ERROR: return NotificationNew.Type.ERROR;
            case SUCCESS: return NotificationNew.Type.SUCCESS;
            case WARNING: return NotificationNew.Type.WARNING;
        }
        return NotificationNew.Type.WARNING;
    }

    static boolean aboba = false;
    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {

            if(mode.getValue() == modeEn.Old) {
                aboba = true;
                if (!notifications.isEmpty()) {
                    ScaledResolution sr = new ScaledResolution(mc);
                    int srScaledHeight = sr.getScaledHeight();
                    int scaledWidth = sr.getScaledWidth();
                    int y = srScaledHeight - 60;
                    for (Notification notification : notifications) {
                        ScreenHelper screenHelper = notification.getTranslate();
                        int width = notification.getWidth() + 40 + Util.fr.getStringWidth(notification.getContent()) / 2;
                        if (!notification.getTimer().hasReached(notification.getTime() - fade.getValue())) {
                            try {
                                screenHelper.calculateCompensation(scaledWidth - width, (float) y, 0.8F, 5F);
                            } catch (Exception ignored) {
                            }
                        } else {
                            try {
                                screenHelper.calculateCompensation(scaledWidth, notification.getTranslate().getY(), 0.8F, 5F);
                            } catch (Exception ignored) {
                            }
                            if (Util.mc.player != null && Util.mc.world != null) {
                                if (notification.getTimer().getTime() > notification.getTime() + fade2.getValue()) {
                                    notifications.remove(notification);
                                }
                            }
                        }
                        float translateX = screenHelper.getX();
                        float translateY = screenHelper.getY();
                        GlStateManager.pushMatrix();
                        GlStateManager.disableBlend();
                        RectHelper.drawRect(translateX, translateY, translateX - 2, (translateY + 28), notification.getType().getColor());
                        RectHelper.drawRect(translateX, translateY, scaledWidth, (translateY + 28), new Color(35, 34, 34).getRGB());

                        Util.fr.drawStringWithShadow(TextFormatting.BOLD + notification.getTitle(), translateX + 5, translateY + 4, -1);
                        Util.fr.drawStringWithShadow(notification.getContent(), translateX + 5, translateY + 15, new Color(245, 245, 245).getRGB());
                        GlStateManager.popMatrix();
                        if (notifications.size() > 1) {
                            y -= 35;
                        }
                    }
                }
            } else {
                aboba = false;

                if (notificationsnew.size() > 4)
                    notificationsnew.remove(0);

                double startY = gggg.getValue() - 36;

                for (int i = 0; i < notificationsnew.size(); i++) {
                    NotificationNew notification = notificationsnew.get(i);
                    notificationsnew.removeIf(NotificationNew::shouldDelete);

                    notification.render(startY);
                    startY -= notification.getHeight() + 3;
                }
            }

    }


    public static class TimerHelper {

        private long ms = getCurrentMS();

        private long getCurrentMS() {
            return System.currentTimeMillis();
        }

        public boolean hasReached(float milliseconds) {
            return getCurrentMS() - ms > milliseconds;
        }

        public void reset() {
            ms = getCurrentMS();
        }

        public long getTime() {
            return getCurrentMS() - ms;
        }
    }
}