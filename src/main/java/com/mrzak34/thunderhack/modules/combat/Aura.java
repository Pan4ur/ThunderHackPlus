package com.mrzak34.thunderhack.modules.combat;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.player.AutoGApple;
import com.mrzak34.thunderhack.modules.render.Search;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Parent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.ExplosionBuilder;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.phobos.IEntityLivingBase;
import com.mrzak34.thunderhack.util.rotations.CastHelper;
import com.mrzak34.thunderhack.util.rotations.RayTracingUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import javax.vecmath.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import static com.mrzak34.thunderhack.modules.funnygame.EffectsRemover.jboost;
import static com.mrzak34.thunderhack.modules.funnygame.EffectsRemover.nig;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;
import static com.mrzak34.thunderhack.util.MovementUtil.strafe;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.wrapDegrees;


public class Aura extends Module {

    public Aura() {
        super("Aura", "Запомните блядь-киллка тх не мисает-а дает шанс убежать", Category.COMBAT);
    }

    public enum rotmod {
        Matrix, AAC, FunnyGame, Matrix2, SunRise;
    }
    public enum CritMode {
        WexSide, Simple;
    }
    public enum AutoSwitch {
        None, Default;
    }
    public enum PointsMode {
        Distance, Angle
    }
    public enum TimingMode {
        Default, Old
    }

    /*-------------   AntiCheat  ----------*/
    private final Setting<rotmod> rotation = register(new Setting("Rotation", rotmod.Matrix));
    public final Setting<PointsMode> pointsMode = register(new Setting("PointsMode", PointsMode.Distance));
    public final Setting<TimingMode> timingMode = register(new Setting("Timing", TimingMode.Default));
    public final Setting<Integer> minCPS = register(new Setting("MinCPS", 10, 1, 20,v -> timingMode.getValue() == TimingMode.Old));
    public final Setting<Integer> maxCPS = register(new Setting("MaxCPS", 12, 1, 20,v -> timingMode.getValue() == TimingMode.Old));
    public final Setting<Float> rotateDistance = register(new Setting("RotateDistance", 1f, 0f, 5f));
    public final Setting<Float> attackDistance = register(new Setting("AttackDistance", 3.4f, 0.0f, 7.0f));
    public final Setting<Float> walldistance = register(new Setting("WallDistance", 3.6f, 0.0f, 7.0f));
    public final Setting<Integer> fov = register(new Setting("FOV", 180, 5, 180));
    public final Setting<Boolean> backTrack = register(new Setting<>("RotateToBackTrack", true));
    public final Setting<Integer> yawStep = register(new Setting("YawStep", 80, 5, 180));
    public final Setting<Float> hitboxScale = register(new Setting("HitBoxScale", 2.8f, 0.0f, 3.0f));
    /*-------------------------------------*/


    /*-------------   Misc  ---------------*/
    public final Setting<Boolean> criticals = register(new Setting<>("Criticals", true));
    public final Setting<CritMode> critMode = register(new Setting("CritMode", CritMode.WexSide,v -> criticals.getValue()));
    public final Setting<Float> critdist = register(new Setting("FallDistance", 0.15f, 0.0f, 1.0f,v -> criticals.getValue() && critMode.getValue() == CritMode.Simple));;
    public final Setting<Boolean> criticals_autojump = register(new Setting<>("AutoJump", true,v-> criticals.getValue()));
    public final Setting<Boolean> smartCrit = register(new Setting<>("SmartCrit", true,v-> criticals.getValue()));
    public final Setting<Boolean> watercrits = register(new Setting<>("WaterCrits", false,v-> criticals.getValue()));
    public final Setting<Boolean> weaponOnly = register(new Setting<>("WeaponOnly", true));
    public final Setting<AutoSwitch> autoswitch = register(new Setting("AutoSwitch", AutoSwitch.None));
    public final Setting<Boolean> firstAxe = register(new Setting<>("FirstAxe", false,v -> autoswitch.getValue() != AutoSwitch.None));
    public final Setting<Boolean> shieldDesync = register(new Setting<>("Shield Desync", false));
    public final Setting<Boolean> shieldDesyncOnlyOnAura = register(new Setting<>("Wait Target", true, v->shieldDesync.getValue()));
    public final Setting<Boolean> clientLook = register(new Setting<>("ClientLook", false));
    public final Setting<Boolean> snap = register(new Setting<>("Snap", false));
    public final Setting<Boolean> shieldBreaker = register(new Setting<>("ShieldBreaker", true));
    public final Setting<Boolean> offhand = register(new Setting<>("OffHandAttack", false));
    public final Setting<Boolean> teleport = register(new Setting<>("TP", false));
    public final Setting<Float> tpY = register(new Setting("TPY", 3f, -5.0f, 5.0f,v-> teleport.getValue()));
    public final Setting<Boolean> Debug = register(new Setting<>("HitsDebug", false));
    /*-------------------------------------*/


