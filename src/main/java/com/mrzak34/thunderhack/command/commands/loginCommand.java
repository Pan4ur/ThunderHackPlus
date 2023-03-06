package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;

public class loginCommand extends Command {

    public loginCommand() {
        super("login");
    }

    @Override
    public void execute(String[] var1) {
        try {
            login(var1[0]);
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("Аккаунт изменен на: " + Util.mc.getSession().getUsername());
            } else {
                Command.sendMessage("Account switched to: " + Util.mc.getSession().getUsername());
            }
        }
        catch (Exception exception) {
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("Использование: .login nick");
            } else {
                Command.sendMessage("Try: .login nick");
            }
        }
    }


    public static void login(String string) {
        try {
            Field field = Minecraft.class.getDeclaredField("field_71449_j"); //session
            field.setAccessible(true);
            Field field2 = Field.class.getDeclaredField("modifiers");
            field2.setAccessible(true);
            field2.setInt(field, field.getModifiers() & 0xFFFFFFEF);
            field.set(Util.mc, new Session(string, "", "", "mojang"));
        }
        catch (Exception exception) {
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("Неверное имя! " + exception);
            } else {
                Command.sendMessage("Wrong name! " + exception);
            }
        }
    }

}
