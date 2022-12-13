package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.DamageBlockEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.MathUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class C4Trap extends Module {

    public C4Trap() {
        super("C4Trap", "C4Trap", Category.FUNNYGAME, true, true, false);
    }





    @SubscribeEvent
    public void onDamageBlock(DamageBlockEvent e){
        ItemStack is = mc.player.getHeldItemMainhand();
        addLoreLine(is,"§e§l[1]§r§e WORLD᎐ " + (e.getBlockPos().getX()) +", " + (e.getBlockPos().getY()) +", " + (e.getBlockPos().getZ()));
        e.setCanceled(true);
    }
//§e§l[1]§r§e WORLD᎐ -3621, 72, -1696
    public static void addLoreLine( ItemStack stack, String line )
    {
        NBTTagList lore = getLoreTagList( stack );
        Command.sendMessage(lore.toString());

        lore.appendTag( new NBTTagString( line ) );
    }

    public static NBTTagList getLoreTagList( ItemStack stack )
    {

        NBTTagCompound displayTag = getDisplayTag( stack );

        if ( !hasLore( stack ) )
        {
            displayTag.setTag( "Lore", new NBTTagList() );
        }

        return displayTag.getTagList( "Lore", Constants.NBT.TAG_STRING );
    }
    public static boolean hasLore( ItemStack stack )
    {
        return hasDisplayTag( stack ) && getDisplayTag( stack ).hasKey( "Lore", Constants.NBT.TAG_LIST );
    }
    public static boolean hasDisplayTag( ItemStack stack )
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey( "display", Constants.NBT.TAG_COMPOUND );
    }

    public static NBTTagCompound getDisplayTag( ItemStack stack )
    {
        return stack.getOrCreateSubCompound( "display" );
    }

}
