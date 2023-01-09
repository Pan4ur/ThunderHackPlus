package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.MatrixMove;
import com.mrzak34.thunderhack.event.events.PushEvent;
import com.mrzak34.thunderhack.event.events.TurnEvent;
import com.mrzak34.thunderhack.modules.combat.HitBoxes;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.phobos.EntityType;
import com.mrzak34.thunderhack.util.phobos.IEntity;
import com.mrzak34.thunderhack.util.phobos.IEntityNoInterp;
import com.mrzak34.thunderhack.util.rotations.CastHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.block.*;
import net.minecraft.crash.*;
import net.minecraft.util.*;

import java.util.*;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.Util.mc;


@Mixin({ Entity.class })
public abstract class MixinEntity implements IEntity
{

    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    public World world;
    @Shadow
    public boolean collidedHorizontally;
    @Shadow
    public boolean collidedVertically;

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();


    @Shadow
    public abstract boolean isInWater();

    @Shadow
    public abstract void playSound(final SoundEvent p0,  final float p1,  final float p2);




    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void onTurn(float yaw, float pitch, CallbackInfo ci) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    //749









    @Redirect(method = { "applyEntityCollision" },  at = @At(value = "INVOKE",  target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(final Entity entity,  final double x,  final double y,  final double z) {
        final PushEvent event = new PushEvent(entity,  x,  y,  z,  true);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            entity.motionX += event.x;
            entity.motionY += event.y;
            entity.motionZ += event.z;
            entity.isAirBorne = event.airbone;
        }
    }

    @Inject(method = "getCollisionBorderSize", at = @At("HEAD"), cancellable = true)
    private void getCollisionBorderSize(final CallbackInfoReturnable<Float> callbackInfoReturnable) {


        if (Thunderhack.moduleManager.getModuleByClass(HitBoxes.class).isEnabled())
            callbackInfoReturnable.setReturnValue(0.1F + Thunderhack.moduleManager.getModuleByClass(HitBoxes.class).expand.getValue());
    }









    @Shadow
    public double prevPosX;
    @Shadow
    public double prevPosY;
    @Shadow
    public double prevPosZ;
    @Shadow
    public double lastTickPosX;
    @Shadow
    public double lastTickPosY;
    @Shadow
    public double lastTickPosZ;

    @Shadow
    public boolean isDead;
    @Shadow
    public float width;
    @Shadow
    public float prevRotationYaw;
    @Shadow
    public float prevRotationPitch;
    @Shadow
    public float height;

    @Unique
    private long oldServerX;
    @Unique
    private long oldServerY;
    @Unique
    private long oldServerZ;

    private final Timer pseudoWatch = new Timer();
    private Supplier<EntityType> type;
    private boolean pseudoDead;
    private long stamp;
    private boolean dummy;


    @Shadow
    protected abstract boolean getFlag(int flag);
    @Shadow
    public abstract boolean equals(Object p_equals_1_);
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
    @Shadow
    public abstract boolean isRiding();

    @Shadow
    public boolean noClip;

    @Shadow
    public abstract void move(MoverType type, double x, double y,
                              double z);
    @Shadow
    public abstract String getName();

    @Shadow public abstract void moveRelative(float strafe, float up, float forward, float friction);

    @Override
    @Accessor(value = "isInWeb")
    public abstract boolean inWeb();

    @Override
    public EntityType getType()
    {
        return type.get();
    }

    @Override
    public long getDeathTime()
    {
        // TODO!!!
        return 0;
    }

    @Override
    public void setOldServerPos(long x, long y, long z)
    {
        this.oldServerX = x;
        this.oldServerY = y;
        this.oldServerZ = z;
    }

    @Override
    public long getOldServerPosX()
    {
        return oldServerX;
    }

    @Override
    public long getOldServerPosY()
    {
        return oldServerY;
    }

    @Override
    public long getOldServerPosZ()
    {
        return oldServerZ;
    }

    @Override
    public boolean isPseudoDead()
    {
        if (pseudoDead && !isDead && pseudoWatch.passedMs(500))
        {
            pseudoDead = false;
        }

        return pseudoDead;
    }

    @Override
    public void setPseudoDead(boolean pseudoDead)
    {
        this.pseudoDead = pseudoDead;
        if (pseudoDead)
        {
            pseudoWatch.reset();
        }
    }

    @Override
    public Timer getPseudoTime()
    {
        return pseudoWatch;
    }

    @Override
    public long getTimeStamp()
    {
        return stamp;
    }

    @Override
    public boolean isDummy()
    {
        return dummy;
    }

    @Override
    public void setDummy(boolean dummy)
    {
        this.dummy = dummy;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void ctrHook(CallbackInfo info)
    {
        this.type = EntityType.getEntityType(Entity.class.cast(mc.player));
        this.stamp = System.currentTimeMillis();
    }

    @Inject(method = "setPositionAndRotation", at = @At("RETURN"))
    public void setPositionAndRotationHook(double x,
                                           double y,
                                           double z,
                                           float yaw,
                                           float pitch,
                                           CallbackInfo ci)
    {
        if (this instanceof IEntityNoInterp)
        {
            ((IEntityNoInterp) mc.player).setNoInterpX(x);
            ((IEntityNoInterp) mc.player).setNoInterpY(y);
            ((IEntityNoInterp) mc.player).setNoInterpZ(z);
        }
    }




}