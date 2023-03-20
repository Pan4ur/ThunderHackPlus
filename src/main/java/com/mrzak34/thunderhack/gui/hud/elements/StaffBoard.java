package com.mrzak34.thunderhack.gui.hud.elements;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.commands.StaffCommand;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.gui.hud.elements.HudEditorGui;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StaffBoard extends HudElement {
    private static final Pattern validUserPattern = Pattern.compile("^\\w{3,16}$");
    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE)));
    private final Setting<Float> psize = this.register(new Setting<>("Size", 1f, 0.1f, 2f));
    List<String> players = new java.util.ArrayList<>();
    List<String> notSpec = new java.util.ArrayList<>();
    private final LinkedHashMap<UUID, String> nameMap = new LinkedHashMap<>();


    public StaffBoard() {
        super("StaffBoard", "StaffBoard", 50, 50);
    }

    public static void size(double width, double height, double animation) {
        GL11.glTranslated(width, height, 0);
        GL11.glScaled(animation, animation, 1);
        GL11.glTranslated(-width, -height, 0);
    }

    public static List<String> getOnlinePlayer() {
        return mc.player.connection.getPlayerInfoMap().stream()
                .map(NetworkPlayerInfo::getGameProfile)
                .map(GameProfile::getName)
                .filter(profileName -> validUserPattern.matcher(profileName).matches())
                .collect(Collectors.toList());
    }

    public static List<String> getOnlinePlayerD() {
        List<String> S = new ArrayList<>();
        for (NetworkPlayerInfo player : mc.player.connection.getPlayerInfoMap()) {
            if (mc.isSingleplayer() || player.getPlayerTeam() == null) break;
            String prefix = player.getPlayerTeam().getPrefix();
            if (check(ChatFormatting.stripFormatting(prefix).toLowerCase())
                    || StaffCommand.staffNames.toString().toLowerCase().contains(player.getGameProfile().getName().toLowerCase())
                    || player.getGameProfile().getName().toLowerCase().contains("1danil_mansoru1") || player.getPlayerTeam().getPrefix().contains("YT")
                    || (player.getPlayerTeam().getPrefix().contains("Y") && player.getPlayerTeam().getPrefix().contains("T"))) {
                String name = Arrays.asList(player.getPlayerTeam().getMembershipCollection().stream().toArray()).toString().replace("[", "").replace("]", "");

                if (player.getGameType() == GameType.SPECTATOR) {
                    S.add(player.getPlayerTeam().getPrefix() + name + ":gm3");
                    continue;
                }
                S.add(player.getPlayerTeam().getPrefix() + name + ":active");
            }
        }
        return S;
    }

    public List<String> getVanish() {
        List<String> list = new ArrayList<>();
        for (ScorePlayerTeam s : mc.world.getScoreboard().getTeams()) {
            if (s.getPrefix().length() == 0 || mc.isSingleplayer()) continue;
            String name = Arrays.asList(s.getMembershipCollection().stream().toArray()).toString().replace("[", "").replace("]", "");

            if (getOnlinePlayer().contains(name) || name.isEmpty())
                continue;
            if (StaffCommand.staffNames.toString().toLowerCase().contains(name.toLowerCase())
                    && check(s.getPrefix().toLowerCase())
                    || check(s.getPrefix().toLowerCase())
                    || name.toLowerCase().contains("1danil_mansoru1")
                    || s.getPrefix().contains("YT")
                    || (s.getPrefix().contains("Y") && s.getPrefix().contains("T"))
            )
                list.add(s.getPrefix() + name + ":vanish");
        }
        return list;
    }

    public static boolean check(String name) {
        return name.contains("helper") || name.contains("moder") || name.contains("admin") || name.contains("owner") || name.contains("curator") || name.contains("куратор") || name.contains("модер") || name.contains("админ") || name.contains("хелпер");
    }


    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        int y_offset1 = 11;
        List<String> all = new java.util.ArrayList<>();
        all.addAll(players);
        all.addAll(notSpec);
        float scale_x = 50;
        for (String player : all) {
            if (player != null) {
                String a = player.split(":")[0] + " " + (player.split(":")[1].equalsIgnoreCase("vanish") ? ChatFormatting.RED + "VANISH" : player.split(":")[1].equalsIgnoreCase("gm3") ? ChatFormatting.RED + "VANISH " + ChatFormatting.YELLOW + "(NEAR!)" : ChatFormatting.GREEN + "ACTIVE");
                if (FontRender.getStringWidth6(a) > scale_x) {
                    scale_x = FontRender.getStringWidth6(a);
                }
            }
            y_offset1 += 13;
        }


        GlStateManager.pushMatrix();
        size(getPosX() + 50, getPosY() + (20 + y_offset1) / 2f, psize.getValue());

        RenderUtil.drawBlurredShadow(getPosX(), getPosY(), scale_x + 20, 20 + y_offset1, 20, shadowColor.getValue().getColorObject());


        RoundedShader.drawRound(getPosX(), getPosY(), scale_x + 20, 20 + y_offset1, 7f, color2.getValue().getColorObject());
        FontRender.drawCentString6("StaffBoard", getPosX() + (scale_x + 20) / 2, getPosY() + 5, textColor.getValue().getColor());
        RoundedShader.drawRound(getPosX() + 2, getPosY() + 13, scale_x + 16, 1, 0.5f, color3.getValue().getColorObject());


        int y_offset = 11;
        for (String player : all) {
            GlStateManager.pushMatrix();
            GlStateManager.resetColor();
            String a = player.split(":")[0] + " " + (player.split(":")[1].equalsIgnoreCase("vanish") ? ChatFormatting.RED + "VANISH" : player.split(":")[1].equalsIgnoreCase("gm3") ? ChatFormatting.RED + "VANISH " + ChatFormatting.YELLOW + "(NEAR!)" : ChatFormatting.GREEN + "ACTIVE");
            FontRender.drawString6(a, getPosX() + 5, getPosY() + 18 + y_offset, -1, false);
            GlStateManager.resetColor();
            GlStateManager.popMatrix();
            y_offset += 13;
        }
        GlStateManager.popMatrix();
    }


    @Override
    public void onDisable() {
        nameMap.clear();
    }

    @Override
    public void onUpdate() {
        if (mc.player.ticksExisted % 10 == 0) {
            players = getVanish();
            notSpec = getOnlinePlayerD();
            players.sort(String::compareTo);
            notSpec.sort(String::compareTo);
        }
    }
}
