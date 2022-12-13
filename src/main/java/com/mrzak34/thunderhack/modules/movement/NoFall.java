package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "рубербендит если ты-упал", Category.MOVEMENT, false, false, false);
    }


    public Setting<rotmod> mod = register(new Setting("Mode", rotmod.Matrix));

    public enum rotmod {
        Matrix, Matrix2, Rubberband, Default;
    }
    public boolean b;


    @Override
    public void onUpdate(){
        if (mc.player.fallDistance > 3 && !mc.player.isSneaking() && mod.getValue() == rotmod.Rubberband) {
            mc.player.motionY -= 0.1;
            mc.player.onGround = true;
            mc.player.capabilities.disableDamage = true;
        }
    }


    @SubscribeEvent
    public void onClientTickEvent(TickEvent.ClientTickEvent clientTickEvent) {

        if (mod.getValue() == rotmod.Default) {
            if ((double)mc.player.fallDistance > 2.5) {
                mc.player.connection.sendPacket((Packet)new CPacketPlayer(true));
            }
        } else if (mod.getValue() == rotmod.Matrix2) {
            if ((double)mc.player.fallDistance > 2.5 && mc.player.fallDistance < 10.0f) {
                a(true);
            } else if (mc.player.onGround) {
                l();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent playerTickEvent) {
        if (mod.getValue() != rotmod.Matrix) {
            return;
        }
        if (!mc.player.onGround && mc.player.fallDistance > 2.0f && b(mc.player.getPosition().down()) == Blocks.AIR) {
            this.b = true;
        }
        if (this.b) {
            mc.player.onGround = false;
        }
    }

    private static boolean l;
    public static boolean c;

    public static void l() {
        c = false;
        l = mc.player.onGround;
    }

    public static void a(boolean bl) {
        l = bl;
        c = true;
    }

    public static IBlockState a(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos);
    }

    public static Block b(BlockPos blockPos) {
        return a(blockPos).getBlock();
    }
}
