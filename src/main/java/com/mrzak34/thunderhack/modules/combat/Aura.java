package com.mrzak34.thunderhack.modules.combat;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.DiscordWebhook;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.notification.NotificationType;
import com.mrzak34.thunderhack.util.AstolfoAnimation;
import com.mrzak34.thunderhack.util.DeadCodeUtils.RotateCalculator;
import com.mrzak34.thunderhack.util.DeadCodeUtils.cy_0;
import com.mrzak34.thunderhack.util.ExplosionBuilder;
import com.mrzak34.thunderhack.util.RotationHelper;
import com.mrzak34.thunderhack.util.rotations.AdvancedCast;
import com.mrzak34.thunderhack.util.rotations.CastHelper;
import com.mrzak34.thunderhack.util.rotations.RaycastHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.mrzak34.thunderhack.util.Timer;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import javax.vecmath.Vector2f;

import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import static com.mrzak34.thunderhack.modules.funnygame.EffectsRemover.jboost;
import static com.mrzak34.thunderhack.modules.funnygame.EffectsRemover.nig;

public class Aura extends Module {

    public Aura() {
        super("Aura", "AuraAura", Category.COMBAT, true, false, false);
    }


    public static Vector2f serverRotation = new Vector2f();

    public enum rotmod {
        Matrix, Nexus, FunnyGame,DeadCode;

    }


    /*-------------   AntiCheat  ----------*/
  //  public  Setting<Parent> antiCheat = this.register(new Setting<>("AntiCheat", new Parent(false)));
    private Setting<rotmod> rotation = register(new Setting("Rotation", rotmod.Matrix));//(antiCheat);
    public Setting<Boolean> fullpower = register(new Setting<>("fullpower", false));//(antiCheat);
    public Setting<Boolean> betterCrits = register(new Setting<>("BetterCrits", true));//(antiCheat);
    public Setting<Boolean> rtx = register(new Setting<>("RTX", true));//(antiCheat);
    public Setting<Boolean> ignoreWalls = register(new Setting<>("Ignore Walls", true));//(antiCheat);
    public Setting<Float> distance = register(new Setting("Distance", 3.6f, 0.0f, 7.0f));//(antiCheat);
    public Setting<Integer> fov = register(new Setting("FOV", 180, 5, 180));//(antiCheat);
    public Setting<Boolean> backtrack = register(new Setting<>("BackTrack", false));//(antiCheat);
    public Setting<Integer> btticks = register(new Setting("TrackTicks", 5, 1, 15));//(antiCheat);
    public Setting<Boolean> gappleFix = register(new Setting<>("GappleFix", true));//(antiCheat);
    /*-------------------------------------*/


    /*-------------   Misc  ---------------*/
  //  public  Setting<Parent> misc = this.register(new Setting<>("Misc", new Parent(false)));
    public Setting<Boolean> criticals_autojump = register(new Setting<>("Auto Jump", true));//(misc);
    public Setting<Boolean> nointer = register(new Setting<>("NoInteract", true));//(misc);
    public Setting<Boolean> criticals = register(new Setting<>("Criticals", true));//(misc);
    public Setting<Boolean> weaponOnly = register(new Setting<>("WeaponOnly", true));//(misc);
    public Setting<Boolean> shieldDesyncOnlyOnAura = register(new Setting<>("Wait Target", true));//(misc);
    public Setting<Boolean> shieldDesync = register(new Setting<>("Shield Desync", true));//(misc);
    public Setting<Boolean> clientLook = register(new Setting<>("ClientLook", true));//(misc);
    public Setting<Boolean> shieldBreaker = register(new Setting<>("ShieldBreaker", true));//(misc);
    public Setting<Boolean> offhand = register(new Setting<>("OffHandAttack", false));//(misc);
    public Setting<Boolean> Debug = register(new Setting<>("HitsDebug", false));//(misc);

    /*-------------------------------------*/



    /*-------------   Targets  ------------*/
   // public  Setting<Parent> targets = this.register(new Setting<>("Targets", new Parent(false)));

