package com.mrzak34.thunderhack.modules.client;


import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.clickui.ClickUI;
import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.clickui.Colors;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;


public class ClickGui extends Module {

    private static ClickGui INSTANCE = new ClickGui();
    private Setting<colorModeEn> colorMode = register(new Setting("ColorMode", colorModeEn.Static));
    public final Setting<ColorSetting> hcolor1 = this.register(new Setting<>("MainColor", new ColorSetting(-6974059)));
    public final Setting<ColorSetting> acolor = this.register(new Setting<>("MainColor2", new ColorSetting(-8365735)));
    public final Setting<ColorSetting> plateColor = this.register(new Setting<>("PlateColor", new ColorSetting(-14474718)));
    public final Setting<ColorSetting> catColor = this.register(new Setting<>("CategoryColor", new ColorSetting(-15395563)));
    public final Setting<ColorSetting> downColor = this.register(new Setting<>("DownColor", new ColorSetting(-14474461)));
    public Setting<Integer> colorSpeed = this.register(new Setting<Integer>("ColorSpeed", 18, 2, 54));
    public Setting<Boolean> showBinds = this.register(new Setting<>("ShowBinds", true));
    private Setting<Moderator> shader = register(new Setting("shader", Moderator.none));

    public ClickGui() {
        super("ClickGui", "кликгуи", Module.Category.CLIENT);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }


    public Color getColor(int count) {
        int index = (int) (count);
        switch (colorMode.getValue()) {
            case Sky:
                return ColorUtil.skyRainbow((int) colorSpeed.getValue(), index);
            case LightRainbow:
                return ColorUtil.rainbow((int) colorSpeed.getValue(), index, .6f, 1, 1);

            case Rainbow:
                return ColorUtil.rainbow((int) colorSpeed.getValue(), index, 1f, 1, 1);

            case Fade:
                return ColorUtil.fade((int) colorSpeed.getValue(), index, hcolor1.getValue().getColorObject(), 1);

            case DoubleColor:
                return ColorUtil.interpolateColorsBackAndForth((int) colorSpeed.getValue(), index,
                        hcolor1.getValue().getColorObject(), Colors.ALTERNATE_COLOR, true);
            case Analogous:
                int val = 1;
                Color analogous = ColorUtil.getAnalogousColor(acolor.getValue().getColorObject())[val];
                return ColorUtil.interpolateColorsBackAndForth((int) colorSpeed.getValue(), index, hcolor1.getValue().getColorObject(), analogous, true);
            default:
                return hcolor1.getValue().getColorObject();
        }
    }


    @Override
    public void onEnable() {
            Util.mc.displayGuiScreen(ClickUI.getClickGui());
    }


    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if(fullNullCheck()) return;
        if(shader.getValue() != Moderator.none) {

            if (OpenGlHelper.shadersSupported && ClickGui.mc.getRenderViewEntity() instanceof EntityPlayer) {
                if (ClickGui.mc.entityRenderer.getShaderGroup() != null) {
                    ClickGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }
                try {
                    ClickGui.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/" + this.shader.getValue() + ".json"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ClickGui.mc.entityRenderer.getShaderGroup() != null && ClickGui.mc.currentScreen == null) {
                ClickGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {

    }





    @Override
    public void onDisable() {
        if (ClickGui.mc.entityRenderer.getShaderGroup() != null) {
            ClickGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }


    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof ClickUI)) {
            this.disable();
        }
    }

    public enum colorModeEn {
        Static,
        Sky,
        LightRainbow,
        Rainbow,
        Fade,
        DoubleColor,
        Analogous
    }

    public enum Moderator
    {
        none,
        notch,
        antialias,
        art,
        bits,
        blobs,
        blobs2,
        blur,
        bumpy,
        color_convolve,
        creeper,
        deconverge,
        desaturate,
        flip,
        fxaa,
        green,
        invert,
        ntsc,
        pencil,
        phosphor,
        sobel,
        spider,
        wobble;
    }


}

