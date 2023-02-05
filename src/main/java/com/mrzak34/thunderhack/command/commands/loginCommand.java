package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
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
            Command.sendMessage("Аккаунт изменен на: " + Util.mc.getSession().getUsername());
        }
        catch (Exception exception) {
            Command.sendMessage("Использование: .login nick");
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
            Command.sendMessage("Неверное имя! " + exception);
        }
    }

}
