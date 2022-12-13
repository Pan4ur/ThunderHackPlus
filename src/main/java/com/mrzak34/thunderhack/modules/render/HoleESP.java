package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.CrystalUtils;
import com.mrzak34.thunderhack.util.FaceMasks;
import com.mrzak34.thunderhack.util.TessellatorUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class HoleESP
        extends Module {
    private final Setting<Integer> rangeXZ = this.register(new Setting<>("RangeXZ", 8, 1, 25));
    private final Setting<Integer> rangeY = this.register(new Setting<>("RangeY", 5, 1, 25));

    private final Setting<Float> width = this.register(new Setting<>("Width", 1.5F, 0F, 10F));
    private final Setting<Float> height = this.register(new Setting<>("Height", 1F, -2F, 8F));

    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.FULL));
    private final Setting<Integer> fadeAlpha = this.register(new Setting<>("FadeAlpha", 0, 0, 255, v -> mode.getValue() == Mode.FADE));
    private final Setting<Boolean> depth = this.register(new Setting<>("Depth", true, v -> mode.getValue() == Mode.FADE));
    private final Setting<Boolean> noLineDepth = this.register(new Setting<>("NotLines", true, v -> mode.getValue() == Mode.FADE && depth.getValue()));
    private final Setting<Lines> lines = this.register(new Setting<>("Lines", Lines.BOTTOM, v -> mode.getValue() == Mode.FADE));
    private final Setting<Boolean> sides = this.register(new Setting<>("Sides", false, v -> mode.getValue() == Mode.FULL || mode.getValue() == Mode.FADE));
    private final Setting<Boolean> notSelf = this.register(new Setting<>("NotSelf", true, v -> mode.getValue() == Mode.FADE));
    private final Setting<Boolean> twoBlock = this.register(new Setting<>("TwoBlock", false));
    private final Setting<Boolean> bedrock = this.register(new Setting<>("Bedrock", true));
    private final Setting<Boolean> obsidian = this.register(new Setting<>("Obsidian", true));
    private final Setting<Boolean> vunerable = this.register(new Setting<>("Vulnerable", false));
    private final Setting<Boolean> selfVunerable = this.register(new Setting<>("Self", false));

    private List<BlockPos> obiHoles = new ArrayList<>();
    private List<BlockPos> bedrockHoles = new ArrayList<>();
    private List<TwoBlockHole> obiHolesTwoBlock = new ArrayList<>();
    private List<TwoBlockHole> bedrockHolesTwoBlock = new ArrayList<>();


    // private final Setting<ColorSetting> bRockHoleColor = new Setting<>("BedrockColor", new ColorSetting(0x8800FF00)).withVisibility(bedrock::getValue);
    // private final Setting<ColorSetting> bRockLineColor = new Setting<>("BedrockLineColor", new ColorSetting(0xFF00FF00)).withVisibility(bedrock::getValue);
    // private final Setting<ColorSetting> obiHoleColor = new Setting<>("ObiColor", new ColorSetting(0x88FF0000)).withVisibility(obsidian::getValue);              конса момент
    // private final Setting<ColorSetting> obiLineHoleColor = new Setting<>("ObiLineColor", new ColorSetting(0xFFFF0000)).withVisibility(obsidian::getValue);
    // private final Setting<ColorSetting> vunerableColor = new Setting<>("VunColor", new ColorSetting(0x66FF00FF)).withVisibility(vunerable::getValue);
    // private final Setting<ColorSetting> vunerableLineColor = new Setting<>("VunLineColor", new ColorSetting(0xFFFF00FF)).withVisibility(vunerable::getValue);


    private final Setting<ColorSetting> bRockHoleColor = this.register(new Setting<>("bRockHoleColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> bRockLineColor = this.register(new Setting<>("bRockLineColor", new ColorSetting(0x88FF0000)));
    private final Setting<ColorSetting> obiHoleColor = this.register(new Setting<>("obiHoleColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> obiLineHoleColor = this.register(new Setting<>("obiLineHoleColor", new ColorSetting(0xFFFF0000)));
    private final Setting<ColorSetting> vunerableColor = this.register(new Setting<>("vunerableColor", new ColorSetting(0x66FF00FF)));
    private final Setting<ColorSetting> vunerableLineColor = this.register(new Setting<>("vunerableLineColor", new ColorSetting(0xFFFF00FF)));

    private enum Lines {
        FULL, BOTTOM, TOP
    }

    public HoleESP() {
        super("HoleESP", "Surrounds you with Obsidian", Category.RENDER, true, false, false);
    }

    private enum Mode {
        BOTTOM,
        OUTLINE,
        FULL,
        WIREFRAME,
        FADE
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null) return;
        obiHoles.clear();
        bedrockHoles.clear();
        obiHolesTwoBlock.clear();
        bedrockHolesTwoBlock.clear();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-rangeXZ.getValue(), -rangeY.getValue(), -rangeXZ.getValue()), mc.player.getPosition().add(rangeXZ.getValue(), rangeY.getValue(), rangeXZ.getValue()));

        for (BlockPos pos : blocks) {
            if (!(
                    mc.world.getBlockState(pos).getMaterial().blocksMovement() &&
                            mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement() &&
                            mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial().blocksMovement()
            )) {


                if (BlockUtils.validObi(pos) && obsidian.getValue()) {
                    this.obiHoles.add(pos);
                } else {
                    final BlockPos validTwoBlock = BlockUtils.validTwoBlockObiXZ(pos);
                    if (validTwoBlock != null && obsidian.getValue() && twoBlock.getValue()) {
                        this.obiHolesTwoBlock.add(new TwoBlockHole(pos, pos.add(validTwoBlock.getX(), validTwoBlock.getY(), validTwoBlock.getZ())));
                    }
                }

                if (BlockUtils.validBedrock(pos) && bedrock.getValue()) {
                    this.bedrockHoles.add(pos);
                } else {
                    final BlockPos validTwoBlock = BlockUtils.validTwoBlockBedrockXZ(pos);
                    if (validTwoBlock != null && bedrock.getValue() && twoBlock.getValue()) {
                        this.bedrockHolesTwoBlock.add(new TwoBlockHole(pos, pos.add(validTwoBlock.getX(), validTwoBlock.getY(), validTwoBlock.getZ())));
                    }
                }


            }
        }

    }

    /*
    @Override
    public String get() {
        return mode.getValue().toString().charAt(0) + mode.getValue().toString().substring(1).toLowerCase(); // хуета чтоб в аррей листе было
    }

     */

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (mode.getValue() == Mode.BOTTOM) {
            GlStateManager.pushMatrix();
            beginRender();
            GlStateManager.enableBlend();
            GlStateManager.glLineWidth(5.0f);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            for (BlockPos pos : this.bedrockHoles) {
                final AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1);

                drawBoundingBox(box, bRockHoleColor.getValue().getColorObject());
            }

            for (BlockPos pos : this.obiHoles) {
                final AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1);

                drawBoundingBox(box, obiHoleColor.getValue().getColorObject());
            }

            for (TwoBlockHole pos : this.bedrockHolesTwoBlock) {
                final AxisAlignedBB box = new AxisAlignedBB(pos.getOne().getX(), pos.getOne().getY(), pos.getOne().getZ(), pos.getExtra().getX() + 1, pos.getExtra().getY(), pos.getExtra().getZ() + 1);

                drawBoundingBox(box, bRockHoleColor.getValue().getColorObject());
            }

            for (TwoBlockHole pos : this.obiHolesTwoBlock) {
                final AxisAlignedBB box = new AxisAlignedBB(pos.getOne().getX(), pos.getOne().getY(), pos.getOne().getZ(), pos.getExtra().getX() + 1, pos.getExtra().getY(), pos.getExtra().getZ() + 1);

                drawBoundingBox(box, obiHoleColor.getValue().getColorObject());
            }

            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            endRender();
            GlStateManager.popMatrix();
        } else {
            for (BlockPos pos : this.bedrockHoles) {
                drawHole(pos, bRockHoleColor.getValue(), bRockLineColor.getValue());
            }

            for (BlockPos pos : this.obiHoles) {
                drawHole(pos, obiHoleColor.getValue(), obiLineHoleColor.getValue());
            }

            for (TwoBlockHole pos : this.bedrockHolesTwoBlock) {
                drawHoleTwoBlock(pos.getOne(), pos.getExtra(), bRockHoleColor.getValue(), bRockLineColor.getValue());
            }

            for (TwoBlockHole pos : this.obiHolesTwoBlock) {
                drawHoleTwoBlock(pos.getOne(), pos.getExtra(), obiHoleColor.getValue(), obiLineHoleColor.getValue());
            }
        }

        if (vunerable.getValue()) {
            List<Entity> targetsInRange = mc.world.loadedEntityList.
                    stream()
                    .filter(e -> e instanceof EntityPlayer)
                    .filter(e -> e.getDistance(mc.player) < rangeXZ.getValue())
                    .filter(e -> e != mc.player || selfVunerable.getValue())
                    .filter(e -> !Thunderhack.friendManager.isFriend(e.getName()))
                    .sorted(Comparator.comparing(e -> mc.player.getDistance(e)))
                    .collect(Collectors.toList());

            for (Entity target : targetsInRange) {
                ArrayList<BlockPos> vuns = getVulnerablePositions(new BlockPos(target));

                for (BlockPos pos : vuns) {
                    AxisAlignedBB axisAlignedBB = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);
                    TessellatorUtil.prepare();
                    TessellatorUtil.drawBox(axisAlignedBB, true, 1, vunerableColor.getValue().getColorObject(), vunerableColor.getValue().getAlpha(), FaceMasks.Quad.ALL);
                    TessellatorUtil.drawBoundingBox(axisAlignedBB, width.getValue(), vunerableLineColor.getValue().getColorObject());
                    TessellatorUtil.release();
                }
            }
        }
    }

    public void drawHole(BlockPos pos, ColorSetting color, ColorSetting lineColor) {
        AxisAlignedBB axisAlignedBB = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);

        axisAlignedBB = axisAlignedBB.setMaxY(axisAlignedBB.minY + height.getValue());

        if (mode.getValue() == Mode.FULL) {
            TessellatorUtil.prepare();
            TessellatorUtil.drawBox(axisAlignedBB, true, 1, color.getColorObject(), color.getAlpha(), sides.getValue() ? FaceMasks.Quad.NORTH | FaceMasks.Quad.SOUTH | FaceMasks.Quad.WEST | FaceMasks.Quad.EAST : FaceMasks.Quad.ALL);
            TessellatorUtil.release();
        }

        if (mode.getValue() == Mode.FULL || mode.getValue() == Mode.OUTLINE) {
            TessellatorUtil.prepare();
            TessellatorUtil.drawBoundingBox(axisAlignedBB, width.getValue(), lineColor.getColorObject());
            TessellatorUtil.release();
        }

        if (mode.getValue() == Mode.WIREFRAME) {
            prepareGL2();
            drawWireframe2(axisAlignedBB.offset(-((IRenderManager) mc.getRenderManager()).getRenderPosX(), -((IRenderManager) mc.getRenderManager()).getRenderPosY(), -((IRenderManager) mc.getRenderManager()).getRenderPosZ()), lineColor.getColor(), width.getValue());
            releaseGL2();
        }

        if (mode.getValue() == Mode.FADE) {
            AxisAlignedBB tBB = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);
            tBB = tBB.setMaxY(tBB.minY + height.getValue());

            if (mc.player.getEntityBoundingBox() != null && tBB.intersects(mc.player.getEntityBoundingBox()) && notSelf.getValue()) {
                tBB = tBB.setMaxY(Math.min(tBB.maxY, mc.player.posY + 1D));
            }

            TessellatorUtil.prepare();
            if (depth.getValue()) {
                GlStateManager.enableDepth();
                tBB = tBB.shrink(0.01D);
            }
            TessellatorUtil.drawBox(tBB, true, height.getValue(), color.getColorObject(), fadeAlpha.getValue(), sides.getValue() ? FaceMasks.Quad.NORTH | FaceMasks.Quad.SOUTH | FaceMasks.Quad.WEST | FaceMasks.Quad.EAST : FaceMasks.Quad.ALL);
            if (width.getValue() >= 0.1F) {
                if (lines.getValue() == Lines.BOTTOM) {
                    tBB = new AxisAlignedBB(tBB.minX, tBB.minY, tBB.minZ, tBB.maxX, tBB.minY, tBB.maxZ);
                } else if (lines.getValue() == Lines.TOP) {
                    tBB = new AxisAlignedBB(tBB.minX, tBB.maxY, tBB.minZ, tBB.maxX, tBB.maxY, tBB.maxZ);
                }
                if (noLineDepth.getValue()) {
                    GlStateManager.disableDepth();
                }
                TessellatorUtil.drawBoundingBox(tBB, width.getValue(), lineColor.getColorObject(), fadeAlpha.getValue());
            }
            TessellatorUtil.release();
        }
    }

    public void drawHoleTwoBlock(BlockPos pos, BlockPos two, ColorSetting color, ColorSetting lineColor) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), two.getX() + 1, two.getY() + height.getValue(), two.getZ() + 1);

        if (mode.getValue() == Mode.FULL) {
            TessellatorUtil.prepare();
            TessellatorUtil.drawBox(axisAlignedBB, true, 1, color.getColorObject(), color.getAlpha(), sides.getValue() ? FaceMasks.Quad.NORTH | FaceMasks.Quad.SOUTH | FaceMasks.Quad.WEST | FaceMasks.Quad.EAST : FaceMasks.Quad.ALL);
            TessellatorUtil.release();
        }

        if (mode.getValue() == Mode.FULL || mode.getValue() == Mode.OUTLINE) {
            TessellatorUtil.prepare();
            TessellatorUtil.drawBoundingBox(axisAlignedBB, width.getValue(), lineColor.getColorObject());
            TessellatorUtil.release();
        }

        if (mode.getValue() == Mode.WIREFRAME) {
            prepareGL2();
            drawWireframe2(axisAlignedBB.offset(-((IRenderManager) mc.getRenderManager()).getRenderPosX(), -((IRenderManager) mc.getRenderManager()).getRenderPosY(), -((IRenderManager) mc.getRenderManager()).getRenderPosZ()), lineColor.getColor(), width.getValue());
            releaseGL2();
        }

        if (mode.getValue() == Mode.FADE) {
            AxisAlignedBB tBB = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), two.getX() + 1, two.getY() + height.getValue(), two.getZ() + 1);

            if (tBB.intersects(mc.player.getEntityBoundingBox()) && notSelf.getValue()) {
                tBB = tBB.setMaxY(Math.min(tBB.maxY, mc.player.posY + 1D));
            }

            TessellatorUtil.prepare();
            if (depth.getValue()) {
                GlStateManager.enableDepth();
                tBB = tBB.shrink(0.01D);
            }
            TessellatorUtil.drawBox(tBB, true, height.getValue(), color.getColorObject(), fadeAlpha.getValue(), sides.getValue() ? FaceMasks.Quad.NORTH | FaceMasks.Quad.SOUTH | FaceMasks.Quad.WEST | FaceMasks.Quad.EAST : FaceMasks.Quad.ALL);
            if (width.getValue() >= 0.1F) {
                if (lines.getValue() == Lines.BOTTOM) {
                    tBB = new AxisAlignedBB(tBB.minX, tBB.minY, tBB.minZ, tBB.maxX, tBB.minY, tBB.maxZ);
                } else if (lines.getValue() == Lines.TOP) {
                    tBB = new AxisAlignedBB(tBB.minX, tBB.maxY, tBB.minZ, tBB.maxX, tBB.maxY, tBB.maxZ);
                }
                if (noLineDepth.getValue()) {
                    GlStateManager.disableDepth();
                }
                TessellatorUtil.drawBoundingBox(tBB, width.getValue(), lineColor.getColorObject(), fadeAlpha.getValue());
            }
            TessellatorUtil.release();
        }
    }

    private static class TwoBlockHole {

        private final BlockPos one;
        private final BlockPos extra;

        public TwoBlockHole(BlockPos one, BlockPos extra) {
            this.one = one;
            this.extra = extra;
        }

        public BlockPos getOne() {
            return one;
        }

        public BlockPos getExtra() {
            return extra;
        }

    }

    public static void prepareGL2() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glDepthMask(false);


        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);

        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void releaseGL2() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        GL11.glDepthMask(true);

        GL11.glCullFace(GL11.GL_BACK);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void drawWireframe2(AxisAlignedBB axisAlignedBB, int color, float lineWidth) {
        GL11.glPushMatrix();

        GL11.glEnable(3042);

        GL11.glBlendFunc(770, 771);

        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);

        GL11.glLineWidth(lineWidth);

        GL11.glColor4f(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color) & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);

        if (axisAlignedBB == null) {
            return;
        }


        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();


        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        GL11.glVertex3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        GL11.glEnd();

        GL11.glLineWidth(1.0f);

        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);

        GL11.glDepthMask(true);

        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }



    public static ArrayList<BlockPos> getVulnerablePositions(BlockPos root) {
        ArrayList<BlockPos> vP = new ArrayList<>();
        if (!(mc.world.getBlockState(root).getBlock() instanceof BlockAir)) {
            return vP;
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (mc.world.getBlockState(root.offset(facing)).getBlock() instanceof BlockAir) return new ArrayList<>();
            if (!(mc.world.getBlockState(root.offset(facing)).getBlock() instanceof BlockObsidian)) continue;
            if (CrystalUtils.canPlaceCrystal(root.offset(facing, 2).down()) && mc.world.getBlockState(root.offset(facing)).getBlock() != Blocks.AIR) {
                vP.add(root.offset(facing));
            } else if (CrystalUtils.canPlaceCrystal(root.offset(facing)) && mc.world.getBlockState(root.offset(facing)).getBlock() != Blocks.AIR && (
                    mc.world.getBlockState(root.offset(facing).down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(root.offset(facing).down()).getBlock() == Blocks.OBSIDIAN
            )) {
                vP.add(root.offset(facing));
            }
        }
        return vP;
    }

    public static void drawBoxOutline(AxisAlignedBB box, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        //drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha); так и было
        buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, 0.0F).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBox(final AxisAlignedBB bb, Color color) {
        AxisAlignedBB boundingBox = bb.offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

        drawBoxOutline(boundingBox.grow(0.0020000000949949026D), color.getRed() * 255, color.getGreen() * 255, color.getBlue() * 255, color.getAlpha() * 255);
    }

    public static void beginRender() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        //GlStateManager.color(1, 1, 1, 1); из за этого ломаеца прозрачность хз
    }

    /**
     * ends the use of GL, to make sure that everything was reset properly
     * Also came from Kami
     */
    public static void endRender() {
        //GlStateManager.resetColor();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

}

