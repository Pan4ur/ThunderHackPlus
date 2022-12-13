package com.mrzak34.thunderhack.setting;

public class Parent {
    private boolean extended;

    public Parent(boolean extended) {
        this.extended = extended;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }
}