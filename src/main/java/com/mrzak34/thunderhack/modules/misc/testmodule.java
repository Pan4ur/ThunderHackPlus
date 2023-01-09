package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mrzak34.thunderhack.util.Timer;

import java.text.DecimalFormat;

public class testmodule extends Module {

    public testmodule() {
        super("TESTMODULE", "TEST", Category.MISC, true, false, false);
    }

    public Setting<Integer> sex  = this.register(new Setting<>("Seconds", 5, 1, 30));
    public Setting<Boolean> cancel = register(new Setting<>("CancelPackets", true));


    Timer lagTimer = new Timer();

    @SubscribeEvent
    public void onRender2D(Render2DEvent e )  {
        float seconds = ((System.currentTimeMillis() - lagTimer.getPassedTimeMs()) / 1000.0f) % 60.0f;

        if (seconds >= sex.getValue())
        {
            ScaledResolution sr = new ScaledResolution(mc);
            Util.fr.drawStringWithShadow("Server has stopped responding for " + new DecimalFormat("#.#").format(seconds) + " seconds!",sr.getScaledWidth() / 2f - (Util.fr.getStringWidth("Server has stopped responding for X.X seconds!") / 2f),sr.getScaledHeight()/2f, -1);
        }

    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event){
        float seconds = ((System.currentTimeMillis() - lagTimer.getPassedTimeMs()) / 1000.0f) % 60.0f;
        if(cancel.getValue() && seconds >= sex.getValue()){
            if(event.getPacket() instanceof CPacketPlayer){
                event.setCanceled(true);
            }
            if(event.getPacket() instanceof CPacketPlayer.Position){
                event.setCanceled(true);
            }
            if(event.getPacket() instanceof CPacketPlayer.Rotation){
                event.setCanceled(true);
            }
            if(event.getPacket() instanceof CPacketPlayer.PositionRotation){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPacketRecieve(PacketEvent.Receive event) {
        lagTimer.reset();
    }

}
