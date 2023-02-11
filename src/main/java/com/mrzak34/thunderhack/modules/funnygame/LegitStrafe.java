package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.events.PlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class LegitStrafe extends Module {
    public LegitStrafe() {
        super("GlideFly", "мsdfsdf", Category.MOVEMENT);
    }

    public Setting<Float> motion =this.register( new Setting<>("motionY", 0.05F, 0F, 0.42F));
    public Setting<Float> motion2 =this.register( new Setting<>("motionY2", 0.05F, 0F, 0.42F));
    public Setting<Float> speed =this.register( new Setting<>("speed", 0.1F, 0.1F, 3F));
    public Setting<Integer> fallDist = this.register(new Setting<>("dsff", 100, 0, 150));


    private Timer timer = new Timer();

    public static boolean skip_tick;

    @SubscribeEvent
    public void onEvent(PlayerUpdateEvent event) {

        int elytra = getSlotIDFromItem(Items.ELYTRA);
        if (elytra == -1) {
            return;
        }
        disabler(elytra);

        if(!mc.player.onGround){
            setSpeed(speed.getValue());
        }


    }

    public void disabler(int elytra) {
        if (elytra != -2 && !skip_tick) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            skip_tick = true;
            return;
        }
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        if (mc.gameSettings.keyBindJump.pressed) {
            mc.player.motionY = motion.getValue();
        } else if (mc.gameSettings.keyBindSneak.pressed) {
            mc.player.motionY = -motion.getValue();
        } else {
            mc.player.motionY = motion2.getValue();
        }
        mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, last_elytra_slot, 1, ClickType.PICKUP, mc.player);
        skip_tick = false;
    }

    public static void setSpeed(float speed) {
        float yaw = mc.player.rotationYaw;
        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        if (forward != 0.0F) {
            if (strafe > 0.0F) {
                yaw += (float)(forward > 0.0F ? -45 : 45);
            } else if (strafe < 0.0F) {
                yaw += (float)(forward > 0.0F ? 45 : -45);
            }

            strafe = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        mc.player.motionX = (double)(forward * speed) * cos + (double)(strafe * speed) * sin;
        mc.player.motionZ = (double)(forward * speed) * sin - (double)(strafe * speed) * cos;
    }




    private static int last_elytra_slot;

    public static int getSlotIDFromItem(Item item) {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() == item) {
                return -2;
            }
        }
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == item) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }

}
