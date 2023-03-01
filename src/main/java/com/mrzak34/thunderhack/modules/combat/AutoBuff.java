package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoBuff extends Module {


    public AutoBuff() {
        super("AutoBuff", "Кидает бафы", Category.COMBAT);
    }

    public Setting<Boolean> strenght = register(new Setting<>("Strenght", true));
    public Setting<Boolean> speed = register(new Setting<>("Speed", true));
    public Setting<Boolean> fire = register(new Setting<>("FireRes", true));
    public Setting<Boolean> heal = register(new Setting<>("Heal", true));
    public Setting<Integer> health = register(new Setting<>("Health", 8, 0, 20));

    public Timer timer = new Timer();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvent(EventPreMotion event) {
            if (Aura.target != null && mc.player.getCooledAttackStrength(1) > 0.5f)
                return;
            boolean shouldThrow = (!mc.player.isPotionActive(MobEffects.SPEED) && isPotionOnHotBar(Potions.SPEED) && speed.getValue())
                        || (!mc.player.isPotionActive(MobEffects.STRENGTH) && isPotionOnHotBar(Potions.STRENGTH) && strenght.getValue())
                        || (!mc.player.isPotionActive(MobEffects.FIRE_RESISTANCE) && isPotionOnHotBar(Potions.FIRERES) && fire.getValue())
                        || (EntityUtil.getHealth(mc.player)  < health.getValue() && isPotionOnHotBar(Potions.HEAL) && heal.getValue());
            if (mc.player.ticksExisted > 80 && shouldThrow) {
                    mc.player.rotationPitch = 90;
            }
    }

    @SubscribeEvent
    public void onPostMotion(EventPostMotion e){
        if (Aura.target != null && mc.player.getCooledAttackStrength(1) > 0.5f)
            return;
        e.addPostEvent(() -> {
            boolean shouldThrow =
                    (!mc.player.isPotionActive(MobEffects.SPEED) && isPotionOnHotBar(Potions.SPEED) && speed.getValue())
                            || (!mc.player.isPotionActive(MobEffects.STRENGTH) && isPotionOnHotBar(Potions.STRENGTH) && strenght.getValue())
                            || (!mc.player.isPotionActive(MobEffects.FIRE_RESISTANCE) && isPotionOnHotBar(Potions.FIRERES) && fire.getValue())
                            || (EntityUtil.getHealth(mc.player) < health.getValue() && isPotionOnHotBar(Potions.HEAL) && heal.getValue());
            if (mc.player.ticksExisted > 80 && shouldThrow && timer.passedMs(1000)) {
                if (!mc.player.isPotionActive(MobEffects.SPEED) && isPotionOnHotBar(Potions.SPEED) && speed.getValue()) {
                    throwPotion(Potions.SPEED);
                }
                if (!mc.player.isPotionActive(MobEffects.STRENGTH) && isPotionOnHotBar(Potions.STRENGTH) && strenght.getValue()) {
                    throwPotion(Potions.STRENGTH);
                }
                if (!mc.player.isPotionActive(MobEffects.FIRE_RESISTANCE) && isPotionOnHotBar(Potions.FIRERES) && fire.getValue()) {
                    throwPotion(Potions.FIRERES);
                }
                if (EntityUtil.getHealth(mc.player)  < health.getValue() && heal.getValue() && isPotionOnHotBar(Potions.HEAL)) {
                    throwPotion(Potions.HEAL);
                }
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                timer.reset();
            }
        });
    }



    public void throwPotion(Potions potion) {
        int slot = getPotionSlot(potion);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.playerController.updateController();
    }

    public static int getPotionSlot(Potions potion) {
        for (int i = 0; i < 9; ++i) {
            if (isStackPotion(mc.player.inventory.getStackInSlot(i), potion)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isPotionOnHotBar(Potions potions) {
        return getPotionSlot(potions) != -1;
    }

    public static boolean isStackPotion(ItemStack stack, Potions potion) {
        if (stack == null)
            return false;

        if (stack.getItem() == Items.SPLASH_POTION) {
            int id = 0;

            switch (potion) {
                case STRENGTH: {
                    id = 5;
                    break;
                }
                case SPEED: {
                    id = 1;
                    break;
                }
                case FIRERES: {
                    id = 12;
                    break;
                }
                case HEAL: {
                    id = 6;
                }
            }

            for (PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
                if (effect.getPotion() == Potion.getPotionById(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public enum Potions {
        STRENGTH, SPEED, FIRERES, HEAL
    }
}