    public Setting<Boolean> Playersss = register(new Setting<>("Players", true));//(targets);
    public Setting<Boolean> Mobsss = register(new Setting<>("Mobs", true));//(targets);
    public Setting<Boolean> Animalsss = register(new Setting<>("Animals", true));//(targets);
    public Setting<Boolean> Villagersss = register(new Setting<>("Villagers", true));//(targets);
    public Setting<Boolean> Friendsss = register(new Setting<>("Friends", false));//(targets);
    public Setting<Boolean> Slimesss = register(new Setting<>("Slimes", true));//(targets);
    public Setting<Boolean> Crystalsss = register(new Setting<>("Crystals", true));//(targets);
    public Setting<Boolean> ignoreNaked = register(new Setting<>("IgnoreNaked", true));//(targets);
    public Setting<Boolean> ignoreInvisible = register(new Setting<>("IgnoreInvis", true));//(targets);

    /*-------------------------------------*/


    /*-------------   Visual  -------------*/
   // public  Setting<Parent> visual = this.register(new Setting<>("Visual", new Parent(false)));
    public Setting<Boolean> targetesp = register(new Setting<>("Target Esp", true));//(visual);
    public Setting<Boolean> btvisual = register(new Setting<>("BTVisual", true));//(visual);
    public final Setting<ColorSetting> shitcollor = this.register(new Setting<>("TargetColor", new ColorSetting(-2009289807)));

    /*-------------------------------------*/


    public static EntityLivingBase target;
    public static EntityLivingBase prevtarget;



    public static double prevCircleStep, circleStep;
    public static boolean hitTick;
    public float prevAdditionYaw;
    public static int minCPS;
    public static Aura instance;
    boolean btprofit = false;
    int killz = 0;
    public static boolean stopAuraRotate = false;
    private boolean isGapping = false;
    private Timer inhibit = new Timer();
    private Timer hitttimer = new Timer();
    public static double targetMaxSpeed;
    public static int misshits;
    public static int hits;
    public static int backtracketHits;
    public static int backtracketMisses;
    public static float backtrackedMaxDist;

    private Queue<Vec3d> btPositions = new LinkedList<>();



    @Override
    public void onUpdate(){
        if(target != null){
            if(prevtarget != null){
                if(target != prevtarget){
                    hits = 0;
                    misshits = 0;
                    backtracketHits = 0;
                    backtracketMisses = 0;
                    backtrackedMaxDist = 0;
                    targetMaxSpeed = 0;
                }
            }
        }

        if(target != null) {
            if (target.getHealth() <= 0) {
                if (target instanceof EntityPlayer) {
                    if (Thunderhack.moduleManager.getModuleByClass(DiscordWebhook.class).isEnabled()) {
                        ++killz;
                        DiscordWebhook.sendAuraMsg((EntityPlayer)target,killz,hits,misshits,targetMaxSpeed,backtrack.getValue(),backtracketHits,backtracketMisses,backtrackedMaxDist);
                    }
                }
            } else {
                if (target instanceof EntityPlayer) {
                    double distTraveledLastTickX = target.posX - target.prevPosX;
                    double distTraveledLastTickZ = target.posZ - target.prevPosZ;
                    double spddd = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
                    double speedometerkphdouble = (double) MathHelper.sqrt(spddd) * 71.2729367892;
                    speedometerkphdouble = (double) Math.round(10.0 * speedometerkphdouble) / 10.0;
                    if(speedometerkphdouble > targetMaxSpeed){
                        targetMaxSpeed = speedometerkphdouble;
                    }
                }
            }

        }
        if (mc.player.onGround && !isInLiquid() && !mc.player.isOnLadder() && !mc.player.isInWeb && !mc.player.isPotionActive(MobEffects.SLOWNESS) && target != null && criticals_autojump.getValue()) {
            mc.player.jump();
        }
        if (clientLook.getValue()) {
            mc.player.rotationYaw = serverRotation.x;
            mc.player.rotationPitch = serverRotation.y;
        }
    }

    public static boolean isInLiquid() {
        return mc.player.isInWater() || mc.player.isInLava();
    }

    @SubscribeEvent
    public void onRotate(EventPreMotion e){
            astolfo.update();
            aura();
            if(stopAuraRotate){
                return;
            }
            if (targetesp.getValue()) {
                prevCircleStep = circleStep;
                circleStep += 0.15;
            }
            if (target != null) {
                if (!(isGapping && gappleFix.getValue())) {
                    mc.player.rotationYaw = serverRotation.x;
                    mc.player.rotationPitch = serverRotation.y;
                    mc.player.renderYawOffset = serverRotation.x;
                }
            } else {
                btPositions.clear();
                isGapping = false;
            }
    }

