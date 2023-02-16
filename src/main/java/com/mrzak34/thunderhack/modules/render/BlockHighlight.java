package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class BlockHighlight extends Module {
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", 1.0f, 0.1f, 5.0f));
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x2250b4b4)));

    public BlockHighlight() {
        super("BlockHighlight", "подсвечивает блок на-который ты смотришь", Module.Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ray.getBlockPos();
            RenderUtil.drawBlockOutline(blockpos, color.getValue().getColorObject(), this.lineWidth.getValue(), false,0);
        }
    }


    int boostTicks;
    /*
    @Override
    public void onUpdate() {
        if (mc.player.onGround) {
            mc.player.jump();
        }

        if (mc.player.ticksExisted % 2 == 0) {
            mc.player.motionX *= 1.0D;
            mc.player.motionZ *= 1.0D;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }

        if (mc.player.isElytraFlying()) {
            ++this.boostTicks;
            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.allowFlying = false;
            if (this.boostTicks > 0) {
                mc.player.motionX *= 1.5;
                mc.player.motionZ *= 1.5;

                setSpeed(lineWidth.getValue());
                if (!isMoving()) {
                    this.boostTicks = 0;
                }
            }
        }
    }
     */

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive pa){
        if(fullNullCheck()) return;
        if(pa.getPacket() instanceof SPacketEntityHeadLook) return;
        if(pa.getPacket() instanceof SPacketEntity.S15PacketEntityRelMove) return;
        if(pa.getPacket() instanceof SPacketEntity.S17PacketEntityLookMove) return;
        if(pa.getPacket() instanceof SPacketEntity.S16PacketEntityLook) return;
        if(pa.getPacket() instanceof SPacketChunkData) return;
        if(pa.getPacket() instanceof SPacketEntityVelocity) return;

        Command.sendMessage(pa.getPacket().toString());
    }


}

