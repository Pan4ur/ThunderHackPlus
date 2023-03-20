package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.mixin.mixins.AccessorMinecraft;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoMine extends Module {

    public Setting<Boolean> autodisable = this.register(new Setting<Boolean>("AutoDisable", true));
    public Setting<Boolean> switchbool = this.register(new Setting<Boolean>("Switch", true));
    public Setting<Boolean> requirepickaxe = this.register(new Setting<Boolean>("RequirePick", true));
    public Setting<Boolean> focused = this.register(new Setting<Boolean>("Focused", true));
    private final Setting<Mode> mode = register(new Setting("Mode", Mode.FEET));
    private BlockPos blockpos = null;


    public AutoMine() {
        super("AutoMine", "AutoMine", Category.PLAYER);
    }


    public static List<BlockPos> blockPosList(BlockPos blockPos) {
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        arrayList.add(blockPos.add(1, 0, 0));
        arrayList.add(blockPos.add(-1, 0, 0));
        arrayList.add(blockPos.add(0, 0, 1));
        arrayList.add(blockPos.add(0, 0, -1));
        return arrayList;
    }


    public static Vec3d vec3dPosition() {
        return new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
    }


    public static float[] shitMethod(Vec3d vec3d) {
        Vec3d vec3d2 = vec3dPosition();
        Vec3d vec3d3 = vec3d;
        double d = vec3d3.x - vec3d2.x;
        double d2 = vec3d3.y - vec3d2.y;
        double d3 = vec3d3.z - vec3d2.z;
        double d4 = d;
        double d5 = d3;
        double d6 = Math.sqrt(d4 * d4 + d5 * d5);
        float f = (float) Math.toDegrees(Math.atan2(d3, d)) - 90.0f;
        float f2 = (float) (-Math.toDegrees(Math.atan2(d2, d6)));
        float[] fArray = new float[2];
        fArray[0] = mc.player.rotationYaw + MathHelper.wrapDegrees(f - mc.player.rotationYaw);
        fArray[1] = mc.player.rotationPitch + MathHelper.wrapDegrees(f2 - mc.player.rotationPitch);
        return fArray;
    }


    public static float[] shitMethod2(BlockPos blockPos) {
        Vec3d vec3d = vec3dPosition();
        Vec3d vec3d2 = new Vec3d(blockPos).add(0.5, 0.5, 0.5);
        double d = vec3d.squareDistanceTo(vec3d2);
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        if (0 < n) {
            EnumFacing enumFacing = enumFacingArray[n2];
            Vec3d vec3d3 = vec3d2.add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));
            return shitMethod(vec3d3);
        }
        return shitMethod(vec3d2);
    }


    public static EnumFacing getFacing(BlockPos blockPos) {
        Vec3d vec3d = vec3dPosition();
        Vec3d vec3d2 = new Vec3d(blockPos).add(0.5, 0.5, 0.5);
        double d = vec3d.squareDistanceTo(vec3d2);
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        if (0 < n) {
            EnumFacing enumFacing = enumFacingArray[n2];
            return enumFacing;
        }
        return EnumFacing.UP;
    }

    @Override
    public void onEnable() {
        blockpos = null;
    }

    @Override
    public void onDisable() {
        blockpos = null;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
    }

    @Override
    public void onUpdate() {
        if (mode.getValue() == Mode.CONTINIOUS) {
            if (!focused.getValue())
                ((AccessorMinecraft) mc).setLeftClickCounter(0);

            ((AccessorMinecraft) mc).invokeSendClickBlockToController(true);
        }
    }

    @SubscribeEvent
    public void onPreMotion(EventSync event) {
        if (mode.getValue() == Mode.CONTINIOUS) return;
        if (!switchbool.getValue() || checkPickaxe()) {
            if (blockpos != null) {
                if (mc.world.getBlockState(blockpos).getBlock().equals(Blocks.AIR)) {
                    if (autodisable.getValue()) {
                        disable();
                        return;
                    }

                    blockpos = null;
                }
            }

            BlockPos blockpos2 = null;
            for (Entity obj : mc.world.playerEntities.stream().filter(player ->
                    player != mc.player && !Thunderhack.friendManager.isFriend(player.getName()) && Float.compare(mc.player.getDistance(player), 7.0f) < 0).collect(Collectors.toList())) {
                BlockPos pos = new BlockPos(obj.getPositionVector());
                if (!checkBlockPos(pos)) continue;

                for (BlockPos pos2 : blockPosList(pos)) {
                    if (!(mc.world.getBlockState(pos2).getBlock() instanceof BlockObsidian)) continue;
                    if (!mc.world.getBlockState(pos2.add(0, 1, 0)).getMaterial().equals(Material.AIR)) continue;

                    double dist = mc.player.getDistance(pos2.getX(), pos2.getY(), pos2.getZ());
                    if (dist < 5.0) {
                        blockpos2 = pos2;
                        break;
                    }
                }
            }

            if (blockpos2 != null) {
                if (switchbool.getValue() && (InventoryUtil.getPicatHotbar() != -1)) {
                    mc.player.inventory.currentItem = InventoryUtil.getPicatHotbar();
                }

                float[] rotation = shitMethod2(blockpos2);
                mc.player.rotationYaw = rotation[0];
                mc.player.rotationPitch = rotation[1];

                if (!requirepickaxe.getValue() || mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE)) {
                    if (blockpos != null) {
                        if (blockpos.equals(blockpos2)) {
                            if (Thunderhack.moduleManager.getModuleByClass(Speedmine.class).isEnabled())
                                return;
                        }
                    }

                    mc.playerController.onPlayerDamageBlock(blockpos2, getFacing(blockpos2));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.blockpos = blockpos2;
                }
            }
        }
    }

    public boolean checkPickaxe() {
        Item item = mc.player.getHeldItemMainhand().getItem();

        return item.equals(Items.DIAMOND_PICKAXE) || item.equals(Items.IRON_PICKAXE) ||
                item.equals(Items.GOLDEN_PICKAXE) || item.equals(Items.STONE_PICKAXE) ||
                item.equals(Items.WOODEN_PICKAXE);
    }


    public boolean checkValidBlock(Block block) {
        return block.equals(Blocks.OBSIDIAN) || block.equals(Blocks.BEDROCK);
    }

    public boolean checkBlockPos(BlockPos blockPos) {
        Block block = mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock();
        Block block2 = mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock();
        Block block3 = mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock();
        Block block4 = mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock();
        Block block5 = mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock();
        if (mc.world.isAirBlock(blockPos)) {
            if (mc.world.isAirBlock(blockPos.add(0, 1, 0))) {
                if (mc.world.isAirBlock(blockPos.add(0, 2, 0))) {
                    if (checkValidBlock(block)) {
                        if (checkValidBlock(block2)) {
                            if (checkValidBlock(block3)) {
                                if (checkValidBlock(block4)) {
                                    return checkValidBlock(block5);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public enum Mode {
        FEET, CONTINIOUS
    }
}
