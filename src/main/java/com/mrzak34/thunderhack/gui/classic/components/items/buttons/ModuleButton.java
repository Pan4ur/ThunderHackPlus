package com.mrzak34.thunderhack.gui.classic.components.items.buttons;


import com.mrzak34.thunderhack.gui.classic.components.Component;
import com.mrzak34.thunderhack.gui.classic.components.items.Item;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button {
    private final Module module;
    private List<Item> items = new ArrayList<Item>();
    private boolean subOpen;
    private int progress;
    private int animscaling;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.initSettings();
    }



    public void initSettings() {
        ArrayList<Item> newItems = new ArrayList<>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.getName().equals("Drawn")) {
                    newItems.add(new BooleanButton(setting));
                }
                if (setting.getValue() instanceof Parent) {
                    newItems.add(new ParentSettingButton(setting));
                }

                if (setting.getValue() instanceof ColorSetting) {
                    newItems.add(new ColorSettingComponent(setting));
                    newItems.add(new ColorShit(setting));
                    newItems.add(new ColorShit(setting));
                    newItems.add(new ColorShit(setting));
                }
                if (setting.getValue() instanceof Bind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindButton(setting));
                }

                if (setting.getValue() instanceof SubBind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new SubBindButton(setting));
                }

                if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new StringButton(setting));
                }
                if (setting.isNumberSetting() && setting.hasRestriction()) {
                    newItems.add(new Slider(setting));
                    continue;
                }

                if (!setting.isEnumSetting() || setting.getValue() instanceof Parent)
                    continue;
                newItems.add(new EnumButton(setting));
            }
        }
        newItems.add(new BindButton(this.module.getSettingByName("Keybind")));
        this.items = newItems;
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
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {

            if(anim < needtoanim){
                anim = anim + needtoanim/10;
            }
            if(anim > needtoanim){
                anim = anim - anim / 10;
            }
            if(anim < needtoanim){
                anim = anim + 1;
            }
            if(anim > needtoanim){
                anim = anim - 1;
            }


            if (this.subOpen || anim > 14) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                float height2 = 1f;

                for (Item item : this.items) {
                    if(!item.isHidden())
                        height2 += 15.0f;
                }

                ScaledResolution sr = new ScaledResolution(mc);
                RenderUtil.glScissor(this.x, this.y + 15, this.x + this.width, this.y + (height2 + 15f), sr);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                if(animscaling != 10) {
                    animscaling = animscaling + 1;
                }


                progress = progress + 5;

                RenderUtil.draw1DGradientRect( this.x, this.y + 15, this.x + this.width, this.y + height2 + 15f, this.getState() ? ClickGui.getInstance().gcolor1.getValue().getColor() : ClickGui.getInstance().downColor.getValue().getColor(),this.getState() ? ClickGui.getInstance().gcolor2.getValue().getColor() : ClickGui.getInstance().downColor.getValue().getColor());
                RenderUtil.drawRect( this.x, this.y + 15, this.x + this.width, this.y + height2 + 15f,getState() ?  ClickGui.getInstance().mainColor2.getValue().getRawColor() : ClickGui.getInstance().mainColor3.getValue().getRawColor());


                if (this.subOpen) {
                    float height = 0.0f;
                    for (Item item : this.items) {
                        Component.counter1[0] = Component.counter1[0] + 1;
                        if (!item.isHidden()) {
                            item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                            item.setHeight(15);
                            item.setWidth(this.width - 9);
                            item.drawScreen(mouseX, mouseY, partialTicks);
                        }
                        item.update();
                    }
                }
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            } else {
                animscaling = 0;
            }
        }
    }




    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (this.subOpen) {
                for (Item item : this.items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }


    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }


    int anim = 0;
    int needtoanim = 0;

    @Override
    public float getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                height += item.getHeight() + 1;
            }
            anim = height + 2;
            return  anim;
        }
        anim = 14;
        return anim;
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    public void toggle() {
        this.module.toggle();
    }

    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }




}