    /*-------------   Targets  ------------*/
    public final  Setting<Parent> targets = register(new Setting<>("Targets", new Parent(false)));
    public final Setting<Boolean> Playersss = register(new Setting<>("Players", true)).withParent(targets);
    public final Setting<Boolean> Mobsss = register(new Setting<>("Mobs", true)).withParent(targets);
    public final Setting<Boolean> Animalsss = register(new Setting<>("Animals", true)).withParent(targets);
    public final Setting<Boolean> Villagersss = register(new Setting<>("Villagers", true)).withParent(targets);
    public final Setting<Boolean> Slimesss = register(new Setting<>("Slimes", true)).withParent(targets);
    public final Setting<Boolean> Crystalsss = register(new Setting<>("Crystals", true)).withParent(targets);
    public final Setting<Boolean> ignoreNaked = register(new Setting<>("IgnoreNaked", false)).withParent(targets);
    public final Setting<Boolean> ignoreInvisible = register(new Setting<>("IgnoreInvis", false)).withParent(targets);
    public final Setting<Boolean> ignoreCreativ = register(new Setting<>("IgnoreCreativ", true)).withParent(targets);
    /*-------------------------------------*/


    /*-------------   Visual  -------------*/
    public final Setting<Boolean> RTXVisual = register(new Setting<>("RTXVisual", false));
    public final Setting<Boolean> targetesp = register(new Setting<>("Target Esp", true));//(visual);
    public final Setting<ColorSetting> shitcollor = this.register(new Setting<>("TargetColor", new ColorSetting(-2009289807)));
    /*-------------------------------------*/


    public static EntityLivingBase target;
    private float prevCircleStep, circleStep, prevAdditionYaw;
    private final Timer oldTimer = new Timer();
    private final Timer hitttimer = new Timer();
    private boolean swappedToAxe, swapBack, rotatedBefore;
    public static BackTrack.Box bestBtBox;
    public static int CPSLimit;
    private Vec3d last_best_vec;


