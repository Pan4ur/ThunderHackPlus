package com.mrzak34.thunderhack.setting;

import java.awt.*;

public class ColorSettingHeader {
    boolean opened;
    Color color;
    String name;


    public ColorSettingHeader(boolean extended) {
        this.opened = extended;
    }

    public void setOpenedCSH(boolean a){
        this.opened = a;
    }

    public boolean getStateCSH(){
        return this.opened;
    }

    public void setNameCSH(String a){
        this.name = a;
    }

    public String getNameCSH(){
        return this.name;
    }

    public void setColorCSH(Color a){
        this.color = a;
    }

    public Color getColorCSH(){
        return this.color;
    }
}
