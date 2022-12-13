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
    @Shadow public int recentlyHit;

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    protected int armorValue = Integer.MAX_VALUE;
    protected float armorToughness = Float.MAX_VALUE;
    protected int explosionModifier = Integer.MAX_VALUE;


    @Shadow
    @Final
    public static DataParameter<Float> HEALTH;
    @Shadow
    public float moveStrafing;
    @Shadow
    public float moveForward;
    @Shadow
    public int activeItemStackUseCount;
    @Shadow
    public ItemStack activeItemStack;

    protected double noInterpX;
    protected double noInterpY;
    protected double noInterpZ;
    protected int noInterpPositionIncrements;
    protected float noInterpPrevSwing;
    protected float noInterpSwingAmount;
    protected float noInterpSwing;
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


    @Shadow
    public abstract int getTotalArmorValue();

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

    /**
    * @author pan4ur
    * @reason ho4u roteity
    */


    @Overwrite
    public void moveRelative(float strafe, float up, float forward, float friction) {
        EventStrafe event = new EventStrafe(this.rotationYaw,strafe,forward,friction);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if(event.isCanceled()){
            return;
        }
        float f = strafe * strafe + up * up + forward * forward;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt(f);
            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe *= f;
            up *= f;
            forward *= f;
            if (this.isInWater() || this.isInLava()) {
                strafe *= (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                up *= (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
                forward *= (float)this.getEntityAttribute(SWIM_SPEED).getAttributeValue();
            }

            float f1 = MathHelper.sin(this.rotationYaw * 0.017453292F);
            float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F);
            this.motionX += (double)(strafe * f2 - forward * f1);
            this.motionY += (double)up;
            this.motionZ += (double)(forward * f2 + strafe * f1);
        }

    }

    @Shadow
    public AbstractAttributeMap attributeMap;


    public AbstractAttributeMap getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new AttributeMap();
        }

        return this.attributeMap;
    }

    public IAttributeInstance getEntityAttribute(IAttribute attribute) {
        return this.getAttributeMap().getAttributeInstance(attribute);
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void jumphook(CallbackInfo ci) {
        EventJump event = new EventJump(this.rotationYaw);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }


    /*
    @Overwrite
    public boolean isChild(){
        return true;
    }

     */




}