package com.mrzak34.thunderhack.modules.render;

import com.google.common.collect.Maps;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.Vec3d;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class LogoutSpots extends Module {
    private final Setting<Integer> removeDistance = this.register(new Setting<Integer>("RemoveDistance", 255, 1, 2000));


    public LogoutSpots() {
        super("LogoutSpots", "Puts Armor on for you.", Category.RENDER, true, false, false);
    }

    private final Map<String, EntityPlayer> playerCache = Maps.newConcurrentMap();
    private final Map<String, PlayerData> logoutCache = Maps.newConcurrentMap();



    @Override
    public void onEnable() {
        super.onToggle();
        playerCache.clear();
        logoutCache.clear();
    }



    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){


        try {
            final SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
            if (packet.getEntries().size() <= 1)
                if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                    packet.getEntries().forEach(data -> {
                        if ( data.getProfile().getId().equals((Minecraft.getMinecraft()).player.getGameProfile().getId()) || data.getProfile().getName() != null || data.getProfile().getId().toString() != "b9523a25-2b04-4a75-bee0-b84027824fe0"|| data.getProfile().getId().toString() != "8c8e8e2f-46fc-4ce8-9ac7-46eeabc12ebd") {
                            try {
                                onPlayerJoin(data.getProfile().getId().toString());
                            } catch (Exception e){}


                        }

                    });
                } else if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                    packet.getEntries().forEach(data2 -> {
                        if (!data2.getProfile().getId().equals((Minecraft.getMinecraft()).player.getGameProfile().getId()) || data2.getProfile().getId() == null || data2.getProfile().getId().toString() != "b9523a25-2b04-4a75-bee0-b84027824fe0"|| data2.getProfile().getId().toString() != "8c8e8e2f-46fc-4ce8-9ac7-46eeabc12ebd") {
                            onPlayerLeave(data2.getProfile().getId().toString());
                        }
                    });
                }
        } catch (Exception exception) {

        }





    }



    @Override
    public void onUpdate() {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null)
            return;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == null || player.equals(mc.player))
                continue;

            this.updatePlayerCache(player.getGameProfile().getId().toString(), player);
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();

        for (String uuid : this.logoutCache.keySet()) {
            final PlayerData data = this.logoutCache.get(uuid);

            if (this.isOutOfRange(data)) {
                this.logoutCache.remove(uuid);
                continue;
            }

            data.ghost.prevLimbSwingAmount = 0;
            data.ghost.limbSwing = 0;
            data.ghost.limbSwingAmount = 0;
            data.ghost.hurtTime = 0;

            GlStateManager.pushMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();

            GlStateManager.color(1, 1, 1, 1);
            try {
                mc.getRenderManager().renderEntity(data.ghost, data.position.x - mc.getRenderManager().renderPosX, data.position.y - mc.getRenderManager().renderPosY, data.position.z - mc.getRenderManager().renderPosZ, data.ghost.rotationYaw, mc.getRenderPartialTicks(), false);
            } catch (Exception e){}

            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();

        for (String uuid : this.logoutCache.keySet()) {
            final PlayerData data = this.logoutCache.get(uuid);

            if (this.isOutOfRange(data)) {
                this.logoutCache.remove(uuid);
                continue;
            }

            RenderUtil.drawNametag2(data.profile.getName() + " just logout at " + data.position.x + " " + data.position.y + " " + data.position.z, new AxisAlignedBB(data.position.x - 0.3f, data.position.y, data.position.z - 0.3f, data.position.x + 0.3f, data.position.y + 1.9f, data.position.z + 0.3f), 1f, new Color(0xCF05FAB0, true).getRGB(), false);
        }
    }


    public void onPlayerLeave(String uuid2){
        final Minecraft mc = Minecraft.getMinecraft();

        for (String uuid : this.playerCache.keySet()) {
            if (!uuid.equals(uuid2)) // not matching uuid
                continue;

            final EntityPlayer player = this.playerCache.get(uuid);

            //final Vec3d interp = MathUtil.interpolateEntity(player, mc.getRenderPartialTicks());
            final PlayerData data = new PlayerData(player.getPositionVector(), player.getGameProfile(), player);

            if (!this.hasPlayerLogged(uuid)) {
                this.logoutCache.put(uuid, data);
            }
        }

        this.playerCache.clear();
    }


    public void onPlayerJoin(String uuid3) {
        final Minecraft mc = Minecraft.getMinecraft();

        for (String uuid : this.logoutCache.keySet()) {
            if (!uuid.equals(uuid3)) // not matching uuid
                continue;

            this.logoutCache.remove(uuid);
        }

        this.playerCache.clear();
    }

    private void cleanLogoutCache(String uuid) {
        this.logoutCache.remove(uuid);
    }

    private void updatePlayerCache(String uuid, EntityPlayer player) {
        this.playerCache.put(uuid, player);
    }

    private boolean hasPlayerLogged(String uuid) {
        return this.logoutCache.containsKey(uuid);
    }

    private boolean isOutOfRange(PlayerData data) {
        try {
            Vec3d position = data.position;
            return Minecraft.getMinecraft().player.getDistance(position.x, position.y, position.z) > this.removeDistance.getValue();
        } catch (Exception e){};
        return true;
    }

    public Map<String, EntityPlayer> getPlayerCache() {
        return playerCache;
    }

    public Map<String, PlayerData> getLogoutCache() {
        return logoutCache;
    }

    private class PlayerData {
        Vec3d position;
        GameProfile profile;
        EntityPlayer ghost;

        public PlayerData(Vec3d position, GameProfile profile, EntityPlayer ghost) {
            this.position = position;
            this.profile = profile;
            this.ghost = ghost;
        }
    }





    private final Map<String, String> uuidNameCache = Maps.newConcurrentMap();

    public String resolveName(String uuid) {
        uuid = uuid.replace("-", "");
        if (uuidNameCache.containsKey(uuid)) {
            return uuidNameCache.get(uuid);
        }

        final String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        try {
            final String nameJson = IOUtils.toString(new URL(url));
            if (nameJson != null && nameJson.length() > 0) {
                final JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(nameJson);
                if (jsonArray != null) {
                    final JSONObject latestName = (JSONObject) jsonArray.get(jsonArray.size() - 1);
                    if (latestName != null) {
                        return latestName.get("name").toString();
                    }
                }
            }
        } catch (IOException | ParseException e) {
            //e.printStackTrace();

        }

        return null;
    }
}

