package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.mixin.ducks.IEntity;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

import static com.mrzak34.thunderhack.util.Util.mc;

// TODO: Also use this if we Multithread and use Rotations?
public class CalculationMotion extends AbstractCalculation<CrystalDataMotion> {
    public CalculationMotion(AutoCrystal module,
                             List<Entity> entities,
                             List<EntityPlayer> players) {
        super(module, entities, players);
    }

    public static boolean isLegit(Entity entity, Entity... additional) {
        RayTraceResult result =
                RayTracer.rayTraceEntities(mc.world,
                        mc.player,
                        mc.player.getDistance(entity)
                                + 1.0,
                        Thunderhack.positionManager,
                        Thunderhack.rotationManager,
                        e -> e != null && e.equals(entity),
                        additional);
        return result != null
                && result.entityHit != null
                && (entity.equals(result.entityHit)
                || additional != null
                && additional.length != 0
                && Arrays.stream(additional)
                .anyMatch(e -> result.entityHit.equals(e)));
    }

    public static boolean isLegit(BlockPos pos) {
        return isLegit(pos, null);
    }

    public static boolean isLegit(BlockPos pos, EnumFacing facing) {
        return isLegit(pos, facing, mc.world);
    }

    public static boolean isLegit(BlockPos pos,
                                  EnumFacing facing,
                                  IBlockAccess world) {
        RayTraceResult ray = rayTraceTo(pos, world);
        //noinspection ConstantConditions
        return ray != null
                && ray.getBlockPos() != null
                && ray.getBlockPos().equals(pos)
                && (facing == null
                || ray.sideHit == facing);
    }

    public static RayTraceResult rayTraceTo(BlockPos pos, IBlockAccess world) {
        return rayTraceTo(pos, world, (b, p) -> p.equals(pos));
    }

    public static RayTraceResult rayTraceTo(BlockPos pos,
                                            IBlockAccess world,
                                            BiPredicate<Block, BlockPos> check) {
        return rayTraceWithYP(pos, world,
                Thunderhack.rotationManager.getServerYaw(),
                Thunderhack.rotationManager.getServerPitch(), check);
    }

    public static RayTraceResult rayTraceWithYP(BlockPos pos,
                                                IBlockAccess world,
                                                float yaw, float pitch,
                                                BiPredicate<Block, BlockPos> check) {
        Entity from = mc.player;
        Vec3d start = Thunderhack.positionManager.getVec().add(0, from.getEyeHeight(), 0);
        Vec3d look = RotationUtil.getVec3d(yaw, pitch);
        double d = from.getDistance(pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5) + 1;

        Vec3d end = start.add(look.x * d, look.y * d, look.z * d);

        return RayTracer.trace(mc.world,
                world,
                start,
                end,
                true,
                false,
                true,
                check);
    }

    @Override
    protected IBreakHelper<CrystalDataMotion> getBreakHelper() {
        return module.breakHelperMotion;
    }

    @Override
    protected boolean evaluate(BreakData<CrystalDataMotion> breakData) {
        // count = breakData.getData().size();
        boolean slowReset = false;
        AutoCrystal.BreakValidity validity;
        if (this.breakData.getAntiTotem() != null
                && (validity =
                HelperUtil.isValid(module, this.breakData.getAntiTotem()))
                != AutoCrystal.BreakValidity.INVALID) {
            attack(this.breakData.getAntiTotem(), validity);
            module.breakTimer.reset(module.breakDelay.getValue());
            module.antiTotemHelper.setTarget(null);
            module.antiTotemHelper.setTargetPos(null);
        } else {
            int packets = !module.noRotateNigga(AutoCrystal.ACRotate.Break)
                    ? 1
                    : module.packets.getValue();

            CrystalDataMotion firstRotation = null;
            List<CrystalDataMotion> valids = new ArrayList<>(packets);
            for (CrystalDataMotion data : this.breakData.getData()) {
                if (EntityUtil.isDead(data.getCrystal())) {
                    continue;
                }

                if (data.getTiming() == CrystalDataMotion.Timing.NONE) {
                    continue;
                }

                validity = isValid(module, data);
                if (validity == AutoCrystal.BreakValidity.VALID && valids.size() < packets) {
                    valids.add(data);
                } else if (validity == AutoCrystal.BreakValidity.ROTATIONS
                        && (data.getTiming() == CrystalDataMotion.Timing.BOTH
                        || data.getTiming() == CrystalDataMotion.Timing.POST)
                        && firstRotation == null) {
                    firstRotation = data;
                }
            }

            int slowDelay = module.slowBreakDelay.getValue();
            float slow = module.slowBreakDamage.getValue();
            if (valids.isEmpty()) {
                if (firstRotation != null
                        && (module.shouldDanger()
                        || !(slowReset = firstRotation.getDamage() <= slow)
                        || module.breakTimer.passed(slowDelay))) {
                    attack(firstRotation.getCrystal(), AutoCrystal.BreakValidity.ROTATIONS);
                }
            } else {
                slowReset = !module.shouldDanger();
                for (CrystalDataMotion v : valids) {
                    boolean high = v.getDamage()
                            > module.slowBreakDamage.getValue();
                    if (high || module.breakTimer
                            .passed(module.slowBreakDelay.getValue())) {
                        slowReset = slowReset && !high;
                        if (v.getTiming() == CrystalDataMotion.Timing.POST
                                || v.getTiming() == CrystalDataMotion.Timing.BOTH
                                && v.getPostSelf() < v.getSelfDmg()) {
                            attackPost(v.getCrystal());
                        } else {
                            attack(v.getCrystal(), AutoCrystal.BreakValidity.VALID);
                        }
                    }
                }
            }
        }

        if (attacking) {
            module.breakTimer.reset(slowReset
                    ? module.slowBreakDelay.getValue()
                    : module.breakDelay.getValue());
        }

        return rotating && !module.noRotateNigga(AutoCrystal.ACRotate.Place);
    }

    protected void attackPost(Entity entity) {
        attacking = true;
        scheduling = true;
        rotating = !module.noRotateNigga(AutoCrystal.ACRotate.Break);
        MutableWrapper<Boolean> attacked = new MutableWrapper<>(false);
        Runnable post = module.rotationHelper.post(entity, attacked);
        module.post.add(post);
    }

    private AutoCrystal.BreakValidity isValid(AutoCrystal module,
                                              CrystalDataMotion dataMotion) {
        Entity crystal = dataMotion.getCrystal();
        if (module.existed.getValue() != 0
                && System.currentTimeMillis()
                - ((IEntity) crystal).getTimeStampT()
                + (module.pingExisted.getValue()
                ? Thunderhack.serverManager.getPing() / 2.0
                : 0)
                < module.existed.getValue()) {
            return AutoCrystal.BreakValidity.INVALID;
        }

        if (module.noRotateNigga(AutoCrystal.ACRotate.Break)
                || module.isNotCheckingRotations()
                || (isLegit(crystal, crystal)
                && AutoCrystal.POSITION_HISTORY
                .arePreviousRotationsLegit(crystal,
                        module.rotationTicks
                                .getValue(),
                        true))) {
            return AutoCrystal.BreakValidity.VALID;
        }

        return AutoCrystal.BreakValidity.ROTATIONS;
    }

}