package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.gui.classic.ClassicGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton
        extends Button {
    public Setting setting;

    public EnumButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawSmoothRect(this.x + 2f, this.y + 2f, this.x + (float) this.width + 5.4f, this.y + (float) this.height - 2.5f,getColor());
        FontRender.drawString5(this.setting.getName() + " " + ChatFormatting.GRAY +  this.setting.currentEnumName(), this.x + 5.3f, this.y - 0.7f - (float) ClassicGui.getClickGui().getTextOffset(), -1 );
    }


    public int getColor(){
        return ClickGui.getInstance().downColor.getValue().withAlpha(100).getColor();
    }


    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        if(mouseButton == 1 && this.isHovering(mouseX, mouseY)){
            this.width = (this.setting.getEnumInt() * 15);
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.increaseEnum();
    }

    @Override
    public boolean getState() {
        return true;
    }
}

