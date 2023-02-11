package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

import java.util.Objects;

public class EffectsRemover extends Module{
    public EffectsRemover() {
        super("PowderTweaks", "Убирает джампбуст от-пороха и юзает его автоматом", Module.Category.FUNNYGAME);
    }


    public Setting<Boolean> jumpBoost = this.register(new Setting<Boolean>("JumpBoostRemove", false));
    public Setting<Boolean> oldr = this.register(new Setting<Boolean>("OldRemove", false));

    public Timer timer = new Timer();
    public static int nig = 0;
    public static boolean jboost = false;

    @Override
    public void onUpdate() {
        --nig;

        if(fullNullCheck()){
            return;
        }

        if( timer.passedMs(500) &&  !(mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation("strength")))) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK){
            int hotbarslot = mc.player.inventory.currentItem;
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(InventoryUtil.getPowderAtHotbar());
            if (!(itemStack.getItem().getItemStackDisplayName(itemStack).equals("Порох"))) return;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getPowderAtHotbar()));
            mc.playerController.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(hotbarslot));
            timer.reset();
        }

        if (this.jumpBoost.getValue() ) {
            if (Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled()){
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    nig = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getDuration();
                    EffectsRemover.mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("jump_boost"));
                    jboost = true;
                }
            }
        }
        if(oldr.getValue()){
            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                EffectsRemover.mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("jump_boost"));
            }
        }

    }
}
