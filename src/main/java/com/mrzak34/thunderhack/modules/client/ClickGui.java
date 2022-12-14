package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.classic.ClassicGui;
import com.mrzak34.thunderhack.gui.clickui.ClickUI;
import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.clickui.Colors;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;


import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_BLEND;


public class ClickGui
        extends Module {
    private final ResourceLocation logo = new ResourceLocation("textures/logo.png");
    private final ResourceLocation neko = new ResourceLocation("textures/neko.png");
    private final ResourceLocation neko2 = new ResourceLocation("textures/image2.png");
    private final ResourceLocation crushcat = new ResourceLocation("textures/image1.png");
    private final ResourceLocation neko3= new ResourceLocation("textures/image6.png");
    private final ResourceLocation anime = new ResourceLocation("textures/image3.png");



    /////////////////////////////////////




    /////////////////////////////////////

    private static ClickGui INSTANCE = new ClickGui();
    private long fadeinn;
    private long fadeinnn;
    private int n;
    public int i = 85;

    //timer utils
    private final Timer timer = new Timer();

    private Setting<MainModeEn> mainMode = register(new Setting("ClickGuiMode", MainModeEn.New));

    private Setting<colorModeEn> colorMode = register(new Setting("ColorMode", colorModeEn.Analogous));
    public final Setting<ColorSetting> hcolor1 = this.register(new Setting<>("MainColor", new ColorSetting(6451313)));
    public Setting<Integer> colorSpeed = this.register(new Setting<Integer>("ColorSpeed", 18, 2, 54));
    public final Setting<ColorSetting> acolor = this.register(new Setting<>("MainColor2", new ColorSetting(6451313)));


    public Setting<Boolean> showBinds = this.register(new Setting<Boolean>("ShowBinds", true));

    public final Setting<ColorSetting> mainColor3 = this.register(new Setting<>("Main Color3", new ColorSetting(1227456905)));
    public final Setting<ColorSetting> mainColor2 = this.register(new Setting<>("Main Color2", new ColorSetting(825110833)));
    public final Setting<ColorSetting> slidercolor = this.register(new Setting<>("slider color", new ColorSetting(1227338229)));
    public final Setting<ColorSetting> gcolor1 = this.register(new Setting<>("gcolor1", new ColorSetting(-8660776,true)));
    public final Setting<ColorSetting> gcolor2 = this.register(new Setting<>("gcolor2", new ColorSetting(-13535104)));
    public final Setting<ColorSetting> mainColor = this.register(new Setting<>("Main Color", new ColorSetting(3649978)));
    public final Setting<ColorSetting> topColor = this.register(new Setting<>("Cat Color", new ColorSetting(-115042915)));
    public final Setting<ColorSetting> downColor = this.register(new Setting<>(" Down Color", new ColorSetting(-114219739)));


    public final Setting<ColorSetting> imagecc = this.register(new Setting<>("ImageColorCorr", new ColorSetting(-705960218)));
    public Setting<Boolean> darkBackGround = this.register(new Setting<Boolean>("Background", false));
    public boolean image = true;
    public Setting<Integer> imageScaleX = this.register(new Setting<Integer>("ImageScaleX", 425, 0, 1023));
    public Setting<Integer> imageScaleY = this.register(new Setting<Integer>("ImageScaleY", 425, 0, 1023));
    public Setting<Integer> hoverAlpha = this.register(new Setting<Integer>("imagey", 103, 0, 255));
    public Setting<Integer> imagex = this.register(new Setting<Integer>("imagex", 1023, 0, 1023));
    public Setting<Integer> imagey = this.register(new Setting<Integer>("imagey", 512, 0, 1023));
    public Setting<Integer> fadeintimeout = this.register(new Setting<Integer>("FadeInTimeout", 512, 0, 2048));
    public Setting<Float> fadeintimespeed = this.register(new Setting<Float>("FadeInSpeed", 0.5f, 0.1f, 5.0f));



    public String[] myString;
    private Setting<Moderator> shader = register(new Setting("shader", Moderator.none));

    private Setting<ClickGui.mode> picture = register(new Setting("image", mode.Zamorozka));
    public ClickGui() {
        super("ClickGui", "старый кликгуи", Module.Category.CLIENT, true, false, false);
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
          myString = new String[]{"image1","image2","image3","image5","image6","logo","neko"};
        n = (int)Math.floor(Math.random() * myString.length);

        if(mainMode.getValue() == MainModeEn.New) {
            Util.mc.displayGuiScreen(ClickUI.getClickGui());
        } else {
            Util.mc.displayGuiScreen(ClassicGui.getClickGui());
        }

        timer.reset();

    }
    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {

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



    ResourceLocation customImg;
    ResourceLocation customImg2;
    ResourceLocation customImg3;


    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        double psx = imagex.getValue();
        double psy = imagey.getValue();
        float xOffset = (float) psx + 10;
        float yOffset = (float) psy;
        float mouseposx = (float) Mouse.getX() / 100;
        float mouseposy = (float) Mouse.getY() / 100;


        fadeinn = (long) (timer.getPassedTimeMs() / fadeintimespeed.getValue());

        if(fadeinn < fadeintimeout.getValue()) {fadeinnn = fadeinn;}
        Gui.drawRect(400, 400, 400, 400, imagecc.getValue().getRawColor());


        if (this.picture.getValue() == mode.Neko ) {
            Util.mc.getTextureManager().bindTexture(this.neko);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.Logo ) {
            Util.mc.getTextureManager().bindTexture(this.logo);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());

        }
        if (this.picture.getValue() == mode.Custom) {
           if(customImg == null) {
               if (PNGtoResourceLocation.getCustomImg("img1", "png") != null) {
                   customImg = PNGtoResourceLocation.getCustomImg("img1", "png");
               }
               return;
           }
           Util.mc.getTextureManager().bindTexture(this.customImg);
           drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.Custom2) {
            if(customImg2 == null) {
                if (PNGtoResourceLocation.getCustomImg("img2", "png") != null) {
                    customImg2 = PNGtoResourceLocation.getCustomImg("img2", "png");
                }
                return;
            }
            Util.mc.getTextureManager().bindTexture(this.customImg2);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.Custom3) {
            if(customImg3 == null) {
                if (PNGtoResourceLocation.getCustomImg("img3", "png") != null) {
                    customImg3 = PNGtoResourceLocation.getCustomImg("img3", "png");
                }
                return;
            }
            Util.mc.getTextureManager().bindTexture(this.customImg3);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.CrushCattyna ) {
            Util.mc.getTextureManager().bindTexture(this.crushcat);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.Neko3) {
            Util.mc.getTextureManager().bindTexture(this.neko3);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }

        if (this.picture.getValue() == mode.Neko2 ) {
            Util.mc.getTextureManager().bindTexture(this.neko2);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.Anime ) {
            Util.mc.getTextureManager().bindTexture(this.anime);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.None ) {
            return;
        }
        if(picture.getValue() == mode.Random  && myString != null && n != 0) {
            ResourceLocation rand = new ResourceLocation("textures/" + myString[n] + ".png");
            Util.mc.getTextureManager().bindTexture(rand);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
    }





    @Override
    public void onDisable() {
        timer.reset();
        if (ClickGui.mc.entityRenderer.getShaderGroup() != null) {
            ClickGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }


    @Override
    public void onLoad() {
        mainColor.getValue().getColorObject();
        Thunderhack.commandManager.setPrefix(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof ClassicGui)) {
            this.disable();
            timer.reset();
        }
    }
    public static void drawCompleteImage(float posX, float posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public enum mode {
        CrushCattyna , Logo, Neko, Neko2, Anime,  Neko3, Zamorozka,  Random, Custom,Custom2,Custom3 , None
    }

    public enum MainModeEn {
        New,
        Classic
    }
    public enum colorModeEn {
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

