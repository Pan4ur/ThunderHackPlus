package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.notification.NotificationType;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class PacketCounter extends Module{
    public PacketCounter() {
        super("PacketCounter", "PacketCounter", Category.HUD);
        this.timer = new Timer();
    }
    private final Timer timer;


    public Setting<Integer> delayms = this.register ( new Setting <> ( "Delay", 500, 0, 5000 ) );


    public Setting<Integer> waterMarkZ1 = register(new Setting("Y", 10, 0, 524));
    public Setting<Integer> waterMarkZ2 = register(new Setting("X", 20, 0, 862));

    private Setting<mode> Mode = register(new Setting("Mode", mode.FunnyGame));

    public enum mode {
        FunnyGame, TOOBEE, Custom;
    }

    public Setting<Integer> i1 = register(new Setting("green under", 20, 0, 100, v-> Mode.getValue() == mode.Custom));
    public Setting<Integer> i2 = register(new Setting("Orange under", 50, 0, 100, v-> Mode.getValue() == mode.Custom));
    public Setting<Integer> i3 = register(new Setting("Red", 50, 0, 100, v-> Mode.getValue() == mode.Custom));


    public Setting<Boolean> notif = this.register(new Setting<Boolean>("Notification", true));





    public int i = 0;
    @SubscribeEvent

    public void onPacketSend(PacketEvent.Send event){
        ++i;
        if(timer.passedMs(delayms.getValue())){
            i = 0;
            timer.reset();
        }
    }
    ScaledResolution sr = new ScaledResolution(mc);
    float x1 = 0;
    float y1 = 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent event){


        y1 = sr.getScaledHeight() / (1000f/ waterMarkZ1.getValue());
        x1 = sr.getScaledWidth() / (1000f/ waterMarkZ2.getValue());



        Color color = null;

        RenderUtil.drawSmoothRect(waterMarkZ2.getValue(), waterMarkZ1.getValue(), 93 + waterMarkZ2.getValue(), 20 + waterMarkZ1.getValue(), new Color(35, 35, 40, 230).getRGB());
        RenderUtil.drawSmoothRect(waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 12 , 90 + waterMarkZ2.getValue(), 15 + waterMarkZ1.getValue(), new Color(51, 51, 58, 230).getRGB());


        if(Mode.getValue() == mode.FunnyGame) {
            if (i < 25) {
                color = new Color(54, 250, 0, 255);
            }
            if (i > 25 && i < 50) {
                color = new Color(255, 178, 0, 255);
            }
            if (i > 50) {
                color = new Color(255, 0, 0, 255);
                if(notif.getValue()) {
                    NotificationManager.publicity("PacketCounter", "Вырубай, ща кикнет!!!", 2, NotificationType.WARNING);
                }
            }
        }
        if(Mode.getValue() == mode.TOOBEE) {
            if (i < 25) {
                color = new Color(54, 250, 0, 255);
            }
            if (i > 25 && i < 50) {
                color = new Color(255, 178, 0, 255);
            }
            if (i > 50) {
                color = new Color(255, 0, 0, 255);
                NotificationManager.publicity("PacketCounter", "Вырубай, ща кикнет!!!", 2, NotificationType.WARNING);
            }
        }
        if(Mode.getValue() == mode.Custom) {
            if (i < i1.getValue()) {
                color = new Color(54, 250, 0, 255);
            }
            if (i > i2.getValue() && i < i3.getValue()) {
                color = new Color(255, 178, 0, 255);
            }
            if (i > i3.getValue()) {
                color = new Color(255, 0, 0, 255);
                NotificationManager.publicity("PacketCounter", "Вырубай, ща кикнет!!!", 2, NotificationType.WARNING);
            }
        }



        if(color != null) {
            RenderUtil.drawSmoothRect(waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 12, (Math.min(i, 85)) + waterMarkZ2.getValue() + 5, 15 + waterMarkZ1.getValue(), color.getRGB());
        }

        Util.fr.drawStringWithShadow("PacketCounter",waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 1, PaletteHelper.astolfo(false, (int) 1).getRGB());
// лево верх право низ
        // прямоугольник лево верх право низ

    }

//fun gam max 60 500ms
    //strict max is ?? ask хач for this shit

}
