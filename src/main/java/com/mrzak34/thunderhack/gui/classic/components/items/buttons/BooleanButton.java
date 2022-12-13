package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mrzak34.thunderhack.gui.classic.ClassicGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderHelper;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class BooleanButton
        extends Button {
    private final Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        RenderHelper.drawCircle(this.x + this.getWidth() - 6.6f , this.y + this.getHeight()/2f + 1f,4f,true,this.getState() ? new Color(0x262626) : new Color(0x262626));
        RenderHelper.drawCircle(this.x + this.getWidth() - 6.6f + 8 , this.y + this.getHeight()/2f + 1f,4f,true,this.getState() ? new Color(0x262626) : new Color(0x262626));
        RenderUtil.drawRect(this.x + this.getWidth() - 6.6f,this.y + this.getHeight()/2f -3.3f,this.x + this.getWidth() - 6.6f + 8f,this.y + this.getHeight()/2f -3 + 8.3f,new Color(0x262626).getRGB());

        FontRender.drawString5(this.getName(), this.x + 2.3f, this.y - (float) ClassicGui.getClickGui().getTextOffset() + 1f, this.getState() ? -1 : -5592406);
        RenderHelper.drawCircle(this.x + this.getWidth() - 6.6f + currentpos , this.y + this.getHeight()/2f + 1f,4f,true,this.getState() ? new Color(0xEAEAEA) : new Color(0x858585));
    }

    float needtoanim = 0f;
    float currentpos = 0f;


    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());

        if(getState()){
            needtoanim = 8f;
        } else {
            needtoanim = 0f;
        }
        if(currentpos > needtoanim){
            currentpos = currentpos - 2f;
        }
        if(currentpos < needtoanim){
            currentpos = currentpos + 2f;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.setValue(!((Boolean) this.setting.getValue()));
    }

    @Override
    public boolean getState() {
        return (Boolean) this.setting.getValue();
    }
}

