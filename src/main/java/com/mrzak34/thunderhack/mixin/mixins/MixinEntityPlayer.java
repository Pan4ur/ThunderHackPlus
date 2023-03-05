package com.mrzak34.thunderhack.mixin.mixins;

import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventPlayerTravel;
import com.mrzak34.thunderhack.mixin.ducks.IEntityPlayer;
import com.mrzak34.thunderhack.modules.movement.KeepSprint;
import com.mrzak34.thunderhack.modules.movement.LegitStrafe;
import com.mrzak34.thunderhack.util.Util;
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


@Mixin(value={EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase implements IEntityPlayer {
    public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn);
    }


    @Unique
    private MotionTracker motionTrackerT;
    @Unique
    private MotionTracker breakMotionTrackerT;
    @Unique
    private MotionTracker blockMotionTrackerT;

    @Override
    public void setMotionTrackerT(MotionTracker motionTracker) {
        this.motionTrackerT = motionTracker;
    }

    @Override
    public MotionTracker getMotionTrackerT() {
        return motionTrackerT;
    }

    @Override
    public MotionTracker getBreakMotionTrackerT() {
        return breakMotionTrackerT;
    }

    @Override
    public void setBreakMotionTrackerT(MotionTracker breakMotionTracker) {
        this.breakMotionTrackerT = breakMotionTracker;
    }

    @Override
    public MotionTracker getBlockMotionTrackerT() {
        return blockMotionTrackerT;
    }

    @Override
    public void setBlockMotionTrackerT(MotionTracker blockMotionTracker) {
        this.blockMotionTrackerT = blockMotionTracker;
    }


    @Inject(method = { "travel" }, at = { @At("HEAD") }, cancellable = true)
    public void travel(final float strafe, final float vertical, final float forward, final CallbackInfo info) {
        EntityPlayer us = null;
        if(Util.mc.player != null) {
            us = (EntityPlayer) Util.mc.player;
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

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void onAttackTargetEntityWithCurrentItem(CallbackInfo callbackInfo) {

        KeepSprint ks = Thunderhack.moduleManager.getModuleByClass(KeepSprint.class);
        if (ks.isEnabled()) {
            final float multiplier = 0.6f + 0.4f * ks.motion.getValue();
            this.motionX = this.motionX / 0.6 * multiplier;
            this.motionZ = this.motionZ / 0.6 * multiplier;
            if (ks.sprint.getValue()) {
                this.setSprinting(true);
            }
        }
    }

}
