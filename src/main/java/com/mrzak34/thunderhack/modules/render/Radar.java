package com.mrzak34.thunderhack.modules.render;


import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.RectHelper;
import com.mrzak34.thunderhack.event.events.Render2DEvent;

import java.awt.*;

public class Radar extends Module {

    public Radar() {
        super("SkeetRadar", "RADAROK", Category.HUD, true, false, false);
    }
    public Setting<Integer> posx = this.register ( new Setting <> ( "PosX", 860, 0, 900 ) );
    public Setting <Integer> posy = this.register ( new Setting <> ( "PosY", 15, 100, 350 ) );
    public Setting <Integer> size = this.register ( new Setting <> ( "Size", 100, 30, 300 ) );
    public int scale;



    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        double psx = posx.getValue();
        double psy = posy.getValue();
        ScaledResolution sr = new ScaledResolution(mc);
        scale = 2;
        int sizeRect = (int) size.getValue();
        float xOffset = (float) (sr.getScaledWidth() - sizeRect - psx);
        float yOffset = (float) psy;
        double playerPosX = mc.player.posX;
        double playerPosZ = mc.player.posZ;
        RectHelper.drawBorderedRect(xOffset + 2.5F, yOffset + 2.5F, (xOffset + sizeRect) - 2.5F, (yOffset + sizeRect) - 2.5F, 0.5F, PaletteHelper.getColor(2), PaletteHelper.getColor(11), false);
        RectHelper.drawBorderedRect(xOffset + 3, yOffset + 3, xOffset + sizeRect - 3, yOffset + sizeRect - 3, 0.2F, PaletteHelper.getColor(2), PaletteHelper.getColor(11), false);


        RectHelper.drawRect(xOffset + (sizeRect / 2F - 0.5), yOffset + 3.5, xOffset + (sizeRect / 2F + 0.2), (yOffset + sizeRect) - 3.5, PaletteHelper.getColor(155, 100));
        RectHelper.drawRect(xOffset + 3.5, yOffset + (sizeRect / 2F - 0.2), (xOffset + sizeRect) - 3.5, yOffset + (sizeRect / 2F + 0.5), PaletteHelper.getColor(155, 100));



        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer == mc.player)
                continue;

            float partialTicks = mc.timer.renderPartialTicks;
            float posX = (float) (entityPlayer.posX + (entityPlayer.posX - entityPlayer.lastTickPosX) * partialTicks - playerPosX) * 2;
            float posZ = (float) (entityPlayer.posZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * partialTicks - playerPosZ) * 2;
            int color = (mc.player.canEntityBeSeen(entityPlayer) ? new Color(255, 0, 0).getRGB() : new Color(255, 255, 0).getRGB());
            float cos = (float) Math.cos(mc.player.rotationYaw * 0.017453292);
            float sin = (float) Math.sin(mc.player.rotationYaw * 0.017453292);
            float rotY = -(posZ * cos - posX * sin);
            float rotX = -(posX * cos + posZ * sin);
            if (rotY > sizeRect / 2F - 6) {
                rotY = sizeRect / 2F - 6;
            } else if (rotY < -(sizeRect / 2F - 8)) {
                rotY = -(sizeRect / 2F - 8);
            }
            if (rotX > sizeRect / 2F - 5) {
                rotX = sizeRect / 2F - 5;
            } else if (rotX < -(sizeRect / 2F - 5)) {
                rotX = -(sizeRect / 2F - 5);
            }
            RectHelper.drawRect((xOffset + sizeRect / 2F + rotX) - 1.5F, (yOffset + sizeRect / 2F + rotY) - 1.5F, (xOffset + sizeRect / 2F + rotX) + 1.5F, (yOffset + sizeRect / 2F + rotY) + 1.5F, color);
        }
    }


}