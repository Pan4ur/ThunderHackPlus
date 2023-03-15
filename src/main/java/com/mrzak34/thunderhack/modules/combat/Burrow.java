package com.mrzak34.thunderhack.modules.combat;

import com.google.common.collect.Sets;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Burrow extends Module {
    public static final Set<Block> BAD_BLOCKS = Sets.newHashSet(
            Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND,
            Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER,
            Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    /**
     * A Set of all Shulkers.
     */
    public static final Set<Block> SHULKERS = Sets.newHashSet(
            Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    protected final Timer scaleTimer = new Timer();
    protected final Timer timer = new Timer();
    public Setting<Float> vClip = register(new Setting("V-Clip", Float.valueOf(-9.0F), Float.valueOf(-256.0F), Float.valueOf(256.0F)));
    public Setting<Float> minDown = register(new Setting("Min-Down", Float.valueOf(3.0F), Float.valueOf(0.0F), Float.valueOf(1337.0F)));
    public Setting<Float> maxDown = register(new Setting("Max-Down", Float.valueOf(10.0F), Float.valueOf(0.0F), Float.valueOf(1337.0F))); // макс даун хахахахахахаха
    public Setting<Float> minUp = register(new Setting("Min-Up", Float.valueOf(3.0F), Float.valueOf(0.0F), Float.valueOf(1337.0F)));
    public Setting<Float> maxUp = register(new Setting("Max-Up", Float.valueOf(10.0F), Float.valueOf(0.0F), Float.valueOf(1337.0F)));
    public Setting<Float> scaleFactor = register(new Setting("Scale-Factor", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(10.0F)));
    public Setting<Integer> scaleDelay = this.register(new Setting<>("Scale-Delay", 250, 0, 1000));
    public Setting<Integer> cooldown = this.register(new Setting<>("Cooldown", 500, 0, 500));
    public Setting<Integer> delay = this.register(new Setting<>("Delay", 100, 0, 1000));
    public Setting<Boolean> scaleDown = this.register(new Setting<>("Scale-Down", false));
    public Setting<Boolean> scaleVelocity = this.register(new Setting<>("Scale-Velocity", false));
    public Setting<Boolean> scaleExplosion = this.register(new Setting<>("Scale-Explosion", false));
    public Setting<Boolean> attackBefore = this.register(new Setting<>("Attack-Before", false));
    public Setting<Boolean> antiWeakness = this.register(new Setting<>("antiWeakness", false));
    public Setting<Boolean> attack = this.register(new Setting<>("Attack", false));
    public Setting<Boolean> deltaY = this.register(new Setting<>("Delta-Y", true));
    public Setting<Boolean> placeDisable = this.register(new Setting<>("PlaceDisable", false));
    public Setting<Boolean> wait = this.register(new Setting<>("Wait", true));
    public Setting<Boolean> highBlock = this.register(new Setting<>("HighBlock", false));
    public Setting<Boolean> evade = this.register(new Setting<>("Evade", false));
    public Setting<Boolean> noVoid = this.register(new Setting<>("NoVoid", false));
    public Setting<Boolean> conflict = this.register(new Setting<>("Conflict", true));
    public Setting<Boolean> onGround = this.register(new Setting<>("OnGround", true));
    public Setting<Boolean> allowUp = this.register(new Setting<>("Allow-Up", false));
    public Setting<Boolean> beacon = this.register(new Setting<>("Beacon", false));
    public Setting<Boolean> echest = this.register(new Setting<>("E-Chest", false));
    public Setting<Boolean> anvil = this.register(new Setting<>("Anvil", false));
    public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public Setting<Boolean> discrete = this.register(new Setting<>("Discrete", true));
    public Setting<Boolean> air = this.register(new Setting<>("Air", false));
    public Setting<Boolean> fallback = this.register(new Setting<>("Fallback", true));
    public Setting<Boolean> skipZero = this.register(new Setting<>("SkipZero", true));
    protected double motionY;
    protected BlockPos startPos;
    private volatile double last_x;
    private volatile double last_y;
    private volatile double last_z;
    private final Setting<OffsetMode> offsetMode = register(new Setting("Mode", OffsetMode.Smart));
    public Burrow() {
        super("Burrow", "Ставит в тебя блок", Category.COMBAT);
    }

    public static void send(Packet<?> packet) {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection != null) {
            connection.sendPacket(packet);
        }
    }

    public static void swingPacket(EnumHand hand) {
        Objects.requireNonNull(
                mc.getConnection()).sendPacket(new CPacketAnimation(hand));
    }

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public static void swing(int slot) {
        mc.player.connection.sendPacket(
                new CPacketAnimation(getHand(slot)));
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing, Entity from) {
        return getRotations(pos, facing, from, mc.world, mc.world.getBlockState(pos));
    }

    public static float[] getRotations(BlockPos pos,
                                       EnumFacing facing,
                                       Entity from,
                                       IBlockAccess world,
                                       IBlockState state) {
        AxisAlignedBB bb = state.getBoundingBox(world, pos);

        double x = pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;

        if (facing != null) {
            x += facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }

        return getRotations(x, y, z, from);
    }

    public static float[] getRotations(double x, double y, double z, Entity f) {
        return getRotations(x, y, z, f.posX, f.posY, f.posZ, f.getEyeHeight());
    }

    public static float[] getRotations(double x,
                                       double y,
                                       double z,
                                       double fromX,
                                       double fromY,
                                       double fromZ,
                                       float fromHeight) {
        double xDiff = x - fromX;
        double yDiff = y - (fromY + fromHeight);
        double zDiff = z - fromZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float prevYaw = mc.player.rotationYaw;
        float diff = yaw - prevYaw;

        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[]{prevYaw + diff, pitch};
    }

    public static void doRotation(float yaw,
                                  float pitch,
                                  boolean onGround) {
        mc.player.connection.sendPacket(rotation(yaw, pitch, onGround));
    }

    public static CPacketPlayer rotation(float yaw,
                                         float pitch,
                                         boolean onGround) {
        return new CPacketPlayer.Rotation(yaw, pitch, onGround);
    }

    public static void doY(Entity entity, double y, boolean onGround) {
        doPosition(entity.posX, y, entity.posZ, onGround);
    }

    public static void doPosition(double x,
                                  double y,
                                  double z,
                                  boolean onGround) {
        mc.player.connection.sendPacket(position(x, y, z, onGround));
    }

    public static CPacketPlayer position(double x, double y, double z) {
        return position(x, y, z, mc.player.onGround);
    }

    public static CPacketPlayer position(double x,
                                         double y,
                                         double z,
                                         boolean onGround) {
        return new CPacketPlayer.Position(x, y, z, onGround);
    }

    public static void doPosRot(double x,
                                double y,
                                double z,
                                float yaw,
                                float pitch,
                                boolean onGround) {
        mc.player.connection.sendPacket(
                positionRotation(x, y, z, yaw, pitch, onGround));
    }

    public static CPacketPlayer positionRotation(double x,
                                                 double y,
                                                 double z,
                                                 float yaw,
                                                 float pitch,
                                                 boolean onGround) {
        return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
    }

    public static void place(BlockPos on,
                             EnumFacing facing,
                             int slot,
                             float x,
                             float y,
                             float z) {
        try {
            place(on, facing, getHand(slot), x, y, z);
        } catch (Exception e) {
            Command.sendMessage("Failed to place the block");
        }
    }

    public static EnumHand getHand(int slot) {
        return slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public static void place(BlockPos on,
                             EnumFacing facing,
                             EnumHand hand,
                             float x,
                             float y,
                             float z) {
        try {
            mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(on, facing, hand, x, y, z));
        } catch (Exception exception) {
            Command.sendMessage("Failed to place the block");
        }
    }

    public static BlockPos getPosition(Entity entity) {
        return getPosition(entity, 0.0);
    }

    public static BlockPos getPosition(Entity entity, double yOffset) {
        double y = entity.posY + yOffset;
        if (entity.posY - Math.floor(entity.posY) > 0.5) {
            y = Math.ceil(entity.posY);
        }

        return new BlockPos(entity.posX, y, entity.posZ);
    }

    public static boolean canBreakWeakness(boolean checkStack) {
        if (!mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            return true;
        }

        int strengthAmp = 0;
        PotionEffect effect =
                mc.player.getActivePotionEffect(MobEffects.STRENGTH);

        if (effect != null) {
            strengthAmp = effect.getAmplifier();
        }

        if (strengthAmp >= 1) {
            return true;
        }

        return checkStack && canBreakWeakness(mc.player.getHeldItemMainhand());
    }

    public static boolean canBreakWeakness(ItemStack stack) {
        return stack.getItem() instanceof ItemSword;
    }

    public static boolean shouldSneak(BlockPos pos, boolean manager) {
        return shouldSneak(pos, mc.world, manager);
    }

    public static boolean shouldSneak(BlockPos pos,
                                      IBlockAccess provider,
                                      boolean manager) {
        return shouldSneak(provider.getBlockState(pos).getBlock(), manager);
    }

    public static boolean shouldSneak(Block block, boolean manager) {
        if (manager && mc.player.isSneaking()) {
            return false;
        }

        return BAD_BLOCKS.contains(block) || SHULKERS.contains(block);
    }

    public static float[] hitVecToPlaceVec(BlockPos pos, Vec3d hitVec) {
        float x = (float) (hitVec.x - pos.getX());
        float y = (float) (hitVec.y - pos.getY());
        float z = (float) (hitVec.z - pos.getZ());

        return new float[]{x, y, z};
    }

    public static RayTraceResult getRayTraceResultWithEntity(float yaw, float pitch, Entity from) {
        return getRayTraceResult(yaw, pitch, mc.playerController.getBlockReachDistance(), from);
    }

    public static RayTraceResult getRayTraceResult(float yaw, float pitch, float d, Entity from) {
        Vec3d vec3d = getEyePos(from);
        Vec3d lookVec = getVec3d(yaw, pitch);
        Vec3d rotations = vec3d.add(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        return Optional.ofNullable(
                        mc.world.rayTraceBlocks(vec3d, rotations, false, false, false))
                .orElseGet(() ->
                        new RayTraceResult(RayTraceResult.Type.MISS,
                                new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos.ORIGIN));
    }

    public static Vec3d getVec3d(float yaw, float pitch) {
        float vx = -MathHelper.sin(rad(yaw)) * MathHelper.cos(rad(pitch));
        float vz = MathHelper.cos(rad(yaw)) * MathHelper.cos(rad(pitch));
        float vy = -MathHelper.sin(rad(pitch));
        return new Vec3d(vx, vy, vz);
    }

    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }

    public static Vec3d getEyePos(Entity entity) {
        return new Vec3d(entity.posX, getEyeHeight(entity), entity.posZ);
    }

    public static double getEyeHeight() {
        return getEyeHeight(mc.player);
    }

    public static double getEyeHeight(Entity entity) {
        return entity.posY + entity.getEyeHeight();
    }

    public static int findAntiWeakness() {
        int slot = -1;
        for (int i = 8; i > -1; i--) {
            if (canBreakWeakness(
                    mc.player.inventory.getStackInSlot(i))) {
                slot = i;
                if (mc.player.inventory.currentItem == i) {
                    break;
                }
            }
        }

        return slot;
    }

    @Override
    public void onEnable() {
        timer.reset();
        if (mc.world == null || mc.player == null) {
            return;
        }
        startPos = getPlayerPos();
    }

    protected void attack(Packet<?> attacking, int slot) {
        if (slot != -1) {
            switchToHotbarSlot(slot, true);
        }

        send(attacking);
        swing(EnumHand.MAIN_HAND);
    }

    public void swing(EnumHand hand) {
        swingPacket(hand);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketExplosion) {
            if (scaleExplosion.getValue()) {
                motionY = ((SPacketExplosion) event.getPacket()).getMotionY();
                scaleTimer.reset();
            }
        }
        if (event.getPacket() instanceof SPacketExplosion) {
            if (scaleVelocity.getValue()) {
                return;
            }

            EntityPlayerSP playerSP = mc.player;
            if (playerSP != null) {
                motionY = ((SPacketExplosion) event.getPacket()).getMotionY() / 8000.0;
                scaleTimer.reset();
            }
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = event.getPacket();
            double x = packet.getX();
            double y = packet.getY();
            double z = packet.getZ();

            if (packet.getFlags()
                    .contains(SPacketPlayerPosLook.EnumFlags.X)) {
                x += mc.player.posX;
            }

            if (packet.getFlags()
                    .contains(SPacketPlayerPosLook.EnumFlags.Y)) {
                y += mc.player.posY;
            }

            if (packet.getFlags()
                    .contains(SPacketPlayerPosLook.EnumFlags.Z)) {
                z += mc.player.posZ;
            }

            last_x = MathHelper.clamp(x, -3.0E7, 3.0E7);
            last_y = y;
            last_z = MathHelper.clamp(z, -3.0E7, 3.0E7);
        }
    }

    @Override
    public void onUpdate() {
        // {
        // if (bypass.getValue()) {
        //      event.motionY = mc.player.getPosition().getY() - bypassOffset.getValue();
        //     //event.setOnGround(false);
        //}

        // }


        if (wait.getValue()) {
            BlockPos currentPos = getPlayerPos();
            if (!currentPos.equals(startPos)) {
                disable();
                return;
            }
        }

        if (isInsideBlock()) {
            return;
        }

        EntityPlayer rEntity = mc.player;

        BlockPos pos = getPosition(rEntity);
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            if (!wait.getValue())
                disable();
            return;
        }

        BlockPos posHead = getPosition(rEntity).up().up();
        if (!mc.world.getBlockState(posHead).getMaterial().isReplaceable()
                && wait.getValue()) {
            return;
        }

        CPacketUseEntity attacking = null;
        boolean crystals = false;
        float currentDmg = Float.MAX_VALUE;
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity != null && !mc.player.equals(entity) && /*!EntityUtil.isDead(entity)*/  entity.preventEntitySpawning) {
                if (entity instanceof EntityEnderCrystal && attack.getValue()) {
                    EntityUtil.attackEntity(entity, true, true);
                    crystals = true;
                    continue;
                }
                if (!wait.getValue())
                    disable();
                return;
            }
        }

        int weaknessSlot = -1;
        if (crystals) {
            if (attacking == null) {
                if (!wait.getValue())
                    disable();
                return;
            }

            if (!canBreakWeakness(true)) {
                if (!antiWeakness.getValue()
                        || cooldown.getValue() != 0
                        || (weaknessSlot = findAntiWeakness()) == -1) {
                    if (!wait.getValue())
                        disable();
                    return;
                }
            }
        }

        if (!allowUp.getValue()) {
            BlockPos upUp = pos.up(2);
            IBlockState upState = mc.world.getBlockState(upUp);
            if (upState.getMaterial().blocksMovement()) // Check if full BB?
            {
                if (!wait.getValue())
                    disable();
                return;
            }
        }

        int slot = anvil.getValue()
                ? InventoryUtil.findHotbarBlock(Blocks.ANVIL)
                : beacon.getValue()
                ? InventoryUtil.findHotbarBlock(Blocks.BEACON)
                : (echest.getValue()
                || mc.world.getBlockState(pos.down())
                .getBlock() == Blocks.ENDER_CHEST
                ? InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST)
                : InventoryUtil.findHotbarBlock(BlockObsidian.class));
        if (slot == -1) {
            Command.sendMessage("No Block found!");
            return;
        }

        EnumFacing f = BlockUtils.getFacing(pos);
        if (f == null) {
            if (!wait.getValue()) {
                disable();
            }

            return;
        }

        double y = applyScale(getY(rEntity, offsetMode.getValue()));
        if (Double.isNaN(y)) {
            return;
        }

        BlockPos on = pos.offset(f);
        float[] r =
                getRotations(on, f.getOpposite(), rEntity);
        RayTraceResult result =
                getRayTraceResultWithEntity(r[0], r[1], rEntity);

        float[] vec = hitVecToPlaceVec(on, result.hitVec);
        boolean sneaking = !shouldSneak(on, true);

        EntityPlayer finalREntity = rEntity;
        int finalWeaknessSlot = weaknessSlot;
        CPacketUseEntity finalAttacking = attacking;
        if (singlePlayerCheck(pos)) {
            if (!wait.getValue() || placeDisable.getValue())
                disable();
            return;
        }


        int lastSlot = mc.player.inventory.currentItem;
        if (attackBefore.getValue() && finalAttacking != null) {
            attack(finalAttacking, finalWeaknessSlot);
        }

        if (conflict.getValue() || rotate.getValue()) {
            if (rotate.getValue()) {
                if (finalREntity.getPositionVector().equals(getVec())) {
                    doRotation(r[0], r[1], true);
                } else {
                    doPosRot(finalREntity.posX,
                            finalREntity.posY,
                            finalREntity.posZ,
                            r[0],
                            r[1],
                            true);
                }
            } else {
                doPosition(finalREntity.posX,
                        finalREntity.posY,
                        finalREntity.posZ,
                        true);
            }
        }

        doY(
                finalREntity, finalREntity.posY + 0.42, onGround.getValue());
        doY(
                finalREntity, finalREntity.posY + 0.75, onGround.getValue());
        doY(
                finalREntity, finalREntity.posY + 1.01, onGround.getValue());
        doY(
                finalREntity, finalREntity.posY + 1.16, onGround.getValue());

        InventoryUtil.switchToHotbarSlot(slot, false);

        if (!sneaking) {
            mc.player.connection.sendPacket(
                    new CPacketEntityAction(mc.player,
                            CPacketEntityAction.Action.START_SNEAKING));
        }

        place(on, f.getOpposite(), slot, vec[0], vec[1], vec[2]);

        if (highBlock.getValue()) {
            doY(
                    finalREntity, finalREntity.posY + 1.67, onGround.getValue());
            doY(
                    finalREntity, finalREntity.posY + 2.01, onGround.getValue());
            doY(
                    finalREntity, finalREntity.posY + 2.42, onGround.getValue());
            BlockPos highPos = pos.up();
            EnumFacing face = EnumFacing.DOWN;
            place(highPos.offset(face), face.getOpposite(), slot, vec[0], vec[1], vec[2]);
        }

        swing(slot);

        InventoryUtil.switchToHotbarSlot(lastSlot, false);


        if (!sneaking) {
            mc.player.connection.sendPacket(
                    new CPacketEntityAction(mc.player,
                            CPacketEntityAction.Action.STOP_SNEAKING));
        }

        doY(rEntity, y, false);
        timer.reset();
        if (!wait.getValue() || placeDisable.getValue())
            disable();
    }

    public Vec3d getVec() {
        return new Vec3d(last_x, last_y, last_z);
    }

    protected double getY(Entity entity, OffsetMode mode) {
        if (mode == OffsetMode.Constant) {
            double y = entity.posY + vClip.getValue();
            if (evade.getValue() && Math.abs(y) < 1) {
                y = -1;
            }

            return y;
        }

        double d = getY(entity, minDown.getValue(), maxDown.getValue(), true);
        if (Double.isNaN(d)) {
            d = getY(entity, -minUp.getValue(), -maxUp.getValue(), false);
            if (Double.isNaN(d)) {
                if (fallback.getValue()) {
                    return getY(entity, OffsetMode.Constant);
                }
            }
        }

        return d;
    }

    protected double getY(Entity entity, double min, double max, boolean add) {
        if (min > max && add || max > min && !add) {
            return Double.NaN;
        }

        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        boolean air = false;
        double lastOff = 0.0;
        BlockPos last = null;
        for (double off = min;
             add ? off < max : off > max;
             off = (add ? ++off : --off)) {
            BlockPos pos = new BlockPos(x, y - off, z);
            if (noVoid.getValue() && pos.getY() < 0) {
                continue;
            }

            if (skipZero.getValue() && Math.abs(y) < 1) {
                air = false;
                last = pos;
                lastOff = y - off;
                continue;
            }

            IBlockState state = mc.world.getBlockState(pos);
            if (!this.air.getValue() && !state.getMaterial().blocksMovement()
                    || state.getBlock() == Blocks.AIR) {
                if (air) {
                    if (add) {
                        return discrete.getValue() ? pos.getY() : y - off;
                    } else {
                        return discrete.getValue() ? last.getY() : lastOff;
                    }
                }

                air = true;
            } else {
                air = false;
            }

            last = pos;
            lastOff = y - off;
        }

        return Double.NaN;
    }

    protected double applyScale(double value) {
        if (value < mc.player.posY && !scaleDown.getValue()
                || !scaleExplosion.getValue() && !scaleVelocity.getValue()
                || scaleTimer.passedMs(scaleDelay.getValue())
                || motionY == 0.0) {
            return value;
        }

        if (value < mc.player.posY) {
            value -= (motionY * scaleFactor.getValue());
        } else {
            value += (motionY * scaleFactor.getValue());
        }


        return discrete.getValue() ? Math.floor(value) : value;
    }

    protected BlockPos getPlayerPos() {
        return deltaY.getValue() && Math.abs(mc.player.motionY) > 0.1
                ? new BlockPos(mc.player)
                : getPosition(mc.player);
    }

    protected boolean isInsideBlock() {
        double x = mc.player.posX;
        double y = mc.player.posY + 0.20;
        double z = mc.player.posZ;

        return mc.world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() || !mc.player.collidedVertically;
    }

    protected boolean singlePlayerCheck(BlockPos pos) {
        if (mc.isSingleplayer()) {
            @SuppressWarnings("ConstantConditions")
            EntityPlayer player = mc.getIntegratedServer()
                    .getPlayerList()
                    .getPlayerByUUID(mc.player.getUniqueID());
            //noinspection ConstantConditions
            if (player == null) {
                this.disable();
                return true;
            }

            player.getEntityWorld().setBlockState(pos, echest.getValue()
                    ? Blocks.ENDER_CHEST.getDefaultState()
                    : Blocks.OBSIDIAN.getDefaultState());

            mc.world.setBlockState(pos, echest.getValue()
                    ? Blocks.ENDER_CHEST.getDefaultState()
                    : Blocks.OBSIDIAN.getDefaultState());

            return true;
        }

        return false;
    }

    public enum OffsetMode {
        Constant,
        Smart
    }
}
