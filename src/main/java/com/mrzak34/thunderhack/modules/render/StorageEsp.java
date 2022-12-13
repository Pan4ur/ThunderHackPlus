package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.*;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.util.PaletteHelper;
import net.minecraft.util.math.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;

import java.awt.*;
import java.util.*;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.item.*;


public
class StorageEsp extends Module
{
    private final Setting<Float> range = (Setting<Float>)this.register(new Setting("Range", 50.0f, 1.0f, 300.0f));
    private final Setting<Boolean> chest = (Setting<Boolean>)this.register(new Setting("Chest", true));
    private final Setting<Boolean> dispenser = (Setting<Boolean>)this.register(new Setting("Dispenser", false));
    private final Setting<Boolean> shulker = (Setting<Boolean>)this.register(new Setting("Shulker", true));
    private final Setting<Boolean> echest = (Setting<Boolean>)this.register(new Setting("Ender Chest", true));
    private final Setting<Boolean> furnace = (Setting<Boolean>)this.register(new Setting("Furnace", false));
    private final Setting<Boolean> hopper = (Setting<Boolean>)this.register(new Setting("Hopper", false));
    private final Setting<Boolean> cart = (Setting<Boolean>)this.register(new Setting("Minecart", false));
    private final Setting<Boolean> frame = (Setting<Boolean>)this.register(new Setting("Item Frame", false));
    private final Setting<Boolean> box = (Setting<Boolean>)this.register(new Setting("Box", false));
    private final Setting<Integer> boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 125, 0, 255,  v -> this.box.getValue()));
    private final Setting<Boolean> outline  = (Setting<Boolean>)this.register(new Setting("Outline", true));
    private final Setting<Float> lineWidth  = (Setting<Float>)this.register(new Setting("LineWidth", 1.0f, 0.1f, 5.0f,  v -> this.outline.getValue()));


    public StorageEsp() {
        super("StorageESP", "Highlights Containers.", Module.Category.RENDER, false, false, false);
    }


    private final Setting<ColorSetting> chestColor = this.register(new Setting<>("colorImgOutline", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> shulkColor = this.register(new Setting<>("secondColorImgOutline", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> echestColor = this.register(new Setting<>("thirdColorImgOutline", new ColorSetting(0x8800FF00)));

    private Setting<mode> Mode = register(new Setting("Shulker Mode", mode.Rainbow));

    public enum mode {
        Custom, Rainbow, Astolfo;
    }

    public Setting <Integer> del = this.register ( new Setting <> ( "Rainbow delay", 1, 0, 2000, v -> Mode.getValue() == mode.Rainbow ));





    public void onRender3D(final Render3DEvent event) {
        final HashMap<BlockPos,  Integer> positions = new HashMap<BlockPos,  Integer>();
        for (final TileEntity tileEntity : StorageEsp.mc.world.loadedTileEntityList) {
            final BlockPos pos;
            if (((tileEntity instanceof TileEntityChest && this.chest.getValue()) || (tileEntity instanceof TileEntityDispenser && this.dispenser.getValue()) || (tileEntity instanceof TileEntityShulkerBox && this.shulker.getValue()) || (tileEntity instanceof TileEntityEnderChest && this.echest.getValue()) || (tileEntity instanceof TileEntityFurnace && this.furnace.getValue()) || (tileEntity instanceof TileEntityHopper && this.hopper.getValue())) && StorageEsp.mc.player.getDistanceSq(pos = tileEntity.getPos()) <= MathUtil.square(this.range.getValue())) {
                final int color;
                if ((color = this.getTileEntityColor(tileEntity)) == -1) {
                    continue;
                }
                positions.put(pos,  color);
            }
        }
        for (final Entity entity : StorageEsp.mc.world.loadedEntityList) {
            final BlockPos pos;
            if (((entity instanceof EntityItemFrame && this.frame.getValue()) || (entity instanceof EntityMinecartChest && this.cart.getValue())) && StorageEsp.mc.player.getDistanceSq(pos = entity.getPosition()) <= MathUtil.square(this.range.getValue())) {
                final int color;
                if ((color = this.getEntityColor(entity)) == -1) {
                    continue;
                }
                positions.put(pos,  color);
            }
        }
        for (final Map.Entry<BlockPos,  Integer> entry : positions.entrySet()) {
            final BlockPos blockPos = entry.getKey();
            final int color = entry.getValue();
            RenderUtil.drawBoxESP(blockPos,  (new Color(color)),  false,  new Color(color),  this.lineWidth.getValue(),  this.outline.getValue(),  this.box.getValue(),  this.boxAlpha.getValue(),  false);
        }
    }

    private int getTileEntityColor(final TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            return chestColor.getValue().getColor();
        }
        if (tileEntity instanceof TileEntityEnderChest) {
            return echestColor.getValue().getColor();
        }



        if (tileEntity instanceof TileEntityShulkerBox) {
            if(Mode.getValue() == mode.Custom) {
                return shulkColor.getValue().getColor();
            }
            if(Mode.getValue() == mode.Rainbow) {
                return PaletteHelper.rainbow(del.getValue(),100,50).getRGB();
            }
            if(Mode.getValue() == mode.Astolfo) {
                return  PaletteHelper.astolfo(false, (int) mc.player.height).getRGB();
            }

        }




        if (tileEntity instanceof TileEntityFurnace) {
            return ColorUtil.toRGBA(255,  128,  0,  255);
        }
        if (tileEntity instanceof TileEntityHopper) {
            return ColorUtil.toRGBA(255,  128,  0,  255);
        }
        if (tileEntity instanceof TileEntityDispenser) {
            return ColorUtil.toRGBA(255,  128,  0,  255);
        }
        return -1;
    }

    private int getEntityColor(final Entity entity) {
        if (entity instanceof EntityMinecartChest) {
            return ColorUtil.toRGBA(255,  128,  0,  255);
        }
        if (entity instanceof EntityItemFrame && ((EntityItemFrame)entity).getDisplayedItem().getItem() instanceof ItemShulkerBox) {
            return ColorUtil.toRGBA(255,  128,  0,  255);
        }
        if (entity instanceof EntityItemFrame && !(((EntityItemFrame)entity).getDisplayedItem().getItem() instanceof ItemShulkerBox)) {
            return ColorUtil.toRGBA(255,  128,  0,  255);
        }
        return -1;
    }
}
