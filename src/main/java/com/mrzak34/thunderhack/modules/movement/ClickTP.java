package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.TessellatorUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ClickTP extends Module {

    private final Setting<Float> ass = this.register(new Setting<>("BlockYCorrect", 1.0f, -1f, 1f));
    private final Setting<Float> adss = this.register(new Setting<>("PlayerYCorrect", 0.0f, -1f, 1f));
    private final Setting<Boolean> ground = register(new Setting<>("ground", false));
    private final Setting<Boolean> spoofs = register(new Setting<>("spoofs", false));
    public ClickTP() {
        super("ClickTP", "ClickTP", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onMotion(EventPreMotion e) {
        if (Mouse.isButtonDown(1)) {
            RayTraceResult ray = mc.player.rayTrace(256, mc.getRenderPartialTicks());
            int i;
            BlockPos pos = null;
            if (ray != null) {
                pos = ray.getBlockPos();
            }
            EntityPlayer rayTracedEntity = getEntityUnderMouse(256);

            if (rayTracedEntity == null && ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (spoofs.getValue()) {
                    for (i = 0; i < 10; ++i) {
                        this.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.getX(), ass.getValue() + pos.getY(), pos.getZ(), ground.getValue()));
                    }
                }
                mc.player.setPosition(pos.getX(), pos.getY() + ass.getValue(), pos.getZ());
            } else if (rayTracedEntity != null) {

                BlockPos bp = new BlockPos(rayTracedEntity);
                if (spoofs.getValue()) {
                    for (i = 0; i < 10; ++i) {
                        this.mc.player.connection.sendPacket(new CPacketPlayer.Position(bp.getX(), adss.getValue() + bp.getY(), bp.getZ(), ground.getValue()));
                    }
                }
                mc.player.setPosition(bp.getX(), bp.getY() + adss.getValue(), bp.getZ());
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        EntityPlayer rayTracedEntity = getEntityUnderMouse(256);

        RayTraceResult ray = mc.player.rayTrace(256, mc.getRenderPartialTicks());
        if (rayTracedEntity == null) {
            if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = ray.getBlockPos();
                // Color color1 = new Color(color.getValue().getColor());
                RenderUtil.drawBlockOutline(blockpos, new Color(0xA7FA00), 1f, false, 0);
            }
        } else {
            TessellatorUtil.prepare();
            TessellatorUtil.drawBoundingBox(rayTracedEntity.getEntityBoundingBox(), 3f, new Color(0x2AFF00));
            TessellatorUtil.release();
        }


    }

    public EntityPlayer getEntityUnderMouse(int range) {
        Entity entity = mc.getRenderViewEntity();

        if (entity != null) {
            Vec3d pos = mc.player.getPositionEyes(1F);
            for (float i = 0F; i < range; i += 0.5F) {
                pos = pos.add(mc.player.getLookVec().scale(0.5));
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (player == mc.player) continue;
                    AxisAlignedBB bb = player.getEntityBoundingBox();
                    if (bb == null) continue;
                    if (player.getDistance(mc.player) > 6) {
                        bb = bb.grow(0.5);
                    }
                    if (bb.contains(pos)) return player;
                }
            }
        }

        return null;
    }
}
