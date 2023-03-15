package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.movement.PacketFly;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


public class AutoTrap
        extends Module {


    public static ConcurrentHashMap<BlockPos, Long> shiftedBlocks = new ConcurrentHashMap<>();
    public final Setting<ColorSetting> Color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<Float> placeRange = this.register(new Setting<>("TargetRange", 4.5f, 1f, 16f));
    private final Setting<Boolean> top = this.register(new Setting<>("Top", true));
    private final Setting<Boolean> piston = this.register(new Setting<>("Piston", false));
    private final Setting<SubBind> self = this.register(new Setting<>("Self", new SubBind(Keyboard.KEY_NONE)));
    Timer renderTimer = new Timer();
    private final Setting<Integer> actionShift = this.register(new Setting<>("ActionShift", 3, 1, 8));
    private final Setting<Integer> actionInterval = this.register(new Setting<>("ActionInterval", 0, 0, 10));
    private final Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    private final Setting<Boolean> toggelable = this.register(new Setting<>("DisableWhenDone", false));
    private int itemSlot;
    private BlockPos renderPos;
    private int tickCounter = 0;
    private BlockPos playerPos = null;
    private InteractionUtil.Placement placement;
    private InteractionUtil.Placement lastPlacement;
    private final Timer lastPlacementTimer = new Timer();

    public AutoTrap() {
        super("AutoTrap", "Трапит ньюфагов", Module.Category.COMBAT);
    }

    @Override
    public void onEnable() {

        if (mc.player == null || mc.world == null) {
            this.toggle();
            return;
        }

        renderPos = null;
        playerPos = null;
        placement = null;
        lastPlacement = null;
        tickCounter = actionInterval.getValue();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion event) {
        if (placement != null) {
            lastPlacement = placement;
            lastPlacementTimer.reset();
        }
        placement = null;
        playerPos = null;

        int ping = CrystalUtils.ping();

        shiftedBlocks.forEach((pos, time) -> {
            if (System.currentTimeMillis() - time > ping + 100) {
                shiftedBlocks.remove(pos);
            }
        });

        if (event.isCanceled() || !InteractionUtil.canPlaceNormally(rotate.getValue())) return;

        if (strict.getValue() && (!mc.player.onGround || !mc.player.collidedVertically)) return;

        if (Thunderhack.moduleManager.getModuleByClass(PacketFly.class).isEnabled()) return;

        if (tickCounter < actionInterval.getValue()) {
            tickCounter++;
        }

        int slot = getBlockSlot();
        if (slot == -1) {
            Command.sendMessage("No Obby Found!");
            toggle();
            return;
        }
        itemSlot = slot;

        EntityPlayer nearestPlayer = getNearestTarget();

        if (nearestPlayer == null) return;

        if (tickCounter < actionInterval.getValue()) {
            if (lastPlacement != null && !lastPlacementTimer.passedMs(650)) {
                mc.player.rotationPitch = (lastPlacement.getPitch());
                mc.player.rotationYaw = (lastPlacement.getYaw());
            }
            return;
        }

        playerPos = new BlockPos(nearestPlayer.posX, nearestPlayer.posY, nearestPlayer.posZ);

        BlockPos firstPos = getNextPos(playerPos);

        if (firstPos != null) {
            placement = InteractionUtil.preparePlacement(firstPos, rotate.getValue(), event);
            if (placement != null) {
                shiftedBlocks.put(firstPos, System.currentTimeMillis());
                tickCounter = 0;
                renderPos = firstPos;
                renderTimer.reset();
            }
        } else {
            if (toggelable.getValue()) {
                toggle();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerPost(EventPostMotion event) {

        if (placement != null && playerPos != null && itemSlot != -1) {
            boolean changeItem = mc.player.inventory.currentItem != itemSlot;
            int startingItem = mc.player.inventory.currentItem;

            if (changeItem) {
                mc.player.inventory.currentItem = itemSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(itemSlot));
            }

            boolean isSprinting = mc.player.isSprinting();
            boolean shouldSneak = BlockUtils.shouldSneakWhileRightClicking(placement.getNeighbour());

            if (isSprinting) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            if (shouldSneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            InteractionUtil.placeBlock(placement, EnumHand.MAIN_HAND, true);

            int extraBlocks = 0;
            while (extraBlocks < actionShift.getValue() - 1) {
                BlockPos nextPos = getNextPos(playerPos);
                if (nextPos != null) {
                    InteractionUtil.Placement nextPlacement = InteractionUtil.preparePlacement(nextPos, rotate.getValue(), true);
                    if (nextPlacement != null) {
                        placement = nextPlacement;
                        shiftedBlocks.put(nextPos, System.currentTimeMillis());
                        InteractionUtil.placeBlock(placement, EnumHand.MAIN_HAND, true);
                        renderPos = nextPos;
                        renderTimer.reset();
                        extraBlocks++;
                    } else {
                        break;
                    }
                } else {
                    if (toggelable.getValue()) {
                        toggle();
                        if (changeItem) {
                            mc.player.inventory.currentItem = startingItem;
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(startingItem));
                        }
                        return;
                    }
                    break;
                }
            }

            if (shouldSneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            if (isSprinting) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }

            if (changeItem) {
                mc.player.inventory.currentItem = startingItem;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(startingItem));
            }
        }
    }

    private boolean canPlaceBlock(BlockPos pos, boolean strictDirection) {
        return InteractionUtil.canPlaceBlock(pos, strictDirection) && !shiftedBlocks.containsKey(pos);
    }

    private boolean pistonCheck(BlockPos facePos, EnumFacing facing) {
        PistonAura pistonAura = Thunderhack.moduleManager.getModuleByClass(PistonAura.class);
        if (pistonAura.facePos != null) {
            return !pistonAura.faceOffset.equals(facing);
        } else {
            pistonAura.evaluateTarget(facePos);
            if (pistonAura.facePos != null) {
                if (pistonAura.faceOffset.equals(facing)) {
                    resetPA(pistonAura);
                    return false;
                }
                resetPA(pistonAura);
            }
        }
        return true;
    }

    private void resetPA(PistonAura pistonAura) {
        pistonAura.facePos = null;
        pistonAura.faceOffset = null;
        pistonAura.pistonOffset = null;
        pistonAura.pistonNeighbour = null;
        pistonAura.crystalPos = null;
    }

    private BlockPos getNextPos(BlockPos playerPos) {
        for (EnumFacing enumFacing : EnumFacing.HORIZONTALS) {
            BlockPos furthestBlock = null;
            double furthestDistance = 0D;
            if (canPlaceBlock(playerPos.offset(enumFacing).down(), true)) {
                BlockPos tempBlock = playerPos.offset(enumFacing).down();
                double tempDistance = mc.player.getDistance(tempBlock.getX() + 0.5, tempBlock.getY() + 0.5, tempBlock.getZ() + 0.5);
                if (tempDistance >= furthestDistance) {
                    furthestBlock = tempBlock;
                    furthestDistance = tempDistance;
                }
            }
            if (furthestBlock != null) return furthestBlock;
        }

        for (EnumFacing enumFacing : EnumFacing.HORIZONTALS) {
            BlockPos furthestBlock = null;
            double furthestDistance = 0D;
            if (canPlaceBlock(playerPos.offset(enumFacing), false)) {
                BlockPos tempBlock = playerPos.offset(enumFacing);
                double tempDistance = mc.player.getDistance(tempBlock.getX() + 0.5, tempBlock.getY() + 0.5, tempBlock.getZ() + 0.5);
                if (tempDistance >= furthestDistance) {
                    furthestBlock = tempBlock;
                    furthestDistance = tempDistance;
                }
            }
            if (furthestBlock != null) return furthestBlock;
        }

        for (EnumFacing enumFacing : EnumFacing.HORIZONTALS) {
            BlockPos furthestBlock = null;
            double furthestDistance = 0D;
            if (canPlaceBlock(playerPos.up().offset(enumFacing), false)) {
                if (!piston.getValue() || pistonCheck(playerPos.up(), enumFacing)) {
                    BlockPos tempBlock = playerPos.up().offset(enumFacing);
                    double tempDistance = mc.player.getDistance(tempBlock.getX() + 0.5, tempBlock.getY() + 0.5, tempBlock.getZ() + 0.5);
                    if (tempDistance >= furthestDistance) {
                        furthestBlock = tempBlock;
                        furthestDistance = tempDistance;
                    }
                }
            }
            if (furthestBlock != null) return furthestBlock;
        }

        if (top.getValue()) {
            Block baseBlock = mc.world.getBlockState(playerPos.up().up()).getBlock();
            if (baseBlock instanceof BlockAir || baseBlock instanceof BlockLiquid) {
                if (canPlaceBlock(playerPos.up().up(), false)) {
                    return playerPos.up().up();
                } else {
                    BlockPos offsetPos = playerPos.up().up().offset(EnumFacing.byHorizontalIndex(MathHelper.floor((double) (mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3));
                    if (canPlaceBlock(offsetPos, false)) {
                        return offsetPos;
                    }
                }
            }
        }

        return null;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) {
            return;
        }

        if (renderPos != null && !renderTimer.passedMs(500)) {
            RenderUtil.drawBlockOutline(renderPos, Color.getValue().getColorObject(), 0.3f, true, 0);
        }
    }

    private int getBlockSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    slot = i;
                    break;
                }
            }
        }

        return slot;
    }

    private EntityPlayer getNearestTarget() {
        Stream<EntityPlayer> stream = mc.world.playerEntities.stream();
        return stream
                .filter(e -> e != mc.player && e != mc.getRenderViewEntity())
                .filter(e -> !Thunderhack.friendManager.isFriend(e.getName()))
                .filter(e -> mc.player.getDistance(e) < Math.max(placeRange.getValue() - 1.0F, 1.0F))
                .filter(this::isValidBase)
                .min(Comparator.comparing(e -> mc.player.getDistance(e)))
                .orElse(PlayerUtils.isKeyDown(self.getValue().getKey()) ? mc.player : null);
    }

    private boolean isValidBase(EntityPlayer player) {
        BlockPos basePos = new BlockPos(player.posX, player.posY, player.posZ).down();

        Block baseBlock = mc.world.getBlockState(basePos).getBlock();

        return !(baseBlock instanceof BlockAir) && !(baseBlock instanceof BlockLiquid);
    }


}

