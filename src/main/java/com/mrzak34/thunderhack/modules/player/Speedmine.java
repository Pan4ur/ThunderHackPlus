package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.event.events.DamageBlockEvent;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;


import java.awt.*;


public class Speedmine
        extends Module {

    public Speedmine() {super("Speedmine", "пакетмайн", Module.Category.PLAYER, true, false, false);}




    private Setting<Mode> mode = register(new Setting("Mode", Mode.Packet));

    public enum Mode {
        Packet, Damage, Instant, Breaker, PacketRebreak,NexusGrief
    }





//    ColorSetting progressColor = registerColor("Progress Color", new GSColor(255, 255, 255), () -> (mode.getValue().equals("Breaker") || mode.getValue().equals("Packet")) && showProgress.getValue());
// ColorSetting blockColor = registerColor("Block Color", new GSColor(255, 0, 0), () -> (mode.getValue().equals("Breaker") || mode.getValue().equals("Packet")) && display.getValue());
//    ColorSetting doneColor = registerColor("Done Color", new GSColor(0, 255, 0), () -> (mode.getValue().equals("Breaker") || mode.getValue().equals("Packet")) && display.getValue());


    private final Setting<Float> startDamage = this.register(new Setting<Float>("Start Damage", Float.valueOf(0.1f), Float.valueOf(0.0f), Float.valueOf(1.0f) , v -> mode.getValue() == Mode.Damage));
    private final Setting<Float> endDamage = this.register(new Setting<Float>("End Damage", Float.valueOf(0.9f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> mode.getValue() == Mode.Damage));
    public Setting<Boolean> display  = this.register(new Setting<Boolean>("Display", false));
    public Setting<Boolean> forceRotation = this.register(new Setting<Boolean>("Force Rotation", false, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet));
    public Setting<Boolean> startPick = this.register(new Setting<Boolean>("Start Pick", false, v -> mode.getValue() == Mode.Breaker));
    public Setting<Boolean> onlyOnPick = this.register(new Setting<Boolean>("Only On Pick", false, v -> mode.getValue() == Mode.Breaker));
    public Setting<Boolean> ignoreChecks = this.register(new Setting<Boolean>("Spammer", false, v -> mode.getValue() == Mode.Breaker));
    public Setting<Boolean> spammer = this.register(new Setting<Boolean>("Rotate", true, v -> mode.getValue() == Mode.Breaker));
    public Setting<Boolean> silentSwitch = this.register(new Setting<Boolean>("Silent Switch", false, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet ));
    public Setting< Boolean > placeCrystal = this.register ( new Setting <> ( "PlaceCrystal" , false ) );

    public Setting<Boolean> stopEating = this.register(new Setting<Boolean>("Stop Eating", false, v -> (mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet) && silentSwitch.getValue() ));
    public Setting<Boolean> switchBack = this.register(new Setting<Boolean>("Switch Back", false, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet ));
    public Setting<Boolean> switchPick = this.register(new Setting<Boolean>("Switch Pick", false, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet ));
    public Setting<Boolean> continueBreaking = this.register(new Setting<Boolean>("Continue Breaking", true));
    public Setting<Boolean> showProgress = this.register(new Setting<Boolean>("Show Progress", true, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet));

    public Setting<Boolean> continueBreakingAlways = this.register(new Setting<Boolean>("Always Continue", false, v-> continueBreaking.getValue()));
    public Setting<Boolean> disableContinueShift = this.register(new Setting<Boolean>("Disable Continue Shift", true, v -> continueBreaking.getValue()));


   // private final Setting<Integer> width = this.register(new Setting<Integer>("Width", 1, 1, 10));
    private final Setting<Integer> rangeDisableBreaker = this.register(new Setting<Integer>("Range Disable Breaker", 15, 6, 50, v -> mode.getValue() == Mode.Breaker ));
    private final Setting<Integer> breakerTickDelay = this.register(new Setting<Integer>("Breaker Delay", 0, 0, 75 , v -> mode.getValue() == Mode.Breaker));
    private final Setting<Integer> spammerTickDelay = this.register(new Setting<Integer>("Spammer Delay", 0, 0, 75, v -> mode.getValue() == Mode.Breaker));
    private final Setting<Integer> pickStill = this.register(new Setting<Integer>("Pick Switch Still", 20, 0, 30, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet));
    private final Setting<Integer> pickTickSwitch = this.register(new Setting<Integer>("Pick Switch Destroy", 75, 0, 200, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet));
    private final Setting<Integer> resetTickDestroy = this.register(new Setting<Integer>("Tick Reset Destroy", 0, 0, 50, v -> mode.getValue() == Mode.Breaker ||mode.getValue() == Mode.Packet));


    private int tick = 99;
    private int tickSpammer = 0;
    private int oldslot;
    private int breakTick = 0;
    private int wait = 100;


    private BlockPos lastBlock = null;
    private BlockPos continueBlock = null;
    private boolean pickStillBol = false,
            ready = false;
    private EnumFacing direction;
    private boolean minedBefore = false;
    private int reseTick;

    private Vec3d lastHitVec = null;



    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPreMotion event) {
        if (lastHitVec == null || !forceRotation.getValue() || lastBlock == null)
            return;
        Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
        Util.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotation.x,rotation.y,Speedmine.mc.player.onGround));
    }

    boolean broke = false;

    boolean needplacecrys = true;

    @Override
    public void onUpdate() {

        if(lastBlock == null){
            needplacecrys = true;
        }
        if(lastBlock != null && placeCrystal.getValue() && needplacecrys) {
            if (mc.world.getBlockState(lastBlock).getBlock() == Blocks.AIR){
                Vec3d vec = new Vec3d(lastBlock).add(0.0, -1.0, 0.0);
                SilentRotaionUtil.lookAtVector(vec);
                int crystalSlot = CrystalUtils.getCrystalSlot();
                if (crystalSlot == -1) {
                    Command.sendMessage("No crystals found!");
                    toggle();
                    return;
                }

                if (mc.player.inventory.currentItem != crystalSlot) {
                    mc.player.inventory.currentItem = crystalSlot;
                    mc.playerController.updateController();
                }
                BlockUtils.rightClickBlock(lastBlock.add(0.0,-1.0,0.0), mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), EnumHand.MAIN_HAND, EnumFacing.UP, true);
                needplacecrys = false;
            }
        }


        if (continueBreaking.getValue()) {
            if (continueBlock != null) {
                if (disableContinueShift.getValue() && mc.gameSettings.keyBindSneak.isKeyDown())
                    continueBlock = null;
                else {
                    if (BlockUtils.getBlockgs(continueBlock) instanceof BlockAir) {
                        broke = true;
                    }
                    if (!(BlockUtils.getBlockgs(continueBlock) instanceof BlockAir) && (broke || continueBreakingAlways.getValue())) {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.playerController.onPlayerDamageBlock(continueBlock, EnumFacing.UP);
                        broke = false;
                    }
                }
            }
        }

        if (tick != 99) {
            if (tick++ >= wait) {
                int prev = mc.player.inventory.currentItem;
                ready = true;
                if (switchPick.getValue() && oldslot != -1) {
                    if (silentSwitch.getValue()) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
                        mc.playerController.updateController();
                        if (lastBlock != null && direction != null)
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastBlock, direction));
                        if (stopEating.getValue() && mc.player.isHandActive())
                            mc.player.stopActiveHand();
                    }
                    else
                        mc.player.inventory.currentItem = oldslot;
                    oldslot = -1;
                }
                // If we have to change
                if (!pickStillBol) {
                    // So, in case we have to switch back
                    if (pickTickSwitch.getValue() != 0 && switchPick.getValue()) {
                        // New wait
                        wait = pickStill.getValue();
                        // Reset tick
                        tick = 0;
                        // New oldslot
                        oldslot = prev;
                        // do not enter this if again basically
                        pickStillBol = true;
                    } else {
                        // Else, just finish
                        tick = 99;
                        if (silentSwitch.getValue()) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(prev));
                            mc.playerController.updateController();
                        }
                        else {
                            mc.player.inventory.currentItem = prev;
                        }
                    }
                    // Just finish in case we have not to switch
                } else
                    tick = 99;
            }
        }

        mc.playerController.blockHitDelay = 0;
        if (!onlyOnPick.getValue() || mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)
            if (mode.getValue() == Mode.Breaker) {
                if (lastBlock != null && ((spammer.getValue() && tickSpammer++ >= spammerTickDelay.getValue()))) {
                    tickSpammer = 0;
                    if (BlockUtils.getBlockgs(lastBlock) instanceof BlockAir) {
                        minedBefore = true;
                        reseTick = 0;
                        lastHitVec = null;
                    }
                    // If we have mined it before
                    if (minedBefore) {
                        if (resetTickDestroy.getValue() != 0 && reseTick++ >= resetTickDestroy.getValue() && !(BlockUtils.getBlockgs(lastBlock) instanceof BlockAir)) {
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, lastBlock, direction));
                            breakerBreak();
                            reseTick = 0;
                            minedBefore = false;
                            return;
                        }
                        if (ignoreChecks.getValue() || !(BlockUtils.getBlockgs(lastBlock) instanceof BlockAir)) {
                            if (forceRotation.getValue())
                                setVec3d(lastBlock, direction);
                            // Get distance, if it's >=, then delete it
                            if (mc.player.getDistanceSq(lastBlock) >= rangeDisableBreaker.getValue())
                                lastBlock = null;
                            else {
                                // Finally break it
                                breakerBreak();
                            }
                        }
                    }
                }
            }
    }

    private void breakerBreak() {
        // Get item
        Item item = mc.player.inventory.getCurrentItem().getItem();
        // OldSlot
        int oldSlot = -1;
        // Switch to pick
        if (!(item instanceof ItemPickaxe) && minedBefore && (switchBack.getValue() || switchPick.getValue())) {
            oldSlot = mc.player.inventory.currentItem;
            int slot = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
            // Who know? Do you want to get kicked?
            if (slot != -1)
                mc.player.inventory.currentItem = slot;
        }
        // Send STOP_DESTROY_BLOCK
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                lastBlock, direction));
        // If oldSlot != -1, so we have switchback
        if (oldSlot != -1 && switchBack.getValue()) {
            // Allow to switchback
            tick = 0;
            oldslot = oldSlot;
            // Since we use it for both packet and breaker, we have to do these checks
            if (!minedBefore || mode.getValue() == Mode.Packet || mode.getValue() == Mode.PacketRebreak) {
                wait = pickTickSwitch.getValue();
                pickStillBol = !switchBack.getValue();
            } else
                wait = pickStill.getValue();
        }

    }

    boolean first = true;

    @SubscribeEvent
    public void onBreakBlock(DamageBlockEvent event){

        if (mc.world == null || mc.player == null) return;
        if (!canBreak(event.getBlockPos()) || event.getBlockPos() == null) return;


        if (forceRotation.getValue())
            setVec3d(event.getBlockPos(), event.getEnumFacing());

        if (continueBreaking.getValue())
            continueBlock = event.getBlockPos();

        switch (mode.getValue()) {
            case Packet: {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                    event.setCanceled(true);
                    lastBlock = event.getBlockPos();
                    direction = event.getEnumFacing();
                    oldslot = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
                    tick = 0;
                    wait = pickTickSwitch.getValue();
                    ready = false;
                    pickStillBol = false;
                    first = false;
                break;
            }

            case PacketRebreak: {
                if(first) {
                    Command.sendMessage("1");
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                    event.setCanceled(true);
                    lastBlock = event.getBlockPos();
                    direction = event.getEnumFacing();
                    oldslot = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
                    tick = 0;
                    wait = pickTickSwitch.getValue();
                    ready = false;
                    pickStillBol = false;
                    first = false;
                } else {
                    Command.sendMessage("2");
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                    event.setCanceled(true);
                    lastBlock = event.getBlockPos();
                    direction = event.getEnumFacing();
                    oldslot = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
                    tick = 0;
                    wait = pickTickSwitch.getValue();
                    ready = false;
                    pickStillBol = false;
                    first = true;
                }
                break;
            }
            case Damage: {
                if (mc.playerController.curBlockDamageMP < startDamage.getValue())
                    mc.playerController.curBlockDamageMP = startDamage.getValue().floatValue();

                if (mc.playerController.curBlockDamageMP >= endDamage.getValue()) {
                    mc.playerController.curBlockDamageMP = 1.0f;
                }
                break;
            }
            case NexusGrief: {
                if(mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                    if (mc.playerController.curBlockDamageMP < 0.17f)
                        mc.playerController.curBlockDamageMP = 0.17f;

                    if (mc.playerController.curBlockDamageMP >= 0.83) {
                        mc.playerController.curBlockDamageMP = 1f;
                    }
                } else if(mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe){
                    if (mc.playerController.curBlockDamageMP < 0.17f)
                        mc.playerController.curBlockDamageMP = 0.17f;

                    if (mc.playerController.curBlockDamageMP >= 1f) {
                        mc.playerController.curBlockDamageMP = 1.0f;
                    }
                } else if(mc.player.getHeldItemMainhand().getItem() == Items.STONE_SHOVEL || mc.player.getHeldItemMainhand().getItem() == Items.IRON_SHOVEL || mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SHOVEL ){
                    if (mc.playerController.curBlockDamageMP < 0.17f)
                        mc.playerController.curBlockDamageMP = 0.17f;

                    if (mc.playerController.curBlockDamageMP >= 1f) {
                        mc.playerController.curBlockDamageMP = 1.0f;
                    }
                }
                break;
            }
            case Instant: {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
                mc.playerController.onPlayerDestroyBlock(event.getBlockPos());
                mc.world.setBlockToAir(event.getBlockPos());
                break;
            }
            case Breaker: {
                breakerAlgo(event);
                break;
            }
        }
    }

    private void setVec3d(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        lastHitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
    }

    private void breakerAlgo(DamageBlockEvent event) {
        // Checks if we have already entered here
        if (lastBlock == null || event.getBlockPos().x != lastBlock.x || event.getBlockPos().y != lastBlock.y || event.getBlockPos().z != lastBlock.z) {
            if (startPick.getValue()) {
                int pick = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
                if (pick != -1)
                    mc.player.inventory.currentItem = pick;
            }
            // Start breaking normally
            minedBefore = false;
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getBlockPos(), event.getEnumFacing()));
            lastBlock = event.getBlockPos();
            direction = event.getEnumFacing();
        }
        if (breakerTickDelay.getValue() <= breakTick++) {
            breakerBreak();
            event.setCanceled(true);
            breakTick = 0;
        }
        wait = pickTickSwitch.getValue();
        ready = false;
        tick = 0;
        if (switchPick.getValue()) {
            oldslot = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
            pickStillBol = !switchBack.getValue();
        }
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1;
    }

    public void onDisable() {
        breakTick = 0;
        continueBlock = null;
        first = true;
    }

    public void onRender3D(Render3DEvent event) {
        if (lastBlock != null)
            if (mc.player.getDistanceSq(lastBlock) >= rangeDisableBreaker.getValue()) {
                lastBlock = null;
            } else if (display.getValue()) {
                if (mode.getValue() == Mode.Breaker || (mode.getValue() == Mode.Packet || mode.getValue() == Mode.PacketRebreak && !(BlockUtils.getBlockgs(lastBlock) instanceof BlockAir)) || (mode.getValue()== Mode.Packet || mode.getValue() == Mode.PacketRebreak)) {
                    RenderUtil.drawBlockOutline(lastBlock, new Color(175, 175, 255), 2f, false);
                    if (showProgress.getValue()) {
                        int prognum = (int) ((((float) tick / pickTickSwitch.getValue() * 100) / Blocks.OBSIDIAN.blockHardness) * mc.world.getBlockState(lastBlock).getBlock().blockHardness);
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
                }
                else lastBlock = null;
            }


    }

}

