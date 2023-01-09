package com.mrzak34.thunderhack.gui.thundergui;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.thundergui.components.items.buttons.TConfigComponent;
import com.mrzak34.thunderhack.gui.thundergui.components.items.buttons.TFriendComponent;
import com.mrzak34.thunderhack.gui.thundergui.components.items.buttons.TModuleButt;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.DiscordWebhook;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.modules.misc.NameProtect;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.manager.FriendManager;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mrzak34.thunderhack.util.Util.mc;

public class ThunderGui extends GuiScreen {


    //TODO ПЕРЕМЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕННЫЕ
    public static int thunderguiX = 150;
    public static int thunderguiY = 100;
    public static int thunderguiscaleX = 500;
    public static int thunderguiscaleY = 300;
    public final ArrayList<TModuleButt> components = new ArrayList();
    public final ArrayList<TFriendComponent> friendslist = new ArrayList();
    public final ArrayList<TConfigComponent> configlist = new ArrayList();
    boolean friendlistening = false;
    boolean configListening = false;
    int anim = 0;
    int anim2 = 0;
    static Categories currentCategory = Categories.COMBAT;
    boolean dragging = false;
    int dragX = 0;
    int i = 0;
    Timer timer = new Timer();
    int dragY = 0;
    float coolfade = 0f;
    Color oldcolor = new Color(getCatColor().getRGB());
    String addConfigLine = "";
    String addFriendLine = "";
    public int totalwheel = 0;
    ResourceLocation head;
    ResourceLocation combaticon = new ResourceLocation("textures/combaticon.png");
    ResourceLocation miscicon = new ResourceLocation("textures/miscicon.png");
    ResourceLocation movementicon = new ResourceLocation("textures/movementicon.png");
    ResourceLocation clienticon = new ResourceLocation("textures/clienticon.png");
    ResourceLocation playericon = new ResourceLocation("textures/playericon.png");
    ResourceLocation hudicon = new ResourceLocation("textures/hudicon.png");
    ResourceLocation friendmanagericon = new ResourceLocation("textures/friendmanagericon.png");
    ResourceLocation rendericon = new ResourceLocation("textures/rendericon.png");
    ResourceLocation configicon = new ResourceLocation("textures/configpng.png");
    ResourceLocation funnygameicon = new ResourceLocation("textures/funnygameicon.png");

    //TODO ПЕРЕМЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕННЫЕ

    private static ThunderGui INSTANCE;

    static {
        INSTANCE = new ThunderGui();
    }

    public ThunderGui() {
        this.setInstance();
        this.load();
    }

    public void retryLoadHead(){
        if(PNGtoResourceLocation.getTexture2(mc.player.getName() , "png") != null){
            head = PNGtoResourceLocation.getTexture2(mc.player.getName() , "png");
        } else {
            try {
                ThunderUtils.saveUserAvatar("https://minotar.net/helm/" + mc.player.getName() + "/100.png",mc.player.getName());
            } catch (Exception ignored){

            }
        }
    }

