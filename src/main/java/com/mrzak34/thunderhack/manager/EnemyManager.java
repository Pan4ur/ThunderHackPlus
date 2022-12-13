package com.mrzak34.thunderhack.manager;

import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

public class EnemyManager
        extends Feature {
    public List<Enemy> enemies = new ArrayList<Enemy>();

    public EnemyManager() {
        super("Enemies");
    }

    public boolean isEnemy(String name) {
        this.cleanEnemies();
        return this.enemies.stream().anyMatch(enemy -> enemy.username.equalsIgnoreCase(name));
    }

    public boolean isEnemy(EntityPlayer player) {
        return this.isEnemy(player.getName());
    }

    public void addEnemy(String name) {
        Enemy enemy = this.getEnemyByName(name);
        if (enemy != null) {
            this.enemies.add(enemy);
        }
        this.cleanEnemies();
    }

    public void removeEnemy(String name) {
        this.cleanEnemies();
        for (Enemy enemy : this.enemies) {
            if (!enemy.getUsername().equalsIgnoreCase(name)) continue;
            this.enemies.remove(enemy);
            break;
        }
    }

    public void onLoad() {
        this.enemies = new ArrayList<Enemy>();
        this.clearSettings();
    }

    public void saveEnemies() {
        this.clearSettings();
        this.cleanEnemies();
        for (Enemy enemy : this.enemies) {
            this.register(new Setting<String>(enemy.getUuid().toString(), enemy.getUsername()));
        }
    }

    public void cleanEnemies() {
        this.enemies.stream().filter(Objects::nonNull).filter(enemy -> enemy.getUsername() != null);
    }

    public List<Enemy> getEnemies() {
        this.cleanEnemies();
        return this.enemies;
    }


    public Enemy getEnemyByName(String input) {
        UUID uuid = PlayerUtils.getUUIDFromName(input);
        if (uuid != null) {
            Enemy enemy = new Enemy(input, uuid);
            return enemy;
        }
        return null;
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);

    }





    public static class Enemy {
        private final String username;
        private final UUID uuid;

        public Enemy(String username, UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }

        public String getUsername() {
            return this.username;
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}

