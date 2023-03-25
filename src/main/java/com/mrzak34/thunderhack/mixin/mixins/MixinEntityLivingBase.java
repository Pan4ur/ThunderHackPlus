package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.modules.movement.NoJumpDelay;
import com.mrzak34.thunderhack.modules.render.Animations;
import com.mrzak34.thunderhack.util.phobos.IEntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin(value = {EntityLivingBase.class})
public abstract class MixinEntityLivingBase
        extends Entity implements IEntityLivingBase {


    @Shadow
    public float moveStrafing;
    @Shadow
    public float moveForward;
    @Shadow
    public int jumpTicks;
    protected float lowestDura = Float.MAX_VALUE;
    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Override
    public void setLowestDura(float lowest) {
        this.lowestDura = lowest;
    }

    @Override
    public float getLowestDurability() {
        return lowestDura;
    }

    @Override
    @Accessor(value = "ticksSinceLastSwing")
    public abstract int getTicksSinceLastSwing();

    @Override
    @Accessor(value = "ticksSinceLastSwing")
    public abstract void setTicksSinceLastSwing(int ticks);

    @Override
    @Accessor(value = "activeItemStackUseCount")
    public abstract int getActiveItemStackUseCount();

    @Override
    @Accessor(value = "activeItemStackUseCount")
    public abstract void setActiveItemStackUseCount(int count);

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void onTravelPre(float strafe, float vertical, float forward, CallbackInfo ci) {
        ElytraEvent event = new ElytraEvent((EntityLivingBase) (Object) this);
        MinecraftForge.EVENT_BUS.post(event);
        if(mc.player != null && (EntityLivingBase) (Object) this == mc.player) {
            EventMoveDirection event2 = new EventMoveDirection(false);
            MinecraftForge.EVENT_BUS.post(event2);
        }
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At("RETURN"), cancellable = true)
    public void onTravelPost(float strafe, float vertical, float forward, CallbackInfo ci) {
        if(mc.player != null && (EntityLivingBase) (Object) this == mc.player) {
            EventMoveDirection event = new EventMoveDirection(true);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }

    @Inject(method = {"handleJumpWater"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void handleJumpWater(CallbackInfo ci) {
        HandleLiquidJumpEvent event = new HandleLiquidJumpEvent();
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"handleJumpLava"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void handleJumpLava(CallbackInfo ci) {
        HandleLiquidJumpEvent event = new HandleLiquidJumpEvent();
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void jumphook(CallbackInfo ci) {
        EventJump event = new EventJump(this.rotationYaw);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"onItemUseFinish"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void finishHook(CallbackInfo ci) {
        FinishUseItemEvent event = new FinishUseItemEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"getArmSwingAnimationEnd"}, at = {@At("HEAD")}, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (Thunderhack.moduleManager.getModuleByClass(Animations.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(Animations.class).rMode.getValue() == Animations.rmode.Slow) {
            info.setReturnValue(Animations.getInstance().slowValue.getValue());
        }
    }

    @Inject(method = {"onLivingUpdate"}, at = {@At("HEAD")})
    public void onLivingUpdate(CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(NoJumpDelay.class).isEnabled()) {
            jumpTicks = 0;
        }
    }
}