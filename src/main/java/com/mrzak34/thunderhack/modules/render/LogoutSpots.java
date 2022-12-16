package com.mrzak34.thunderhack.modules.render;

import com.google.common.collect.Maps;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
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
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static com.mrzak34.thunderhack.gui.font.FontRendererWrapper.getFontHeight;
import static com.mrzak34.thunderhack.util.RenderUtil.drawRect;
import static com.mrzak34.thunderhack.util.RenderUtil.interpolate;

public class LogoutSpots extends Module {
    private final Setting<Integer> removeDistance = this.register(new Setting<Integer>("RemoveDistance", 255, 1, 2000));


    public LogoutSpots() {
        super("LogoutSpots", "Puts Armor on for you.", Category.RENDER, true, false, false);
    }

    private final Map<String, EntityPlayer> playerCache = Maps.newConcurrentMap();
    private final Map<String, PlayerData> logoutCache = Maps.newConcurrentMap();

    private final Setting<Float> scaling = this.register(new Setting<>("Size", 0.3f, 0.1f, 20.0f));
    private final Setting<Boolean> scaleing = this.register(new Setting<>("Scale", false));
    private final Setting<Float> factor = this.register(new Setting<>("Factor", 0.3f, 0.1f, 1.0f));
    private final Setting<Boolean> smartScale = this.register(new Setting<>("SmartScale", false));

    @Override
    public void onEnable() {
        super.onToggle();
        playerCache.clear();
        logoutCache.clear();
    }



    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){


        try {
            final SPacketPlayerListItem packet = event.getPacket();
            if (packet.getEntries().size() <= 1)
                if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                    packet.getEntries().forEach(data -> {
                        if ( data.getProfile().getId().equals((Minecraft.getMinecraft()).player.getGameProfile().getId()) || data.getProfile().getName() != null || data.getProfile().getId().toString() != "b9523a25-2b04-4a75-bee0-b84027824fe0"|| data.getProfile().getId().toString() != "8c8e8e2f-46fc-4ce8-9ac7-46eeabc12ebd") {
                            try {
                                onPlayerJoin(data.getProfile().getId().toString());
                            } catch (Exception ignored){}


                        }

                    });
                } else if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                    packet.getEntries().forEach(data2 -> {
                        if (!data2.getProfile().getId().equals((Minecraft.getMinecraft()).player.getGameProfile().getId()) || data2.getProfile().getId() == null || data2.getProfile().getId().toString() != "b9523a25-2b04-4a75-bee0-b84027824fe0"|| data2.getProfile().getId().toString() != "8c8e8e2f-46fc-4ce8-9ac7-46eeabc12ebd") {
                            onPlayerLeave(data2.getProfile().getId().toString());
                        }
                    });
                }
        } catch (Exception ignored) {
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

            boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
            boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean depthtest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);

            GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
            GlStateManager.color(1, 1, 1, 1);
            try {
                mc.getRenderManager().renderEntity(data.ghost,
                        data.position.x - mc.getRenderManager().renderPosX,
                        data.position.y - mc.getRenderManager().renderPosY,
                        data.position.z - mc.getRenderManager().renderPosZ,

                        data.ghost.rotationYaw, mc.getRenderPartialTicks(), false);
            } catch (Exception ignored){}

            if(!depthtest)
                GlStateManager.disableDepth();
            if(!lighting)
                GlStateManager.disableLighting();
            if(!blend)
                GlStateManager.disableBlend();

            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onRenderPost(PostRenderEvent event) {
        for (String uuid : this.logoutCache.keySet()) {
            final PlayerData data = this.logoutCache.get(uuid);

            if (this.isOutOfRange(data)) {
                this.logoutCache.remove(uuid);
                continue;
            }
            renderNameTag(data.position.x - mc.getRenderManager().renderPosX,
                          data.position.y - mc.getRenderManager().renderPosY,
                          data.position.z - mc.getRenderManager().renderPosZ,
                    event.getPartialTicks(),data.profile.getName() + " just logout at " + (int) data.position.x + " " + (int) data.position.y + " " + (int) data.position.z);
        }
    }


    private void renderNameTag(final double x,  final double y,  final double z,  final float delta,String displayTag) {
        double tempY = y;
        tempY +=  0.7;
        final Entity camera = NameTags.mc.getRenderViewEntity();
        assert camera != null;
        final double originalPositionX = camera.posX;
        final double originalPositionY = camera.posY;
        final double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX,  camera.posX,  delta);
        camera.posY = interpolate(camera.prevPosY,  camera.posY,  delta);
        camera.posZ = interpolate(camera.prevPosZ,  camera.posZ,  delta);
        final double distance = camera.getDistance(x + NameTags.mc.getRenderManager().viewerPosX,  y + NameTags.mc.getRenderManager().viewerPosY,  z + NameTags.mc.getRenderManager().viewerPosZ);
        final int width = mc.fontRenderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + scaling.getValue() * (distance * factor.getValue())) / 1000.0;
        if (distance <= 8.0 && smartScale.getValue()) {
            scale = 0.0245;
        }
        if (!scaleing.getValue()) {
            scale = scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f,  -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x,  (float)tempY + 1.4f,  (float)z);
        GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY,  0.0f,  1.0f,  0.0f);
        GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX,  (NameTags.mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f,  0.0f,  0.0f);
        GlStateManager.scale(-scale,  -scale,  scale);
        GlStateManager.disableDepth ( );
        GlStateManager.enableBlend ( );

        drawRect((float)(-width - 2),  (float)(-(getFontHeight() + 1)),  width + 2.0f,  1.5f,  1426063360);

        GlStateManager.disableBlend ( );
        mc.fontRenderer.drawStringWithShadow(displayTag,  (float)(-width),  (float)(-(getFontHeight() - 1)),  -1);
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth ( );
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f,  1500000.0f);
        GlStateManager.popMatrix();
    }


    public void onPlayerLeave(String uuid2){
        for (String uuid : playerCache.keySet()) {
            if (!uuid.equals(uuid2)) // not matching uuid
                continue;

            final EntityPlayer player = playerCache.get(uuid);

            //final Vec3d interp = MathUtil.interpolateEntity(player, mc.getRenderPartialTicks());
            final PlayerData data = new PlayerData(player.getPositionVector(), player.getGameProfile(), player);

            if (!hasPlayerLogged(uuid)) {
                logoutCache.put(uuid, data);
            }
        }

        playerCache.clear();
    }


    public void onPlayerJoin(String uuid3) {
        for (String uuid : this.logoutCache.keySet()) {
            if (!uuid.equals(uuid3)) // not matching uuid
                continue;
            Command.sendMessage(playerCache.get(uuid) + " logged back at  X: " + (int) logoutCache.get(uuid).position.x + " Y: " + (int) logoutCache.get(uuid).position.y + " Z: " + (int) logoutCache.get(uuid).position.z);
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
        } catch (Exception ignored){};
        return true;
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

