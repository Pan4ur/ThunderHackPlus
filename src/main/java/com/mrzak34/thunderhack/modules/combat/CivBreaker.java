package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InteractionUtil;
import com.mrzak34.thunderhack.util.RotationUtil;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class CivBreaker extends Module {

    private final Setting<type> targetType = this.register(new Setting("Target", type.NEAREST));
    private final Setting<mode> breakMode = this.register(new Setting("Break Mode", mode.Vanilla));
    private final Setting<Integer> startDelay = this.register(new Setting("Start Delay", 1, 0, 10));
    private final Setting<Integer> breakDelay = this.register(new Setting("Break Delay", 1, 0, 10));
    private final Setting<Integer> crystalDelay = this.register(new Setting("Crystal Delay", 1, 0, 10));
    private final Setting<Integer> hitDelay = this.register(new Setting("Hit Delay", 3, 0, 10));
    private final Setting<Integer> nosleep = this.register(new Setting("Block Delay", 3, 0, 10));
    private boolean placedCrystal = false;
    private boolean breaking = false;
    private boolean broke = false;
    private EntityPlayer target = null;
    private BlockPos breakPos = null;
    private BlockPos placePos = null;
    private int timer = 0;
    private int attempts = 0;
    public CivBreaker() {
        super("CivBreaker", "CivBreaker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        final int pix = findItem(Items.DIAMOND_PICKAXE);
        if (pix != -1) {
            mc.player.inventory.currentItem = pix;
        }
        target = null;
        placedCrystal = false;
        breaking = false;
        broke = false;
        timer = 0;
        attempts = 0;
    }


    @SubscribeEvent
    public void onEntitySync(EventSync ev) {
        final int pix = findItem(Items.DIAMOND_PICKAXE);
        final int crystal = findItem(Items.END_CRYSTAL);
        final int obby = findMaterials();

        if (pix == -1 || crystal == -1 || obby == -1) {
            Command.sendMessage("No materials!");
            disable();
            return;
        }

        if (target == null) {
            if (targetType.getValue() == type.NEAREST) {
                target = Util.mc.world.playerEntities.stream().filter(p -> p.getEntityId() != Util.mc.player.getEntityId()).min(Comparator.comparing(p -> p.getDistance(Util.mc.player))).orElse(null);
            }
            if (target == null) {
                disable();
                return;
            }
        }

        searchSpace();

        if (!placedCrystal) {
            if (timer < startDelay.getValue()) {
                ++timer;
                return;
            }
            timer = 0;
            doPlace(obby, crystal);
        } else if (!breaking) {
            if (timer < breakDelay.getValue()) {
                ++timer;
                return;
            }
            timer = 0;
            if (breakMode.getValue() == mode.Vanilla) {
                Util.mc.player.inventory.currentItem = pix;
                Util.mc.playerController.updateController();
                Util.mc.player.swingArm(EnumHand.MAIN_HAND);
                Util.mc.playerController.onPlayerDamageBlock(breakPos, EnumFacing.DOWN);
            } else {
                Util.mc.player.swingArm(EnumHand.MAIN_HAND);
                Util.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, EnumFacing.DOWN));
                Util.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, EnumFacing.DOWN));
            }
            breaking = true;
        } else if (!broke) {
            if (getBlock(breakPos) == Blocks.AIR) {
                broke = true;
            }
        } else {
            if (timer < crystalDelay.getValue()) {
                ++timer;
                return;
            }
            timer = 0;
            final Entity bcrystal = Util.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).min(Comparator.comparing(c -> c.getDistance(target))).orElse(null);
            if (bcrystal == null) {
                if (attempts < hitDelay.getValue()) {
                    ++attempts;
                    return;
                }
                if (attempts < nosleep.getValue()) {
                    ++attempts;
                    return;
                }
                placedCrystal = false;
                breaking = false;
                broke = false;
                attempts = 0;
            } else {
                float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), bcrystal.getPositionVector());
                mc.player.rotationYaw = angle[0];
                mc.player.rotationPitch = angle[1];
                Util.mc.player.connection.sendPacket(new CPacketUseEntity(bcrystal));
                placedCrystal = false;
                breaking = false;
                broke = false;
                attempts = 0;
            }
        }
        if (breaking) {
            if (breakPos != null) {
                float[] angle = RotationUtil.getRotations(breakPos, EnumFacing.DOWN);
                mc.player.rotationYaw = angle[0];
                mc.player.rotationPitch = angle[1];
            }
        }
    }


    private void doPlace(final int obby, final int crystal) {
        if (placePos == null) return;
        if (getBlock(placePos) == Blocks.AIR) {
            int oldslot = Util.mc.player.inventory.currentItem;
            Util.mc.player.inventory.currentItem = obby;
            Util.mc.playerController.updateController();
            InteractionUtil.placeBlock(placePos, true);
            Util.mc.player.inventory.currentItem = oldslot;
        } else if (!placedCrystal) {
            int oldslot = Util.mc.player.inventory.currentItem;
            if (crystal != 999) {
                Util.mc.player.inventory.currentItem = crystal;
            }
            Util.mc.playerController.updateController();
            Util.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, (Util.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            Util.mc.player.inventory.currentItem = oldslot;
            placedCrystal = true;
        }
    }

    private void searchSpace() {
        BlockPos tpos = new BlockPos(target.posX, target.posY, target.posZ);

        final BlockPos[] offset = {
                new BlockPos(1, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, -1)
        };

        // burrow check
        if (getBlock(tpos) != Blocks.AIR || getBlock(tpos.add(0, 1, 0)) != Blocks.AIR) {
            return;
        }

        List<BlockPos> posList = new ArrayList<>();
        for (BlockPos blockPos : offset) {
            final BlockPos offsetPos = tpos.add(blockPos);
            final Block block = getBlock(offsetPos);
            final Block block2 = getBlock(offsetPos.add(0, 1, 0));
            final Block block3 = getBlock(offsetPos.add(0, 2, 0));
            if ((block != Blocks.AIR && !(block instanceof BlockLiquid)) && (block2 != Blocks.BEDROCK) && (block3 == Blocks.AIR)) {
                posList.add(offsetPos);
            }
        }

        BlockPos base = posList.stream().max(Comparator.comparing(b -> mc.player.getDistance(b.getX(), b.getY(), b.getZ()))).orElse(null);
        if (base == null) {
            return;
        }
        placePos = base.add(0, 1, 0);
        breakPos = base.add(0, 1, 0);
    }

    private int findMaterials() {
        for (int i = 0; i < 9; ++i) {
            if (Util.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock) Util.mc.player.inventory.getStackInSlot(i).getItem()).getBlock() == Blocks.OBSIDIAN) {
                return i;
            }
        }
        return -1;
    }

    private int findItem(final Item item) {
        if (item == Items.END_CRYSTAL && Util.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return 999;
        }
        for (int i = 0; i < 9; ++i) {
            if (Util.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private Block getBlock(final BlockPos b) {
        return Util.mc.world.getBlockState(b).getBlock();
    }


    public enum type {
        NEAREST,
        LOOKING
    }

    public enum mode {
        Vanilla,
        Packet
    }
}
