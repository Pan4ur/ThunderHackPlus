package com.mrzak34.thunderhack.modules.client;

import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.EventSchematicaPlaceBlockFull;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.BlockInteractionHelper;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PrinterBypass extends Module {

    public PrinterBypass() {
        super("PrinterBypass", "PrinterBypass", Category.CLIENT, true, false, false);
    }



    public Setting<Float> Delay = register(new Setting("Delay", 0.20f, 0.1f, 1.0f));
    public Setting<Float> reach = register(new Setting("reach", 3.80f, 1.1f, 6.0f));



    private BlockPos BlockToPlace = null;
    private Item NeededItem = null;
    private Timer timer = new Timer();


    public static ItemStack stacktoswap = null;



    @Override
    public void onEnable()
    {
        BlockToPlace = null;
        SchematicPrinter.INSTANCE.setPrinting(true);

    }

    @Override
    public void onDisable()
    {
        BlockToPlace = null;
        SchematicPrinter.INSTANCE.setPrinting(false);

    }


    @SubscribeEvent
    public void onEventSchematicaPlaceBlockFull(EventSchematicaPlaceBlockFull event){


        boolean l_Result = BlockToPlace == null;
        if(l_Result)
            stacktoswap = event.ItemStack;
        if (l_Result)
            BlockToPlace = event.Pos;
        event.Result = l_Result;
    }

    @SubscribeEvent
    public void onEventPreMotion(EventPreMotion event) {
        if (BlockToPlace == null)
            return;

        if (!timer.passedMs((long) (Delay.getValue() * 1000)))
            return;

        timer.reset();

        float[] rotations = BlockInteractionHelper.getLegitRotations(new Vec3d(BlockToPlace.getX(), BlockToPlace.getY(), BlockToPlace.getZ()));


        mc.player.rotationPitch = rotations[1];
        mc.player.rotationYaw = rotations[0];

        mc.player.rotationYawHead = rotations[0];


        BlockInteractionHelper.PlaceResult l_Place = BlockInteractionHelper.place(BlockToPlace, 5.0f, false, false,stacktoswap);


        BlockToPlace = null;
    }







}
