package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventPostSync;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.mixin.ducks.IPlayerControllerMP;
import com.mrzak34.thunderhack.mixin.mixins.IMinecraft;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mrzak34.thunderhack.util.InteractionUtil.*;


public class C4Aura extends Module {

    public Setting<Float> mindmg = this.register(new Setting<>("MinDamage", 6.0f, 0.0f, 20.0f));
    public Setting<Float> maxSelfDmg = this.register(new Setting<>("MaxSelfDamage", 6.0f, 0.0f, 20.0f));
    public Setting<Float> stophp = this.register(new Setting<>("StopHp", 6.0f, 0.0f, 20.0f));
    public Setting<Float> targetRange = this.register(new Setting<>("TargetRange", 8.0f, 3f, 15.0f));
    public Setting<Boolean> placeinside = register(new Setting<>("placeInside", true));
    public Setting<Boolean> autoBurrow = register(new Setting<>("AutoBurrow", true));

    private List<BlockPos> positions = null;
    private BlockPos renderblockpos;
    private BlockPos postSyncPlace;
    public static EntityPlayer target = null;


    public C4Aura() {
        super("C4Aura", "Ставит с4","mcfunny.su only", Category.FUNNYGAME);
    }


    @SubscribeEvent
    public void onEntitySync(EventSync e) {
        if (fullNullCheck()) return;
        if (autoBurrow.getValue() && mc.player.isSneaking()) {
            if (findC4() != -1) {
                if (!canPlaceC4(new BlockPos(mc.player))) return;
                mc.player.rotationPitch = 90;
                PlayerUtils.centerPlayer(mc.player.getPositionVector());
                return;
            }
        }
        if (stophp.getValue() >= mc.player.getHealth()) {
            return;
        }
        if (findC4() == -1) {
            return;
        }
        target = findTarget();
        if (mc.player.ticksExisted % 5 == 0) {
            positions = getPositions(mc.player);
        }
        if (target != null && positions != null) {
            BlockPos bp = getBestPos(positions, target);
            if (bp != null) {
                placePre(bp);
            }
            if (mc.player.getHeldItemMainhand().getDisplayName().contains("C4") && !mc.player.getHeldItemMainhand().getDisplayName().contains("2")){
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), EnumFacing.UP));
            }
        } else {
            renderblockpos = null;
        }
    }

    @SubscribeEvent
    public void postSync(EventPostSync event){
        if (fullNullCheck()) return;
        if (autoBurrow.getValue() && mc.player.isSneaking()) {
            if (findC4() != -1) {
                if (!canPlaceC4(new BlockPos(mc.player))) return;
                mc.player.inventory.currentItem = 2;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(findC4()));
                placePost(new BlockPos(mc.player));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(2));
                return;
            }
        }
        if(postSyncPlace != null){
            placePost(postSyncPlace);
        }
    }


    private List<BlockPos> getPositions(Entity entity2) {
        ArrayList<BlockPos> arrayList = new ArrayList<>();
        int playerX = (int) entity2.posX;
        int playerY = (int) entity2.posY;
        int playerZ = (int) entity2.posZ;
        int n4 = 4;
        double playerX1 = entity2.posX - 0.5D;
        double playerY1 = entity2.posY + (double) entity2.getEyeHeight() - 1.0D;
        double playerZ1 = entity2.posZ - 0.5D;
        for (int n5 = playerX - n4; n5 <= playerX + n4; ++n5) {
            for (int n6 = playerZ - n4; n6 <= playerZ + n4; ++n6) {
                for (int n8 = playerY - n4; n8 < playerY + n4; ++n8) {
                    if (((double) n5 - playerX1) * ((double) n5 - playerX1) + ((double) n8 - playerY1) * ((double) n8 - playerY1) + ((double) n6 - playerZ1) * ((double) n6 - playerZ1) <= (double) ((float) 4.0 * (float) 4.0) && canPlaceC4(new BlockPos(n5, n8, n6))) {
                        arrayList.add(new BlockPos(n5, n8, n6));
                    }
                }
            }
        }
        return arrayList;
    }


    private boolean canPlaceC4(BlockPos bp) {
        if (mc.player.getDistanceSq(bp.getX(), bp.getY(), bp.getZ()) > 16) {
            return false;
        }
        if (target != null) {
            BlockPos jew = new BlockPos(target);
            if (Objects.equals(bp, jew) && !placeinside.getValue()) {
                return false;
            }
            if (Objects.equals(bp, jew.add(0, 1, 0))) {
                return false;
            }
        }
        if(getDamage(bp.getX(),bp.getY(),bp.getZ(),mc.player) > maxSelfDmg.getValue()){
            return false;
        }
        if(!mc.world.getBlockState(bp).getMaterial().isReplaceable()){
            return false;
        }
        if(mc.world.getBlockState(bp.down()).getBlock() == Blocks.SKULL ){
            return false;
        }
        if(mc.world.getBlockState(bp.down()).getBlock() == Blocks.LEVER ){
            return false;
        }
        if(mc.player.getHeldItemMainhand().getDisplayName().contains("C4") && mc.player.getHeldItemMainhand().getDisplayName().contains("0")){
            return false;
        }
        return mc.world.getBlockState(bp).getBlock() == Blocks.AIR && mc.world.getBlockState(bp.down()).getBlock() != Blocks.AIR;
    }


    public float getDamage(double d7, double d2, double d3, EntityLivingBase entityLivingBase) {
        float f;
        double d5 = entityLivingBase.getDistance(d7, d2, d3) / 12.0;
        double d6 = entityLivingBase.world.getBlockDensity(new Vec3d(d7, d2, d3), entityLivingBase.getEntityBoundingBox());
        d5 = (1.0 - d5) * d6;
        f = (int) ((d5 * d5 + d5) / 2.0 * 7.0 * 12.0 + 1.0);
        f = Math.min(f / 2.0f + 1.0f, f);
        DamageSource dmgg = DamageSource.causeExplosionDamage(new Explosion(mc.world, mc.player, d7, d2, d3, 6.0f, false, true));
        f = CombatRules.getDamageAfterAbsorb(f, (float) entityLivingBase.getTotalArmorValue(), (float) entityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        int n = EnchantmentHelper.getEnchantmentModifierDamage(entityLivingBase.getArmorInventoryList(), dmgg);
        if (n > 0) {
            f = CombatRules.getDamageAfterMagicAbsorb(f, (float) n);
        }
        if (entityLivingBase.getActivePotionEffect(MobEffects.RESISTANCE) != null) {
            f = f * (float) (25 - (Objects.requireNonNull(entityLivingBase.getActivePotionEffect(MobEffects.RESISTANCE)).getAmplifier() + 1) * 5) / 25.0f;
        }
        f = Math.max(f, 0.0f);
        return f;
    }


    public BlockPos getBestPos(List<BlockPos> vsepos, EntityPlayer nigger) {
        BlockPos pos = null;
        double bestdmg = mindmg.getValue();
        for (BlockPos pos1 : vsepos) {
            if (getDamage(pos1.getX(), pos1.getY(), pos1.getZ(), nigger) > bestdmg) {
                bestdmg = getDamage(pos1.getX(), pos1.getY(), pos1.getZ(), nigger);
                pos = pos1;
            }
        }
        return pos;
    }


    public EntityPlayer findTarget() {
        EntityPlayer target = null;
        double distance = targetRange.getPow2Value();
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (entity == mc.player) {
                continue;
            }
            if (Thunderhack.friendManager.isFriend(entity)) {
                continue;
            }
            if (mc.player.getDistanceSq(entity) <= distance) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
            }
        }
        return target;
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent e) {
        if (mc.player == null && mc.world == null) {
            return;
        }
        if (renderblockpos != null && target != null) {
            try {
                DecimalFormat df = new DecimalFormat("0.0");
                RenderUtil.drawBlockOutline(renderblockpos, new Color(0x05FDCE), 3f, true, 0);

                GlStateManager.pushMatrix();
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                RenderUtil.glBillboardDistanceScaled((float) renderblockpos.getX() + 0.5f, (float) renderblockpos.getY() + 0.5f, (float) renderblockpos.getZ() + 0.5f, mc.player, 1);
                FontRender.drawString3(df.format(getDamage(renderblockpos.getX(), renderblockpos.getY(), renderblockpos.getZ(), target)), (int) -(FontRender.getStringWidth(df.format(getDamage(renderblockpos.getX(), renderblockpos.getY() + 1, renderblockpos.getZ(), target))) / 2.0D), -4, -1);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
            } catch (Exception ignored) {

            }
        }
    }

    private int findC4() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() != Item.getItemFromBlock(Blocks.LEVER)) continue;
            if (!(itemStack.getDisplayName().contains("C4"))) continue;
            return i;
        }
        return -1;
    }

    public void placePre(BlockPos position) {
        mc.player.inventory.currentItem = findC4();
        ((IPlayerControllerMP)mc.playerController).syncItem();
        renderblockpos = position;
        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos directionOffset = position.offset(direction);
            EnumFacing oppositeFacing = direction.getOpposite();
            if (mc.world.getBlockState(directionOffset).getMaterial().isReplaceable()) continue;
            float[] rotation = getAnglesToBlock(directionOffset, oppositeFacing);
            Vec3d interactVector = null;
            RayTraceResult result = getTraceResult(4f, rotation[0],rotation[1]);
            if (result != null && result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                interactVector = result.hitVec;
            }
            if (interactVector == null) {
                interactVector = new Vec3d(directionOffset).add(0.5, 0.5, 0.5);
                rotation = calculateAngles(interactVector);
            }
            mc.player.rotationYaw = rotation[0];
            mc.player.rotationPitch = rotation[1];
            postSyncPlace = position;
            break;
        }
    }


    public void placePost(BlockPos position) {
        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos directionOffset = position.offset(direction);
            EnumFacing oppositeFacing = direction.getOpposite();
            if (mc.world.getBlockState(directionOffset).getMaterial().isReplaceable()) continue;
            boolean sprint = mc.player.isSprinting();
            if (sprint) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            boolean sneak = mc.world.getBlockState(directionOffset).getBlock().onBlockActivated(mc.world, directionOffset, mc.world.getBlockState(directionOffset), mc.player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
            if (sneak) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            float[] rotation = getAnglesToBlock(directionOffset, oppositeFacing);
            Vec3d interactVector = null;
            RayTraceResult result = getTraceResult(4f, rotation[0],rotation[1]);
            if (result != null && result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                interactVector = result.hitVec;
            }
            if (interactVector == null) {
                interactVector = new Vec3d(directionOffset).add(0.5, 0.5, 0.5);
            }
            mc.playerController.processRightClickBlock(mc.player, mc.world, directionOffset, direction.getOpposite(), interactVector, EnumHand.MAIN_HAND);
            if (sneak) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            if (sprint) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), EnumFacing.UP));
            ((IMinecraft) mc).setRightClickDelayTimer(4);
            postSyncPlace = null;
            break;
        }
    }


    public static float[] calculateAngles(Vec3d to) {
        float yaw = (float) (Math.toDegrees(Math.atan2(to.subtract(mc.player.getPositionEyes(1)).z, to.subtract(mc.player.getPositionEyes(1)).x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(to.subtract(mc.player.getPositionEyes(1)).y, Math.hypot(to.subtract(mc.player.getPositionEyes(1)).x, to.subtract(mc.player.getPositionEyes(1)).z)));
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }
}





