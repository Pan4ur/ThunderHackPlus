package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mrzak34.thunderhack.util.Util.mc;
import static com.mrzak34.thunderhack.util.phobos.CalculationMotion.rayTraceTo;
import static net.minecraft.util.EnumFacing.HORIZONTALS;

public class ServerTimeHelper {
    private static final ScheduledExecutorService THREAD;

    static {
        THREAD = ThreadUtil.newDaemonScheduledExecutor("Server-Helper");
    }

    private final AutoCrystal module;
    private final Setting<AutoCrystal.ACRotate> rotate;
    private final Setting<AutoCrystal.SwingTime> placeSwing;
    private final Setting<Boolean> antiFeetPlace;
    private final Setting<Boolean> newVersion;
    private final Setting<Integer> buffer;

    public ServerTimeHelper(AutoCrystal module,
                            Setting<AutoCrystal.ACRotate> rotate,
                            Setting<AutoCrystal.SwingTime> placeSwing,
                            Setting<Boolean> antiFeetPlace,
                            Setting<Boolean> newVersion,
                            Setting<Integer> buffer) {
        this.module = module;
        this.rotate = rotate;
        this.placeSwing = placeSwing;
        this.antiFeetPlace = antiFeetPlace;
        this.newVersion = newVersion;
        this.buffer = buffer;
    }

    public static boolean isAtFeet(List<EntityPlayer> players,
                                   BlockPos pos,
                                   boolean ignoreCrystals,
                                   boolean noBoost2) {
        for (EntityPlayer player : players) {
            if (Thunderhack.friendManager.isFriend(player)
                    || player == mc.player) continue;
            if (isAtFeet(player, pos, ignoreCrystals, noBoost2)) return true;
        }
        return false;
    }

    public static boolean isAtFeet(EntityPlayer player,
                                   BlockPos pos,
                                   boolean ignoreCrystals,
                                   boolean noBoost2) {
        BlockPos up = pos.up();
        if (!canPlaceCrystal(pos, ignoreCrystals, noBoost2)) return false;
        for (EnumFacing face : HORIZONTALS) {
            BlockPos off = up.offset(face);
            //IBlockState state = mc.world.getBlockState(off);
            if (mc.world.getEntitiesWithinAABB(EntityPlayer.class,
                            new AxisAlignedBB(off))
                    .contains(player)) {
                return true;
            }

            BlockPos off2 = off.offset(face);
            //IBlockState offState = mc.world.getBlockState(off2);
            if (mc.world.getEntitiesWithinAABB(EntityPlayer.class,
                            new AxisAlignedBB(off2))
                    .contains(player)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canPlaceCrystal(BlockPos pos,
                                          boolean ignoreCrystals,
                                          boolean noBoost2) {
        return canPlaceCrystal(pos, ignoreCrystals, noBoost2, null);
    }

    public static boolean canPlaceCrystal(BlockPos pos,
                                          boolean ignoreCrystals,
                                          boolean noBoost2,
                                          List<Entity> entities) {
        return canPlaceCrystal(pos, ignoreCrystals, noBoost2, entities, noBoost2, 0);
    }

    public static boolean canPlaceCrystal(BlockPos pos,
                                          boolean ignoreCrystals,
                                          boolean noBoost2,
                                          List<Entity> entities,
                                          boolean ignoreBoost2Entities,
                                          long deathTime) {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN
                && state.getBlock() != Blocks.BEDROCK) {
            return false;
        }

        return checkBoost(pos, ignoreCrystals, noBoost2, entities,
                ignoreBoost2Entities, deathTime);
    }

    public static boolean canPlaceCrystalReplaceable(BlockPos pos,
                                                     boolean ignoreCrystals,
                                                     boolean noBoost2,
                                                     List<Entity> entities,
                                                     boolean ignoreBoost2Entities,
                                                     long deathTime) {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.OBSIDIAN
                && state.getBlock() != Blocks.BEDROCK
                && !state.getMaterial().isReplaceable()) {
            return false;
        }

        return checkBoost(pos, ignoreCrystals, noBoost2, entities,
                ignoreBoost2Entities, deathTime);
    }

    public static boolean checkBoost(BlockPos pos,
                                     boolean ignoreCrystals,
                                     boolean noBoost2,
                                     List<Entity> entities,
                                     boolean ignoreBoost2Entities,
                                     long deathTime) {
        BlockPos boost = pos.up();
        if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR
                || !checkEntityList(boost, ignoreCrystals, entities, deathTime)) {
            return false;
        }

        if (!noBoost2) {
            BlockPos boost2 = boost.up();

            if (mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }

            return ignoreBoost2Entities
                    || checkEntityList(boost2, ignoreCrystals, entities, deathTime);
        }

        return true;
    }

    public static boolean checkEntityList(BlockPos pos,
                                          boolean ignoreCrystals,
                                          List<Entity> entities) {
        return checkEntityList(pos, ignoreCrystals, entities, 0);
    }

