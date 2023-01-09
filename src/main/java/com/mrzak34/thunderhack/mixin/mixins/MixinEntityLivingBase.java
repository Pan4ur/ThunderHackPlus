package com.mrzak34.thunderhack.mixin.mixins;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.util.phobos.IEntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.entity.EntityLivingBase.SWIM_SPEED;

@Mixin(value={EntityLivingBase.class})
public abstract class MixinEntityLivingBase
        extends Entity implements  IEntityLivingBase
{


    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }



    @Shadow
    public float moveStrafing;
    @Shadow
    public float moveForward;

    protected float lowestDura = Float.MAX_VALUE;

    @Override
    @Invoker(value = "getArmSwingAnimationEnd")
    public abstract int armSwingAnimationEnd();


    @Override
    public void setLowestDura(float lowest)
    {
        this.lowestDura = lowest;
    }

    @Override
    public float getLowestDurability()
    {
        return lowestDura;
    }



    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void onTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        ElytraEvent event = new ElytraEvent((EntityLivingBase) (Object) this);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method={"handleJumpWater"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleJumpWater(CallbackInfo ci) {
        HandleLiquidJumpEvent event = new HandleLiquidJumpEvent();
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method={"handleJumpLava"}, at={@At(value="HEAD")}, cancellable=true)
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
}