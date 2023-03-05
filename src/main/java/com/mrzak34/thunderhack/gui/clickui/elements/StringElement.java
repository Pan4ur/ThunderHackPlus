package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.util.ChatAllowedCharacters;

import static com.mrzak34.thunderhack.gui.clickui.elements.SliderElement.removeLastChar;

public class StringElement extends AbstractElement {
    public StringElement(Setting setting) {
        super(setting);
    }

    public boolean isListening;
    private CurrentString currentString = new CurrentString("");


    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if (this.isListening) {
            FontRender.drawString5(this.currentString.getString() + getIdleSign(), (float) (x + 3), (float) (y + height / 2 - (FontRender.getFontHeight5() / 2f)), -1);
        } else {
            FontRender.drawString5(this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.GRAY : "") + this.setting.getValue(), (float) (x + 3), (float) (y + height / 2 - (FontRender.getFontHeight5() / 2f)), -1);
        }


    }

    public String getIdleSign(){
        return "...";
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            isListening = !isListening;
        }
    }

    @Override
    public void keyTyped(char chr, int keyCode) {
        if (this.isListening) {
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    this.enterString();
                }
                case 14: {
                    this.setString(removeLastChar(this.currentString.getString()));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(chr)) {
                this.setString(this.currentString.getString() + chr);
            }
        }
    }


    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        } else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        isListening = !isListening;
    }


    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
