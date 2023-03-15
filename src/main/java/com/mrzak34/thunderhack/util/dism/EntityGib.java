package com.mrzak34.thunderhack.util.dism;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.Dismemberment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class EntityGib extends Entity {
    public EntityLivingBase parent;
    public int type;
    public float pitchSpin;
    public float yawSpin;

    public int groundTime;
    public int liveTime;

    public boolean explosion;

    public EntityGib(World world) {
        super(world);
        parent = null;
        type = 0; //0 == head; 1 == left arm; 2 == right arm; 3 == head; 4 == left leg; 5 == right leg; 6+ creeperfeet. -1 playerhead2layer

        groundTime = 0;
        liveTime = Dismemberment.ticks;
        ignoreFrustumCheck = true;
    }

    public EntityGib(World world, EntityLivingBase gibParent, int gibType, Entity explo) {
        this(world);
        parent = gibParent;
        type = gibType;

        liveTime = Dismemberment.ticks;

        setLocationAndAngles(parent.posX, parent.getEntityBoundingBox().minY, parent.posZ, parent.rotationYaw, parent.rotationPitch);
        rotationYaw = parent.prevRenderYawOffset;
        prevRotationYaw = parent.rotationYaw;
        prevRotationPitch = parent.rotationPitch;

        motionX = parent.motionX + (rand.nextDouble() - rand.nextDouble()) * 0.25D;
        motionY = parent.motionY;
        motionZ = parent.motionZ + (rand.nextDouble() - rand.nextDouble()) * 0.25D;

        if (type == -1) {
            rotationYaw = parent.rotationYawHead;
            setSize(1, 1);
            posY += 1.5D;
        }
        if (type == 0) {
            rotationYaw = parent.rotationYawHead;
            setSize(0.5F, 0.5F);
            if (parent instanceof EntityCreeper) {
                posY += 1.25D;
            } else {
                posY += 1.5D;
            }
        } else if (type == 1 || type == 2) {
            setSize(0.3F, 0.4F);

            double offset = 0.350D;
            double offset1 = -0.250D;

            if (parent instanceof EntitySkeleton) {
                offset -= 0.05D;
                posY += 0.15D;
            }
            if (type == 2) {
                offset *= -1D;
            }

            posX += offset * Math.cos(Math.toRadians(parent.renderYawOffset));
            posZ += offset * Math.sin(Math.toRadians(parent.renderYawOffset));

            posX += offset1 * Math.sin(Math.toRadians(parent.renderYawOffset));
            posZ -= offset1 * Math.cos(Math.toRadians(parent.renderYawOffset));

            posY += 1.25D;

            prevRotationPitch = rotationPitch = -90F;
        } else if (type == 3) {
            setSize(0.5F, 0.5F);
            if (parent instanceof EntityCreeper) {
                posY += 0.75D;
            } else {
                posY += 1.0D;
            }
        } else if (type == 4 || type == 5) {
            setSize(0.3F, 0.4F);

            double offset = 0.125D;

            if (type == 5) {
                offset *= -1D;
            }

            posX += offset * Math.cos(Math.toRadians(parent.renderYawOffset));
            posZ += offset * Math.sin(Math.toRadians(parent.renderYawOffset));

            posY += 0.375D;
        } else if (type >= 6) {
            setSize(0.3F, 0.4F);

            double offset = 0.125D;
            double offset1 = -0.250D;

            if (parent instanceof EntitySkeleton) {
                offset -= 0.05D;
                posY += 0.15D;
            }
            if (type % 2 == 1) {
                offset *= -1D;
            }
            if (type >= 8) {
                offset1 *= -1D;
            }

            posX += offset * Math.cos(Math.toRadians(parent.renderYawOffset));
            posZ += offset * Math.sin(Math.toRadians(parent.renderYawOffset));

            posX += offset1 * Math.sin(Math.toRadians(parent.renderYawOffset));
            posZ -= offset1 * Math.cos(Math.toRadians(parent.renderYawOffset));

            posY += 0.3125D;
        }

        float i = rand.nextInt(45) + 5F + rand.nextFloat();
        float j = rand.nextInt(45) + 5F + rand.nextFloat();
        if (rand.nextInt(2) == 0) {
            i *= -1;
        }
        if (rand.nextInt(2) == 0) {
            j *= -1;
        }
        pitchSpin = i * (float) (motionY + 0.3D);
        yawSpin = j * (float) (Math.sqrt(motionX * motionZ) + 0.3D);

        setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);

        if (explo != null) {
            double mag = 1.0D;
            double mag2 = 1.0D;
            double dist = explo.getDistance(parent);
            dist = Math.pow(dist / 2D, 2);
            if (dist < 0.1D) {
                dist = 0.1D;
            }
            if (explo instanceof EntityTNTPrimed || explo instanceof EntityMinecartTNT) {
                mag = (4.0 / dist);
            } else if (explo instanceof EntityCreeper) {
                EntityCreeper creep = (EntityCreeper) explo;
                if (creep.getPowered()) {
                    mag = (6.0D / dist);
                } else {
                    mag = (3.0D / dist);
                }
            }
            mag = Math.pow(mag, 2) * 0.2D;
            mag2 = ((posY - explo.posY));
            motionX *= mag;
            motionY = mag2 * 0.4D + 0.22D;
            motionZ *= mag;

            explosion = true;
        }
    }


    @Override
    public void onUpdate() {
        if (parent == null) {
            setDead();
            return;
        }
        if (explosion) {
            motionX *= 1D / 0.92D;
            motionY *= 1D / 0.95D;
            motionZ *= 1D / 0.92D;
        }
        super.onUpdate();
        move(MoverType.SELF, motionX, motionY, motionZ);

        this.motionY -= 0.08D;

        this.motionY *= 0.98D;
        this.motionX *= 0.91D;
        this.motionZ *= 0.91D;

        if (inWater) {
            motionY = 0.3D;
            pitchSpin = 0.0F;
            yawSpin = 0.0F;
        }
        if (onGround || handleWaterMovement()) {
            rotationPitch += (-90F - (rotationPitch % 360F)) / 2;

            this.motionY *= 0.8D;
            this.motionX *= 0.8D;
            this.motionZ *= 0.8D;
        } else {
            rotationPitch += pitchSpin;
            rotationYaw += yawSpin;
            pitchSpin *= 0.98F;
            yawSpin *= 0.98F;
        }

        if (true) // TODO pushing
        {
            List var2 = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(0.15D, 0.0D, 0.15D));
            if (var2 != null && !var2.isEmpty()) {
                Iterator var10 = var2.iterator();

                while (var10.hasNext()) {
                    Entity var4 = (Entity) var10.next();

                    if (var4 instanceof EntityGib && !var4.onGround) {
                        continue;
                    }

                    if (var4.canBePushed()) {
                        var4.applyEntityCollision(this);
                    }
                }
            }
        }

        if (onGround || handleWaterMovement()) {
            groundTime++;
            if (groundTime > Thunderhack.moduleManager.getModuleByClass(Dismemberment.class).gibGroundTime.getValue() + 20) {
                setDead();
            }
        } else if (groundTime > Thunderhack.moduleManager.getModuleByClass(Dismemberment.class).gibGroundTime.getValue()) {
            groundTime--;
        } else {
            groundTime = 0;
        }
        if (liveTime + Thunderhack.moduleManager.getModuleByClass(Dismemberment.class).gibTime.getValue() < Dismemberment.ticks) {
            setDead();
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public boolean isEntityAlive() {
        return !this.isDead;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound) {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }
}