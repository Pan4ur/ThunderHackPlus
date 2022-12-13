package com.mrzak34.thunderhack.mixin.mixins;

import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.event.events.EventPlayerTravel;
import com.mrzak34.thunderhack.mixin.ducks.IEntityPlayer;
import com.mrzak34.thunderhack.util.phobos.MotionTracker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.ItemUtil.mc;

@Mixin(value={EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase implements IEntityPlayer {
    public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn);
    }

    @Inject(method={"getCooldownPeriod"}, at={@At(value="HEAD")}, cancellable=true)
    private void getCooldownPeriodHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {

    }


    @Unique
    private MotionTracker motionTracker;
    @Unique
    private MotionTracker breakMotionTracker;
    @Unique
    private MotionTracker blockMotionTracker;
    @Unique
    private int ticksWithoutMotionUpdate;

    @Override
    public void setMotionTracker(MotionTracker motionTracker) {
        this.motionTracker = motionTracker;
    }

    @Override
    public MotionTracker getMotionTracker() {
        return motionTracker;
    }

    @Override
    public MotionTracker getBreakMotionTracker() {
        return breakMotionTracker;
    }

    @Override
    public void setBreakMotionTracker(MotionTracker breakMotionTracker) {
        this.breakMotionTracker = breakMotionTracker;
    }

    @Override
    public MotionTracker getBlockMotionTracker() {
        return blockMotionTracker;
    }

    @Override
    public void setBlockMotionTracker(MotionTracker blockMotionTracker) {
        this.blockMotionTracker = blockMotionTracker;
    }

    @Override
    public int getTicksWithoutMotionUpdate() {
        return ticksWithoutMotionUpdate;
    }

    @Override
    public void setTicksWithoutMotionUpdate(int ticksWithoutMotionUpdate) {
        this.ticksWithoutMotionUpdate = ticksWithoutMotionUpdate;
    }

    @Inject(method = { "travel" }, at = { @At("HEAD") }, cancellable = true)
    public void travel(final float strafe, final float vertical, final float forward, final CallbackInfo info) {
        EntityPlayer us = null;
        if(mc.player != null) {
            us = (EntityPlayer) mc.player;
        }
        if (us == null) {
            return;
        }
        final EventPlayerTravel event = new EventPlayerTravel(strafe, vertical, forward);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            info.cancel();
        }
    }

}
