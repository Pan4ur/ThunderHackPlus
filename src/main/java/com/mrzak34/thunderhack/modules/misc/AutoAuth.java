package com.mrzak34.thunderhack.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.RandomStringUtils;


public class AutoAuth extends Module {
    public AutoAuth() {
        super("AutoAuth", "Автоматически-логинится на -серверах","AutoAuth", Category.MISC);
    }


    private String password;
    private final Setting<Mode> passwordMode = register(new Setting<>("Password Mode", Mode.Custom));
    public Setting < String > cpass = register ( new Setting <> ( "Password" , "babidjon777" ,v-> passwordMode.getValue() == Mode.Custom));
    public Setting<Boolean> showPasswordInChat = register(new Setting<>("Show Password In Chat", true));

    private enum Mode {
        Custom, Random, Qwerty
    }

    @Override
    public void onEnable(){
        Command.sendMessage(ChatFormatting.RED + "Внимание!!! " + ChatFormatting.RESET + "Пароль сохраняется в конфиге, перед передачей конфига " + ChatFormatting.RED +  " ВЫКЛЮЧИ МОДУЛЬ!");
        Command.sendMessage(ChatFormatting.RED + "Внимание!!! " + ChatFormatting.RESET + "Пароль сохраняется в конфиге, перед передачей конфига " + ChatFormatting.RED +  " ВЫКЛЮЧИ МОДУЛЬ!");
        Command.sendMessage(ChatFormatting.RED + "Внимание!!! " + ChatFormatting.RESET + "Пароль сохраняется в конфиге, перед передачей конфига " + ChatFormatting.RED +  " ВЫКЛЮЧИ МОДУЛЬ!");
        Command.sendMessage(ChatFormatting.RED + "Attention!!! " + ChatFormatting.RESET + "The password is saved in the config, before sharing the config " + ChatFormatting.RED + " TURN OFF THIS MODULE!");
        Command.sendMessage(ChatFormatting.RED + "Attention!!! " + ChatFormatting.RESET + "The password is saved in the config, before sharing the config " + ChatFormatting.RED + " TURN OFF THIS MODULE!");
        Command.sendMessage(ChatFormatting.RED + "Attention!!! " + ChatFormatting.RESET + "The password is saved in the config, before sharing the config " + ChatFormatting.RED + " TURN OFF THIS MODULE!");
    }

    @Override
    public void onDisable(){
        Command.sendMessage(ChatFormatting.RED +  "AutoAuth " + ChatFormatting.RESET + "reseting password...");
        cpass.setValue("none");
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if(event.getPacket() instanceof SPacketChat) {
            SPacketChat pac = event.getPacket();
            if (passwordMode.getValue() == Mode.Custom) {
                this.password = cpass.getValue();
            } else if (passwordMode.getValue() == Mode.Qwerty) {
                this.password = "qwerty123";
            } else if (passwordMode.getValue() == Mode.Random) {
                String str1 = RandomStringUtils.randomAlphabetic(5);
                String str2 = RandomStringUtils.randomPrint(5);
                this.password = str1 + str2;
            }
            if (passwordMode.getValue() == Mode.Custom && (this.password == null || this.password.isEmpty()))
                return;
            if (pac.getChatComponent().getFormattedText().contains("/reg") || pac.getChatComponent().getFormattedText().contains("/register") || pac.getChatComponent().getFormattedText().contains("Зарегестрируйтесь")) {
                AutoAuth.mc.player.sendChatMessage("/reg " + this.password + " " + this.password);
                if (this.showPasswordInChat.getValue())
                    Command.sendMessage("Your password: " + ChatFormatting.RED + this.password);
                if (Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isEnabled())
                    NotificationManager.publicity("You are successfully registered!", 4, Notification.Type.SUCCESS);
            } else if (pac.getChatComponent().getFormattedText().contains("Авторизуйтесь") || pac.getChatComponent().getFormattedText().contains("/l")) {
                AutoAuth.mc.player.sendChatMessage("/login " + this.password);
                if (Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isEnabled())
                    NotificationManager.publicity("You are successfully login!", 4, Notification.Type.SUCCESS);
            }
        }
    }
}