    public static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }

    public static AstolfoAnimation astolfo = new AstolfoAnimation();


    @SubscribeEvent
    public void onRender3D(Render3DEvent e){
        if(btvisual.getValue() && btprofit && target != null && getBacktrackPos() != null){
            if(!(target instanceof EntityPlayer)) return;

            EntityPlayer ghost = (EntityPlayer) target;
            ghost.prevLimbSwingAmount = target.prevLimbSwingAmount;
            ghost.limbSwing = target.limbSwing;
            ghost.limbSwingAmount = target.limbSwingAmount;
            ghost.hurtTime = target.hurtTime;
            GlStateManager.pushMatrix();

            boolean BLEND = GL11.glIsEnabled(GL11.GL_BLEND);
            GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            try {
                mc.getRenderManager().renderEntity(ghost, getBacktrackPos().x - mc.getRenderManager().renderPosX, getBacktrackPos().y - mc.getRenderManager().renderPosY, getBacktrackPos().z - mc.getRenderManager().renderPosZ, target.rotationYaw, mc.getRenderPartialTicks(), false);
            } catch (Exception ignored){}

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();


            GlStateManager.popMatrix();
        }
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
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glBegin(GL11.GL_QUAD_STRIP);
                for (int i = 0; i <= 360; i++) {
                    int clr = astolfo.getColor((i + 90) / 360);
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
                    int clr = astolfo.getColor((i + 90) / 360);
                    int red = ((clr >> 16) & 255);
                    int green = ((clr >> 8) & 255);
                    int blue = ((clr & 255));
                    GL11.glColor4f(red, green, blue, 0.8F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }
                GL11.glEnd();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glShadeModel(GL11.GL_FLAT);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glPopMatrix();
                GlStateManager.resetColor();
            }
        }
    }


    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            if(nointer.getValue()) {
                CPacketUseEntity cPacketUseEntity = event.getPacket();
                if (cPacketUseEntity.getAction() == CPacketUseEntity.Action.INTERACT) {
                    event.setCanceled(true);
                }
                if (cPacketUseEntity.getAction() == CPacketUseEntity.Action.INTERACT_AT) {
                    event.setCanceled(true);
                }
            }
        }
        if (event.getPacket() instanceof SPacketOpenWindow){
            if(nointer.getValue()){
                event.setCanceled(true);
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
        btprofit = false;
        target = null;
        serverRotation.x = mc.player.rotationYaw;
        serverRotation.y = mc.player.rotationPitch;
        stopAuraRotate = false;
        if(jboost && mc.player.isPotionActive(MobEffects.STRENGTH)){
            mc.player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(8)), nig, 1));
        }
    }

    public void aura() {
        boolean shieldDesyncActive = shieldDesync.getValue();
        if (shieldDesyncOnlyOnAura.getValue() && target == null) {
            shieldDesyncActive = false;
        }
        if (isActiveItemStackBlocking(mc.player, 4 + new Random().nextInt(4)) && shieldDesyncActive && mc.player.isHandActive()) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }
        if (minCPS > 0) {
            minCPS--;
        }
        if (Crystalsss.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (getBestHitbox(entity, getDistance(entity)) != null && needExplosion(entity.getPositionVector())) {
                        if(inhibit.passedMs(100)) {
                            attack(entity);
                            inhibit.reset();
                        }
                        return;
                    }
                }
            }
        }
        if (target != null) {
            if (!isEntityValid(target)) {
                target = null;
            }
        }
        if (target == null) {
            target = findTarget();
        }
        if (target == null) {
            serverRotation.x = mc.player.rotationYaw;
            serverRotation.y = mc.player.rotationPitch;
            return;
        }
        if (!weaponOnly()) {
            return;
        }
        if(backtrack.getValue()){
            addPos(target.getPositionVector());
        }
        attack(target);
        prevtarget = target;
        rotate(target, false);
    }

    public void attack(Entity base) {
        if (base instanceof EntityEnderCrystal || (canAttack() && minCPS == 0)) {
            if (getBestHitbox(base, getDistance(base)) != null) {
                boolean crystal = base instanceof EntityEnderCrystal;
                if (!crystal)
                    rotate(base, true);

                if (
                        !rtx.getValue()
                        || btprofit
                        || (AdvancedCast.instance.getMouseOver(base, serverRotation.x, serverRotation.y, distance.getValue(), ignoreWalls(base)) == base)
                        || (crystal && mc.player.getDistance(base) <= 4.5)
                ) {

                    boolean blocking = mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().getItemUseAction(mc.player.getActiveItemStack()) == EnumAction.BLOCK;
                    if (blocking) {
                        mc.playerController.onStoppedUsingItem(mc.player);
                    }
                    boolean needSwap = false;

                    if (mc.player.isSprinting()) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SPRINTING));
                        needSwap = true;
                    }

                    if(betterCrits.getValue()){
                        mc.gameSettings.keyBindSneak.pressed = true;
                    }
                    minCPS = 10;
                    hitTick = true;
                    mc.playerController.attackEntity(mc.player, base);
                    hits++;


                    if(Debug.getValue()){
                        Command.sendMessage("Attacked with delay: " + hitttimer.getPassedTimeMs());
                        hitttimer.reset();
                    }

                    if(!offhand.getValue()) {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    } else {
                        mc.player.swingArm(EnumHand.OFF_HAND);
                    }

                    if (getAxe() >= 0 && shieldBreaker.getValue() && base instanceof EntityPlayer && isActiveItemStackBlocking((EntityPlayer) base, 1)) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(getAxe()));
                        shieldBreaker((EntityPlayer) base);
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    }
                    if (blocking) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
                    }
                    if (needSwap) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SPRINTING));
                    }
                    if(betterCrits.getValue()){
                        mc.gameSettings.keyBindSneak.pressed = false;
                    }
                }
            }
        }
    }

    public double getDistance(Entity entity) {
        double dstValue = getAttackdistance();
        if (entity instanceof EntityEnderCrystal) {
            return rotation.getValue()  == rotmod.Matrix ? 4.5 : dstValue;
        }
        return dstValue;
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
        boolean reasonForCancelCritical = mc.player.isPotionActive(MobEffects.SLOWNESS) || mc.player.isOnLadder() || isInLiquid() || mc.player.isInWeb;
        if(!fullpower.getValue()) {
            if (mc.player.getCooledAttackStrength(1.5f) < 0.93) {
                return false;
            }
        } else {
            if (mc.player.getCooledAttackStrength(0) < 1f) {
                return false;
            }
        }
        if (!reasonForCancelCritical && criticals.getValue()) {
            int r = (int) mc.player.posY;
            int c = (int) Math.ceil(mc.player.posY);
            if (r != c && mc.player.onGround && isBlockAboveHead()) {
                return true;
            }
            return !mc.player.onGround && mc.player.fallDistance > 0;
        }
        return true;
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ + 0.3, mc.player.posX + 0.3, mc.player.posY + (!mc.player.onGround ? 1.5 : 2.5),
                mc.player.posZ - 0.3);
        return !mc.world.getCollisionBoxes(mc.player, axisAlignedBB).isEmpty();
    }

    public static int getAxe() {
        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() instanceof ItemAxe) {
                return i;
            }
        }
        return -1;
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
        if (entity.getHealth() <= 0) {
            return false;
        }
        if (!targetsCheck(entity)) {
            return false;
        }
        if (!ignoreWalls(entity)) {
            return getBestHitbox(entity, getAttackdistance()) != null;
        } else
            return !(entity.getDistance(mc.player) > getAttackdistance() );
    }

    public Vec3d getBestHitbox(Entity target, double rotateDistance) {
        if (mc.player.getDistance(target) >= 7) {
            return null;
        }
        Vec3d head = findHitboxCoord(Hitbox.HEAD, target);
        Vec3d chest = findHitboxCoord(Hitbox.CHEST, target);
        Vec3d legs = findHitboxCoord(Hitbox.LEGS, target);
        ArrayList<Vec3d> points = new ArrayList<>(Arrays.asList(head, chest, legs));

        if(!backtrack.getValue()) {
            points.removeIf(point -> !isHitBoxVisible(target, point, rotateDistance));
        }

        if (points.isEmpty()) {
            return null;
        }
        points.sort((d1, d2) -> {
            Vector2f r1 = getDeltaForCoord(serverRotation, d1);
            Vector2f r2 = getDeltaForCoord(serverRotation, d2);
            float y1 = Math.abs(r1.y);
            float y2 = Math.abs(r2.y);
            return (int) ((y1 - y2) * 1000);
        });
        return points.get(0);
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
        if (Friendsss.getValue()) {
            castHelper.apply(CastHelper.EntityType.FRIENDS);
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
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.getBlockState(pos).getMaterial() != Material.AIR && rotation.getValue() == rotmod.Matrix) {
            return true;
        }
        return ignoreWalls.getValue();
    }

    public static Vector2f getDeltaForCoord(Vector2f rot, Vec3d point) {
        EntityPlayerSP client = Minecraft.getMinecraft().player;
        double x = point.x - client.posX;
        double y = point.y - client.getPositionEyes(1).y;
        double z = point.z - client.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = MathHelper.wrapDegrees(yawToTarget - rot.x);
        float pitchDelta = (pitchToTarget - rot.y);
        return new Vector2f(yawDelta, pitchDelta);
    }

    public boolean isHitBoxVisible(Entity target, Vec3d vector, double dst) {
        return RaycastHelper.getPointedEntity(getRotationForCoord(vector), dst, 1, !ignoreWalls(target), target) == target;
    }

    public static Vector2f getRotationForCoord(Vec3d point) {
        EntityPlayerSP client = Minecraft.getMinecraft().player;
        double x = point.x - client.posX;
        double y = point.y - client.getPositionEyes(1).y;
        double z = point.z - client.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        return new Vector2f(yawToTarget, pitchToTarget);
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

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public void rotate(Entity base, boolean attackContext) {
        Vec3d bestHitbox = getBestHitbox(base,  getAttackdistance());
        if (bestHitbox == null) {
            bestHitbox = base.getPositionEyes(1);
        }
        float sensitivity = 1.0001f;
        double x =  (bestHitbox.x - mc.player.posX);
        double y =  bestHitbox.y - mc.player.getPositionEyes(1).y;
        double z =  bestHitbox.z - mc.player.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
        float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = MathHelper.wrapDegrees(yawToTarget - serverRotation.x) / sensitivity;
        float pitchDelta = (pitchToTarget - serverRotation.y) / sensitivity;
        if (yawDelta > 180) {
            yawDelta = yawDelta - 180;
        }
        int yawDeltaAbs = (int) Math.abs(yawDelta);
        if (yawDeltaAbs < fov.getValue()) {
            switch (rotation.getValue()) {
                case Matrix: {
                    float pitchDeltaAbs = Math.abs(pitchDelta);
                    float additionYaw = Math.min(Math.max(yawDeltaAbs, 1), 80);
                    float additionPitch = Math.max(attackContext ? pitchDeltaAbs : 1, 2);
                    if (Math.abs(additionYaw - this.prevAdditionYaw) <= 3.0f) {
                        additionYaw = this.prevAdditionYaw + 3.1f;
                    }
                    float newYaw = serverRotation.x + (yawDelta > 0 ? additionYaw : -additionYaw) * sensitivity;
                    float newPitch = MathHelper.clamp(serverRotation.y + (pitchDelta > 0 ? additionPitch : -additionPitch) * sensitivity, -90, 90);
                    serverRotation.x = newYaw;
                    serverRotation.y = newPitch;
                    this.prevAdditionYaw = additionYaw;
                    break;
                }
                case FunnyGame: {
                    float[] ncp;
                    if(!backtrack.getValue() || !btprofit || getBestHitbox(base,getAttackdistance()) == null) {
                        ncp = RotationHelper.getNCPRotations(base, false);
                    } else {
                        ncp = RotationHelper.getNCPRotationsBT(getBestHitbox(base,getAttackdistance()));
                    }
                    serverRotation.x = ncp[0];
                    serverRotation.y = ncp[1];
                    break;
                }
                case DeadCode:{
                    float[] angles = RotateCalculator.a(base, cy_0.b);
                    serverRotation.x = angles[0];
                    serverRotation.y = angles[1];
                    break;
                }
                case Nexus: {
                    if (attackContext) {
                        int pitchDeltaAbs = (int) Math.abs(pitchDelta);
                        float newYaw = serverRotation.x + (yawDelta > 0 ? yawDeltaAbs : -yawDeltaAbs) * sensitivity;
                        float newPitch = MathHelper.clamp(serverRotation.y + (pitchDelta > 0 ? pitchDeltaAbs : -pitchDeltaAbs) * sensitivity, -90, 90);
                        serverRotation.x = newYaw;
                        serverRotation.y = newPitch;
                        mc.player.rotationYaw = newYaw;
                        mc.player.rotationPitch = newPitch;
                    }
                    break;
                }
            }
        }
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive sound) {
        if(fullNullCheck()){
            return;
        }
        if (sound.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus sPacketEntityStatus = sound.getPacket();
            if (sPacketEntityStatus.getOpCode() == 30) {
                if (sPacketEntityStatus.getEntity(mc.world) == target) {
                    NotificationManager.publicity(TextFormatting.GREEN + "ShieldBreaker", "Успешно снёс щит " + target.getName(), 2, NotificationType.SUCCESS);
                }
            }
        }
        if(sound.getPacket() instanceof SPacketSoundEffect){
            SPacketSoundEffect pac = sound.getPacket();
            BlockPos bp1 =  new BlockPos(pac.getX(),pac.getY(),pac.getZ());
            if(mc.player.getDistance(bp1.x,bp1.y,bp1.z) < 1.5){
                if(pac.sound.getSoundName().toString().contains("nodamage")){
                    misshits++;
                    if(btprofit){
                        backtracketHits++;
                        backtracketMisses++;
                        Command.sendMessage("BackTrack FAILED!");
                    }
                } else if(pac.sound.getSoundName().toString().contains("crit")){
                    if(btprofit){
                        backtracketHits++;
                        if(target != null){
                            backtrackedMaxDist = mc.player.getDistance(target);
                        }
                        Command.sendMessage("BackTracked! distance: " +  target != null ? mc.player.getDistance(target) +"" : "(target is dead or null)");
                    }

                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send action) {
        if(action.getPacket() instanceof CPacketPlayerDigging){
            CPacketPlayerDigging pac = action.getPacket();
            if(pac.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM){
                isGapping = false;
            }
        }
        if(action.getPacket() instanceof CPacketPlayerTryUseItem){
            isGapping = true;
        }
        if(action.getPacket() instanceof CPacketPlayerTryUseItemOnBlock){
            isGapping = true;
        }
    }


    public Vec3d findHitboxCoord(Hitbox box, Entity target) {
        double yCoord = 0;
        switch (box) {
            case HEAD:
                yCoord = target.getEyeHeight();
                break;
            case CHEST:
                yCoord = target.getEyeHeight() / 2;
                break;
            case LEGS:
                yCoord = 0.05;
                break;
        }
        if(!backtrack.getValue()){
            btprofit = false;
            return target.getPositionVector().add(0, yCoord, 0);
        } else {
            if(getBacktrackPos() != null) {
                if((getDistanceBT(getBacktrackPos().add(0, yCoord, 0)) < getDistanceBT(target.getPositionVector().add(0, yCoord, 0))) && (getDistanceBT(target.getPositionVector().add(0, yCoord, 0)) > (distance.getValue()*distance.getValue()))){
                    btprofit = true;
                    return getBacktrackPos().add(0, yCoord, 0);
                } else{
                    btprofit = false;
                    return target.getPositionVector().add(0, yCoord, 0);
                }
            } else {
                btprofit = false;
                return target.getPositionVector().add(0, yCoord, 0);
            }
        }
    }

    public float getDistanceBT(Vec3d vector) {
        float f = (float)(mc.player.posX - vector.x);
        float f1 = (float)(mc.player.getPositionEyes(1).y - vector.y);
        float f2 = (float)(mc.player.posZ - vector.z);
        return (f * f + f1 * f1 + f2 * f2);
    }

    private float getAttackdistance(){
        if(!btprofit){
            return distance.getValue();
        } else {
            return distance.getValue() + 10;
        }
    }

    private void addPos(Vec3d pos) {
        while (btPositions.size() > btticks.getValue()) {
            btPositions.remove();
        }
        btPositions.add(pos);
    }

    private Vec3d getBacktrackPos() {
        if (btPositions.isEmpty()) return null;
        return btPositions.peek();
    }

}