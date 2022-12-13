package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShiftInterp extends Module {

    private static ShiftInterp INSTANCE = new ShiftInterp();

    public ShiftInterp() {
        super("ShiftInterp", "ShiftInterp", Category.RENDER, true, false, false);
        this.setInstance();
    }



    public static ShiftInterp getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShiftInterp();
        }
        return INSTANCE;
    }
    private void setInstance() {
        INSTANCE = this;
    }
    public Setting<Boolean> sleep = this.register ( new Setting <> ( "Sleep", false ) );




}
