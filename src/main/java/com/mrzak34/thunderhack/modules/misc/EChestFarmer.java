package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.events.EventPostSync;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InteractionUtil;
import com.mrzak34.thunderhack.util.SilentRotationUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EChestFarmer extends Module {
    private final Setting<Integer> range = this.register(new Setting<>("Range", 2, 1, 3));
    private final Setting<Integer> bd = this.register(new Setting<Integer>("BreakDelay", 4000, 0, 5000));
    private final Timer timer = new Timer();
    private final Timer breakTimer = new Timer();
    private InteractionUtil.Placement placement = null;
    public EChestFarmer() {
        super("EChestFarmer", "афк фарм обсы", Module.Category.MISC);
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    @Override
    public void onEnable() {
        placement = null;
        breakTimer.reset();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventSync event) {

        placement = null;
        if (event.isCanceled() || !InteractionUtil.canPlaceNormally()) return;

        BlockPos closestEChest = getSphere(new BlockPos(mc.player), range.getValue(), range.getValue(), false, true, 0).stream()
                .filter(pos -> mc.world.getBlockState(pos).getBlock() instanceof BlockEnderChest)
                .min(Comparator.comparing(pos -> mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)))
                .orElse(null);

        if (closestEChest != null) {
            if (breakTimer.passedMs(bd.getValue())) {
                boolean holdingPickaxe = mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;

                if (!holdingPickaxe) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.player.inventory.getStackInSlot(i);

                        if (stack.isEmpty()) {
                            continue;
                        }

                        if (stack.getItem() == Items.DIAMOND_PICKAXE) {
                            holdingPickaxe = true;
                            mc.player.inventory.currentItem = i;
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                            break;
                        }
                    }
                }

                if (!holdingPickaxe) {
                    return;
                }

                EnumFacing facing = mc.player.getHorizontalFacing().getOpposite();

                SilentRotationUtil.lookAtVector(new Vec3d(closestEChest.getX() + 0.5 + facing.getDirectionVec().getX() * 0.5,
                        closestEChest.getY() + 0.5 + facing.getDirectionVec().getY() * 0.5,
                        closestEChest.getZ() + 0.5 + facing.getDirectionVec().getZ() * 0.5));

                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, closestEChest, facing));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, closestEChest, facing));
                breakTimer.reset();
            }
        } else if (timer.passedMs(350)) {
            timer.reset();
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
                final ItemBlock block = (ItemBlock) mc.player.getHeldItemMainhand().getItem();
                if (block.getBlock() != Blocks.ENDER_CHEST) {
                    if (!changeToEChest()) return;
                }
            } else {
                if (!changeToEChest()) return;
            }

            for (BlockPos pos : getSphere(new BlockPos(mc.player), range.getValue(), range.getValue(), false, true, 0)) {
                InteractionUtil.Placement cPlacement = InteractionUtil.preparePlacement(pos, true, event);
                if (cPlacement != null) {
                    placement = cPlacement;
                }
            }
        }

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(EventPostSync event) {
        if (placement != null) {
            InteractionUtil.placeBlockSafely(placement, EnumHand.MAIN_HAND, false);
            breakTimer.reset();
        }
    }

    private boolean changeToEChest() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            final ItemBlock block = (ItemBlock) stack.getItem();
            if (block.getBlock() == Blocks.ENDER_CHEST) {
                mc.player.inventory.currentItem = i;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                return true;
            }
        }

        return false;
    }

}
