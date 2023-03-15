package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class MainSettings extends Module {
    public Setting<Boolean> showcapes = this.register(new Setting<>("Capes", true));
    public Setting<Boolean> DownloadCapes = this.register(new Setting<>("DownloadCapes", true));
    public Setting<Boolean> notifyToggles = this.register(new Setting<>("NotifyToggles", false));
    public Setting<Boolean> mainMenu = this.register(new Setting<>("MainMenu", true));
    public Setting<Boolean> renderRotations = this.register(new Setting<>("RenderRotations", true));
    public Setting<ShaderModeEn> shaderMode = register(new Setting("ShaderMode", ShaderModeEn.Smoke));
    public Setting<Language> language = register(new Setting("Language", Language.RU));
    public MainSettings() {
        super("ClientSettings", "Настройки клиента", Category.CLIENT);
    }


    public enum ShaderModeEn {
        Smoke,
        WarThunder,
        Dicks
    }


    public enum Language {
        RU,
        ENG
    }
}
