package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.movement.ElytraFlight;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.HoleUtil;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.PlacementUtil;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



public class HoleFill extends Module {
    public HoleFill() {
        super("HoleFill", "HoleFill", Category.COMBAT, true, false, false);
    }

    private  Setting<Mode> mode =register( new Setting<>("Mode", Mode.Web));
    private enum Mode {
        Obby, Echest, Web,Plate,Skull,Both
    }


    private  Setting<Integer> bpc = register(new Setting<>("BPS", 2, 1, 5));
    private  Setting<Integer> retryDelay = register(new Setting<>("RetryDelay", 10, 0, 50));
    private  Setting<Integer> placeDelay = register(new Setting<>("Delay", 2, 0, 10));

    public Setting<Float> range = this.register(new Setting<>("Range", 4f, 0f, 10f));
    public Setting<Float> playerRange = this.register(new Setting<>("PlayerRange", 3f, 1f, 6f));

    public Setting<Boolean> doubleHole = this.register(new Setting<>("DoubleHole", true));
    public Setting<Boolean> SilentSwitch = this.register(new Setting<>("SilentSwitch", true));
    public Setting<Boolean> disableOnFinish = this.register(new Setting<>("Disable", true));
    public Setting<Boolean> autoSwitch = this.register(new Setting<>("Switch", true));
    public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public Setting<Boolean> onlyPlayer = this.register(new Setting<>("Onl Player", true));


    private int delayTicks = 0;
    private int oldHandEnable = -1;
    private boolean activedOff;
    private int obbySlot;
    boolean hasPlaced;
    int oldslot = 1;


    private final HashMap<BlockPos, Integer> recentPlacements = new HashMap<>();

    public void onEnable() {
        if (autoSwitch.getValue() && mc.player != null) {
            oldHandEnable = mc.player.inventory.currentItem;
        }
        obbySlot = findObsidianSlot();
    }

    private final ArrayList<EnumFacing> exd = new ArrayList<EnumFacing>() {
        {
            add(EnumFacing.DOWN);
            add(EnumFacing.UP);
        }
    };

    public void onDisable() {
        if (autoSwitch.getValue() && mc.player != null && oldHandEnable != -1) {
            mc.player.inventory.currentItem = oldHandEnable;
        }
        recentPlacements.clear();
    }

    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        recentPlacements.replaceAll(((blockPos, integer) -> integer + 1));
        recentPlacements.values().removeIf(integer -> integer > retryDelay.getValue() * 2);

        if (delayTicks <= placeDelay.getValue() * 2) {
            delayTicks++;
            return;
        }

        if (obbySlot == 9) {
            if (!(mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return;
            }
        }


        List<BlockPos> holePos = new ArrayList<>(findHoles());
        holePos.removeAll(recentPlacements.keySet());

        AtomicInteger placements = new AtomicInteger();
        holePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.distanceSq((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).collect(Collectors.toList());
        List<EntityPlayer> listPlayer = new ArrayList<>(mc.world.playerEntities);
        listPlayer.removeIf(player -> (player.getName().equals(mc.player.getName()) || Thunderhack.friendManager.isFriend(player.getName()) || player.isDead || player.getName().length() == 0) || (!onlyPlayer.getValue() || mc.player.getDistance(player) > 6 + playerRange.getValue()));
        hasPlaced = false;
        holePos.removeIf(placePos -> {
            if (placements.get() >= bpc.getValue()) {
                return false;
            }

            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos)).stream().anyMatch(entity -> entity instanceof EntityPlayer)) {
                return true;
            }

            boolean output = false;

            if (isHoldingRightBlock(mc.player.inventory.currentItem, mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem())  || SilentSwitch.getValue()) {
                // Player range
                boolean found = false;
                if (onlyPlayer.getValue()) {
                    for (EntityPlayer player : listPlayer) {
                        if (player.getDistanceSqToCenter(placePos) < playerRange.getValue() * 2) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        return false;
                }
                if (placeBlock(placePos)) {
                    placements.getAndIncrement();
                    output = true;
                    delayTicks = 0;

                }
                recentPlacements.put(placePos, 0);
            }

            return output;
        });

