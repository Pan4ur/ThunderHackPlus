package com.mrzak34.thunderhack.gui.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.font.FontRendererWrapper;
import com.mrzak34.thunderhack.modules.misc.MiddleClick;
import com.mrzak34.thunderhack.modules.render.ImageESP;
import com.mrzak34.thunderhack.util.GuiRenderHelper;
import com.mrzak34.thunderhack.util.RectHelper;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GuiMiddleClickMenu extends GuiScreen {
    private final EntityPlayer player;
    private final Timer timer = new Timer();

    public double posX;
    public double posY;




    public GuiMiddleClickMenu(EntityPlayer player) {
        this.player = player;
        timer.reset();
    }
    private Vector3d project2D(Float scaleFactor, double x, double y, double z) {
        float xPos = (float) x;
        float yPos = (float) y;
        float zPos = (float) z;
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject(xPos, yPos, zPos, modelview, projection, viewport, vector))
            return new Vector3d((vector.get(0) / scaleFactor), ((Display.getHeight() - vector.get(1)) / scaleFactor), vector.get(2));
        return null;
    }


   // @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Float scaleFactor = MiddleClick.getInstance().scalefactor.getValue();
        double scaling = scaleFactor / Math.pow(scaleFactor, 2);
        GlStateManager.scale(scaling, scaling, scaling);
        Color c = new Color(255, 255, 255);
        int color = 0;
        color = c.getRGB();
        float scale = 1;
                EntityPlayer entityPlayer = (EntityPlayer) player;
                if(entityPlayer != ImageESP.mc.player) {
                    double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.getRenderPartialTicks();
                    double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.getRenderPartialTicks();
                    double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.getRenderPartialTicks();
                    AxisAlignedBB axisAlignedBB2 = player.getEntityBoundingBox();
                    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(axisAlignedBB2.minX - player.posX + x - 0.05, axisAlignedBB2.minY - player.posY + y, axisAlignedBB2.minZ - player.posZ + z - 0.05, axisAlignedBB2.maxX - player.posX + x + 0.05, axisAlignedBB2.maxY - player.posY + y + 0.15, axisAlignedBB2.maxZ - player.posZ + z + 0.05);
                    Vector3d[] vectors = new Vector3d[]{new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
                    mc.entityRenderer.setupCameraTransform(partialTicks, 0);

                    Vector4d position = null;
                    for (Vector3d vector : vectors) {
                        vector = project2D(scaleFactor, vector.x - mc.getRenderManager().renderPosX, vector.y - mc.getRenderManager().renderPosY, vector.z - mc.getRenderManager().renderPosZ);
                        if (vector != null && vector.z > 0 && vector.z < 1) {
                            if (position == null)
                                position = new Vector4d(vector.x, vector.y, vector.z, 0);
                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    if (position != null) {


                        mc.entityRenderer.setupOverlayRendering();
                        posX = position.x ;
                        posY = position.y ;
                        double endPosX = position.z;
                        double endPosY = position.w;


                        RectHelper.drawRect(posX - 1F, posY, posX + 0.5, endPosY + 0.5, black);
                        RectHelper.drawRect(posX - 1F, posY - 0.5, endPosX + 0.5, posY + 0.5 + 0.5, black);
                        RectHelper.drawRect(endPosX - 0.5 - 0.5, posY, endPosX + 0.5, endPosY + 0.5, black);
                        RectHelper.drawRect(posX - 1, endPosY - 0.5 - 0.5, endPosX + 0.5, endPosY + 0.5, black);
                        RectHelper.drawRect(posX - 0.5, posY, posX + 0.5 - 0.5, endPosY, color);
                        RectHelper.drawRect(posX, endPosY - 0.5, endPosX, endPosY, color);
                        RectHelper.drawRect(posX - 0.5, posY, endPosX, posY + 0.5, color);RectHelper.drawRect(endPosX - 0.5, posY, endPosX, endPosY, color);

                        RectHelper.drawRect(posX, posY, posX, posY, color);

                    }


        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        mc.entityRenderer.setupOverlayRendering();

        super.drawScreen(mouseX, mouseY, partialTicks);

        boolean friended = Thunderhack.friendManager.isFriend(player.getName());
      //  boolean partied = PartyCommand.party.contains(player.getName());

        boolean practice = false;

        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            if (mc.getCurrentServerData().serverIP.contains("pvp")) {
                practice = true;
            }
        }

        float width = Math.max(FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend"), FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()));

        float height = 8F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend") + FontRendererWrapper.getStringHeight(MiddleClick.getInstance().commandtext.getValue());

        if (practice) {
            height += 4F + FontRendererWrapper.getStringHeight("Duel");
        }






        GuiRenderHelper.drawRect((float) posX - width/2F - 2F, (float) posY - 2F, width + 4F, height, 0x80000000);
        GuiRenderHelper.drawOutlineRect((float) posX - width/2F - 2F, (float) posY - 2F, width + 4F, height,1F, 0xD0000000);

        FontRendererWrapper.drawString(friended ? "Unfriend" : "Friend", (float) posX - FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend") / 2F, (float) posY, -1);
        FontRendererWrapper.drawString(MiddleClick.getInstance().commandtext.getValue(), (float) posX - FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()) / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend"), -1);
        if (practice) {
            FontRendererWrapper.drawString("Duel", (float) posX - FontRendererWrapper.getStringWidth("Duel") / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend") + 4F + FontRendererWrapper.getStringHeight(MiddleClick.getInstance().commandtext.getValue()), -1);
        }

        if (timer.passedMs(5000)) {
            mc.displayGuiScreen(null);
        }
    }




   // @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);


        // System.out.println(screenPos.x +" " +screenPos.y);

        boolean friended = Thunderhack.friendManager.isFriend(player.getName());
        //  boolean partied = PartyCommand.party.contains(player.getName());

        boolean practice = false;

        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            if (mc.getCurrentServerData().serverIP.contains("pvp")) {
                practice = true;
            }
        }

        if (mouseButton == 0) {
            if (mouseWithinBounds(mouseX, mouseY, (float) posX - FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend") / 2F, (float) posY, FontRendererWrapper.getStringWidth(friended ? "Unfriend" : "Friend"), FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend"))) {
                if (friended) {
                    Thunderhack.friendManager.removeFriend(player.getName());
                } else {
                    Thunderhack.friendManager.addFriend(player.getName());
                }
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mc.displayGuiScreen(null);
            } else if (mouseWithinBounds(mouseX, mouseY, (float) posX - FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()) / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend"), FontRendererWrapper.getStringWidth(MiddleClick.getInstance().commandtext.getValue()), FontRendererWrapper.getStringHeight( MiddleClick.getInstance().commandtext.getValue()))) {
              //  if (partied) {
               //     PartyCommand.party.remove(player.getName());
               // } else {
               //     PartyCommand.party.add(player.getName());
               // }


                mc.player.sendChatMessage(MiddleClick.getInstance().commandname.getValue() + " " + player.getName() );

                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mc.displayGuiScreen(null);
            } else if (practice && mouseWithinBounds(mouseX, mouseY, (float) posX - FontRendererWrapper.getStringWidth( MiddleClick.getInstance().commandtext.getValue()) / 2F, (float) posY + 4F + FontRendererWrapper.getStringHeight(friended ? "Unfriend" : "Friend") + 4F + FontRendererWrapper.getStringHeight( MiddleClick.getInstance().commandtext.getValue()), FontRendererWrapper.getStringWidth("Duel"), FontRendererWrapper.getStringHeight("Duel"))) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mc.displayGuiScreen(null);
                mc.player.connection.sendPacket(new CPacketChatMessage("/duel " + player.getName()));
            }
        }
    }

    public static boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }

































    // КОСТЫЛЬ+++++++++++++++++++++++++++++++++++++++++++++++++





    private final int black = Color.BLACK.getRGB();

    private boolean isValid(Entity entity) {
        return entity instanceof EntityPlayer;
    }













}