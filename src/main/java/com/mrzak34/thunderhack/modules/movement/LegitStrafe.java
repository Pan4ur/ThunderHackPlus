package com.mrzak34.thunderhack.modules.movement;


import com.mrzak34.thunderhack.events.PlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.modules.player.ElytraSwap.*;
import static com.mrzak34.thunderhack.modules.player.ElytraSwap.clickSlot;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class LegitStrafe extends Module {

    public LegitStrafe() {
        super("GlideFly", "флай на саник-хуй пососаник", Category.MOVEMENT);
    }

    public Setting<Float> motion =this.register( new Setting<>("motionY", 0.2F, 0F, 0.42F));
    public Setting<Float> motion2 =this.register( new Setting<>("motionY2", 0.42F, 0F, 0.84F));
    public Setting<Float> speed =this.register( new Setting<>("Speed", 0.8F, 0.1F, 3F));

    int prevElytraSlot = -1;

    @SubscribeEvent
    public void onEvent(PlayerUpdateEvent event) {

        if(mc.player.ticksExisted % 2 != 0) return;
        ItemStack itemStack = getItemStack(38);
        if(itemStack == null) return;
        if(mc.player.onGround) return;

        if (itemStack.getItem() == Items.ELYTRA) {
            if (prevElytraSlot != -1) {
                clickSlot(prevElytraSlot);
                clickSlot(38);
                clickSlot(prevElytraSlot);
            }
        } else if (hasItem(Items.ELYTRA)) {
            prevElytraSlot = getSlot(Items.ELYTRA);
            clickSlot(prevElytraSlot);
            clickSlot(38);
            clickSlot(prevElytraSlot);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            mc.player.motionY = motion.getValue();

            if (mc.gameSettings.keyBindJump.pressed) {
                mc.player.motionY = motion2.getValue();
            } else if (mc.gameSettings.keyBindSneak.pressed) {
                mc.player.motionY = -motion2.getValue();
            }
            if(isMoving()) {
                setSpeed(speed.getValue());
            } else {
                setSpeed2(0.1f);
            }
        }
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



    public static void setSpeed2(float speed) {
        float yaw = mc.player.rotationYaw;
        float forward = 1.0F;
        double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        mc.player.motionX = (double)(forward * speed) * cos;
        mc.player.motionZ = (double)(forward * speed) * sin;
    }

}