        if (hasPlaced)
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));

        if (disableOnFinish.getValue() && holePos.size() == 0) {
            disable();
        }
    }

    private boolean placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (autoSwitch.getValue() || SilentSwitch.getValue()) {
            int newHand = findRightBlock();

            if (newHand != -1) {
                if (mc.player.inventory.currentItem != newHand) {
                    if (SilentSwitch.getValue()) {
                        if (!hasPlaced) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(newHand));
                            hasPlaced = true;
                            oldslot = mc.player.inventory.currentItem;
                        }
                    } else {
                        mc.player.inventory.currentItem = newHand;
                        mc.playerController.syncCurrentPlayItem();
                    }
                }
            } else {
                return false;
            }
        }
        return mode.getValue() == Mode.Skull ? PlacementUtil.place(pos, handSwing, rotate.getValue(), exd) : PlacementUtil.place(pos, handSwing, rotate.getValue(), !SilentSwitch.getValue());
    }

    private List<BlockPos> findHoles() {
        NonNullList<BlockPos> holes = NonNullList.create();

        //from old HoleFill module, really good way to do this
        List<BlockPos> blockPosList = getSphere(mc.player.getPosition(), range.getValue(), range.getValue().intValue(), false, true, 0);

        blockPosList.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                AxisAlignedBB centreBlocks = holeInfo.getCentre();

                if (centreBlocks == null)
                    return;

                if (doubleHole.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                    holes.add(pos);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    holes.add(pos);
                }
            }
        });

        if (holes.isEmpty() && disableOnFinish.getValue())
            disable();

        return holes;
    }

    private int findRightBlock() {
        switch (mode.getValue()) {
            case Both: {
                int newHand = InventoryUtil.findFirstBlockSlot(BlockObsidian.class, 0, 8);
                if (newHand == -1) return InventoryUtil.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
                else return newHand;
            }
            case Obby: {
                return InventoryUtil.findFirstBlockSlot(BlockObsidian.class, 0, 8);
            }
            case Echest: {
                return InventoryUtil.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
            }
            case Web: {
                return InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
            }
            case Plate: {
                return InventoryUtil.findFirstBlockSlot(BlockPressurePlate.class, 0, 8);
            }
            case Skull: {
               // return InventoryUtil.findSkullSlot(false, false);
            }
            default: {
                return -1;
            }
        }
    }

    private Boolean isHoldingRightBlock(int hand, Item item) {
        if (hand == -1) return false;

        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();

            switch (mode.getValue()) {
                case Both: {
                    return block instanceof BlockObsidian || block instanceof BlockEnderChest;
                }
                case Obby: {
                    return block instanceof BlockObsidian;
                }
                case Echest: {
                    return block instanceof BlockEnderChest;
                }
                case Web: {
                    return block instanceof BlockWeb;
                }

                case Plate: {
                    return block instanceof BlockPressurePlate;
                }
                default: {
                    return false;
                }
            }
        }

        return false;
    }


    public static int findObsidianSlot() {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;


        for (int i = 0; i < 9; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float radius, int height, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int locX = loc.getX();
        int locY = loc.getY();
        int locZ = loc.getZ();

        for (int x = locX - (int) radius; x <= locX + radius; x++) {
            for (int z = locZ - (int) radius; z <= locZ + radius; z++) {
                for (int y = (sphere ? locY - (int) radius : locY); y < (sphere ? locY + radius : locY + height); y++) {
                    double dist = (locX - x) * (locX - x) + (locZ - z) * (locZ - z) + (sphere ? (locY - y) * (locY - y) : 0);
                    if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }

        return circleBlocks;
    }
}
