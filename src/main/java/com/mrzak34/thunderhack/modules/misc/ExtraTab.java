package com.mrzak34.thunderhack.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.manager.EnemyManager;
import com.mrzak34.thunderhack.manager.FriendManager;
import com.mrzak34.thunderhack.util.PlayerUtils;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class ExtraTab extends Module {
    private static ExtraTab INSTANCE = new ExtraTab();
    public Setting<Integer> size = this.register(new Setting<Integer>("Size", 250, 1, 1000));

    public Setting<Integer> X = this.register(new Setting<Integer>("ManagerX", 900, 0, 1000));
    public Setting<Integer> Y = this.register(new Setting<Integer>("ManagerY", 900, 0, 1000));
public Setting<SubBind> breakBind = this.register(new Setting<>("Bind", new SubBind(Keyboard.KEY_TAB)));

    public ExtraTab() {
        super("ExtraTab", "расширяет таб", Module.Category.MISC, false, false, false);
        this.setInstance();
    }

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String name;
        String string = name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (Thunderhack.friendManager.isFriend(name)) {
            //   NotificationManager.publicity("Friends",name + " на сервере!", 3, NotificationType.INFO);
            return ChatFormatting.GREEN + name;
        }
        return name;
    }

    public static ExtraTab getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ExtraTab();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    float y1 = 0;
    float x1 = 0;


    private ArrayList onlineFriends = new ArrayList();
    private ArrayList onlineEnemies = new ArrayList();

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){

        if(!PlayerUtils.isKeyDown(breakBind.getValue().getKey())){
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        y1 = sr.getScaledHeight() / (1000f/ Y.getValue());
        x1 = sr.getScaledWidth() / (1000f/ X.getValue());
        RenderUtil.drawSmoothRect(x1, y1, x1 + 250,y1 + 150,new Color(0x9E000000, true).getRGB());
        RenderUtil.drawSmoothRect(x1, y1, x1 + 250,y1 + 15,new Color(0xDA262626, true).getRGB());

        Util.fr.drawStringWithShadow("Друзья",x1 + 15, y1 + 3, PaletteHelper.astolfo(false, 1).getRGB());
        Util.fr.drawStringWithShadow("Попущенцы",x1 + 200, y1 + 3, PaletteHelper.astolfo(false, 1).getRGB());



        RenderUtil.drawSmoothRect(x1 + 124, y1, x1 + 126,y1 + 150,new Color(0xB8262626, true).getRGB());



        for( FriendManager.Friend friend : Thunderhack.friendManager.getFriends() ) {
            if(mc.player.connection.getPlayerInfo(friend.getUsername()) != null && !onlineFriends.contains(friend.getUsername())){
                onlineFriends.add(friend.getUsername());
            }
            if(mc.player.connection.getPlayerInfo(friend.getUsername()) == null && onlineFriends.contains(friend.getUsername())){
                onlineFriends.remove(friend.getUsername());
            }
            if(mc.player.connection.getPlayerInfo(friend.getUsername()) == null && onlineFriends.contains(friend.getUsername())){
               onlineFriends.remove(friend.getUsername());
            }
        }

        for( EnemyManager.Enemy enemy : Thunderhack.enemyManager.getEnemies() ) {
            if(mc.player.connection.getPlayerInfo(enemy.getUsername()) != null && !onlineEnemies.contains(enemy.getUsername())){
                onlineEnemies.add(enemy.getUsername());
            }
            if(mc.player.connection.getPlayerInfo(enemy.getUsername()) == null && onlineEnemies.contains(enemy.getUsername())){
                onlineEnemies.remove(enemy.getUsername());
            }
            if(mc.player.connection.getPlayerInfo(enemy.getUsername()) == null && onlineEnemies.contains(enemy.getUsername())){
                onlineEnemies.remove(enemy.getUsername());
            }
        }

        int schetchik_gaygera = 2;
        int schetchik_gaygera2= 2;

        for(Object onlinefriendnames : onlineFriends){
            FriendNigger(onlinefriendnames.toString(),schetchik_gaygera);
            ++schetchik_gaygera;
        }

        for(Object onlineenemiesnames : onlineEnemies){
            EnemyNigger(onlineenemiesnames.toString(),schetchik_gaygera2);
            ++schetchik_gaygera2;
        }


    }

    public void FriendNigger( String name,int position){
        Util.fr.drawStringWithShadow(name,x1 + 5 ,y1 + position*10,new Color(0x30F803).getRGB());
    }


    public void EnemyNigger( String name,int position){
        Util.fr.drawStringWithShadow(name,x1 + 190 ,y1 + position*10,new Color(0xF80303).getRGB());
    }

}

