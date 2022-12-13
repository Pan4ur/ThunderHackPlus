package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.ChatColor;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Objects;

public class KeyBinds extends Module{

    public KeyBinds() {
        super("KeyBinds", "KeyBinds", Module.Category.HUD,true,true,false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));


    float x1 =0;
    float y1= 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        ScaledResolution sr = new ScaledResolution(mc);
        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();






        float y = y1 -7;

        for (Module feature : Thunderhack.moduleManager.modules) {
            if (!Objects.equals(feature.getBind().toString(), "None") && !feature.getName().equalsIgnoreCase("clickgui") && !feature.getName().equalsIgnoreCase("thundergui")) {
                RenderUtil.drawRect(x1, y, x1 + 105, 13 + y1, new Color(61, 58, 58).getRGB());
                RenderUtil.drawRect(x1, y1 - 10, 105 + x1, 2 + y1, new Color(123, 0, 255).getRGB());
                RenderUtil.drawRect(x1, y1 - 8, 105 + x1, 12 + y1, new Color(61, 58, 58).getRGB());
                fr.drawStringWithShadow("keybinds", 10 + fr.getStringWidth("keybinds") + x1, y1 - 5, -1);
                String toggled = feature.isOn() ? ChatColor.GREEN +  " [enabled]" : ChatColor.GRAY +  " [disabled]";
                fr.drawStringWithShadow(feature.getName(), 10 + x1, y + 4, -1);
                fr.drawStringWithShadow(toggled, 75  + x1 , y + 4, -1);
                y += 12;
            }
        }



        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  sr.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / sr.getScaledHeight());
                }
            }
        }
        if(Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * sr.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * sr.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }

    int dragX, dragY = 0;
    boolean mousestate = false;

    public int normaliseX(){
        return (int) ((Mouse.getX()/2f));
    }
    public int normaliseY(){
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight())/2);
    }

    public boolean isHovering(){
        return normaliseX() > x1 - 10 && normaliseX()< x1 + 100 && normaliseY() > y1 &&  normaliseY() < y1 + 100;
    }
}
