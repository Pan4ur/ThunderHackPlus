package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.gui.hud.elements.HudEditorGui;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.Util;

public class HudEditor extends Module {
    private static HudEditor INSTANCE = new HudEditor();

    public HudEditor() {
        super("HudEditor", "худ изменять да", Module.Category.CLIENT);
        this.setInstance();
    }

    public static HudEditor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudEditor();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(HudEditorGui.getHudGui());
        toggle();
    }


}
