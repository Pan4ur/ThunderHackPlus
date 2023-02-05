package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.gui.mainmenu.ThunderMenu;
import com.mrzak34.thunderhack.modules.client.AntiDisconnect;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.util.phobos.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin(value = {Minecraft.class})
public abstract class MixinMinecraft implements IMinecraft {
    @Shadow @Nullable public GuiScreen currentScreen;

    @Inject(method = {"shutdownMinecraftApplet"}, at = {@At(value = "HEAD")})
    private void stopClient(CallbackInfo callbackInfo) {
        this.unload();
    }

    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        this.unload();
    }

    @Inject(method = {"runTickKeyboard"}, at = {@At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE)})
    private void onKeyboard(CallbackInfo callbackInfo) {
        int i;
        int n = i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            KeyEvent event = new KeyEvent(i);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }
    private int gameLoop = 0;
    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoopHead(CallbackInfo callbackInfo)
    {
        gameLoop++;
    }

    @Inject(method = "middleClickMouse", at = @At(value = "HEAD"), cancellable = true)
    public void middleClickMouseHook(CallbackInfo callbackInfo)
    {
        ClickMiddleEvent event = new ClickMiddleEvent();
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled())
        {
            callbackInfo.cancel();
        }
    }
    @Inject(method={"runTick()V"}, at={@At(value="RETURN")})
    private void runTick(CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu && Thunderhack.moduleManager != null && Thunderhack.moduleManager.getModuleByClass(MainSettings.class).mainMenu.getValue() ) {
            Minecraft.getMinecraft().displayGuiScreen(new ThunderMenu());
        }
    }

    @Inject(method={"displayGuiScreen"}, at={@At(value="HEAD")})
    private void displayGuiScreenHook(GuiScreen screen, CallbackInfo ci) {
        if (screen instanceof GuiMainMenu && Thunderhack.moduleManager != null && Thunderhack.moduleManager.getModuleByClass(MainSettings.class).mainMenu.getValue()) {
            mc.displayGuiScreen(new ThunderMenu());
        }
    }

    @Inject(
            method = "runTickMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/input/Mouse;getEventButton()I",
                    remap = false))
    public void runTickMouseHook(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new MouseEvent(Mouse.getEventButton(), Mouse.getEventButtonState()));
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;tick()V",
                    shift = At.Shift.AFTER))
    private void postUpdateWorld(CallbackInfo info)
    {
        MinecraftForge.EVENT_BUS.post(new PostWorldTick());
    }

    @Inject(
            method = "runGameLoop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/profiler/Profiler;endSection()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private void post_ScheduledTasks(CallbackInfo callbackInfo)
    {
        MinecraftForge.EVENT_BUS.post(new GameZaloopEvent());
    }
    @Override
    public int getGameLoop()
    {
        return gameLoop;
    }
    @Inject(
            method = "runTickKeyboard",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "org/lwjgl/input/Keyboard.getEventKeyState()Z",
                    remap = false))
    public void runTickKeyboardHook(CallbackInfo callbackInfo)
    {
        MinecraftForge.EVENT_BUS.post(new KeyboardEvent(Keyboard.getEventKeyState(),
                Keyboard.getEventKey(),
                Keyboard.getEventCharacter()));
    }

    @Redirect(method={"runGameLoop"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;shutdown()V"))
    private void Method5080(Minecraft minecraft) {
        if (minecraft.world != null && Thunderhack.moduleManager.getModuleByClass(AntiDisconnect.class).isOn()) {
            GuiScreen screen = minecraft.currentScreen;
            GuiYesNo g = new GuiYesNo((result, id) -> {
                if (result) {
                    minecraft.shutdown();
                } else {
                    Minecraft.getMinecraft().displayGuiScreen(screen);
                }
            }, "Ты точно хочешь закрыть майн?", "", 0);
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)g);
        } else {
            minecraft.shutdown();
        }
    }
    private void unload() {
        Thunderhack.unload(false);
    }
}

