package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.PreRenderEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static net.minecraft.client.renderer.GlStateManager.resetColor;
import static net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting;


public
class StorageEsp extends Module {
    public final Setting<Float> range = this.register(new Setting<>("Range", 50.0f, 1.0f, 300.0f));


    public final Setting<Boolean> chest = this.register(new Setting<>("Chest", true));
    public final Setting<Boolean> dispenser = this.register(new Setting<>("Dispenser", false));
    public final Setting<Boolean> shulker = this.register(new Setting<>("Shulker", true));
    public final Setting<Boolean> echest = this.register(new Setting<>("Ender Chest", true));
    public final Setting<Boolean> furnace = this.register(new Setting<>("Furnace", false));
    public final Setting<Boolean> hopper = this.register(new Setting<>("Hopper", false));
    public final Setting<Boolean> cart = this.register(new Setting<>("Minecart", false));
    public final Setting<Boolean> frame = this.register(new Setting<>("ItemFrame", false));
    private final Setting<ColorSetting> chestColor = this.register(new Setting<>("ChestColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> shulkColor = this.register(new Setting<>("ShulkerColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> echestColor = this.register(new Setting<>("EChestColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> frameColor = this.register(new Setting<>("FrameColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> shulkerframeColor = this.register(new Setting<>("ShulkFrameColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> furnaceColor = this.register(new Setting<>("FurnaceColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> hopperColor = this.register(new Setting<>("HopperColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> dispenserColor = this.register(new Setting<>("DispenserColor", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> minecartColor = this.register(new Setting<>("MinecartColor", new ColorSetting(0x8800FF00)));
    public Setting<Mode> mode = register(new Setting("Mode", Mode.ShaderBox));
    public final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 10.0f, v -> mode.getValue() != Mode.Box));
    public final Setting<Integer> boxAlpha = this.register(new Setting<>("BoxAlpha", 170, 0, 255, v -> mode.getValue() != Mode.Outline));
    private final ArrayList<Storage> storages = new ArrayList<>();
    public StorageEsp() {
        super("StorageESP", "подсвечивает контейнеры", Module.Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderingShit(PreRenderEvent event) {


        boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        GlStateManager.disableDepth();

        if (mode.getValue() == Mode.ShaderBox || mode.getValue() == Mode.ShaderOutline) {
            checkSetupFBO();
        }
        for (final TileEntity tileEntity : StorageEsp.mc.world.loadedTileEntityList) {
            final BlockPos pos;
            if (((tileEntity instanceof TileEntityChest && this.chest.getValue()) || (tileEntity instanceof TileEntityDispenser && this.dispenser.getValue()) || (tileEntity instanceof TileEntityShulkerBox && this.shulker.getValue()) || (tileEntity instanceof TileEntityEnderChest && this.echest.getValue()) || (tileEntity instanceof TileEntityFurnace && this.furnace.getValue()) || (tileEntity instanceof TileEntityHopper && this.hopper.getValue())) && StorageEsp.mc.player.getDistanceSq(pos = tileEntity.getPos()) <= MathUtil.square(this.range.getValue())) {

                int mode = 0;

                if (tileEntity instanceof TileEntityChest) {
                    TileEntityChest chest = (TileEntityChest) tileEntity;
                    if (chest.adjacentChestZPos != null) {
                        mode = 3;
                    } else if (chest.adjacentChestXPos != null) {
                        mode = 1;
                    } else if (chest.adjacentChestXNeg != null) {
                        mode = 2;
                    } else if (chest.adjacentChestZNeg != null) {
                        mode = 4;
                    }
                }
                storages.add(new Storage(pos, getTileEntityColor(tileEntity), mode));
            }
        }
        for (final Entity entity : StorageEsp.mc.world.loadedEntityList) {
            final BlockPos pos;
            if (((entity instanceof EntityItemFrame && this.frame.getValue()) || (entity instanceof EntityMinecartChest && this.cart.getValue())) && StorageEsp.mc.player.getDistanceSq(pos = entity.getPosition()) <= MathUtil.square(this.range.getValue())) {
                storages.add(new Storage(pos, getEntityColor(entity), 0));
            }
        }
        for (Storage storage : storages) {
            if (mode.getValue() != Mode.ShaderOutline) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1f, 1f, 1f, 1f);
                RenderUtil.drawBoxESP(storage.position, (new Color(storage.color)), false, new Color(storage.color), this.lineWidth.getValue(), mode.getValue() == Mode.Outline || mode.getValue() == Mode.BoxOutline, mode.getValue() == Mode.ShaderBox || mode.getValue() == Mode.Box || mode.getValue() == Mode.BoxOutline, boxAlpha.getValue(), false, storage.getChest());
                resetColor();
                GlStateManager.popMatrix();
            }
        }
        if (depth)
            GlStateManager.enableDepth();
        storages.clear();
    }

    public int getTileEntityColor(final TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            return chestColor.getValue().getColor();
        }
        if (tileEntity instanceof TileEntityEnderChest) {
            return echestColor.getValue().getColor();
        }
        if (tileEntity instanceof TileEntityShulkerBox) {
            return shulkColor.getValue().getColor();
        }
        if (tileEntity instanceof TileEntityFurnace) {
            return furnaceColor.getValue().getColor();
        }
        if (tileEntity instanceof TileEntityHopper) {
            return hopperColor.getValue().getColor();
        }
        if (tileEntity instanceof TileEntityDispenser) {
            return dispenserColor.getValue().getColor();
        }
        return -1;
    }

    private int getEntityColor(final Entity entity) {
        if (entity instanceof EntityMinecartChest) {
            return minecartColor.getValue().getColor();
        }
        if (entity instanceof EntityItemFrame && ((EntityItemFrame) entity).getDisplayedItem().getItem() instanceof ItemShulkerBox) {
            return shulkerframeColor.getValue().getColor();
        }
        if (entity instanceof EntityItemFrame && !(((EntityItemFrame) entity).getDisplayedItem().getItem() instanceof ItemShulkerBox)) {
            return frameColor.getValue().getColor();
        }
        return -1;
    }

    public void checkSetupFBO() {
        final Framebuffer fbo = mc.getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    public void setupFBO(final Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        final int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
    }

    public void renderNormal(final float n) {
        enableStandardItemLighting();
        for (final TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntityChest) && !(tileEntity instanceof TileEntityEnderChest) && !(tileEntity instanceof TileEntityShulkerBox)) {
                continue;
            }
            GL11.glPushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            TileEntityRendererDispatcher.instance.render(tileEntity, tileEntity.getPos().getX() - mc.renderManager.renderPosX, tileEntity.getPos().getY() - mc.renderManager.renderPosY, tileEntity.getPos().getZ() - mc.renderManager.renderPosZ, n);
            GL11.glPopMatrix();
        }
    }

    public void renderColor(final float n) {
        enableStandardItemLighting();
        for (final TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntityChest) && !(tileEntity instanceof TileEntityEnderChest) && !(tileEntity instanceof TileEntityShulkerBox) && !(tileEntity instanceof TileEntityFurnace) && !(tileEntity instanceof TileEntityHopper)) {
                continue;
            }

            setColor(new Color(getTileEntityColor(tileEntity)));

            TileEntityRendererDispatcher.instance.render(tileEntity, tileEntity.getPos().getX() - mc.renderManager.renderPosX, tileEntity.getPos().getY() - mc.renderManager.renderPosY, tileEntity.getPos().getZ() - mc.renderManager.renderPosZ, n);
        }
    }

    public void setColor(final Color c) {
        GL11.glColor3f(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
    }

    public enum Mode {
        Outline, Box, BoxOutline, ShaderOutline, ShaderBox
    }

    private class Storage {

        BlockPos position;

        int color;
        int chest;

        private Storage(BlockPos pos, int color, int chest) {
            this.position = pos;
            this.color = color;
            this.chest = chest;
        }

        public int getChest() {
            return chest;
        }


        public BlockPos getPosition() {
            return position;
        }

        public int getColor() {
            return color;
        }

    }
}
