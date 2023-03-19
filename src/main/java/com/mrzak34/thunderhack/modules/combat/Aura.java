package com.mrzak34.thunderhack.modules.combat;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayer;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.player.AutoGApple;
import com.mrzak34.thunderhack.modules.render.Search;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Parent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.SilentRotationUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.ExplosionBuilder;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.phobos.IEntity;
import com.mrzak34.thunderhack.util.phobos.IEntityLivingBase;
import com.mrzak34.thunderhack.util.rotations.CastHelper;
import com.mrzak34.thunderhack.util.rotations.RayTracingUtils;
import com.mrzak34.thunderhack.util.rotations.ResolverUtil;
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
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;

import static com.mrzak34.thunderhack.gui.clickui.ColorUtil.interpolateColorC;
import static com.mrzak34.thunderhack.modules.funnygame.EffectsRemover.jboost;
import static com.mrzak34.thunderhack.modules.funnygame.EffectsRemover.nig;
import static com.mrzak34.thunderhack.util.render.RenderUtil.TwoColoreffect;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.wrapDegrees;


public class Aura extends Module {

    public static EntityLivingBase target;
    public static BackTrack.Box bestBtBox;
    public static int CPSLimit;
    /*-------------   AntiCheat  ----------*/
    public final Setting<Parent> antiCheat = register(new Setting<>("AntiCheat", new Parent(false)));
    private final Setting<rotmod> rotation = register(new Setting("Rotation", rotmod.Matrix)).withParent(antiCheat);
    public final Setting<Float> rotateDistance = register(new Setting("RotateDst", 1f, 0f, 5f)).withParent(antiCheat);
    public final Setting<Float> attackDistance = register(new Setting("AttackDst", 3.1f, 0.0f, 7.0f)).withParent(antiCheat);
    public final Setting<RayTracingMode> rayTracing = register(new Setting("RayTracing", RayTracingMode.NewJitter)).withParent(antiCheat);
    public final Setting<PointsMode> pointsMode = register(new Setting("PointsSort", PointsMode.Distance)).withParent(antiCheat);
    public final Setting<TimingMode> timingMode = register(new Setting("Timing", TimingMode.Default)).withParent(antiCheat);
    public final Setting<Integer> minCPS = register(new Setting("MinCPS", 10, 1, 20, v -> timingMode.getValue() == TimingMode.Old)).withParent(antiCheat);
    public final Setting<Integer> maxCPS = register(new Setting("MaxCPS", 12, 1, 20, v -> timingMode.getValue() == TimingMode.Old)).withParent(antiCheat);
    public final Setting<Boolean> rtx = register(new Setting<>("RTX", true)).withParent(antiCheat);
    public final Setting<Float> walldistance = register(new Setting("WallDst", 3.6f, 0.0f, 7.0f)).withParent(antiCheat);
    public final Setting<Integer> fov = register(new Setting("FOV", 180, 5, 180)).withParent(antiCheat);
    public final Setting<Float> hitboxScale = register(new Setting("RTXScale", 2.8f, 0.0f, 3.0f)).withParent(antiCheat);
    public final Setting<Integer> yawStep = register(new Setting("YawStep", 80, 5, 180, v -> rotation.getValue() == rotmod.Matrix)).withParent(antiCheat);
    /*------------   Exploits  ------------*/
    public final Setting<Parent> exploits = register(new Setting<>("Exploits", new Parent(false)));
    public final Setting<Boolean> resolver = register(new Setting<>("Resolver", false)).withParent(exploits);
    public final Setting<Boolean> shieldDesync = register(new Setting<>("Shield Desync", false)).withParent(exploits);
    public final Setting<Boolean> backTrack = register(new Setting<>("RotateToBackTrack", true)).withParent(exploits);
    public final Setting<Boolean> shiftTap = register(new Setting<>("ShiftTap", false)).withParent(exploits);
    /*-------------   Misc  ---------------*/
    public final Setting<Parent> misc = register(new Setting<>("Misc", new Parent(false)));
    public final Setting<Boolean> shieldDesyncOnlyOnAura = register(new Setting<>("Wait Target", true, v -> shieldDesync.getValue())).withParent(misc);
    public final Setting<Boolean> criticals = register(new Setting<>("OnlyCrits", true)).withParent(misc);
    public final Setting<CritMode> critMode = register(new Setting("CritMode", CritMode.WexSide, v -> criticals.getValue())).withParent(misc);
    public final Setting<Float> critdist = register(new Setting("FallDistance", 0.15f, 0.0f, 1.0f, v -> criticals.getValue() && critMode.getValue() == CritMode.Simple)).withParent(misc);
    public final Setting<Boolean> criticals_autojump = register(new Setting<>("AutoJump", false, v -> criticals.getValue())).withParent(misc);
    public final Setting<Boolean> smartCrit = register(new Setting<>("SpaceOnly", true, v -> criticals.getValue())).withParent(misc);
    public final Setting<Boolean> watercrits = register(new Setting<>("WaterCrits", false, v -> criticals.getValue())).withParent(misc);
    public final Setting<Boolean> weaponOnly = register(new Setting<>("WeaponOnly", true)).withParent(misc);
    public final Setting<AutoSwitch> autoswitch = register(new Setting("AutoSwitch", AutoSwitch.None)).withParent(misc);
    public final Setting<Boolean> firstAxe = register(new Setting<>("FirstAxe", false, v -> autoswitch.getValue() != AutoSwitch.None)).withParent(misc);
    public final Setting<Boolean> clientLook = register(new Setting<>("ClientLook", false)).withParent(misc);
    public final Setting<Boolean> snap = register(new Setting<>("Snap", false)).withParent(misc);
    public final Setting<Boolean> shieldBreaker = register(new Setting<>("ShieldBreaker", true)).withParent(misc);
    public final Setting<Boolean> offhand = register(new Setting<>("OffHandAttack", false)).withParent(misc);
    public final Setting<Boolean> teleport = register(new Setting<>("TP", false)).withParent(misc);
    public final Setting<Float> tpY = register(new Setting("TPY", 3f, -5.0f, 5.0f, v -> teleport.getValue())).withParent(misc);
    public final Setting<Boolean> Debug = register(new Setting<>("HitsDebug", false)).withParent(misc);
    /*-------------   Targets  ------------*/
    public final Setting<Parent> targets = register(new Setting<>("Targets", new Parent(false)));
    public final Setting<Boolean> Playersss = register(new Setting<>("Players", true)).withParent(targets);
    public final Setting<Boolean> Mobsss = register(new Setting<>("Mobs", true)).withParent(targets);
    public final Setting<Boolean> Animalsss = register(new Setting<>("Animals", true)).withParent(targets);
    public final Setting<Boolean> Villagersss = register(new Setting<>("Villagers", true)).withParent(targets);
    public final Setting<Boolean> Slimesss = register(new Setting<>("Slimes", true)).withParent(targets);
    /*-------------------------------------*/
    public final Setting<Boolean> Crystalsss = register(new Setting<>("Crystals", true)).withParent(targets);
    public final Setting<Boolean> ignoreNaked = register(new Setting<>("IgnoreNaked", false)).withParent(targets);
    public final Setting<Boolean> ignoreInvisible = register(new Setting<>("IgnoreInvis", false)).withParent(targets);
    public final Setting<Boolean> ignoreCreativ = register(new Setting<>("IgnoreCreativ", true)).withParent(targets);
    /*-------------   Visual  -------------*/
    public final Setting<Parent> render = register(new Setting<>("Render", new Parent(false)));
    public final Setting<Boolean> RTXVisual = register(new Setting<>("RTXVisual", false)).withParent(render);
    public final Setting<Boolean> targetesp = register(new Setting<>("Target Esp", true)).withParent(render);//(visual);
    public final Setting<Float> circleStep1 = register(new Setting("CircleSpeed", 0.15f, 0.1f, 1.0f)).withParent(render);
    public final Setting<Float> circleHeight = register(new Setting("CircleHeight", 0.15f, 0.1f, 1.0f)).withParent(render);
    public final Setting<Integer> colorOffset1 = register(new Setting("ColorOffset", 10, 1, 20)).withParent(render);
    public final Setting<ColorSetting> shitcollor = this.register(new Setting<>("TargetColor", new ColorSetting(-2009289807))).withParent(render);
    public final Setting<ColorSetting> shitcollor2 = this.register(new Setting<>("TargetColor2", new ColorSetting(-2009289807))).withParent(render);
    /*-------------------------------------*/
    private final Timer oldTimer = new Timer();
    private final Timer hitttimer = new Timer();
    private float prevCircleStep, circleStep, prevAdditionYaw;
    private boolean swappedToAxe, swapBack, rotatedBefore;
    /*-------------------------------------*/
    private Vec3d last_best_vec;
    private float rotation_smoother;
    private float rotationPitch, rotationYaw;

