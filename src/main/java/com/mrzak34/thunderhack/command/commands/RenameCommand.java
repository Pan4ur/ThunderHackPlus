package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class RenameCommand extends Command {

    public RenameCommand() {
        super("rename", new String[]{"<module>",  "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        Setting setting;
        if (commands.length == 1) {
            ModuleCommand.sendMessage("Дебил");
            return;
        }
        Module module = Thunderhack.moduleManager.getModuleByDisplayName(commands[0]);
        if (module == null) {
            ModuleCommand.sendMessage("Такого модуля нет!");
        }
        if (commands.length == 2) {

            ModuleCommand.sendMessage("Модуль " + module.getDisplayName() + " переименован в " + commands[1]);

            module.setDisplayName(commands[1]);
        }


    }

}
