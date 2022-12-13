package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.event.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class ThunderHackGui extends Module {
    public ThunderHackGui() {
        super("ThunderGui", "новый клик гуи", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }


    public final Setting<ColorSetting> buttsColor = this.register(new Setting<>("ButtonsColor", new ColorSetting(-955051502)));
    public final Setting<ColorSetting> catcolorinmodule = this.register(new Setting<>("CatColor", new ColorSetting(-162567959)));


    public final Setting<ColorSetting> LeftsideColor = this.register(new Setting<>("Leftside Color", new ColorSetting(-1390009051)));
    public final Setting<ColorSetting> thplate = this.register(new Setting<>("thplate Color", new ColorSetting(-652291704)));

    public Setting<Integer> blurstr = this.register(new Setting<Integer>("blurstr", 100, 0, 100));




    public enum mode {
        CrushCattyna , Logo, Neko, Neko2, Anime,  Neko3, Zamorozka,  Random, None,Custom,Custom2,Custom3;
    }

    private Setting<mode> picture = register(new Setting("image", mode.Zamorozka));
    public int i = 85;
    private final Timer timer = new Timer();
    private final ResourceLocation logo = new ResourceLocation("textures/logo.png");
    private final ResourceLocation neko = new ResourceLocation("textures/neko.png");
    private final ResourceLocation neko2 = new ResourceLocation("textures/image2.png");
    private final ResourceLocation crushcat = new ResourceLocation("textures/image1.png");
    private final ResourceLocation zamorozka = new ResourceLocation("textures/girl2.png");
    private final ResourceLocation neko3= new ResourceLocation("textures/image6.png");
    private final ResourceLocation anime = new ResourceLocation("textures/image3.png");

    public boolean image = true;
    public Setting<Integer> imageScaleX = this.register(new Setting<Integer>("ImageScaleX", 512, 0, 1023));
    public Setting<Integer> imageScaleY = this.register(new Setting<Integer>("ImageScaleY", 512, 0, 1023));
    public final Setting<ColorSetting> imagecc = this.register(new Setting<>("ImageColorCorr", new ColorSetting(449170629)));

    public Setting<Integer> imagex = this.register(new Setting<Integer>("imagex", 1011, 0, 1500));
    public Setting<Integer> imagey = this.register(new Setting<Integer>("imagey", 90, 0, 1023));
    public Setting<Integer> fadeintimeout = this.register(new Setting<Integer>("FadeInTimeout", 500, 0, 2048));
    public Setting<Float> fadeintimespeed = this.register(new Setting<Float>("FadeInSpeed", Float.valueOf(0.3f), Float.valueOf(0.1f), Float.valueOf(5.0f)));


    private long fadeinn;
    private long fadeinnn;
    public String[] myString;

    ResourceLocation customImg;
    ResourceLocation customImg2;
    ResourceLocation customImg3;

    @Override
    public void onDisable() {
        timer.reset();
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        double psx = imagex.getValue();
        double psy = imagey.getValue();
        float xOffset = (float) psx + 10;
        float yOffset = (float) psy;
        float mouseposx = (float) Mouse.getX() / 100;
        float mouseposy = (float) Mouse.getY() / 100;

        fadeinn = (long) (timer.getPassedTimeMs() / fadeintimespeed.getValue());

        if(fadeinn < fadeintimeout.getValue()) {fadeinnn = fadeinn;}


        Gui.drawRect(400, 400, 400, 400, imagecc.getValue().getRawColor());

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

        if (this.picture.getValue() == mode.Neko ) {
            Util.mc.getTextureManager().bindTexture(this.neko);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.Logo ) {
            Util.mc.getTextureManager().bindTexture(this.logo);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());

        }
        // if (this.picture.getValue() == mode.Custom && image.getValue()) {
        //    Util.mc.getTextureManager().bindTexture(this.custom);
        //    drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
//
        // }

        if (this.picture.getValue() == mode.CrushCattyna ) {
            Util.mc.getTextureManager().bindTexture(this.crushcat);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }
        if (this.picture.getValue() == mode.Zamorozka ) {
            Util.mc.getTextureManager().bindTexture(this.zamorozka);
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
        }

        if(picture.getValue() == mode.Random  && myString != null && n != 0) {
            ResourceLocation rand = new ResourceLocation("textures/" + myString[n] + ".png");
            Util.mc.getTextureManager().bindTexture(rand);
            drawCompleteImage(xOffset - 1.0f + mouseposx - fadeinnn, yOffset - 1.0f - mouseposy, imageScaleX.getValue(), imageScaleY.getValue());
        }



    }


    int n = 0;
    private void setInstance() {
        INSTANCE = this;
    }
    private static ThunderHackGui INSTANCE = new ThunderHackGui();
    @Override
    public void onEnable() {
        myString = new String[]{"image1","image2","image3","image5","image6","logo","neko"};
        n = (int)Math.floor(Math.random() * myString.length);
        timer.reset();
        Util.mc.displayGuiScreen(ThunderGui.getThunderGui());
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
    public static ThunderHackGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThunderHackGui();
        }
        return INSTANCE;
    }

}
