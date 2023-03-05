package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class AimAssist extends Module{

    public AimAssist() {
        super("AimAssist", "AimAssist", Category.COMBAT);
    }


    private Setting<sortEn> sort = register(new Setting("TargetMode", sortEn.Distance));

    public enum sortEn {
        Distance, HigherArmor, BlockingStatus,LowestArmor, Health,Angle,HurtTime;
    }

    public Setting<Boolean> players = register(new Setting<>("Players",  true));
    public static EntityLivingBase target;

    public Setting<Float> strength = this.register ( new Setting <> ( "Strength", 40.0f, 1.0f, 50.0f ) );
    public Setting<Float> range = this.register ( new Setting <> ( "Range", 6.0f, 0.1f, 10.0f ) );

    public Setting<Boolean> dead = register(new Setting<>("Dead",  false));
    public Setting<Boolean> invisibles = register(new Setting<>("Invisibles",  false));
    public Setting<Boolean> teams = register(new Setting<>("Teams",  true));
    public Setting<Boolean> nonPlayers = register(new Setting<>("NonPlayerslayers",  true));
    public Setting<Boolean> vertical = register(new Setting<>("Vertical",  true));
    public Setting<Boolean> onlyClick = register(new Setting<>("Clicking",  true));

    public Setting<Float> fov = register(new Setting("FOV", 180.0f, 5.0f, 180.0f));

    public static boolean canSeeEntityAtFov(Entity entityLiving, float scope) {
        double diffZ = entityLiving.posZ - mc.player.posZ;
        double diffX = entityLiving.posX - mc.player.posX;
        float yaw = (float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        double difference = angleDifference(yaw, mc.player.rotationYaw);
        return difference <= (double)scope;
    }

    public static double angleDifference(double a, double b) {
        float yaw360 = (float)(Math.abs(a - b) % 360.0);
        if (yaw360 > 180.0f) {
            yaw360 = 360.0f - yaw360;
        }
        return yaw360;
    }

    public static int deltaX, deltaY;

    @SubscribeEvent
    public void onPreMotion(EventPreMotion event) {

        target = getClosest(range.getValue());

        final float s = (float) (strength.getMax() - strength.getValue()) + 1;

        if (target == null || !mc.player.canEntityBeSeen(target)) {
            deltaX = deltaY = 0;
            return;
        }

        final float[] rotations = getRotations();

        final float targetYaw = (float) (rotations[0] + Math.random());
        final float targetPitch = (float) (rotations[1] + Math.random());

        final float niggaYaw = (targetYaw - mc.player.rotationYaw) / Math.max(2, s);
        final float niggaPitch = (targetPitch - mc.player.rotationPitch) / Math.max(2, s);

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 8.0F;

        deltaX = Math.round(niggaYaw / gcd);

        if (vertical.getValue()) deltaY = Math.round(niggaPitch / gcd);
        else deltaY = 0;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if(target == null){
            return;
        }

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 8.0F;

        int i = mc.gameSettings.invertMouse ? -1 : 1;
        float f2 = (float) (this.mc.mouseHelper.deltaX + (deltaX - this.mc.mouseHelper.deltaX)) * gcd;
        float f3 = vertical.getValue() ? (float) (this.mc.mouseHelper.deltaY - (deltaY - this.mc.mouseHelper.deltaY)) * gcd : 0;

        if (!this.onlyClick.getValue() || Mouse.isButtonDown(0) && mc.currentScreen == null) {
            mc.player.rotationYaw = mc.player.rotationYaw + f2;
            mc.player.rotationPitch = mc.player.rotationPitch + f3 * (float) i;
        }
    }

    @Override
    public void onDisable() {
        deltaX = 0;
        deltaY = 0;
    }

    private EntityLivingBase getClosest(final double range) {
        if (mc.world == null) return null;

        double dist = range;
        EntityLivingBase target = null;

        for (final Entity entity : mc.world.loadedEntityList) {

            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase livingBase = (EntityLivingBase) entity;

                if (canAttack(livingBase)) {
                    final double currentDist = mc.player.getDistance(livingBase);

                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = livingBase;
                    }
                }
            }
        }

        return target;
    }

    private boolean canAttack(final EntityLivingBase player) {
        if (player instanceof EntityPlayer && !players.getValue()) {
            return false;
        }

        if(!canSeeEntityAtFov(player,fov.getValue() *2)){
            return false;
        }

        if (player instanceof EntityAnimal || player instanceof EntityMob || player instanceof EntityVillager) {
            if (!nonPlayers.getValue())
                return false;
        }

        if (player.isInvisible() && !invisibles.getValue())
            return false;

        if (player.isDead && !dead.getValue())
            return false;

        if (player.isOnSameTeam(mc.player) && teams.getValue())
            return false;

        if (player.ticksExisted < 2)
            return false;

        if(Thunderhack.friendManager.isFriend(player.getName()))
            return false;

        return mc.player != player;
    }

    private float[] getRotations() {
        final double var4 = (target.posX - (target.lastTickPosX - target.posX)) + 0.01 - mc.player.posX;
        final double var6 = (target.posZ - (target.lastTickPosZ - target.posZ)) - mc.player.posZ;
        final double var8 = (target.posY - (target.lastTickPosY - target.posY)) + 0.4 + target.getEyeHeight() / 1.3 - (mc.player.posY + mc.player.getEyeHeight());

        final double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);

        float yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);

        yaw = mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
        pitch = mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch);
        final float[] rotations = new float[]{yaw, pitch};
        final float[] lastRotations = new float[]{yaw, pitch};
        final float[] fixedRotations = getFixedRotation(rotations, lastRotations);
        yaw = fixedRotations[0];
        pitch = fixedRotations[1];
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        return new float[]{yaw, pitch};
    }


    public float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final Minecraft mc = Minecraft.getMinecraft();

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 1.2F;

        final float deltaYaw = yaw - lastYaw;
        final float deltaPitch = pitch + lastPitch;

        final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        final float fixedYaw = lastYaw + fixedDeltaYaw;
        final float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[]{fixedYaw, fixedPitch};
    }
}
