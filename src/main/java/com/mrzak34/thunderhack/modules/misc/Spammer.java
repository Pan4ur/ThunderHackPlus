package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;


import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Spammer extends Module {

    public Spammer() {
        super("Spammer", "спаммер", Category.MISC);
    }

    private Setting<ModeEn> Mode = register(new Setting("Mode", ModeEn.API));

    public enum ModeEn {
        Custom,
        API
    }

    public Setting<Boolean> global = this.register ( new Setting <> ( "global", true));
    public Setting<Integer> delay = register(new Setting<>("delay", 5, 1, 30));


    @Override
    public void onEnable(){
        loadSpammer();
    }

    private Timer timer_delay = new Timer();


    @Override
    public void onUpdate(){
        if(timer_delay.passedS(delay.getValue())) {
            if (Mode.getValue() != ModeEn.Custom) {
                getMsg();
                if (!Objects.equals(word_from_api, "-")) {
                    word_from_api = word_from_api.replace("<p>", "");
                    word_from_api = word_from_api.replace("</p>", "");
                    word_from_api = word_from_api.replace(".", "");
                    word_from_api = word_from_api.replace(",", "");

                    mc.player.sendChatMessage(global.getValue() ? "!" + word_from_api : word_from_api);
                }
            } else {
                if (SpamList.isEmpty()) {
                    Command.sendMessage("Файл spammer пустой!");
                    this.toggle();
                    return;
                }
                String c = SpamList.get(new Random().nextInt(SpamList.size()));
                mc.player.sendChatMessage(global.getValue() ? "!" + c : c);
            }
            timer_delay.reset();
        }
    }



    public static ArrayList<String> SpamList = new ArrayList<>();
    private String word_from_api = "-";

    public void getMsg(){
        new Thread(() -> {
            try {
                URL api = new URL("https://fish-text.ru/get?format=html&number=1");
                BufferedReader in = new BufferedReader(new InputStreamReader(api.openStream(), StandardCharsets.UTF_8));
                String inputLine;
                if ((inputLine = in.readLine()) != null) {
                    word_from_api = inputLine;
                }
            } catch (Exception ignored) {
            }
        }).start();
    }

    public static void loadSpammer() {
        try {
            File file = new File("ThunderHack/misc/spammer.txt");
            if (!file.exists()) file.createNewFile();;
            new Thread(() -> {
                try {

                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr);
                    ArrayList<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }

                    boolean newline = false;

                    for (String l : lines) {
                        if (l.equals("")) {
                            newline = true;
                            break;
                        }
                    }

                    SpamList.clear();
                    ArrayList<String> spamList = new ArrayList<>();

                    if (newline) {
                        StringBuilder spamChunk = new StringBuilder();

                        for (String l : lines) {
                            if (l.equals("")) {
                                if (spamChunk.length() > 0) {
                                    spamList.add(spamChunk.toString());
                                    spamChunk = new StringBuilder();
                                }
                            } else {
                                spamChunk.append(l).append(" ");
                            }
                        }
                        spamList.add(spamChunk.toString());
                    } else {
                        spamList.addAll(lines);
                    }
                    SpamList = spamList;
                } catch (Exception e) {
                    System.err.println("Could not load file ");
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Could not load file ");
        }

    }
}
