package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class KitCommand extends Command {
    final static private String pathSave = "ThunderHack/kits/AutoGear.json";

    private static final HashMap<String, String> errorMessage = new HashMap<String, String>() {
        {
            put("NoPar", "Not enough parameters");
            put("Exist", "This kit arleady exist");
            put("Saving", "Error saving the file");
            put("NoEx", "Kit not found");
        }
    };

    public KitCommand() {
        super("kit", new String[]{"<create/set/del/list>", "<name>"});
    }



    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("kit <create/set/del/list> <name>");
            return;
        }
        if (commands.length == 2) {
            switch (commands[0]) {
                case "list": {
                    listMessage();
                    return;
                }
            }
            return;
        }

        if (commands.length >= 2) {
            switch (commands[0]) {
                case "create": {
                    save(commands[1]);
                    return;
                }
                case "set": {
                    set(commands[1]);
                    return;
                }
                case "del": {
                    delete(commands[1]);
                    return;
                }
            }
            KitCommand.sendMessage("WTF R U MEAN ТУПОЙ ДЕБИЛ?!?!?!?!?!?");
        }
    }


    private void listMessage() {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            int lenghtJson = completeJson.entrySet().size();
            for (int i = 0; i < lenghtJson; i++) {
                String item = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject().entrySet().toArray()[i].toString().split("=")[0];
                if (!item.equals("pointer"))
                    Command.sendMessage("Kit avaible: " + item);
            }

        } catch (IOException e) {
            // Case not found, reset
            errorMessage("NoEx");
        }
    }

    private void delete(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                // Delete
                completeJson.remove(name);
                // Check if it's setter
                if (completeJson.get("pointer").getAsString().equals(name))
                    completeJson.addProperty("pointer", "none");
                // Save
                saveFile(completeJson, name, "deleted");
            } else errorMessage("NoEx");

        } catch (IOException e) {
            // Case not found, reset
            errorMessage("NoEx");
        }
    }

    private void set(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                // Change the value
                completeJson.addProperty("pointer", name);
                // Save
                saveFile(completeJson, name, "selected");
            } else errorMessage("NoEx");

        } catch (IOException e) {
            // Case not found, reset
            errorMessage("NoEx");
        }
    }

    private void save(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                errorMessage("Exist");
                return;
            }
            // We can continue

        } catch (IOException e) {
            // Case not found, reset
            completeJson.addProperty("pointer", "none");
        }

        // String that is going to be our inventory
        StringBuilder jsonInventory = new StringBuilder();
        for (ItemStack item : mc.player.inventory.mainInventory) {
            // Add everything
            jsonInventory.append(item.getItem().getRegistryName().toString() + item.getMetadata()).append(" ");
        }
        // Add to the json
        completeJson.addProperty(name, jsonInventory.toString());
        // Save json
        saveFile(completeJson, name, "saved");
    }

    private void saveFile(JsonObject completeJson, String name, String operation) {
        // Save the json
        try {
            // Open
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathSave));
            // Write
            bw.write(completeJson.toString());
            // Save
            bw.close();
            // Chat message
            Command.sendMessage("Kit " + name + " " + operation);
        } catch (IOException e) {
            errorMessage("Saving");
        }
    }

    private static void errorMessage(String e) {
        Command.sendMessage("Error: " + errorMessage.get(e));
    }

    public static String getCurrentSet() {

        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (!completeJson.get("pointer").getAsString().equals("none"))
                return completeJson.get("pointer").getAsString();


        } catch (IOException e) {
            // Case not found, reset
        }
        errorMessage("NoEx");
        return "";
    }

    public static String getInventoryKit(String kit) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            return completeJson.get(kit).getAsString();


        } catch (IOException e) {
            // Case not found, reset
        }
        errorMessage("NoEx");
        return "";
    }

}
