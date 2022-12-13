package com.mrzak34.thunderhack.gui.windows;

import com.mrzak34.thunderhack.gui.windows.window.WindowAltManager;
import com.mrzak34.thunderhack.gui.windows.window.WindowConfig;
import com.mrzak34.thunderhack.gui.windows.window.WindowFriends;
import com.mrzak34.thunderhack.gui.windows.window.WindowPackets;
import com.mrzak34.thunderhack.modules.client.Windows;
import com.mrzak34.thunderhack.setting.Bind;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class WindowsGui extends GuiScreen {

    private static WindowsGui INSTANCE;

    static {
        INSTANCE = new WindowsGui();
    }

    public WindowsGui() {
        this.setInstance();
        this.load();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public static WindowsGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WindowsGui();
        }
        return INSTANCE;
    }

    public static WindowsGui getWindowsGui() {
        return getInstance();
    }

    private void load() {

    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(Windows.getInstance().altmanager.getValue())
            WindowAltManager.drawScreen(mouseX,mouseY, partialTicks);
        if(Windows.getInstance().configs.getValue())
            WindowConfig.drawScreen(mouseX,mouseY, partialTicks);
        if(Windows.getInstance().packets.getValue())
            WindowPackets.drawScreen(mouseX,mouseY, partialTicks);
        if(Windows.getInstance().friends.getValue())
            WindowFriends.drawScreen(mouseX,mouseY, partialTicks);
    }

    public void keyTyped(char typedChar, int keyCode) {
        Bind bind = new Bind(keyCode);

        if (bind.toString().equalsIgnoreCase("Escape")){
            Mouse.setGrabbed(false);
            mc.currentScreen = null;
        }
        if(Windows.getInstance().altmanager.getValue())
            WindowAltManager.keyTyped(typedChar, keyCode);
        if(Windows.getInstance().configs.getValue())
            WindowConfig.keyTyped(typedChar, keyCode);
        if(Windows.getInstance().packets.getValue())
            WindowPackets.keyTyped(typedChar, keyCode);
        if(Windows.getInstance().friends.getValue())
            WindowFriends.keyTyped(typedChar, keyCode);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        if(Windows.getInstance().altmanager.getValue())
            WindowAltManager.mouseClicked(mouseX, mouseY,mouseButton);
        if(Windows.getInstance().configs.getValue())
            WindowConfig.mouseClicked(mouseX, mouseY,mouseButton);
        if(Windows.getInstance().packets.getValue())
            WindowPackets.mouseClicked(mouseX, mouseY,mouseButton);
        if(Windows.getInstance().friends.getValue())
            WindowFriends.mouseClicked(mouseX, mouseY,mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if(Windows.getInstance().altmanager.getValue())
            WindowAltManager.mouseReleased(mouseX, mouseY,state);
        if(Windows.getInstance().configs.getValue())
            WindowConfig.mouseReleased(mouseX, mouseY,state);
        if(Windows.getInstance().packets.getValue())
            WindowPackets.mouseReleased(mouseX, mouseY,state);
        if(Windows.getInstance().friends.getValue())
            WindowFriends.mouseReleased(mouseX, mouseY,state);
    }


}

