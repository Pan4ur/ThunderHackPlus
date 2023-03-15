package com.mrzak34.thunderhack.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.InteractionUtil;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import static com.mrzak34.thunderhack.util.SilentRotationUtil.calcAngle;

public class HoleFiller extends Module {

    private final Setting<Float> rangeXZ = this.register(new Setting<>("Range", 6f, 1f, 7f));
    private final Setting<Integer> predictTicks = this.register(new Setting<>("PredictTicks", 3, 0, 25));
    private final List<BlockPos> Holes = new ArrayList<>();
    private final Timer notification_timer = new Timer();
    EntityPlayer target;
    BlockPos targetPosition;
    public HoleFiller() {
        super("HoleFiller", "HoleFiller", "HoleFiller", Category.COMBAT);
    }

    public static EntityEnderCrystal searchCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
            if (entity instanceof EntityEnderCrystal) {
                return (EntityEnderCrystal) entity;
            }
        }
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
            if (entity instanceof EntityEnderCrystal) {
                return (EntityEnderCrystal) entity;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onEntitySync(EventPreMotion e) {
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-rangeXZ.getValue(), -rangeXZ.getValue(), -rangeXZ.getValue()), mc.player.getPosition().add(rangeXZ.getValue(), rangeXZ.getValue(), rangeXZ.getValue()));
        Holes.clear();
        for (BlockPos pos : blocks) {
            if (!(mc.world.getBlockState(pos).getMaterial().blocksMovement() && mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement() && mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial().blocksMovement())) {
                if (checkHole(pos)) {
                    Holes.add(pos);
                }
            }
        }

        if (Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).getTarget() != null) {
            target = Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).getTarget();
        } else {
            target = findTarget();
        }

        if (target == null) return;

        double predict_x = (target.posX - target.prevPosX) * predictTicks.getValue();
        double predict_z = (target.posZ - target.prevPosZ) * predictTicks.getValue();
        BlockPos predict_pos = new BlockPos(target.posX + predict_x, target.posY, target.posZ + predict_z);

        for (BlockPos bp : Holes) {
            if (target.getDistanceSq(bp) < 4) {
                fixHolePre(bp);
            } else if (predict_pos.distanceSq(bp.x, bp.y, bp.z) < 4) {
                fixHolePre(bp);
            }
        }

    }

    public void fixHolePre(BlockPos bp) {
        float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(bp.down().add(0.5, 1, 0.5)));
        mc.player.rotationYaw = angle[0];
        mc.player.rotationPitch = angle[1];
        targetPosition = bp;
    }

    @SubscribeEvent
    public void postEntitySync(EventPostMotion e) {
        if (targetPosition != null) {
            int obby_slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            if (obby_slot == -1) {
                Command.sendMessage("no obby");
                targetPosition = null;
                toggle();
                return;
            }
            mc.player.connection.sendPacket(new CPacketHeldItemChange(obby_slot));
            InteractionUtil.placeBlock(targetPosition, true);
            if (notification_timer.passedMs(200) && Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isOn()) {
                NotificationManager.publicity("HoleFiller " + ChatFormatting.GREEN + "hole X" + targetPosition.x + " Y" + targetPosition.y + " Z" + targetPosition.z + " is successfully blocked", 2, Notification.Type.SUCCESS);
                notification_timer.reset();
            }
            targetPosition = null;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }

    public EntityPlayer findTarget() {
        EntityPlayer target = null;
        double distance = rangeXZ.getPow2Value();
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (entity == mc.player) {
                continue;
            }
            if (Thunderhack.friendManager.isFriend(entity)) {
                continue;
            }
            if (mc.player.getDistanceSq(entity) <= distance) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
            }
        }
        return target;
    }

    public boolean isOccupied(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
            if (entity instanceof EntityPlayer) {
                return true;
            }
        }
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
            if (entity instanceof EntityEnderCrystal) {
                return true;
            }
        }
        return false;
    }

    public boolean checkHole(BlockPos pos) {
        return (BlockUtils.validObi(pos) || BlockUtils.validBedrock(pos)) && !isOccupied(pos);
    }
}
