package com.mrzak34.thunderhack.manager;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.util.ThunderUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FriendManager {
    public static List<String> friends = new ArrayList<>();

    public static void loadFriends() {
        try {
            File file = new File("ThunderHack/misc/friends.txt");

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while (reader.ready()) {
                        friends.add(reader.readLine());
                    }

                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void saveFriends() {
        File file = new File("ThunderHack/misc/friends.txt");
        try {
            file.createNewFile();
        } catch (Exception ignored) {

        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String friend : friends) {
                writer.write(friend + "\n");
            }
        } catch (Exception ignored) {
        }
    }

    public boolean isFriend(String name) {
        return friends.stream().anyMatch(friend -> friend.equalsIgnoreCase(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return this.isFriend(player.getName());
    }

    public boolean isEnemy(EntityPlayer player) {
        return false;
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public List<String> getFriends() {
        return friends;
    }

    public void addFriend(String friend) {
        friends.add(friend);
        try {
            ThunderUtils.saveUserAvatar("https://minotar.net/helm/" + friend + "/100.png", friend);
        } catch (Exception e) {
            Command.sendMessage("Не удалось загрузить скин!");
        }
    }


    public void clear() {
        friends.clear();
    }
}

