package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.EventMove;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.PushEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MathUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.mrzak34.thunderhack.util.MovementUtil.strafe;


public class Flight extends Module {


    public Setting<Boolean> better;
    public Setting<Boolean> phase;
    public Setting<Float> speed = this.register(new Setting("Speed", 0.1f, 0.0f, 10.0f));



    public Flight() {
        super("Flight",  "Makes you fly.",  Module.Category.MOVEMENT,  true,  false,  false);
    }







    @SubscribeEvent
    public void onUpdateWalkingPlayer(final EventPreMotion event) {
        Flight.mc.player.setVelocity(0.0, 0.0, 0.0);
        Flight.mc.player.jumpMovementFactor = this.speed.getValue();
        final double[] dir = MathUtil.directionSpeed(this.speed.getValue());
        if (Flight.mc.player.movementInput.moveStrafe != 0.0f || Flight.mc.player.movementInput.moveForward != 0.0f) {
            Flight.mc.player.motionX = dir[0];
            Flight.mc.player.motionZ = dir[1];
        } else {
            Flight.mc.player.motionX = 0.0;
            Flight.mc.player.motionZ = 0.0;
        }
        if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
            final EntityPlayerSP player3 = Flight.mc.player;
            double motionY;
            final EntityPlayerSP player4 = Flight.mc.player;
            motionY = (player4.motionY += this.speed.getValue());
            player3.motionY = motionY;
        }
        if (Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
            final EntityPlayerSP player5 = Flight.mc.player;
            player5.motionY -= this.speed.getValue();
        }
    }
}
