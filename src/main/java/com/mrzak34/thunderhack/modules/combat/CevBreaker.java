package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayerSP;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.movement.PacketFly;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


public class CevBreaker extends Module {

    public static ConcurrentHashMap<BlockPos, Long> shiftedBlocks = new ConcurrentHashMap<>();
    public final Setting<ColorSetting> Color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    public final Setting<ColorSetting> Color2 = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<Integer> pickTickSwitch = this.register(new Setting<>("Pick Switch Destroy", 0, 0, 20));
    private final Setting<Float> placeRange = this.register(new Setting<>("TargetRange", 4.5f, 1f, 16f));
    public boolean startBreak = false;
    boolean broke = false;
    Timer renderTimer = new Timer();
    private final Setting<Mode> mode = register(new Setting("BreakMode", Mode.TripleP));
    private final Setting<Integer> crysDelay = this.register(new Setting<>("CrysDelay", 200, 1, 1000));
    private final Setting<Integer> atttt = this.register(new Setting<>("AttackDelay", 200, 1, 1000));
    private final Setting<Integer> pausedelay = this.register(new Setting<>("PauseDelay", 300, 1, 1000));
    private final Setting<Integer> actionShift = this.register(new Setting<>("ActionShift", 3, 1, 8));
    private final Setting<Integer> actionInterval = this.register(new Setting<>("ActionInterval", 0, 0, 10));
    private final Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    private final Setting<Boolean> p1 = this.register(new Setting<>("PacketCrystal", true));
    private final Setting<Boolean> MStrict = this.register(new Setting<>("ModeStrict", true));
    private final Setting<Boolean> strictdirection = this.register(new Setting<>("StrictDirection", true));
    private int tick = 99;
    private int oldslot;
    private int wait = 50;
    private BlockPos lastBlock = null;
    private BlockPos continueBlock = null;
    private boolean pickStillBol = false;
    private EnumFacing direction;
    private final Timer attackTimer = new Timer();
    private final Timer cryTimer = new Timer();
    private int itemSlot;
    private BlockPos renderPos;
    private int tickCounter = 0;
    private BlockPos playerPos = null;
    private BlockPos toppos = null;
    private InteractionUtil.Placement placement;
    private InteractionUtil.Placement lastPlacement;
    private final Timer lastPlacementTimer = new Timer();
    private final Timer pausetimer = new Timer();
    public CevBreaker() {
        super("CevBreaker", "CevBreaker", Category.COMBAT);
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

    public static int getPicSlot() {
        int pic = -1;

        if (Util.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE) {
            pic = Util.mc.player.inventory.currentItem;
        }
        if (pic == -1) {
            for (int l = 0; l < 9; ++l) {
                if (Util.mc.player.inventory.getStackInSlot(l).getItem() == Items.DIAMOND_PICKAXE) {
                    pic = l;
                    break;
                }
            }
        }

        return pic;
    }

    @Override
    public void onEnable() {

        if (mc.player == null || mc.world == null) {
            this.toggle();
            return;
        }
        startBreak = false;
        renderPos = null;
        playerPos = null;
        placement = null;
        lastPlacement = null;
        tickCounter = actionInterval.getValue();
        lastBlock = null;
        tick = 99;
        wait = 50;
        continueBlock = null;
        pickStillBol = false;
        direction = null;
        broke = false;
    }

    @Override
    public void onDisable() {
        continueBlock = null;
        lastBlock = null;
    }

    @Override
    public void onUpdate() {
        if (mode.getValue() == Mode.DoubleP) {
            if (continueBlock != null) {
                if (BlockUtils.getBlockgs(continueBlock) instanceof BlockAir) {
                    broke = true;
                }
                if (!(BlockUtils.getBlockgs(continueBlock) instanceof BlockAir) && (broke)) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.playerController.onPlayerDamageBlock(continueBlock, EnumFacing.UP);
                    broke = false;
                }
            }
        }

        if (tick != 99) {
            if (tick++ >= wait) {
                if (oldslot != -1) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
                    mc.playerController.updateController();
                    if (lastBlock != null && direction != null)
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastBlock, direction));
                    oldslot = -1;
                }
                if (!pickStillBol) {
                    wait = 12;
                    tick = 0;
                    oldslot = InventoryUtil.getPicatHotbar();
                    pickStillBol = true;
                } else
                    tick = 99;
            }
        }
        mc.playerController.blockHitDelay = 0;
    }

    public void onBreakPacket() {

        if (mode.getValue() == Mode.Vanilla) return;
        if (mc.world == null || mc.player == null) return;
        if (toppos == null) return;

        if (mode.getValue() == Mode.DoubleP)
            continueBlock = toppos;

        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, toppos, handlePlaceRotation(toppos)));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, toppos, handlePlaceRotation(toppos)));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, toppos, handlePlaceRotation(toppos)));

        lastBlock = toppos;
        direction = handlePlaceRotation(toppos);
        oldslot = InventoryUtil.getPicatHotbar();
        tick = 0;
        wait = pickTickSwitch.getValue() + 50;
        pickStillBol = false;
    }

    public void onRender3D(Render3DEvent event) {
        if (renderPos != null && !renderTimer.passedMs(500)) {
            RenderUtil.drawBlockOutline(renderPos, Color2.getValue().getColorObject(), 0.3f, true, 0);
        }
        if (lastBlock != null) {
            RenderUtil.drawBlockOutline(lastBlock, new Color(175, 175, 255), 2f, false, 0);

            float prognum = ((((float) tick / pickTickSwitch.getValue() * 100) / Blocks.OBSIDIAN.blockHardness) * mc.world.getBlockState(lastBlock).getBlock().blockHardness);


            GlStateManager.pushMatrix();
            try {
                RenderUtil.glBillboardDistanceScaled((float) lastBlock.getX() + 0.5f, (float) lastBlock.getY() + 0.5f, (float) lastBlock.getZ() + 0.5f, mc.player, 1);
            } catch (Exception ignored) {
            }
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GL11.glColor4f(1, 1, 1, 1);
            mc.fontRenderer.drawStringWithShadow(String.valueOf(prognum), (int) -(mc.fontRenderer.getStringWidth(String.valueOf(prognum)) / 2.0D), -4, -1);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        if (toppos != null) {
            EntityEnderCrystal ent = searchCrystal(toppos);
            if (ent != null && attackTimer.passedMs(atttt.getValue())) {
                RenderUtil.drawBoxESP(toppos, new Color(0x25BB02), false, new Color(0x2FFF00), 0.5f, true, true, 170, false, 0);
            } else {
                RenderUtil.drawBoxESP(toppos, new Color(0xBB0202), false, new Color(0xFF0000), 0.5f, true, true, 170, false, 0);
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion event) {

        if (playerPos != null) {
            if (canPlaceCrystal(playerPos.up().up()) && cryTimer.passedMs(crysDelay.getValue()) && searchCrystal(playerPos.up().up()) == null) {
                placeCrystal(playerPos.up().up(), handlePlaceRotation(playerPos.up().up()));
                setPickSlot();
                cryTimer.reset();
            } else if (canBreakCrystal(playerPos.up().up())) {
                EntityEnderCrystal ent = searchCrystal(playerPos.up().up());
                if (ent != null && attackTimer.passedMs(atttt.getValue())) {
                    mc.playerController.attackEntity(mc.player, ent);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    startBreak = true;
                    attackTimer.reset();
                    pausetimer.reset();
                }
            }
        }
        if (toppos != null) {
            if (mode.getValue() == Mode.Vanilla) {
                EntityEnderCrystal ent = searchCrystal(toppos);
                if (ent == null) {
                    placeCrystal(toppos, handlePlaceRotation(toppos));
                } else {
                    setPickSlot();
                    if (mc.world.getBlockState(toppos).getBlock() != Blocks.AIR) {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.playerController.onPlayerDamageBlock(toppos, handlePlaceRotation(toppos));
                    }
                }
            } else {
                if (!startBreak) {
                    onBreakPacket();
                    startBreak = true;
                }
            }
        }

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

        if (event.isCanceled()) return;

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
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(EventPostMotion event) {

        if (!pausetimer.passedMs(pausedelay.getValue())) {
            return;
        }
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
                    break;
                }
            }
            cryTimer.reset();
            if (shouldSneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            if (isSprinting) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
//
            //     if (changeItem) {
            //       mc.player.inventory.currentItem = startingItem;
            //        mc.player.connection.sendPacket(new CPacketHeldItemChange(startingItem));
            //    }

        }
    }

    private boolean canPlaceBlock(BlockPos pos, boolean strictDirection) {
        return InteractionUtil.canPlaceBlock(pos, strictDirection) && !shiftedBlocks.containsKey(pos);
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
                BlockPos tempBlock = playerPos.up().offset(enumFacing);
                double tempDistance = mc.player.getDistance(tempBlock.getX() + 0.5, tempBlock.getY() + 0.5, tempBlock.getZ() + 0.5);
                if (tempDistance >= furthestDistance) {
                    furthestBlock = tempBlock;
                    furthestDistance = tempDistance;
                }
            }
            if (furthestBlock != null) return furthestBlock;
        }

        Block baseBlock = mc.world.getBlockState(playerPos.up().up()).getBlock();
        if (baseBlock instanceof BlockAir || baseBlock instanceof BlockLiquid) {  //TODO TOP
            if (canPlaceBlock(playerPos.up().up(), false)) {
                toppos = playerPos.up().up();
                return playerPos.up().up();
            } else {
                BlockPos offsetPos = playerPos.up().up().offset(EnumFacing.byHorizontalIndex(MathHelper.floor((double) (mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3));
                if (canPlaceBlock(offsetPos, false)) {
                    return offsetPos;
                }
            }
        }
        return null;
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    private boolean canBreakCrystal(BlockPos blockPos) {
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return true;
            }
        } catch (Exception ignored) {
            return false;
        }
        return false;
    }

    public boolean setCrystalSlot() {
        int crystalSlot = CrystalUtils.getCrystalSlot();
        if (crystalSlot == -1) {
            return false;
        } else if (mc.player.inventory.currentItem != crystalSlot) {
            mc.player.inventory.currentItem = crystalSlot;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(crystalSlot));
        }
        return true;
    }

    public boolean setPickSlot() {
        int pickslot = getPicSlot();
        if (pickslot == -1) {
            return false;
        } else if (mc.player.inventory.currentItem != pickslot) {
            mc.player.inventory.currentItem = pickslot;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(pickslot));
        }
        return true;
    }

    public boolean placeCrystal(BlockPos pos, EnumFacing facing) {
        if (pos != null) {
            if (!setCrystalSlot()) return false;
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL)
                return false;
            BlockUtils.rightClickBlock(pos, mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), EnumHand.MAIN_HAND, facing, true);
            return true;
        }
        return false;
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
                .orElse(null);
    }

    private boolean isValidBase(EntityPlayer player) {
        BlockPos basePos = new BlockPos(player.posX, player.posY, player.posZ).down();

        Block baseBlock = mc.world.getBlockState(basePos).getBlock();

        return !(baseBlock instanceof BlockAir) && !(baseBlock instanceof BlockLiquid);
    }

    public EnumFacing handlePlaceRotation(BlockPos pos) {
        if (pos == null || mc.player == null) {
            return null;
        }
        EnumFacing facing = null;
        Vec3d placeVec = null;
        double[] placeRotation = null;

        double increment = 0.45D;
        double start = 0.05D;
        double end = 0.95D;

        Vec3d eyesPos = new Vec3d(mc.player.posX, (mc.player.getEntityBoundingBox().minY + mc.player.getEyeHeight()), mc.player.posZ);

        for (double xS = start; xS <= end; xS += increment) {
            for (double yS = start; yS <= end; yS += increment) {
                for (double zS = start; zS <= end; zS += increment) {
                    Vec3d posVec = (new Vec3d(pos)).add(xS, yS, zS);

                    double distToPosVec = eyesPos.distanceTo(posVec);
                    double diffX = posVec.x - eyesPos.x;
                    double diffY = posVec.y - eyesPos.y;
                    double diffZ = posVec.z - eyesPos.z;
                    double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

                    double[] tempPlaceRotation = new double[]{MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F), MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))};

                    float yawCos = MathHelper.cos((float) (-tempPlaceRotation[0] * 0.017453292F - 3.1415927F));
                    float yawSin = MathHelper.sin((float) (-tempPlaceRotation[0] * 0.017453292F - 3.1415927F));
                    float pitchCos = -MathHelper.cos((float) (-tempPlaceRotation[1] * 0.017453292F));
                    float pitchSin = MathHelper.sin((float) (-tempPlaceRotation[1] * 0.017453292F));

                    Vec3d rotationVec = new Vec3d((yawSin * pitchCos), pitchSin, (yawCos * pitchCos));
                    Vec3d eyesRotationVec = eyesPos.add(rotationVec.x * distToPosVec, rotationVec.y * distToPosVec, rotationVec.z * distToPosVec);

                    RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(eyesPos, eyesRotationVec, false, true, false);
                    if ((rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && rayTraceResult.getBlockPos().equals(pos))) {
                        Vec3d currVec = posVec;
                        double[] currRotation = tempPlaceRotation;

                        if (strictdirection.getValue()) {
                            if (placeVec != null && placeRotation != null && ((rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) || facing == null)) {
                                if (mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0).distanceTo(currVec) < mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0).distanceTo(placeVec)) {
                                    placeVec = currVec;
                                    placeRotation = currRotation;
                                    if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                        facing = rayTraceResult.sideHit;
                                    }
                                }
                            } else {
                                placeVec = currVec;
                                placeRotation = currRotation;
                                if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                    facing = rayTraceResult.sideHit;
                                }
                            }
                        } else {
                            if (placeVec != null && placeRotation != null && ((rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) || facing == null)) {
                                if (Math.hypot((((currRotation[0] - ((IEntityPlayerSP) mc.player).getLastReportedYaw()) % 360.0F + 540.0F) % 360.0F - 180.0F), (currRotation[1] - ((IEntityPlayerSP) mc.player).getLastReportedPitch())) <
                                        Math.hypot((((placeRotation[0] - ((IEntityPlayerSP) mc.player).getLastReportedYaw()) % 360.0F + 540.0F) % 360.0F - 180.0F), (placeRotation[1] - ((IEntityPlayerSP) mc.player).getLastReportedPitch()))) {
                                    placeVec = currVec;
                                    placeRotation = currRotation;
                                    if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                        facing = rayTraceResult.sideHit;
                                    }
                                }
                            } else {
                                placeVec = currVec;
                                placeRotation = currRotation;
                                if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                    facing = rayTraceResult.sideHit;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (MStrict.getValue()) {
            if (placeRotation != null && facing != null) {
                return facing;
            } else {
                for (double xS = start; xS <= end; xS += increment) {
                    for (double yS = start; yS <= end; yS += increment) {
                        for (double zS = start; zS <= end; zS += increment) {
                            Vec3d posVec = (new Vec3d(pos)).add(xS, yS, zS);

                            double distToPosVec = eyesPos.distanceTo(posVec);
                            double diffX = posVec.x - eyesPos.x;
                            double diffY = posVec.y - eyesPos.y;
                            double diffZ = posVec.z - eyesPos.z;
                            double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

                            double[] tempPlaceRotation = new double[]{MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F), MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))};

                            float yawCos = MathHelper.cos((float) (-tempPlaceRotation[0] * 0.017453292F - 3.1415927F));
                            float yawSin = MathHelper.sin((float) (-tempPlaceRotation[0] * 0.017453292F - 3.1415927F));
                            float pitchCos = -MathHelper.cos((float) (-tempPlaceRotation[1] * 0.017453292F));
                            float pitchSin = MathHelper.sin((float) (-tempPlaceRotation[1] * 0.017453292F));

                            Vec3d rotationVec = new Vec3d((yawSin * pitchCos), pitchSin, (yawCos * pitchCos));
                            Vec3d eyesRotationVec = eyesPos.add(rotationVec.x * distToPosVec, rotationVec.y * distToPosVec, rotationVec.z * distToPosVec);

                            RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(eyesPos, eyesRotationVec, false, true, true);
                            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                Vec3d currVec = posVec;
                                double[] currRotation = tempPlaceRotation;

                                if (strictdirection.getValue()) {
                                    if (placeVec != null && placeRotation != null && ((rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) || facing == null)) {
                                        if (mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0).distanceTo(currVec) < mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0).distanceTo(placeVec)) {
                                            placeVec = currVec;
                                            placeRotation = currRotation;
                                            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                                facing = rayTraceResult.sideHit;
                                            }
                                        }
                                    } else {
                                        placeVec = currVec;
                                        placeRotation = currRotation;
                                        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                            facing = rayTraceResult.sideHit;
                                        }
                                    }
                                } else {
                                    if (placeVec != null && placeRotation != null && ((rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) || facing == null)) {
                                        if (Math.hypot((((currRotation[0] - ((IEntityPlayerSP) mc.player).getLastReportedYaw()) % 360.0F + 540.0F) % 360.0F - 180.0F), (currRotation[1] - ((IEntityPlayerSP) mc.player).getLastReportedPitch())) <
                                                Math.hypot((((placeRotation[0] - ((IEntityPlayerSP) mc.player).getLastReportedYaw()) % 360.0F + 540.0F) % 360.0F - 180.0F), (placeRotation[1] - ((IEntityPlayerSP) mc.player).getLastReportedPitch()))) {
                                            placeVec = currVec;
                                            placeRotation = currRotation;
                                            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                                facing = rayTraceResult.sideHit;
                                            }
                                        }
                                    } else {
                                        placeVec = currVec;
                                        placeRotation = currRotation;
                                        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                                            facing = rayTraceResult.sideHit;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (facing != null) {
                return facing;
            }
        }

        if ((double) pos.getY() > mc.player.posY + (double) mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }

    public enum Mode {
        Packet, DoubleP, TripleP, Vanilla, StrictFast
    }
}
