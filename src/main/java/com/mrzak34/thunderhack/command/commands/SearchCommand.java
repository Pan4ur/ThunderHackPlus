package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.render.Search;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class SearchCommand  extends Command {

    public SearchCommand() {
        super("search");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Search.defaultBlocks.isEmpty()) {
                sendMessage("Search list empty");
            } else {
                String f = "Search list: ";
                for (Block name :  Search.defaultBlocks) {
                    try {
                        f = f + name.getRegistryName() + ", ";
                    } catch (Exception exception) {
                    }
                }
                sendMessage(f);
            }
            return;
        }
        if (commands.length == 2) {
            if ("reset".equals(commands[0])) {
                Search.defaultBlocks.clear();
                sendMessage("Search got reset.");
                mc.renderGlobal.loadRenderers();
                return;
            }
            return;
        }

        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    Search.defaultBlocks.add(getRegisteredBlock(commands[1]));
                    sendMessage(ChatFormatting.GREEN + commands[1] + " added to search");
                    mc.renderGlobal.loadRenderers();
                    return;
                }
                case "del": {
                    Search.defaultBlocks.remove(getRegisteredBlock(commands[1]));
                    sendMessage(ChatFormatting.RED + commands[1] + " removed from search");
                    mc.renderGlobal.loadRenderers();
                    return;
                }
            }
            sendMessage("Unknown Command, try search add/del <block name>");
        }

    }

    private static Block getRegisteredBlock(String blockName) {
        return (Block)Block.REGISTRY.getObject(new ResourceLocation(blockName));
    }
}
