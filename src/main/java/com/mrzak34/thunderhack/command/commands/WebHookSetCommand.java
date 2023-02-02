package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.DiscordWebhook;

public class WebHookSetCommand extends Command{

    public WebHookSetCommand() {
        super("webhook");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Напиши URL вебхука");
            return;
        }
        DiscordWebhook.saveurl(commands[0]);
        sendMessage("Успешно!");
    }
}
