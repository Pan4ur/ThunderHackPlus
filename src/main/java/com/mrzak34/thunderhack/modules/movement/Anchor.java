package com.mrzak34.thunderhack.modules.movement;


import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.EntityUtil;
import net.minecraft.init.*;
import net.minecraft.util.math.*;

public class Anchor extends Module
{
    public static boolean Anchoring;
    private final Setting<Integer> pitch = this.register(new Setting<>("Pitch", 60, 0, 90));
    private final Setting<Boolean> disable  = this.register(new Setting<>("AutoDisable", true));
    private final Setting<Boolean> pull = this.register(new Setting<>("Pull", true));;
    int holeblocks;

    public Anchor() {
        super("Anchor",  "если над холкой-движение=0 так понятно?",  Module.Category.MOVEMENT);
    }

    public boolean isBlockHole(final BlockPos blockPos) {
        this.holeblocks = 0;
        if (Anchor.mc.world.getBlockState(blockPos.add(0,  3,  0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(0,  2,  0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(0,  1,  0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(0,  0,  0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(0,  -1,  0)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockPos.add(0,  -1,  0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(1,  0,  0)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockPos.add(1,  0,  0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(-1,  0,  0)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockPos.add(-1,  0,  0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(0,  0,  1)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockPos.add(0,  0,  1)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockPos.add(0,  0,  -1)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockPos.add(0,  0,  -1)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        return this.holeblocks >= 9;
    }

    public Vec3d GetCenter(final double d,  final double d2,  final double d3) {
        final double d4 = Math.floor(d) + 0.5;
        final double d5 = Math.floor(d2);
        final double d6 = Math.floor(d3) + 0.5;
        return new Vec3d(d4,  d5,  d6);
    }


    public void onUpdate() {
        if (Anchor.mc.world == null) {
            return;
        }
        if (Anchor.mc.player.rotationPitch >= this.pitch.getValue()) {
            if (this.isBlockHole(this.getPlayerPos().down(1)) || this.isBlockHole(this.getPlayerPos().down(2)) || this.isBlockHole(this.getPlayerPos().down(3)) || this.isBlockHole(this.getPlayerPos().down(4))) {
                Anchor.Anchoring = true;
                if (!this.pull.getValue()) {
                    Anchor.mc.player.motionX = 0.0;
                    Anchor.mc.player.motionZ = 0.0;
                }
                else {
                    final Vec3d center = this.GetCenter(Anchor.mc.player.posX,  Anchor.mc.player.posY,  Anchor.mc.player.posZ);
                    final double d = Math.abs(center.x - Anchor.mc.player.posX);
                    final double d2 = Math.abs(center.z - Anchor.mc.player.posZ);
                    if (d > 0.1 || d2 > 0.1) {
                        final double d3 = center.x - Anchor.mc.player.posX;
                        final double d4 = center.z - Anchor.mc.player.posZ;
                        Anchor.mc.player.motionX = d3 / 2.0;
                        Anchor.mc.player.motionZ = d4 / 2.0;
                    }
                }
            }
            else {
                Anchor.Anchoring = false;
            }
        }
        if (this.disable.getValue() && EntityUtil.isSafe(Anchor.mc.player)) {
            this.disable();
        }
    }

    public void onDisable() {
        Anchor.Anchoring = false;
        this.holeblocks = 0;
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Anchor.mc.player.posX),  Math.floor(Anchor.mc.player.posY),  Math.floor(Anchor.mc.player.posZ));
    }
}