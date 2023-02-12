package com.mrzak34.thunderhack.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.phobos.ThreadUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;

public class XRay extends Module {
    public XRay() {
        super("XRay", "Искать алмазы на ezzzzz", Category.MISC);
    }
    ArrayList<BlockPos> ores = new ArrayList();
    ArrayList<BlockPos> toCheck = new ArrayList();
    public static int done;
    public static int all;

    BlockPos verycute;

    private Setting<mode> Mode = register(new Setting("Render Mode", mode.FullBox));
    public Setting<Integer> checkSpeed = this.register(new Setting<>("checkSpeed", 4, 1, 5, v -> this.brutForce.getValue()));
    public Setting<Integer> rxz = this.register(new Setting<>("Radius XZ", 20, 5, 200, v -> this.brutForce.getValue()));
    public Setting<Integer> ry = this.register(new Setting<>("Radius Y", 6, 2, 50, v -> this.brutForce.getValue()));

    public enum mode {
        FullBox, Frame;
    }
    public Setting<Boolean> wh = this.register(new Setting<>("wallhack", false));
    public Setting<Boolean> brutForce = this.register(new Setting<>("BrutForce", false));
    public Setting<Boolean> diamond  = this.register(new Setting<>("diamond ", false));
    public Setting<Boolean> gold = this.register(new Setting<>("gold", false));
    public Setting<Boolean> iron = this.register(new Setting<>("iron", false));
    public Setting<Boolean> emerald = this.register(new Setting<>("emerald", false));
    public Setting<Boolean> redstone = this.register(new Setting<>("redstone", false));
    public Setting<Boolean> lapis = this.register(new Setting<>("lapis", false));
    public Setting<Boolean> coal = this.register(new Setting<>("coal", false));
    public Setting<Boolean> wow = this.register(new Setting<>("WowEffect", true,v-> brutForce.getValue()));
    public Setting<Boolean> water = this.register(new Setting<>("water", false));
    public Setting<Boolean> lava = this.register(new Setting<>("lava", false));

