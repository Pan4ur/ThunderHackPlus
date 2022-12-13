package com.mrzak34.thunderhack.gui.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.misc.MiddleClick;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.RenderHelper;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.io.IOException;


public class NewGuiMiddleClickMenu extends GuiScreen {
    private final EntityPlayer player;
    private final Timer timer = new Timer();



    public NewGuiMiddleClickMenu(EntityPlayer player) {
        this.player = player;
        timer.reset();
    }

    int x1,y1;
    int selectedItem;

    // @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();

        ScaledResolution sr = new ScaledResolution(mc);

        drawPlayerOnScreen((int) sr.getScaledWidth()/2, (int) ((int) sr.getScaledHeight()/ 2) + 50, 45, -30, 0, player, true, true);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
       // drawSlice(buffer,sr.getScaledWidth(),sr.getScaledHeight(),1,MiddleClick.getInstance().circus.getValue(),MiddleClick.getInstance().circus.getValue() * 2,);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < 3; i++) {
            float s = i*120;
            float e = (i*120) + 120;



            if (selectedItem == i) {
                drawSlice(
                        buffer,sr.getScaledWidth()/2,sr.getScaledHeight()/2,1,MiddleClick.getInstance().circus.getValue(),MiddleClick.getInstance().circus.getValue() * 2, s, e, 255, 255, 255, 64
                );              //  hasMouseOver = true;
            }
            else
                drawSlice(
                        buffer,sr.getScaledWidth()/2,sr.getScaledHeight()/2,1,MiddleClick.getInstance().circus.getValue(),MiddleClick.getInstance().circus.getValue() * 2, s, e, 0, 0, 0, 64
                );
        }
        tessellator.draw();

        GlStateManager.popMatrix();


        if(mouseX <= sr.getScaledWidth()/2 && mouseY <= sr.getScaledHeight() * 0.75){
            selectedItem = 1;
        }
        if(mouseX >= sr.getScaledWidth()/2 && mouseY <= sr.getScaledHeight()/2){
            selectedItem = 2;
        }
        if(mouseX >= sr.getScaledWidth()/2 && mouseY >= sr.getScaledHeight()/2){
            selectedItem = 0;
        }


        RenderHelper.drawCircle(sr.getScaledWidth()/2,sr.getScaledHeight()/2,MiddleClick.getInstance().circus.getValue() * 2,false, PaletteHelper.astolfo(false, 1));

        Util.fr.drawStringWithShadow(Thunderhack.friendManager.isFriend(player.getName()) ? "Del Friend" : "Add Friend",sr.getScaledWidth()/2 - 100,sr.getScaledHeight()/2 - 8, PaletteHelper.astolfo(false, 1).getRGB());
        Util.fr.drawStringWithShadow(Thunderhack.enemyManager.isEnemy(player.getName()) ? "Del Enemy" : "Add Enemy",sr.getScaledWidth()/2 + 30,sr.getScaledHeight()/2 + 55, PaletteHelper.astolfo(false, 1).getRGB());
        Util.fr.drawStringWithShadow(MiddleClick.getInstance().commandtext.getValue(),sr.getScaledWidth()/2 + 30,sr.getScaledHeight()/2 - 60, PaletteHelper.astolfo(false, 1).getRGB());

        Util.fr.drawStringWithShadow(player.getName(), sr.getScaledWidth()/2 - 15,sr.getScaledHeight()/2 - 40,-1);


    }

//ban kick custom friend duel enemy clearinent




    // @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean friended = Thunderhack.friendManager.isFriend(player.getName());
        boolean practice = false;

        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            if (mc.getCurrentServerData().serverIP.contains("pvp")) {
                practice = true;
            }
        }

        if (mouseButton == 0) {
            if(selectedItem == 0 ){
                if(Thunderhack.enemyManager.isEnemy(player.getName())){
                    Thunderhack.enemyManager.removeEnemy(player.getName());
                    Command.sendMessage("Враг " + player.getName() + " удален!");
                } else {
                    Thunderhack.enemyManager.addEnemy(player.getName());
                    Command.sendMessage("Враг " + player.getName() + " добавлен!");
                }
            }
            if(selectedItem == 1 ){
                if(Thunderhack.friendManager.isFriend(player.getName())){
                    Thunderhack.friendManager.removeFriend(player.getName());
                    Command.sendMessage("Друг " + player.getName() + " удален!");
                } else {
                    Thunderhack.friendManager.addFriend(player.getName());
                    Command.sendMessage("Друг " + player.getName() + " добавлен!");
                    if(MiddleClick.getInstance().fm.getValue()){
                        mc.player.sendChatMessage("/w " + player.getName() + " I friended u at Thunderhack plus!" );
                    }
                }
            }
            if(selectedItem == 2 ){
                mc.player.sendChatMessage(MiddleClick.getInstance().commandname.getValue() + " " + player.getName() );
            }
        }
    }

    public static void drawPlayerOnScreen(int x, int y, int scale, float mouseX, float mouseY, EntityPlayer player, boolean yaw, boolean pitch) {
        //  ESP.hackyFix = true;
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
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
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
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
        // ESP.hackyFix = false;

    }

    private static final float PRECISION = 5.0f;

    public static boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }


    private void drawSlice(
            BufferBuilder buffer,
            float x,
            float y,
            float z,
            float radiusIn,
            float radiusOut,
            float startAngle,
            float endAngle,
            int r,
            int g,
            int b,
            int a
    ) {
        float angle = endAngle - startAngle;
        int sections = Math.max(1, MathHelper.ceil(angle / PRECISION));

        startAngle = (float) Math.toRadians(startAngle);
        endAngle = (float) Math.toRadians(endAngle);
        angle = endAngle - startAngle;

        for (int i = 0; i < sections; i++)
        {
            float angle1 = startAngle + (i / (float) sections) * angle;
            float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

            float pos1InX = x + radiusIn * (float) Math.cos(angle1);
            float pos1InY = y + radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
            float pos2InX = x + radiusIn * (float) Math.cos(angle2);
            float pos2InY = y + radiusIn * (float) Math.sin(angle2);

            buffer.pos(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
            buffer.pos(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
            buffer.pos(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
            buffer.pos(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
        }
    }


}