    public Aura() {
        super("Aura", "Запомните блядь-киллка тх не мисает-а дает шанс убежать", "attacks entities", Category.COMBAT);
    }

    public static boolean isInLiquid() {
        return mc.player.isInWater() || mc.player.isInLava();
    }

    public static double absSinAnimation(double input) {
        return Math.abs(1 + Math.sin(input)) / 2;
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ + 0.3, mc.player.posX + 0.3, mc.player.posY + (!mc.player.onGround ? 1.5 : 2.5),
                mc.player.posZ - 0.3);
        return !mc.world.getCollisionBoxes(mc.player, axisAlignedBB).isEmpty();
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

    public static boolean isActiveItemStackBlocking(EntityPlayer other, int time) {
        if (other.isHandActive() && !other.getActiveItemStack().isEmpty()) {
            Item item = other.getActiveItemStack().getItem();
            if (item.getItemUseAction(other.getActiveItemStack()) != EnumAction.BLOCK) {
                return false;
            } else {
                return item.getMaxItemUseDuration(other.getActiveItemStack()) - ((IEntityLivingBase)other).getActiveItemStackUseCount() >= time;
            }
        } else {
            return false;
        }
    }

    public static float interpolateRandom(float var0, float var1) {
        return (float) (var0 + (var1 - var0) * Math.random());
    }

