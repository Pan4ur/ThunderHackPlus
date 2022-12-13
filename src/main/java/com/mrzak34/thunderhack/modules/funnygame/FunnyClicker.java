package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.SilentRotaionUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FunnyClicker extends Module {


    public FunnyClicker() {
        super("FunnyClicker", "FunnyClicker", Category.FUNNYGAME, true, false, false);
    }
    public Setting<Integer> chanceval = this.register ( new Setting <> ( "Chance", 100, 1, 1000) );

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof SPacketOpenWindow){
            e.setCanceled(true);
        }
    }

    BlockPos bp = null;
    Timer timer = new Timer();

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(EventPreMotion event) {
            for (final TileEntity tileEntity : mc.world.loadedTileEntityList) {
                if(mc.player.getDistanceSq(new BlockPos(tileEntity.getPos())) > 4) continue;
                if (((tileEntity instanceof TileEntityChest))) {
                    if(mc.player.getDistance(tileEntity.getPos().x,tileEntity.getPos().y,tileEntity.getPos().z) > 8){
                        continue;
                    }
                    if(timer.passedMs(chanceval.getValue())) {
                        bp = tileEntity.getPos();
                        SilentRotaionUtil.lookAtBlock(tileEntity.getPos());
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(tileEntity.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                        timer.reset();
                    }
                }
            }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent e){
        if(bp != null){
            try {
                RenderUtil.drawBlockOutline(bp, new Color(0x0AF886), 3f, true);
            } catch (Exception ee){

            }
        }
    }

}