    @SubscribeEvent
    public void onRotate(EventPreMotion e){
        if(firstAxe.getValue() && hitttimer.passedMs(3000) && InventoryUtil.getBestAxe() != -1){
            if(autoswitch.getValue() == AutoSwitch.Default){
                mc.player.inventory.currentItem = InventoryUtil.getBestAxe();
                swappedToAxe = true;
            }
        } else {
            if(autoswitch.getValue() == AutoSwitch.Default){
                if(InventoryUtil.getBestSword() != -1){
                    mc.player.inventory.currentItem = InventoryUtil.getBestSword();
                } else if(InventoryUtil.getBestAxe() != -1){
                    mc.player.inventory.currentItem = InventoryUtil.getBestAxe();
                }
            }
        }

        if (CPSLimit > 0) CPSLimit--;

        boolean shieldDesyncActive = shieldDesync.getValue();
        if (shieldDesyncOnlyOnAura.getValue() && target == null) {
            shieldDesyncActive = false;
        }
        if (isActiveItemStackBlocking(mc.player, 4 + new Random().nextInt(4)) && shieldDesyncActive && mc.player.isHandActive()) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }
        if (target != null) {
            if (!isEntityValid(target, false)) {
                target = null;
            }
        }
        if (Crystalsss.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (getVector(entity) != null && needExplosion(entity.getPositionVector())) {
                        if(oldTimer.passedMs(100)) {
                            attack(entity);
                            oldTimer.reset();
                        }
                        return;
                    }
                }
            }
        }

        if (target == null) {
            target = findTarget();
        }

        if (target == null || mc.player.getDistanceSq(target) > attackDistance.getPow2Value()) {
            BackTrack bt = Thunderhack.moduleManager.getModuleByClass(BackTrack.class);
            if(bt.isOn() && backTrack.getValue()){
                float best_distance = 100;
                for(EntityPlayer BTtarget : mc.world.playerEntities) {
                    if(mc.player.getDistanceSq(BTtarget) > 100) continue;
                    if(!isEntityValid(BTtarget, true)) continue;
                    if(bt.entAndTrail.get(BTtarget) == null) continue;
                    if(bt.entAndTrail.get(BTtarget).size() == 0) continue;
                    for (BackTrack.Box box : bt.entAndTrail.get(BTtarget)) {
                        if(getDistanceBT(box) < best_distance){
                            best_distance = getDistanceBT(box);
                            if(target != null && best_distance < mc.player.getDistance(target)){
                                target = BTtarget;
                            } else if(target == null && best_distance < attackDistance.getPow2Value()){
                                target = BTtarget;
                            }
                        }
                    }
                }
            }
        }

        if (target == null){
            return;
        }

        if (weaponOnly.getValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)) {
            return;
        }
        rotatedBefore = false;
        attack(target);
        if (!rotatedBefore) {
            rotate(target, false);
        }
    }


    @Override
    public void onUpdate(){
        if (mc.player.onGround && !isInLiquid() && !mc.player.isOnLadder() && !mc.player.isInWeb && !mc.player.isPotionActive(MobEffects.SLOWNESS) && target != null && criticals_autojump.getValue()) {
            mc.player.jump();
        }
        if (targetesp.getValue()) {
            prevCircleStep = circleStep;
            circleStep += 0.15;
        }
        if(target != null) {
            if(snap.getValue()){
                if(hitttimer.getPassedTimeMs() < 100){
                    mc.player.rotationPitch = Thunderhack.rotationManager.getServerPitch();
                    mc.player.rotationYaw = Thunderhack.rotationManager.getServerYaw();
                }
            }
        }
    }

    public static boolean isInLiquid() {
        return mc.player.isInWater() || mc.player.isInLava();
    }

    public static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent e){
        if (targetesp.getValue()) {
            EntityLivingBase entity = Aura.target;
            if (entity != null) {
                double cs = prevCircleStep + (circleStep - prevCircleStep) * mc.getRenderPartialTicks();
                double prevSinAnim = absSinAnimation(cs - 0.15);
                double sinAnim = absSinAnimation(cs);
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosY + prevSinAnim * 1.4f;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosZ;
                double nextY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - mc.getRenderManager().renderPosY + sinAnim * 1.4f;

                GL11.glPushMatrix();

                boolean cullface = GL11.glIsEnabled(GL11.GL_CULL_FACE);
                boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
                boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
                boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
                boolean alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);


                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_ALPHA_TEST);

                GL11.glShadeModel(GL11.GL_SMOOTH);

                GL11.glBegin(GL11.GL_QUAD_STRIP);
                for (int i = 0; i <= 360; i++) {
                    int clr = shitcollor.getValue().getColor();
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));
                    GL11.glColor4f(red, green, blue, 0.6F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                    GL11.glColor4f(red, green, blue, 0.01F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, y, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }

                GL11.glEnd();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                for (int i = 0; i <= 360; i++) {
                    int clr = shitcollor.getValue().getColor();
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));
                    GL11.glColor4f(red, green, blue, 0.8F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }
                GL11.glEnd();

                if(!cullface)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);

                if(texture)
                    GL11.glEnable(GL11.GL_TEXTURE_2D);


                if(depth)
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                GL11.glShadeModel(GL11.GL_FLAT);

                if(!blend)
                    GL11.glDisable(GL11.GL_BLEND);
                if(cullface)
                    GL11.glEnable(GL11.GL_CULL_FACE);
                if(alpha)
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glPopMatrix();
                GlStateManager.resetColor();


                if(RTXVisual.getValue()){
                    GlStateManager.pushMatrix();
                    Vec3d eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(mc.player.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.player.rotationYaw));
                    for(Vec3d point : RayTracingUtils.getHitBoxPoints(target.getPositionVector(),hitboxScale.getValue()/10f)){
                        if(!isPointVisible(target, point, (attackDistance.getValue() + rotateDistance.getValue()))){
                            Search.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, point.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), point.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), point.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xEA6E6E6E, true).getRGB());
                        } else {
                            Search.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, point.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), point.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), point.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xC800802D, true).getRGB());
                        }
                    }
                    if (last_best_vec != null){
                        Search.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, last_best_vec.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), last_best_vec.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), last_best_vec.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xEA00FF58, true).getRGB());
                    }
                    GlStateManager.popMatrix();
                    GlStateManager.resetColor();
                }
            }
        }
    }

    @Override
    public void onDisable(){
        target = null;
        if(jboost && mc.player.isPotionActive(MobEffects.STRENGTH)){
            mc.player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(8)), nig, 1));
        }
    }

    public boolean isPointVisible(Entity target, Vec3d vector, double dst) {
        return RayTracingUtils.getPointedEntity(getRotationForCoord(vector), dst, !ignoreWalls(target), target) == target;
    }

    public void attack(Entity base) {
        if (base instanceof EntityEnderCrystal || canAttack()) {
            if (getVector(base) != null) {
                rotate(base, true);
                if (
                        (RayTracingUtils.getMouseOver(base, Thunderhack.rotationManager.getServerYaw(), Thunderhack.rotationManager.getServerPitch(), attackDistance.getValue(), ignoreWalls(base)) == base)
                        || (base instanceof EntityEnderCrystal && mc.player.getDistanceSq(base) <= 20)
                        || (backTrack.getValue() && bestBtBox != null)
                ) {
                    if(teleport.getValue()){
                        mc.player.setPosition(base.posX, base.posY + tpY.getValue(), base.posZ);
                    }

                    boolean blocking = mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().getItemUseAction(mc.player.getActiveItemStack()) == EnumAction.BLOCK;
                    if (blocking) {
                        mc.playerController.onStoppedUsingItem(mc.player);
                    }
                    boolean needSwap = false;

                    if (mc.player.isSprinting()) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SPRINTING));
                        needSwap = true;
                    }

                    mc.playerController.attackEntity(mc.player, base);
                    if(Debug.getValue()){
                        if(target != null && last_best_vec != null) {
                            Command.sendMessage("Attacked with delay: " + hitttimer.getPassedTimeMs() + " | Distance to target: " + mc.player.getDistance(target) + " | Distance to best point: " + mc.player.getDistance(last_best_vec.x, last_best_vec.y, last_best_vec.z));
                        }
                    }
                    hitttimer.reset();
                    mc.player.swingArm(offhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

                    if (InventoryUtil.getBestAxe() >= 0 && shieldBreaker.getValue() && base instanceof EntityPlayer && isActiveItemStackBlocking((EntityPlayer) base, 1)) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getBestAxe()));
                        mc.playerController.attackEntity(mc.player, (EntityPlayer) base);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.resetCooldown();
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    }

                    if (blocking) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
                    }
                    if (needSwap) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SPRINTING));
                    }
                    if(swappedToAxe){
                        swapBack = true;
                        swappedToAxe = false;
                    }
                    CPSLimit = 10;
                }
            }
        }

    }


    @SubscribeEvent
    public void onPostAttack(EventPostMotion e){
        if(firstAxe.getValue() && InventoryUtil.getBestSword() != -1 && swapBack){
            if(autoswitch.getValue() == AutoSwitch.Default){
                mc.player.inventory.currentItem = InventoryUtil.getBestSword();
                swapBack = false;
            }
        }
        if (clientLook.getValue()) {
            mc.player.rotationYaw = Thunderhack.rotationManager.getServerYaw();
            mc.player.rotationPitch = Thunderhack.rotationManager.getServerPitch();
        }
    }


    public float getDistanceBT(BackTrack.Box box) {
        float f = (float)(mc.player.posX - box.getPosition().x);
        float f1 = (float)(mc.player.getPositionEyes(1).y - box.getPosition().y);
        float f2 = (float)(mc.player.posZ - box.getPosition().z);
        return (f * f + f1 * f1 + f2 * f2);
    }
    public float getDistanceBTPoint(Vec3d point) {
        float f = (float)(mc.player.posX - point.x);
        float f1 = (float)(mc.player.getPositionEyes(1).y - point.y);
        float f2 = (float)(mc.player.posZ - point.z);
        return (f * f + f1 * f1 + f2 * f2);
    }


    public boolean isNakedPlayer(EntityLivingBase base) {
        if (!(base instanceof EntityOtherPlayerMP)) {
            return false;
        }
        return base.getTotalArmorValue() == 0;
    }

    public boolean isInvisible(EntityLivingBase base) {
        if (!(base instanceof EntityOtherPlayerMP)) {
            return false;
        }
        return base.isInvisible();
    }

    public boolean needExplosion(Vec3d position) {
        ExplosionBuilder builder = new ExplosionBuilder(mc.world, null, position.x, position.y, position.z, 6);
        boolean needExplosion = false;
        for (Entry<EntityPlayer, Float> entry : builder.damageMap.entrySet()) {
            if (Thunderhack.friendManager.isFriend(entry.getKey().getName()) && entry.getValue() > entry.getKey().getHealth()) {
                return false;
            }
            if (entry.getKey() == mc.player && entry.getValue() > 25) {
                return false;
            }
            if (entry.getValue() > 35) {
                needExplosion = true;
            }
        }
        return needExplosion;
    }



    public boolean canAttack() {
        boolean reasonForCancelCritical = mc.player.isPotionActive(MobEffects.SLOWNESS) || mc.player.isOnLadder() || (isInLiquid()) || mc.player.isInWeb || (smartCrit.getValue() && (isCrystalNear() || (!criticals_autojump.getValue() && !mc.gameSettings.keyBindJump.isKeyDown())));

        if(timingMode.getValue() == TimingMode.Default) {
            if(CPSLimit > 0) return false;
            if(mc.player.getCooledAttackStrength(1.5f) <= 0.93) return false;
        } else {
            if (!oldTimer.passedMs((long) ((1000 + (MathUtil.random(1, 50) - MathUtil.random(1, 60) + MathUtil.random(1, 70))) / (int) MathUtil.random(minCPS.getValue(), maxCPS.getValue())))) {
                return false;
            }
        }

        if(last_best_vec != null){
            if(mc.player.getDistanceSq(last_best_vec.x,last_best_vec.y,last_best_vec.z) > attackDistance.getPow2Value()){
                return false;
            }
        }

        if (criticals.getValue() && watercrits.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() instanceof BlockLiquid && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock() instanceof BlockAir)){
           return mc.player.fallDistance >= 0.08f;
        }

        if(criticals.getValue() && !reasonForCancelCritical) {
            if(critMode.getValue() == CritMode.WexSide) {
                if ((int) mc.player.posY != (int) Math.ceil(mc.player.posY) && mc.player.onGround && isBlockAboveHead()) {
                    return true;
                }
                return !mc.player.onGround && mc.player.fallDistance > 0.08;
            } else if(critMode.getValue() == CritMode.Simple) {
                return (isBlockAboveHead() ? mc.player.fallDistance > 0 : mc.player.fallDistance >= critdist.getValue()) && !mc.player.onGround;
            }
        }
        oldTimer.reset();
        return true;
    }


    private float getCooledAttackStrength() {
        return clamp(((float)  ((IEntityLivingBase) mc.player).getTicksSinceLastSwing()  + 1.5f ) / getCooldownPeriod(), 0.0F, 1.0F);
    }
    public float getCooldownPeriod() {
        return (float)(1.0 / mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * (20f * Thunderhack.TICK_TIMER));
    }


    private boolean isCrystalNear() {
        EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.loadedEntityList.stream().filter(e -> (e instanceof EntityEnderCrystal && mc.player.getDistance(e) <= 6)).min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);
        return crystal != null;
    }


    public static boolean isBlockAboveHead() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ + 0.3, mc.player.posX + 0.3, mc.player.posY + (!mc.player.onGround ? 1.5 : 2.5), mc.player.posZ - 0.3);
        return !mc.world.getCollisionBoxes(mc.player, axisAlignedBB).isEmpty();
    }

    public EntityLivingBase findTarget() {
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityLivingBase && isEntityValid((EntityLivingBase) entity, false)) {
                targets.add((EntityLivingBase) entity);
            }
        }
        targets.sort((e1, e2) -> {
            int dst1 = (int) (mc.player.getDistance(e1) * 1000);
            int dst2 = (int) (mc.player.getDistance(e2) * 1000);
            return dst1 - dst2;
        });
        return targets.isEmpty() ? null : targets.get(0);
    }


    public boolean isEntityValid(EntityLivingBase entity, boolean backtrack) {
        if (ignoreNaked.getValue()) {
            if (isNakedPlayer(entity))
                return false;
        }
        if (ignoreInvisible.getValue()) {
            if (isInvisible(entity))
                return false;
        }
        if(ignoreCreativ.getValue()) {
            if(entity instanceof EntityPlayer){
                if(((EntityPlayer) entity).isCreative()){
                    return false;
                }
            }
        }
        if (entity.getHealth() <= 0) {
            return false;
        }
        if (AntiBot.bots.contains(entity)) {
            return false;
        }
        if (!targetsCheck(entity)) {
            return false;
        }
        if(backtrack){
            return true;
        }

        if (!ignoreWalls(entity))
            return getVector(entity) != null;
        else
            return mc.player.getDistanceSq(entity) <= Math.pow((attackDistance.getValue() + rotateDistance.getValue()),2) ;
    }




    public Vec3d getVector(Entity target) {
        BackTrack bt = Thunderhack.moduleManager.getModuleByClass(BackTrack.class);
        if(!backTrack.getValue()
                || (mc.player.getDistanceSq(target) <= attackDistance.getPow2Value() )
                || bt.isOff()
                || !(target instanceof EntityPlayer)
                || (backTrack.getValue() && bt.entAndTrail.get(target) == null)
                || (backTrack.getValue() && bt.entAndTrail.get(target) != null  && bt.entAndTrail.get(target).size() == 0) ) {


            ArrayList<Vec3d> points = RayTracingUtils.getHitBoxPoints(target.getPositionVector(),hitboxScale.getValue()/10f);
            points.removeIf(point -> !isPointVisible(target, point, (attackDistance.getValue() + rotateDistance.getValue())));

            if (points.isEmpty()) {
                return null;
            }


            float best_distance = 100;
            Vec3d best_point = null;
            float best_angle = 180f;
            
            if(pointsMode.getValue() == PointsMode.Angle) {
                for (Vec3d point : points) {
                    Vector2f r = getDeltaForCoord(new Vector2f(Thunderhack.rotationManager.getServerYaw(), Thunderhack.rotationManager.getServerPitch()), point);
                    float y = Math.abs(r.y);
                    if(y < best_angle){
                        best_angle = y;
                        best_point = point;
                    }
                }
            } else {
                for (Vec3d point : points) {
                    if (getDistanceFromHead(point) < best_distance) {
                        best_point = point;
                        best_distance = getDistanceFromHead(point);
                    }
                }
            }
            last_best_vec = best_point;
            return best_point;
        } else {
            bestBtBox = null;
            float best_distance = 36;
            BackTrack.Box best_box = null;
            for(BackTrack.Box boxes : bt.entAndTrail.get(target)){
                if(getDistanceBT(boxes) < best_distance){
                    best_box = boxes;
                    best_distance = getDistanceBT(boxes);
                }
            }

            if(best_box != null){
                bestBtBox = best_box;
                ArrayList<Vec3d> points = RayTracingUtils.getHitBoxPoints(best_box.getPosition(),hitboxScale.getValue() / 10f);
                points.removeIf(point -> getDistanceBTPoint(point) > Math.pow((attackDistance.getValue() + rotateDistance.getValue()),2) );

                if (points.isEmpty()) {
                    return null;
                }


                float best_distance2 = 100;
                Vec3d best_point = null;
                float best_angle = 180f;

                if(pointsMode.getValue() == PointsMode.Angle) {
                    for (Vec3d point : points) {
                        Vector2f r = getDeltaForCoord(new Vector2f(Thunderhack.rotationManager.getServerYaw(), Thunderhack.rotationManager.getServerPitch()), point);
                        float y = Math.abs(r.y);
                        if(y < best_angle){
                            best_angle = y;
                            best_point = point;
                        }

                    }
                } else {
                    for (Vec3d point : points) {
                        if (getDistanceFromHead(point) < best_distance2) {
                            best_point = point;
                            best_distance2 = getDistanceFromHead(point);
                        }
                    }
                }

                last_best_vec = best_point;
                return best_point;
            }
        }
        return null;
    }

    public boolean targetsCheck(EntityLivingBase entity) {
        CastHelper castHelper = new CastHelper();
        if (Playersss.getValue()) {
            castHelper.apply(CastHelper.EntityType.PLAYERS);
        }
        if (Mobsss.getValue()) {
            castHelper.apply(CastHelper.EntityType.MOBS);
        }
        if (Animalsss.getValue()) {
            castHelper.apply(CastHelper.EntityType.ANIMALS);
        }
        if (Villagersss.getValue()) {
            castHelper.apply(CastHelper.EntityType.VILLAGERS);
        }
        if (entity instanceof EntitySlime) {
            return Slimesss.getValue();
        }
        return CastHelper.isInstanceof(entity, castHelper.build()) != null && !entity.isDead;
    }

    public boolean ignoreWalls(Entity input) {
        if (input instanceof EntityEnderCrystal) return true;
        if (mc.world.getBlockState(new BlockPos(Thunderhack.positionManager.getX(), Thunderhack.positionManager.getY(), Thunderhack.positionManager.getZ())).getMaterial() != Material.AIR) return true;
        return mc.player.getDistanceSq(input) <= walldistance.getPow2Value();
    }

    public static Vector2f getDeltaForCoord(Vector2f rot, Vec3d point) {
        EntityPlayerSP client = Minecraft.getMinecraft().player;
        double x = point.x - client.posX;
        double y = point.y - client.getPositionEyes(1).y;
        double z = point.z - client.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = wrapDegrees(yawToTarget - rot.x);
        float pitchDelta = (pitchToTarget - rot.y);
        return new Vector2f(yawDelta, pitchDelta);
    }


    public static Vector2f getRotationForCoord(Vec3d point) {
        double x = point.x - mc.player.posX;
        double y = point.y - mc.player.getPositionEyes(1).y;
        double z = point.z - mc.player.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        return new Vector2f(yawToTarget, pitchToTarget);
    }


    private float getDistanceFromHead(Vec3d d1) {
        double x = d1.x - mc.player.posX;
        double y = d1.y - mc.player.getPositionEyes(1).y;
        double z = d1.z - mc.player.posZ;
        return (float) (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z,2));
    }


    public static boolean isActiveItemStackBlocking(EntityPlayer other, int time) {
        if (other.isHandActive() && !other.activeItemStack.isEmpty()) {
            Item item = other.activeItemStack.getItem();
            if (item.getItemUseAction(other.activeItemStack) != EnumAction.BLOCK) {
                return false;
            } else {
                return item.getMaxItemUseDuration(other.activeItemStack) - other.activeItemStackUseCount >= time;
            }
        } else {
            return false;
        }
    }

    public enum Hitbox {
        HEAD, CHEST, LEGS
    }


    public void rotate(Entity base, boolean attackContext) {
        rotatedBefore = true;
        Vec3d bestVector = getVector(base);
        if (bestVector == null) {
            bestVector = base.getPositionEyes(1);
        }


        double x =  (bestVector.x - mc.player.posX);
        double y =  bestVector.y - mc.player.getPositionEyes(1).y;
        double z =  bestVector.z - mc.player.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));

        float yawDelta = wrapDegrees(yawToTarget - Thunderhack.rotationManager.getServerYaw());
        float pitchDelta = (pitchToTarget - Thunderhack.rotationManager.getServerPitch());


        if (yawDelta > 180) {
            yawDelta = yawDelta - 180;
        }

        int yawDeltaAbs = (int) Math.abs(yawDelta);


        if (yawDeltaAbs < fov.getValue()) {
            switch (rotation.getValue()) {
                case Matrix: {
                    float pitchDeltaAbs = Math.abs(pitchDelta);

                    float additionYaw = Math.min(Math.max(yawDeltaAbs, 1), yawStep.getValue());
                    float additionPitch = Math.max(attackContext ? pitchDeltaAbs : 1, 2);

                    if (Math.abs(additionYaw - prevAdditionYaw) <= 3.1f) {
                        additionYaw = prevAdditionYaw + 3.0f;
                    }

                    float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? additionYaw : -additionYaw);
                    float newPitch = clamp(Thunderhack.rotationManager.getServerPitch() + (pitchDelta > 0 ? additionPitch : -additionPitch), -90, 90);

                    mc.player.rotationYaw = newYaw;
                    mc.player.rotationPitch = newPitch;
                    mc.player.rotationYawHead = newYaw;
                    mc.player.renderYawOffset = newYaw;
                    prevAdditionYaw = additionYaw;
                    break;
                }
                case SunRise:
                case Matrix2: {

                    boolean sanik = rotation.getValue()  == rotmod.SunRise;

                    float absoluteYaw = MathHelper.abs(yawDelta);

                    float randomize = interpolateRandom(-2.0F, 2.0F);
                    float randomizeClamp = interpolateRandom(-5.0F, 5.0F);

                    float deltaYaw = MathHelper.clamp(absoluteYaw + randomize, -60.0F + randomizeClamp, 60.0F + randomizeClamp);
                    float deltaPitch = MathHelper.clamp(pitchDelta + randomize, (-((sanik) ? 13 : 45)), (sanik) ? 13 : 45);

                    float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? deltaYaw : -deltaYaw);
                    float newPitch  = MathHelper.clamp(Thunderhack.rotationManager.getServerPitch() + deltaPitch / (sanik ? 4.0F : 2.0F), -90.0F, 90.0F);

                    float gcdFix1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcdFix2 = Math.pow(gcdFix1, 3.0) * 8.0;
                    double gcdFix = gcdFix2 * 0.15000000596046448;

                    newYaw = (float) (newYaw - (newYaw - Thunderhack.rotationManager.getServerYaw()) % gcdFix);
                    newPitch = (float)(newPitch - (newPitch - Thunderhack.rotationManager.getServerPitch()) % gcdFix);


                    mc.player.rotationYaw = newYaw;
                    mc.player.rotationPitch = newPitch;
                    mc.player.rotationYawHead = newYaw;
                    mc.player.renderYawOffset = newYaw;
                    break;
                }

                case FunnyGame: {
                    float[] ncp = SilentRotaionUtil.calcAngle(bestVector);
                    if(ncp != null && !AutoGApple.stopAura) {
                        mc.player.rotationYaw = ncp[0];
                        mc.player.rotationPitch = ncp[1];
                        mc.player.rotationYawHead = ncp[0];
                        mc.player.renderYawOffset = ncp[0];
                    }
                    break;
                }
                case AAC: {
                    if (attackContext) {
                        int pitchDeltaAbs = (int) Math.abs(pitchDelta);
                        float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? yawDeltaAbs : -yawDeltaAbs);
                        float newPitch = clamp(Thunderhack.rotationManager.getServerPitch() + (pitchDelta > 0 ? pitchDeltaAbs : -pitchDeltaAbs), -90, 90);
                        mc.player.rotationYaw = newYaw;
                        mc.player.rotationPitch = newPitch;
                        mc.player.rotationYawHead = newYaw;
                        mc.player.renderYawOffset = newYaw;
                    }
                    break;
                }
            }

        }
    }

    public static float interpolateRandom(float var0, float var1) {
        return (float) (var0 + (var1 - var0) * Math.random());
    }
}
