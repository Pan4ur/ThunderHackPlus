package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.mixin.ducks.IEntity;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.atomic.AtomicInteger;

import static com.mrzak34.thunderhack.modules.combat.Burrow.rotation;
import static com.mrzak34.thunderhack.util.phobos.CalculationMotion.isLegit;
import static com.mrzak34.thunderhack.util.phobos.CalculationMotion.rayTraceTo;
import static com.mrzak34.thunderhack.util.phobos.RotationSmoother.getRotations;

// TODO: make the resetting better!
public class HelperRotation {
    private static final AtomicInteger ID = new AtomicInteger();
    //  private static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);


    private final RotationSmoother smoother;
    private final AutoCrystal module;

    public HelperRotation(AutoCrystal module) {
        this.smoother = new RotationSmoother(Thunderhack.rotationManager);
        this.module = module;
    }

    public static Runnable wrap(Runnable runnable) {
        return () -> acquire(runnable);
    }

    public static void acquire(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {

        }
    }

    public static void startDigging(BlockPos pos, EnumFacing facing) {
        Util.mc.player.connection.sendPacket(new CPacketPlayerDigging(
                CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
    }

    public static void stopDigging(BlockPos pos, EnumFacing facing) {
        Util.mc.player.connection.sendPacket(new CPacketPlayerDigging(
                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
    }

    public static WeaknessSwitch antiWeakness(AutoCrystal module) {
        if (!module.weaknessHelper.isWeaknessed()) {
            return WeaknessSwitch.NONE;
        } else if (module.antiWeakness.getValue() == AutoCrystal.AntiWeakness.None
                || module.cooldown.getValue() != 0) {
            return WeaknessSwitch.INVALID;
        }

        return new WeaknessSwitch(DamageUtil.findAntiWeakness(), true);
    }

    public RotationFunction forPlacing(BlockPos pos,
                                       MutableWrapper<Boolean> hasPlaced) {
        int id = ID.incrementAndGet();
        Timer timer = new Timer();
        MutableWrapper<Boolean> ended = new MutableWrapper<>(false);
        return (x, y, z, yaw, pitch) ->
        {
            boolean breaking = false;
            float[] rotations = null;
            if (hasPlaced.get()
                    || Util.mc.player.getDistanceSq(pos) > 64
                    && pos.distanceSq(x, y, z) > 64
                    || (module.autoSwitch.getValue() != AutoCrystal.AutoSwitch.Always
                    && !module.switching
                    && !module.weaknessHelper.canSwitch()
                    && !InventoryUtil.isHolding(Items.END_CRYSTAL))) {
                if (!ended.get()) {
                    ended.set(true);
                    timer.reset();
                }

                if (!module.attack.getValue()
                        || timer.passedMs(module.endRotations.getValue())) {
                    if (id == ID.get()) {
                        module.rotation = null;
                    }

                    return new float[]{yaw, pitch};
                }

                breaking = true;
                double height = 1.7 * module.height.getValue();
                rotations =
                        getRotations(pos.getX() + 0.5f,
                                pos.getY() + 1 + height,
                                pos.getZ() + 0.5f,
                                x, y, z,
                                Util.mc.player.getEyeHeight());
            } else {
                double height = module.placeHeight.getValue();
                if (module.smartTrace.getValue()) {
                    for (EnumFacing facing : EnumFacing.values()) {
                        Ray ray = RayTraceFactory.rayTrace(
                                Util.mc.player,
                                pos,
                                facing,
                                Util.mc.world,
                                Blocks.OBSIDIAN.getDefaultState(),
                                module.traceWidth.getValue());
                        if (ray.isLegit()) {
                            rotations = ray.getRotations();
                            break;
                        }
                    }
                }

                if (rotations == null) {
                    if (module.fallbackTrace.getValue()) {
                        rotations = getRotations(
                                pos.getX() + 0.5,
                                pos.getY() + 1.0,
                                pos.getZ() + 0.5,
                                x, y, z,
                                Util.mc.player.getEyeHeight());
                    } else {
                        rotations = getRotations(
                                pos.getX() + 0.5,
                                pos.getY() + height,
                                pos.getZ() + 0.5,
                                x, y, z,
                                Util.mc.player.getEyeHeight());
                    }
                }
            }

            return smoother.smoothen(rotations,
                    breaking ? module.angle.getValue()
                            : module.placeAngle.getValue());
        };
    }

    public RotationFunction forBreaking(Entity entity,
                                        MutableWrapper<Boolean> attacked) {
        int id = ID.incrementAndGet();
        Timer timer = new Timer();
        MutableWrapper<Boolean> ended = new MutableWrapper<>(false);
        return (x, y, z, yaw, pitch) ->
        {
            if (Util.mc.player.getDistanceSq(entity) > 64) {
                attacked.set(true);
            }

            if (module.getTarget() == null) {
                attacked.set(true);
            }

            if (attacked.get()) {
                if (!ended.get()) {
                    ended.set(true);
                    timer.reset();
                }

                if (ended.get()
                        && timer.passedMs(module.endRotations.getValue())) {
                    if (id == ID.get()) {
                        module.rotation = null;
                    }

                    return new float[]{yaw, pitch};
                }
            }

            return smoother.getRotations(entity, x, y, z,
                    Util.mc.player.getEyeHeight(),
                    module.height.getValue(),
                    module.angle.getValue());
        };
    }

    public RotationFunction forObby(PositionData data) {
        return (x, y, z, yaw, pitch) ->
        {
            if (data.getPath().length <= 0) {
                return new float[]{yaw, pitch};
            }

            Ray ray = data.getPath()[0];
            ray.updateRotations(Util.mc.player);
            return ray.getRotations();
        };
    }

    public Runnable post(AutoCrystal module,
                         float damage,
                         boolean realtime,
                         BlockPos pos,
                         MutableWrapper<Boolean> placed,
                         MutableWrapper<Boolean> liquidBreak) {
        return () ->
        {
            if (liquidBreak != null && !liquidBreak.get()) {
                return;
            }

            if (!InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                if (module.autoSwitch.getValue() == AutoCrystal.AutoSwitch.Always
                        || module.autoSwitch.getValue() == AutoCrystal.AutoSwitch.Bind
                        && module.switching) {
                    if (!module.mainHand.getValue()) {

                        // OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL));
/*

                        if (module.instantOffhand.getValue())
                        {
                            if (OFFHAND.get().isSafe())
                            {
                                OFFHAND.get().setMode(OffhandMode.CRYSTAL);

                                for (int i = 0; i < 3; i++)
                                {
                                    OFFHAND.get().getTimer().setTime(10000);
                                    OFFHAND.get().doOffhand();
                                }
                            }

                            if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
                            {
                                return;
                            }


                        }
                        else
                        {
                            return;
                        }

 */
                    }
                } else {
                    return;
                }
            }

            int slot = -1;
            EnumHand hand = InventoryUtil.getHand(Items.END_CRYSTAL);
            if (hand == null) {
                if (module.mainHand.getValue()) {
                    slot = InventoryUtil.getCrysathotbar();
                    if (slot == -1) {
                        return;
                    }
                    // -2 shouldn't really happen, but just to be safe
                    hand = slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                } else {
                    return;
                }
            }

            RayTraceResult ray = rayTraceTo(pos, Util.mc.world);
            if (ray == null || !pos.equals(ray.getBlockPos())) {
                if (!module.noRotateNigga(AutoCrystal.ACRotate.Place) && !module.isNotCheckingRotations()) {
                    return;
                }

                ray = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            } else if (module.fallbackTrace.getValue()
                    && Util.mc.world.getBlockState(ray.getBlockPos().offset(ray.sideHit))
                    .getMaterial()
                    .isSolid()) {
                ray = new RayTraceResult(
                        new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            }

            module.switching = false;
            AutoCrystal.SwingTime swingTime = module.placeSwing.getValue();
            float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.hitVec);
            boolean noGodded = false;
            // we need to check this here since we switch
            if (module.idHelper.isDangerous(Util.mc.player,
                    module.holdingCheck.getValue(),
                    module.toolCheck.getValue())) {
                module.noGod = true;
                noGodded = true;
            }

            int finalSlot = slot;
            EnumHand finalHand = hand;
            RayTraceResult finalRay = ray;
            boolean finalNoGodded = noGodded;
            acquire(() ->
            {
                int lastSlot = Util.mc.player.inventory.currentItem;
                if (finalSlot != -1 && finalSlot != -2) {
                    switch (module.cooldownBypass.getValue()) {
                        case None:
                            CooldownBypass.None.switchTo(finalSlot);
                            break;
                        case Pick:
                            CooldownBypass.Pick.switchTo(finalSlot);
                            break;
                        case Slot:
                            CooldownBypass.Slot.switchTo(finalSlot);
                            break;
                        case Swap:
                            CooldownBypass.Swap.switchTo(finalSlot);
                            break;
                    }
                }

                InventoryUtil.syncItem();
                if (swingTime == AutoCrystal.SwingTime.Pre) {
                    swing(finalHand, false);
                }

                Util.mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                                pos, finalRay.sideHit, finalHand, f[0], f[1], f[2]));
                module.sequentialHelper.setExpecting(pos);

                if (finalNoGodded) {
                    module.noGod = false;
                }

                placed.set(true);

                if (swingTime == AutoCrystal.SwingTime.Post) {
                    swing(finalHand, false);
                }

                if (module.switchBack.getValue()) {
                    switch (module.cooldownBypass.getValue()) {
                        case None:
                            CooldownBypass.None.switchBack(lastSlot, finalSlot);
                            break;
                        case Pick:
                            CooldownBypass.Pick.switchBack(lastSlot, finalSlot);
                            break;
                        case Slot:
                            CooldownBypass.Slot.switchBack(lastSlot, finalSlot);
                            break;
                        case Swap:
                            CooldownBypass.Swap.switchBack(lastSlot, finalSlot);
                            break;
                    }
                }
            });

            if (realtime) {
                module.setRenderPos(pos, damage);
            }

            if (module.simulatePlace.getValue() != 0) {
                module.crystalRender.addFakeCrystal(
                        new EntityEnderCrystal(Util.mc.world, pos.getX() + 0.5f,
                                pos.getY() + 1,
                                pos.getZ() + 0.5f));
            }
        };
    }

    public Runnable post(Entity entity, MutableWrapper<Boolean> attacked) {
        return () ->
        {
            WeaknessSwitch w = antiWeakness(module);
            if (w.needsSwitch() && w.getSlot() == -1
                    || (EntityUtil.isDead(entity))
                    || !module.noRotateNigga(AutoCrystal.ACRotate.Break)
                    && !module.isNotCheckingRotations()
                    && !isLegit(entity)) {
                return;
            }

            CPacketUseEntity packet = new CPacketUseEntity(entity);
            AutoCrystal.SwingTime swingTime = module.breakSwing.getValue();
            Runnable runnable = () ->
            {
                int lastSlot = Util.mc.player.inventory.currentItem;
                if (w.getSlot() != -1) {
                    switch (module.antiWeaknessBypass.getValue()) {
                        case None:
                            CooldownBypass.None.switchTo(w.getSlot());
                            break;
                        case Pick:
                            CooldownBypass.Pick.switchTo(w.getSlot());
                            break;
                        case Slot:
                            CooldownBypass.Slot.switchTo(w.getSlot());
                            break;
                        case Swap:
                            CooldownBypass.Swap.switchTo(w.getSlot());
                            break;
                    }
                }

                if (swingTime == AutoCrystal.SwingTime.Pre) {
                    swing(EnumHand.MAIN_HAND, true);
                }

                Util.mc.player.connection.sendPacket(packet);
                attacked.set(true);

                if (swingTime == AutoCrystal.SwingTime.Post) {
                    swing(EnumHand.MAIN_HAND, true);
                }

                if (w.getSlot() != -1) {
                    switch (module.antiWeaknessBypass.getValue()) {
                        case None:
                            CooldownBypass.None.switchBack(lastSlot, w.getSlot());
                            break;
                        case Pick:
                            CooldownBypass.Pick.switchBack(lastSlot, w.getSlot());
                            break;
                        case Slot:
                            CooldownBypass.Slot.switchBack(lastSlot, w.getSlot());
                            break;
                        case Swap:
                            CooldownBypass.Swap.switchBack(lastSlot, w.getSlot());
                            break;
                    }
                }
            };

            if (w.getSlot() != -1) {
                acquire(runnable);
            } else {
                runnable.run();
            }

            if (module.pseudoSetDead.getValue()) {
                ((IEntity) entity).setPseudoDeadT(true);
            }

            if (module.setDead.getValue()) {
                Thunderhack.setDeadManager.setDead(entity);
            }
        };
    }

    public Runnable postBlock(PositionData data) {
        return postBlock(data, -1, module.obbyRotate.getValue(), null, null);
    }

    public Runnable postBlock(PositionData data,
                              int preSlot,
                              AutoCrystal.Rotate rotate,
                              MutableWrapper<Boolean> placed,
                              MutableWrapper<Integer> switchBack) {
        return () ->
        {
            if (!data.isValid()) {
                return;
            }

            int slot = preSlot == -1
                    ? InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
                    : preSlot;
            if (slot == -1) {
                return;
            }

            EnumHand hand = slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            AutoCrystal.PlaceSwing placeSwing = module.obbySwing.getValue();

            acquire(() ->
            {
                int lastSlot = Util.mc.player.inventory.currentItem;
                if (switchBack != null) {
                    switchBack.set(lastSlot);
                }

                switch (module.obsidianBypass.getValue()) {
                    case None:
                        CooldownBypass.None.switchTo(slot);
                        break;
                    case Pick:
                        CooldownBypass.Pick.switchTo(slot);
                        break;
                    case Slot:
                        CooldownBypass.Slot.switchTo(slot);
                        break;
                    case Swap:
                        CooldownBypass.Swap.switchTo(slot);
                        break;
                }

                for (Ray ray : data.getPath()) {
                    if (rotate == AutoCrystal.Rotate.Packet
                            && !isLegit(ray.getPos(), ray.getFacing())) {
                        Thunderhack.rotationManager.setBlocking(true);
                        float[] r = ray.getRotations();
                        Util.mc.player.connection.sendPacket(rotation(r[0], r[1], Util.mc.player.onGround));
                        Thunderhack.rotationManager.setBlocking(false);
                    }

                    float[] f = RayTraceUtil.hitVecToPlaceVec(
                            ray.getPos(), ray.getResult().hitVec);

                    Util.mc.player.connection.sendPacket(
                            new CPacketPlayerTryUseItemOnBlock(
                                    ray.getPos(),
                                    ray.getFacing(),
                                    hand,
                                    f[0],
                                    f[1],
                                    f[2]));

                    if (module.setState.getValue() && preSlot == -1) {
                        Util.mc.addScheduledTask(() ->
                        {
                            if (Util.mc.world != null) {
                                Util.mc.world.setBlockState(
                                        ray.getPos().offset(ray.getFacing()),
                                        Blocks.OBSIDIAN.getDefaultState());
                            }
                        });
                    }

                    if (placeSwing == AutoCrystal.PlaceSwing.Always) {
                        Swing.Packet.swing(hand);
                    }
                }

                if (placeSwing == AutoCrystal.PlaceSwing.Once) {
                    Swing.Packet.swing(hand);
                }

                switch (module.obsidianBypass.getValue()) {
                    case None:
                        CooldownBypass.None.switchBack(lastSlot, slot);
                        break;
                    case Pick:
                        CooldownBypass.Pick.switchBack(lastSlot, slot);
                        break;
                    case Slot:
                        CooldownBypass.Slot.switchBack(lastSlot, slot);
                        break;
                    case Swap:
                        CooldownBypass.Swap.switchBack(lastSlot, slot);
                        break;
                }

                if (placed != null) {
                    placed.set(true);
                }
            });

            EnumHand swingHand = resolvehand3();
            if (swingHand != null) {
                Swing.Client.swing(swingHand);
            }
        };
    }

    public Runnable breakBlock(int toolSlot,
                               MutableWrapper<Boolean> placed,
                               MutableWrapper<Integer> lastSlot,
                               int[] order,
                               Ray... positions) {
        return wrap(() ->
        {
            if (order.length != positions.length) {
                throw new IndexOutOfBoundsException("Order length: "
                        + order.length + ", Positions length: " + positions.length);
            }

            if (!placed.get()) {
                return;
            }

            switch (module.mineBypass.getValue()) {
                case None:
                    CooldownBypass.None.switchTo(toolSlot);
                    break;
                case Pick:
                    CooldownBypass.Pick.switchTo(toolSlot);
                    break;
                case Slot:
                    CooldownBypass.Slot.switchTo(toolSlot);
                    break;
                case Swap:
                    CooldownBypass.Swap.switchTo(toolSlot);
                    break;
            }
            for (int i : order) {
                Ray ray = positions[i];
                BlockPos pos = ray.getPos().offset(ray.getFacing());
                startDigging(pos, ray.getFacing().getOpposite());
                stopDigging(pos, ray.getFacing().getOpposite());
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }

            switch (module.mineBypass.getValue()) {
                case None:
                    CooldownBypass.None.switchBack(lastSlot.get(), toolSlot);
                    break;
                case Pick:
                    CooldownBypass.Pick.switchBack(lastSlot.get(), toolSlot);
                    break;
                case Slot:
                    CooldownBypass.Slot.switchBack(lastSlot.get(), toolSlot);
                    break;
                case Swap:
                    CooldownBypass.Swap.switchBack(lastSlot.get(), toolSlot);
                    break;
            }
        });
    }

    public void swing(EnumHand hand, boolean breaking) {
        Swing.Packet.swing(hand);
        EnumHand swingHand = breaking ? resolvehand() : resolvehand2();

        if (swingHand != null) {
            Swing.Client.swing(swingHand);
        }
    }

    EnumHand resolvehand() {
        switch (module.swing.getValue()) {
            case None:
                return null;
            case OffHand:
                return EnumHand.OFF_HAND;
            case MainHand:
                return EnumHand.MAIN_HAND;
        }
        return EnumHand.MAIN_HAND;
    }

    EnumHand resolvehand2() {
        switch (module.placeHand.getValue()) {
            case None:
                return null;
            case OffHand:
                return EnumHand.OFF_HAND;
            case MainHand:
                return EnumHand.MAIN_HAND;
        }
        return EnumHand.MAIN_HAND;
    }

    EnumHand resolvehand3() {
        switch (module.obbyHand.getValue()) {
            case None:
                return null;
            case OffHand:
                return EnumHand.OFF_HAND;
            case MainHand:
                return EnumHand.MAIN_HAND;
        }
        return EnumHand.MAIN_HAND;
    }

}