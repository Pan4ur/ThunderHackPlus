package com.mrzak34.thunderhack.util.DeadCodeUtils;

import java.util.Random;

import com.mrzak34.thunderhack.modules.combat.DeadCodeAura;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.RandomUtils;

import static com.mrzak34.thunderhack.util.Util.mc;

public class MathUtils {
    public static double a;

    public static float a(Entity entity) {
        float f2 = entity.rotationYaw;
        return f2 *= (float)Math.PI / 180;
    }

    public static double a(boolean bl) {
        EntityPlayerSP entityPlayerSP = mc.player;
        float f2 = entityPlayerSP.rotationYaw;
        if (entityPlayerSP.moveForward < 0.0f) {
            f2 += 180.0f;
        }
        float f3 = 1.0f;
        if (entityPlayerSP.moveForward < 0.0f) {
            f3 = -0.5f;
        } else if (entityPlayerSP.moveForward > 0.0f) {
            f3 = 0.5f;
        }
        if (entityPlayerSP.moveStrafing > 0.0f) {
            f2 -= 90.0f * f3;
        }
        if (entityPlayerSP.moveStrafing < 0.0f) {
            f2 += 90.0f * f3;
        }
        return bl ? Math.toRadians(f2) : (double)f2;
    }

    public static void a(Entity entity, boolean bl, boolean bl2, float f2) {
        float[] fArray;
        EntityPlayerSP entityPlayerSP = mc.player;
        double d2 = entity.posX + (bl2 ? (entity.posX - entity.prevPosX) * (double)f2 : 0.0) - (entityPlayerSP.posX + (bl2 ? entityPlayerSP.posX - entityPlayerSP.prevPosX : 0.0));
        double d3 = entity.getEntityBoundingBox().minY + (bl2 ? (entity.getEntityBoundingBox().minY - entity.prevPosY) * (double)f2 : 0.0) + (double)entity.getEyeHeight() - 0.15 - (entityPlayerSP.getEntityBoundingBox().minY + (bl2 ? entityPlayerSP.posY - entityPlayerSP.prevPosY : 0.0)) - (double)entityPlayerSP.getEyeHeight();
        double d4 = entity.posZ + (bl2 ? (entity.posZ - entity.prevPosZ) * (double)f2 : 0.0) - (entityPlayerSP.posZ + (bl2 ? entityPlayerSP.posZ - entityPlayerSP.prevPosZ : 0.0));
        double d5 = Math.sqrt(d2 * d2 + d4 * d4);
        float f3 = (float)entityPlayerSP.getItemInUseCount() / 20.0f;
        if ((f3 = (f3 * f3 + f3 * 2.0f) / 3.0f) > 1.0f) {
            f3 = 1.0f;
        }
        float f4 = (float)(Math.atan2(d4, d2) * 180.0 / Math.PI) - 90.0f;
        float f5 = (float)(-Math.toDegrees(Math.atan(((double)(f3 * f3) - Math.sqrt((double)(f3 * f3 * f3 * f3) - (double)0.006f * ((double)0.006f * (d5 * d5) + 2.0 * d3 * (double)(f3 * f3)))) / ((double)0.006f * d5))));
        if (f3 < 0.1f) {
            fArray = MathUtils.a(MathUtils.a(entity.getEntityBoundingBox()), true);
            f4 = fArray[0];
            f5 = fArray[1];
        }
        if (bl) {
            ef.a(f4, f5);
            mc.player.renderYawOffset = ef.d();
            mc.player.rotationYawHead = ef.d();
            return;
        }
        fArray = MathUtils.a(new float[]{entityPlayerSP.rotationYaw, entityPlayerSP.rotationPitch}, new float[]{f4, f5}, (float)(10 + RandomUtils.nextInt(1,5)));
        if (fArray == null) {
            return;
        }
        entityPlayerSP.rotationYaw = fArray[0];
        entityPlayerSP.rotationPitch = fArray[1];
    }

