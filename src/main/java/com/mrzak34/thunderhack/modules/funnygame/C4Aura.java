package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.phobos.RayTraceUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class C4Aura extends Module {

    public static EntityPlayer target = null;
    public Setting<Float> mindmg = this.register(new Setting<>("mindmg", 6.0f, 0.0f, 20.0f));
    public Setting<Float> maxself = this.register(new Setting<>("maxselfdmg", 6.0f, 0.0f, 20.0f));
    public Setting<Float> stophp = this.register(new Setting<>("StopHp", 6.0f, 0.0f, 20.0f));
    public Setting<Float> ddd2 = this.register(new Setting<>("TrgtRange", 1.0f, 3f, 15.0f));
    public Setting<Float> rang = this.register(new Setting<>("Range", 1.0f, 0.1f, 10.0f));
    public Setting<Integer> placedelay = this.register(new Setting<>("PlaceDelay", 100, 0, 1000));
    public Setting<Boolean> placeinside = register(new Setting<>("placeInside", true));
    public Setting<Boolean> autoBurrow = register(new Setting<>("AutoBurrow", true));


    Timer placeTimer = new Timer();
    Timer getpostim = new Timer();
    List<BlockPos> positions = null;
    BlockPos renderblockpos;

    public C4Aura() {
        super("C4Aura", "Ставит с4", Category.FUNNYGAME, true, false, false);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion e) {
        if (fullNullCheck()) return;

        if(autoBurrow.getValue() && mc.player.isSneaking()){
            if(findC4() != -1){
                    if(!canPlaceC4(new BlockPos(mc.player))) return;
                    mc.player.inventory.currentItem = 0;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(findC4()));
                    mc.player.rotationPitch = 90;
                    PlayerUtils.centerPlayer(mc.player.getPositionVector());
                    BlockUtils.placeBlockSmartRotate(new BlockPos(mc.player),EnumHand.MAIN_HAND,false,true,false,null);
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(0));
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
        if (getpostim.passedMs(300)) {
            positions = getPositions(mc.player, rang.getValue());
            getpostim.reset();
        }

        if (target != null && positions != null) {
            if (getBestPos(positions, target) != null) {
                BlockPos blockoftheblocks = getBestPos(positions, target);
                placeC4(blockoftheblocks, e);
            }
        } else {
            renderblockpos = null;
        }
        sneak_fix = stopSneaking(sneak_fix);
    }

    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && EntityUtil.mc.player != null) {
            EntityUtil.mc.player.connection.sendPacket(new CPacketEntityAction(EntityUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }

    boolean sneak_fix;

    public void placeC4(BlockPos bp, EventPreMotion ed) {
        mc.player.inventory.currentItem = findC4();
        mc.playerController.syncCurrentPlayItem();

        renderblockpos = bp;
        if (placeTimer.passedMs(placedelay.getValue())) {
            sneak_fix =  BlockUtils.placeBlockSmartRotate(bp, EnumHand.MAIN_HAND, true, false, sneak_fix, ed);
            placeTimer.reset();
        }
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), EnumFacing.UP));
    }

    private List<BlockPos> getPositions(Entity entity2, float range) {
        ArrayList<BlockPos> arrayList = new ArrayList<>();
        int playerX = (int) entity2.posX;
        int playerY = (int) entity2.posY;
        int playerZ = (int) entity2.posZ;
        int n4 = (int) (range);
        double playerX1 = entity2.posX - 0.5D;
        double playerY1 = entity2.posY + (double) entity2.getEyeHeight() - 1.0D;
        double playerZ1 = entity2.posZ - 0.5D;
        for (int n5 = playerX - n4; n5 <= playerX + n4; ++n5) {
            for (int n6 = playerZ - n4; n6 <= playerZ + n4; ++n6) {
                for (int n8 = playerY - n4; n8 < playerY + n4; ++n8) {
                    if (((double) n5 - playerX1) * ((double) n5 - playerX1) + ((double) n8 - playerY1) * ((double) n8 - playerY1) + ((double) n6 - playerZ1) * ((double) n6 - playerZ1) <= (double) (range * range) && canPlaceC4(new BlockPos(n5, n8, n6))) {
                        arrayList.add(new BlockPos(n5, n8, n6));
                    }
                }
            }
        }
        return arrayList;
    }


    private boolean canPlaceC4(BlockPos bp) {
        if (mc.player.getDistance(bp.x, bp.y, bp.z) > rang.getValue()) {
            return false;
        }
        /*
        if(!rtx(bp.add(0,-1,0)) && rtxxx.getValue()){
            return false;
        }

         */
        if (target != null) {
            BlockPos jew = new BlockPos(target);

            if (Objects.equals(bp, jew) && !placeinside.getValue()) {
                return false;
            }

            if (Objects.equals(bp, jew.add(0, 1, 0))) {
                return false;
            }
        }
        if (CrystalUtils.calculateDamage2(bp, mc.player) > maxself.getValue()) {
            return false;
        }

        if (mc.world.getBlockState(bp).getBlock() == Blocks.SNOW_LAYER) {
            return true;
        }

        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.DOUBLE_PLANT) {
            return false;
        }

        if (mc.world.getBlockState(bp).getBlock() == Blocks.SKULL) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.SKULL) {
            return false;
        }

        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.AIR) {
            return false;
        }

        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.TALLGRASS) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, 1, 0)).getBlock() == Blocks.SKULL) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.FLOWER_POT) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.RED_FLOWER) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.CHORUS_FLOWER) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.CHORUS_PLANT) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.YELLOW_FLOWER) {
            return false;
        }
        if (mc.world.getBlockState(bp).getBlock() == Blocks.PORTAL) {
            return false;
        }
        if (mc.world.getBlockState(bp).getBlock() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            return false;
        }
        if (mc.world.getBlockState(bp).getBlock() == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            return false;
        }
        if (mc.world.getBlockState(bp).getBlock() == Blocks.STONE_PRESSURE_PLATE) {
            return false;
        }
        if (mc.world.getBlockState(bp).getBlock() == Blocks.WOODEN_PRESSURE_PLATE) {
            return false;
        }
        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.WATER) {
            if (mc.world.getBlockState(bp.add(0, -2, 0)).getBlock() == Blocks.WATER) {
                return false;
            }
        }

        if (mc.world.getBlockState(bp.add(0, -1, 0)).getBlock() == Blocks.LAVA) {
            return false;
        }
        if (mc.world.getBlockState(bp).getBlock() == Blocks.LADDER) {
            return false;
        }
        if (mc.world.getBlockState(bp).getBlock() == Blocks.TALLGRASS) {
            return true;
        }
        return mc.world.getBlockState(bp).getBlock() == Blocks.AIR;
    }


    private float getDifDamage(float f) {
        return Math.min(f / 2.0f + 1.0f, f);
    }

    public float getDamage(double d7, double d2, double d3, EntityLivingBase entityLivingBase) {
        float f;
        double d5 = entityLivingBase.getDistance(d7, d2, d3) / 12.0;
        double d6 = entityLivingBase.world.getBlockDensity(new Vec3d(d7, d2, d3), entityLivingBase.getEntityBoundingBox());
        d5 = (1.0 - d5) * d6;
        f = (int) ((d5 * d5 + d5) / 2.0 * 7.0 * 12.0 + 1.0);
        f = getDifDamage(f);
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
            if (getDamage(pos1.x, pos1.y, pos1.z, nigger) > bestdmg) {
                bestdmg = getDamage(pos1.x, pos1.y, pos1.z, nigger);
                pos = pos1;
            }
        }
        return pos;
    }


    public EntityPlayer findTarget() {
        EntityPlayer target = null;
        double distance = ddd2.getValue() * ddd2.getValue();
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
            renderdmg(renderblockpos, target);
        }
    }

    public void renderdmg(BlockPos aboba, EntityPlayer target) {
        try {
            DecimalFormat df = new DecimalFormat("0.0");
            RenderUtil.drawBlockOutline(aboba, new Color(0x05FDCE), 3f, true);


            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            try {
                RenderUtil.glBillboardDistanceScaled((float) aboba.getX() + 0.5f, (float) aboba.getY() + 0.5f, (float) aboba.getZ() + 0.5f, mc.player, 1);
            } catch (Exception ignored) {
            }
            FontRender.drawString3(df.format(getDamage(aboba.x, aboba.y, aboba.z, target)), (int) -(FontRender.getStringWidth(df.format(getDamage(aboba.x, aboba.y + 1, aboba.z, target))) / 2.0D), -4, -1);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        } catch (Exception ignored) {

        }
    }

    private int findC4() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem().getItemStackDisplayName(itemStack).contains("Рычаг")) && !(itemStack.getItem().getItemStackDisplayName(itemStack).contains("Lever")))
                continue;
            return i;
        }
        return -1;
    }


    /*
    private boolean rtx(BlockPos bp){
        // Центр
        RayTraceResult result1 = mc.world.rayTraceBlocks(getEyePos(mc.player), new Vec3d(bp.getX() + 0.5, bp.getY(), bp.getZ() + 0.5), false, true, false);

        // Угол 0 0
        RayTraceResult result2 = mc.world.rayTraceBlocks(getEyePos(mc.player), new Vec3d(bp.getX(), bp.getY(), bp.getZ()), false, true, false);

        // Угол 1 0
        RayTraceResult result3 = mc.world.rayTraceBlocks(getEyePos(mc.player), new Vec3d(bp.getX() + 1, bp.getY(), bp.getZ()), false, true, false);

        // Угол 1 1
        RayTraceResult result4 = mc.world.rayTraceBlocks(getEyePos(mc.player), new Vec3d(bp.getX() + 1, bp.getY(), bp.getZ() + 1), false, true, false);

        // Угол 0 1
        RayTraceResult result5 = mc.world.rayTraceBlocks(getEyePos(mc.player), new Vec3d(bp.getX(), bp.getY(), bp.getZ() + 1), false, true, false);


        return (result1 != null && result1.typeOfHit == RayTraceResult.Type.BLOCK && result1.getBlockPos().equals(bp))
                || (result2 != null && result2.typeOfHit == RayTraceResult.Type.BLOCK && result2.getBlockPos().equals(bp))
                || (result3 != null && result3.typeOfHit == RayTraceResult.Type.BLOCK && result3.getBlockPos().equals(bp))
                || (result4 != null && result4.typeOfHit == RayTraceResult.Type.BLOCK && result4.getBlockPos().equals(bp))
                || (result5 != null && result5.typeOfHit == RayTraceResult.Type.BLOCK && result5.getBlockPos().equals(bp));
    }

     */
}



