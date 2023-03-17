package com.mrzak34.thunderhack.modules.render;


import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IItemRenderer;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public
class Animations extends Module {


    private static Animations INSTANCE = new Animations();
    public Setting<Boolean> ed = register(new Setting("EquipDisable", Boolean.valueOf(true)));
    public Setting<Boolean> auraOnly = register(new Setting("auraOnly", Boolean.valueOf(false)));
    public Setting<Float> fapSmooth = this.register(new Setting<Float>("fapSmooth", 4f, 0.5f, 15f));
    public Setting<Integer> slowValue = this.register(new Setting<>("SlowValue", 6, 1, 50));
    public Setting<rmode> rMode = register(new Setting("SwordMode", rmode.Swipe));
    public float shitfix = 1;
    public boolean abobka228 = false;


    public Animations() {
        super("Animations", "анимации удара", Module.Category.RENDER);
        this.setInstance();
    }

    public static Animations getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Animations();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        if (mc.world != null && mc.player != null) {
            shitfix = mc.player.getSwingProgress(mc.getRenderPartialTicks());
        }
    }

    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        abobka228 = ((IItemRenderer)mc.getItemRenderer()).getEquippedProgressMainHand() < 1f;
        if (ed.getValue() && ((IItemRenderer)mc.getItemRenderer()).getEquippedProgressMainHand() >= 0.9) {
            ((IItemRenderer)mc.getItemRenderer()).setEquippedProgressMainHand(1f);
            ((IItemRenderer)mc.getItemRenderer()).setItemStackMainHand(Animations.mc.player.getHeldItemMainhand());
        }
    }

    public enum rmode {
        Swipe, Rich, Glide, Default, New, Oblique, Fap, Slow
    }

}