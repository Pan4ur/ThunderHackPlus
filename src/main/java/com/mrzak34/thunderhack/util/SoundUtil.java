package com.mrzak34.thunderhack.util;

import net.minecraft.util.ResourceLocation;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import static com.mrzak34.thunderhack.util.Util.mc;

public class SoundUtil {
    public static final SoundUtil INSTANCE = new SoundUtil();
    public static ResourceLocation ON_SOUND = new ResourceLocation("sounds/on.wav");
    public static ResourceLocation OFF_SOUND = new ResourceLocation("sounds/off.wav");
    public static ResourceLocation SUCCESS_SOUND = new ResourceLocation("sounds/success.wav");
    public static ResourceLocation ERROR_SOUND = new ResourceLocation("sounds/error.wav");


    public static void playSound(ThunderSound snd) {
        ResourceLocation resourceLocation = null;

        switch (snd){
            case ON:
                resourceLocation = ON_SOUND;
                break;
            case OFF:
                resourceLocation = OFF_SOUND;
                break;
            case SUCCESS:
                resourceLocation = SUCCESS_SOUND;
                break;
            case ERROR:
                resourceLocation = ERROR_SOUND;
                break;
        }
        try {AudioPlayer.player.start(new AudioStream(mc.getResourceManager().getResource(resourceLocation).getInputStream()));}
        catch (Exception ignored) {}
    }

    public enum ThunderSound {
        ON,OFF,ERROR,SUCCESS
    }
}