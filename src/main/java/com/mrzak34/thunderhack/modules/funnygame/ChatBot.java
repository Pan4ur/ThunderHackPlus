package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Pair;
import com.mrzak34.thunderhack.util.Timer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

public class ChatBot extends Module {
    public ChatBot() {
        super("ChatBot", "ChatBot", Category.FUNNYGAME);
    }

    private Setting<Modes> mode = register(new Setting("Mode", Modes.Custom));
    public Setting<Integer> delay = this.register(new Setting<Integer>("delay", 5000, 1, 20000));

    public enum Modes {
        Custom, Generator
    }

    public static String target = "none";

    private Timer timer = new Timer();

    @Override
    public void onUpdate(){
        if(timer.passedMs(delay.getValue())){
            if(!Objects.equals(target, "none")){
                mc.player.sendChatMessage(target + " " + getMsg());
            }
            timer.reset();
        }
    }


    public String getMsg(){
            try {
                URL capesList = new URL("https://www.evilinsult.com/generate_insult.php?lang=ru");
                BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
                String inputLine;
                if ((inputLine = in.readLine()) != null) {
                    return inputLine;
                }
            } catch (Exception ignored) {
            }
        return "";
    }
}
