package com.mrzak34.thunderhack.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.ClientEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainSettings extends Module {
    public MainSettings() {
        super("MainSettings", "Настройки клиента", Category.CLIENT, true, false, false);
    }


    public Setting<String> prefix = this.register(new Setting<String>("Prefix", "."));
    public Setting<Boolean> showcapes = this.register(new Setting<Boolean>("Capes", true));
    public Setting<Boolean> DownloadCapes = this.register(new Setting<Boolean>("DownloadCapes", true));
    public Setting<Boolean> notifyToggles = this.register(new Setting<Boolean>("NotifyToggles", false));



    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Thunderhack.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Установлен префикс " + ChatFormatting.DARK_GRAY + Thunderhack.commandManager.getPrefix());
            }
        }
    }
}