    @Override
    public void onEnable() {
        this.ores.clear();
        this.toCheck.clear();
        int radXZ = rxz.getValue();
        int radY = ry.getValue();
        ArrayList<BlockPos> blockPositions = this.getBlocks(radXZ, radY, radXZ);
        for (BlockPos pos : blockPositions) {
            IBlockState state = BlockUtils.getState(pos);
            if (!this.isCheckableOre(Block.getIdFromBlock(state.getBlock()))) continue;
            this.toCheck.add(pos);
        }
        all = this.toCheck.size();
        done = 0;
        if(wh.getValue() && brutForce.getValue()){
            wh.setValue(false);
        }
        if(!brutForce.getValue()){
            mc.renderGlobal.loadRenderers();
        }
    }



    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
        super.onDisable();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion event) {
        for (int i = 0; i < this.checkSpeed.getValue(); ++i) {
            if (this.toCheck.size() < 1) {
                return;
            }
            BlockPos pos = this.toCheck.remove(0);
            ++done;
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            if(wow.getValue()) {
                verycute = pos;
            }

        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent e) {
        if (e.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange p = (SPacketBlockChange)e.getPacket();
            if (this.isEnabledOre(Block.getIdFromBlock(p.getBlockState().getBlock()))) {
                this.ores.add(p.getBlockPosition());
            }
        } else if (e.getPacket() instanceof SPacketMultiBlockChange) {
            SPacketMultiBlockChange p = (SPacketMultiBlockChange)e.getPacket();
            for (SPacketMultiBlockChange.BlockUpdateData dat : p.getChangedBlocks()) {
                if (!this.isEnabledOre(Block.getIdFromBlock(dat.getBlockState().getBlock()))) continue;
                this.ores.add(dat.getPos());
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent e) {
        try {
            for (BlockPos pos : this.ores) {
                IBlockState state = BlockUtils.getState(pos);
                Block mat = state.getBlock();
                if (Mode.getValue() == mode.FullBox) {
                    if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 56 && this.diamond.getValue() && Block.getIdFromBlock(mat) == 56) {
                        RenderUtil.blockEsp(pos, new Color(0, 255, 255, 50), 1.0, 1.0);
                    }
                    if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 14 && this.gold.getValue() && Block.getIdFromBlock(mat) == 14) {
                        RenderUtil.blockEsp(pos, new Color(255, 215, 0, 100), 1.0, 1.0);
                    }
                    if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 15 && this.iron.getValue() && Block.getIdFromBlock(mat) == 15) {
                        RenderUtil.blockEsp(pos, new Color(213, 213, 213, 100), 1.0, 1.0);
                    }
                    if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 129 && this.emerald.getValue() && Block.getIdFromBlock(mat) == 129) {
                        RenderUtil.blockEsp(pos, new Color(0, 255, 77, 100), 1.0, 1.0);
                    }
                    if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 73 && this.redstone.getValue() && Block.getIdFromBlock(mat) == 73) {
                        RenderUtil.blockEsp(pos, new Color(255, 0, 0, 100), 1.0, 1.0);
                    }
                    if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 16 && this.coal.getValue() && Block.getIdFromBlock(mat) == 16) {
                        RenderUtil.blockEsp(pos, new Color(0, 0, 0, 100), 1.0, 1.0);
                    }
                    if (Block.getIdFromBlock(mat) == 0 || Block.getIdFromBlock(mat) != 21 || !this.lapis.getValue() || Block.getIdFromBlock(mat) != 21)
                        continue;
                    RenderUtil.blockEsp(pos, new Color(38, 97, 156, 100), 1.0, 1.0);
                    continue;
                }

                if (Mode.getValue() != mode.Frame) continue;
                if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 56 && this.diamond.getValue() && Block.getIdFromBlock(mat) == 56) {
                    RenderUtil.blockEspFrame(pos, 0.0, 255.0, 255.0);
                }
                if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 14 && this.gold.getValue() && Block.getIdFromBlock(mat) == 14) {
                    RenderUtil.blockEspFrame(pos, 255.0, 215.0, 0.0);
                }
                if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 15 && this.iron.getValue() && Block.getIdFromBlock(mat) == 15) {
                    RenderUtil.blockEspFrame(pos, 213.0, 213.0, 213.0);
                }
                if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 129 && this.emerald.getValue() && Block.getIdFromBlock(mat) == 129) {
                    RenderUtil.blockEspFrame(pos, 0.0, 255.0, 77.0);
                }
                if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 73 && this.redstone.getValue() && Block.getIdFromBlock(mat) == 73) {
                    RenderUtil.blockEspFrame(pos, 255.0, 0.0, 0.0);
                }
                if (Block.getIdFromBlock(mat) != 0 && Block.getIdFromBlock(mat) == 16 && this.coal.getValue() && Block.getIdFromBlock(mat) == 16) {
                    RenderUtil.blockEspFrame(pos, 0.0, 0.0, 0.0);
                }
                if (Block.getIdFromBlock(mat) == 0 || Block.getIdFromBlock(mat) != 21 || !this.lapis.getValue() || Block.getIdFromBlock(mat) != 21)
                    continue;
                RenderUtil.blockEspFrame(pos, 38.0, 97.0, 156.0);

            }
            if (verycute != null && (done != all) && wow.getValue()) {
                RenderUtil.drawBlockOutline(verycute, new Color(255, 0, 30), 1, false,0);
            }
        } catch (Exception ignored){

        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        String f = "" + all;
        String g = "" + done;
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer font = XRay.mc.fontRenderer;
        int size = 125;
        float xOffset = (float)sr.getScaledWidth() / 2.0f - (float)size / 2.0f;
        float yOffset = 5.0f;
        float Y = 0.0f;
        RenderUtil.rectangleBordered(xOffset + 2.0f, yOffset + 1.0f, xOffset + 10.0f + (float)size + (float)font.getStringWidth(g) + 1.0f, yOffset + (float)size / 6.0f + 3.0f + ((float)font.FONT_HEIGHT + 2.2f), 0.5, 90, 0);
        RenderUtil.rectangleBordered(xOffset + 3.0f, yOffset + 2.0f, xOffset + 10.0f + (float)size + (float)font.getStringWidth(g), yOffset + (float)size / 6.0f + 2.0f + ((float)font.FONT_HEIGHT + 2.2f), 0.5, 27, 61);
        font.drawStringWithShadow("" + ChatFormatting.GREEN + "Done: " + ChatFormatting.WHITE + done + " / " + ChatFormatting.RED + "All: " + ChatFormatting.WHITE + all, xOffset + 25.0f, yOffset + (float)font.FONT_HEIGHT + 4.0f, -1);
        GlStateManager.disableBlend();
    }

    private boolean isCheckableOre(int id) {
        int check = 0;
        int check1 = 0;
        int check2 = 0;
        int check3 = 0;
        int check4 = 0;
        int check5 = 0;
        int check6 = 0;
        if (this.diamond.getValue() && id != 0) {
            check = 56;
        }
        if (this.gold.getValue() && id != 0) {
            check1 = 14;
        }
        if (this.iron.getValue() && id != 0) {
            check2 = 15;
        }
        if (this.emerald.getValue() && id != 0) {
            check3 = 129;
        }
        if (this.redstone.getValue() && id != 0) {
            check4 = 73;
        }
        if (this.coal.getValue() && id != 0) {
            check5 = 16;
        }
        if (this.lapis.getValue() && id != 0) {
            check6 = 21;
        }
        if (id == 0) {
            return false;
        }
        return id == check || id == check1 || id == check2 || id == check3 || id == check4 || id == check5 || id == check6;
    }

    private boolean isEnabledOre(int id) {
        int check = 0;
        int check1 = 0;
        int check2 = 0;
        int check3 = 0;
        int check4 = 0;
        int check5 = 0;
        int check6 = 0;
        if (this.diamond.getValue() && id != 0) {
            check = 56;
        }
        if (this.gold.getValue() && id != 0) {
            check1 = 14;
        }
        if (this.iron.getValue() && id != 0) {
            check2 = 15;
        }
        if (this.emerald.getValue() && id != 0) {
            check3 = 129;
        }
        if (this.redstone.getValue() && id != 0) {
            check4 = 73;
        }
        if (this.coal.getValue() && id != 0) {
            check5 = 16;
        }
        if (this.lapis.getValue() && id != 0) {
            check6 = 21;
        }
        if (id == 0) {
            return false;
        }
        return id == check || id == check1 || id == check2 || id == check3 || id == check4 || id == check5 || id == check6;
    }

    private ArrayList<BlockPos> getBlocks(int x, int y, int z) {
        BlockPos min = new BlockPos(Util.mc.player.posX - (double)x, Util.mc.player.posY - (double)y, Util.mc.player.posZ - (double)z);
        BlockPos max = new BlockPos(Util.mc.player.posX + (double)x, Util.mc.player.posY + (double)y, Util.mc.player.posZ + (double)z);
        return BlockUtils.getAllInBox(min, max);
    }

    public Boolean shouldRender(Block cast) {
        if(cast == Blocks.DIAMOND_ORE && diamond.getValue()){
            return true;
        }
        if(cast == Blocks.GOLD_ORE && gold.getValue()){
            return true;
        }
        if(cast == Blocks.WATER && water.getValue()){
            return true;
        }
        if(cast == Blocks.LAVA && lava.getValue()){
            return true;
        }
        if(cast == Blocks.IRON_ORE && iron.getValue()){
            return true;
        }
        if(cast == Blocks.EMERALD_ORE && emerald.getValue()){
            return true;
        }
        if(cast == Blocks.REDSTONE_ORE && redstone.getValue()){
            return true;
        }
        if(cast == Blocks.LAPIS_ORE && lapis.getValue()){
            return true;
        }
        if(cast == Blocks.COAL_ORE && coal.getValue()){
            return true;
        }
        return !wh.getValue();
    }


}

