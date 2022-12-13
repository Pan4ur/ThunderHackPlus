package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;

import com.mrzak34.thunderhack.gui.classic.ClassicGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;

import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class SubBindButton
        extends Button {
    private final Setting setting;
    public boolean isListening;

    public SubBindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515) : (!this.isHovering(mouseX, mouseY) ? Thunderhack.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue() : Thunderhack.moduleManager.getModuleByClass(ClickGui.class).mainColor.getValue().getAlpha()));
        if (this.isListening) {
            FontRender.drawString5("...", this.x + 2.3f, this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset(), -1);
        } else {
            FontRender.drawString5("SubBind " + ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase(), this.x + 2.3f, this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
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
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            SubBind subBindbind = new SubBind(keyCode);
            if (subBindbind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (subBindbind.toString().equalsIgnoreCase("Delete")) {
                subBindbind = new SubBind(-1);
            }
            this.setting.setValue(subBindbind);
            this.onMouseClick();
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }
}
