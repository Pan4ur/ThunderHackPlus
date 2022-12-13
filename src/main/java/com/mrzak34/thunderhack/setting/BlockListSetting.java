package com.mrzak34.thunderhack.setting;

import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockListSetting {
    private List<Block> blocks;

    private List<String> blocksString;

    public BlockListSetting(String... blockNames) {
        blocks = new ArrayList<>();
        blocksString = new ArrayList<>();

        for (String name : blockNames) {
            if (!blocksString.contains(name.toUpperCase(Locale.ENGLISH)) && Block.getBlockFromName(name) != null) {
                blocksString.add(name.toUpperCase(Locale.ENGLISH));
            }
        }
    }

    public BlockListSetting(ArrayList<String> blockNames) {
        blocks = new ArrayList<>();
        blocksString = new ArrayList<>();

        for (String name : blockNames) {
            if (!blocksString.contains(name.toUpperCase(Locale.ENGLISH)) && Block.getBlockFromName(name) != null) {
                blocksString.add(name.toUpperCase(Locale.ENGLISH));
            }
        }
    }

    public void addBlockStrings(ArrayList<String> blockNames) {
        for (String name : blockNames) {
            if (!blocksString.contains(name.toUpperCase(Locale.ENGLISH)) && Block.getBlockFromName(name) != null) {
                blocksString.add(name.toUpperCase(Locale.ENGLISH));
            }
        }
    }

    public boolean addBlock(String blockName) {
        if (!blocksString.contains(blockName.toUpperCase(Locale.ENGLISH)) && Block.getBlockFromName(blockName) != null ) {
            blocksString.add(blockName.toUpperCase(Locale.ENGLISH));
            return true;
        }
        return false;
    }

    public boolean removeBlock(String blockName) {
        return blocksString.remove(blockName.toUpperCase(Locale.ENGLISH));
    }

    public void refreshBlocks() {
        blocks.clear();
        blocksString.forEach(str -> {
            Block block = Block.getBlockFromName(str);
            if (block != null) {
                blocks.add(block);
            }
        });
    }

    public List<String> getBlocksAsString() {
        List<String> str = new ArrayList<>();
        blocks.forEach(block -> {
            String path = block.getRegistryName().getPath();
            if (path != null) {
                str.add(path);
            }
        });
        return str;
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}