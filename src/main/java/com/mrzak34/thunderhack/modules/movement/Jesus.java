package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Jesus extends Module {


    public Jesus() {
        super("Jesus", "Jesus", Category.MOVEMENT, true, false  , false);
    }


    private  Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.SOLID));
    private  Setting<Boolean> glide = this.register(new Setting<>("Glide", false));
    private  Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    private  Setting<Boolean> boost = this.register(new Setting<>("Boost", false));

    private boolean jumping;

    private int glideCounter = 0;

    private float lastOffset;

    private enum Mode {
        SOLID, TRAMPOLINE
    }



    @Override
    public void onUpdate() {
        if (mode.getValue() == Mode.TRAMPOLINE) return;
        if (!mc.player.movementInput.sneak && !mc.player.movementInput.jump && isInLiquid()) {
            mc.player.motionY = 0.1D;
        }
        if (isOnLiquid() && mc.player.fallDistance < 3.0F && !mc.player.movementInput.jump && !isInLiquid() && !mc.player.isSneaking() && glide.getValue()) {
            switch (glideCounter) {
                case 0:
                    mc.player.motionX *= 1.1D;
                    mc.player.motionZ *= 1.1D;
                    break;
                case 1:
                    mc.player.motionX *= 1.27D;
                    mc.player.motionZ *= 1.27D;
                    break;
                case 2:
                    mc.player.motionX *= 1.51D;
                    mc.player.motionZ *= 1.51D;
                    break;
                case 3:
                    mc.player.motionX *= 1.15D;
                    mc.player.motionZ *= 1.15D;
                    break;
                case 4:
                    mc.player.motionX *= 1.23D;
                    mc.player.motionZ *= 1.23D;
                    break;
            }

            glideCounter++;

            if (glideCounter > 4) {
                glideCounter = 0;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            glideCounter = 0;
        }
    }

    @SubscribeEvent
    public void onLiquidJump(HandleLiquidJumpEvent event) {
        if ((mc.player.isInWater() || mc.player.isInLava()) && (mc.player.motionY == 0.1 || mc.player.motionY == 0.5)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onWalkingPlayerUpdatePre(EventPreMotion event) {
        if (mode.getValue() == Mode.TRAMPOLINE) {
            int minY = MathHelper.floor(mc.player.getEntityBoundingBox().minY - 0.2D);
            boolean inLiquid = checkIfBlockInBB(BlockLiquid.class, minY) != null;

            if (inLiquid && !mc.player.isSneaking()) {
                mc.player.onGround = false;
            }

            Block block = mc.world.getBlockState(new BlockPos((int) Math.floor(mc.player.posX), (int) Math.floor(mc.player.posY), (int) Math.floor(mc.player.posZ))).getBlock();

            if (jumping && !mc.player.capabilities.isFlying && !mc.player.isInWater()) {
                if (mc.player.motionY < -0.3D || mc.player.onGround || mc.player.isOnLadder()) {
                    jumping = false;
                    return;
                }

                mc.player.motionY = mc.player.motionY / 0.9800000190734863D + 0.08D;
                mc.player.motionY -= 0.03120000000005D;
            }

            if (mc.player.isInWater() || mc.player.isInLava()) {
                mc.player.motionY = 0.1D;
            }

            if (!mc.player.isInLava() && (!mc.player.isInWater() || boost.getValue()) && block instanceof BlockLiquid && mc.player.motionY < 0.2D) {
                mc.player.motionY = 0.5D;
                jumping = true;
            }
        }
    }

    /*
    @SubscribeEvent
    public void onBoundingBox(CollisionBoxEvent event) {
        if (((event.getBlock() instanceof BlockLiquid))
                && event.getEntity() == mc.player
                && event.getPos().getY() <= mc.player.posY
                && checkIfBlockInBB(BlockLiquid.class, MathHelper.floor((mc.player.getEntityBoundingBox().minY + 0.01))) != null
                && checkIfBlockInBB(BlockLiquid.class, MathHelper.floor((mc.player.getEntityBoundingBox().minY - 0.02))) != null
                && (mc.player.fallDistance < 3.0F)
                && (!mc.player.isSneaking())) {
          //  event.setBoundingBox(Block.FULL_BLOCK_AABB);
        }
    }

     */

    @SubscribeEvent
    public void onLiquidCollision(final JesusEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getStage() == 0 && (this.mode.getValue() == Mode.SOLID) && Jesus.mc.world != null && Jesus.mc.player != null && checkCollide() && Jesus.mc.player.motionY < 0.10000000149011612 && event.getPos().getY() < Jesus.mc.player.posY - 0.05000000074505806) {
            if (Jesus.mc.player.getRidingEntity() != null) {
                event.setBoundingBox(new AxisAlignedBB(0.0,  0.0,  0.0,  1.0,  0.949999988079071,  1.0));
            }
            else {
                event.setBoundingBox(Block.FULL_BLOCK_AABB);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void sendPacket(PacketEvent.Send event) {
        if (mc.world == null || mc.player == null) return;
        if (mode.getValue() == Mode.SOLID) {
            if (event.getPacket() instanceof CPacketPlayer
                    && mc.player.ticksExisted > 20
                    && mode.getValue().equals(Mode.SOLID)
                    && mc.player.getRidingEntity() == null
                    && !mc.gameSettings.keyBindJump.isKeyDown()
                    && mc.player.fallDistance < 3.0F) {
                final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                if (isOnLiquid() && !isInLiquid()) {
                    packet.onGround = (false);
                    if (strict.getValue()) {
                        lastOffset += 0.12F;

                        if (lastOffset > 0.4F) {
                            lastOffset = 0.2F;
                        }
                        packet.y = (packet.getY(mc.player.posY) - lastOffset);
                    } else {
                        packet.y = ((mc.player.ticksExisted % 2 == 0) ? (packet.getY(mc.player.posY) - 0.05D) : packet.getY(mc.player.posY));
                    }
                }
            }
        }
    }

    public static IBlockState checkIfBlockInBB(Class<? extends Block> blockClass, int minY) {
        for(int iX = MathHelper.floor(mc.player.getEntityBoundingBox().minX); iX < MathHelper.ceil(mc.player.getEntityBoundingBox().maxX); iX++) {
            for(int iZ = MathHelper.floor(mc.player.getEntityBoundingBox().minZ); iZ < MathHelper.ceil(mc.player.getEntityBoundingBox().maxZ); iZ++) {
                IBlockState state = mc.world.getBlockState(new BlockPos(iX, minY, iZ));
                if (blockClass.isInstance(state.getBlock())) {
                    return state;
                }
            }
        }
        return null;
    }

    private boolean checkCollide() {

        if (mc.player.isSneaking()) {
            return false;
        }

        if (mc.player.getRidingEntity() != null
                && mc.player.getRidingEntity().fallDistance >= 3.0f) {
            return false;
        }

        return mc.player.fallDistance <= 3.0f;
    }

    public static boolean isInLiquid() {

        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        boolean inLiquid = false;
        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    public static boolean isOnLiquid() {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d, -0.05000000074505806D, 0.0d) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d, -0.05000000074505806D, 0.0d);
        boolean onLiquid = false;
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;

    }

}

