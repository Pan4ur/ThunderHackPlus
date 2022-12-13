package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.mrzak34.thunderhack.util.PlacementUtil.placeBlock;

public class CivBreaker extends Module {

    public CivBreaker() {
        super("CivBreaker", "CivBreaker", Category.COMBAT, true, false, false);
    }

    private List<BlockPos> placeList = new ArrayList<>();
    private boolean placing= false;
    private boolean placedCrystal= false;
    private boolean breaking= false;
    private boolean broke= false;
    private EntityPlayer _target= null;;
    private BlockPos b_crystal= null;;
    private BlockPos breakPos= null;;
    private int attempts= 0;;
    private Setting<type> targetType = this.register(new Setting("Target", type.NEAREST));;
    private Setting<mode> breakMode  = this.register(new Setting("Break Mode", mode.Vanilla));;
    private Setting<Boolean> rotate = this.register(new Setting("Rotate", true));;
    private Setting<Integer> startDelay = this.register(new Setting("Start Delay", 1, 0, 10));
    private Setting<Integer> breakDelay = this.register(new Setting("Break Delay", 1, 0, 10));;
    private Setting<Integer> crystalDelay = this.register(new Setting("Crystal Delay", 1, 0, 10));;
    private Setting<Integer> hitDelay = this.register(new Setting("Hit Delay", 3, 0, 10));
    private Setting<Integer> nosleep = this.register(new Setting("Block Delay", 3, 0, 10));;
    private int timer = 0;



    @Override
    public void onEnable() {
        this.init();
    }

    private void init() {
        this.placeList = new ArrayList<>();
        this._target = null;
        this.b_crystal = null;
        this.placedCrystal = false;
        this.placing = false;
        this.breaking = false;
        this.broke = false;
        this.timer = 0;
        this.attempts = 0;
    }

    @SubscribeEvent
    public void onEntitySync(EventPreMotion ev) {
        final int pix = this.findItem(Items.DIAMOND_PICKAXE);
        final int crystal = this.findItem(Items.END_CRYSTAL);
        final int obby = this.findMaterials();
        if (pix == -1 || crystal == -1 || obby == -1) {
            disable();
            return;
        }
        if (_target == null) {
            if (targetType.getValue() == type.NEAREST) {
                _target = Util.mc.world.playerEntities.stream().filter(p -> p.getEntityId() != Util.mc.player.getEntityId()).min(Comparator.comparing(p -> p.getDistance((Entity)Util.mc.player))).orElse(null);
            }
            if (this._target == null) {
                disable();
                return;
            }
        }
        if (placeList.size() == 0 && !this.placing) {
            searchSpace();
            if (placeList.size() == 0) {
                disable();
                return;
            }
        }
        if (!placedCrystal) {
            if (timer < startDelay.getValue()) {
                ++timer;
                return;
            }
            timer = 0;
            doPlace(obby, crystal);
        }
        else if (!breaking) {
            if (timer < breakDelay.getValue()) {
                ++timer;
                return;
            }
            timer = 0;
            if (breakMode.getValue() == mode.Vanilla) {
                Util.mc.player.inventory.currentItem = pix;
                Util.mc.playerController.updateController();
                Util.mc.player.swingArm(EnumHand.MAIN_HAND);
                Util.mc.playerController.onPlayerDamageBlock(this.breakPos, EnumFacing.DOWN);
            }
            else {
                Util.mc.player.swingArm(EnumHand.MAIN_HAND);
                Util.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.breakPos, EnumFacing.DOWN));
                Util.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.breakPos, EnumFacing.DOWN));
            }
            this.breaking = true;
        }
        else if (breaking && !this.broke) {
            if (this.getBlock(this.breakPos) == Blocks.AIR) {
                this.broke = true;
            }
        }
        else if (broke) {
            if (timer < this.crystalDelay.getValue()) {
                ++this.timer;
                return;
            }
            this.timer = 0;
            final Entity bcrystal = Util.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).min(Comparator.comparing(c -> c.getDistance(this._target))).orElse(null);
            if (bcrystal == null) {
                if (this.attempts < this.hitDelay.getValue()) {
                    ++this.attempts;
                    return;
                }
                if (this.attempts < this.nosleep.getValue()) {
                    ++this.attempts;
                    return;
                }
                this.placedCrystal = false;
                this.placeList.add(this.breakPos);
                this.breaking = false;
                this.broke = false;
                this.attempts = 0;
            }
            else {
                Util.mc.player.connection.sendPacket(new CPacketUseEntity(bcrystal));
                this.placedCrystal = false;
                this.placeList.add(this.breakPos);
                this.breaking = false;
                this.broke = false;
                this.attempts = 0;
            }
        }
    }


    private void doPlace(final int obby, final int crystal) {
        this.placing = true;
        if (this.placeList.size() != 0) {
            final int oldslot = Util.mc.player.inventory.currentItem;
            Util.mc.player.inventory.currentItem = obby;
            Util.mc.playerController.updateController();

            placeBlock(this.placeList.get(0), EnumHand.MAIN_HAND, this.rotate.getValue(), false, null,true);
            this.placeList.remove(0);
            Util.mc.player.inventory.currentItem = oldslot;
        }
        else if (!this.placedCrystal) {
            final int oldslot = Util.mc.player.inventory.currentItem;
            if (crystal != 999) {
                Util.mc.player.inventory.currentItem = crystal;
            }
            Util.mc.playerController.updateController();
            Util.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.b_crystal, EnumFacing.UP, (Util.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            Util.mc.player.inventory.currentItem = oldslot;
            this.placedCrystal = true;
        }
    }

    private void searchSpace() {
        final BlockPos tpos = new BlockPos(this._target.posX, this._target.posY, this._target.posZ);
        this.placeList = new ArrayList<>();
        final BlockPos[] offset = { new BlockPos(1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 0), new BlockPos(0, 0, 0) };
        if (this.getBlock(new BlockPos(tpos.getX(), tpos.getY() + 1, tpos.getZ())) != Blocks.AIR || this.getBlock(new BlockPos(tpos.getX(), tpos.getY() + 2, tpos.getZ())) != Blocks.AIR) {
            return;
        }
        final List<BlockPos> posList = new ArrayList<>();
        for (BlockPos blockPos : offset) {
            final BlockPos offsetPos = tpos.add(blockPos);
            final Block block = this.getBlock(offsetPos);
            if (block != Blocks.AIR && !(block instanceof BlockLiquid)) {
                posList.add(offsetPos);
            }
        }
        final BlockPos base = posList.stream().max(Comparator.comparing(b -> this._target.getDistance(b.getX(), b.getY(), b.getZ()))).orElse(null);
        if (base == null) {
            return;
        }
        this.placeList.add(base);
        this.placeList.add(base.add(0, 0, 0));
        this.placeList.add(base.add(0, 0, 0));
        this.placeList.add(tpos.add(1, 1, 0));
        this.breakPos = tpos.add(1, 1, 0);
        this.b_crystal = tpos.add(1, 1, 0);
    }

    private int findMaterials() {
        for (int i = 0; i < 9; ++i) {
            if (Util.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)Util.mc.player.inventory.getStackInSlot(i).getItem()).getBlock() == Blocks.OBSIDIAN) {
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

    public enum type
    {
        NEAREST,
        LOOKING;
    }

    public enum mode
    {
        Vanilla,
        Packet;
    }
}
