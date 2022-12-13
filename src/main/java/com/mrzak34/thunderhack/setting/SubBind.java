package com.mrzak34.thunderhack.setting;


import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.input.Keyboard;

public class SubBind {
    private int key;

    public SubBind(int key) {
        this.key = key;
    }

    public static SubBind none() {
        return new SubBind(-1);
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isEmpty() {
        return this.key < 0;
    }

    public String toString() {
        return this.isEmpty() ? "None" : (this.key < 0 ? "None" : this.capitalise(Keyboard.getKeyName(this.key)));
    }

    public boolean isDown() {
        return !this.isEmpty() && Keyboard.isKeyDown(this.getKey());
    }

    private String capitalise(String str) {
        if (str.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + (str.length() != 1 ? str.substring(1).toLowerCase() : "");
    }

    public static class SubBindConverter
            extends Converter<SubBind, JsonElement> {
        public JsonElement doForward(SubBind subbind) {
            return new JsonPrimitive(subbind.toString());
        }

        public SubBind doBackward(JsonElement jsonElement) {
            String s = jsonElement.getAsString();
            if (s.equalsIgnoreCase("None")) {
                return SubBind.none();
            }
            int key = -1;
            try {
                key = Keyboard.getKeyIndex(s.toUpperCase());
            } catch (Exception exception) {
                // empty catch block
            }
            if (key == 0) {
                return SubBind.none();
            }
            return new SubBind(key);
        }
    }
}