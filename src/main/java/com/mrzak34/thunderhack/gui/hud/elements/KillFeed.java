package com.mrzak34.thunderhack.gui.hud.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.commands.StaffCommand;
import com.mrzak34.thunderhack.events.DeathEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.modules.funnygame.C4Aura;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class KillFeed extends HudElement {
    public KillFeed() {
        super("KillFeed","статистика убийств",100,100);
    }

    List<String> players = new CopyOnWriteArrayList<>();


    @SubscribeEvent
    public void onPlayerDeath(DeathEvent e){
        if(Aura.target != null && Aura.target == e.player){
            players.add(getFullName(e.player.getName()));
            return;
        }
        if(C4Aura.target != null && C4Aura.target == e.player){
            players.add(getFullName(e.player.getName()));
            return;
        }
        if(Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).target != null && Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).target == e.player){
            players.add(getFullName(e.player.getName()));
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        int y_offset1 = 11;
        float scale_x = 50;
        for (String player : players) {
            if (player != null) {
                if (FontRender.getStringWidth6("EZ - " + player) > scale_x) {
                    scale_x = FontRender.getStringWidth6("EZ - " + player);
                }
            }
            y_offset1 += 13;
        }
        RenderUtil.drawBlurredShadow(getPosX(), getPosY(), scale_x + 20, 20 + y_offset1, 20, shadowColor.getValue().getColorObject());
        RoundedShader.drawRound(getPosX(), getPosY(), scale_x + 20, 20 + y_offset1, 7f, color2.getValue().getColorObject());
        FontRender.drawCentString6("KillFeed [" + players.size() +"]", getPosX() + (scale_x + 20) / 2, getPosY() + 5, textColor.getValue().getColor());
        RoundedShader.drawRound(getPosX() + 2, getPosY() + 13, scale_x + 16, 1, 0.5f, color3.getValue().getColorObject());
        int y_offset = 11;
        for (String player : players) {
            GlStateManager.pushMatrix();
            GlStateManager.resetColor();
            FontRender.drawString6( ChatFormatting.RED + "EZ - " + ChatFormatting.RESET + player, getPosX() + 5, getPosY() + 18 + y_offset, -1, false);
            GlStateManager.resetColor();
            GlStateManager.popMatrix();
            y_offset += 13;
        }
    }


    public static String getFullName(String raw) {
        for (NetworkPlayerInfo player : mc.player.connection.getPlayerInfoMap()) {
            if (mc.isSingleplayer() || player.getPlayerTeam() == null) break;
            String name = Arrays.asList(player.getPlayerTeam().getMembershipCollection().stream().toArray()).toString().replace("[", "").replace("]", "");
            if(name.contains(raw)){
                return player.getPlayerTeam().getPrefix() + name;
            }
        }
        return "null";
    }
}
