package com.mrzak34.thunderhack.gui.hud;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.commands.StaffCommand;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
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

public class StaffBoard extends Module {
    private static final Pattern validUserPattern = Pattern.compile("^\\w{3,16}$");
    static boolean yt2 = false;
    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));
    private final Setting<Float> psize = this.register(new Setting<>("Size", 1f, 0.1f, 2f));
    public Setting<Boolean> yt = register(new Setting<Boolean>("YT", true));
    float x1 = 0;
    float y1 = 0;
    int dragX, dragY = 0;
    boolean mousestate = false;
    List<String> players = new java.util.ArrayList<>();
    List<String> notSpec = new java.util.ArrayList<>();
    private final LinkedHashMap<UUID, String> nameMap = new LinkedHashMap<>();


    public StaffBoard() {
        super("StaffBoard", "StaffBoard", Module.Category.HUD);
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
        List<String> S = new java.util.ArrayList<>();
        for (NetworkPlayerInfo player : mc.player.connection.getPlayerInfoMap()) {
            if (mc.isSingleplayer() || player.getPlayerTeam() == null) break;
            String prefix = player.getPlayerTeam().getPrefix();

            if (check(ChatFormatting.stripFormatting(prefix).toLowerCase())
                    || StaffCommand.staffNames.toString().toLowerCase().contains(player.getGameProfile().getName().toLowerCase())
                    || player.getGameProfile().getName().toLowerCase().contains("1danil_mansoru1")
                    || player.getPlayerTeam().getPrefix().contains("YT")
                    || player.getGameProfile().getName().toLowerCase().contains("vas371")
                    || player.getGameProfile().getName().toLowerCase().contains("barslan_")
                    || (player.getPlayerTeam().getPrefix().contains("Y") && player.getPlayerTeam().getPrefix().contains("T") && yt2)
            ) {
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

    public static boolean check(String name) {
        String ip = (mc.getCurrentServerData() == null ? "SinglePlayer" : mc.getCurrentServerData().serverIP);
        if (Objects.equals(ip, "mcfunny.su")) {
            return name.contains("helper") || name.contains("moder") || name.contains("хелпер");
        }
        return name.contains("helper") || name.contains("moder") || name.contains("admin") || name.contains("owner") || name.contains("curator") || name.contains("хелпер") || name.contains("модер") || name.contains("админ") || name.contains("куратор");
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();


        int y_offset1 = 11;


        //  if (players.isEmpty() && notSpec.isEmpty()) return;
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
        size(x1 + 50, y1 + (20 + y_offset1) / 2f, psize.getValue());

        RenderUtil.drawBlurredShadow(x1, y1, scale_x + 20, 20 + y_offset1, 20, shadowColor.getValue().getColorObject());


        RoundedShader.drawRound(x1, y1, scale_x + 20, 20 + y_offset1, 7f, color2.getValue().getColorObject());
        FontRender.drawCentString6("StaffBoard", x1 + (scale_x + 20) / 2, y1 + 5, textColor.getValue().getColor());
        RoundedShader.drawRound(x1 + 2, y1 + 13, scale_x + 16, 1, 0.5f, color3.getValue().getColorObject());


        int y_offset = 11;
        for (String player : all) {
            GlStateManager.pushMatrix();
            GlStateManager.resetColor();
            String a = player.split(":")[0] + " " + (player.split(":")[1].equalsIgnoreCase("vanish") ? ChatFormatting.RED + "VANISH" : player.split(":")[1].equalsIgnoreCase("gm3") ? ChatFormatting.RED + "VANISH " + ChatFormatting.YELLOW + "(NEAR!)" : ChatFormatting.GREEN + "ACTIVE");
            FontRender.drawString6(a, x1 + 5, y1 + 18 + y_offset, -1, false);
            GlStateManager.resetColor();
            GlStateManager.popMatrix();
            y_offset += 13;
        }

        GlStateManager.popMatrix();

        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ThunderGui2) {
            if (isHovering()) {
                if (Mouse.isButtonDown(0) && mousestate) {
                    pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                    pos.getValue().setY((float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
                }
            }
        }
        if (Mouse.isButtonDown(0) && isHovering()) {
            if (!mousestate) {
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > x1 - 10 && normaliseX() < x1 + 100 && normaliseY() > y1 && normaliseY() < y1 + 100;
    }

    @Override
    public void onDisable() {
        nameMap.clear();
    }

    @Override
    public void onUpdate() {
        yt2 = yt.getValue();
        if (mc.player.ticksExisted % 10 == 0) {
            players = getVanish();
            notSpec = getOnlinePlayerD();
            players.sort(String::compareTo);
            notSpec.sort(String::compareTo);
        }
    }

    public List<String> getVanish() {
        List<String> list = new ArrayList<>();
        for (ScorePlayerTeam s : mc.world.getScoreboard().getTeams()) {
            if (s.getPrefix().length() == 0 || mc.isSingleplayer()) continue;
            String name = Arrays.asList(s.getMembershipCollection().stream().toArray()).toString().replace("[", "").replace("]", "");

            if (getOnlinePlayer().contains(name) || name.isEmpty())
                continue;

            if (StaffCommand.staffNames.toString().toLowerCase().contains(name.toLowerCase()) && check(s.getPrefix().toLowerCase()) || StaffCommand.staffNames.toString().toLowerCase().contains(name.toLowerCase()) && check(s.getPrefix().toLowerCase()) || name.toLowerCase().contains("1danil_mansoru1") || name.toLowerCase().contains("vas371") || name.toLowerCase().contains("barslan_") || s.getPrefix().contains("YT") || (s.getPrefix().contains("Y") && s.getPrefix().contains("T") && yt.getValue()))
                list.add(s.getPrefix() + name + ":vanish");
        }
        return list;
    }


}
