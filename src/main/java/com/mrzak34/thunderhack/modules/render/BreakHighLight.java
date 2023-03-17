package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.mixin.mixins.IPlayerControllerMP;
import com.mrzak34.thunderhack.mixin.mixins.IRenderGlobal;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.TessellatorUtil;
import com.mrzak34.thunderhack.util.render.BlockRenderUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class BreakHighLight extends Module {

    private final Setting<BreakRenderMode> bRenderMode = this.register(new Setting<>("BRenderMove", BreakRenderMode.GROW));
    private final Setting<Float> bRange = this.register(new Setting<>("BRange", 15f, 5f, 255f));
    private final Setting<Boolean> bOutline = this.register(new Setting<>("BOutline", true));
    private final Setting<Boolean> bWireframe = this.register(new Setting<>("BWireframe", false));
    private final Setting<Float> bWidth = this.register(new Setting<>("BWidth", 1.5f, 1f, 10f));
    private final Setting<ColorSetting> bOutlineColor = this.register(new Setting<>("BOutlineColor", new ColorSetting(0xFFFF0000)));
    private final Setting<ColorSetting> bCrossOutlineColor = this.register(new Setting<>("BCrossOutlineColor", new ColorSetting(0xFFFF0000)));
    private final Setting<Boolean> naame = this.register(new Setting<>("Name", true));
    private final Setting<Boolean> bFill = this.register(new Setting<>("BFill", true));
    private final Setting<ColorSetting> bFillColor = this.register(new Setting<>("BFillColor", new ColorSetting(0x66FF0000)));
    private final Setting<ColorSetting> bCrossFillColor = this.register(new Setting<>("BCrossFillColor", new ColorSetting(0x66FF0000)));
    private final Setting<Boolean> bTracer = this.register(new Setting<>("BTracer", false));
    private final Setting<ColorSetting> bTracerColor = this.register(new Setting<>("BTracerColor", new ColorSetting(0xFFFF0000)));
    private final Setting<Boolean> pOutline = this.register(new Setting<>("POutline", true));
    private final Setting<Boolean> pWireframe = this.register(new Setting<>("PWireframe", false));
    private final Setting<Float> pWidth = this.register(new Setting<>("PWidth", 1.5f, 1f, 10f));
    private final Setting<ColorSetting> pOutlineColor = this.register(new Setting<>("POutlineColor", new ColorSetting(0xFF0000FF)));
    private final Setting<Boolean> pFill = this.register(new Setting<>("PFill", true));
    private final Setting<ColorSetting> pFillColor = this.register(new Setting<>("PFillColor", new ColorSetting(0x660000FF)));
    public BreakHighLight() {
        super("BreakHighLight", "рендерит ломания-блоков", Category.RENDER);
    }

    public static void renderBreakingBB2(AxisAlignedBB bb, Color fill, Color outline) {
        BlockRenderUtil.prepareGL();
        TessellatorUtil.drawBox(bb, fill);
        BlockRenderUtil.releaseGL();
        BlockRenderUtil.prepareGL();
        TessellatorUtil.drawBoundingBox(bb, 1, outline);
        BlockRenderUtil.releaseGL();
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.world == null) return;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();

        if (mc.playerController.getIsHittingBlock()) {
            float progress = ((IPlayerControllerMP) mc.playerController).getCurBlockDamageMP();

            BlockPos pos = ((IPlayerControllerMP) mc.playerController).getCurrentBlock();
            AxisAlignedBB bb = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);

            switch (bRenderMode.getValue()) {
                case GROW: {
                    renderBreakingBB(bb.shrink(0.5 - progress * 0.5), bFillColor.getValue(), bOutlineColor.getValue());
                    break;
                }
                case SHRINK: {
                    renderBreakingBB(bb.shrink(progress * 0.5), bFillColor.getValue(), bOutlineColor.getValue());
                    break;
                }
                case CROSS: {
                    renderBreakingBB(bb.shrink(0.5 - progress * 0.5), bFillColor.getValue(), bOutlineColor.getValue());
                    renderBreakingBB(bb.shrink(progress * 0.5), bCrossFillColor.getValue(), bCrossOutlineColor.getValue());
                    break;
                }
                default: {
                    renderBreakingBB(bb, bFillColor.getValue(), bOutlineColor.getValue());
                    break;
                }
            }

            if (bTracer.getValue()) {
                Vec3d eyes = new Vec3d(0, 0, 1)
                        .rotatePitch(-(float) Math
                                .toRadians(mc.player.rotationPitch))
                        .rotateYaw(-(float) Math
                                .toRadians(mc.player.rotationYaw));

                renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z,
                        pos.getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX() + 0.5,
                        pos.getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY() + 0.5,
                        pos.getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ() + 0.5,
                        bTracerColor.getValue().getColor());
            }
        }

        ((IRenderGlobal) mc.renderGlobal).getDamagedBlocks().forEach(((integer, destroyBlockProgress) -> {
            renderGlobalBreakage(destroyBlockProgress);
            Entity object = mc.world.getEntityByID(integer);

            if (object != null && naame.getValue() && !object.getName().equals(mc.player.getName())) {
                GlStateManager.pushMatrix();
                BlockPos pos = destroyBlockProgress.getPosition();
                try {
                    RenderUtil.glBillboardDistanceScaled((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f, mc.player, 1);
                } catch (Exception ignored) {

                }
                String name = object.getName();
                FontRender.drawString3(name, (int) -(FontRender.getStringWidth(name) / 2.0D), -4, -1);
                GlStateManager.popMatrix();
            }

        }));
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderGlobalBreakage(DestroyBlockProgress destroyBlockProgress) {
        if (destroyBlockProgress != null) {
            BlockPos pos = destroyBlockProgress.getPosition();
            if (mc.playerController.getIsHittingBlock()) {
                if (((IPlayerControllerMP) mc.playerController).getCurrentBlock().equals(pos)) return;
            }
            if (mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > bRange.getValue()) return;
            float progress = Math.min(1F, (float) destroyBlockProgress.getPartialBlockDamage() / 8F);

            AxisAlignedBB bb = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);

            switch (bRenderMode.getValue()) {
                case GROW: {
                    renderBreakingBB(bb.shrink(0.5 - progress * 0.5), bFillColor.getValue(), bOutlineColor.getValue());
                    break;
                }
                case SHRINK: {
                    renderBreakingBB(bb.shrink(progress * 0.5), bFillColor.getValue(), bOutlineColor.getValue());
                    break;
                }
                case CROSS: {
                    renderBreakingBB(bb.shrink(0.5 - progress * 0.5), bFillColor.getValue(), bOutlineColor.getValue());
                    renderBreakingBB(bb.shrink(progress * 0.5), bCrossFillColor.getValue(), bCrossOutlineColor.getValue());
                    break;
                }
                default: {
                    renderBreakingBB(bb, bFillColor.getValue(), bOutlineColor.getValue());
                    break;
                }
            }

            if (bTracer.getValue()) {
                Vec3d eyes = new Vec3d(0, 0, 1)
                        .rotatePitch(-(float) Math
                                .toRadians(mc.player.rotationPitch))
                        .rotateYaw(-(float) Math
                                .toRadians(mc.player.rotationYaw));

                renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z,
                        pos.getX() - ((IRenderManager) mc.getRenderManager()).getRenderPosX() + 0.5,
                        pos.getY() - ((IRenderManager) mc.getRenderManager()).getRenderPosY() + 0.5,
                        pos.getZ() - ((IRenderManager) mc.getRenderManager()).getRenderPosZ() + 0.5,
                        bTracerColor.getValue().getColor());
            }
        }
    }

    public void renderPlacingBB(AxisAlignedBB bb) {
        if (pFill.getValue()) {
            BlockRenderUtil.prepareGL();
            TessellatorUtil.drawBox(bb, pFillColor.getValue().getColorObject());
            BlockRenderUtil.releaseGL();
        }

        if (pOutline.getValue()) {
            BlockRenderUtil.prepareGL();
            if (pWireframe.getValue()) {
                BlockRenderUtil.drawWireframe(bb.offset(-((IRenderManager) Module.mc.getRenderManager()).getRenderPosX(), -((IRenderManager) Module.mc.getRenderManager()).getRenderPosY(), -((IRenderManager) Module.mc.getRenderManager()).getRenderPosZ()), pOutlineColor.getValue().getColor(), pWidth.getValue());
            } else {
                TessellatorUtil.drawBoundingBox(bb, pWidth.getValue(), pOutlineColor.getValue().getColorObject());
            }
            BlockRenderUtil.releaseGL();
        }
    }

    private void renderBreakingBB(AxisAlignedBB bb, ColorSetting fill, ColorSetting outline) {
        if (bFill.getValue()) {
            BlockRenderUtil.prepareGL();
            TessellatorUtil.drawBox(bb, fill.getColorObject());
            BlockRenderUtil.releaseGL();
        }

        if (bOutline.getValue()) {
            BlockRenderUtil.prepareGL();
            if (bWireframe.getValue()) {
                BlockRenderUtil.drawWireframe(bb.offset(-((IRenderManager) mc.getRenderManager()).getRenderPosX(), -((IRenderManager) mc.getRenderManager()).getRenderPosY(), -((IRenderManager) mc.getRenderManager()).getRenderPosZ()), outline.getColor(), bWidth.getValue());
            } else {
                TessellatorUtil.drawBoundingBox(bb, bWidth.getValue(), outline.getColorObject());
            }
            BlockRenderUtil.releaseGL();
        }
    }

    private void renderTracer(double x, double y, double z, double x2, double y2, double z2, int color) {
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
        GL11.glColor3d(1d, 1d, 1d);
        GlStateManager.enableLighting();
    }


    private enum BreakRenderMode {
        GROW, SHRINK, CROSS, STATIC
    }
}
