package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.gui.windows.WindowsGui;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;

public class Windows extends Module {

    private static Windows INSTANCE = new Windows();


    public Windows() {
        super("Windows", "окна", Module.Category.CLIENT);
        this.setInstance();
    }


    public Setting<Boolean> friends = register(new Setting<>("friends", true));
    public Setting<Boolean> configs = register(new Setting<>("configs", true));
    public Setting<Boolean> altmanager = register(new Setting<>("altmanager", true));
    public Setting<Boolean> packets = register(new Setting<>("packets", true));


    public static Windows getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Windows();
        }
        return INSTANCE;
    }
    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(WindowsGui.getWindowsGui());
        toggle();
    }

}
