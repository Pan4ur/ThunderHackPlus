package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.mixin.ducks.IEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {

    public static double oldSpeed, contextFriction;
    public Setting<Float> reduction = this.register(new Setting<>("reduction ", 0.1f, 0f, 0.5f));
    int cooldown;
    private final Setting<mode> Mode = register(new Setting("Mode", mode.Default));


    public Sprint() {
        super("Sprint", "автоматически-спринтится", Category.MOVEMENT);
    }

    public static double calculateSpeed(double speed) {
        float speedAttributes = getAIMoveSpeed(mc.player);
        final float n6 = getFrictionFactor(mc.player);
        final float n7 = 0.16277136f / (n6 * n6 * n6);
        float n8 = speedAttributes * n7;
        double max2 = oldSpeed + n8;
        max2 = Math.max(0.25, max2);
        contextFriction = n6;
        return max2 + speed;
    }

    public static void postMove(double horizontal) {
        oldSpeed = horizontal * contextFriction;
    }

    public static float getAIMoveSpeed(EntityPlayer contextPlayer) {
        boolean prevSprinting = contextPlayer.isSprinting();
        contextPlayer.setSprinting(false);
        float speed = contextPlayer.getAIMoveSpeed() * 1.3f;
        contextPlayer.setSprinting(prevSprinting);
        return speed;
    }

    private static float getFrictionFactor(EntityPlayer contextPlayer) {
        BlockPos.PooledMutableBlockPos blockpos = BlockPos.PooledMutableBlockPos.retain(mc.player.prevPosX, mc.player.getEntityBoundingBox().minY, mc.player.prevPosZ);
        return contextPlayer.world.getBlockState(blockpos).getBlock().slipperiness * 0.91F;
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.player.setSprinting(true);
        }
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (fullNullCheck()) {
            return;
        }

        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            cooldown = 60;
        }
    }

    @SubscribeEvent
    public void onMove(EventMove event) {
        if (Mode.getValue() == mode.NexusGrief && Thunderhack.moduleManager.getModuleByClass(Speed.class).isDisabled()) {

            double dX = mc.player.posX - mc.player.prevPosX;
            double dZ = mc.player.posZ - mc.player.prevPosZ;
            postMove(Math.sqrt(dX * dX + dZ * dZ));
            // return;

            if (strafes()) {
                double forward = mc.player.movementInput.moveForward;
                double strafe = mc.player.movementInput.moveStrafe;
                float yaw = mc.player.rotationYaw;
                if (forward == 0.0 && strafe == 0.0) {
                    oldSpeed = 0;
                } else {

                    if (forward != 0.0) {
                        if (strafe > 0.0) {
                            yaw += ((forward > 0.0) ? -45 : 45);
                        } else if (strafe < 0.0) {
                            yaw += ((forward > 0.0) ? 45 : -45);
                        }
                        strafe = 0.0;
                        if (forward > 0.0) {
                            forward = 1.0;
                        } else if (forward < 0.0) {
                            forward = -1.0;
                        }
                    }
                    double speed = calculateSpeed(reduction.getValue() / 10f);
                    event.set_x(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
                    event.set_z(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
                }
            } else {
                oldSpeed = 0;
            }
            // if (event.getStage() == 0) {
            event.setCanceled(true);
            //  }
        }
    }

    public boolean strafes() {

        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (mc.player.isInWater()) {
            return false;
        }
        if (((IEntity)mc.player).isInWeb()) {
            return false;
        }
        if (!mc.player.onGround) {
            return false;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            return false;
        }
        if (cooldown > 0) {
            return false;
        }
        if (Thunderhack.moduleManager.getModuleByClass(RusherScaffold.class).isOn()) {
            return false;
        }
        if (Thunderhack.moduleManager.getModuleByClass(LongJump.class).isOn()) {
            return false;
        }
        return !mc.player.capabilities.isFlying;
    }

    public enum mode {
        Default, NexusGrief
    }

}
