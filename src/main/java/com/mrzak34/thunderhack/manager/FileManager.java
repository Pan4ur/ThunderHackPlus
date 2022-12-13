package com.mrzak34.thunderhack.manager;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.modules.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager
        extends Feature {
    private final Path base = this.getMkDirectory(this.getRoot(), "ThunderHack");
    private final Path config = this.getMkDirectory(this.base, "config");
    private final Path notebot = this.getMkDirectory(this.base, "tmp");
    private final Path avatars = this.getMkDirectory(this.base, "friendsAvatars");
    private final Path skins = this.getMkDirectory(this.base, "skins");
    private final Path niggs = this.getMkDirectory(this.base, "customimg");


    public FileManager() {
        this.getMkDirectory(this.base, "pvp");
        for (Module.Category category : Thunderhack.moduleManager.getCategories()) {
            this.getMkDirectory(this.config, category.getName());
        }
    }

    private Path lookupPath(Path root, String... paths) {
        return Paths.get(root.toString(), paths);
    }
    private Path getRoot() {
        return Paths.get("");
    }

    private void createDirectory(Path dir) {
        try {
            if (!Files.isDirectory(dir)) {
                if (Files.exists(dir)) {
                    Files.delete(dir);
                }
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getMkDirectory(Path parent, String... paths) {
        if (paths.length < 1) {
            return parent;
        }
        Path dir = this.lookupPath(parent, paths);
        this.createDirectory(dir);
        return dir;
    }

    public Path getBasePath() {
        return this.base;
    }

    public Path getConfig() {
        return this.getBasePath().resolve("config");
    }


}