    @SubscribeEvent
    public void onCalc(PlayerUpdateEvent e) {
        if (firstAxe.getValue() && hitttimer.passedMs(3000) && InventoryUtil.getBestAxe() != -1) {
            if (autoswitch.getValue() == AutoSwitch.Default) {
                mc.player.inventory.currentItem = InventoryUtil.getBestAxe();
                swappedToAxe = true;
            }
        } else {
            if (autoswitch.getValue() == AutoSwitch.Default) {
                if (InventoryUtil.getBestSword() != -1) {
                    mc.player.inventory.currentItem = InventoryUtil.getBestSword();
                } else if (InventoryUtil.getBestAxe() != -1) {
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
            if (target instanceof EntityOtherPlayerMP && resolver.getValue()) {
                ResolverUtil.resolve((EntityOtherPlayerMP) target);
            }
            if (!isEntityValid(target, false)) {
                target = null;
                ResolverUtil.reset();
            }
        }
        if (Crystalsss.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (getVector(entity) != null && needExplosion(entity.getPositionVector())) {
                        if (oldTimer.passedMs(100)) {
                            attack(entity);
                            oldTimer.reset();
                        }
                        return;
                    }
                }
            }
        }

        if (target == null) {
            ResolverUtil.reset();
            target = findTarget();
        }

        if (target == null || mc.player.getDistanceSq(target) > attackDistance.getPow2Value()) {
            BackTrack bt = Thunderhack.moduleManager.getModuleByClass(BackTrack.class);
            if (bt.isOn() && backTrack.getValue()) {
                float best_distance = 100;
                for (EntityPlayer BTtarget : mc.world.playerEntities) {
                    if (mc.player.getDistanceSq(BTtarget) > 100) continue;
                    if (!isEntityValid(BTtarget, true)) continue;
                    if (bt.entAndTrail.get(BTtarget) == null) continue;
                    if (bt.entAndTrail.get(BTtarget).size() == 0) continue;
                    for (BackTrack.Box box : bt.entAndTrail.get(BTtarget)) {
                        if (getDistanceBT(box) < best_distance) {
                            best_distance = getDistanceBT(box);
                            if (target != null && best_distance < mc.player.getDistanceSq(target)) {
                                target = BTtarget;
                            } else if (target == null && best_distance < attackDistance.getPow2Value()) {
                                target = BTtarget;
                            }
                        }
                    }
                }
            }
        }

        if (target == null) {
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
        if (target != null && resolver.getValue()) {
            if (target instanceof EntityOtherPlayerMP) {
                ResolverUtil.releaseResolver((EntityOtherPlayerMP) target);
            }
        }
    }

    @SubscribeEvent
    public void onRotate(EventPreMotion e) {
        if (target != null) {
            mc.player.rotationYaw = rotationYaw;
            mc.player.rotationPitch = rotationPitch;
            mc.player.rotationYawHead = rotationYaw;
            mc.player.renderYawOffset = rotationYaw;
        }
    }

    @Override
    public void onUpdate() {
        if (targetesp.getValue()) {
            prevCircleStep = circleStep;
            circleStep += circleStep1.getValue();
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent e) {
        if (targetesp.getValue()) {
            EntityLivingBase entity = Aura.target;
            if (entity != null) {
                double cs = prevCircleStep + (circleStep - prevCircleStep) * mc.getRenderPartialTicks();
                double prevSinAnim = absSinAnimation(cs - circleHeight.getValue());
                double sinAnim = absSinAnimation(cs);
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks() - ((IRenderManager)mc.getRenderManager()).getRenderPosX();
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - ((IRenderManager)mc.getRenderManager()).getRenderPosY() + prevSinAnim * 1.4f;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks() - ((IRenderManager)mc.getRenderManager()).getRenderPosZ();
                double nextY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - ((IRenderManager)mc.getRenderManager()).getRenderPosY() + sinAnim * 1.4f;

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
                    Color clr = getTargetColor(shitcollor.getValue().getColorObject(),shitcollor2.getValue().getColorObject(),i);
                    GL11.glColor4f(clr.getRed() / 255f, clr.getGreen() / 255f, clr.getBlue() / 255f, 0.6F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);

                    GL11.glColor4f(clr.getRed() / 255f, clr.getGreen() / 255f, clr.getBlue() / 255f, 0.01F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, y, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }

                GL11.glEnd();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                for (int i = 0; i <= 360; i++) {
                    Color clr = getTargetColor(shitcollor.getValue().getColorObject(),shitcollor2.getValue().getColorObject(),i);
                    GL11.glColor4f(clr.getRed() / 255f, clr.getGreen() / 255f, clr.getBlue() / 255f, 0.8F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY, z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }
                GL11.glEnd();

                if (!cullface)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);

                if (texture)
                    GL11.glEnable(GL11.GL_TEXTURE_2D);


                if (depth)
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                GL11.glShadeModel(GL11.GL_FLAT);

                if (!blend)
                    GL11.glDisable(GL11.GL_BLEND);
                if (cullface)
                    GL11.glEnable(GL11.GL_CULL_FACE);
                if (alpha)
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glPopMatrix();
                GlStateManager.resetColor();


                if (RTXVisual.getValue()) {
                    GlStateManager.pushMatrix();
                    Vec3d eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(mc.player.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.player.rotationYaw));
                    if (rayTracing.getValue() == RayTracingMode.Beta) {
                        Vec3d point = RayTracingUtils.getVecTarget(target, attackDistance.getValue() + rotateDistance.getValue());
                        if (point == null) return;
                        Search.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, point.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), point.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), point.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xEA008F00, true).getRGB());
                    } else {
                        for (Vec3d point : RayTracingUtils.getHitBoxPoints(target.getPositionVector(), hitboxScale.getValue() / 10f)) {
                            if (!isPointVisible(target, point, (attackDistance.getValue() + rotateDistance.getValue()))) {
                                Search.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, point.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), point.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), point.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xEA6E6E6E, true).getRGB());
                            } else {
                                Search.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, point.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), point.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), point.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xC800802D, true).getRGB());
                            }
                        }
                    }

                    if (last_best_vec != null) {
                        Search.renderTracer(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, last_best_vec.x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), last_best_vec.y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), last_best_vec.z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ(), new Color(0xEA00FF58, true).getRGB());
                    }
                    GlStateManager.popMatrix();
                    GlStateManager.resetColor();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        target = null;
        if (jboost && mc.player.isPotionActive(MobEffects.STRENGTH)) {
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
                        (RayTracingUtils.getMouseOver(base, rotationYaw, rotationPitch, attackDistance.getValue(), ignoreWalls(base)) == base)
                                || (base instanceof EntityEnderCrystal && mc.player.getDistanceSq(base) <= 20)
                                || (backTrack.getValue() && bestBtBox != null)
                                || !rtx.getValue()
                ) {
                    if (teleport.getValue()) {
                        mc.player.setPosition(base.posX, base.posY + tpY.getValue(), base.posZ);
                    }
                    boolean blocking = mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().getItemUseAction(mc.player.getActiveItemStack()) == EnumAction.BLOCK;
                    if (blocking) {
                        mc.playerController.onStoppedUsingItem(mc.player);
                    }
                    boolean needSwap = false;

                    if (EventManager.serversprint) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SPRINTING));
                        needSwap = true;
                    }
                    if (shiftTap.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
                    }
                    if (snap.getValue()) {
                        mc.player.rotationPitch = rotationPitch;
                        mc.player.rotationYaw = rotationYaw;
                    }

                    mc.playerController.attackEntity(mc.player, base);
                    if (Debug.getValue()) {
                        if (target != null && last_best_vec != null) {
                            Command.sendMessage("Attacked with delay: " + hitttimer.getPassedTimeMs() + " | Distance to target: " + mc.player.getDistance(target) + " | Distance to best point: " + mc.player.getDistance(last_best_vec.x, last_best_vec.y, last_best_vec.z));
                        }
                    }
                    hitttimer.reset();
                    mc.player.swingArm(offhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

                    if (InventoryUtil.getBestAxe() >= 0 && shieldBreaker.getValue() && base instanceof EntityPlayer && isActiveItemStackBlocking((EntityPlayer) base, 1)) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getBestAxe()));
                        mc.playerController.attackEntity(mc.player, base);
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
                    if (swappedToAxe) {
                        swapBack = true;
                        swappedToAxe = false;
                    }
                    CPSLimit = 10;
                }
            }
        }

    }

    @SubscribeEvent
    public void onPostAttack(EventPostMotion e) {
        if (firstAxe.getValue() && InventoryUtil.getBestSword() != -1 && swapBack) {
            if (autoswitch.getValue() == AutoSwitch.Default) {
                mc.player.inventory.currentItem = InventoryUtil.getBestSword();
                swapBack = false;
            }
        }
        if (clientLook.getValue()) {
            mc.player.rotationYaw = rotationYaw;
            mc.player.rotationPitch = rotationPitch;
        }
        if (shiftTap.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
        }
    }

    public float getDistanceBT(BackTrack.Box box) {
        float f = (float) (mc.player.posX - box.getPosition().x);
        float f1 = (float) (mc.player.getPositionEyes(1).y - box.getPosition().y);
        float f2 = (float) (mc.player.posZ - box.getPosition().z);
        return (f * f + f1 * f1 + f2 * f2);
    }

    public float getDistanceBTPoint(Vec3d point) {
        float f = (float) (mc.player.posX - point.x);
        float f1 = (float) (mc.player.getPositionEyes(1).y - point.y);
        float f2 = (float) (mc.player.posZ - point.z);
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
        boolean reasonForCancelCritical = mc.player.isOnLadder() || (isInLiquid()) || ((IEntity)mc.player).isInWeb() || (smartCrit.getValue() && ((!criticals_autojump.getValue() && !mc.gameSettings.keyBindJump.isKeyDown())));

        if (timingMode.getValue() == TimingMode.Default) {
            if (CPSLimit > 0) return false;
            if (mc.player.getCooledAttackStrength(1.5f) <= 0.93) return false;
        } else {
            if (!oldTimer.passedMs((long) ((1000 + (MathUtil.random(1, 50) - MathUtil.random(1, 60) + MathUtil.random(1, 70))) / (int) MathUtil.random(minCPS.getValue(), maxCPS.getValue())))) {
                return false;
            }
        }

        if (last_best_vec != null) {
            if (RayTracingUtils.getDistanceFromHead(new Vec3d(last_best_vec.x, last_best_vec.y, last_best_vec.z)) > attackDistance.getPow2Value()) {
                return false;
            }
        }

        if (criticals.getValue() && watercrits.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() instanceof BlockLiquid && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock() instanceof BlockAir)) {
            return mc.player.fallDistance > 0f;
        }

        if (criticals.getValue() && !reasonForCancelCritical) {
            if (critMode.getValue() == CritMode.WexSide) {
                EntityPlayerSP client = mc.player;
                int r = (int) mc.player.posY;
                int c = (int) Math.ceil(mc.player.posY);
                if (r != c && mc.player.onGround && isBlockAboveHead()) {
                    return true;
                }
                return !client.onGround && client.fallDistance > 0;
            } else if (critMode.getValue() == CritMode.Simple) {
                return (isBlockAboveHead() ? mc.player.fallDistance > 0 : mc.player.fallDistance >= critdist.getValue()) && !mc.player.onGround;
            }
        }
        oldTimer.reset();
        return true;
    }

    private float getCooledAttackStrength() {
        return clamp(((float) ((IEntityLivingBase) mc.player).getTicksSinceLastSwing() + 1.5f) / getCooldownPeriod(), 0.0F, 1.0F);
    }

    public float getCooldownPeriod() {
        return (float) (1.0 / mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * (20f * Thunderhack.TICK_TIMER));
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
        if (ignoreCreativ.getValue()) {
            if (entity instanceof EntityPlayer) {
                if (((EntityPlayer) entity).isCreative()) {
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
        if (backtrack) {
            return true;
        }

        if (!ignoreWalls(entity))
            return getVector(entity) != null;
        else
            return mc.player.getDistanceSq(entity) <= Math.pow((attackDistance.getValue() + rotateDistance.getValue()), 2);
    }

    public Vec3d getVector(Entity target) {
        BackTrack bt = Thunderhack.moduleManager.getModuleByClass(BackTrack.class);
        if (!backTrack.getValue()
                || (mc.player.getDistanceSq(target) <= attackDistance.getPow2Value())
                || bt.isOff()
                || !(target instanceof EntityPlayer)
                || (backTrack.getValue() && bt.entAndTrail.get(target) == null)
                || (backTrack.getValue() && bt.entAndTrail.get(target) != null && bt.entAndTrail.get(target).size() == 0)) {

            if (rayTracing.getValue() == RayTracingMode.Beta) {
                return RayTracingUtils.getVecTarget(target, attackDistance.getValue() + rotateDistance.getValue());
            }
            ArrayList<Vec3d> points = RayTracingUtils.getHitBoxPoints(target.getPositionVector(), hitboxScale.getValue() / 10f);

            points.removeIf(point -> !isPointVisible(target, point, (attackDistance.getValue() + rotateDistance.getValue())));

            if (points.isEmpty()) {
                return null;
            }

            float best_distance = 100;
            Vec3d best_point = null;
            float best_angle = 180f;

            if (pointsMode.getValue() == PointsMode.Angle) {
                for (Vec3d point : points) {
                    Vector2f r = getDeltaForCoord(new Vector2f(Thunderhack.rotationManager.getServerYaw(), Thunderhack.rotationManager.getServerPitch()), point);
                    float y = Math.abs(r.y);
                    if (y < best_angle) {
                        best_angle = y;
                        best_point = point;
                    }
                }
            } else {
                for (Vec3d point : points) {
                    if (RayTracingUtils.getDistanceFromHead(point) < best_distance) {
                        best_point = point;
                        best_distance = RayTracingUtils.getDistanceFromHead(point);
                    }
                }
            }
            last_best_vec = best_point;
            return best_point;
        } else {
            bestBtBox = null;
            float best_distance = 36;
            BackTrack.Box best_box = null;
            for (BackTrack.Box boxes : bt.entAndTrail.get(target)) {
                if (getDistanceBT(boxes) < best_distance) {
                    best_box = boxes;
                    best_distance = getDistanceBT(boxes);
                }
            }

            if (best_box != null) {
                bestBtBox = best_box;
                ArrayList<Vec3d> points = RayTracingUtils.getHitBoxPoints(best_box.getPosition(), hitboxScale.getValue() / 10f);
                points.removeIf(point -> getDistanceBTPoint(point) > Math.pow((attackDistance.getValue() + rotateDistance.getValue()), 2));

                if (points.isEmpty()) {
                    return null;
                }


                float best_distance2 = 100;
                Vec3d best_point = null;
                float best_angle = 180f;

                if (pointsMode.getValue() == PointsMode.Angle) {
                    for (Vec3d point : points) {
                        Vector2f r = getDeltaForCoord(new Vector2f(Thunderhack.rotationManager.getServerYaw(), Thunderhack.rotationManager.getServerPitch()), point);
                        float y = Math.abs(r.y);
                        if (y < best_angle) {
                            best_angle = y;
                            best_point = point;
                        }

                    }
                } else {
                    for (Vec3d point : points) {
                        if (RayTracingUtils.getDistanceFromHead(point) < best_distance2) {
                            best_point = point;
                            best_distance2 = RayTracingUtils.getDistanceFromHead(point);
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
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getMaterial() != Material.AIR)
            return true;
        return mc.player.getDistanceSq(input) <= walldistance.getPow2Value();
    }

    public void rotate(Entity base, boolean attackContext) {
        rotatedBefore = true;

        Vec3d bestVector = getVector(base);
        if (bestVector == null) {
            bestVector = base.getPositionEyes(1);
        }

        boolean inside_target = mc.player.getEntityBoundingBox().intersects(base.getEntityBoundingBox());


        if (rotation.getValue() == rotmod.Matrix3 && inside_target) {
            bestVector = base.getPositionVector().add(new Vec3d(0, interpolateRandom(0.7f, 0.9f), 0));
        }


        double x = (bestVector.x - mc.player.posX);
        double y = bestVector.y - mc.player.getPositionEyes(1).y;
        double z = bestVector.z - mc.player.posZ;
        double dst = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));

        float sensitivity = 1.0001f;

        float yawDelta = wrapDegrees(yawToTarget - Thunderhack.rotationManager.getServerYaw()) / sensitivity;
        float pitchDelta = (pitchToTarget - Thunderhack.rotationManager.getServerPitch()) / sensitivity;


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

                    if (Math.abs(additionYaw - prevAdditionYaw) <= 3.0f) {
                        additionYaw = prevAdditionYaw + 3.1f;
                    }

                    float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? additionYaw : -additionYaw) * sensitivity;
                    float newPitch = clamp(Thunderhack.rotationManager.getServerPitch() + (pitchDelta > 0 ? additionPitch : -additionPitch) * sensitivity, -90, 90);

                    rotationYaw = newYaw;
                    rotationPitch = newPitch;
                    prevAdditionYaw = additionYaw;
                    break;
                }
                case SunRise:
                case Matrix2: {
                    boolean sanik = rotation.getValue() == rotmod.SunRise;
                    float absoluteYaw = MathHelper.abs(yawDelta);

                    float randomize = interpolateRandom(-3.0F, 3.0F);
                    float randomizeClamp = interpolateRandom(-5.0F, 5.0F);

                    float deltaYaw = MathHelper.clamp(absoluteYaw + randomize, -60.0F + randomizeClamp, 60.0F + randomizeClamp);
                    float deltaPitch = MathHelper.clamp(pitchDelta + randomize, (-((sanik) ? 13 : 45)), (sanik) ? 13 : 45);

                    float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? deltaYaw : -deltaYaw);
                    float newPitch = MathHelper.clamp(Thunderhack.rotationManager.getServerPitch() + deltaPitch / (sanik ? 4.0F : 2.0F), -90.0F, 90.0F);

                    float gcdFix1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcdFix2 = Math.pow(gcdFix1, 3.0) * 8.0;
                    double gcdFix = gcdFix2 * 0.15000000596046448;

                    rotationYaw = (float) (newYaw - (newYaw - Thunderhack.rotationManager.getServerYaw()) % gcdFix);
                    rotationPitch = (float) (newPitch - (newPitch - Thunderhack.rotationManager.getServerPitch()) % gcdFix);
                    break;
                }
                case Matrix3: {
                    float absoluteYaw = MathHelper.abs(yawDelta);

                    float randomize = interpolateRandom(-2.0F, 2.0F);
                    float randomizeClamp = interpolateRandom(-5.0F, 5.0F);

                    boolean looking_at_box = RayTracingUtils.getMouseOver(base, Thunderhack.rotationManager.getServerYaw(), Thunderhack.rotationManager.getServerPitch(), attackDistance.getValue() + rotateDistance.getValue(), ignoreWalls(base)) == base;

                    if (looking_at_box) {
                        rotation_smoother = 15f;
                    } else if (rotation_smoother < 60f) {
                        rotation_smoother += 9f;
                    }

                    float yaw_speed = (inside_target && attackContext) ? 60f : rotation_smoother;
                    float pitch_speed = looking_at_box ? 0.5f : rotation_smoother / 2f;

                    float deltaYaw = MathHelper.clamp(absoluteYaw + randomize, -yaw_speed + randomizeClamp, yaw_speed + randomizeClamp);
                    float deltaPitch = MathHelper.clamp(pitchDelta, -pitch_speed, pitch_speed);

                    float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? deltaYaw : -deltaYaw);
                    float newPitch = MathHelper.clamp(Thunderhack.rotationManager.getServerPitch() + deltaPitch, -90.0F, 90.0F);

                    float gcdFix1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcdFix2 = Math.pow(gcdFix1, 3.0) * 8.0;
                    double gcdFix = gcdFix2 * 0.15000000596046448;

                    rotationYaw = (float) (newYaw - (newYaw - Thunderhack.rotationManager.getServerYaw()) % gcdFix);
                    rotationPitch = (float) (newPitch - (newPitch - Thunderhack.rotationManager.getServerPitch()) % gcdFix);
                    break;
                }
                case FunnyGame: {
                    float[] ncp = SilentRotationUtil.calcAngle(getVector(base));
                    if (ncp != null && !AutoGApple.stopAura) {
                        rotationYaw = ncp[0];
                        rotationPitch = ncp[1];
                    }
                    break;
                }
                case AAC: {
                    if (attackContext) {
                        int pitchDeltaAbs = (int) Math.abs(pitchDelta);
                        float newYaw = Thunderhack.rotationManager.getServerYaw() + (yawDelta > 0 ? yawDeltaAbs : -yawDeltaAbs) * sensitivity;
                        float newPitch = clamp(Thunderhack.rotationManager.getServerPitch() + (pitchDelta > 0 ? pitchDeltaAbs : -pitchDeltaAbs) * sensitivity, -90, 90);
                        rotationYaw = newYaw;
                        rotationPitch = newPitch;
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPostPlayerUpdate(PostPlayerUpdateEvent event) {
        if (criticals_autojump.getValue()) {
            if (mc.player.onGround && !isInLiquid() && !mc.player.isOnLadder() && !((IEntity)mc.player).isInWeb()  && !mc.player.isPotionActive(MobEffects.SLOWNESS) && target != null && criticals_autojump.getValue()) {
                mc.player.jump();
            }
        }
    }

    private Color getTargetColor(Color color1, Color color2, int offset){
        return TwoColoreffect(color1, color2, Math.abs(System.currentTimeMillis() / 10) / 100.0 + offset * ((20f - colorOffset1.getValue()) / 200) );
    }



    public enum rotmod {
        Matrix, AAC, FunnyGame, Matrix2, SunRise, Matrix3
    }


    public enum CritMode {
        WexSide, Simple
    }


    public enum AutoSwitch {
        None, Default
    }

    public enum PointsMode {
        Distance, Angle
    }


    public enum TimingMode {
        Default, Old
    }


    public enum RayTracingMode {
        NewJitter, New, Old, OldJitter, Beta
    }

    public enum Hitbox {
        HEAD, CHEST, LEGS
    }
}
