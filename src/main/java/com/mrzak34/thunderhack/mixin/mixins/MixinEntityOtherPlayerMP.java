package com.mrzak34.thunderhack.mixin.mixins;

import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.render.NoInterp;
import com.mrzak34.thunderhack.modules.render.ShiftInterp;
import com.mrzak34.thunderhack.util.rotations.ResolverUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.Util.mc;


@Mixin( EntityOtherPlayerMP.class )
public class MixinEntityOtherPlayerMP extends AbstractClientPlayer
{
    @Shadow
    private int otherPlayerMPPosRotationIncrements;

    @Shadow
    private double otherPlayerMPX;

    @Shadow
    private double otherPlayerMPY;

    @Shadow
    private double otherPlayerMPZ;

    @Shadow
    private double otherPlayerMPYaw;

    @Shadow
    private double otherPlayerMPPitch;

    public MixinEntityOtherPlayerMP( World worldIn, GameProfile gameProfileIn )
    {
        super( worldIn, gameProfileIn );
    }



    @Inject(method = { "onLivingUpdate" }, at = { @At("HEAD") }, cancellable = true)
    public void onLivingUpdate(CallbackInfo ci) {
        if(NoInterp.getInstance().isEnabled())
        {
            ci.cancel();
            onLivingUpdateCustom();
        }
    }

    public void onLivingUpdateCustom() {
        if (this.otherPlayerMPPosRotationIncrements > 0)
        {
            double d0, d1, d2;
            double d3;

            if(NoInterp.getInstance().isEnabled())
            {
                d0 = serverPosX / 4096.0D;
                d1 = serverPosY / 4096.0D;
                d2 = serverPosZ / 4096.0D;
            }
            else
            {
                d0 = this.posX + (this.otherPlayerMPX - this.posX) / otherPlayerMPPosRotationIncrements;
                d1 = this.posY + (this.otherPlayerMPY - this.posY) / otherPlayerMPPosRotationIncrements;
                d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / otherPlayerMPPosRotationIncrements;
            }

            for (d3 = this.otherPlayerMPYaw - (double)this.rotationYaw; d3 < -180.0D; d3 += 360.0D) {}

            while (d3 >= 180.0D) {d3 -= 360.0D;}

            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.otherPlayerMPPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
            --this.otherPlayerMPPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }

        this.prevCameraYaw = this.cameraYaw;
        this.updateArmSwingProgress();
        float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float f = (float)Math.atan(-this.motionY * 0.20000000298023224D) * 15.0F;
        if (f1 > 0.1F) {f1 = 0.1F;}

        if (!this.onGround || this.getHealth() <= 0.0F) {f1 = 0.0F;}

        if (this.onGround || this.getHealth() <= 0.0F) {f = 0.0F;}
        this.cameraYaw += (f1 - this.cameraYaw) * 0.4F;
        this.cameraPitch += (f - this.cameraPitch) * 0.8F;
        this.world.profiler.startSection("push");
        this.collideWithNearbyEntities();
        this.world.profiler.endSection();
    }

    @Inject(method = "onUpdate", at = @At ("HEAD"), cancellable = true)
    public void prikol(CallbackInfo ci) {
        if(NoInterp.getInstance().lowIQ.getValue() && NoInterp.getInstance().isEnabled()) {
            renderOffsetY = 0;
            super.onUpdate();
            limbSwing = 0;
            limbSwingAmount = 0;
            prevLimbSwingAmount = 0;
            ci.cancel();
        }
    }


    private double serverX, serverY, serverZ, prevServerX, prevServerY, prevServerZ;

    @Inject(method = "setPositionAndRotationDirect", at = @At ("HEAD"), cancellable = true)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport, CallbackInfo ci) {
        this.prevServerX = this.serverX;
        this.prevServerY = this.serverY;
        this.prevServerZ = this.serverZ;
        this.serverX = x;
        this.serverY = y;
        this.serverZ = z;
        if(Aura.target != null && Aura.target == this){
            ResolverUtil.prevServerX = prevServerX;
            ResolverUtil.prevServerY = prevServerY;
            ResolverUtil.prevServerZ = prevServerZ;
            ResolverUtil.serverX = serverX;
            ResolverUtil.serverY = serverY;
            ResolverUtil.serverZ = serverZ;
        }
    }

    @Inject(method = "onUpdate", at = @At ("HEAD"), cancellable = true)
    public void prikol2(CallbackInfo ci) {
        if(ShiftInterp.getInstance().isOn()) {
            renderOffsetY = 0;
            super.onUpdate();
           // limbSwing = 0;
           // limbSwingAmount = 0;
           // prevLimbSwingAmount = 0;

            if(ShiftInterp.getInstance().sleep.getValue()) {
                sleeping = true;
            } else if(ShiftInterp.getInstance().aboba.getValue()){
                EntityPig rockez = new EntityPig(mc.world);
                rockez.limbSwing = limbSwing;
                rockez.limbSwingAmount = limbSwingAmount;
                ridingEntity = rockez;
                ridingEntity.posX = posX;
                ridingEntity.posY = posY;
                ridingEntity.posZ = posZ;
                ridingEntity.rotationYaw = rotationYaw;
                ridingEntity.rotationPitch = rotationPitch;
                renderYawOffset = rotationYaw;
            }
            else {
                sleeping = false;
                setSneaking(true);
            }
            ci.cancel();
        }
    }

}