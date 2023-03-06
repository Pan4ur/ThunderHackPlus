package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.PearlESP;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Mixin({ EntityArrow.class })
public abstract class MixinEntityArrow extends Entity
{

    public MixinEntityArrow(World worldIn) {
        super(worldIn);
    }

    
    @Inject(method = { "setVelocity" },  at = { @At("RETURN") })
    private void setVelocityHook(double x, double y, double z, CallbackInfo ci) {
        if(!breaked && Thunderhack.moduleManager.getModuleByClass(PearlESP.class).isOn()) {
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
            ticksExisted2 = ticksExisted;
            prevRotationYaw2 = prevRotationYaw;
            prevRotationPitch2 = prevRotationPitch;
            onGround2 = onGround;
            inGround2 = inGround;
            arrowShake2 = arrowShake;
            rotationYaw2 = rotationYaw;
            rotationPitch2 = rotationPitch;
            buildPositions(200);
        }
    }
    

    @Inject(method = { "onUpdate" },  at = { @At("HEAD") })
    private void onUpdate(CallbackInfo ci) {
        if(motionX == 0 && motionY == 0 && motionZ == 0 && Thunderhack.moduleManager.getModuleByClass(PearlESP.class).isOn()){
                if(Thunderhack.moduleManager.getModuleByClass(PearlESP.class).entAndTrail.get(Util.mc.world.getEntityByID(getEntityId())) != null) {
                    Thunderhack.moduleManager.getModuleByClass(PearlESP.class).entAndTrail.get(Util.mc.world.getEntityByID(getEntityId())).clear();
                }
        }
    }



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
    int ticksExisted2;
    float prevRotationYaw2;
    float  prevRotationPitch2;
    int arrowShake2;

    boolean onGround2;
    boolean inGround2;

    float rotationYaw2;
    float  rotationPitch2;
    


    @Shadow private int ticksInGround;
    @Shadow private int ticksInAir;
    @Shadow protected boolean inGround;
    @Shadow public int arrowShake;
    public boolean predictTick, breaked;


    public void buildPositions(int ticks) {

        PearlESP tm = Thunderhack.moduleManager.getModuleByClass(PearlESP.class);

        int i = 0;
        double prevLastPosX = lastTickPosX2;
        double prevLastPosY = lastTickPosY2;
        double prevLastPosZ = lastTickPosZ2;
        double prevPrevPosX = prevPosX2;
        double prevPrevPosY = prevPosY2;
        double prevPrevPosZ = prevPosZ2;
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
        int prevTicksExisted = ticksExisted2;
        float prevYaw = rotationYaw2;
        float prevPitch = rotationPitch2;
        float prevPrevYaw = prevRotationYaw2;
        float prevPrevPitch = prevRotationPitch2;
        int prevArrowShake = arrowShake2;
        predictTick = true;

        if(tm.entAndTrail.get(Util.mc.world.getEntityByID(getEntityId())) != null){
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
            tm.entAndTrail.get(Util.mc.world.getEntityByID(getEntityId())).add(pos);
            if (i == 0) {
                breaked = false;
            }
            if (breaked) {
                break;
            }
            i++;
        }
        arrowShake2 = prevArrowShake;
        prevRotationYaw2 = prevPrevYaw;
        prevRotationPitch2 = prevPrevPitch;
        rotationYaw2 = prevRotationYaw;
        rotationPitch2 = prevRotationPitch;
        predictTick = false;
        lastTickPosX2 = prevLastPosX;
        lastTickPosY2 = prevLastPosY;
        lastTickPosZ2 = prevLastPosZ;
        prevPosX2 = prevPrevPosX;
        prevPosY2 = prevPrevPosY;
        prevPosZ2 = prevPrevPosZ;
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
        ticksExisted2 = prevTicksExisted;
    }

    public void onUpdateFake() {
        if (prevRotationPitch2 == 0.0F && prevRotationYaw2 == 0.0F) {
            float f = MathHelper.sqrt(motionX2 * motionX2 + motionZ2 * motionZ2);
            rotationYaw2 = (float)(MathHelper.atan2(motionX2, motionZ2) * 57.29577951308232);
            rotationPitch2 = (float)(MathHelper.atan2(motionY2, (double)f) * 57.29577951308232);
            prevRotationYaw2 = rotationYaw2;
            prevRotationPitch2 = rotationPitch2;
        }

        if (arrowShake2 > 0) {
            --arrowShake2;
        }

        if (inGround2) {
            if (!world.collidesWithAnyBlock(getEntityBoundingBox().grow(0.05))) {
                inGround = false;
                motionX2 *= (double)(rand.nextFloat() * 0.2F);
                motionY2 *= (double)(rand.nextFloat() * 0.2F);
                motionZ2 *= (double)(rand.nextFloat() * 0.2F);
                ticksInGround2 = 0;
                ticksInAir2 = 0;
            }
        } else {



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


            ++ticksInAir2;
            posX2 += motionX2;
            posY2 += motionY2;
            posZ2 += motionZ2;
            float f4 = MathHelper.sqrt(motionX2 * motionX2 + motionZ2 * motionZ2);
            rotationYaw2 = (float)(MathHelper.atan2(motionX2, motionZ2) * 57.29577951308232);
            for(rotationPitch2 = (float)(MathHelper.atan2(motionY2, (double)f4) * 57.29577951308232); rotationPitch2 - prevRotationPitch2 < -180.0F; prevRotationPitch2 -= 360.0F) {
            }
            while(rotationPitch2 - prevRotationPitch2 >= 180.0F) {
                prevRotationPitch2 += 360.0F;
            }

            while(rotationYaw2 - prevRotationYaw2 < -180.0F) {
                prevRotationYaw2 -= 360.0F;
            }

            while(rotationYaw2 - prevRotationYaw2 >= 180.0F) {
                prevRotationYaw2 += 360.0F;
            }
            rotationPitch2 = prevRotationPitch2 + (rotationPitch2 - prevRotationPitch2) * 0.2F;
            rotationYaw2 = prevRotationYaw2 + (rotationYaw2 - prevRotationYaw2) * 0.2F;
            float f1 = 0.99F;
            if (isInWater()) {
                f1 = 0.6F;
            }
            motionX2 *= (double)f1;
            motionY2 *= (double)f1;
            motionZ2 *= (double)f1;
            if (!hasNoGravity()) {
                motionY2 -= 0.05000000074505806;
            }
           // setPosition(posX2, posY2, posZ2);
        }

    }

    private Vec3d getFakePosition() {
        return new Vec3d(posX2,posY2,posZ2);
    }


}