    public static float[] a(Vec3d vec3d, boolean bl) {
        Vec3d vec3d2 = MathUtils.a();
        if (bl) {
            vec3d2.add(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
        }
        double d2 = vec3d.x - vec3d2.x;
        double d3 = vec3d.y - vec3d2.y;
        double d4 = vec3d.z - vec3d2.z;
        double d5 = Math.sqrt(d2 * d2 + d4 * d4);
        float f2 = (float)Math.toDegrees(Math.atan2(d4, d2)) - 90.0f;
        float f3 = (float)(-Math.toDegrees(Math.atan2(d3, d5)));
        return new float[]{MathHelper.wrapDegrees((float)f2), MathHelper.wrapDegrees((float)f3)};
    }

    public static float b(Entity entity) {
        double d2 = entity.posY - cs.a.e();
        double d3 = Math.asin(d2 /= (double)mc.player.getDistance(entity)) * 57.29577951308232;
        d3 = -d3;
        return (float)d3;
    }

    public static float c(Entity entity) {
        double d2 = entity.posX - cs.a.d();
        double d3 = entity.posZ - cs.a.f();
        double d4 = Math.atan2(d2, d3) * 57.29577951308232;
        d4 = -d4;
        return (float)d4;
    }

    public static Vec3d a(AxisAlignedBB axisAlignedBB) {
        return new Vec3d(axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * 0.5, axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * 0.5, axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * 0.5);
    }

    public static boolean a(Entity entity, Entity entity2, double d2) {
        double d3 = MathUtils.a(entity.rotationYaw, MathUtils.a(entity, entity2.posX, entity2.posY, entity2.posZ)[0]);
        return d3 > 0.0 && d3 < (d2 *= 0.5) || -d2 < d3 && d3 < 0.0;
    }

    public static boolean a(Entity entity, double d2) {
        return MathUtils.a((Entity)mc.player, entity, d2);
    }

    public static boolean a(BlockPos blockPos, double d2) {
        double d3 = MathUtils.a(mc.player.rotationYaw, MathUtils.a((Entity)mc.player, blockPos.getX(), blockPos.getY(), (double)blockPos.getZ())[0]);
        return d3 > 0.0 && d3 < (d2 *= 0.5) || -d2 < d3 && d3 < 0.0;
    }

    public static float a(float f2, float f3) {
        float f4 = Math.abs(f3 - f2) % 360.0f;
        float f5 = f4 > 180.0f ? 360.0f - f4 : f4;
        return f5;
    }

    public static float a(float f2, float f3, float f4) {
        float f5;
        float f6 = MathHelper.wrapDegrees((float)(f3 - f2));
        if (f6 > f4) {
            f6 = f4;
        }
        if (f6 < -f4) {
            f6 = -f4;
        }
        if ((f5 = f2 + f6) < 0.0f) {
            f5 += 360.0f;
        } else if (f5 > 360.0f) {
            f5 -= 360.0f;
        }
        return f5;
    }

    public static float b(float f2, float f3) {
        return ((f2 - f3) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public static float b(float f2, float f3, float f4) {
        float f5 = MathUtils.b(f3, f2);
        return f2 + (f5 > f4 ? f4 : Math.max(f5, -f4));
    }

    public static float[] a(float[] fArray, float[] fArray2, float f2) {
        double d2 = MathUtils.b(fArray2[0], fArray[0]);
        double d3 = MathUtils.b(fArray2[1], fArray[1]);
        boolean bl = false;
        fArray[0] = fArray[0] + (float)(d2 > (double)f2 ? (double)f2 : (d2 < (double)(-f2) ? (double)(-f2) : d2));
        boolean bl2 = true;
        fArray[1] = fArray[1] + (float)(d3 > (double)f2 ? (double)f2 : (d3 < (double)(-f2) ? (double)(-f2) : d3));
        return fArray;
    }

    public static float[] d(Entity entity) {
        return MathUtils.a(entity, 6.5f);
    }

    public static float[] a(Entity entity, float f2) {
        float f3 = f2 / mc.player.getDistance(entity);
        float f4 = 6.0f;
        return new float[]{f3 * entity.width * f4, f3 * entity.height * f4};
    }

    public static float c(float f2, float f3) {
        float f4 = f3 - f2;
        if (f4 > 180.0f) {
            f4 = -(360.0f - f3 + f2);
        } else if (f4 < -180.0f) {
            f4 = 360.0f - f2 + f3;
        }
        return f4;
    }

    public static double d(float f2, float f3) {
        return Math.sqrt(Math.pow(Math.abs(MathUtils.b(ef.d() % 360.0f, f2)), 2.0) + Math.pow(Math.abs(MathUtils.b(ef.e(), f3)), 2.0));
    }

    public static Vec3d a() {
        return new Vec3d(cs.a.d(), cs.a.e() + (double)mc.player.getEyeHeight(), cs.a.f());
    }

    public static Vec3d e(float f2, float f3) {
        float f4 = MathHelper.cos((float)(-f3 * ((float)Math.PI / 180) - (float)Math.PI));
        float f5 = MathHelper.sin((float)(-f3 * ((float)Math.PI / 180) - (float)Math.PI));
        float f6 = -MathHelper.cos((float)(-f2 * ((float)Math.PI / 180)));
        float f7 = MathHelper.sin((float)(-f2 * ((float)Math.PI / 180)));
        return new Vec3d((double)(f5 * f6), (double)f7, (double)(f4 * f6));
    }

    public static float c(float f2, float f3, float f4) {
        float f5 = MathHelper.wrapDegrees((float)(f3 - f2));
        if (f5 > f4) {
            f5 = f4;
        }
        if (f5 < -f4) {
            f5 = -f4;
        }
        return f2 + f5;
    }

    public static float[] a(Entity entity, boolean bl) {
        EntityPlayerSP entityPlayerSP = mc.player;
        float f2 = bl ? entityPlayerSP.rotationYaw : ef.d();
        float f3 = bl ? entityPlayerSP.rotationPitch : ef.e();
        float f4 = 4.096f;
        double d2 = entity.posX - entityPlayerSP.posX;
        double d3 = entity.getPositionEyes((float)1.0f).y - entityPlayerSP.getPositionEyes((float)1.0f).y;
        double d4 = entity.posZ - entityPlayerSP.posZ;
        double d5 = Math.sqrt(Math.pow(d2, 2.0) + Math.pow(d4, 2.0));
        float f5 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(d4, d2)) - 90.0));
        float f6 = (float)(-Math.toDegrees(Math.atan2(d3, d5))) + 10.0f;
        float f7 = MathHelper.wrapDegrees((float)(f5 - f2));
        float f8 = f6 - f3;
        int n2 = (int)Math.abs(f7);
        int n3 = (int)Math.abs(f8);
        float f9 = f2;
        float f10 = f3;
        if (n2 > 4 || n3 > 4) {
            if (f7 > 180.0f) {
                f7 -= 180.0f;
            }
            int n4 = (int)(Math.abs(Math.sin(a)) * 7.0);
            int n5 = (int)(Math.abs(Math.sin(a)) * 3.0);
            if (n2 < 10) {
                n4 = 0;
            }
            if (n3 < 10) {
                n5 = 0;
            }
            if (n4 == 0 && new Random().nextBoolean()) {
                n4 = 1;
            }
            f9 = f2 + (float)(f7 > 0.0f ? n4 : -n4) * f4;
            f10 = f3 + (float)(f8 > 0.0f ? n5 : -n5) * f4;
            a += new Random().nextDouble() * 0.19666;
        }
        return new float[]{f9, f10};
    }

