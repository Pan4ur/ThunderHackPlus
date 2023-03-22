package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.DestroyBlockEvent;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.MovementUtil;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class NoClip extends Module {


    public NoClip() {
        super("NoClip", "NoClip", Category.PLAYER);
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Default));
    private final Setting<Integer> timeout = register(new Setting<>("Timeout", 5, 1, 10, v -> mode.getValue() == Mode.CC));
    public Setting<Boolean> silent = register(new Setting<>("Silent", false,v-> mode.getValue() == Mode.SunriseBypass));
    public int itemIndex;
    public Setting<Boolean> waitBreak = register(new Setting<>("WaitBreak", true,v-> mode.getValue() == Mode.SunriseBypass));
    private Setting<Integer> afterBreak = register(new Setting<>("BreakTimeout", 4, 1, 20, v -> mode.getValue() == Mode.SunriseBypass && waitBreak.getValue()));


    public boolean playerInsideBlock() {
        return mc.world.getBlockState(mc.player.getPosition()).getBlock() != Blocks.AIR;
    }

    @SubscribeEvent
    public void onPreSync(EventSync e) {
        if (mode.getValue() == Mode.SunriseBypass && (mc.player.collidedHorizontally || playerInsideBlock()) && !mc.player.isInWater() && !mc.player.isInLava()) {
            double[] dir = MovementUtil.forward(0.5);

            BlockPos blockToBreak = null;
            if(mc.gameSettings.keyBindSneak.isKeyDown()){
                blockToBreak = new BlockPos(mc.player.posX + dir[0], mc.player.posY - 1, mc.player.posZ + dir[1]);
            } else if(isMoving()){
                blockToBreak = new BlockPos(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1]);
            }
            if(blockToBreak == null) return;
            int best_tool = getTool(blockToBreak);
            if(best_tool == -1) return;
            itemIndex = best_tool;

            if(silent.getValue()){
                mc.player.connection.sendPacket(new CPacketHeldItemChange(best_tool));
            } else {
                mc.player.inventory.currentItem = best_tool;
                InventoryUtil.syncItem();
            }

            if(blockToBreak != null) {
                mc.playerController.onPlayerDamageBlock(blockToBreak, mc.player.getHorizontalFacing());
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            if(silent.getValue()){
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            }
        }
    }

    @Override
    public void onUpdate() {
        if(clipTimer > 0) clipTimer--;
        if(mode.getValue() == Mode.CC) {
        if (isMoving()) {
            disable();
            return;
        }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
                mc.player.setPosition(MathUtil.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, MathUtil.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));
            } else if (mc.player.ticksExisted % timeout.getValue() == 0) {
                mc.player.setPosition(mc.player.posX + MathHelper.clamp(MathUtil.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(MathUtil.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(MathUtil.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, MathUtil.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));
            }
        }
    }

    public int clipTimer;

    public boolean canNoClip() {
        if(mode.getValue() == Mode.Default) return true;
        if(!waitBreak.getValue()) return true;
        return clipTimer != 0;
    }

    @SubscribeEvent
    public void onDestroyBlock(DestroyBlockEvent e){
        clipTimer = afterBreak.getValue();
    }

    public enum Mode {
        Default, SunriseBypass, CC
    }


    private int getTool(final BlockPos pos) {
        int index = -1;
        float CurrentFastest = 1.0f;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                mc.player.inventory.getStackInSlot(i).getMaxDamage();
                mc.player.inventory.getStackInSlot(i).getItemDamage();

                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getDestroySpeed(mc.world.getBlockState(pos));

                if (mc.world.getBlockState(pos).getBlock() instanceof BlockAir) return -1;
                mc.world.getBlockState(pos).getBlock();
                if (digSpeed + destroySpeed > CurrentFastest) {
                    CurrentFastest = digSpeed + destroySpeed;
                    index = i;
                }
            }
        }
        return index;
    }
}
