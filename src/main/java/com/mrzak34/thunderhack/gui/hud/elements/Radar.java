package com.mrzak34.thunderhack.gui.hud.elements;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.modules.player.NoClip;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Radar extends HudElement {
    public Radar() {
        super("Radar", "классический 2д-радар", "classic 2d-radar",100,100);
    }

    private final Setting<Integer> size = register(new Setting<>("Size", 80, 20, 300));

    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("PlayerColor", new ColorSetting(0xC59B9B9B)));
    public Setting<Boolean> jew = register(new Setting<>("Jew", false));
    public Setting<Boolean> ljew = register(new Setting<>("LaaaargeJew", false));


    private CopyOnWriteArrayList<EntityPlayer> players = new CopyOnWriteArrayList<>();

    @Override
    public void onUpdate() {
        players.clear();
        players.addAll(mc.world.playerEntities);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        super.onRender2D(e);

        double psx = getPosX();
        double psy = getPosY();
        int sizeRect = size.getValue();
        float xOffset = (float)  psx;
        float yOffset = (float) psy;
        double playerPosX = mc.player.posX;
        double playerPosZ = mc.player.posZ;


        RenderUtil.drawBlurredShadow(xOffset, getPosY(), sizeRect, sizeRect, 20, shadowColor.getValue().getColorObject());
        RoundedShader.drawRound(xOffset, getPosY(), sizeRect, sizeRect, 7f, color2.getValue().getColorObject());



        RenderUtil.drawRect(
                xOffset + (sizeRect / 2F - 0.5),
                yOffset + 3.5,
                xOffset + (sizeRect / 2F + 0.2),
                (yOffset + sizeRect) - 3.5,
                PaletteHelper.getColor(155, 100));

        RenderUtil.drawRect(
                xOffset + 3.5,
                yOffset + (sizeRect / 2F - 0.2),
                (xOffset + sizeRect) - 3.5,
                yOffset + (sizeRect / 2F + 0.5),
                PaletteHelper.getColor(155, 100));


        for (EntityPlayer entityPlayer : players) {
            if (entityPlayer == mc.player)
                continue;

            float partialTicks = mc.getRenderPartialTicks();
            float posX = (float) (entityPlayer.posX + (entityPlayer.posX - entityPlayer.lastTickPosX) * partialTicks - playerPosX) * 2;
            float posZ = (float) (entityPlayer.posZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * partialTicks - playerPosZ) * 2;
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
            if(jew.getValue()){
                if(!ljew.getValue()){
                    FontRender.drawIconF("y",(xOffset + sizeRect / 2F + rotX) - 2, (yOffset + sizeRect / 2F + rotY) - 2,color3.getValue().getColor());
                } else
                    FontRender.drawMidIcon("y",(xOffset + sizeRect / 2F + rotX) - 4, (yOffset + sizeRect / 2F + rotY) - 4,color3.getValue().getColor());
            }else {
                RoundedShader.drawRound((xOffset + sizeRect / 2F + rotX) - 2, (yOffset + sizeRect / 2F + rotY) - 2, 4, 4, 4f, color3.getValue().getColorObject());
            }
        }
    }

}