    public static float[] a(Entity entity, DeadCodeAura.Hitbox cy_02, float f2) {
        double d2;
        EntityPlayerSP entityPlayerSP = mc.player;
        double d3 = entity.posX - entityPlayerSP.posX;
        double d4 = entity.posZ - entityPlayerSP.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            float f3 = randomFloat((float)(entityLivingBase.posY + (double)(entityLivingBase.getEyeHeight() / 1.5f)), (float)(entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (double)(entityLivingBase.getEyeHeight() / 3.0f)));
            d2 = (double)f3 - (entityPlayerSP.posY + (double)entityPlayerSP.getEyeHeight());
        } else {
            d2 = (double)randomFloat((float)entity.getEntityBoundingBox().minY, (float)entity.getEntityBoundingBox().maxY) - (entityPlayerSP.posY + (double)entityPlayerSP.getEyeHeight());
        }
        switch (cy_02) {
            case HEAD: {
                d2 += 0.6;
                break;
            }
            case TIGHS: {
                d2 -= 0.6;
                break;
            }
        }
        double d5 = Math.sqrt(d3 * d3 + d4 * d4);
        float f4 = (float)(Math.atan2(d4, d3) * 180.0 / Math.PI) - 90.0f;
        float f5 = (float)(-(Math.atan2(d2, d5) * 180.0 / Math.PI));
        if ((double)f2 > 0.0) {
            f4 += randomFloat(-f2, f2);
            f5 += randomFloat(-f2, f2);
        }
        float f6 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f7 = f6 * f6 * f6 * 1.2f;
        f4 -= f4 % f7;
        f5 -= f5 % f7;
        double d6 = entityPlayerSP.getDistance(entity.posX, entityPlayerSP.posY, entity.posZ);
        if (d6 < 0.5) {
            f5 = RandomUtils.nextInt(80, 90);
        }
        return new float[]{f4, f5};
    }

    public static float[] a(Entity entity, DeadCodeAura.Hitbox cy_02) {
        double d2;
        double d3 = entity.posX - mc.player.posX;
        double d4 = entity.posZ - mc.player.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            d2 = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (mc.player.posY + (double)mc.player.getEyeHeight());
        } else {
            d2 = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.player.posY + (double)mc.player.getEyeHeight());
        }
        switch (cy_02) {
            case CHEST: {
                d2 -= 0.5;
                break;
            }
            case TIGHS: {
                d2 -= 1.4;
                break;
            }
        }
        double d5 = Math.sqrt(d3 * d3 + d4 * d4);
        float f2 = (float)(Math.atan2(d4, d3) * 180.0 / Math.PI) - 90.0f;
        float f3 = (float)(-(Math.atan2(d2, d5) * 180.0 / Math.PI));
        float f4 = f2;
        float f5 = f3;
        return new float[]{f4, f5};
    }

    public static float[] a(Entity entity, double d2, double d3, double d4) {
        double d5 = d2 + 0.5 - entity.posX;
        double d6 = d3 - entity.posY;
        double d7 = d4 + 0.5 - entity.posZ;
        double d8 = Math.sqrt(d5 * d5 + d7 * d7);
        float f2 = (float)(Math.atan2(d7, d5) * 180.0 / Math.PI) - 90.0f;
        float f3 = (float)(-(Math.atan2(d6, d8) * 180.0 / Math.PI));
        return new float[]{f2, f3};
    }

    public static float[] a(BlockPos blockPos) {
        double d2 = (double)blockPos.getX() + 0.5 - cs.a.d();
        double d3 = (double)blockPos.getY() + 0.5 - (mc.player.getEntityBoundingBox().minY + (double)mc.player.getEyeHeight());
        double d4 = (double)blockPos.getZ() + 0.5 - cs.a.f();
        double d5 = Math.sqrt(d2 * d2 + d4 * d4);
        float f2 = (float)(Math.atan2(d4, d2) * 180.0 / Math.PI) - 90.0f;
        float f3 = (float)(-(Math.atan2(d3, d5) * 180.0 / Math.PI));
        float f4 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f5 = f4 * f4 * f4 * 1.2f;
        f2 -= f2 % f5;
        f3 -= f3 % f5;
        return new float[]{f2, f3};
    }

    public static float randomFloat(float f2, float f3) {
        if (f2 == f3 || f3 - f2 <= 0.0f) {
            return f2;
        }
        return (float)((double)f2 + (double)(f3 - f2) * Math.random());
    }
}