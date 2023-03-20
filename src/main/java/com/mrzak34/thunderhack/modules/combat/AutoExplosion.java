package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

import static com.mrzak34.thunderhack.util.RotationUtil.calcAngle;

public class AutoExplosion extends Module {

    public static EntityPlayer trgt;
    public Setting<Boolean> packetplace = register(new Setting<>("packetplace", true));
    public Setting<Integer> stophp = this.register(new Setting<>("stophp", 8, 1, 20));
    public Setting<Integer> delay = this.register(new Setting<>("TicksExisted", 8, 1, 20));
    public Setting<Integer> placedelay = this.register(new Setting<>("PlaceDelay", 8, 1, 1000));
    public Setting<Integer> maxself = this.register(new Setting<>("maxself", 10, 1, 20));
    public Setting<SubBind> bindButton = this.register(new Setting<>("BindButton", new SubBind(Keyboard.KEY_LSHIFT)));
    int ticksNoOnGround = 0;
    BlockPos CoolPosition;
    Timer placeDelay = new Timer();
    Timer breakDelay = new Timer();
    int extraTicks = 5;
    private final Setting<Mode> mode = register(new Setting("Mode", Mode.FullAuto));
    private final Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.Aura));
    public Setting<Boolean> offAura = register(new Setting<>("offAura", true, v -> targetMode.getValue() == TargetMode.AutoExplosion));
    private BlockPos crysToExplosion;
    public AutoExplosion() {
        super("AutoExplosion", "более тупая кристалка-для кринж серверов", "don't use-this shit", Category.COMBAT);
    }

    @SubscribeEvent
    public void onPlayerPre(EventSync e) {
        if (targetMode.getValue() == TargetMode.Aura) {
            offAura.setValue(false);
        }
        if (mc.player.getHealth() < stophp.getValue()) {
            return;
        }
        if (mode.getValue() == Mode.FullAuto) {
            FullAuto(e);
        } else if (mode.getValue() == Mode.Semi) {
            Semi(e);
        } else if (mode.getValue() == Mode.Bind && PlayerUtils.isKeyDown(bindButton.getValue().getKey())) {
            onBind(e);
        }
    }

    public void Semi(EventSync e) {
        if (Mouse.isButtonDown(1)) {
            if (offAura.getValue() && Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled()) {
                Thunderhack.moduleManager.getModuleByClass(Aura.class).disable();
            }
            RayTraceResult ray = mc.player.rayTrace(4.5, mc.getRenderPartialTicks());
            BlockPos pos = null;
            if (ray != null) {
                pos = ray.getBlockPos();
            }
            if (pos != null) {
                if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
                    int crysslot = InventoryUtil.getCrysathotbar();
                    if (crysslot == -1) {
                        return;
                    }
                    int oldSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = InventoryUtil.getCrysathotbar();
                    mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.inventory.currentItem = oldSlot;
                    crysToExplosion = pos;
                    extraTicks = 5;
                }
            }
        } else {
            if (crysToExplosion != null) {
                EntityEnderCrystal ourCrys = getCrystal(crysToExplosion);
                if (ourCrys != null) {
                    if (ourCrys.ticksExisted > delay.getValue() && breakDelay.passedMs(156)) {
                        if (CrystalUtils.calculateDamage(ourCrys, mc.player) < maxself.getValue()) {
                            mc.player.setSprinting(false);
                            float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), ourCrys.getPositionEyes(mc.getRenderPartialTicks()));
                            mc.player.rotationYaw = (angle[0]);
                            mc.player.rotationPitch = (angle[1]);
                            mc.player.connection.sendPacket(new CPacketUseEntity(ourCrys));
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            breakDelay.reset();
                        }
                    }
                } else {
                    extraTicks--;
                    if (extraTicks <= 0) {
                        crysToExplosion = null;
                        extraTicks = 10;
                    }
                }
            }
        }
    }

    public void FullAuto(EventSync e) {
        for (Entity ent : mc.world.loadedEntityList) {
            if (ent instanceof EntityEnderCrystal) {
                if (mc.player.getDistance(ent) < 5f) {
                    if (ent.ticksExisted >= delay.getValue()) {
                        if (breakDelay.passedMs(156)) {
                            if (CrystalUtils.calculateDamage((EntityEnderCrystal) ent, mc.player) < maxself.getValue()) {
                                if (offAura.getValue()) {
                                    Thunderhack.moduleManager.getModuleByClass(Aura.class).disable();
                                }
                                mc.player.setSprinting(false);
                                float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), ent.getPositionEyes(mc.getRenderPartialTicks()));
                                mc.player.rotationYaw = (angle[0]);
                                mc.player.rotationPitch = (angle[1]);
                                mc.player.connection.sendPacket(new CPacketUseEntity(ent));
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                                breakDelay.reset();
                            }
                        }
                    }
                }
            }
        }

        if (targetMode.getValue() == TargetMode.Aura) {
            if (Aura.target != null) {
                if (Aura.target instanceof EntityPlayer) {
                    trgt = (EntityPlayer) Aura.target;
                    if (!trgt.onGround) {
                        ++ticksNoOnGround;
                    } else {
                        ticksNoOnGround = 0;
                    }
                }
            } else {
                trgt = null;
                return;
            }
        } else {
            for (EntityPlayer ent : mc.world.playerEntities) {
                if (mc.player.getDistanceSq(ent) < 36) {
                    if (!Thunderhack.friendManager.isFriend(ent)) {
                        trgt = ent;
                    }
                }
            }
            if (trgt == null) {
                return;
            }
        }


        if (getPosition(mc.player) != null && (mc.player.posY + 0.228f < trgt.posY)) {
            CoolPosition = getPosition(mc.player);
            if (mc.world.getBlockState(CoolPosition).getBlock() == Blocks.OBSIDIAN) {
                if (getCrystal(CoolPosition) != null) {
                    return;
                }
                if (!placeDelay.passedMs(placedelay.getValue())) {
                    return;
                }
                int crysslot = InventoryUtil.getCrysathotbar();
                if (crysslot == -1) {
                    return;
                }
                if (offAura.getValue()) {
                    Thunderhack.moduleManager.getModuleByClass(Aura.class).disable();
                }
                InventoryUtil.switchToHotbarSlot(InventoryUtil.getCrysathotbar(), false);
                BlockUtils.placeBlockSmartRotate(CoolPosition.add(0, 1, 0), EnumHand.MAIN_HAND, true, packetplace.getValue(), mc.player.isSneaking(), e);
                placeDelay.reset();
            } else {
                if (offAura.getValue()) {
                    Thunderhack.moduleManager.getModuleByClass(Aura.class).disable();
                }
                int obbyslot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                if (obbyslot == -1) {
                    return;
                }
                InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(BlockObsidian.class), false);
                BlockUtils.placeBlockSmartRotate(CoolPosition, EnumHand.MAIN_HAND, true, packetplace.getValue(), mc.player.isSneaking(), e);
            }
        }
    }

    public void onBind(EventSync e) {
        for (Entity ent : mc.world.loadedEntityList) {
            if (ent instanceof EntityEnderCrystal) {
                if (mc.player.getDistance(ent) < 5f) {
                    if (ent.ticksExisted >= delay.getValue()) {
                        if (breakDelay.passedMs(156)) {
                            if (CrystalUtils.calculateDamage((EntityEnderCrystal) ent, mc.player) < maxself.getValue()) {
                                mc.player.setSprinting(false);
                                float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), ent.getPositionEyes(mc.getRenderPartialTicks()));
                                mc.player.rotationYaw = (angle[0]);
                                mc.player.rotationPitch = (angle[1]);
                                mc.player.connection.sendPacket(new CPacketUseEntity(ent));
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                                breakDelay.reset();
                            }
                        }
                    }
                }
            }
        }

        if (targetMode.getValue() == TargetMode.Aura) {
            if (Aura.target != null) {
                if (Aura.target instanceof EntityPlayer) {
                    trgt = (EntityPlayer) Aura.target;
                }
            } else {
                trgt = null;
                return;
            }
        } else {
            for (EntityPlayer ent : mc.world.playerEntities) {
                if (mc.player.getDistanceSq(ent) < 36) {
                    if (!Thunderhack.friendManager.isFriend(ent)) {
                        trgt = ent;
                    }
                }
            }
            if (trgt == null) {
                return;
            }
        }


        if (getPosition(mc.player) != null) {
            CoolPosition = getPosition(mc.player);
            if (mc.world.getBlockState(CoolPosition).getBlock() == Blocks.OBSIDIAN) {
                if (getCrystal(CoolPosition) != null) {
                    return;
                }
                if (!placeDelay.passedMs(placedelay.getValue())) {
                    return;
                }
                int crysslot = InventoryUtil.getCrysathotbar();
                if (crysslot == -1) {
                    return;
                }
                if (offAura.getValue()) {
                    Thunderhack.moduleManager.getModuleByClass(Aura.class).disable();
                }
                InventoryUtil.switchToHotbarSlot(InventoryUtil.getCrysathotbar(), false);
                BlockUtils.placeBlockSmartRotate(CoolPosition.add(0, 1, 0), EnumHand.MAIN_HAND, true, packetplace.getValue(), mc.player.isSneaking(), e);
                placeDelay.reset();
            } else {
                if (offAura.getValue()) {
                    Thunderhack.moduleManager.getModuleByClass(Aura.class).disable();
                }
                int obbyslot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                if (obbyslot == -1) {
                    return;
                }
                InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(BlockObsidian.class), false);
                BlockUtils.placeBlockSmartRotate(CoolPosition, EnumHand.MAIN_HAND, true, packetplace.getValue(), mc.player.isSneaking(), e);
            }
        }
    }

    public boolean canPlace(BlockPos bp) {
        if (mc.world.getBlockState(bp.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, 2, 0)).getBlock() != Blocks.AIR) {
            return false;
        }
        return mc.world.getBlockState(bp).getBlock() == Blocks.AIR || mc.world.getBlockState(bp).getBlock() == Blocks.OBSIDIAN;
    }

    private BlockPos getPosition(EntityPlayer entity2) {
        ArrayList<BlockPos> arrayList = new ArrayList<>();
        int playerX = (int) entity2.posX;
        int playerZ = (int) entity2.posZ;
        int n4 = (int) ((float) 4.0);
        double playerX1 = entity2.posX - 0.5D;
        double playerY1 = entity2.posY + (double) entity2.getEyeHeight() - 1.0D;
        double playerZ1 = entity2.posZ - 0.5D;
        for (int n5 = playerX - n4; n5 <= playerX + n4; ++n5) {
            for (int n6 = playerZ - n4; n6 <= playerZ + n4; ++n6) {
                if (((double) n5 - playerX1) * ((double) n5 - playerX1) + (mc.player.posY - playerY1) * (mc.player.posY - playerY1) + ((double) n6 - playerZ1) * ((double) n6 - playerZ1) <= (double) ((float) 5.0 * (float) 5.0) && canPlace(new BlockPos(n5, mc.player.posY, n6))) {
                    if (mc.world.getBlockState(new BlockPos(n5, mc.player.posY, n6)).getBlock() == Blocks.OBSIDIAN && (trgt.getDistanceSqToCenter(new BlockPos(n5, mc.player.posY, n6)) < 16)) {
                        return new BlockPos(n5, mc.player.posY, n6);
                    } else {
                        arrayList.add(new BlockPos(n5, mc.player.posY, n6));
                    }
                }
            }
        }
        return AI(arrayList);
    }

    public EntityEnderCrystal getCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        for (Entity ent : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost, boost2.add(1, 1, 1)))) {
            if (ent instanceof EntityEnderCrystal) {
                return (EntityEnderCrystal) ent;
            }
        }
        return null;
    }

    // mega smart xD
    public BlockPos AI(ArrayList<BlockPos> blocks) {
        BlockPos pos = null;
        double bestdist = 5;
        if (trgt == null) return null;
        for (BlockPos pos1 : blocks) {
            if ((pos1.getDistance((int) trgt.posX, (int) trgt.posY, (int) trgt.posZ) > 2) && trgt.getDistanceSqToCenter(pos1) < bestdist) {
                bestdist = trgt.getDistanceSqToCenter(pos1);
                pos = pos1;
            }
        }
        return pos;
    }

    public enum Mode {
        FullAuto, Semi, Bind
    }

    public enum TargetMode {
        Aura, AutoExplosion
    }
}
