package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.Drawable;

import java.awt.*;

public class ModeComponent extends SettingElement {

    private double wheight;
    public ModeComponent(Setting setting) {
        super(setting);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        if((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY){
            return;
        }
        FontRender.drawString6(getSetting().getName(), (float) getX(), (float) getY() + 5,isHovered() ? -1 : new Color(0xB0FFFFFF, true).getRGB(),false);


        if (open) {
            double offsetY2 = 0;
            for (int i = 0; i <= setting.getModes().length - 1; i++) {
                offsetY2 += 12;
            }
            RoundedShader.drawRound((float) (x + 114), (float) (y + 2), 62F, (float) (11 + offsetY2), 0.5f, new Color(50,35,60, 121));
        }

        if(mouseX > x + 114 && mouseX < x + 176 && mouseY > y + 2 && mouseY < y + 15 ){
            RoundedShader.drawRound((float) (x + 114), (float) (y + 2), 62, 11, 0.5f, new Color(82, 57, 100, 178));
        } else {
            RoundedShader.drawRound((float) (x + 114), (float) (y + 2), 62, 11, 0.5f, new Color(50,35,60, 178));
        }




        FontRender.drawString6(setting.currentEnumName(), (float) (x + 116), (float) (y + 5), new Color(0xB0FFFFFF, true).getRGB(),false);


        String arrow = "n";
        switch (progress){
            case 0:
                arrow = "n";
                break;
            case 1:
                arrow = "o";
                break;
            case 2:
                arrow = "p";
                break;
            case 3:
                arrow = "q";
                break;
            case 4:
                arrow = "r";
                break;
        }
        FontRender.drawIcon(arrow,(int)(x + 166),(int)(y + 6),-1);

        double offsetY = 13;
        if (open) {
            Color color = ClickGui.getInstance().getColor(0);
            for(int i = 0; i <= setting.getModes().length - 1; i++){
                FontRender.drawString5(setting.getModes()[i], (float) (x + 116), (float) ((y + 5) + offsetY), setting.currentEnumName().equalsIgnoreCase(setting.getModes()[i]) ? color.getRGB() : -1);
                offsetY += 12;
            }
        }



    }

    private boolean open;
    int progress = 0;

    @Override
    public void onTick(){
        if(open && progress > 0) {
            progress--;
        }
        if(!open && progress < 4) {
            progress++;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY){
            return;
        }
        if (mouseX > x + 114 && mouseX < x + 176 && mouseY > y + 2 && mouseY < y + 15 ) {
            open = !open;
        }
        if (open) {
            double offsetY = 0;
            for(int i = 0; i <= setting.getModes().length - 1; i++){
                if (Drawable.isHovered(mouseX, mouseY, x, y + wheight + offsetY, width, 12) && button == 0)
                    setting.setEnumByNumber(i);
                offsetY += 12;
            }
        }
    }

    public void setWHeight(double height) {
        this.wheight = height;
    }
    public boolean isOpen() {
        return open;
    }

}
