package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GuiScreen.class)
public interface IGuiScreen {

    @Accessor(value = "buttonList")
    List<GuiButton> getButtonList();

    @Accessor(value = "buttonList")
    void setButtonList(List<GuiButton> buttonList);

}