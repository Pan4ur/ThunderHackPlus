package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.PearlESP;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Mixin({EntityThrowable.class})
public abstract class MixinEntityThrowable extends Entity {


    // https://fabricmc.net/wiki/tutorial:mixin_examples + 0iq =

    @Shadow
    public Entity ignoreEntity;
    public boolean predictTick, breaked;
    @Shadow
    protected EntityLivingBase thrower;
    @Shadow
    protected boolean inGround;
    double lastTickPosX2;
    double lastTickPosY2;
    double lastTickPosZ2;
    double posX2;
    double posY2;
    double posZ2;
    double prevPosX2;
    double prevPosY2;
    double prevPosZ2;
    double motionX2;
    double motionY2;
    double motionZ2;
    int ticksInGround2;
    int ticksInAir2;
    int ignoreTime2;
    int ticksExisted2;
    float prevRotationYaw2;
    float prevRotationPitch2;
    Entity ignoreEntity2;
    boolean onGround2;
    boolean inGround2;
    float rotationYaw2;
    float rotationPitch2;
    @Shadow
    private int ticksInGround;
    @Shadow
    private int ticksInAir;
    @Shadow
    private int ignoreTime;
    public MixinEntityThrowable(World worldIn) {
        super(worldIn);
    }

    @Inject(method = {"setVelocity"}, at = {@At("RETURN")})
    private void setVelocityHook(double x, double y, double z, CallbackInfo ci) {
        if (!breaked && Thunderhack.moduleManager.getModuleByClass(PearlESP.class).isOn()) {
            lastTickPosX2 = lastTickPosX;
            lastTickPosY2 = lastTickPosY;
            lastTickPosZ2 = lastTickPosZ;
            posX2 = posX;
            posY2 = posY;
            posZ2 = posZ;
            prevPosX2 = prevPosX;
            prevPosY2 = prevPosY;
            prevPosZ2 = prevPosZ;
            motionX2 = motionX;
            motionY2 = motionY;
            motionZ2 = motionZ;
            ticksInGround2 = ticksInGround;
            ticksInAir2 = ticksInAir;
            ignoreTime2 = ignoreTime;
            ticksExisted2 = ticksExisted;
            prevRotationYaw2 = prevRotationYaw;
            prevRotationPitch2 = prevRotationPitch;
            ignoreEntity2 = ignoreEntity;

            onGround2 = onGround;
            inGround2 = inGround;

            rotationYaw2 = rotationYaw;
            rotationPitch2 = rotationPitch;
            buildPositions(200);

        }
    }

    public void buildPositions(int ticks) {

        PearlESP tm = Thunderhack.moduleManager.getModuleByClass(PearlESP.class);
        int i = 0;
        double prevLastPosX = lastTickPosX2;
        double prevLastPosY = lastTickPosY2;
        double prevLastPosZ = lastTickPosZ2;
        double prevprevPosX = prevPosX2;
        double prevprevPosY = prevPosY2;
        double prevprevPosZ = prevPosZ2;
        double prevPosX = posX2;
        double prevPosY = posY2;
        double prevPosZ = posZ2;
        double prevMotionX = motionX2;
        double prevMotionY = motionY2;
        double prevMotionZ = motionZ2;
        boolean prevOnGround = onGround2;
        boolean prevInGround = inGround2;
        int prevTicksInGround = ticksInGround2;
        int prevTicksInAir = ticksInAir2;
        int prevIgnoreTime = ignoreTime2;
        int prevTicksExisted = ticksExisted2;
        float prevPrevYaw = prevRotationYaw2;
        float prevPrevPitch = prevRotationPitch2;
        Entity prevIgnoreEntity = ignoreEntity2;
        predictTick = true;
        if (tm.entAndTrail.get(Util.mc.world.getEntityByID(getEntityId())) != null) {
            tm.entAndTrail.get(Util.mc.world.getEntityByID(getEntityId())).clear();
        }

        List<PearlESP.PredictedPosition> trails22 = new ArrayList<>();
        tm.entAndTrail.putIfAbsent(Util.mc.world.getEntityByID(getEntityId()), trails22);


        while (i < ticks) {
            onUpdateFake();
            PearlESP.PredictedPosition pos = new PearlESP.PredictedPosition();
            pos.pos = getFakePosition();
            pos.tick = i;
            pos.color = new Color(-1);
            // ..  pos.color.setRGBA(ESP.predict.getColor().getRed(), ESP.predict.getColor().getGreen(), ESP.predict.getColor().getBlue(), ESP.predict.getColor().getAlpha());
            tm.entAndTrail.get(Util.mc.world.getEntityByID(getEntityId())).add(pos);
            if (i == 0) {
                breaked = false;
            }
            if (breaked) {
                break;
            }
            i++;
        }
        prevRotationYaw2 = prevPrevYaw;
        prevRotationPitch2 = prevPrevPitch;
        rotationYaw2 = prevRotationYaw2;
        rotationPitch2 = prevRotationPitch2;
        predictTick = false;
        lastTickPosX2 = prevLastPosX;
        lastTickPosY2 = prevLastPosY;
        lastTickPosZ2 = prevLastPosZ;
        prevPosX2 = prevprevPosX;
        prevPosY2 = prevprevPosY;
        prevPosZ2 = prevprevPosZ;
        posX2 = prevPosX;
        posY2 = prevPosY;
        posZ2 = prevPosZ;
        motionX2 = prevMotionX;
        motionY2 = prevMotionY;
        motionZ2 = prevMotionZ;
        onGround2 = prevOnGround;
        inGround2 = prevInGround;
        ticksInGround2 = prevTicksInGround;
        ticksInAir2 = prevTicksInAir;
        ignoreTime2 = prevIgnoreTime;
        ticksExisted2 = prevTicksExisted;
        ignoreEntity2 = prevIgnoreEntity;
    }

