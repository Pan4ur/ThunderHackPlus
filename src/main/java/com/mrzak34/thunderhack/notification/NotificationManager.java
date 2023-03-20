package com.mrzak34.thunderhack.notification;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager extends Module {
    private static final List<Notification> notificationsnew = new CopyOnWriteArrayList<>();
    private final Setting<Float> position = this.register(new Setting<>("Position", 1f, 0f, 1f));

    public NotificationManager() {
        super("Notifications", "aga", Category.CLIENT);
    }

    public static void publicity(String content, int second, Notification.Type type) {
        notificationsnew.add(new Notification(content, type, second * 1000));
    }


    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (notificationsnew.size() > 8)
            notificationsnew.remove(0);
        float startY = (float) (event.getScreenHeight() * position.getValue() - 36f);
        for (int i = 0; i < notificationsnew.size(); i++) {
            Notification notification = notificationsnew.get(i);
            notificationsnew.removeIf(Notification::shouldDelete);
            notification.render(startY);
            startY -= notification.getHeight() + 3;
        }
    }
}