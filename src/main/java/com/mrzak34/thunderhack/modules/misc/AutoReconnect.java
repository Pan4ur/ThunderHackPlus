package com.mrzak34.thunderhack.modules.misc;


import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class AutoReconnect extends Module {
    private static ServerData serverData;
    private static AutoReconnect INSTANCE;

    static {
        AutoReconnect.INSTANCE = new AutoReconnect();
    }

    public Setting<Integer> delay = this.register(new Setting<>("delay", 34, 0, 90));

    public AutoReconnect() {
        super("AutoReconnect", "коннектит к серву-если кикнуло", Category.MISC);
        this.setInstance();
    }

    public static AutoReconnect getInstance() {
        if (AutoReconnect.INSTANCE == null) {
            AutoReconnect.INSTANCE = new AutoReconnect();
        }
        return AutoReconnect.INSTANCE;
    }

    private void setInstance() {
        AutoReconnect.INSTANCE = this;
    }

    @SubscribeEvent
    public void sendPacket(final GuiOpenEvent event) {
        if (event.getGui() instanceof GuiDisconnected) {
            this.updateLastConnectedServer();
            //  if (AutoLog.getInstance().isOff()) {
            final GuiDisconnected disconnected = (GuiDisconnected) event.getGui();
            event.setGui(new GuiDisconnectedHook(disconnected));
            //  }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        this.updateLastConnectedServer();
    }

    public void updateLastConnectedServer() {
        final ServerData data = AutoReconnect.mc.getCurrentServerData();
        if (data != null) {
            AutoReconnect.serverData = data;
        }
    }

    private class GuiDisconnectedHook extends GuiDisconnected {
        private final Timer timer;

        public GuiDisconnectedHook(final GuiDisconnected disconnected) {
            super(disconnected.parentScreen, disconnected.reason, disconnected.message);
            (this.timer = new Timer()).reset();
        }

        public void updateScreen() {
            if (this.timer.passedS(AutoReconnect.this.delay.getValue())) {
                this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, (AutoReconnect.serverData == null) ? this.mc.currentServerData : AutoReconnect.serverData));
            }
        }

        public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            final String s = "Reconnecting in " + MathUtil.round((AutoReconnect.this.delay.getValue() * 1000 - this.timer.getPassedTimeMs()) / 1000.0, 1);
            mc.fontRenderer.drawString(s, (float) (this.width / 2 - mc.fontRenderer.getStringWidth(s) / 2), (float) (this.height - 16), 16777215, true);
        }
    }
}