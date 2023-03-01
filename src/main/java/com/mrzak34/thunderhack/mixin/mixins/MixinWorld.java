package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.modules.render.NoRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin(value = {World.class})
public class MixinWorld {


    @Shadow
    @Final
    public boolean isRemote;

@Inject(
        method = "updateEntities",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
                ordinal = 2
        )
)
public void updateEntitiesHook(CallbackInfo ci)
{
    if (isRemote)
    {
        UpdateEntitiesEvent event = new UpdateEntitiesEvent();
        MinecraftForge.EVENT_BUS.post(event);
    }
}

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void updateLightmapHook(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NoRender noRender = Thunderhack.moduleManager.getModuleByClass(NoRender.class);
        if (lightType == EnumSkyBlock.SKY && noRender.isEnabled() && noRender.SkyLight.getValue() && !mc.isSingleplayer()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }


    @Inject(method = "onEntityAdded", at = @At("HEAD"), cancellable = true)
    public void onEntityAdded(Entity entity, CallbackInfo callbackInfo) {
        EntityAddedEvent event = new EntityAddedEvent(entity);

        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"), cancellable = true)
    public void onEntityRemoved(Entity entity, CallbackInfo callbackInfo) {
        EntityRemovedEvent event = new EntityRemovedEvent(entity);

        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method = { "handleMaterialAcceleration" },  at = @At(value = "INVOKE",  target = "Lnet/minecraft/entity/Entity;isPushedByWater()Z"))
    public boolean isPushedbyWaterHook(final Entity entity) {
        final PushEvent event = new PushEvent(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return entity.isPushedByWater() && !event.isCanceled();
    }

    @Inject(method = { "spawnEntity" }, at = { @At("HEAD") }, cancellable = true)
    public void spawnEntityFireWork(final Entity entityIn, final CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.getInstance().fireworks.getValue() && NoRender.getInstance().isEnabled() && entityIn instanceof EntityFireworkRocket) {
            cir.cancel();
        }
    }


    double nigga1,nigga2,nigga3;



    @Inject(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V", shift = At.Shift.AFTER))
    public void updateEntityWithOptionalForceHookPost(Entity entityIn, boolean forceUpdate, CallbackInfo ci) {
        if (nigga1 != entityIn.posX || nigga2 != entityIn.posY || nigga3 != entityIn.posZ) {
            EventEntityMove event = new EventEntityMove(entityIn, new Vec3d(nigga1, nigga2, nigga3));
            MinecraftForge.EVENT_BUS.post(event);
        }
    }


    @Inject(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
    public void updateEntityWithOptionalForceHookPre(Entity entityIn, boolean forceUpdate, CallbackInfo ci) {
        nigga1 = entityIn.posX;
        nigga2 = entityIn.posY;
        nigga3 = entityIn.posZ;
    }
}

