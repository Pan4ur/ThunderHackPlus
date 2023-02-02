package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.BlockRenderEvent;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;


import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.TessellatorUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
public class Search extends Module {

    public Search() {
        super("Search", "подсветка блоков", Category.RENDER);
    }


    public static CopyOnWriteArrayList<BlockVec> blocks = new CopyOnWriteArrayList<>();
    private  Setting<Float> range = this.register(new Setting<>("Range", 100f, 1f, 500f));
    private  Setting<ColorSetting> color =this.register( new Setting<>("Color", new ColorSetting(0xFF00FFFF)));
    private  Setting<Boolean> illegals = this.register(new Setting<>("Illegals", true));
    private  Setting<Boolean> tracers = this.register(new Setting<>("Tracers", false));
    private  Setting<Boolean> fill =this.register( new Setting<>("Fill", true));
    private  Setting<Boolean> outline =this.register( new Setting<>("Outline", true));
    public  Setting<Boolean> softReload =this.register( new Setting<>("SoftReload", true));



    public void onEnable() {
        if (softReload.getValue()) {
            doSoftReload();
        }
    }

    public static void doSoftReload() {
        if (mc.world != null && mc.player != null) {
            int posX = (int) mc.player.posX;
            int posY = (int) mc.player.posY;
            int posZ = (int) mc.player.posZ;
            int range = mc.gameSettings.renderDistanceChunks * 16;
            mc.renderGlobal.markBlockRangeForRenderUpdate(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
        }
    }

    @SubscribeEvent
    public void onBlockRender(BlockRenderEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (blocks.size() > 100000) {
            blocks.clear();
        }
        if(shouldAdd(event.getBlock(), event.getPos())) {
            BlockVec vec = new BlockVec(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
            if (!blocks.contains(vec)) {
                blocks.add(vec);
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {

        if (mc.world == null || mc.player == null || blocks.isEmpty()) return;

        if (fill.getValue() || outline.getValue()) {
            for (BlockVec vec : blocks) {
                if (vec.getDistance(new BlockVec(mc.player.posX, mc.player.posY, mc.player.posZ)) > range.getValue() || !shouldRender(vec)) {
                    blocks.remove(vec);
                    continue;
                }

                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);

                AxisAlignedBB axisAlignedBB = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);

                if (fill.getValue()) {
                    TessellatorUtil.prepare();
                    TessellatorUtil.drawBox(axisAlignedBB, color.getValue().getColorObject());
                    TessellatorUtil.release();
                }

                if (outline.getValue()) {
                    TessellatorUtil.prepare();
                    TessellatorUtil.drawBoundingBox(axisAlignedBB, 1.5F, color.getValue().getColorObject());
                    TessellatorUtil.release();
                }
            }
        }

        if (tracers.getValue()) {
            for (BlockVec vec : blocks) {
                if (vec.getDistance(new BlockVec(mc.player.posX, mc.player.posY, mc.player.posZ)) > range.getValue() || !shouldRender(vec)) {
                    blocks.remove(vec);
                    continue;
                }

                Vec3d eyes = new Vec3d(0, 0, 1)
                        .rotatePitch(-(float) Math
                                .toRadians(mc.player.rotationPitch))
                        .rotateYaw(-(float) Math
                                .toRadians(mc.player.rotationYaw));

                renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z,
                        vec.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX() + 0.5,
                        vec.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY() + 0.5,
                        vec.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ() + 0.5,
                        color.getValue().getColor());
            }
        }
    }

    private boolean shouldAdd(Block block, BlockPos pos) {
        if (defaultBlocks.contains(block)) {
            return true;
        }
        if (illegals.getValue()) {
            return isIllegal(block, pos);
        }
        return false;
    }


    public static ArrayList<Block> defaultBlocks = new ArrayList<>();


    private boolean shouldRender(BlockVec vec) {
        if (defaultBlocks.contains(mc.world.getBlockState(new BlockPos(vec.x, vec.y, vec.z)).getBlock())) {
            return true;
        }

        if (illegals.getValue()) {
            return isIllegal(mc.world.getBlockState(new BlockPos(vec.x, vec.y, vec.z)).getBlock(), new BlockPos(vec.x, vec.y, vec.z));
        }

        return false;
    }


    private static Block getRegisteredBlock(String blockName) {
        Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(blockName));
        return block;
    }

    private boolean isIllegal(Block block, BlockPos pos) {
        if (block instanceof BlockCommandBlock || block instanceof BlockBarrier) return true;

        if (block == Blocks.BEDROCK) {
            if (mc.player.dimension == 0) {
                return pos.getY() > 4;
            } else if (mc.player.dimension == -1) {
                return pos.getY() > 127 || (pos.getY() < 123 && pos.getY() > 4);
            } else {
                return true;
            }
        }
        return false;
    }

    public static void renderTracer(double x, double y, double z, double x2, double y2, double z2, int color){
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glColor4f(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color) & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();

        ((IEntityRenderer) mc.entityRenderer).orientCam(mc.getRenderPartialTicks());

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor3d(1d,1d,1d);
        GlStateManager.enableLighting();
    }

    private static class BlockVec {
        public final double x;
        public final double y;
        public final double z;

        public BlockVec(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean equals(Object object) {
            if (object instanceof BlockVec) {
                BlockVec v = (BlockVec) object;
                return Double.compare(x, v.x) == 0 && Double.compare(y, v.y) == 0 && Double.compare(z, v.z) == 0;
            }
            return super.equals(object);
        }

        public double getDistance(BlockVec v) {
            double dx = x - v.x;
            double dy = y - v.y;
            double dz = z - v.z;

            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        }
    }
}
