package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.PlayerUtils;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.inventory.ClickType;
import org.lwjgl.input.Keyboard;

public class AutoCappRegear extends Module{
    public AutoCappRegear() {
        super("CappRegear", "регирит каппучино-по бинду", Category.FUNNYGAME);
    }

    public Timer timer = new Timer();
    public Setting<SubBind> aboba = this.register(new Setting<>("BuyBind", new SubBind(Keyboard.KEY_O)));
    public Setting <Integer> delay = this.register ( new Setting <> ( "Delay", 100, 1, 500 ) );


    boolean open_shop = false;

    @Override
    public void onUpdate(){
        if(mc.currentScreen instanceof GuiChat){
            return;
        }

        if(PlayerUtils.isKeyDown(aboba.getValue().getKey())){
            if(!open_shop){
                mc.player.sendChatMessage("/drinks");
                open_shop = true;
            }
            if(open_shop && timer.passedMs(delay.getValue())){
                  mc.playerController.windowClick(mc.player.openContainer.windowId, 1, 0, ClickType.PICKUP, mc.player);
                  mc.playerController.windowClick(0, 1, 0, ClickType.PICKUP, mc.player);
                timer.reset();
            }
        } else {
            open_shop = false;
        }
    }



}
