package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TModuleButt extends TItem{
    private final Module module;
    private int progress;
    public TModuleButt(Module module,int x,int y) {
        super(module.getName());
        this.module = module;
        this.setLocation(x,y);
    }
    public static List<TItem> items = new ArrayList<TItem>();



    public int desh2(){
        int returnedval = 37;
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {
                if(setting.getValue() instanceof ColorSetting){
                    returnedval = 100;
                }
                if (setting.isPositionSetting()){
                    returnedval = 100;
                }
                if(setting.isNumberSetting()){
                    if(70 > returnedval){
                        returnedval = 70;
                    }
                }
                if(setting.isEnumSetting()){
                    if(47 > returnedval){
                        returnedval = 47;
                    }
                }
            }
        }
        return returnedval;
    }

    public static void drawModalRect(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, float var9) {
        Gui.drawScaledCustomSizeModalRect(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }
    public float age = 0;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }


        RenderUtil.drawRect(this.x,this.y,this.x + 127,this.y + 40, ThunderHackGui.getInstance().buttsColor.getValue().getColorObject().getRGB());





        age = age + 0.5f;

        if(module.isSetting()) {
            GlStateManager.pushMatrix();
           // GlStateManager.enableBlend();
            RenderUtil.drawSmoothRect(this.x + 114, this.y + 27, this.x + 124, this.y + 37, RenderUtil.TwoColoreffect(new Color(0xA4A4A4), new Color(0x2C2C2C), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0F * (age / 16) / 60).getRGB());
            progress = progress + 1;
            Util.mc.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/modulegear.png"));
            GlStateManager.translate(getX() + 119f, getY() + 32f, 0.0F);
            GlStateManager.rotate(calculateRotation((float) this.progress), 0.0F, 0.0F, 1.0F);
            drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0f, 10.0f);
           // GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        } else {
            age = 0;
            GlStateManager.pushMatrix();
           // GlStateManager.enableBlend();
            RenderUtil.drawSmoothRect(this.x + 114, this.y + 27, this.x + 124, this.y + 37, new Color(0xA4A4A4).getRGB());
            Util.mc.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/modulegear.png"));
            GlStateManager.translate(getX() + 119f, getY() + 32f, 0.0F);
            drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0f, 10.0f);
          //  GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        if(!this.module.isEnabled()){
            FontRender.drawString(module.getName(), this.x +3, this.y+6, new Color(0xE3D9D9).getRGB());
        } else {
            FontRender.drawString(module.getName(), this.x + 3, this.y + 6, ThunderGui.getCatColor().getRGB());
        }
        if(!module.getBind().isEmpty() && module.isValidBind(module.getBind().toString()) ) {
            FontRender.drawString(module.getBind().toString(), this.x + 115, this.y + 6, new Color(0xE3D9D9).getRGB());
        }

        String myString = module.getDescription();
        String[] splitString = myString.split("-");
        if(splitString[0] != null && !splitString[0].equals("")) {
            Util.fr.drawString(splitString[0], (int) this.x + 5, (int) (this.y + 17), new Color(0x656565).getRGB());
        }
        if(splitString.length  > 1) {
            if (splitString[1] != null && !splitString[1].equals("")) {
                Util.fr.drawString(splitString[1], (int) this.x + 5, (int) (this.y + 25), new Color(0x656565).getRGB());
            }
        }
        if(splitString.length == 3) {
            if (splitString[2] != null && !splitString[2].equals("")) {
                Util.fr.drawString(splitString[2], (int) this.x + 5, (int) (this.y + 32), new Color(0x656565).getRGB());
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if(mouseButton == 0 && mouseX >= this.x + 114 && mouseX <= this.x + 124 && mouseY >= this.y +27 && mouseY <= this.y  + 37){
            ThunderGui.getInstance().components.forEach(component -> component.setSetting(false));
            ThunderGui.getInstance().components.forEach(component -> component.items.clear());
            this.module.setSetting(true);
            this.initSettings();
            return;
        }
        if(mouseButton == 0 && mouseX >= this.x && mouseX <= this.x + 127 && mouseY >= this.y && mouseY <= this.y + 40){
            this.module.toggle();
        }
    }

    public void setSetting(boolean b) {
        module.setSetting(b);
    }


    public static float calculateRotation(float var0) {
        if ((var0 %= 360.0F) >= 180.0F) {
            var0 -= 360.0F;
        }

        if (var0 < -180.0F) {
            var0 += 360.0F;
        }

        return var0;
    }


    public int chooseSide(int a){
        if(a == 0){
            return (int) (this.x + 150);
        } else if( a == 1){
            return (int) (this.x + 250);
        } else if(a == 2){
            return (int) (this.x + 350);
        }
        return (int) (this.x + 150);
    }


    public int sintegers = 0;
    public int sfloats = 0;
    public int senums = 0;
    public int scolors= 0;
    public int sbools = 0;
    int stepik = 0;
    boolean biloslider = false; // 41
    boolean bilocolor = false; // 90
    boolean bilopos = false; // 67
    boolean biloparent= false; // 67
    boolean lastbiloslider = false; // 41
    boolean lastbilocolor = false; // 90
    boolean lastbilopos = false; // 67
    boolean lastparent = false; // 67

    public void initSettings() {
        sintegers = 0;
        sfloats = 0;
        senums = 0;
        scolors= 0;
        sbools = 0;
        AtomicInteger index = new AtomicInteger();
        stepik = 0;
        // 23
        ArrayList<TItem> newItems = new ArrayList<TItem>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {


                if(stepik == 3){
                    index.set(index.get() + 1);
                    stepik = 0;
                }
                if(setting.getValue() instanceof Parent){
                    stepik = 3;
                    newItems.add(new ModuleSettingCategory(setting, (int) chooseSide(0), ThunderGui.thunderguiY + 129 + (desh2()* index.get())));
                    biloparent = true;
                }

                if(setting.isPositionSetting()){
                    newItems.add(new TPositionSelector(setting, (int) chooseSide(stepik), ThunderGui.thunderguiY + 77 + (desh2())* index.get()));
                    ++stepik;
                }
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.getName().equals("Drawn")) {
                    newItems.add(new TBooleanButt(setting, (int) chooseSide(stepik), ThunderGui.thunderguiY + 77 + (desh2())* index.get()));
                    ++sbools;
                    ++stepik;
                }
                if (setting.isEnumSetting()) {
                    newItems.add(new TModeButt(setting, (int) chooseSide(stepik), ThunderGui.thunderguiY + 77 + (desh2()) * index.get()));
                    ++stepik;
                    ++senums;
                }
                if(setting.isColorSetting()){
                    newItems.add(new TColorPicker(setting, (int) chooseSide(stepik), ThunderGui.thunderguiY + 77 + (desh2()) * index.get()));
                    ++stepik;
                    ++scolors;

                }

                if (setting.isNumberSetting() && setting.hasRestriction()) {
                    newItems.add(new TSlider(setting,(int) chooseSide(stepik),ThunderGui.thunderguiY + 77 + (desh2())* index.get()));
                    if(setting.isFloat()){
                        ++sfloats;
                    }
                    if(setting.isInteger()){
                        ++sintegers;
                    }

                    ++stepik;
                    continue;
                }
                if (setting.getValue() instanceof SubBind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new TSubBindButt(setting,(int) chooseSide(stepik),ThunderGui.thunderguiY + 77 + (desh2()* index.get())));
                    ++stepik;

                }
                if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new TStringButt(setting,(int) chooseSide(stepik),ThunderGui.thunderguiY + 77 + (desh2()* index.get())));
                    ++stepik;
                }

            }
        }
        newItems.add(new TBindButt(this.module.getSettingByName("Keybind"),(int) chooseSide(0),ThunderGui.thunderguiY + 200 + (desh2()* index.get())));
        this.items = newItems;
    }

    /*
    public int desh(){
        if(lastbilocolor){
            return 60;
        } else {
            if (lastbilopos) {
                return 37;
            } else {
                if (lastbiloslider){
                    return 30;
                } else {
                    return 0;
                }
            }
        }
    }


     */
    public boolean isSetting() {
        return this.module.isSetting();
    }
}
