package com.mrzak34.thunderhack.modules.combat;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.DiscordWebhook;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Parent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.ExplosionBuilder;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.phobos.IEntityLivingBase;
import com.mrzak34.thunderhack.util.rotations.AdvancedCast;
import com.mrzak34.thunderhack.util.rotations.CastHelper;
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
        None, Default/*, Silent*/;
    }
    public enum PointsMode {
        Distance, Angle
    }
    public enum TimingMode {
        Default, Old
    }

    /*-------------   AntiCheat  ----------*/
    private final Setting<rotmod> rotation = register(new Setting("Rotation", rotmod.Matrix));//(antiCheat);
    public final Setting<PointsMode> pointsMode = register(new Setting("PointsMode", PointsMode.Distance));
    public final Setting<TimingMode> timingMode = register(new Setting("Timing", TimingMode.Default));
    public final Setting<Integer> minCPS = register(new Setting("MinCPS", 10, 1, 20,v -> timingMode.getValue() == TimingMode.Old));//(antiCheat);
    public final Setting<Integer> maxCPS = register(new Setting("MaxCPS", 12, 1, 20,v -> timingMode.getValue() == TimingMode.Old));//(antiCheat);
    public final Setting<Boolean> rtx = register(new Setting<>("RTX", true));//(antiCheat);
    public final Setting<Boolean> ignoreWalls = register(new Setting<>("Ignore Walls", true));//(antiCheat);
    public final Setting<Float> rotateDistance = register(new Setting("RotateDistance", 1f, 0f, 5f));//(antiCheat);
    public final Setting<Float> attackDistance = register(new Setting("AttackDistance", 3.4f, 0.0f, 7.0f));//(antiCheat);
    public final Setting<Float> walldistance = register(new Setting("WallDistance", 3.6f, 0.0f, 7.0f,v-> ignoreWalls.getValue()));//(antiCheat);
    public final Setting<Integer> fov = register(new Setting("FOV", 180, 5, 180));//(antiCheat);
    public final Setting<Boolean> backTrack = register(new Setting<>("RotateToBackTrack", true));//(antiCheat);
    public final Setting<Integer> yawStep = register(new Setting("YawStep", 80, 5, 180));//(antiCheat);


    /*-------------------------------------*/


    /*-------------   Misc  ---------------*/
    public final Setting<Boolean> criticals = register(new Setting<>("Criticals", true));
    public final Setting<CritMode> critMode = register(new Setting("CritMode", CritMode.WexSide,v -> criticals.getValue()));
    public final Setting<Float> critdist = register(new Setting("FallDistance", 0.15f, 0.0f, 1.0f,v -> criticals.getValue() && critMode.getValue() == CritMode.Simple));;
    public final Setting<Boolean> criticals_autojump = register(new Setting<>("Auto Jump", true,v-> criticals.getValue()));
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
    public final Setting<Boolean> targetesp = register(new Setting<>("Target Esp", true));//(visual);
    public final Setting<ColorSetting> shitcollor = this.register(new Setting<>("TargetColor", new ColorSetting(-2009289807)));
    /*-------------------------------------*/


    public static EntityLivingBase target;

    public static double prevCircleStep, circleStep;
    public float prevAdditionYaw;
    int killz = 0;

    private final Timer oldTimer = new Timer();
    private final Timer hitttimer = new Timer();

    private int prevSlot = -1;

    private boolean swappedToAxe = false;
    private boolean swapBack = false;

    public static BackTrack.Box bestBtBox;
    public boolean thisContextRotatedBefore;

    @Override
    public void onUpdate(){
        if(target != null) {
            if (target.getHealth() <= 0) {
                if (target instanceof EntityPlayer) {
                    if (Thunderhack.moduleManager.getModuleByClass(DiscordWebhook.class).isEnabled()) {
                        ++killz;
                        DiscordWebhook.sendAuraMsg((EntityPlayer)target,killz);
                    }
                }
            } 
            if(snap.getValue()){
                if(hitttimer.getPassedTimeMs() < 100){
                    mc.player.rotationPitch = Thunderhack.rotationManager.getServerPitch();
                    mc.player.rotationYaw = Thunderhack.rotationManager.getServerYaw();
                }
            }
        }

        if (mc.player.onGround && !isInLiquid() && !mc.player.isOnLadder() && !mc.player.isInWeb && !mc.player.isPotionActive(MobEffects.SLOWNESS) && target != null && criticals_autojump.getValue()) {
            mc.player.jump();
        }
    }

    public static boolean isInLiquid() {
        return mc.player.isInWater() || mc.player.isInLava();
    }



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

        boolean shieldDesyncActive = shieldDesync.getValue();
        if (shieldDesyncOnlyOnAura.getValue() && target == null) {
            shieldDesyncActive = false;
        }
        if (isActiveItemStackBlocking(mc.player, 4 + new Random().nextInt(4)) && shieldDesyncActive && mc.player.isHandActive()) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }
        if (target != null) {
            if (!isEntityValid(target)) {
                target = null;
            }
        }
        if (target == null) {
            target = findTarget();
            if(prevSlot != -1){
                mc.player.connection.sendPacket(new CPacketHeldItemChange(prevSlot));
                prevSlot = -1;
            }
        }

        if (Crystalsss.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (getBestHitbox(entity) != null && needExplosion(entity.getPositionVector())) {
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
            return;
        }
        if (!weaponOnly()) {
            return;
        }


        thisContextRotatedBefore = false;
        attack(target);
        if (!thisContextRotatedBefore) {
            rotate(target, false);
        }
        if (targetesp.getValue()) {
            prevCircleStep = circleStep;
            circleStep += 0.15;
        }
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

                if(alpha)
                    GL11.glEnable(GL11.GL_ALPHA_TEST);

                if(depth)
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                GL11.glShadeModel(GL11.GL_FLAT);

                if(!blend)
                    GL11.glDisable(GL11.GL_BLEND);
                if(cullface)
                    GL11.glEnable(GL11.GL_CULL_FACE);

                GL11.glPopMatrix();
                GlStateManager.resetColor();
            }
        }
    }


    public boolean weaponOnly() {
        if (!weaponOnly.getValue()) {
            return true;
        }
        return (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe);
    }

    @Override
    public void onDisable(){
        target = null;
        if(jboost && mc.player.isPotionActive(MobEffects.STRENGTH)){
            mc.player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(8)), nig, 1));
        }
    }

    public void attack(Entity base) {
        if (base instanceof EntityEnderCrystal || canAttack()) {
            if (getBestHitbox(base) != null) {
                boolean crystal = base instanceof EntityEnderCrystal;
                if (!crystal)
                    rotate(base, true);
                if (
                        !rtx.getValue()
                        || (AdvancedCast.getMouseOver(base, Thunderhack.rotationManager.getServerYaw(), Thunderhack.rotationManager.getServerPitch(), attackDistance.getValue(), ignoreWalls(base)) == base)
                        || (crystal && mc.player.getDistance(base) <= 4.5)
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
                        Command.sendMessage("Attacked with delay: " + hitttimer.getPassedTimeMs());
                    }
                    hitttimer.reset();
                    mc.player.swingArm(offhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

                    if (InventoryUtil.getBestAxe() >= 0 && shieldBreaker.getValue() && base instanceof EntityPlayer && isActiveItemStackBlocking((EntityPlayer) base, 1)) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getBestAxe()));
                        shieldBreaker((EntityPlayer) base);
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
        float f1 = (float)(mc.player.getPositionEyes(1).y - box.getPosition().y); //очень тупо
        float f2 = (float)(mc.player.posZ - box.getPosition().z);
        return (f * f + f1 * f1 + f2 * f2);
    }
    public float getDistanceBTPoint(Vec3d point) {
        float f = (float)(mc.player.posX - point.x);
        float f1 = (float)(mc.player.getPositionEyes(1).y - point.y);
        float f2 = (float)(mc.player.posZ - point.z);
        return (f * f + f1 * f1 + f2 * f2);
    }

    public void shieldBreaker(EntityPlayer base) {
        mc.playerController.attackEntity(mc.player, base);
        mc.player.swingArm(EnumHand.MAIN_HAND);
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
        boolean reasonForCancelCritical =
                mc.player.isPotionActive(MobEffects.SLOWNESS)
                || mc.player.isOnLadder()
                || (isInLiquid())
                || mc.player.isInWeb
                || (smartCrit.getValue() && (isCrystalNear() || (!criticals_autojump.getValue() && !mc.gameSettings.keyBindJump.isKeyDown())));


        if(timingMode.getValue() == TimingMode.Default) {
            if (getCooledAttackStrength() < 0.9) {
                return false;
            }
        } else {
            final int CPS = (int) MathUtil.random(minCPS.getValue(), maxCPS.getValue());
            if (!oldTimer.passedMs((long) ((1000 + (MathUtil.random(1, 50) - MathUtil.random(1, 60) + MathUtil.random(1, 70))) / CPS))) {
                return false;
            }
        }




        if(target != null){
            if(mc.player.getDistanceSq(target) > attackDistance.getValue() * attackDistance.getValue()){
                return  false;
            }
        }


        if( criticals.getValue() && watercrits.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() instanceof BlockLiquid && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock() instanceof BlockAir && mc.player.fallDistance >= 0.08f)){
            return true;
        }

        if(criticals.getValue() && !reasonForCancelCritical) {
            if(critMode.getValue() == CritMode.WexSide) {
                if ((int) mc.player.posY != (int) Math.ceil(mc.player.posY) && mc.player.onGround && isBlockAboveHead()) {
                    return true;
                }
                return !mc.player.onGround && mc.player.fallDistance > 0;
            } else
            if(critMode.getValue() == CritMode.Simple) {
                boolean onFall = isBlockAboveHead() ? mc.player.fallDistance > 0 : mc.player.fallDistance >= critdist.getValue();
                return onFall && !mc.player.onGround;
            }
        }
        oldTimer.reset();
        return true;
    }



    private float getCooledAttackStrength() {
        return clamp(((float)  ((IEntityLivingBase) mc.player).getTicksSinceLastSwing()) / getCooldownPeriod(), 0.0F, 1.0F);
    }
    public float getCooldownPeriod() {
        return (float)(1.0 / mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * ( Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).isOn() ? 20f * Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).speed.getValue() : 20.0) );
    }



    private boolean isCrystalNear() {
        EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.loadedEntityList.stream()
                .filter(e -> (e instanceof EntityEnderCrystal && mc.player.getDistance(e) <= 6))
                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                .orElse(null);
        if (crystal != null) {
            return true;
        }
        return false;
    }




    public static boolean isBlockAboveHead() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ + 0.3, mc.player.posX + 0.3, mc.player.posY + (!mc.player.onGround ? 1.5 : 2.5),
                mc.player.posZ - 0.3);
        return !mc.world.getCollisionBoxes(mc.player, axisAlignedBB).isEmpty();
    }

    public EntityLivingBase findTarget() {
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityLivingBase && isEntityValid((EntityLivingBase) entity)) {
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


    public boolean isEntityValid(EntityLivingBase entity) {
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
        if (!ignoreWalls(entity)) {
            return getBestHitbox(entity) != null;
        } else
            return !(entity.getDistanceSq(mc.player) > Math.pow((attackDistance.getValue() + rotateDistance.getValue()),2) );
    }

    public Vec3d getBestHitbox(Entity target) {
        if (mc.player.getDistanceSq(target) > Math.pow((attackDistance.getValue() + rotateDistance.getValue()),2)) {
            return null;
        }
        BackTrack bt = Thunderhack.moduleManager.getModuleByClass(BackTrack.class);

        if(!backTrack.getValue()
                || (mc.player.getDistanceSq(target) <= Math.pow(attackDistance.getValue(),2) )
                || bt.isOff()
                || !(target instanceof EntityPlayer)
                || (backTrack.getValue() && bt.entAndTrail.get(target) == null)
                || (backTrack.getValue() && bt.entAndTrail.get(target) != null  && bt.entAndTrail.get(target).size() == 0) ) {


            Vec3d head = target.getPositionVector().add(0, 1.6f, 0);
            Vec3d chest = target.getPositionVector().add(0,  0.8f, 0);
            Vec3d legs = target.getPositionVector().add(0, 0.225f, 0);
            ArrayList<Vec3d> points = new ArrayList<>(Arrays.asList(head, chest, legs));


            points.removeIf(point -> !isHitBoxVisible(target, point, (attackDistance.getValue() + rotateDistance.getValue())));

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
            /*
            Command.sendMessage("1   " +  getDistanceFromHead(head));
            Command.sendMessage("2   " +  getDistanceFromHead(chest));
            Command.sendMessage("3   " +  getDistanceFromHead(legs));
            Command.sendMessage("final   " +  getDistanceFromHead(best_point));
             */

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
                Vec3d head = best_box.getPosition().add(0, 1.6f, 0);
                Vec3d chest = best_box.getPosition().add(0,  0.8f, 0);
                Vec3d legs = best_box.getPosition().add(0, 0.225f, 0);

                ArrayList<Vec3d> points = new ArrayList<>(Arrays.asList(head, chest, legs));


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
        if (input instanceof EntityEnderCrystal) {
            return true;
        }
        BlockPos pos = new BlockPos(mc.player.posX, Thunderhack.positionManager.getY(), mc.player.posZ);
        if (mc.world.getBlockState(pos).getMaterial() != Material.AIR) {
            return true;
        }

        if(ignoreWalls.getValue()){
            return mc.player.getDistanceSq(input) <= walldistance.getValue()*walldistance.getValue();
        }

        return ignoreWalls.getValue();
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

    public boolean isHitBoxVisible(Entity target, Vec3d vector, double dst) {
        return AdvancedCast.getMouseOver(target,getRotationForCoord(vector).x,getRotationForCoord(vector).y, dst, !ignoreWalls(target)) == target;
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
        thisContextRotatedBefore = true;
        Vec3d bestHitbox = getBestHitbox(base);
        if (bestHitbox == null) {
            bestHitbox = base.getPositionEyes(1);
        }


        double x =  (bestHitbox.x - mc.player.posX);
        double y =  bestHitbox.y - mc.player.getPositionEyes(1).y;
        double z =  bestHitbox.z - mc.player.posZ;
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

                    float var32 = MathHelper.clamp(absoluteYaw + randomize, -60.0F + randomizeClamp, 60.0F + randomizeClamp);
                    float var33 = MathHelper.clamp(pitchDelta + randomize, (-((sanik) ? 13 : 45)), (sanik) ? 13 : 45);

                    float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? var32 : -var32);
                    float newPitch  = MathHelper.clamp(Thunderhack.rotationManager.getServerPitch() + var33 / (sanik ? 4.0F : 2.0F), -90.0F, 90.0F);

                    float var34 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double var35 = Math.pow((double)var34, 3.0) * 8.0;
                    double var37 = var35 * 0.15000000596046448;

                    newYaw = (float) (newYaw - (newYaw - Thunderhack.rotationManager.getServerYaw()) % var37);
                    newPitch = (float)(newPitch - (newPitch - Thunderhack.rotationManager.getServerPitch()) % var37);


                    mc.player.rotationYaw = newYaw;
                    mc.player.rotationPitch = newPitch;
                    mc.player.rotationYawHead = newYaw;
                    mc.player.renderYawOffset = newYaw;
                    break;
                }

                case FunnyGame: {
                    float[] ncp = SilentRotaionUtil.calcAngle(getBestHitbox(base));
                    if(ncp != null) {
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
