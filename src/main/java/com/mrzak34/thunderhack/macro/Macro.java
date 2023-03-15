package com.mrzak34.thunderhack.macro;

import net.minecraft.client.Minecraft;

public class Macro {

    private final String name;
    private String text;
    private int bind;

    public Macro(String name, String text, int bind) {
        this.name = name;
        this.text = text;
        this.bind = bind;
    }

    public void runMacro() {
        Minecraft.getMinecraft().player.sendChatMessage(text);
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Macro) {
            return (this.getName().equalsIgnoreCase(((Macro) obj).getName()));
        } else {
            return false;
        }
    }

}