    public static boolean checkEntityList(BlockPos pos,
                                          boolean ignoreCrystals,
                                          List<Entity> entities,
                                          long deathTime) {
        if (entities == null) {
            return checkEntities(pos, ignoreCrystals, deathTime);
        }

        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities) {
            if (checkEntity(entity, ignoreCrystals, deathTime)
                    && entity.getEntityBoundingBox().intersects(bb)) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkEntities(BlockPos pos,
                                        boolean ignoreCrystals,
                                        long deathTime) {
        for (Entity entity : mc.world
                .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (checkEntity(entity, ignoreCrystals, deathTime)) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkEntity(Entity entity,
                                       boolean ignoreCrystals,
                                       long deathTime) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof EntityEnderCrystal) {
            if (ignoreCrystals) {
                return false;
            }

            return !entity.isDead
                    || !Thunderhack.setDeadManager.passedDeathTime(entity, deathTime);
        }

        return !EntityUtil.isDead(entity);
    }

    public static boolean isSemiSafe(EntityPlayer player,
                                     boolean ignoreCrystals,
                                     boolean noBoost2) {
        BlockPos origin = (player.getPosition());
        int i = 0;
        for (EnumFacing face : HORIZONTALS) {
            BlockPos off = origin.offset(face);
            if (mc.world.getBlockState(off).getBlock() != Blocks.AIR) i++;
        }
        return i >= 3;
    }

    public static EntityPlayer getClosestEnemy() {
        return getClosestEnemy(mc.world.playerEntities);
    }

    public static EntityPlayer getClosestEnemy(List<EntityPlayer> list) {
        return getClosestEnemy(mc.player.getPositionVector(), list);
    }

    public static EntityPlayer getClosestEnemy(BlockPos pos,
                                               List<EntityPlayer> list) {
        return getClosestEnemy(pos.getX(), pos.getY(), pos.getZ(), list);
    }

    public static EntityPlayer getClosestEnemy(Vec3d vec3d,
                                               List<EntityPlayer> list) {
        return getClosestEnemy(vec3d.x, vec3d.y, vec3d.z, list);
    }

    public static EntityPlayer getClosestEnemy(double x,
                                               double y,
                                               double z,
                                               double maxRange,
                                               List<EntityPlayer> enemies,
                                               List<EntityPlayer> players) {
        EntityPlayer closestEnemied = getClosestEnemy(x, y, z, enemies);
        if (closestEnemied != null
                && closestEnemied.getDistanceSq(x, y, z)
                < MathUtil.square(maxRange)) {
            return closestEnemied;
        }

        return getClosestEnemy(x, y, z, players);
    }

    public static EntityPlayer getClosestEnemy(double x,
                                               double y,
                                               double z,
                                               List<EntityPlayer> players) {
        EntityPlayer closest = null;
        double distance = Float.MAX_VALUE;

        for (EntityPlayer player : players) {
            if (player != null
                    && !(player.isDead)
                    && !player.equals(mc.player)
                    && !Thunderhack.friendManager.isFriend(player)) {
                double dist = player.getDistanceSq(x, y, z);
                if (dist < distance) {
                    closest = player;
                    distance = dist;
                }
            }
        }

        return closest;
    }

    public static boolean isValid(Entity player, double range) {
        return player != null
                && !(player.isDead)
                && mc.player.getDistanceSq(player) <= MathUtil.square(range)
                && !Thunderhack.friendManager.isFriend((EntityPlayer) player);
    }

    public void onUseEntity(CPacketUseEntity packet, Entity crystal) {
        // You can also check out ICPacketUseEntity to access the entity better
        EntityPlayer closest;
        if (packet.getAction() == CPacketUseEntity.Action.ATTACK
                && antiFeetPlace.getValue()
                && (rotate.getValue() == AutoCrystal.ACRotate.None || rotate.getValue() == AutoCrystal.ACRotate.Break)
                && crystal instanceof EntityEnderCrystal
                && (closest = getClosestEnemy()) != null
                && isSemiSafe(closest, true, newVersion.getValue())
                && isAtFeet(mc.world.playerEntities, crystal.getPosition().down(), true, newVersion.getValue())) {
            int intoTick = Thunderhack.servtickManager.getTickTimeAdjusted();
            int sleep = Thunderhack.servtickManager.getServerTickLengthMS() + Thunderhack.servtickManager.getSpawnTime() + buffer.getValue() - intoTick;
            place(crystal.getPosition().down(), sleep);
        }
    }

    private void place(BlockPos pos, int sleep) {
        AutoCrystal.SwingTime time = placeSwing.getValue();
        THREAD.schedule(() -> {
            if (InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                EnumHand hand = InventoryUtil.getHand(Items.END_CRYSTAL);
                RayTraceResult ray = rayTraceTo(pos, mc.world);
                float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.hitVec);
                if (time == AutoCrystal.SwingTime.Pre) {
                    Swing.Packet.swing(hand);
                    Swing.Client.swing(hand);
                }
                mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                                pos, ray.sideHit, hand, f[0], f[1], f[2]));
                module.sequentialHelper.setExpecting(pos);

                if (time == AutoCrystal.SwingTime.Post) {
                    Swing.Packet.swing(hand);
                    Swing.Client.swing(hand);
                }
            }
        }, sleep, TimeUnit.MILLISECONDS);
    }


}