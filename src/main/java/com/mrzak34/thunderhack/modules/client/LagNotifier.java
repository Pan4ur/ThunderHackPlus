package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.render.Drawable;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;

public class LagNotifier extends Module {
    public LagNotifier() {
        super("LagNotifier", "оповещает о-проблемах с сервером","LagNotifier", Category.CLIENT);
    }
    private final ResourceLocation ICON = new ResourceLocation("textures/lagg.png");
    private final Setting<Integer> timeout = register(new Setting("Timeout", 5, 5, 30));

    private Timer notifTimer = new Timer();
    private Timer packetTimer = new Timer();
    private Timer rubberbandTimer = new Timer();

    private boolean isLag = false;


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()) return;
        if(e.getPacket() instanceof SPacketPlayerPosLook){
            rubberbandTimer.reset();
        }
        packetTimer.reset();
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        if(!rubberbandTimer.passedMs(5000)){
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
                FontRender.drawCentString6("Обнаружен руббербенд! " + decimalFormat.format((5000f - (float) rubberbandTimer.getTimeMs()) / 1000f) ,(float) e.getScreenWidth() / 2f, (float) e.getScreenHeight() / 3f,new Color(0xFFDF00).getRGB());
            } else {
                DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
                FontRender.drawCentString6("Rubberband detected! " + decimalFormat.format((5000f - (float) rubberbandTimer.getTimeMs()) / 1000f) ,(float) e.getScreenWidth() / 2f, (float) e.getScreenHeight() / 3f,new Color(0xFFDF00).getRGB());
            }
        }
        if(packetTimer.passedMs(timeout.getValue() * 1000)){
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
                FontRender.drawCentString6("Сервер перестал отвечать! " + decimalFormat.format((float) packetTimer.getTimeMs() / 1000f) ,(float) e.getScreenWidth() / 2f, (float) e.getScreenHeight() / 3f,new Color(0xFFDF00).getRGB());
                Drawable.drawTexture(ICON, (float) e.getScreenWidth() / 2f - 40, (float) e.getScreenHeight() / 3f - 120, 80, 80,new Color(0xFFDF00));
            } else {
                DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
                FontRender.drawCentString6("Server offline! " + decimalFormat.format((float) packetTimer.getTimeMs() / 1000f) ,(float) e.getScreenWidth() / 2f, (float) e.getScreenHeight() / 3f,new Color(0xFFDF00).getRGB());
                Drawable.drawTexture(ICON, (float) e.getScreenWidth() / 2f - 40, (float) e.getScreenHeight() / 3f - 120, 80, 80,new Color(0xFFDF00));
            }
        }
        if(Thunderhack.serverManager.getTPS() < 10 && notifTimer.passedMs(60000)){
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                NotificationManager.publicity("LagNotifier ТПС сервера ниже 10! Рекомендуется включить TPSSync",8, Notification.Type.ERROR);
            } else {
                NotificationManager.publicity("LagNotifier TPS below 10! It is recommended to enable TPSSync",8, Notification.Type.ERROR);
            }
            isLag = true;
            notifTimer.reset();
        }
        if(Thunderhack.serverManager.getTPS() > 15 && isLag) {
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                NotificationManager.publicity("ТПС сервера стабилизировался!",8, Notification.Type.SUCCESS);
            } else {
                NotificationManager.publicity("TPS of the server has stabilized!",8, Notification.Type.SUCCESS);
            }
            isLag = false;
        }
    }

}
