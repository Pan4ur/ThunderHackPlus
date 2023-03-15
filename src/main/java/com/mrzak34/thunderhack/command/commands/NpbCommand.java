package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;

import java.util.Arrays;

public class NpbCommand extends Command {


    public NpbCommand() {
        super("npb");
    }

    @Override
    public void execute(String[] args) {
        if (args.length >= 3) {
            String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length - 1));
            Command.sendMessageWithoutTH(ChatFormatting.GRAY + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "*" + ChatFormatting.RESET + ChatFormatting.GRAY + "] Игрок " + ChatFormatting.AQUA + ChatFormatting.BOLD + args[0] + ChatFormatting.RESET + ChatFormatting.GRAY + " забанил " + ChatFormatting.RED + ChatFormatting.BOLD + args[1] + ChatFormatting.RESET + ChatFormatting.GRAY + " на 10 минут по причине" + ChatFormatting.GREEN + " " + ChatFormatting.BOLD + text + ChatFormatting.RESET);
        }
    }
    //[*] Игрок unreasonable забанил vova_lox88 по причине: хуесос
}
