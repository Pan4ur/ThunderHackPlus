package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

import static com.mrzak34.thunderhack.modules.combat.Aura.calcAngle;
import static com.mrzak34.thunderhack.modules.combat.Aura.target;

public class KillauraAddon extends Module {

    public KillauraAddon() {
        super("KillauraAddon", "KillauraAddon", Category.COMBAT, true, false, false);
    }

    public Setting<Boolean> packetplace = register(new Setting<>("packetplace", true));
    public Setting<Integer> stophp = this.register (new Setting<>("stophp", 8, 1, 20) );
    public Setting<Integer> delay = this.register (new Setting<>("delay", 8, 1, 20) );
    public Setting<Boolean> placec = register(new Setting<>("placeCrys", true));
    public Setting<Boolean> breakcrys = register(new Setting<>("breakcrys", true));
    public Setting<Integer> maxself = this.register (new Setting<>("maxself", 10, 1, 20) );


    EntityPlayer trgt;
    int ticksNoOnGround = 0;
    BlockPos CoolPosition;
    Timer placeDelay = new Timer();
    Timer breakDelay = new Timer();


    @SubscribeEvent
    public void onPlayerPre(EventPreMotion e){
        if(mc.player.getHealth() < stophp.getValue()){
            return;
        }

        if(breakcrys.getValue()) {
            for (Entity ent : mc.world.loadedEntityList) {
                if (ent instanceof EntityEnderCrystal) {
                    if (mc.player.getDistance(ent) < 5f) {
                        if (ent.ticksExisted >= delay.getValue()) {
                            if (breakDelay.passedMs(156)) {
                                if(CrystalUtils.calculateDamage((EntityEnderCrystal) ent, mc.player) < maxself.getValue()) {
                                    Aura.stopAuraRotate = true;
                                    mc.player.setSprinting(false);

                                    float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), ent.getPositionEyes(mc.getRenderPartialTicks()));
                                    mc.player.rotationYaw =(angle[0]);
                                    mc.player.rotationPitch =(angle[1]);
                                    mc.player.connection.sendPacket(new CPacketUseEntity(ent));
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                    Aura.stopAuraRotate = false;
                                    breakDelay.reset();
                                }
                            }
                        }
                    }
                }
            }
        }


        if(Aura.target !=  null) {
            if(Aura.target instanceof EntityPlayer) {
                trgt = (EntityPlayer) Aura.target;
                if(!trgt.onGround){
                    ++ticksNoOnGround;
                } else {
                    ticksNoOnGround = 0;
                }
            }
        } else {
            trgt = null;
            return;
        }
        if(getPosition(mc.player) != null  && (mc.player.posY + 0.4f < trgt.posY)){
            CoolPosition = getPosition(mc.player);
            if(mc.world.getBlockState(CoolPosition).getBlock() == Blocks.OBSIDIAN  && placec.getValue()){
                if(!CheckCrystal(CoolPosition)){
                    return;
                }
                if(!placeDelay.passedMs(36)){
                    return;
                }
                int crysslot = InventoryUtil.getCrysathotbar();
                if(crysslot == -1){
                    return;
                }
                Aura.stopAuraRotate = true;
                InventoryUtil.switchToHotbarSlot(InventoryUtil.getCrysathotbar(), false);
                BlockUtils.placeBlockSmartRotate(CoolPosition.add(0,1,0),EnumHand.MAIN_HAND,true,packetplace.getValue(),mc.player.isSneaking(),e);
                placeDelay.reset();
            } else {
                Aura.stopAuraRotate = true;
                int obbyslot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                if(obbyslot == -1){
                    Aura.stopAuraRotate = false;
                    return;
                }
                InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(BlockObsidian.class), false);
                BlockUtils.placeBlockSmartRotate(CoolPosition,EnumHand.MAIN_HAND,true,packetplace.getValue(),mc.player.isSneaking(),e);
            }
            Aura.stopAuraRotate = false;
        }
    }
    public boolean canPlace(BlockPos bp){
        if(mc.world.getBlockState(bp.add(0,1,0)).getBlock() != Blocks.AIR){
            return false;
        }
        if(mc.world.getBlockState(bp.add(0,2,0)).getBlock() != Blocks.AIR){
            return false;
        }
        return mc.world.getBlockState(bp).getBlock() == Blocks.AIR || mc.world.getBlockState(bp).getBlock() == Blocks.OBSIDIAN ;
    }
    private BlockPos getPosition(EntityPlayer entity2) {
        ArrayList<BlockPos> arrayList = new ArrayList<>();
        int playerX = (int)entity2.posX;
        int playerZ = (int)entity2.posZ;
        int n4 = (int)((float) 4.0);
        double playerX1 = entity2.posX - 0.5D;
        double playerY1 = entity2.posY + (double)entity2.getEyeHeight() - 1.0D;
        double playerZ1 = entity2.posZ - 0.5D;
        for(int n5 = playerX - n4; n5 <= playerX + n4; ++n5) {
            for(int n6 = playerZ - n4; n6 <= playerZ + n4; ++n6) {
                if (((double)n5 - playerX1) * ((double)n5 - playerX1) + (mc.player.posY - playerY1) * (mc.player.posY - playerY1) + ((double)n6 - playerZ1) * ((double)n6 - playerZ1) <= (double)((float) 5.0 * (float) 5.0) && canPlace(new BlockPos(n5, mc.player.posY, n6))) {
                    if(mc.world.getBlockState(new BlockPos(n5, mc.player.posY, n6)).getBlock() == Blocks.OBSIDIAN && (trgt.getDistanceSqToCenter(new BlockPos(n5, mc.player.posY, n6)) < 16)){
                        return new BlockPos(n5, mc.player.posY, n6);
                    } else {
                        arrayList.add(new BlockPos(n5, mc.player.posY, n6));
                    }
                }
            }
        }
        return AI(arrayList);
    }


    public boolean CheckCrystal(BlockPos blockPos) {
            BlockPos boost = blockPos.add(0, 1, 0);
            BlockPos boost2 = blockPos.add(0, 2, 0);
            return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost, boost2.add(1, 1, 1))).stream().filter(entity -> (!(entity instanceof EntityEnderCrystal) || entity.ticksExisted > 20)).count() == 0;
    }


    // mega smart xD
    public BlockPos AI(ArrayList<BlockPos> blocks){
        BlockPos pos = null;
        double bestdist= 5;
        if(trgt == null) return null;
        for (BlockPos pos1 : blocks){
            if((pos1.getDistance((int) trgt.posX, (int) trgt.posY, (int) trgt.posZ) > 2) && trgt.getDistanceSqToCenter(pos1) < bestdist){
                bestdist = trgt.getDistanceSqToCenter(pos1);
                pos = pos1;
            }
        }
        return pos;
    }
}