    private Vec3d getFakePosition() {
        return new Vec3d(posX2, posY2, posZ2);
    }


    public void onUpdateFake() {
        lastTickPosX2 = posX2;
        lastTickPosY2 = posY2;
        lastTickPosZ2 = posZ2;
        if (inGround2) {
            inGround2 = false;
            motionX2 *= rand.nextFloat() * 0.2F;
            motionY2 *= rand.nextFloat() * 0.2F;
            motionZ2 *= rand.nextFloat() * 0.2F;
            ticksInGround2 = 0;
            ticksInAir2 = 0;
        } else {
            ++ticksInAir2;
        }


        Vec3d vec3d = new Vec3d(posX2, posY2, posZ2);
        Vec3d vec3d1 = new Vec3d(posX2 + motionX2, posY2 + motionY2, posZ2 + motionZ2);
        RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1);
        vec3d = new Vec3d(posX2, posY2, posZ2);
        vec3d1 = new Vec3d(posX2 + motionX2, posY2 + motionY2, posZ2 + motionZ2);
        if (raytraceresult != null) {
            vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }

        Entity entity = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX2, motionY2, motionZ2).grow(1.0));
        double d0 = 0.0;
        boolean flag = false;

        for (Entity entity1 : list) {
            if (entity1.canBeCollidedWith()) {
                if (entity1 == ignoreEntity2) {
                    flag = true;
                } else if (thrower != null && ticksExisted < 2 && ignoreEntity2 == null) {
                    ignoreEntity2 = entity1;
                    flag = true;
                } else {
                    flag = false;
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896);
                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
                    if (raytraceresult1 != null) {
                        double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);
                        if (d1 < d0 || d0 == 0.0) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }
        }

        if (ignoreEntity2 != null) {
            if (flag) {
                ignoreTime2 = 2;
            } else if (ignoreTime2-- <= 0) {
                ignoreEntity2 = null;
            }
        }

        if (entity != null) {
            raytraceresult = new RayTraceResult(entity);
        }

        if (raytraceresult != null) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {

            } else {
                if (!this.predictTick) {

                } else {
                    this.breaked = true;
                }
            }
        }

        posX2 += motionX2;
        posY2 += motionY2;
        posZ2 += motionZ2;
        float f = MathHelper.sqrt(motionX2 * motionX2 + motionZ2 * motionZ2);
        rotationYaw2 = (float) (MathHelper.atan2(motionX2, motionZ2) * 57.29577951308232);

        for (rotationPitch2 = (float) (MathHelper.atan2(motionY2, f) * 57.29577951308232); rotationPitch2 - prevRotationPitch2 < -180.0F; prevRotationPitch2 -= 360.0F) {
        }

        while (rotationPitch2 - prevRotationPitch2 >= 180.0F) {
            prevRotationPitch2 += 360.0F;
        }

        while (rotationYaw2 - prevRotationYaw2 < -180.0F) {
            prevRotationYaw2 -= 360.0F;
        }

        while (rotationYaw2 - prevRotationYaw2 >= 180.0F) {
            prevRotationYaw2 += 360.0F;
        }

        rotationPitch2 = prevRotationPitch2 + (rotationPitch2 - prevRotationPitch2) * 0.2F;
        rotationYaw2 = prevRotationYaw2 + (rotationYaw2 - prevRotationYaw2) * 0.2F;
        float f1 = 0.99F;
        float f2 = 0.03f;
        motionX2 *= f1;
        motionY2 *= f1;
        motionZ2 *= f1;
        if (!hasNoGravity()) {
            motionY2 -= f2;
        }
    }


    //как меня заебали эти миксины


}