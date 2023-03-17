package com.mrzak34.thunderhack.gui.hud.elements;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.gui.hud.elements.HudEditorGui;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class Player extends HudElement {
    public Setting<Integer> scale = this.register(new Setting<Integer>("Scale", 50, 0, 200));
    public Setting<Boolean> yw = this.register(new Setting<Boolean>("Yaw", true));
    public Setting<Boolean> pch = this.register(new Setting<Boolean>("Pitch", true));



    public Player() {
        super("PlayerView", "Player", 100,100);
    }

    public static void drawPlayerOnScreen(int x, int y, int scale, float mouseX, float mouseY, EntityPlayer player, boolean yaw, boolean pitch) {
        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.color(1f, 1f, 1f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = player.renderYawOffset;
        float f1 = player.rotationYaw;
        float f2 = player.rotationPitch;
        float f3 = player.prevRotationYawHead;
        float f4 = player.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        mouseX = yaw ? player.rotationYaw * -1 : mouseX;
        mouseY = pitch ? player.rotationPitch * -1 : mouseY;
        GlStateManager.rotate(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        if (!yaw) {
            player.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
            player.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
            player.rotationYawHead = player.rotationYaw;
            player.prevRotationYawHead = player.rotationYaw;
        }
        if (!pitch) {
            player.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        }
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        if (!yaw) {
            player.renderYawOffset = f;
            player.rotationYaw = f1;
            player.prevRotationYawHead = f3;
            player.rotationYawHead = f4;
        }
        if (!pitch) {
            player.rotationPitch = f2;
        }
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        drawPlayerOnScreen((int) getPosX(), (int) getPosY(), scale.getValue(), -30, 0, Minecraft.getMinecraft().player, yw.getValue(), pch.getValue());
    }


}
