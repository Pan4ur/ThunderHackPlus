package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.macro.Macro;
import com.mrzak34.thunderhack.manager.MacroManager;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class MacroCommand extends Command {


    public MacroCommand() {
        super("macro");
    }

    @Override
    public void execute(String[] args) {
            if(args[0] == null){
                Command.sendMessage(usage());
            }
            if (args[0].equals("list")) {
                sendMessage("Макросы:");
                sendMessage(" ");
                Thunderhack.macromanager.getMacros().forEach(macro -> sendMessage(macro.getName() + (macro.getBind() != Keyboard.KEY_NONE ? " [" + Keyboard.getKeyName(macro.getBind()) + "]" : "") + " {" + macro.getText() + "}"));
            }
            if (args[0].equals("remove")) {
                if (Thunderhack.macromanager.getMacroByName(args[1]) != null) {
                    Macro macro = Thunderhack.macromanager.getMacroByName(args[1]);
                    Thunderhack.macromanager.removeMacro(macro);
                    sendMessage("Удален макрос " + macro.getName());
                } else {
                    sendMessage("Не существует макроса с именем " + args[1]);
                }
            }
        if(args.length >= 4) {
            if (args[0].equals("add")) {
                String name = args[1];
                String bind = args[2].toUpperCase();
                String text = String.join(" ", Arrays.copyOfRange(args, 3, args.length - 1));
                if(Keyboard.getKeyIndex(bind) == Keyboard.KEY_NONE) {
                    sendMessage("Неправильный бинд!");
                    return;
                }
                Macro macro = new Macro(name, text, Keyboard.getKeyIndex(bind));
                Thunderhack.macromanager.addMacro(macro);
                sendMessage("Добавлен макрос " + name + " на кнопку " + Keyboard.getKeyName(macro.getBind()));
            }else {
                sendMessage(usage());
            }
        }
    }


    String usage(){
        return "macro add/remove/list (macro add name key text), (macro remove name)";
    }
}
