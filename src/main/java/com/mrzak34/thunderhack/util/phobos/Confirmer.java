package com.mrzak34.thunderhack.util.phobos;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Confirmer extends Feature
{
    private final Timer placeTimer = new Timer();
    private final Timer breakTimer = new Timer();

    private BlockPos current;
    private AxisAlignedBB bb;
    private boolean placeConfirmed;
    private boolean breakConfirmed;
    private boolean newVer;
    private boolean valid;
    private int placeTime;

    public Confirmer()
    {

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof  SPacketSpawnObject){
            SPacketSpawnObject p = e.getPacket();
            if (p.getType() == 51)
            {
                confirmPlace(p.getX(), p.getY(), p.getZ());
            }
        }
        if(e.getPacket() instanceof  SPacketSoundEffect){
            SPacketSoundEffect p = e.getPacket();
            if (p.getCategory() == SoundCategory.BLOCKS
                    && p.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
            {
                confirmBreak(p.getX(), p.getY(), p.getZ());
            }
        }
    }

    public void setPos(BlockPos pos, boolean newVer, int placeTime)
    {
        this.newVer = newVer;

        if (pos == null)
        {
            this.current = null;
            this.valid = false;
        }
        else
        {
            BlockPos crystalPos = new BlockPos(pos.getX() + 0.5f,
                    pos.getY() + 1,
                    pos.getZ() + 0.5f);
            this.current = crystalPos;
            this.bb = createBB(crystalPos, newVer);
            this.valid = true;
            this.placeConfirmed = false;
            this.breakConfirmed = false;
            this.placeTime = placeTime < 50 ? 0 : placeTime;
            this.placeTimer.reset();
        }
    }

    public void confirmPlace(double x, double y, double z)
    {
        if ( valid && !placeConfirmed)
        {
            BlockPos p = new BlockPos(x, y, z);
            if (p.equals(current))
            {
                placeConfirmed = true;
                breakTimer.reset();
            }
            else if (placeTimer.passedMs(placeTime))
            {
                AxisAlignedBB currentBB = bb;
                if (currentBB != null
                        && currentBB.intersects(createBB(x, y, z, newVer)))
                {
                    valid = false;
                }
            }
        }
    }

    public void confirmBreak(double x, double y, double z)
    {
        if (valid && !breakConfirmed && placeConfirmed)
        {
            BlockPos current = this.current;
            if (current != null && current.distanceSq(x, y, z) < 144)
            {
                if (current.equals(new BlockPos(x, y, z)))
                {
                    breakConfirmed = true;
                }
                else
                {
                    valid = false;
                }
            }
        }
    }

    public boolean isValid()
    {
        return valid;
    }

    public boolean isPlaceConfirmed(int placeConfirm)
    {
        if (!placeConfirmed && placeTimer.passedMs(placeConfirm))
        {
            valid = false;
            return false;
        }

        return placeConfirmed && valid;
    }

    public boolean isBreakConfirmed(int breakConfirm)
    {
        if (placeConfirmed
                && !breakConfirmed
                && breakTimer.passedMs(breakConfirm))
        {
            valid = false;
            return false;
        }

        return breakConfirmed && valid;
    }

    private AxisAlignedBB createBB(BlockPos crystalPos, boolean newVer)
    {
        return createBB(crystalPos.getX() + 0.5f,
                crystalPos.getY(),
                crystalPos.getZ() + 0.5f,
                newVer);
    }

    private AxisAlignedBB createBB(double x, double y, double z, boolean newVer)
    {
        return new AxisAlignedBB(x - 1,
                y,
                z - 1,
                x + 1,
                y + (newVer ? 1 : 2),
                z + 1);
    }

    public static Confirmer createAndSubscribe()
    {
        Confirmer confirmer = new Confirmer();
        MinecraftForge.EVENT_BUS.register(confirmer);
        return confirmer;
    }

}