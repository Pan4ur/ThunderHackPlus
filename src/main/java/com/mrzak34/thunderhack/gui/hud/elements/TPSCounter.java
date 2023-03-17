package com.mrzak34.thunderhack.gui.hud.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TPSCounter extends HudElement {
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));

    public TPSCounter() {
        super("TPS", "trps", 50,10);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        String str = "TPS " + ChatFormatting.WHITE + (Thunderhack.serverManager.getTPS());
        FontRender.drawString6(str, getPosX(), getPosY(), color.getValue().getRawColor(), false);
    }
}
