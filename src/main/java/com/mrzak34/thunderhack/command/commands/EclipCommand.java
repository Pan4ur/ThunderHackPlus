package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.math.NumberUtils;

public class EclipCommand extends Command {
    public EclipCommand() {
        super("eclip");
    }


    public static int getSlotIDFromItem(Item item) {
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (s.getItem() != item) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(".eclip <value> , /up/down/bedrock");
            return;
        }

        int elytra;
        int i;
        float y = 0.0f;
        if (commands[0].equals("bedrock")) {
            y = -((float) this.mc.player.posY) - 3.0f;
        }
        if (commands[0].equals("down")) {
            for (i = 1; i < 255; ++i) {
                if (this.mc.world.getBlockState(new BlockPos(this.mc.player).add(0, -i, 0)) == Blocks.AIR.getDefaultState()) {
                    y = -i - 1;
                    break;
                }
                if (this.mc.world.getBlockState(new BlockPos(this.mc.player).add(0, -i, 0)) != Blocks.BEDROCK.getDefaultState())
                    continue;
                Command.sendMessage(ChatFormatting.RED + " можно телепортироваться только под бедрок");
                Command.sendMessage(ChatFormatting.RED + "eclip bedrock");
                return;
            }
        }
        if (commands[0].equals("up")) {
            for (i = 4; i < 255; ++i) {
                if (this.mc.world.getBlockState(new BlockPos(this.mc.player).add(0, i, 0)) != Blocks.AIR.getDefaultState())
                    continue;
                y = i + 1;
                break;
            }
        }
        if (y == 0.0f) {
            if (NumberUtils.isNumber(commands[0])) {
                y = Float.parseFloat(commands[0]);
            } else {
                Command.sendMessage(ChatFormatting.RED + commands[0] + ChatFormatting.GRAY + "не являестя числом");
                return;
            }
        }
        if ((elytra = getSlotIDFromItem(Items.ELYTRA)) == -1) {
            Command.sendMessage(ChatFormatting.RED + "вам нужны элитры в инвентаре");
            return;
        }
        if (elytra != -2) {
            this.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, this.mc.player);
            this.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, this.mc.player);
        }
        this.mc.getConnection().sendPacket(new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ, false));
        this.mc.getConnection().sendPacket(new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ, false));
        this.mc.getConnection().sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        this.mc.getConnection().sendPacket(new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + (double) y, this.mc.player.posZ, false));
        this.mc.getConnection().sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        if (elytra != -2) {
            this.mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, this.mc.player);
            this.mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, this.mc.player);
        }
        this.mc.player.setPosition(this.mc.player.posX, this.mc.player.posY + (double) y, this.mc.player.posZ);
    }
}