    public static ThunderGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThunderGui();
        }
        return INSTANCE;
    }

    public static ThunderGui getThunderGui() {
        return ThunderGui.getInstance();

    }

    private void setInstance() {
        INSTANCE = this;
    }





    public void load() {


        File file = new File("ThunderHack/");
        List<File> directories = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());

        AtomicInteger index = new AtomicInteger();
        AtomicInteger index2 = new AtomicInteger();
        AtomicInteger index3 = new AtomicInteger();

            if(currentCategory != Categories.FRIENDS && currentCategory != Categories.CONFIGS) {
                Thunderhack.moduleManager.getModulesByCategory(currentCategory).forEach(module -> {
                    if (!module.hidden) {
                        //  this.addButton(new ModuleButton(module));
                        this.components.add(new TModuleButt(module, thunderguiX + 140, thunderguiY + 58 + (44 * index.get())));
                        index.set(index.get() + 1);
                    }
                });
            } else if(currentCategory == Categories.FRIENDS){
                    for (FriendManager.Friend friend : Thunderhack.friendManager.getFriends()) {
                        this.friendslist.add(new TFriendComponent(friend.getUsername(), thunderguiX + 140,  thunderguiY + 90 + (35 * index2.get())));
                        index2.set(index2.get() + 1);
                    }
            } else {
                for (File file1 : directories) {
                    if(!(Objects.equals(file1.getName(), "customimage") || Objects.equals(file1.getName(), "pvp") || Objects.equals(file1.getName(), "notebot") || Objects.equals(file1.getName(), "customimage")|| Objects.equals(file1.getName(), "kits")|| Objects.equals(file1.getName(), "tmp")|| Objects.equals(file1.getName(), "friendsAvatars")|| Objects.equals(file1.getName(), "skins") || file1.getName().contains("oldcfg"))){
                        this.configlist.add(new TConfigComponent(file1.getName(), thunderguiX + 140,  thunderguiY + 90 + (35 * index3.get())));
                        index3.set(index3.get() + 1);
                    }
                }
            }
    }




    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        Color downplatecolor =  new Color(PaletteHelper.fadeColor(oldcolor.getRGB(), getCatColor().getRGB(),coolfade));

        if (coolfade <= 1f){
            coolfade = coolfade + 0.02f;
        }
        if (coolfade >= 1f){
            oldcolor = getCatColor();
        }

        if (dragging) {
            int raznostX = (mouseX - dragX) - thunderguiX;
            int raznostY = (mouseY - dragY) - thunderguiY;
            thunderguiX = mouseX - dragX;
            thunderguiY = mouseY - dragY;


            this.configlist.forEach(components -> components.setLocation(components.getX() + raznostX,components.getY()+ raznostY));
            this.friendslist.forEach(components -> components.setLocation(components.getX() + raznostX,components.getY()+ raznostY));
            this.components.forEach(components -> components.setLocation(components.getX() + raznostX,components.getY()+ raznostY));
            TModuleButt.items.forEach(components -> components.setLocation(components.getX() + raznostX,components.getY() + raznostY));

        }
        this.checkMouseWheel(mouseX);
        final ScaledResolution sr = new ScaledResolution(mc);
        BlurUtil.getInstance().blur(thunderguiX,thunderguiY,thunderguiX + thunderguiscaleX + 87,thunderguiY + thunderguiscaleY,ThunderHackGui.getInstance().blurstr.getValue()/5);
        RenderUtil.drawRect2(thunderguiX,thunderguiY,thunderguiX + thunderguiscaleX + 87,thunderguiY + thunderguiscaleY, new Color(downplatecolor.getRed(),downplatecolor.getGreen(),downplatecolor.getBlue(),19).getRGB());
        RenderUtil.drawSmoothRect(thunderguiX + 7,thunderguiY + 5,thunderguiX + 127,thunderguiY + 43, ThunderHackGui.getInstance().thplate.getValue().getRawColor());
        RenderUtil.drawSmoothRect(thunderguiX + 137,thunderguiY + 5,thunderguiX + thunderguiscaleX - 7 + 87,thunderguiY + 43, new Color(0xFFFFFF).getRGB());

        if(head != null){
            RenderUtil.drawCircleWithTexture(thunderguiX + 156,thunderguiY + 24,0,360,15f,head,new Color(-1).getRGB());
        } else {
            retryLoadHead();
        }

        if(Thunderhack.moduleManager.getModuleByClass(NameProtect.class).isDisabled()) {
            FontRender.drawString2(mc.player.getName(),thunderguiX + 182,thunderguiY + 9, new Color(0x2A2A2A).getRGB());
        } else {
            FontRender.drawString2("Protected",thunderguiX + 182,thunderguiY + 9, new Color(0x2A2A2A).getRGB());
        }
        FontRender.drawString3("current cfg: " + Thunderhack.configManager.currentcfg,thunderguiX + 182,thunderguiY + 28, new Color(0x0A0A0A).getRGB());


        if(timer.passedMs(30)){
            ++i;
            timer.reset();
        }

        String w1 = "SNHRGHPKUXF";
        String w2 = "TvVTMLFERJC";
        String w3 = "THdTSGLHLBU";
        String w4 = "THUtWVZMQVX";
        String w5 = "THUNgGUDLUF";
        String w6 = "THUNDgMZDUB";
        String w7 = "THUNDEeBYZZY";
        String w8 = "THUNDERjHJCH";
        String w9 = "THUNDERHkKQG";
        String w10 = "THUNDERHApK";
        String w11 = "THUNDERHACu";
        String w12 = "THUNDERHACK";

        String text = "";
        if(i == 0){text = w1;}
        if(i == 1){text = w2;}
        if(i == 2){text = w3;}
        if(i == 3){text = w4;}
        if(i == 4){text = w5;}
        if(i == 5){text = w6;}
        if(i == 6){text = w7;}
        if(i == 7){text = w8;}
        if(i == 8){text = w9;}
        if(i == 9){text = w10;}
        if(i == 10){text = w11;}
        if(i >= 11){text = w12;}



        FontRender.drawString2(text,thunderguiX + 12,thunderguiY + 16,-1);
        FontRender.drawString2("+",thunderguiX + 22 + FontRender.getStringWidth("THUNDERHACK"),thunderguiY + 16, PaletteHelper.astolfo(false,1).getRGB());
        //категории

        //COMBAT
        if(currentCategory != Categories.COMBAT) {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58, thunderguiX + 120, thunderguiY + 77, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Combat",thunderguiX + 30 + 10, thunderguiY + 62,-1);
            drawImage(combaticon, thunderguiX + 12, thunderguiY + 60, 15, 15, new Color(0xF8F6F6));

        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58, thunderguiX + 120, thunderguiY + 77, getCatColor().getRGB());
            FontRender.drawString("Combat",thunderguiX + 30+ 10, thunderguiY + 62,new Color(0).getRGB());
            drawImage(combaticon, thunderguiX + 12, thunderguiY + 60, 15, 15, new Color(0x0A0A0A));

        }

        //Movement
        if(currentCategory != Categories.MOVEMENT) {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 21, thunderguiX + 120, thunderguiY + 77 + 21, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Movement",thunderguiX + 30+ 10, thunderguiY + 62 + 21,-1);
            drawImage(movementicon, thunderguiX + 12, thunderguiY + 60 +21, 15, 15, new Color(0xF8F6F6));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 21, thunderguiX + 120, thunderguiY + 77 + 21, getCatColor().getRGB());
            FontRender.drawString("Movement",thunderguiX + 30+ 10, thunderguiY + 62 + 21,new Color(0).getRGB());
            drawImage(movementicon, thunderguiX + 12, thunderguiY + 60+21, 15, 15, new Color(0x000000));
        }

        //RENDER
        if(currentCategory != Categories.RENDER) {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 42, thunderguiX + 120, thunderguiY + 77 + 42, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Render",thunderguiX + 30+ 10, thunderguiY + 62 + 21 + 21,-1);
            drawImage(rendericon, thunderguiX + 12, thunderguiY + 60+21+ 21, 15, 15, new Color(0xFFFFFF));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 42, thunderguiX + 120, thunderguiY + 77 + 42, getCatColor().getRGB());
            FontRender.drawString("Render",thunderguiX + 30+ 10, thunderguiY + 62 + 21 + 21,new Color(0).getRGB());
            drawImage(rendericon, thunderguiX + 12, thunderguiY + 60+21+ 21, 15, 15, new Color(0x000000));
        }

        //MISC
        if(currentCategory != Categories.MISC) {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 63, thunderguiX + 120, thunderguiY + 77 + 63, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Misc",thunderguiX + 30+ 10, thunderguiY + 62 + 21+ 21+ 21,-1);
            drawImage(miscicon, thunderguiX + 12, thunderguiY + 60+21+21+21, 15, 15, new Color(0xF8F6F6));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 63, thunderguiX + 120, thunderguiY + 77 + 63, getCatColor().getRGB());
            FontRender.drawString("Misc",thunderguiX + 30+ 10, thunderguiY + 62+ 21+ 21+ 21,new Color(0).getRGB());
            drawImage(miscicon, thunderguiX + 12, thunderguiY + 60+21+21+21, 15, 15, new Color(0x000000));
        }

        //PLAYER
        if(currentCategory != Categories.PLAYER) {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 84, thunderguiX + 120, thunderguiY + 77 + 84, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Player",thunderguiX + 30+ 10, thunderguiY + 62+ 21+ 21+ 21+ 21,-1);
            drawImage(playericon, thunderguiX + 12, thunderguiY + 60+21+21+21+21, 15, 15, new Color(0xF8F6F6));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 84, thunderguiX + 120, thunderguiY + 77 + 84, getCatColor().getRGB());
            FontRender.drawString("Player",thunderguiX + 30+ 10, thunderguiY + 62+ 21+ 21+ 21+ 21,new Color(0).getRGB());
            drawImage(playericon, thunderguiX + 12, thunderguiY + 60+21+21+21+21, 15, 15, new Color(0x000000));
        }

        //FUNNYGAME
        if(currentCategory != Categories.FUNNYGAME) {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 105, thunderguiX + 120, thunderguiY + 77 + 105, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("FunnyGame",thunderguiX + 30+ 10, thunderguiY + 62+ 21+ 21+ 21+ 21+ 21,-1);
            drawImage(funnygameicon, thunderguiX + 12, thunderguiY + 60+ 21+ 21+ 21+ 21+ 21, 15, 15, new Color(0xFFFFFF));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 58 + 105, thunderguiX + 120, thunderguiY + 77 + 105, getCatColor().getRGB());
            FontRender.drawString("FunnyGame",thunderguiX + 30+ 10, thunderguiY + 62+ 21+ 21+ 21+ 21+ 21,new Color(0).getRGB());
            drawImage(funnygameicon, thunderguiX + 12, thunderguiY + 60+ 21+ 21+ 21+ 21+ 21, 15, 15, new Color(0x000000));
        }

        //CLIENT
        if(currentCategory != Categories.CLIENT) {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 184, thunderguiX + 120, thunderguiY + 77 + 126, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Client",thunderguiX + 30+ 10, thunderguiY + 62+ 21+ 21+ 21+ 21+ 21+ 21,-1);
            drawImage(clienticon, thunderguiX + 12, thunderguiY + 60+21+21+21+21+21+21, 15, 15, new Color(0xF8F6F6));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 10, thunderguiY + 184, thunderguiX + 120, thunderguiY + 77 + 126, getCatColor().getRGB());
            FontRender.drawString("Client",thunderguiX + 30+ 10, thunderguiY + 62+ 21+ 21+ 21+ 21+ 21+ 21,new Color(0).getRGB());
            drawImage(clienticon, thunderguiX + 12, thunderguiY + 60+21+21+21+21+21+21, 15, 15, new Color(0x000000));
        }


        //TODO френд менеджер
        if(currentCategory != Categories.FRIENDS) {
            RenderUtil.drawSmoothRect(thunderguiX + 20, thunderguiY + 226, thunderguiX + 120, thunderguiY + 245, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Friends",thunderguiX + 40+ 10, thunderguiY + 230,-1);
            drawImage(friendmanagericon,thunderguiX + 22, thunderguiY + 228, 15, 15, new Color(0xFFFFFF));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 20, thunderguiY + 226, thunderguiX + 120, thunderguiY + 245, getCatColor().getRGB());
            FontRender.drawString("Friends",thunderguiX + 40+ 10, thunderguiY + 230,new Color(0).getRGB());
            drawImage(friendmanagericon,thunderguiX + 22, thunderguiY + 228, 15, 15, new Color(0x000000));

        }
        //TODO конфиг менеджер
        if(currentCategory != Categories.CONFIGS) {
            RenderUtil.drawSmoothRect(thunderguiX + 20, thunderguiY + 247, thunderguiX + 120, thunderguiY + 245 + 21, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("Configs",thunderguiX + 40+ 10, thunderguiY + 251,-1);
            drawImage(configicon,thunderguiX + 22, thunderguiY + 249, 15, 15, new Color(0xFFFFFF));
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 20, thunderguiY + 247, thunderguiX + 120, thunderguiY + 245 + 21, getCatColor().getRGB());
            FontRender.drawString("Configs",thunderguiX + 40+ 10, thunderguiY + 251,new Color(0).getRGB());
            drawImage(configicon,thunderguiX + 22, thunderguiY + 249, 15, 15, new Color(0x000000));
        }

        //TODO худ
        if(currentCategory != Categories.HUD) {
            RenderUtil.drawSmoothRect(thunderguiX + 20, thunderguiY + 268, thunderguiX + 120, thunderguiY + 245 + 21 + 21, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString("HUD",thunderguiX + 40+ 10, thunderguiY + 272,-1);
            mc.getTextureManager().bindTexture(this.hudicon);
            drawCompleteImage(thunderguiX + 22, thunderguiY + 272, 14, 14);
        } else {
            RenderUtil.drawSmoothRect(thunderguiX + 20, thunderguiY + 268, thunderguiX + 120, thunderguiY + 245 + 21 + 21, getCatColor().getRGB());
            FontRender.drawString("HUD",thunderguiX + 40+ 10, thunderguiY + 272,new Color(0).getRGB());
            mc.getTextureManager().bindTexture(this.hudicon);
            drawCompleteImage2(thunderguiX + 22, thunderguiY + 272, 14, 14);
        }


        if(currentCategory != Categories.CONFIGS && currentCategory != Categories.FRIENDS) {
            RenderUtil.glScissor(thunderguiX + 139, thunderguiY + 58, thunderguiX + thunderguiscaleX + 87, thunderguiY + thunderguiscaleY, sr);
        } else {
            RenderUtil.glScissor(thunderguiX + 139, thunderguiY + 82, thunderguiX + thunderguiscaleX + 87, thunderguiY + thunderguiscaleY, sr);

        }


        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.friendslist.forEach(friend -> friend.drawScreen(mouseX,mouseY,partialTicks));
        this.configlist.forEach(friend -> friend.drawScreen(mouseX,mouseY,partialTicks));
        TModuleButt.items.forEach(items -> items.drawScreen(mouseX,mouseY,partialTicks));
        this.components.forEach(components -> components.drawScreen(mouseX,mouseY,partialTicks));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);


        if(currentCategory != Categories.CONFIGS && currentCategory != Categories.FRIENDS) {
            RenderUtil.drawSmoothRect(290 + thunderguiX, thunderguiY + 58, thunderguiX + thunderguiscaleX - 7 + 87, 69 + thunderguiY, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            FontRender.drawString3(whoasked(), 291 + thunderguiX, thunderguiY + 59, -1);
            Util.fr.drawString(countSettings(), 281 + thunderguiX + FontRender.getStringWidth(whoasked()), thunderguiY + 59, new Color(0x9A9A9A).getRGB());
        }
        if(currentCategory == Categories.FRIENDS){
            RenderUtil.drawRect2(thunderguiX + 137, thunderguiY + 58, thunderguiX + thunderguiscaleX - 7 + 87, 76 + thunderguiY, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            RenderUtil.drawRect2(thunderguiX + 137, thunderguiY + 79, thunderguiX + thunderguiscaleX - 7 + 87, 81 + thunderguiY, getCatColor().getRGB());

            if(!isHoveringItem(thunderguiX + thunderguiscaleX - 6, thunderguiY + 62,thunderguiX + thunderguiscaleX - 7 + 80, 72 + thunderguiY,mouseX,mouseY)){
                RenderUtil.drawSmoothRect(thunderguiX + thunderguiscaleX - 6, thunderguiY + 62,thunderguiX + thunderguiscaleX - 7 + 80, 72 + thunderguiY,new  Color(0xCECECE).getRGB());
                FontRender.drawString3("+ Add Friend",thunderguiX + thunderguiscaleX - 6, thunderguiY + 63,new Color(0x3D3D3D).getRGB());
            } else {
                RenderUtil.drawSmoothRect(thunderguiX + thunderguiscaleX - 6, thunderguiY + 62,thunderguiX + thunderguiscaleX - 7 + 80, 72 + thunderguiY,new  Color(0x646464).getRGB());
                FontRender.drawString3("+ Add Friend",thunderguiX + thunderguiscaleX - 6, thunderguiY + 63,new Color(0xB6B5B5).getRGB());
            }

            if(Objects.equals(addFriendLine, "")){
                if(!friendlistening) {
                    FontRender.drawString("Type friend's name", thunderguiX + 139, thunderguiY + 60, new Color(-1).getRGB());
                } else {
                    FontRender.drawString("Type friend's name", thunderguiX + 139, thunderguiY + 60, new Color(0x626262).getRGB());
                }
            } else {
                FontRender.drawString(addFriendLine + "_",thunderguiX + 139, thunderguiY + 60,new Color(-1).getRGB());
            }
        }
        if(currentCategory == Categories.CONFIGS){
            RenderUtil.drawRect2(thunderguiX + 137, thunderguiY + 58, thunderguiX + thunderguiscaleX - 7 + 87, 76 + thunderguiY, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
            RenderUtil.drawRect2(thunderguiX + 137, thunderguiY + 79, thunderguiX + thunderguiscaleX - 7 + 87, 81 + thunderguiY, getCatColor().getRGB());

            if(!isHoveringItem(thunderguiX + thunderguiscaleX - 6, thunderguiY + 62,thunderguiX + thunderguiscaleX - 7 + 80, 72 + thunderguiY,mouseX,mouseY)){
                RenderUtil.drawSmoothRect(thunderguiX + thunderguiscaleX - 6, thunderguiY + 62,thunderguiX + thunderguiscaleX - 7 + 80, 72 + thunderguiY,new  Color(0xCECECE).getRGB());
                FontRender.drawString3("+ Add Config",thunderguiX + thunderguiscaleX - 6, thunderguiY + 63,new Color(0x3D3D3D).getRGB());
            } else {
                RenderUtil.drawSmoothRect(thunderguiX + thunderguiscaleX - 6, thunderguiY + 62,thunderguiX + thunderguiscaleX - 7 + 80, 72 + thunderguiY,new  Color(0x646464).getRGB());
                FontRender.drawString3("+ Add Config",thunderguiX + thunderguiscaleX - 6, thunderguiY + 63,new Color(0xB6B5B5).getRGB());
            }

            if(Objects.equals(addConfigLine, "") || !configListening){
                if(!configListening) {
                    FontRender.drawString("Type config name", thunderguiX + 139, thunderguiY + 60, new Color(-1).getRGB());
                } else {
                    FontRender.drawString("Type config name", thunderguiX + 139, thunderguiY + 60, new Color(0x646464).getRGB());
                }
            } else {
                FontRender.drawString(addConfigLine + "_",thunderguiX + 139, thunderguiY + 60,new Color(-1).getRGB());
            }

        }
    }





    public String whoasked(){
        for(TModuleButt module : components){
            if(module.isSetting()){
                return module.getName() + " settings";
            }
        }
        return "choose module";
    }

    public String countSettings(){
        for(TModuleButt module : components){
            if(module.isSetting()){
                return "b: " + module.sbools + " i: " + module.sintegers + " f: " + module.sfloats + " c: " + module.scolors + " e: " + module.senums;
            }
        }
        return "  ";
    }

    public static Color getCatColor(){
        if(currentCategory == Categories.COMBAT){
            return new Color(0xFF4254);
        }
        if(currentCategory == Categories.MISC){
            return new Color(0xC335FF);
        }
        if(currentCategory == Categories.PLAYER){
            return new Color(0xFDA145);
        }
        if(currentCategory == Categories.MOVEMENT){
            return new Color(0x17FFFF);
        }
        if(currentCategory == Categories.FUNNYGAME){
            return new Color(0xD4FF17);
        }
        if(currentCategory == Categories.RENDER){
            return new Color(0x4B8CFF);
        }
        if(currentCategory == Categories.CLIENT){
            return new Color(0x47FDB0);
        }
        if(currentCategory == Categories.FRIENDS){
            return new Color(0x00FA2A);
        }
        if(currentCategory == Categories.HUD){
            return new Color(0xFFFFFF);
        }
        if(currentCategory == Categories.CONFIGS){
            return new Color(0xDB09FF);
        }
        return new Color(0x00C78E);
    }

    public void resetThunderGui(Categories cat){
        friendlistening =false;
        configListening = false;
        coolfade = 0;
        currentCategory = cat;
        ThunderGui.getInstance().components.forEach(component -> component.setSetting(false));
        ThunderGui.getInstance().components.forEach(component -> component.items.clear());
        friendslist.clear();
        configlist.clear();
        components.clear();
        load();

    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
//        BlurUtil.getInstance().blur(thunderguiX,thunderguiY,thunderguiX + thunderguiscaleX + 87,thunderguiY + thunderguiscaleY,ThunderHackGui.getInstance().blurstr.getValue()/5);
        if((mouseX > thunderguiX + thunderguiscaleX + 87) || (mouseX <thunderguiX) || (mouseY > thunderguiY + thunderguiscaleY) || (mouseY <thunderguiY )){
            return;
        }

        if(isHoveringItem(thunderguiX + thunderguiscaleX - 6, thunderguiY + 62,thunderguiX + thunderguiscaleX - 7 + 80, 72 + thunderguiY,mouseX,mouseY)) {
           if(currentCategory == Categories.FRIENDS) {
               if (!Objects.equals(addFriendLine, "") && friendlistening) {
                   Thunderhack.friendManager.addFriend(addFriendLine);
                   resetThunderGui(currentCategory);
                   addFriendLine = "";
               }
           }
           if(currentCategory == Categories.CONFIGS){
               if(!Objects.equals(addConfigLine, "") && configListening){
                   Thunderhack.configManager.saveConfig(addConfigLine);
                   resetThunderGui(currentCategory);
                   addConfigLine = "";
               }
           }
        }




        if(currentCategory == Categories.FRIENDS) {
            friendlistening = mouseX >= thunderguiX + 137 && mouseX <= thunderguiX + 137 + 137 && mouseY >= thunderguiY + 58 && mouseY <= 76 + thunderguiY;
        }
        if(currentCategory == Categories.CONFIGS) {
            configListening = mouseX >= thunderguiX + 137 && mouseX <= thunderguiX + 137 + 137 && mouseY >= thunderguiY + 58 && mouseY <= 76 + thunderguiY;
        }



        if(isHoveringLogo(mouseX,mouseY)) {
            dragging = true;
            dragX = mouseX - thunderguiX;
            dragY = mouseY - thunderguiY;
        }





        this.friendslist.forEach(components -> components.mouseClicked(mouseX,mouseY,clickedButton));
        this.configlist.forEach(components -> components.mouseClicked(mouseX,mouseY,clickedButton));
        this.components.forEach(components -> components.mouseClicked(mouseX,mouseY,clickedButton));
        TModuleButt.items.forEach(components -> components.mouseClicked(mouseX,mouseY,clickedButton));

        if(mouseX >= thunderguiX + 10 && mouseX <= thunderguiX + 127 && mouseY >= thunderguiY + 58 && mouseY <= thunderguiY + 77){
            resetThunderGui(Categories.COMBAT);
        }
        if(mouseX >= thunderguiX + 10 && mouseX <= thunderguiX + 127 && mouseY >= thunderguiY + 58 + 21 && mouseY <= thunderguiY + 77+ 21){
            resetThunderGui(Categories.MOVEMENT);
        }
        if(mouseX >= thunderguiX + 10 && mouseX <= thunderguiX + 127 && mouseY >= thunderguiY + 58+ 21+ 21 && mouseY <= thunderguiY + 77+ 21+ 21){
            resetThunderGui(Categories.RENDER);
        }
        if(mouseX >= thunderguiX + 10 && mouseX <= thunderguiX + 127 && mouseY >= thunderguiY + 58+ 21+ 21+ 21 && mouseY <= thunderguiY + 77+ 21+ 21+ 21){
            resetThunderGui(Categories.MISC);
        }
        if(mouseX >= thunderguiX + 10 && mouseX <= thunderguiX + 127 && mouseY >= thunderguiY + 58 + 21+ 21+ 21+ 21&& mouseY <= thunderguiY + 77+ 21+ 21+ 21+ 21){
            resetThunderGui(Categories.PLAYER);
        }
        if(mouseX >= thunderguiX + 10 && mouseX <= thunderguiX + 127 && mouseY >= thunderguiY + 58 + 21+ 21+ 21+ 21+ 21&& mouseY <= thunderguiY + 77+ 21+ 21+ 21+ 21+ 21){
            resetThunderGui(Categories.FUNNYGAME);
        }
        if(mouseX >= thunderguiX + 10 && mouseX <= thunderguiX + 127 && mouseY >= thunderguiY + 58 + 21+ 21+ 21+ 21+ 21+ 21 && mouseY <= thunderguiY + 77+ 21+ 21+ 21+ 21+ 21+ 21){
            resetThunderGui(Categories.CLIENT);
        }
        if(isHoveringItem(thunderguiX + 20, thunderguiY + 226, thunderguiX + 120, thunderguiY + 245,mouseX,mouseY)){
            resetThunderGui(Categories.FRIENDS);
        }
        if(isHoveringItem(thunderguiX + 20, thunderguiY + 247, thunderguiX + 120, thunderguiY + 245 + 21,mouseX,mouseY)){
            resetThunderGui(Categories.CONFIGS);
        }
        if(isHoveringItem(thunderguiX + 20, thunderguiY + 268, thunderguiX + 120, thunderguiY + 245 + 21 + 21,mouseX,mouseY)){
            resetThunderGui(Categories.HUD);
        }

        if(isHoveringItem(thunderguiX + thunderguiscaleX + 35, 81 + thunderguiY,thunderguiX + thunderguiscaleX + 73, thunderguiscaleY + thunderguiY,mouseX,mouseY)) {
            if(currentCategory == Categories.CONFIGS || currentCategory == Categories.FRIENDS) {
                resetThunderGui(currentCategory);
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        dragging = false;
    }

    public boolean doesGuiPauseGame() {
        return false;
    }


    public ArrayList<TModuleButt> getComponents() {
        return this.components;
    }

    public enum Categories{
        RENDER,MISC,HUD,COMBAT,PLAYER,CLIENT,MOVEMENT,FUNNYGAME,FRIENDS,CONFIGS
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        TModuleButt.items.forEach(components -> components.onKeyTyped(typedChar, keyCode));

        if (friendlistening){

            if(keyCode == 42){
                return;
            }

            if(keyCode == 54){
                return;
            }

            if(keyCode == 1){
                addFriendLine = "";
                friendlistening = false;
                return;
            }
            if(keyCode == 14){
                addFriendLine = removeLastChar(addFriendLine);
                return;
            }
            if(keyCode == 28){
                Thunderhack.friendManager.addFriend(addFriendLine);
                addFriendLine = "";
                friendlistening = false;
                resetThunderGui(currentCategory);
                return;
            }
            addFriendLine = addFriendLine + typedChar;
        }

        if (configListening){

            if(keyCode == 42){
                return;
            }

            if(keyCode == 54){
                return;
            }

            if(keyCode == 1){
                addConfigLine = "";
                configListening = false;
                return;
            }
            if(keyCode == 14){
                addConfigLine = removeLastChar(addConfigLine);
                return;
            }
            if(keyCode == 28){
                Thunderhack.configManager.saveConfig(addConfigLine);
                addConfigLine = "";
                configListening = false;
                resetThunderGui(currentCategory);
                return;
            }
            addConfigLine = addConfigLine + typedChar;
        }

    }


    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }


    public void checkMouseWheel(int mouseX) {
        int dWheel = Mouse.getDWheel();


        if(currentCategory != Categories.CONFIGS && currentCategory != Categories.FRIENDS) {
            if (mouseX >= thunderguiX + 140 && mouseX <= thunderguiX + 140 + 127) {

                if (dWheel < 0) {
                    if (components.get(components.size() - 1).getY() + 30 < thunderguiY + thunderguiscaleY) {
                        return;
                    }
                    this.components.forEach(component -> component.setY(component.getY() - 5));
                    anim = -10;
                } else if (dWheel > 0) {
                    if (components.get(0).getY() + 15 > ThunderGui.thunderguiY + 58) {
                        return;
                    }
                    this.components.forEach(component -> component.setY(component.getY() + 5));
                    anim = 10;
                }
                if (dWheel == 0) {

                    if (anim > 0) {
                        int finalAnim = anim;
                        this.components.forEach(component -> component.setY(component.getY() + finalAnim));
                        --anim;
                    }
                    if (anim < 0) {
                        int finalAnim = anim;
                        this.components.forEach(component -> component.setY(component.getY() + finalAnim));
                        ++anim;
                    }
                }
            } else {
                if (dWheel < 0) {
                    TModuleButt.items.forEach(component -> component.setY(component.getY() - 5));
                    anim2 = -10;
                } else if (dWheel > 0) {
                    TModuleButt.items.forEach(component -> component.setY(component.getY() + 5));
                    anim2 = 10;
                }
                if (dWheel == 0) {

                    if (anim2 > 0) {
                        int finalAnim = anim2;
                        TModuleButt.items.forEach(component -> component.setY(component.getY() + finalAnim));
                        --anim2;
                    }
                    if (anim2 < 0) {
                        int finalAnim = anim2;
                        TModuleButt.items.forEach(component -> component.setY(component.getY() + finalAnim));
                        ++anim2;
                    }
                }
            }
        } else if(currentCategory == Categories.FRIENDS){
            try {
                if (dWheel < 0) {
                    if (friendslist.get(friendslist.size() - 1).getY() + 30 < thunderguiY + thunderguiscaleY) {
                        return;
                    }
                    this.friendslist.forEach(component -> component.setY(component.getY() - 5));
                    anim = -10;
                    totalwheel = totalwheel - 15;
                } else if (dWheel > 0) {
                    if (friendslist.get(0).getY() + 15 > ThunderGui.thunderguiY + 58) {
                        return;
                    }
                    this.friendslist.forEach(component -> component.setY(component.getY() + 5));
                    anim = 10;
                    totalwheel = totalwheel + 15;
                }
                if (dWheel == 0) {

                    if (anim > 0) {
                        int finalAnim = anim;
                        this.friendslist.forEach(component -> component.setY(component.getY() + finalAnim));
                        --anim;
                    }
                    if (anim < 0) {
                        int finalAnim = anim;
                        this.friendslist.forEach(component -> component.setY(component.getY() + finalAnim));
                        ++anim;
                    }
                }
            } catch (Exception ignored){

            }
        } else {
            try {
                if (dWheel < 0) {
                    if (configlist.get(configlist.size() - 1).getY() + 30 < thunderguiY + thunderguiscaleY) {
                        return;
                    }
                    this.configlist.forEach(component -> component.setY(component.getY() - 5));
                    anim = -10;
                    totalwheel = totalwheel - 15;
                } else if (dWheel > 0) {
                    if (configlist.get(0).getY() + 15 > ThunderGui.thunderguiY + 58) {
                        return;
                    }
                    this.configlist.forEach(component -> component.setY(component.getY() + 5));
                    anim = 10;
                    totalwheel = totalwheel + 15;
                }
                if (dWheel == 0) {

                    if (anim > 0) {
                        int finalAnim = anim;
                        this.configlist.forEach(component -> component.setY(component.getY() + finalAnim));
                        --anim;
                    }
                    if (anim < 0) {
                        int finalAnim = anim;
                        this.configlist.forEach(component -> component.setY(component.getY() + finalAnim));
                        ++anim;
                    }
                }
            } catch (Exception ignored){

            }
        }

    }


    public void onGuiClosed() {
        i = 0;
        Thunderhack.moduleManager.getModuleByClass(ThunderHackGui.class).toggle();
    }

    public boolean isHoveringLogo(int mouseX, int mouseY){
        return (mouseX >=thunderguiX + 7 && mouseX <= thunderguiX + thunderguiscaleX + 87 && mouseY >= thunderguiY + 5 && mouseY <=thunderguiY + 43 );
    }


    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY){
        return (mouseX >= x && mouseY >= y && mouseX <= x1 && mouseY <= y1);
    }

    public static void drawCompleteImage(float posX, float posY, int width, int height) {

        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        setColor(new Color(0xF6F6F6).getRGB());
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


    public static void drawCompleteImage2(float posX, float posY, int width, int height) {

        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        setColor(new Color(0xA4A4A4).getRGB());
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


    public static void setColor(int color) {
        GL11.glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, Color color) {
        GL11.glPushMatrix();
       // GL11.glDisable(2929);
      //  GL11.glEnable(3042);
     //   GL11.glDepthMask(false);
      //  OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        setColor(color.getRGB());
        Util.mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0.0F, 0.0F, (int) width, (int) height, width, height);
      //  GL11.glDepthMask(true);
      //  GL11.glDisable(3042);
      //  GL11.glEnable(2929);
        GL11.glPopMatrix();
    }
//todo



    public static void setColor(Color color, float alpha) {
        float red = color.getRed() / 255.0F;
        float green = color.getGreen() / 255.0F;
        float blue = color.getBlue() / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
    }
}
