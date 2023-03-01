package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.misc.Timer;

public class TpsSync
        extends Module {
    public TpsSync() {
        super("TpsSync", "синхронизирует игру-с тпс", Module.Category.PLAYER);
    }


    @Override
    public void onUpdate(){
        if(Thunderhack.moduleManager.getModuleByClass(Timer.class).isEnabled()){
            return;
        }
        if(Thunderhack.serverManager.getTPS() > 1) {
            Thunderhack.TICK_TIMER = Thunderhack.serverManager.getTPS() / 20f;
        } else {
            Thunderhack.TICK_TIMER = 1f;
        }
    }


    @Override
    public void onDisable(){
        Thunderhack.TICK_TIMER = 1f;
    }
}

