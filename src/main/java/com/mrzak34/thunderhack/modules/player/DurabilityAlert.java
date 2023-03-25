package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.render.Drawable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;

public class DurabilityAlert extends Module {
    public DurabilityAlert() {
        super("DurabilityAlert", "предупреждает о-прочности брони", "durability alert", Category.PLAYER);
    }

    public Setting<Boolean> friends = register(new Setting<>("Friend message", true));
    public Setting<Integer> percent = register(new Setting<>("Percent", 20, 1, 100));
    private final ResourceLocation ICON = new ResourceLocation("textures/broken_shield.png");
    private boolean need_alert = false;
    private Timer timer = new Timer();

    @Override
    public void onUpdate() {
        if(friends.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (!Thunderhack.friendManager.isFriend(player)) continue;
                if (player == mc.player) continue;
                for (ItemStack stack : player.inventory.armorInventory) {
                    if (stack.isEmpty()) continue;
                    if (getDurability(stack) < percent.getValue() && timer.passedMs(30000)) {
                        if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                            mc.player.sendChatMessage("/msg " + player.getName() + " Срочно чини броню!");
                        } else {
                            mc.player.sendChatMessage("/msg " + player.getName() + " Repair your armor immediately!");
                        }
                        timer.reset();
                    }
                }
            }
        }

        boolean flag = false;
        for (ItemStack stack : mc.player.inventory.armorInventory) {
            if (stack.isEmpty()) continue;
            if(getDurability(stack) < percent.getValue()){
                need_alert = true;
                flag = true;
            }
        }
        if(!flag && need_alert){
            need_alert = false;
        }

    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        if(need_alert) {
            if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                FontRender.drawCentString6("Срочно чини броню!", (float) e.getScreenWidth() / 2f, (float) e.getScreenHeight() / 3f, new Color(0xFFDF00).getRGB());
                Drawable.drawTexture(ICON, (float) e.getScreenWidth() / 2f - 40, (float) e.getScreenHeight() / 3f - 120, 80, 80, new Color(0xFFDF00));
            } else {
                FontRender.drawCentString6("Repair your armor immediately!", (float) e.getScreenWidth() / 2f, (float) e.getScreenHeight() / 3f, new Color(0xFFDF00).getRGB());
                Drawable.drawTexture(ICON, (float) e.getScreenWidth() / 2f - 40, (float) e.getScreenHeight() / 3f - 120, 80, 80, new Color(0xFFDF00));
            }
        }
    }


    public static int getDurability(ItemStack stack) {
        return (int) ((stack.getMaxDamage() - stack.getItemDamage()) / Math.max(0.1, stack.getMaxDamage()) * 100.0f);
    }
}
