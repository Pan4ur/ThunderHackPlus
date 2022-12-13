package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.DiscordWebhook;
import com.mrzak34.thunderhack.modules.misc.AntiBot;
import com.mrzak34.thunderhack.modules.render.NameTags;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.AnimationHelper;
import com.mrzak34.thunderhack.util.MathematicHelper;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.RenderHelper;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.notification.NotificationType;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.rotations.GCDFix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class OldAura extends Module {
    public OldAura() {
        super("OldAura", "старая киллка", Category.COMBAT, true, false, false);
        this.setInstance();
    }


    public static EntityPlayer target;
    public boolean killauraen = false;
    private final Timer timer = new Timer();
    private final Timer shieldtimr = new Timer();
    public boolean rotate = true;
    int easingHealth = 0;

    private static OldAura INSTANCE = new OldAura();
    private final ResourceLocation logo = new ResourceLocation("textures/logo.png");

    public Setting<Float> range = register(new Setting("Range", 3.8F, 0.1F, 7.0F));
    public Setting<Boolean> onlySharp = register(new Setting("SwordOnly", true));
    public Setting<Float> raytrace = register(new Setting("WallRange", 6.0F, 0.1F, 7.0F));
    public Setting<Boolean> onlycrits = register(new Setting("OnlyCrits", false));
    public Setting<Float> critfall = register(new Setting("FallDist", 0.4F, 0.1F, 1.0F, v -> onlycrits.getValue()));
    public Setting<SubBind> targetlock = this.register(new Setting<>("LockTarget", new SubBind(Keyboard.KEY_L)));
    public Setting<Float> fov = register(new Setting("FOV", 360.0F, 5.0F, 360.0F));
    public Setting<Boolean> antibot = register(new Setting("AntiBot", false));


    public Setting<Boolean> timrewq = register(new Setting("timer", false));

    private Setting<sortEn> sort = register(new Setting("SortMode", sortEn.Distance));
    public static ArrayList<EntityPlayer> targets = new ArrayList<>();
    public enum sortEn {
        Distance, HigherArmor,BlockingStatus,LowestArmor,HealthUp,HealthDown,HurtTime,Smart;
    }

    private Setting<targethudEn> targethudmode = register(new Setting("TargetHudMode", targethudEn.New));

    public enum targethudEn {
        None,Wint, New;
    }

    private Setting<hiten> attakmode = register(new Setting("HitMode", hiten.SunRise));
    public enum hiten {
        Normal,Packet,SunRise;
    }
    public Setting<Float> attackd = register(new Setting("SunCoolDown", 0.85F, 0.1F, 1.0F));
    public Setting<Boolean> shieldsfucker = register(new Setting("FuckShields", false));
    private Setting<rmode> rMode = register(new Setting("Rotation Mode", rmode.FunnyGame));
    private final Setting<Float> aboba = this.register(new Setting<>("HudX", 400.0f, 0.0f, 2048.0f));
    private final Setting<Float> aboba2 = this.register(new Setting<>("HudY", 400.0f, 0.0f, 2048.0f));
    public Setting<Boolean> chance = register(new Setting("RandomChance", false));
    public Setting <Integer> chanceval = this.register ( new Setting <> ( "Chance", 100, 1, 100 , v -> chance.getValue()) );
    public Setting<Boolean> owncircle = register(new Setting("Own Cirlce", true));
    public Setting<Boolean> swing = register(new Setting("Swing", true));
    public Setting<Boolean> circle = register(new Setting("Range Circle", true));
    public Setting <Integer> owncirclepoints = this.register ( new Setting <> ( "OwnCirclePoints", 16, 1, 64 , v -> owncircle.getValue()) );
    public Setting <Integer> circlepoints = this.register ( new Setting <> ( "CirclePoints", 16, 1, 64, v -> circle.getValue() ) );
    public Setting<Float> owncirclerange = register(new Setting("OwnCirlceRange", 1.8F, 0.1F, 7.0F));



    public Setting<Float> timrf = register(new Setting("rte", 0.85F, 0.1F, 10.0F));


    public enum rmode {
        FunnyGame, Client, Wint, SunRise,Predict,TenacityRand,Smooth,SmoothClient;
    }

    public static OldAura getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OldAura();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
    public void onTick() {
        doKillaura();
    }

    @Override
    public void onEnable() {
        killauraen = true;

    }
    @Override
    public void onDisable() {
        killauraen = false; target = null; Thunderhack.TICK_TIMER = 1.0f;
    }




    @Override
    public void onUpdate(){
        if (mc.currentScreen instanceof GuiGameOver && mc.player.isDead) {
            NotificationManager.publicity("KillAura","Лошара, как ты сдох?",2, NotificationType.INFO);
            this.toggle();
        }
        if(target != null){
            float cdValue = onlycrits.getValue() ? 0.95f : 1.0f;
            if (rMode.getValue() == rmode.Client) {
                if (target != null) {
                    float[] rots = getMatrixRotations(target, false);
                    Util.mc.player.rotationYaw = rots[0];
                    Util.mc.player.rotationPitch = rots[1];
                }
            }
            if (rMode.getValue() == rmode.SmoothClient) {
                float[] rotations = getRotationsToEnt(target);
                float sens = getSensitivityMultiplier();
                rotations[0] = smoothRotation(mc.player.rotationYaw, rotations[0], 360);
                rotations[1] = smoothRotation(mc.player.rotationPitch, rotations[1], 90);

                rotations[0] = Math.round(rotations[0] / sens) * sens;
                rotations[1] = Math.round(rotations[1] / sens) * sens;
                Util.mc.player.rotationYaw = rotations[0];
                Util.mc.player.rotationPitch = rotations[1];
            }
        }
    }

    public void lookAtAngles(float yaw,float pitch,EventPreMotion e){
        mc.player.rotationYaw =(yaw);
        mc.player.rotationPitch =(pitch);
    }

    float yaw2 = 0;
    float pitch2 = 0;

    int killz = 0;
    Timer whtimer = new Timer();
    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(EventPreMotion event) {

        if(timrewq.getValue())
            Thunderhack.TICK_TIMER = 1.088f;
        if(target != null) {
            if (target.getHealth() <= 0) {
                    if (Thunderhack.moduleManager.getModuleByClass(DiscordWebhook.class).isEnabled()) {
                        if(!whtimer.passedMs(1200)){
                            return;
                        }
                        ++killz;
                        DiscordWebhook.sendAuraMsg((EntityPlayer)target,killz,0,0,0,false,0,0,0f);
                        whtimer.reset();
                    }
            }

        }
            if (OldAura.target != null) {
                if (rMode.getValue() == rmode.FunnyGame) {
                    lookAtEntity(OldAura.target,event);
                }
                if (rMode.getValue() == rmode.SunRise) {

                    float[] sunriseRots = rotats(target);

                    yaw2 = GCDFix.getFixedRotation(EaseOutBack2(yaw2, sunriseRots[0], attackd.getValue() * 0.7f + 0.3f));
                    pitch2 = GCDFix.getFixedRotation(Rotate(pitch2, sunriseRots[1], 0.35f, 2.1f));
                    lookAtAngles(yaw2,pitch2,event);
                    mc.player.renderYawOffset = sunriseRots[0];
                    mc.player.rotationYawHead = sunriseRots[0];
                }
                if (rMode.getValue() == rmode.Predict) {
                    float[] rotations = getRotationsToEnt(target);
                    rotations[0] = (float) (rotations[0] + ((Math.abs(target.posX - target.lastTickPosX) - Math.abs(target.posZ - target.lastTickPosZ)) * (2 / 3)) * 2);
                    rotations[1] = (float) (rotations[1] + ((Math.abs(target.posY - target.lastTickPosY) - Math.abs(target.getEntityBoundingBox().minY - target.lastTickPosY)) * (2 / 3)) * 2);
                    mc.player.rotationYaw =(rotations[0]);
                    mc.player.rotationPitch =(rotations[1]);
                }
                float cdValue = onlycrits.getValue() ? 0.95f : 1.0f;

                if (rMode.getValue() == rmode.Wint && (Util.mc.player.getCooledAttackStrength(0.0f)) > cdValue && getRandomDouble(0.0, 100.0) <= (double) chanceval.getValue()) {
                    if (target != null) {
                        float[] rots = getMatrixRotations(target, false);
                        mc.player.rotationYaw =(rots[0]);
                        mc.player.rotationPitch =(rots[1]);
                    }
                }
                if (rMode.getValue() == rmode.TenacityRand) {
                    float[] rotations = getRotationsToEnt(target);
                    rotations[0] += randomizeFloat(1, 5);
                    rotations[1] += randomizeFloat(1, 5);
                    mc.player.rotationYaw =(rotations[0]);
                    mc.player.rotationPitch =(rotations[1]);
                }
                if (rMode.getValue() == rmode.Smooth) {
                    float[] rotations = getRotationsToEnt(target);
                    float sens = getSensitivityMultiplier();
                    rotations[0] = smoothRotation(mc.player.rotationYaw, rotations[0], 360);
                    rotations[1] = smoothRotation(mc.player.rotationPitch, rotations[1], 90);

                    rotations[0] = Math.round(rotations[0] / sens) * sens;
                    rotations[1] = Math.round(rotations[1] / sens) * sens;
                    mc.player.rotationYaw =(rotations[0]);
                    mc.player.rotationPitch =(rotations[1]);
                }
                doKillaura();
            }
    }

    public static float getSensitivityMultiplier() {
        float SENSITIVITY = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        return (SENSITIVITY * SENSITIVITY * SENSITIVITY * 8.0F) * 0.15F;
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapDegrees(to - from);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return from + f;
    }

    public static float[] rotats(EntityLivingBase entity) {

        double diffX = entity.posX - mc.player.posX;
        double diffZ = entity.posZ - mc.player.posZ;
        double diffY = entity.posY + entity.getEyeHeight() * 0.7 - (mc.player.posY + mc.player.getEyeHeight());
        if (!mc.player.canEntityBeSeen(entity)) {
            diffY = entity.posY + entity.height - (mc.player.posY + mc.player.getEyeHeight());
        }

        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);


        float yaw = (float) (((Math.atan2(diffZ, diffX) * 180 / Math.PI) - 90)) + randomizeFloat(-2,2);
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180 / 3f)) + randomizeFloat(-2,2);

        yaw = mc.player.prevRotationYaw + GCDFix.getFixedRotation(MathHelper.wrapDegrees(yaw - mc.player.rotationYaw));
        pitch = mc.player.prevRotationPitch + GCDFix.getFixedRotation(pitch - mc.player.rotationPitch);
        pitch = MathHelper.clamp(pitch, -80, 70);
        return new float[]{yaw, pitch};
    }

    public static float randomizeFloat(float min, float max) {
        return (float) (min + (max - min) * Math.random());
    }

    public static float Rotate(float from, float to, float minstep, float maxstep) {

        float f = MathHelper.wrapDegrees(to - from) * MathHelper.clamp(0.6f, 0, 1);

        if (f < 0){
            f = MathHelper.clamp(f, -maxstep, -minstep);
        }
        else {
            f = MathHelper.clamp(f, minstep, maxstep);
        }
        if(Math.abs(f) > Math.abs(to - from))
            return to;

        return from + f;
    }

    public static float EaseOutBack2(float start, float end, float value) {
        float s = 1.70158f;
        end = MathHelper.wrapDegrees(end - start);
        value = (value) - 1;
        return end * ((value) * value * ((s + 1) * value + s) + 1) + start;
    }

    public static double getRandomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 1.0);
    }

    public static float[] getMatrixRotations(Entity e, boolean oldPositionUse) {
        double diffY;
        double diffX = (oldPositionUse ? e.prevPosX : e.posX) - (oldPositionUse ? Util.mc.player.prevPosX : Util.mc.player.posX);
        double diffZ = (oldPositionUse ? e.prevPosZ : e.posZ) - (oldPositionUse ? Util.mc.player.prevPosZ : Util.mc.player.posZ);
        if (e instanceof EntityPlayer) {
            EntityPlayer EntityPlayer = (EntityPlayer)e;
            float randomed = nextFloat((float)(EntityPlayer.posY + (double)(EntityPlayer.getEyeHeight() / 1.5f)), (float)(EntityPlayer.posY + (double)EntityPlayer.getEyeHeight() - (double)(EntityPlayer.getEyeHeight() / 3.0f)));
            diffY = (double)randomed - (Util.mc.player.posY + (double)Util.mc.player.getEyeHeight());
        } else {
            diffY = (double)nextFloat((float)e.getEntityBoundingBox().minY, (float)e.getEntityBoundingBox().maxY) - (Util.mc.player.posY + (double)Util.mc.player.getEyeHeight());
        }
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI - 90.0) + nextFloat(-2.0f, 2.0f);
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI)) + nextFloat(-2.0f, 2.0f);
        yaw = Util.mc.player.rotationYaw + getFixedRotation(MathHelper.wrapDegrees(yaw - Util.mc.player.rotationYaw));
        pitch = Util.mc.player.rotationPitch + getFixedRotation(MathHelper.wrapDegrees(pitch - Util.mc.player.rotationPitch));
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        return new float[]{yaw, pitch};
    }
    public static float getFixedRotation(float rot) {
        return getDeltaMouse(rot) * getGCDValue();
    }
    public static float getGCDValue() {
        return (float)((double)getGCD() * 0.15);
    }

    public static float getGCD() {
        float f1 = (float)((double)mc.gameSettings.mouseSensitivity * 0.6 + 0.2);
        return f1 * f1 * f1 * 8.0f;
    }

    public static float getDeltaMouse(float delta) {
        return Math.round(delta / getGCDValue());
    }


    public static float nextFloat(float startInclusive, float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) {
            return startInclusive;
        }
        return (float)((double)startInclusive + (double)(endInclusive - startInclusive) * Math.random());
    }


    public static void lookAtEntity(Entity entity,EventPreMotion e) {
        float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        setPlayerRotations(angle[0], angle[1],e);
        Util.mc.player.renderYawOffset = angle[0];
        Util.mc.player.rotationYawHead = angle[0];
    }



    public static void setPlayerRotations(float yaw, float pitch,EventPreMotion e) {
        mc.player.rotationYawHead = yaw;
        mc.player.rotationYaw =(yaw);
        mc.player.rotationPitch =(pitch);
    }
    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }



    private void doKillaura() {
        if (this.onlySharp.getValue() && !EntityUtil.holdingWeapon(mc.player)) {
            target = null;
            return;
        }
        int wait = DamageUtil.getCooldownByWeapon(mc.player);

        target = getTarget();
        if (target == null)
            return;
        BreakShield(target);
        if(attakmode.getValue() != hiten.SunRise) {
            if (!this.timer.passedMs(wait))
                return;
        }


        if (Util.mc.player.fallDistance > critfall.getValue() && onlycrits.getValue() && !isBlockAboveHead()) {
            if (Util.mc.player.isInWater() && Util.mc.player.isInLava()) {
                return;
            }

            if(rMode.getValue() == rmode.Wint) {
                if(attakmode.getValue() == hiten.Packet) {
                    EntityUtil.attackEntity(target, true, this.swing.getValue());
                    this.timer.reset();
                }
                if(attakmode.getValue() == hiten.Normal) {
                    EntityUtil.attackEntity(target, false, this.swing.getValue());
                    this.timer.reset();
                }
                if(attakmode.getValue() == hiten.SunRise){
                    attackEntitySuccess(target);
                }

            } else {
                if(attakmode.getValue() == hiten.Packet) {
                    EntityUtil.attackEntity(target, true, this.swing.getValue());
                    this.timer.reset();
                }
                if(attakmode.getValue() == hiten.Normal) {
                    EntityUtil.attackEntity(target, false, this.swing.getValue());
                    this.timer.reset();
                }
                if(attakmode.getValue() == hiten.SunRise){
                    attackEntitySuccess(target);
                }

            }
        }
        if (!onlycrits.getValue() || isBlockAboveHead()) {
            if(attakmode.getValue() == hiten.Packet) {
                EntityUtil.attackEntity(target, true, this.swing.getValue());
                this.timer.reset();
            }
            if(attakmode.getValue() == hiten.Normal) {
                EntityUtil.attackEntity(target, false, this.swing.getValue());
                this.timer.reset();
            }
            if(attakmode.getValue() == hiten.SunRise){
                attackEntitySuccess(target);
            }

        }
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(OldAura.mc.player.posX - 0.3, OldAura.mc.player.posY + (double)OldAura.mc.player.getEyeHeight(), OldAura.mc.player.posZ + 0.3, OldAura.mc.player.posX + 0.3, OldAura.mc.player.posY + 2.5, OldAura.mc.player.posZ - 0.3);
        return !MovementUtil.mc.world.getCollisionBoxes(OldAura.mc.player, bb).isEmpty();
    }



    public Timer locktimer = new Timer();
    boolean looked = false;

    private EntityPlayer getTarget() {
        if (target != null) {
            if (PlayerUtils.isKeyDown(targetlock.getValue().getKey()) && !looked && locktimer.passedMs(1000)) {
                looked = true;
                if (Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isEnabled()) {
                    NotificationManager.publicity("KillAura", "Цель " + target.getName() + " захвачена!", 2, NotificationType.SUCCESS);
                } else {
                    Command.sendMessage("Цель " + target.getName() + " захвачена!");
                }
                locktimer.reset();
            }

            if (PlayerUtils.isKeyDown(targetlock.getValue().getKey()) && looked && locktimer.passedMs(1000)) {
                looked = false;
                locktimer.reset();
                if (Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isEnabled()) {
                    NotificationManager.publicity("KillAura", "Есть отставить цель!", 2, NotificationType.SUCCESS);
                } else {
                    Command.sendMessage("Есть отставить цель!");
                }
            }

            if (looked) {
                return target;
            }
        }

        if(sort.getValue() == OldAura.sortEn.Smart) {
            Entity target = null;
            double distance = this.range.getValue();
            double maxHealth = 36.0D;
            for (EntityPlayer entity : mc.world.playerEntities) {
                if(entity == null){
                    continue;
                }
                if (EntityUtil.isntValid(entity, distance))
                    continue;
                if (!mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && mc.player.getDistanceSq(entity) > MathUtil.square(this.raytrace.getValue()))
                    continue;
                if (AntiBot.getBots().contains(entity))
                    continue;
                if (entity.isCreative())
                    continue;
                if (target == null) {
                    target = entity;
                    distance = mc.player.getDistanceSq(entity);
                    maxHealth = EntityUtil.getHealth(entity);
                    continue;
                }
                if (DamageUtil.isArmorLow(entity, 18)) {
                    target = entity;
                }
                if (mc.player.getDistanceSq(entity) < distance) {
                    target = entity;
                    distance = mc.player.getDistanceSq(entity);
                    maxHealth = EntityUtil.getHealth(entity);
                }
                if (EntityUtil.getHealth(entity) < maxHealth) {
                    target = entity;
                    distance = mc.player.getDistanceSq(entity);
                    maxHealth = EntityUtil.getHealth(entity);
                }

            }
        }
        if(sort.getValue() == OldAura.sortEn.Distance){
            target = getClosest(range.getValue());
        }
        if(sort.getValue() == OldAura.sortEn.LowestArmor){
            target = getArmorLess();
        }
        if(sort.getValue() == OldAura.sortEn.HigherArmor){
            target = getArmorhigh();
        }
        if(sort.getValue() == OldAura.sortEn.HealthUp){
            target = getHealthUp();
        }
        if(sort.getValue() == OldAura.sortEn.HealthDown){
            target = getHealthDown();
        }
        if(sort.getValue() == OldAura.sortEn.HurtTime){
            target = getHurttimer();
        }
        if(sort.getValue() == OldAura.sortEn.BlockingStatus){
            if(getBloking()!=null) {
                target = getBloking();
            } else {
                target = getClosest(range.getValue());
            }
        }
        return target;
    }

    private EntityPlayer getClosest(double range) {
        targets.clear();
        double dist = range;
        EntityPlayer target = null;

        for (Entity object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) object;
                if (canAttack(player)) {
                    double currentDist = mc.player.getDistance(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                        targets.add(player);
                    }
                }
            }
        }

        return target;
    }

    private EntityPlayer getBloking() {
        targets.clear();
        EntityPlayer target = null;

        for (Entity object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) object;
                if (canAttack(player)) {
                    if (player.isActiveItemStackBlocking()) {
                        return player;
                    }
                }
            }
        }

        return target;
    }


    private EntityPlayer getArmorLess() {
        targets.clear();
        double armor = 1815;
        EntityPlayer target = null;

        for (Entity object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) object;
                if (canAttack(player)) {
                    double currentarm = DamageUtil.ChekTotalarmorDamage(player);
                    if (currentarm <= armor) {
                        armor = currentarm;
                        target = player;
                        targets.add(player);
                    }
                }
            }
        }
        return target;
    }


    private EntityPlayer getHurttimer() {
        targets.clear();
        double hurttime = 0;
        EntityPlayer target = null;

        for (Entity object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) object;
                if (canAttack(player)) {
                    double currenthurttime = player.hurtTime;
                    if (currenthurttime <= hurttime) {
                        hurttime = currenthurttime;
                        target = player;
                        targets.add(player);
                    }
                }
            }
        }
        return target;
    }

    private EntityPlayer getArmorhigh() {
        targets.clear();
        double armor = 100;
        EntityPlayer target = null;

        for (Entity object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) object;
                if (canAttack(player)) {
                    double currentarm = DamageUtil.ChekTotalarmorDamage( player);
                    if (currentarm >= armor) {
                        armor = currentarm;
                        target = player;
                        targets.add(player);
                    }
                }
            }
        }
        return target;
    }


    private EntityPlayer getHealthUp() {
        targets.clear();
        double health = 10;
        EntityPlayer target = null;

        for (Entity object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) object;
                if (canAttack(player)) {
                    double currenhealth = player.getHealth();
                    if (currenhealth >= health) {
                        health = currenhealth;
                        target = player;
                        targets.add(player);
                    }
                }
            }
        }
        return target;
    }

    private EntityPlayer getHealthDown() {
        targets.clear();
        double health = 36;
        EntityPlayer target = null;

        for (Entity object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) object;
                if (canAttack(player)) {
                    double currenhealth = player.getHealth();
                    if (currenhealth <= health) {
                        health = currenhealth;
                        target = player;
                        targets.add(player);
                    }
                }
            }
        }
        return target;
    }


    public boolean canAttack(EntityPlayer nigger){
        if(nigger == mc.player){
            return false;
        }
        if(Thunderhack.friendManager.isFriend(nigger.getName())){
            return false;
        }
        if(!canSeeEntityAtFov(nigger, fov.getValue())){
            return false;
        }

        if(AntiBot.isBot(nigger) && antibot.getValue()){
            return false;
        }

        return !(mc.player.getDistance(nigger) > range.getValue());
    }

    public String getDisplayInfo() {
        if (target != null)
            return target.getName();
        return null;
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (OldAura.target != null) {
            if(circle.getValue()) {
                RenderHelper.drawCircle3D(OldAura.target, range.getValue() - 0.00625, event.getPartialTicks(), circlepoints.getValue(), 2, new Color(0, 255, 135).getRGB());
                RenderHelper.drawCircle3D(OldAura.target, range.getValue() + 0.00625, event.getPartialTicks(), circlepoints.getValue(), 2, new Color(0, 255, 135).getRGB());
                RenderHelper.drawCircle3D(OldAura.target, range.getValue(), event.getPartialTicks(), circlepoints.getValue(), 2, -1);
            }
            if(owncircle.getValue()){
                RenderHelper.drawCircle3D(OldAura.mc.player, owncirclerange.getValue() - 0.00625, event.getPartialTicks(), owncirclepoints.getValue(), 2, PaletteHelper.astolfo(false, (int) mc.player.height).getRGB());
                RenderHelper.drawCircle3D(OldAura.mc.player, owncirclerange.getValue() + 0.00625, event.getPartialTicks(), owncirclepoints.getValue(), 2, PaletteHelper.astolfo(false, (int) mc.player.height).getRGB());
                RenderHelper.drawCircle3D(OldAura.mc.player, owncirclerange.getValue(), event.getPartialTicks(), owncirclepoints.getValue(), 2, PaletteHelper.astolfo(false, (int) mc.player.height).getRGB());
            }
        }


    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive sound) {
        if (shieldsfucker.getValue() && sound.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus sPacketEntityStatus = sound.getPacket();
            if (sPacketEntityStatus.getOpCode() == 30 && sPacketEntityStatus.getEntity(mc.world) == target) {
                NotificationManager.publicity("KillAura","успешно сломан щит " + target.getName(),2,NotificationType.SUCCESS);
            }
        }
    }



    private double scale;
    private float healthBarWidth;
    private float armorWidth;

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (OldAura.target != null) {
            if(targethudmode.getValue() == OldAura.targethudEn.Wint) {
                EntityPlayer entityPlayer = target;

                int ht = entityPlayer.hurtTime;


                int width = 60 + Util.fr.getStringWidth(target.getName());
                GL11.glPushMatrix();
                RenderUtil.drawSmoothRect(-10.0f + aboba.getValue(), 20.0f + aboba2.getValue(), 2 + width + aboba.getValue(), (target.getTotalArmorValue() != 0 ? 56.0f : 50.0f) + aboba2.getValue(), new Color(35, 35, 40, 230).getRGB());
                Util.fr.drawString(target.getName(), (int) ((int) 10.0f + aboba.getValue()), (int) ((int) 26.0f + aboba2.getValue()), 0xFFFFFF);

                Util.fr.drawStringWithShadow(MathUtil.round(target.getHealth(), 1) + " HP", 10.0f + aboba.getValue(), 35.0f + aboba2.getValue(), 0xFFFFFF);


                String status = null;
                Color statusColor = null;
                for (PotionEffect effect : entityPlayer.getActivePotionEffects()) {
                    if (effect.getPotion() == MobEffects.WEAKNESS) {
                        status = "Weakness!";
                        statusColor = new Color(135, 0, 25);
                    } else if (effect.getPotion() == MobEffects.INVISIBILITY) {
                        status = "Invisible!";
                        statusColor = new Color(90, 90, 90);
                    } else if (effect.getPotion() == MobEffects.STRENGTH) {
                        status = "Strength!";
                        statusColor = new Color(185, 65, 185);
                    }
                }

                if (status != null) {
                    Util.fr.drawString(status, (int) (8 + aboba.getValue()), (int) (19 + aboba2.getValue()), statusColor.getRGB());
                }

                try {
                    drawHead(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(target.getUniqueID()).getLocationSkin(), (int) (-8 + aboba.getValue()), (int) (22 + aboba2.getValue()));
                } catch (Exception exception) {
                    drawfakeHead((int) (-8 + aboba.getValue()), (int) (22 + aboba2.getValue()));
                }

                final ItemStack renderOffhand = target.getHeldItemOffhand().copy();
                if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
                    renderOffhand.stackSize = 1;
                }
                renderItemStack(renderOffhand, (int) (60 + aboba.getValue()), (int) (20 + aboba2.getValue()));


                RenderUtil.drawSmoothRect(-8.0f + aboba.getValue(), 44.0f + aboba2.getValue(), width + aboba.getValue(), 46.0f + aboba2.getValue(), new Color(25, 25, 35, 255).getRGB());
                this.easingHealth = (int) animation(this.easingHealth, target.getHealth() - (float) this.easingHealth, 1.0E-4f);
                this.easingHealth += (int) ((double) (target.getHealth() - (float) this.easingHealth) / Math.pow(2.0, 7.0));
                if (this.easingHealth < 0 || (float) this.easingHealth > target.getMaxHealth()) {
                    this.easingHealth = (int) target.getHealth();
                }
                if ((float) this.easingHealth > target.getHealth()) {
                    RenderUtil.drawSmoothRect(-8.0f + aboba.getValue(), 66.0f + aboba2.getValue(), ((float) this.easingHealth / target.getMaxHealth() * (float) width) + aboba.getValue(), 58.0f + aboba2.getValue(), new Color(231, 182, 0, 255).getRGB());
                }
                if ((float) this.easingHealth < target.getHealth()) {
                    RenderUtil.drawRect(((float) this.easingHealth / target.getMaxHealth() * (float) width) + aboba.getValue(), 56.0f + aboba2.getValue(), ((float) this.easingHealth / target.getMaxHealth() * (float) width) + aboba.getValue(), 58.0f + aboba2.getValue(), new Color(231, 182, 0, 255).getRGB());
                }
                RenderUtil.drawSmoothRect(-8.0f + aboba.getValue(), 44.0f + aboba2.getValue(), aboba.getValue() + (target.getHealth() / target.getMaxHealth() * (float) width), 46.0f + aboba2.getValue(), ColorUtil.getHealthColor(target).getRGB());


                if (target.getTotalArmorValue() != 0) {
                    RenderUtil.drawSmoothRect(-8.0f + aboba.getValue(), 50.0f + aboba2.getValue(), width + aboba.getValue(), 52.0f + aboba2.getValue(), new Color(25, 25, 35, 255).getRGB());
                    RenderUtil.drawSmoothRect(-8.0f + aboba.getValue(), 50.0f + aboba2.getValue(), aboba.getValue() + (DamageUtil.ChekTotalarmorDamage( OldAura.target) / 1815.0F * width), 52.0f + aboba2.getValue(), new Color(77, 128, 255, 255).getRGB());
                }


                GL11.glPopMatrix();

                if (ht >= 7) {
                    RenderUtil.drawSmoothRect(-8 + aboba.getValue(), 22 + aboba2.getValue(), 8 + aboba.getValue(), 38 + aboba2.getValue(), new Color(225, 3, 3, 166).getRGB());
                }
            } else if(targethudmode.getValue() == targethudEn.New){
                float x = aboba.getValue();
                float y = aboba2.getValue();
                if (target != null) {
                    this.scale = AnimationHelper.animation((float)this.scale, 1.0f, (float)(14.0 * deltaTime()));
                }
                else {
                    this.scale = AnimationHelper.animation((float)this.scale, 0.0f, (float)(14.0 * deltaTime()));
                }
                if (target != null) {
                    try {
                        GlStateManager.pushMatrix();

                        GL11.glTranslated((x + 50.0f), (y + 31.0f), 0.0);
                        GL11.glScaled(this.scale, this.scale, 0.0);
                        GL11.glTranslated((-(x + 50.0f)), (-(y + 31.0f)), 0.0);

                        RenderUtil.drawSmoothRect(x, y, x + 153, y + 58, new Color(17, 17, 17, 200).getRGB());
                        double healthWid = target.getHealth() / target.getMaxHealth() * 110.0f;
                        healthWid = MathHelper.clamp(healthWid, 0.0, 110.0);


                        double armorWid = DamageUtil.ChekTotalarmorDamage( OldAura.target) / 1815.0F * 110.0f;
                        armorWid = MathHelper.clamp(armorWid, 0.0, 110.0);


                        this.healthBarWidth = AnimationHelper.calculateCompensation((float)healthWid, this.healthBarWidth, (long) 5.0f, 5.0);
                        this.armorWidth = AnimationHelper.calculateCompensation((float)armorWid, armorWidth, (long) 5.0f, 5.0);


                        final String health = "" + MathematicHelper.round(target.getHealth(), 1);
                        final String arm = "" + MathematicHelper.round(DamageUtil.ChekTotalarmorDamage(OldAura.target), 1);


                        final String distance = "" + MathematicHelper.round(mc.player.getDistance(target), 1);
                        mc.fontRenderer.drawString(target.getName(), (int) (x + 42.0f), (int) (y + 6.0f), -1);
                        mc.fontRenderer.drawString("Distance: " + distance, (int) (x + 42.0f), (int) (y + 15.0f), -1);

                        mc.fontRenderer.drawString((target.getHealth() >= 3.0f) ? health : "", (int) (x + 24.0f + this.healthBarWidth), (int) (y + 24.5f), new Color(200, 200, 200).getRGB());
                        mc.fontRenderer.drawString( DamageUtil.ChekTotalarmorDamage(OldAura.target) >= 100.0f ? arm : "", (int) (x + 24.0f + armorWidth), (int) (y + 42 + 5.5f), new Color(200, 200, 200).getRGB());

                        RenderUtil.drawSmoothRect(x + 38.0f, y + 33.0f, x + 38.0f + this.healthBarWidth, y + 33.0f + 5.0f, ColorUtil.getHealthColor(target).getRGB());
                        RenderUtil.drawSmoothRect(x + 38.0f, y + 42f, x + 38.0f + this.armorWidth, y + 42 + 5.0f, new Color(0x006EFF).getRGB());

                        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, target.getHeldItem(EnumHand.OFF_HAND), (int)x + 132, (int)y + 7);
                        mc.getRenderItem().renderItemAndEffectIntoGUI(target.getHeldItem(EnumHand.OFF_HAND), (int)x + 135, (int)y + 1);
                        final float hurtPercent = getHurtPercent(target);
                        GlStateManager.pushMatrix();
                        GL11.glColor4f(1.0f, 1.0f - hurtPercent, 1.0f - hurtPercent, 1.0f);
                        try {
                            drawHead2(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(target.getUniqueID()).getLocationSkin(),(int)x + 3, (int) y + 4);
                        } catch (Exception exception) {
                            drawfakeHead2((int)x + 3, (int) y + 4);
                        }
                        GlStateManager.popMatrix();
                    }
                    catch (Exception ignored) {}
                    GlStateManager.popMatrix();
                }
            }
        }
    }
    public static double deltaTime() {
        return (System.currentTimeMillis() > 0) ? (1.0 / System.currentTimeMillis()) : 1.0;
    }


    public static float getRenderHurtTime(final EntityPlayer hurt) {
        return hurt.hurtTime - ((hurt.hurtTime != 0) ? mc.getRenderPartialTicks() : 0.0f);
    }

    public static float getHurtPercent(final EntityPlayer hurt) {
        return getRenderHurtTime(hurt) / 10.0f;
    }


    private void renderItemStack(final ItemStack stack,  final int x,  final int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        NameTags.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        NameTags.mc.getRenderItem().renderItemAndEffectIntoGUI(stack,  x,  y);
        NameTags.mc.getRenderItem().renderItemOverlays(NameTags.mc.fontRenderer,  stack,  x,  y);
        NameTags.mc.getRenderItem().zLevel = 0.0f;
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f,  0.5f,  0.5f);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f,  2.0f,  2.0f);
        GlStateManager.popMatrix();
    }

    public void drawfakeHead( int width, int height) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(logo);
        Gui.drawScaledCustomSizeModalRect(width, height, 8.0f, 8.0f, 8, 8, 16, 16, 64.0f, 64.0f);
    }

    public void drawHead(ResourceLocation skin, int width, int height) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(width, height, 8.0f, 8.0f, 8, 8, 16, 16, 64.0f, 64.0f);
    }

    public void drawfakeHead2( int width, int height) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(logo);
        Gui.drawScaledCustomSizeModalRect(width, height, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
    }

    public void drawHead2(ResourceLocation skin, int width, int height) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(width, height, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
    }




    public static float animation(float animation, float target,  float speedTarget) {
        float da = (target - animation) / Math.max((float) Minecraft.getDebugFPS(), 5.0f) * 15.0f;
        if (da > 0.0f) {
            da = Math.max(speedTarget, da);
            da = Math.min(target - animation, da);
        } else if (da < 0.0f) {
            da = Math.min(-speedTarget, da);
            da = Math.max(target - animation, da);
        }
        return animation + da;
    }

    public static boolean canSeeEntityAtFov(Entity entityLiving, float scope) {
        double diffX = entityLiving.posX - Minecraft.getMinecraft().player.posX;
        double diffZ = entityLiving.posZ - Minecraft.getMinecraft().player.posZ;
        float newYaw = (float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0D);
        double difference = angleDifference(newYaw, Minecraft.getMinecraft().player.rotationYaw);
        return difference <= (double)scope;
    }

    public static double angleDifference(double a, double b) {
        float yaw360 = (float)(Math.abs(a - b) % 360.0D);
        if (yaw360 > 180.0F) {
            yaw360 = 360.0F - yaw360;
        }

        return yaw360;
    }


    public void attackEntitySuccess(EntityLivingBase target) {
        if (target.getHealth() > 0) {
            float attackDelay = attackd.getValue();
            try {
                if (mc.player.getCooledAttackStrength(attackDelay) == 1) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                    mc.playerController.attackEntity(mc.player, rayCast(target, range.getValue()));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
            } catch (Exception ignored){

            }
        }
    }

    public static Entity rayCast(Entity entityIn, double range) {
        Vec3d vec = entityIn.getPositionVector().add(new Vec3d(0, entityIn.getEyeHeight(), 0));
        Vec3d vecPositionVector = mc.player.getPositionVector().add(new Vec3d(0, mc.player.getEyeHeight(), 0));
        AxisAlignedBB axis = mc.player.getEntityBoundingBox().expand(vec.x - vecPositionVector.x, vec.y - vecPositionVector.y, vec.z - vecPositionVector.z).expand(1, 1, 1);
        Entity entityRayCast = null;
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, axis)) {
            if (entity.canBeCollidedWith() && entity instanceof EntityLivingBase) {
                float size = entity.getCollisionBorderSize();
                AxisAlignedBB axis1 = entity.getEntityBoundingBox().expand(size, size, size);
                RayTraceResult rayTrace = axis1.calculateIntercept(vecPositionVector, vec);
                if (axis1.contains(vecPositionVector)) {
                    if (range >= 0) {
                        entityRayCast = entity;
                        range = 0;
                    }
                } else if (rayTrace != null) {
                    double dist = vecPositionVector.distanceTo(rayTrace.hitVec);
                    if (range == 0 || dist < range) {
                        entityRayCast = entity;
                        range = dist;
                    }
                }
            }
        }

        return entityRayCast;
    }


    public void BreakShield(final EntityPlayer tg) {
        if(shieldtimr.passedMs(250)) {
            if ((InventoryUtil.getAxeAtHotbar() != -1)) {
                final int item = InventoryUtil.getAxeAtHotbar();
                if (tg.isActiveItemStackBlocking() && tg.getHeldItemOffhand().getItem() instanceof ItemShield) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(item));
                    mc.playerController.attackEntity(mc.player, tg);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                }
            }
            shieldtimr.reset();
        }
    }

    private float[] getRotationsToEnt(Entity ent) {
        final double differenceX = ent.posX - mc.player.posX;
        final double differenceY = (ent.posY + ent.height) - (mc.player.posY + mc.player.height) - 0.5;
        final double differenceZ = ent.posZ - mc.player.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, mc.player.getDistance(ent)) * 180.0D
                / Math.PI);
        final float finishedYaw = mc.player.rotationYaw
                + MathHelper.wrapDegrees(rotationYaw - mc.player.rotationYaw);
        final float finishedPitch = mc.player.rotationPitch
                + MathHelper.wrapDegrees(rotationPitch - mc.player.rotationPitch);
        return new float[]{finishedYaw, -MathHelper.clamp(finishedPitch, -90, 90)};
    }
}
