package com.mrzak34.thunderhack.util;


import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mrzak34.thunderhack.command.Command;
import java.lang.reflect.Field;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;


public class AccountAuthenticator {
    public static String a(String string, String string2) {
        YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication)yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
        yggdrasilUserAuthentication.setUsername(string);
        yggdrasilUserAuthentication.setPassword(string2);
        String string3 = null;
        try {
            yggdrasilUserAuthentication.logIn();
            try {
                Field field = Minecraft.class.getDeclaredField(fieldSession);
                field.setAccessible(true);
                Field field2 = Field.class.getDeclaredField("modifiers");
                field2.setAccessible(true);
                field2.setInt(field, field.getModifiers() & 0xFFFFFFEF);
                field.set(Util.mc, new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang"));
                string3 = "Logged [License]: " + Util.mc.getSession().getUsername();
            }
            catch (Exception exception) {
                string3 = "Unknown error.";
                Command.sendMessage("AuthUtils: login " + exception);
            }
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException) {
            string3 = "Cannot contact authentication server!";
        }
        catch (AuthenticationException authenticationException) {
            string3 = authenticationException.getMessage().contains("Invalid username or password.") || authenticationException.getMessage().toLowerCase().contains("account migrated") ? "Wrong password!" : "Cannot contact authentication server!";
        }
        catch (NullPointerException nullPointerException) {
            string3 = "Wrong password!";
        }
        return string3;
    }

    public static String b(String string, String string2) {
        YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication)yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
        yggdrasilUserAuthentication.setUsername(string);
        yggdrasilUserAuthentication.setPassword(string2);
        try {
            yggdrasilUserAuthentication.logIn();
            return yggdrasilUserAuthentication.getSelectedProfile().getName();
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static void a(String string) {
        try {
            Field field = Minecraft.class.getDeclaredField(fieldSession);
            field.setAccessible(true);
            Field field2 = Field.class.getDeclaredField("modifiers");
            field2.setAccessible(true);
            field2.setInt(field, field.getModifiers() & 0xFFFFFFEF);
            field.set(Util.mc, new Session(string, "", "", "mojang"));
        }
        catch (Exception exception) {
            Command.sendMessage("AuthUtils: changeCrackedName " + exception);
        }
    }

    public static String fieldSession = isInstanceNotNull() ? "session" : "field_71449_j";

    public static boolean isInstanceNotNull() {
        try {
            return Minecraft.class.getDeclaredField("instance") != null;
        }
        catch (Exception exception) {
            return false;
        }
    }
}