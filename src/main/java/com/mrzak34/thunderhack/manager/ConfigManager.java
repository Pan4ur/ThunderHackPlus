package com.mrzak34.thunderhack.manager;

import com.google.gson.*;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.modules.Module;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class ConfigManager implements Util {
    public ArrayList<Feature> features = new ArrayList<>();
    public String currentcfg;
    public String config = "ThunderHack/config/";

    /*----------------- WRITE ---------------*/

    public JsonObject writeSettings(Feature feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();

        for (Setting setting : feature.getSettings()) {
            if (setting.isEnumSetting()) {
                EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                object.add(setting.getName(), converter.doForward((Enum) setting.getValue()));
                continue;
            }
            if (setting.isStringSetting()) {
                String str = (String) setting.getValue();
                setting.setValue(str.replace(" ", "_"));
            }
            if(setting.isColorSetting()){
                JsonArray array = new JsonArray();
                array.add(new JsonPrimitive(((ColorSetting) setting.getValue()).getRawColor()));
                array.add(new JsonPrimitive(((ColorSetting) setting.getValue()).isCycle()));
                array.add(new JsonPrimitive(((ColorSetting) setting.getValue()).getGlobalOffset()));


                object.add(setting.getName(), array);
                continue;
            }
            if(setting.isPositionSetting()){
                JsonArray array = new JsonArray();
                float num2 = ((PositionSetting) setting.getValue()).getX();
                float num1 = ((PositionSetting) setting.getValue()).getY();
                array.add(new JsonPrimitive(num2));
                array.add(new JsonPrimitive(num1));

                object.add(setting.getName(), array);
                continue;
            }
            if(setting.isBindSetting()){
                JsonArray array = new JsonArray();
                String key = setting.getValueAsString();
                boolean hold = ((Bind) setting.getValue()).isHold();
                array.add(new JsonPrimitive(key));
                array.add(new JsonPrimitive(hold));

                object.add(setting.getName(), array);
                continue;
            }

            try {
                object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            } catch (Exception e) {
            }
        }
        return object;
    }
    /*----------------- LOAD ---------------*/

    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        String str;
        switch (setting.getType()) {
            case "Parent":
                return;
            case "Boolean":
                setting.setValue(Boolean.valueOf(element.getAsBoolean()));
                return;
            case "Double":
                setting.setValue(Double.valueOf(element.getAsDouble()));
                return;
            case "Float":
                setting.setValue(Float.valueOf(element.getAsFloat()));
                return;
            case "Integer":
                setting.setValue(Integer.valueOf(element.getAsInt()));
                return;
            case "String":
                str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                return;
            case "Bind":
                JsonArray array4 = element.getAsJsonArray();
                setting.setValue((new Bind.BindConverter()).doBackward(array4.get(0)));
                ((Bind) setting.getValue()).setHold(array4.get(1).getAsBoolean());
                return;
            case "BlockListSetting":
                JsonArray array2 = element.getAsJsonArray();
                array2.forEach(jsonElement -> {
                    String str2 = jsonElement.getAsString();
                    ((BlockListSetting) setting.getValue()).addBlock(str2);
                });
                ((BlockListSetting) setting.getValue()).refreshBlocks();
                return;
            case "ColorSetting":
                JsonArray array = element.getAsJsonArray();
                ((ColorSetting) setting.getValue()).setColor(array.get(0).getAsInt());
                ((ColorSetting) setting.getValue()).setCycle(array.get(1).getAsBoolean());
                ((ColorSetting) setting.getValue()).setGlobalOffset(array.get(2).getAsInt());
                return;
            case "PositionSetting":
                JsonArray array3 = element.getAsJsonArray();
                ((PositionSetting) setting.getValue()).setX(array3.get(0).getAsFloat());
                ((PositionSetting) setting.getValue()).setY(array3.get(1).getAsFloat());
                return;
            case "SubBind":
                setting.setValue((new SubBind.SubBindConverter()).doBackward(element));
                return;
            case "Enum":
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue((value == null) ? setting.getDefaultValue() : value);
                } catch (Exception ignored) {
                }
        }
    }

    private static void loadFile(JsonObject input, Feature feature) {
        for (Map.Entry<String, JsonElement> entry : input.entrySet()) {
            String settingName = entry.getKey();
            JsonElement element = entry.getValue();
            if (feature instanceof FriendManager) {
                try {
                    Thunderhack.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName),""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (feature instanceof EnemyManager) {
                try {
                    Thunderhack.enemyManager.addEnemy(new EnemyManager.Enemy(element.getAsString(), UUID.fromString(settingName)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            for (Setting setting : feature.getSettings()) {
                if (settingName.equals(setting.getName())) {
                    try {
                        setValueFromJson(feature, setting, element);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void loadConfig(String name,boolean firstrun) {
        if(!firstrun)
            saveConfig(currentcfg);
        Thunderhack.moduleManager.onUnload();
        Thunderhack.moduleManager.onUnloadPost();

        currentcfg = name;
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("ThunderHack").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        if (files.contains(new File("ThunderHack/" + name + "/"))) {
            this.config = "ThunderHack/" + name + "/";
        } else {
            this.config = "ThunderHack/config/";
        }
        Thunderhack.friendManager.onLoad();
        Thunderhack.enemyManager.onLoad();
        MacroManager.onLoad();
        for (Feature feature : this.features) {
            try {
                loadSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Thunderhack.moduleManager.onLoad();
        saveCurrentConfig();
    }

    public boolean configExists(String name) {
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("ThunderHack").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        return files.contains(new File("ThunderHack/" + name + "/"));
    }


    public void deleteConfig(String name){
        this.config = "ThunderHack/" + name;
        File path = new File(this.config);
        path.renameTo(new File("ThunderHack/oldcfg" + Math.random()*1000));
    }


    public void saveConfig(String name) {
        this.config = "ThunderHack/" + name + "/";
        File path = new File(this.config);
        if (!path.exists())
            path.mkdir();
        Thunderhack.friendManager.saveFriends();
        Thunderhack.enemyManager.saveEnemies();
        Thunderhack.macromanager.saveMacro();
        for (Feature feature : this.features) {
                try {
                    saveSettings(feature);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
      //  if(savecurrent)
       //     saveCurrentConfig();
    }

    public void saveCurrentConfig() {
        File currentConfig = new File("ThunderHack/currentconfig.txt");
        try {
            if (currentConfig.exists()) {
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("ThunderHack", ""));
                writer.close();
            } else {
                currentConfig.createNewFile();
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("ThunderHack", ""));
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadCurrentConfig() {
        File currentConfig = new File("ThunderHack/currentconfig.txt");
        String name = "config";
        try {
            if (currentConfig.exists()) {
                Scanner reader = new Scanner(currentConfig);
                while (reader.hasNextLine())
                    name = reader.nextLine();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentcfg = name;
        return name;
    }

    public void resetConfig(boolean saveConfig, String name) {
        for (Feature feature : this.features)
            feature.reset();
        if (saveConfig)
            saveConfig(name);
    }

    public void saveSettings(Feature feature) throws IOException {
        JsonObject object = new JsonObject();
        File directory = new File(this.config + getDirectory(feature));
        if (!directory.exists())
            directory.mkdir();
        String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
        Path outputFile = Paths.get(featureName);
        if (!Files.exists(outputFile))
            Files.createFile(outputFile);
        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        String json = gson.toJson(writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        writer.write(json);
        writer.close();
    }

    public void init() {
        this.features.addAll(Thunderhack.moduleManager.modules);
        this.features.add(Thunderhack.friendManager);
        this.features.add(Thunderhack.enemyManager);
        String name = loadCurrentConfig();
        loadConfig(name,true);
    }

    private void loadSettings(Feature feature) throws IOException {
        String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
        Path featurePath = Paths.get(featureName);
        if (!Files.exists(featurePath))
            return;
        loadPath(featurePath, feature);
    }

    private void loadPath(Path path, Feature feature) throws IOException {
        InputStream stream = Files.newInputStream(path);
        try {
            loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException e) {
            loadFile(new JsonObject(), feature);
        }
        stream.close();
    }


    public String getDirectory(Feature feature) {
        String directory = "";
        if (feature instanceof Module)
            directory = directory + ((Module) feature).getCategory().getName() + "/";
        return directory;
    }

}