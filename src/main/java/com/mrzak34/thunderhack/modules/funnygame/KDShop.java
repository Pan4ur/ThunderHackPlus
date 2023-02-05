package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.PlayerUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class KDShop extends Module{
    public KDShop() {
        super("KDShop", "Не всегда работает-но да ладно", Category.FUNNYGAME);
    }
    
    public static GuiScreen lastGui;
    public Setting<SubBind> breakBind = this.register(new Setting<>("Open", new SubBind(Keyboard.KEY_I)));


    @Override
    public void onUpdate() {
        if (KDShop.mc.currentScreen instanceof GuiContainer) {
            KDShop.lastGui = KDShop.mc.currentScreen;
        }
        if(PlayerUtils.isKeyDown(breakBind.getValue().getKey())){
            mc.displayGuiScreen(lastGui);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
                event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketCloseWindow) {
            Command.sendMessage("fuck");
        }
    }


    boolean closeInv;
    public static boolean cancelRender = false;

    /*
    @Override
    public void onUpdate(){
        if(PlayerUtils.isKeyDown(breakBind.getValue().getKey())){
            closeInv = !closeInv;
        }
        if (closeInv) {
            Mouse.setGrabbed(true);
            mc.inGameHasFocus = true;
            cancelRender = true;
        } else {
            Mouse.setGrabbed(false);
            mc.inGameHasFocus = false;
            cancelRender = false;
        }
    }

     */


}
