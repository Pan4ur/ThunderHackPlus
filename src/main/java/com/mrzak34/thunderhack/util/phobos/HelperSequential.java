package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HelperSequential extends Feature {
    private final Timer timer = new Timer();
    private final AutoCrystal module;
    private volatile BlockPos expecting;
    private volatile Vec3d crystalPos;

    public HelperSequential(AutoCrystal module) {
        this.module = module;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.SendPost e){
        if(e.getPacket() instanceof  CPacketUseEntity){
            Entity entity = mc.world.getEntityByID(((CPacketUseEntity) e.getPacket()).entityId);
            if (entity instanceof EntityEnderCrystal) {
                if (module.endSequenceOnBreak.getValue()) {
                    setExpecting(null);
                } else {
                    crystalPos = entity.getPositionVector();
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()){
            return;
        }
        if(e.getPacket() instanceof SPacketSoundEffect){
            Vec3d cPos = crystalPos;
            if (module.endSequenceOnExplosion.getValue()
                    && ((SPacketSoundEffect)e.getPacket()).getCategory() == SoundCategory.BLOCKS
                    && ((SPacketSoundEffect)e.getPacket()).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE
                    && cPos != null
                    && cPos.squareDistanceTo(((SPacketSoundEffect)e.getPacket()).getX(), ((SPacketSoundEffect)e.getPacket()).getY(), ((SPacketSoundEffect)e.getPacket()).getZ()) < 144) {
                setExpecting(null);
            }
        }
        if(e.getPacket() instanceof SPacketSpawnObject){
            if (((SPacketSpawnObject)e.getPacket()).getType() == 51) {
                BlockPos pos = new BlockPos(((SPacketSpawnObject)e.getPacket()).getX(),
                        ((SPacketSpawnObject)e.getPacket()).getY(),
                        ((SPacketSpawnObject)e.getPacket()).getZ());
                if (pos.down().equals(expecting)) {
                    if (module.endSequenceOnSpawn.getValue()) {
                        setExpecting(null);
                    } else if (crystalPos == null) {
                        crystalPos = new Vec3d(
                                (((SPacketSpawnObject)e.getPacket())).getX(),
                                (((SPacketSpawnObject)e.getPacket())).getY(),
                                (((SPacketSpawnObject)e.getPacket())).getZ());
                    }
                }
            }
        }
        if(e.getPacket() instanceof SPacketBlockChange){
            BlockPos expected = expecting;
            if (expected != null && expected.equals(((SPacketBlockChange)e.getPacket()).getBlockPosition())) {
                if (module.antiPlaceFail.getValue() && crystalPos == null) {
                    module.placeTimer.setTime(0);
                    setExpecting(null);
                    if (module.debugAntiPlaceFail.getValue()) {
                        mc.addScheduledTask(
                                () -> Command.sendMessage("Crystal failed to place!"));
                    }
                }
            }
        }
    }

    public boolean isBlockingPlacement() {
        return module.sequential.getValue()
                && expecting != null
                && !timer.passedMs(module.seqTime.getValue());
    }

    public void setExpecting(BlockPos expecting) {
        timer.reset();
        this.expecting = expecting;
        this.crystalPos = null;
    }

}