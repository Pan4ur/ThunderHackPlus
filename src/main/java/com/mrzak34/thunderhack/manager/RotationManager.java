package com.mrzak34.thunderhack.manager;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayerSP;
import com.mrzak34.thunderhack.modules.Feature;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class RotationManager extends Feature
{
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    private boolean blocking;
    private volatile float last_yaw;
    private volatile float last_pitch;
    private float renderYaw;
    private float renderPitch;
    private float renderYawOffset;
    private float prevYaw;
    private float prevPitch;
    private float prevRenderYawOffset;
    private float prevRotationYawHead;
    private float rotationYawHead;
    private int ticksExisted;


    @SubscribeEvent
    public void onEntitySync(EventPreMotion e){
        set(e.getYaw(), e.getPitch());
    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event){
        if(event.getPacket() instanceof CPacketPlayer){
            readCPacket(event.getPacket());
        }
        if(event.getPacket() instanceof CPacketPlayer.Position){
            readCPacket(event.getPacket());
        }
        if(event.getPacket() instanceof CPacketPlayer.Rotation){
            readCPacket(event.getPacket());
        }
        if(event.getPacket() instanceof CPacketPlayer.PositionRotation){
            readCPacket(event.getPacket());
        }
    }
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if(fullNullCheck()) return;

        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = e.getPacket();
            float yaw = packet.getYaw();
            float pitch = packet.getPitch();

            if (packet.getFlags()
                    .contains(SPacketPlayerPosLook.EnumFlags.X_ROT))
            {
                yaw += mc.player.rotationYaw;
            }

            if (packet.getFlags()
                    .contains(SPacketPlayerPosLook.EnumFlags.Y_ROT))
            {
                pitch += mc.player.rotationPitch;
            }

            if (mc.player != null)
            {
                setServerRotations(yaw, pitch);
            }
        }
    }




    /**
     * @return the last yaw reported to/by the server.
     */
    public float getServerYaw()
    {
        return last_yaw;
    }

    /**
     * @return the last pitch reported to/by the server.
     */
    public float getServerPitch()
    {
        return last_pitch;
    }

    /**
     * Makes {@link RotationManager#isBlocking()} return the given
     * argument, that won't prevent other modules from
     * spoofing rotations but they can check it. This
     * can be marked as true by modules which think they are
     * important like Surround, to prevent other modules
     * from rotating.
     *
     * Remember to set this to false after
     * the Rotations have been sent.
     *
     * @param blocking blocks rotation spoofing
     */
    public void setBlocking(boolean blocking)
    {
        this.blocking = blocking;
    }

    /**
     * Indicates that a module is currently
     * spoofing rotations and they shouldn't
     * be spoofed by others.
     *
     * @return <tt>true</tt> if blocking.
     */
    public boolean isBlocking()
    {
        return blocking;
    }

    public void setServerRotations(float yaw, float pitch)
    {
        last_yaw   = yaw;
        last_pitch = pitch;
    }

    /**
     * Reads yaw and pitch from a packet.
     *
     * @param packetIn the packet to read.
     */
    public void readCPacket(CPacketPlayer packetIn)
    {

        ((IEntityPlayerSP) mc.player).setLastReportedYaw(packetIn.getYaw(((IEntityPlayerSP) mc.player).getLastReportedYaw()));
        ((IEntityPlayerSP) mc.player).setLastReportedPitch(packetIn.getPitch(((IEntityPlayerSP) mc.player).getLastReportedPitch()));

        setServerRotations(packetIn.getYaw(last_yaw), packetIn.getPitch(last_pitch));
        Thunderhack.positionManager.setOnGround(packetIn.isOnGround());
    }

    private void set(float yaw, float pitch)
    {
        if (mc.player.ticksExisted == ticksExisted)
        {
            return;
        }

        ticksExisted = mc.player.ticksExisted;
        prevYaw      = renderYaw;
        prevPitch    = renderPitch;

        prevRenderYawOffset = renderYawOffset;
        renderYawOffset     = getRenderYawOffset(yaw, prevRenderYawOffset);

        prevRotationYawHead = rotationYawHead;
        rotationYawHead     = yaw;

        renderYaw   = yaw;
        renderPitch = pitch;
    }

    public float getRenderYaw()
    {
        return renderYaw;
    }

    public float getRenderPitch()
    {
        return renderPitch;
    }

    public float getRotationYawHead()
    {
        return rotationYawHead;
    }

    public float getRenderYawOffset()
    {
        return renderYawOffset;
    }

    public float getPrevYaw()
    {
        return prevYaw;
    }

    public float getPrevPitch()
    {
        return prevPitch;
    }

    public float getPrevRotationYawHead()
    {
        return prevRotationYawHead;
    }

    public float getPrevRenderYawOffset()
    {
        return prevRenderYawOffset;
    }

    private float getRenderYawOffset(float yaw, float offsetIn)
    {
        float result = offsetIn;
        float offset;

        double xDif = mc.player.posX - mc.player.prevPosX;
        double zDif = mc.player.posZ - mc.player.prevPosZ;

        if (xDif * xDif + zDif * zDif > 0.0025000002f)
        {
            offset = (float) MathHelper.atan2(zDif, xDif) * 57.295776f - 90.0f;
            float wrap = MathHelper.abs(MathHelper.wrapDegrees(yaw) - offset);
            if (95.0F < wrap && wrap < 265.0F)
            {
                result = offset - 180.0F;
            }
            else
            {
                result = offset;
            }
        }

        if (mc.player.swingProgress > 0.0F)
        {
            result = yaw;
        }

        result = offsetIn + MathHelper.wrapDegrees(result - offsetIn) * 0.3f;
        offset = MathHelper.wrapDegrees(yaw - result);

        if (offset < -75.0f)
        {
            offset = -75.0f;
        }
        else if (offset >= 75.0f)
        {
            offset = 75.0f;
        }

        result = yaw - offset;
        if (offset * offset > 2500.0f)
        {
            result += offset * 0.2f;
        }

        return result;
    }

}