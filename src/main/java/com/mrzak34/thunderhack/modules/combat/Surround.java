package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.EventEntitySpawn;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.IMinecraft;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.SilentRotationUtil;
import com.mrzak34.thunderhack.util.math.DamageUtil;
import com.mrzak34.thunderhack.util.phobos.ThreadUtil;
import com.mrzak34.thunderhack.util.surround.BlockPosWithFacing;
import com.mrzak34.thunderhack.util.surround.ModeUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Surround extends Module {

    private String really = " TheKisDevs & LavaHack Development owns you, and I am sorry, because it is uncrackable <3";

    /**
    *   Thanks to @h1tm4nqq for deobf (https://github.com/h1tm4nqq/LavaHack-LavaHurk--deobf)
    *   Made readable and runable by me :P
    **/

    public Surround() {super("Surround", "окружает тебя обсой","surrounds you", Category.COMBAT);}

    public final Setting<Boolean> multiThread = register(new Setting<>("Multi Thread", false));
    private final Setting<Integer> delay = register(new Setting<>("Delay", 15, 0, 100));

    private final Setting<EventMode> eventMode = register(new Setting<>("Event Mode", EventMode.SyncEvent));
    public enum EventMode {Tick, Update, SyncEvent}

    private final Setting<Mode> mainMode = register(new Setting<>("Mode", Mode.Normal));
    public enum Mode { High, AntiFacePlace, Dynamic, Cubic,Safe,SemiSafe,Strict,Normal }

    private final Setting<BlockMode> blockMode = register(new Setting<>("Block", BlockMode.Obsidian));
    public enum BlockMode { Obsidian, EnderChest }

    private final Setting<Switch> switchMode = register(new Setting<>("Switch", Switch.Silent));
    public enum Switch { None, Vanilla, Packet, Silent }

    private final Setting<SwitchWhen> switchWhen = register(new Setting<>("SwitchWhen", SwitchWhen.Place));
    public enum SwitchWhen {Place, RunSurround}

    private final Setting<CBTimings> cBTimingsMode = register(new Setting<>("CB Timings", CBTimings.Adaptive));
    public enum CBTimings { Sequential, Adaptive }

    private final Setting<CbMode> cbMode = register(new Setting<>("CbMode", CbMode.SurroundBlocks));
    public enum CbMode { SurroundBlocks, Area }

    private final Setting<CBRotateMode> cBRotateMode = register(new Setting<>("CBRotateMode", CBRotateMode.Packet));
    public enum CBRotateMode { Client, Packet, Both }

    private final Setting<Rotate> rotateMode = register(new Setting<>("Rotate", Rotate.Packet));
    public enum Rotate { None, Packet, Silent }

    private final Setting<detectEntityMode> detectEntity = register(new Setting<>("DetectEntity", detectEntityMode.RemoveEntity));
    public enum detectEntityMode {Off, RemoveEntity,SetDead,Both}

    private final Setting<ToggleMode> toggleMode = register(new Setting<>("Toggle", ToggleMode.OnComplete));
    public enum ToggleMode { Never, OffGround, OnComplete, Combo, PositiveYChange, YChange}

    public Setting<Boolean> syncronized = register(new Setting<>("Syncronized", false));
    public Setting<Boolean> allEntities = register(new Setting<>("AllEntities", false));
    public Setting<Boolean> extension = register(new Setting<>("Extension", false));
    public Setting<Boolean> safeDynamic = register(new Setting<>("Safe Dynamic", false));
    public Setting<Boolean> rangeCheck = register(new Setting<>("RangeCheck", false));
    public Setting<Boolean> smartBlock = register(new Setting<>("Smart Block", false));
    public Setting<Boolean> safeEChest = register(new Setting<>("Safe E Chest", false));
    public Setting<Boolean> center = register(new Setting<>("Center", true));
    public Setting<Boolean> smartCenter = register(new Setting<>("SmartCenter", false));
    public Setting<Boolean> smartHelping = register(new Setting<>("SmartHelping", false));
    public Setting<Boolean> fightCA = register(new Setting<>("FightCA", true));
    public Setting<Boolean> detectSound = register(new Setting<>("DetectSound", true));
    public Setting<Boolean> onEntityDestruction = register(new Setting<>("OnEntityDestruction", false));
    public Setting<Boolean> antiCity = register(new Setting<>("AntiCity", false));
    public Setting<Boolean> manipulateWorld = register(new Setting<>("ManipulateWorld", false));
    public Setting<Boolean> postReceive = register(new Setting<>("PostReceive", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet", false));
    public Setting<Boolean> feetBlocks = register(new Setting<>("FeetBlocks", false));
    public Setting<Boolean> down = register(new Setting<>("Down", false));
    public Setting<Boolean> inAir = register(new Setting<>("InAir", false));
    public Setting<Boolean> airMotion = register(new Setting<>("InAirMotionStop", false));
    public Setting<Boolean> crystalBreaker = register(new Setting<>("CrystalBreaker", true));
    public Setting<Boolean> cBRotate = register(new Setting<>("CBRotate", false));
    public Setting<Boolean> cBPacket = register(new Setting<>("CBPacket", false));
    public Setting<Boolean> cientSide = register(new Setting<>("ClientSide", false));
    public final Setting<Boolean> cbTerrain = register(new Setting<>("CbTerrain", true));
    public  final Setting<Boolean> cbNoSuicide = register(new Setting<>("CbNoSuicide", true));
    private final Setting<Float> heightLimit = register(new Setting<>("HeightLimit", 256.0f, 0f, 256.0f));
    private final Setting<Float> cBSequentialDelay = register(new Setting<>("CBSequentialDelay", 1f, 0f, 10f));
    private final Setting<Float> cBRange = register(new Setting<>("CBRange", 3f, 0f, 10f));
    private final Setting<Integer> cBDelay = register(new Setting<>("CBDelay", 0, 0, 500));
    private final Setting<Float> placeRange = register(new Setting<>("PlaceRange", 5f, 2f, 10f));
    private final Setting<Float> toggleHeight = register(new Setting<>("ToggleHeight", 0.4f, 0.1f, 1f));

    private ModeUtil modeUtil = new ModeUtil();
    private Timer breakTimer = new Timer();
    private boolean haveBlock = false;
    private Function blockState = Surround::getBlockState;
    private double pos_Y;
    private static final ScheduledExecutorService THREAD;
    static {
        THREAD = ThreadUtil.newDaemonScheduledExecutor("SURROUND");
    }


    @SubscribeEvent(priority=EventPriority.HIGH)
    public void onTick(TickEvent tickEvent) {
        if (eventMode.getValue() != EventMode.Tick) {
            return;
        }
        if (syncronized.getValue()) {
            doSynchronized();
            return;
        }
        doNonSynchronized();
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        if (eventMode.getValue() != EventMode.Update) return;
        if (syncronized.getValue()) {
            doSynchronized();
        } else {
            doNonSynchronized();
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public void onSync(EventSync e) {
        if (eventMode.getValue() != EventMode.SyncEvent) {
            return;
        }
        if (syncronized.getValue()) {
            doSynchronized();
            return;
        }
        doNonSynchronized();
    }

    @Override
    public void onEnable() {
        breakTimer.reset();
        if (fullNullCheck()) return;
        pos_Y = mc.player.posY;
        if (center.getValue() && !setCenter())
            if(toggleMode.getValue() != ToggleMode.Never)
                disable();
    }

    @SubscribeEvent
    public void onPacketReceive2(PacketEvent.Receive event) {
        if (!onEntityDestruction.getValue()) {
            return;
        }
        if (!(event.getPacket() instanceof SPacketDestroyEntities)) {
            return;
        }
        SPacketDestroyEntities sPacketDestroyEntities = event.getPacket();
        int[] nArray = sPacketDestroyEntities.getEntityIDs();
        for (int n2 : nArray) {
            mc.world.removeEntityFromWorld(n2);
        }
        if (syncronized.getValue()) {
            doSynchronized();
            return;
        }
        doNonSynchronized();
    }

    @SubscribeEvent
    public void onPacketReceivePost(PacketEvent.ReceivePost e) {
        if (!postReceive.getValue()) {
            return;
        }
        doAntiCity(new PacketEvent.Receive(e.getPacket()));
    }

    @SubscribeEvent
    public void onPacketReceivePre(PacketEvent.Receive e) {
        if (postReceive.getValue()) {
            return;
        }
        doAntiCity(e);
    }

    @SubscribeEvent
    public void onSpawnCrystal(EventEntitySpawn event) {
        if (!fightCA.getValue()) {
            return;
        }
        if (detectEntity.getValue() == detectEntityMode.Off) {
            return;
        }
        Entity entity = event.getEntity();
        List<BlockPos> list = modeUtil.getBlockPositions(mainMode.getValue());
        if (!checkIntersections(entity.getEntityBoundingBox(), list)) {
            return;
        }
        if (detectEntity.getValue() == detectEntityMode.SetDead || detectEntity.getValue() == detectEntityMode.Off) {
            entity.setDead();
        }
        if (detectEntity.getValue() == detectEntityMode.RemoveEntity || detectEntity.getValue() == detectEntityMode.Both) {
            mc.world.removeEntity(entity);
        }
        if (syncronized.getValue()) {
            doSynchronized();
            return;
        }
        doNonSynchronized();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect sPacketSoundEffect = event.getPacket();
            if (sPacketSoundEffect.getSound() == SoundEvents.BLOCK_STONE_PLACE) {
                event.setCanceled(true);
            }
        }
        if (!fightCA.getValue()) {
            return;
        }
        if (!detectSound.getValue()) {
            return;
        }
        if (!(event.getPacket() instanceof SPacketSoundEffect)) {
            return;
        }
        SPacketSoundEffect sPacketSoundEffect = event.getPacket();
        if (sPacketSoundEffect.getSound() != SoundEvents.ENTITY_GENERIC_EXPLODE) {
            return;
        }
        Vec3d vec3d = new Vec3d(sPacketSoundEffect.getX(), sPacketSoundEffect.getY(), sPacketSoundEffect.getZ());
        if (!doesCrystalWantToFuckUs(vec3d, modeUtil.getBlockPositions(mainMode.getValue()))) {
            return;
        }
        if (syncronized.getValue()) {
            doSynchronized();
            return;
        }
        doNonSynchronized();
    }


    private void doNonSynchronized() {
        if(multiThread.getValue()) {
            THREAD.schedule(this::handleSurround, delay.getValue(), TimeUnit.MILLISECONDS);
        } else {
            handleSurround();
        }
    }

    private synchronized void doSynchronized() {
        if(multiThread.getValue()) {
            THREAD.schedule(this::handleSurround, delay.getValue(), TimeUnit.MILLISECONDS);
        } else {
            handleSurround();
        }
    }

    private void handleSurround() {
        if(fullNullCheck()) return;
        if(mc.player.ticksExisted < 60) return;
        if(!inAir.getValue() || mc.player.onGround) {
            if(inAir.getValue() && airMotion.getValue() && !mc.player.onGround){
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
            int n2 = mc.player.inventory.currentItem;
            int n = getSlotWithBestBlock();
            if (n == -1) {
                return;
            }
            if (switchWhen.getValue() == SwitchWhen.RunSurround) {
                SwitchMethod(switchMode.getValue(), n, false);
            }
            List<BlockPos> positions = modeUtil.getBlockPositions(mainMode.getValue());
            doPlace(positions);
            if (crystalBreaker.getValue()) {
                doCrystalBreaker(positions);
            }
            if (switchWhen.getValue() == SwitchWhen.RunSurround) {
                SwitchMethod(switchMode.getValue(), n2, true);
            }
        }
        if(mc.player.posY > pos_Y + toggleHeight.getValue() && toggleMode.getValue() == ToggleMode.PositiveYChange){
            disable();
        }
        if(mc.player.posY != pos_Y && toggleMode.getValue() == ToggleMode.YChange){
            disable();
        }
        if(((mc.player.posY != pos_Y) || !mc.player.onGround) && toggleMode.getValue() == ToggleMode.Combo){
            disable();
        }
        if(toggleMode.getValue() == ToggleMode.OnComplete){
            disable();
        }
        if(!mc.player.onGround && toggleMode.getValue() == ToggleMode.OffGround){
            disable();
        }
    }

    @Override
    public void onDisable() {
        blockState = Surround::getBlockState;
        breakTimer.reset();
    }

    private void doCrystalBreaker(List<BlockPos> list) {
        if (!breakTimer.passedMs(cBDelay.getValue())) {
            return;
        }
        float[] fArray = new float[2];
        fArray[0] = mc.player.rotationYaw;
        fArray[1] = mc.player.rotationPitch;
        HashSet<EntityEnderCrystal> hashSet = new HashSet<>(64);
        if (cbMode.getValue() == CbMode.Area) {
            double d = cBRange.getValue();
            double d2 = mc.player.posX - d;
            double d3 = mc.player.posY - d;
            double d4 = mc.player.posZ - d;
            double d5 = mc.player.posX + d;
            double d6 = mc.player.posY + d;
            double d7 = mc.player.posZ + d;
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(d2, d3, d4, d5, d6, d7);
            for (EntityEnderCrystal entityEnderCrystal : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, axisAlignedBB)) {
                if (!canBreakCrystal(entityEnderCrystal)) {
                    return;
                }
                breakCrystal(entityEnderCrystal, fArray);
            }
            return;
        }
        Iterator<BlockPos> iterator = list.iterator();
        block1: while (iterator.hasNext()) {
            BlockPos blockPos = iterator.next();
            Iterator<EntityEnderCrystal> iterator2 = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos)).iterator();
            while (true) {
                if (!iterator2.hasNext()) continue block1;
                EntityEnderCrystal entityEnderCrystal = iterator2.next();
                if (hashSet.contains(entityEnderCrystal) || !canBreakCrystal(entityEnderCrystal)) continue;
                breakCrystal(entityEnderCrystal, fArray);
                hashSet.add(entityEnderCrystal);
            }
        }

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean canBreakCrystal(EntityEnderCrystal entityEnderCrystal) {
        if (cBTimingsMode.getValue() == CBTimings.Sequential && entityEnderCrystal.ticksExisted < cBSequentialDelay.getValue()) {
            return false;
        }
        if (!cbNoSuicide.getValue()) {
            return true;
        }
        float f = DamageUtil.calculateDamage( entityEnderCrystal.posX, entityEnderCrystal.posY, entityEnderCrystal.posZ, mc.player, cbTerrain.getValue());
        return f < mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    private void breakCrystal(EntityEnderCrystal entityEnderCrystal, float[] fArray) {
        if (cBRotate.getValue()) {
            float[] fArray2 = SilentRotationUtil.calcAngle(entityEnderCrystal.getPositionVector());
            rotateToCrystal(fArray2);
        }
        if (cBPacket.getValue()) {
            mc.player.connection.sendPacket(new CPacketUseEntity((Entity)entityEnderCrystal));
        } else {
            mc.playerController.attackEntity((EntityPlayer)mc.player, (Entity)entityEnderCrystal);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if (cientSide.getValue()) {
            mc.world.removeEntityFromWorld(entityEnderCrystal.getEntityId());
        }
        if (!cBRotate.getValue()) return;
        rotateToCrystal(fArray);
    }

    private void rotateToCrystal(float[] fArray) {
        if (cBRotateMode.getValue() != CBRotateMode.Client ) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(fArray[0], fArray[1], mc.player.onGround));
        }
        if (cBRotateMode.getValue() != CBRotateMode.Client) {
            if (cBRotateMode.getValue() != CBRotateMode.Both) return;
        }
        mc.player.rotationYaw = fArray[0];
        mc.player.rotationPitch = fArray[1];
    }

    private boolean setCenter() {
        if (!smartCenter.getValue()) {
            setCenterPos(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ));
            return true;
        }
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (checkBlockPos(blockPos)) {
            blockPos = getCenterBlockPos(blockPos);
        }
        if (blockPos == null) {
            return false;
        }
        setCenterPos(blockPos);
        return true;
    }

    private void setCenterPos(BlockPos blockPos) {
        Vec3d vec3d = new Vec3d((double)blockPos.getX() + 0.5, mc.player.posY, (double)blockPos.getZ() + 0.5);
        mc.player.motionX = 0.0;
        mc.player.motionZ = 0.0;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(vec3d.x, vec3d.y, vec3d.z, true));
        mc.player.setPosition(vec3d.x, vec3d.y, vec3d.z);
    }

    private void doPlace(List<BlockPos> list) {
        Item item;
        ItemStack itemStack;
        int n = getSlotWithBestBlock();
        if (n == -1) {
            return;
        }
        int n2 = mc.player.inventory.currentItem;
        if (switchMode.getValue() == Switch.None) {
            itemStack = mc.player.inventory.getStackInSlot(n2);
            item = itemStack.getItem();
            if (!(item instanceof ItemBlock)) {
                return;
            }
            Block block2 = ((ItemBlock)item).getBlock();
            if (block2 != getBlockByMode()) {
                return;
            }
        }
        for (BlockPos o : list) {
            if (o.getY() > heightLimit.getValue() || !checkBlockPos(o) || getInterferingEntities(o) || rangeCheck.getValue() && mc.player.getDistanceSq(o) > placeRange.getValue())
                continue;
            SwitchMethod(switchMode.getValue(),n, false);
            PlaceMethod(o, EnumHand.MAIN_HAND,  rotateMode.getValue(), packet.getValue());
            SwitchMethod(switchMode.getValue(),n2, true);
        }
    }

    public static void PlaceMethod(BlockPos blockPos, EnumHand enumHand, Rotate rotate, boolean bl) {
        EnumFacing enumFacing = getFacing(blockPos);
        if (enumFacing == null) {
            return;
        }
        BlockPos blockPos2 = blockPos.offset(enumFacing);
        EnumFacing enumFacing2 = enumFacing.getOpposite();
        Vec3d vec3d = new Vec3d(blockPos2).add(new Vec3d(0.5, 0.5, 0.5).add(new Vec3d(enumFacing2.getDirectionVec()).scale(0.5)));
        boolean sneak = mc.world.getBlockState(blockPos2).getBlock().onBlockActivated(mc.world, blockPos2, mc.world.getBlockState(blockPos2), mc.player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
        if (sneak)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        float[] angle = getNeededRotations2(blockPos2);

        if (rotate == Rotate.Packet) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
        }
        if (rotate == Rotate.Silent) {
            mc.player.rotationYaw = angle[0];
            mc.player.rotationPitch = angle[1];
        }

        placeMethod(blockPos2, vec3d, enumHand, enumFacing2, bl);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if (sneak)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        if (rotate == Rotate.Packet) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
        }
    }

    public static float[] getNeededRotations2(BlockPos bp) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        double diffX = bp.getX() - eyesPos.x;
        double diffY = bp.getY() - eyesPos.y;
        double diffZ = bp.getZ() - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static void placeMethod(BlockPos blockPos, Vec3d vec3d, EnumHand enumHand, EnumFacing enumFacing, boolean bl) {
        if (bl) {
            float f = (float)(vec3d.x - (double)blockPos.getX());
            float f2 = (float)(vec3d.y - (double)blockPos.getY());
            float f3 = (float)(vec3d.z - (double)blockPos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos, enumFacing, enumHand, f, f2, f3));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, enumHand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        ((IMinecraft)mc).setRightClickDelayTimer(4);
    }

    public static EnumFacing getFacing(BlockPos blockPos) {
        Iterator<EnumFacing> iterator = getFacings(blockPos).iterator();
        if (!iterator.hasNext()) return null;
        return iterator.next();
    }

    private void SwitchMethod(Switch mode,int slot,boolean update_controller){
        if(mc.player.inventory.currentItem == slot){
            return;
        }
        if(mode == Switch.Packet){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        } else if(mode == Switch.Silent){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
        } else if(mode == Switch.Vanilla){
            if(!update_controller) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
            }
        }
        if(update_controller) mc.playerController.updateController();
    }

    private void doAntiCity(PacketEvent.Receive event) {
        if (!antiCity.getValue()) {
            return;
        }
        if (!(event.getPacket() instanceof SPacketBlockChange)) {
            return;
        }
        SPacketBlockChange sPacketBlockChange = event.getPacket();
        BlockPos blockPos = sPacketBlockChange.getBlockPosition();
        if (!sPacketBlockChange.getBlockState().getBlock().isReplaceable(mc.world, blockPos)) {
            return;
        }
        List<BlockPos> list = modeUtil.getBlockPositions(mainMode.getValue());
        if (!list.contains(blockPos)) {
            return;
        }
        if (manipulateWorld.getValue()) {
            blockState = arg_0 -> getBlockStateAS(list, (BlockPos) arg_0);
        }
        if (syncronized.getValue()) {
            doSynchronized();
        } else {
            doNonSynchronized();
        }
        blockState = Surround::getBlockState;
    }

    private static IBlockState getBlockStateAS(List<BlockPos> list, BlockPos blockPos) {
        if (list.contains(blockPos)) return Blocks.AIR.getDefaultState();
        return mc.world.getBlockState(blockPos);
    }

    private boolean checkIntersections(AxisAlignedBB axisAlignedBB, List<BlockPos> list) {
        Iterator<BlockPos> iterator = list.iterator();
        do {
            if (!iterator.hasNext()) return false;
        } while (!new AxisAlignedBB(iterator.next()).intersects(axisAlignedBB));
        return true;
    }

    private boolean getInterferingEntities(BlockPos blockPos) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(blockPos);
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, axisAlignedBB)) {
            if (entity instanceof EntityItem) continue;
            if (!(entity instanceof EntityXPOrb)) return true;
        }
        return false;
    }

    private int getSlotWithBestBlock() {
        int obby_slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        int echest_slot = InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST);
        if (blockMode.getValue() == BlockMode.Obsidian) {
            haveBlock = smartBlock.getValue() && obby_slot == -1;
            return obby_slot;
        }
        haveBlock = !smartBlock.getValue() && echest_slot != -1;
        return echest_slot;
    }

    private Block getBlockByMode() {
        if (blockMode.getValue() != BlockMode.Obsidian) return Blocks.ENDER_CHEST;
        return Blocks.OBSIDIAN;
    }

    private BlockPos getCenterBlockPos(BlockPos blockPos) {
        ArrayList<BlockPos> arrayList = new ArrayList<>();
        if (((IBlockState)blockState.apply(blockPos.north().down())).getMaterial().isSolid()) {
            arrayList.add(blockPos.north());
        }
        if (((IBlockState)blockState.apply(blockPos.east().down())).getMaterial().isSolid()) {
            arrayList.add(blockPos.east());
        }
        if (((IBlockState)blockState.apply(blockPos.south().down())).getMaterial().isSolid()) {
            arrayList.add(blockPos.south());
        }
        if (!((IBlockState)blockState.apply(blockPos.west().down())).getMaterial().isSolid()) return arrayList.stream().min(Comparator.comparingDouble(Surround::getDistanceToBlock)).orElse(null);
        arrayList.add(blockPos.west());
        return arrayList.stream().min(Comparator.comparingDouble(Surround::getDistanceToBlock)).orElse(null);
    }

    public List<BlockPos> getDynamicPositions() {
        if (!extension.getValue()) return getDynamicPositionWOE();
        return getDynamicPositionWE();
    }

    private List<BlockPos> getDynamicPositionWOE() {
        List<BlockPos> list = checkEntities(mc.player, mc.player.posY);
        ArrayList<BlockPos> arrayList = new ArrayList<>(16);
        if (feetBlocks.getValue()) {
            arrayList.addAll(checkHitBoxes(mc.player, mc.player.posY, -1));
        }
        for (BlockPos o : list) {
            List<BlockPos> list2 = getSmartHelpingPositions(o);
            arrayList.addAll(list2);
            arrayList.add(o);
        }
        return arrayList;
    }

    private List<BlockPos> getDynamicPositionWE() {
        List<Entity> list;
        List<BlockPos> list2 = getDynamicPositionWOE();
        List<Entity> list3 = new ArrayList<>();
        for (BlockPos bp : list2) {
            list = mc.world.getEntitiesWithinAABB(allEntities.getValue() ? Entity.class : EntityPlayer.class, new AxisAlignedBB(bp));
            if (list3.isEmpty()) {
                list3 = mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(bp.down()));
            }
            list3.addAll(list);
        }
        ArrayList<BlockPos> arrayList = new ArrayList<>(list2);

        for (Entity value : list3) {
            List<BlockPos> object3;
            if (value.equals(mc.player)) continue;
            List<BlockPos> list4 = checkEntities(value, mc.player.posY);
            ArrayList<BlockPos> arrayList2 = new ArrayList<>(16);
            if (feetBlocks.getValue()) {
                arrayList2.addAll(checkHitBoxes(value, mc.player.posY, -1));
            }
            for (BlockPos bp : list4) {
                object3 = getSmartHelpingPositions(bp);
                arrayList2.addAll(object3);
                arrayList2.add(bp);
            }
            ArrayList<Entity> arrayList3 = new ArrayList<>(list3);
            arrayList3.add(mc.player);

            for (Entity entity : arrayList3) {
                List<BlockPos> list5 = checkHitBoxes(entity, mc.player.posY, 0);
                for (BlockPos blockPos : arrayList2) {
                    if (!list5.contains(blockPos)) continue;
                    list5.add(blockPos);
                }
            }
            arrayList2.removeAll(list4);
            arrayList.addAll(arrayList2);
        }
        return arrayList;
    }

    private List<BlockPos> getSmartHelpingPositions(BlockPos blockPos) {
        if (!smartHelping.getValue()) return Collections.singletonList(blockPos.down());
        if (getFacings(blockPos).isEmpty()) return Collections.singletonList(blockPos.down());
        return Collections.emptyList();
    }

    private List<BlockPos> checkEntities(Entity entity, double d) {
        List<BlockPos> list = checkHitBoxes(entity, d, 0);
        ArrayList<BlockPos> arrayList = new ArrayList<>(16);
        for (BlockPos blockPos : list) {
            BlockPos blockPos2 = blockPos.north();
            BlockPos blockPos3 = blockPos.east();
            BlockPos blockPos4 = blockPos.south();
            BlockPos blockPos5 = blockPos.west();
            if (!list.contains(blockPos2)) {
                arrayList.add(blockPos2);
            }
            if (!list.contains(blockPos3)) {
                arrayList.add(blockPos3);
            }
            if (!list.contains(blockPos4)) {
                arrayList.add(blockPos4);
            }
            if (!list.contains(blockPos5)) {
                arrayList.add(blockPos5);
            }
            if (!safeDynamic.getValue() && (!safeEChest.getValue() || !haveBlock)) continue;
            BlockPos blockPos6 = blockPos.north().west();
            BlockPos blockPos7 = blockPos.north().east();
            BlockPos blockPos8 = blockPos.south().east();
            BlockPos blockPos9 = blockPos.south().west();
            if (!list.contains(blockPos6)) {
                arrayList.add(blockPos6);
            }
            if (!list.contains(blockPos7)) {
                arrayList.add(blockPos7);
            }
            if (!list.contains(blockPos8)) {
                arrayList.add(blockPos8);
            }
            if (list.contains(blockPos9)) continue;
            arrayList.add(blockPos9);
        }
        return arrayList;
    }

    public List<BlockPos> checkHitBoxes(Entity entity, double d, int n) {
        ArrayList<BlockPos> arrayList = new ArrayList<>(16);
        AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox();
        double d2 = (axisAlignedBB.maxX - axisAlignedBB.minX) / 2d;
        double d3 = (axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2d;
        Vec3d vec3d = new Vec3d(entity.posX + d2, d + (double)n, entity.posZ + d3);
        Vec3d vec3d2 = new Vec3d(entity.posX + d2, d + (double)n, entity.posZ - d3);
        Vec3d vec3d3 = new Vec3d(entity.posX - d2, d + (double)n, entity.posZ + d3);
        Vec3d vec3d4 = new Vec3d(entity.posX - d2, d + (double)n, entity.posZ - d3);
        addBlockToList(vec3d, arrayList);
        addBlockToList(vec3d2, arrayList);
        addBlockToList(vec3d3, arrayList);
        addBlockToList(vec3d4, arrayList);
        return arrayList;
    }

    public List<BlockPos> getAntiFacePlacePositions() {
        ArrayList<BlockPos> arrayList = new ArrayList<>(16);
        arrayList.addAll(modeUtil.getBlockPositions(Mode.Normal));
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<BlockPosWithFacing> list = getNeighbours(blockPos.up());
        for (BlockPosWithFacing pos : list) {
            if (getBlock(pos.getPosition().up()) == Blocks.AIR) {
                arrayList.add(pos.getPosition());
                continue;
            }
            if (getBlock(pos.getPosition().offset(pos.getFacing())) != Blocks.AIR) continue;
            arrayList.add(pos.getPosition());
        }
        return arrayList;
    }

    private List<BlockPosWithFacing> getNeighbours(BlockPos blockPos) {
        ArrayList<BlockPosWithFacing> arrayList = new ArrayList<>(16);
        arrayList.add(new BlockPosWithFacing(blockPos.north(), EnumFacing.NORTH));
        arrayList.add(new BlockPosWithFacing(blockPos.east(), EnumFacing.EAST));
        arrayList.add(new BlockPosWithFacing(blockPos.south(), EnumFacing.SOUTH));
        arrayList.add(new BlockPosWithFacing(blockPos.west(), EnumFacing.WEST));
        return arrayList;
    }

    public static List<EnumFacing> getFacings(BlockPos blockPos) {
        ArrayList<EnumFacing> arrayList = new ArrayList<>();
        if (mc.world == null) return arrayList;
        if (blockPos == null) {
            return arrayList;
        }
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing enumFacing = enumFacingArray[n2];
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);
            if (iBlockState != null && iBlockState.getBlock().canCollideCheck(iBlockState, false) && !iBlockState.getMaterial().isReplaceable()) {
                arrayList.add(enumFacing);
            }
            ++n2;
        }
        return arrayList;
    }

    private void addBlockToList(Vec3d vec3d, List<BlockPos> list) {
        BlockPos blockPos = new BlockPos(vec3d);
        if (!checkBlockPos(blockPos)) return;
        if (list.contains(blockPos)) return;
        list.add(blockPos);
    }

    private boolean checkBlockPos(BlockPos blockPos) {
        return blockPos != null && mc.world != null && ((IBlockState) blockState.apply(blockPos)).getMaterial().isReplaceable();
    }

    private Block getBlock(BlockPos blockPos) {
        return ((IBlockState)blockState.apply(blockPos)).getBlock();
    }

    private boolean doesCrystalWantToFuckUs(Vec3d vec3d, List<BlockPos> list) {
        Iterator<BlockPos> iterator = list.iterator();
        do {
            if (!iterator.hasNext()) return false;
        } while (!new AxisAlignedBB(iterator.next()).contains(vec3d));
        return true;
    }

    private static double getDistanceToBlock(BlockPos blockPos) {
        return mc.player.getDistance((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5);
    }

    private static IBlockState getBlockState(Object blockPos) {
        return mc.world.getBlockState((BlockPos) blockPos);
    }
}
