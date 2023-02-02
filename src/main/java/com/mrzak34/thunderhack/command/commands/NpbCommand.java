package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.macro.Macro;
import com.mrzak34.thunderhack.util.ChatColor;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class NpbCommand extends Command {


    public NpbCommand() {
        super("npb");
    }

    @Override
    public void execute(String[] args) {
        if(args.length >= 3) {
            String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length - 1));
            Command.sendMessageWithoutTH( ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "*" + ChatColor.RESET  + ChatColor.GRAY + "] Игрок " + ChatColor.AQUA + ChatColor.BOLD +  args[0] + ChatColor.RESET +  ChatColor.GRAY + " забанил " + ChatColor.RED  +  ChatColor.BOLD + args[1] + ChatColor.RESET  + ChatColor.GRAY +  " на 10 минут по причине" + ChatColor.GREEN + " " +  ChatColor.BOLD + text + ChatColor.RESET);
        }
    }
    //[*] Игрок unreasonable забанил vova_lox88 по причине: хуесос
}
