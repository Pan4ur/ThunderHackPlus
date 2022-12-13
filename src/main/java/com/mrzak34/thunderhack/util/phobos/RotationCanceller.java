package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.ICPacketPlayer;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.modules.movement.PacketFly;
import com.mrzak34.thunderhack.modules.render.Rotation;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.modules.combat.Burrow.rotation;
import static com.mrzak34.thunderhack.util.ItemUtil.mc;

public class RotationCanceller
{


    private final Timer timer = new Timer();
    private final Setting<Integer> maxCancel;
    private final AutoCrystal module;

    private volatile CPacketPlayer last;

    public RotationCanceller(AutoCrystal module, Setting<Integer> maxCancel)
    {
        this.module = module;
        this.maxCancel = maxCancel;
    }

    /**
     * Sends the last cancelled packet if
     * the timer passed the MaxCancel time.
     */
    public void onGameLoop()
    {
        if (last != null && timer.passedMs(maxCancel.getValue()))
        {
            sendLast();
        }
    }

    public synchronized void onPacketNigger(PacketEvent.Send event)
    {
        if(event.getPacket() instanceof CPacketPlayer) {
            if (event.isCanceled() || Thunderhack.moduleManager.getModuleByClass(PacketFly.class).isEnabled()) {
                return;
            }

            reset(); // Send last Packet if it hasn't been yet
            if (Thunderhack.rotationManager.isBlocking()) {
                return;
            }

            event.setCanceled(true);
            last = event.getPacket();
            timer.reset();
        }
    }


    /**
     * Sets the Rotations of the last Packet and sends it,
     * if it has been cancelled.
     *
     * @param function the RotationFunction setting the packet.
     * @return <tt>true</tt> if Rotations have been set.
     */
    public synchronized boolean setRotations(RotationFunction function)
    {
        if (last == null)
        {
            return false;
        }

        double x = last.getX(Thunderhack.positionManager.getX());
        double y = last.getX(Thunderhack.positionManager.getY());
        double z = last.getX(Thunderhack.positionManager.getZ());
        float yaw   = Thunderhack.rotationManager.getServerYaw();
        float pitch = Thunderhack.rotationManager.getServerPitch();
        boolean onGround = last.isOnGround();

        ICPacketPlayer accessor = (ICPacketPlayer) last;
        float[] r = function.apply(x, y, z, yaw, pitch);
        if (r[0] - yaw == 0.0 || r[1] - pitch == 0.0)
        {
            if (!accessor.isRotating()
                    && !accessor.isMoving()
                    && onGround == Thunderhack.positionManager.isOnGround())
            {
                last = null;
                return true;
            }

            sendLast();
            return true;
        }

        if (accessor.isRotating())
        {
            accessor.setYaw(r[0]);
            accessor.setPitch(r[1]);
            sendLast();
        }
        else if (accessor.isMoving())
        {
            last = positionRotation(x, y, z, r[0], r[1], onGround);
            sendLast();
        }
        else
        {
            last = rotation(r[0], r[1], onGround);
            sendLast();
        }

        return true;
    }

    public static CPacketPlayer positionRotation(double x,
                                                 double y,
                                                 double z,
                                                 float yaw,
                                                 float pitch,
                                                 boolean onGround)
    {
        return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
    }

    /**
     * Sends the last Packet if it has been cancelled.
     */
    public void reset()
    {
        if (last != null && mc.player != null)
        {
            sendLast();
        }
    }

    /**
     * Drops the current packet. It won't be send.
     */
    public synchronized void drop()
    {
        last = null;
    }

    private synchronized void sendLast()
    {
        CPacketPlayer packet = last;
        if (packet != null && mc.player != null)
        {
            mc.player.connection.sendPacket(packet);
            module.runPost();
        }

        last = null;
    }


    public void onPacketNigger9(CPacketPlayer.Rotation rotation) {

            if (Thunderhack.moduleManager.getModuleByClass(PacketFly.class).isEnabled()) {
                return;
            }

            reset(); // Send last Packet if it hasn't been yet
            if (Thunderhack.rotationManager.isBlocking()) {
                return;
            }


            last = rotation;
            timer.reset();

    }
}