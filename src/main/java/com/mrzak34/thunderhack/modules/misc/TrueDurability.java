package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.SpecialTagCompound;
import io.netty.buffer.ByteBuf;
import com.mrzak34.thunderhack.util.ffp.PacketListener;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;


import java.io.IOException;

import java.util.List;
public class TrueDurability extends Module implements PacketListener  {
    public TrueDurability() {
        super("TrueDurability", "реальная прочность-нелегальных предметов", Module.Category.PLAYER);
    }

    @Override
    public void onUpdate(){
        if(mc.world!= null && mc.player!= null){
            Thunderhack.networkHandler.registerListener(EnumPacketDirection.CLIENTBOUND, this, 20, 22, 63);
        }
    }

    @Override
    public void onEnable() {
        Thunderhack.networkHandler.registerListener(EnumPacketDirection.CLIENTBOUND, this, 20, 22, 63);;
    }

    @Override
    public void onDisable() {
        Thunderhack.networkHandler.unregisterListener(EnumPacketDirection.CLIENTBOUND, this, 20, 22, 63);
    }

    @SubscribeEvent
    public void itemToolTip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int max = stack.getMaxDamage();

        if(stack.isEmpty() || max <= 0) return;
        if(stack.hasTagCompound()) {
            assert stack.getTagCompound() != null;
            if (stack.getTagCompound().getBoolean("Unbreakable")) return;
        }

        List<String> toolTip = event.getToolTip();

        int damage;
        NBTTagCompound tag = stack.getTagCompound();
        if(tag instanceof SpecialTagCompound) {
            damage = ((SpecialTagCompound)tag).getTrueDamage();
        } else {
            damage = stack.getItemDamage();
        }
        long count = (long)max - (long)damage;

        TextFormatting color;
        if(damage < 0) color = TextFormatting.DARK_PURPLE;
        else if(damage > max) color = TextFormatting.DARK_RED;
        else color = TextFormatting.BLUE;

        toolTip.add("");
        toolTip.add(color + "Durability: " + count + " [Max: " + Long.toString(max) + "]" + TextFormatting.RESET);
    }


    public Packet<?> packetReceived(EnumPacketDirection direction, int id, Packet<?> packet, ByteBuf in) {
        switch(id) {
            case 20: // SPacketWindowItems
            {
                SPacketWindowItems packet_window = (SPacketWindowItems) packet;
                PacketBuffer buf = new PacketBuffer(in);
                buf.readerIndex(buf.readerIndex() + 4);
                for(ItemStack i : packet_window.getItemStacks()) {
                    if(buf.readShort() >= 0) {
                        buf.readerIndex(buf.readerIndex() + 1);
                        short true_damage = buf.readShort();
                        try {
                            if(true_damage < 0) {
                                i.setTagCompound(new SpecialTagCompound(buf.readCompoundTag(), (int)true_damage));
                            } else buf.readCompoundTag();
                        } catch (IOException e) {
                            break;
                        }
                    }
                }
            }
            break;
            case 22: // SPacketSetSlot
            {
                SPacketSetSlot packet_slot = (SPacketSetSlot) packet;
                PacketBuffer buf = new PacketBuffer(in);
                buf.readerIndex(buf.readerIndex() + 4);
                if(buf.readShort() >= 0) {
                    buf.readerIndex(buf.readerIndex() + 1);
                    short real_damage = buf.readShort();
                    if(real_damage < 0) {
                        ItemStack stack = packet_slot.getStack();
                        stack.setTagCompound(new SpecialTagCompound(stack.getTagCompound(), (int)real_damage));
                    }
                }
            }
            break;
            case 63: // SPacketEntityEquipment
            {
                SPacketEntityEquipment equipment = (SPacketEntityEquipment) packet;
                PacketBuffer buf = new PacketBuffer(in);
                buf.readerIndex(buf.readerIndex() + 3 + (int)Math.floor(Math.log((double)equipment.getEntityID()) / Math.log(128d)));
                if(buf.readShort() >= 0) {
                    buf.readerIndex(buf.readerIndex() + 1);
                    short real_damage = buf.readShort();
                    if(real_damage < 0) {
                        ItemStack stack = equipment.getItemStack();
                        stack.setTagCompound(new SpecialTagCompound(stack.getTagCompound(), (int)real_damage));
                    }
                }
            }
            break;
        }
        return packet;
    }
}
