package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Set;

public class NewChunks extends Module {

    public Setting<ColorSetting> color = register(new Setting<>("Color", new ColorSetting(new Color(214f / 255f, 86f / 255f, 147f / 255f, 100f / 255f).hashCode(), false)));
    private final ICamera frustum = new Frustum();

    private final Set<ChunkPos> chunks = new ConcurrentSet<>();

    public NewChunks() {
        super("NewChunks", "NewChunks", "NewChunks", Category.RENDER);
    }

    public static void drawBox(AxisAlignedBB box, int mode, int color) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(mode, DefaultVertexFormats.POSITION_COLOR);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float a = (float) (color >> 24 & 255) / 255.0F;
        buffer.pos(box.minX, box.minY, box.minZ).color(r, g, b, 0.0f).endVertex();
        buffer.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex();
        buffer.pos(box.maxX, box.minY, box.minZ).color(r, g, b, a).endVertex();
        buffer.pos(box.maxX, box.minY, box.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(box.minX, box.minY, box.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex();
        buffer.pos(box.minX, box.maxY, box.minZ).color(r, g, b, a).endVertex();
        buffer.pos(box.maxX, box.maxY, box.minZ).color(r, g, b, a).endVertex();
        buffer.pos(box.maxX, box.maxY, box.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(box.minX, box.maxY, box.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(box.minX, box.maxY, box.minZ).color(r, g, b, a).endVertex();
        buffer.pos(box.minX, box.maxY, box.maxZ).color(r, g, b, 0.0f).endVertex();
        buffer.pos(box.minX, box.minY, box.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(box.maxX, box.maxY, box.maxZ).color(r, g, b, 0.0f).endVertex();
        buffer.pos(box.maxX, box.minY, box.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(box.maxX, box.maxY, box.minZ).color(r, g, b, 0.0f).endVertex();
        buffer.pos(box.maxX, box.minY, box.minZ).color(r, g, b, a).endVertex();
        buffer.pos(box.maxX, box.minY, box.minZ).color(r, g, b, 0.0f).endVertex();
        tessellator.draw();
    }

    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChunkData) {
            final SPacketChunkData packet = event.getPacket();
            if (packet.isFullChunk()) return;
            final ChunkPos newChunk = new ChunkPos(packet.getChunkX(), packet.getChunkZ());
            this.chunks.add(newChunk);
        }
    }

    @SubscribeEvent
    public void onRender(Render3DEvent event) {
        if (mc.getRenderViewEntity() == null) return;
        this.frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        GlStateManager.pushMatrix();
        RenderUtil.beginRender();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.glLineWidth(2f);

        for (ChunkPos chunk : this.chunks) {
            final AxisAlignedBB chunkBox = new AxisAlignedBB(chunk.getXStart(), 0, chunk.getZStart(), chunk.getXEnd(), 0, chunk.getZEnd());


            GlStateManager.pushMatrix();
            if (this.frustum.isBoundingBoxInFrustum(chunkBox)) {
                double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double) event.getPartialTicks();
                double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double) event.getPartialTicks();
                double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double) event.getPartialTicks();
                drawBox(chunkBox.offset(-x, -y, -z), GL11.GL_LINE_STRIP, color.getValue().getColor());
            }

            GlStateManager.popMatrix();
        }

        GlStateManager.glLineWidth(1f);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        RenderUtil.endRender();
        GlStateManager.popMatrix();
    }

    @Override
    public void onEnable() {
        chunks.clear();
    }
}
