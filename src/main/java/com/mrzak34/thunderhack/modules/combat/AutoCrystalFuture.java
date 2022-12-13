package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.modules.Module;

public class AutoCrystalFuture extends Module {


    public AutoCrystalFuture() {
        super("AutoCrystal", "AutoCrystal", Category.COMBAT, true, false, false);
    }

/*


    //TODO получить дамаг согласно сложности
    private float getDifDamage(float f) {
        EnumDifficulty enumDifficulty = mc.world.getDifficulty();
        if (enumDifficulty == EnumDifficulty.PEACEFUL) {
            f = 0.0f;
            return 0.0f;
        }
        if (enumDifficulty == EnumDifficulty.EASY) {
            f = Math.min(f / 2.0f + 1.0f, f);
            return f;
        }
        if (enumDifficulty == EnumDifficulty.HARD) {
            f = f * 3.0f / 2.0f;
        }
        return f;
    }


    //Кажется оно
    private List<BlockPos> getPositionToPlace(Entity entity2, float f) {
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        int entity = 0;
        int playerX = (int)entity2.posX;
        int playerY = (int)entity2.posY;
        int playerZ = (int)entity2.posZ;

        int n4 = (int)(f + 2.0f); //че за хуйня

        double playerX1 = entity2.posX - 0.5;
        double playerY1 = entity2.posY + (double)entity2.getEyeHeight() - 1.0;
        double playerZ1 = entity2.posZ - 0.5;

        // int n5 = entity = n - n4;
        int n5 =  playerX - n4;


        while (n5 <= playerX + n4) {
            int n6 = playerZ - n4;
            while (n6 <= playerZ + n4) {
                int n7 = 0;
                int n8 = playerY - n4;
                while (n8 < playerY + n4) {
                    int n9 = 0;
                    float f2 = f;
                    if (((double)entity - playerX1) * ((double)entity - playerX1) + ((double)n9 - playerY1) * ((double)n9 - playerY1) + ((double)n7 - playerZ1) * ((double)n7 - playerZ1) <= (double)(f2 * f2)) {
                        arrayList.add(new BlockPos(entity, n9, n7));
                    }
                    n8 = ++n9;
                }
                n6 = ++n7;
            }
            n5 = ++entity;
        }
        return arrayList;
    }






    //TODO поиск цели
    private iE f$a() {
        var1_1 = null;
        var2_2 = 0.5;
        var4_3 = Pg.f$a().iterator();
        block0: while (true) {
            v0 = var4_3;
            while (v0.hasNext()) {
                var5_4 = var4_3.next();
                if (!(var5_4 instanceof EntityLivingBase)) continue block0;
                if (!0.f$E((EntityLivingBase)(var5_4 = (EntityLivingBase)var5_4))) {
                    v0 = var4_3;
                    continue;
                }

                var6_5 = 0.f$E((Entity)hF.f$d.player, 0.f$A.f$E().floatValue()).iterator();
                block2: while (true) {
                    v1 = var6_5;
                    while (true) {
                        if (v1.hasNext()) ** break;
                        continue block0;
                        v2 = var7_6 = var6_5.next();
                        var8_7 = (double)v2.func_177958_n() + 0.5;
                        if (var5_4.func_70092_e(var8_7, var10_8 = (double)v2.func_177956_o() + 1.0, var12_9 = (double)v2.func_177952_p() + 0.5) >= 0.f$B.f$E().doubleValue() * 0.f$B.f$E().doubleValue()) continue block2;
                        if (!0.f$E(var7_6)) {
                            v1 = var6_5;
                            continue;
                        }
                        var14_10 = 0.f$E(var8_7, var10_8, var12_9, (EntityLivingBase)var5_4);
                        if (v3 < 0.f$a.f$E().doubleValue() && var14_10 * (1.0 + 0.f$C.f$E().doubleValue()) < (double)(var5_4.getHealth() + var5_4.getAbsorptionAmount())) {
                            v1 = var6_5;
                            continue;
                        }
                        if (!(var14_10 > var2_2)) continue block2;
                        var8_7 = 0.f$E(var8_7, var10_8, var12_9, (EntityLivingBase)hF.f$d.player);
                        if (v4 > var14_10 && var14_10 < (double)(var5_4.getHealth() + var5_4.getAbsorptionAmount())) continue block2;
                        if (!((double)(hF.f$d.player.func_110143_aJ() + hF.f$d.player.func_110139_bj()) - var8_7 <= 0.5)) break;
                        v1 = var6_5;
                    }
                    var2_2 = var14_10;
                    var1_1 = var7_6;
                }
            }
            break;
        }
        if (var1_1 == null) {
            return null;
        }
        return new iE(var1_1, var2_2, null);
    }



    //TODO может ли поставить блок
    private boolean canPlaceCrystal(BlockPos blockPos) {
        if (0.f$l.f$E().booleanValue() && !CG.f$e(blockPos)) {
            return false;
        }
        Block block = mc.world.getBlockState(blockPos).getBlock();
        BlockPos blockPos2 = blockPos;
        blockPos = blockPos2.up(1);
        BlockPos blockPos3 = blockPos2.up(2);
        if ((block.equals(Blocks.OBSIDIAN) || block.equals(Blocks.BEDROCK)) && mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(blockPos3).getBlock().equals(Blocks.AIR)) {
            if (mc.world.getCollisionBoxes(mc.player, new AxisAlignedBB(blockPos, blockPos3.add(1, 1, 1))).isEmpty()) {
                return true;
            }
        }
        return false;
    }


    //TODO проверка на валидность цели
    private boolean f$E(EntityLivingBase entityLivingBase) {
        if (Pg.f$E((Entity)entityLivingBase)) {
            Ze ze = (Ze)kH.f$E().f$E().f$E(Ze.class);
            if (entityLivingBase instanceof EntityPlayer && 0.f$J.f$E().booleanValue() && entityLivingBase != hF.f$d.player && entityLivingBase.func_145782_y() != -1337 && ze != null && (!ze.f$h.f$E().booleanValue() || !kH.f$E().f$E().f$E(entityLivingBase.func_70005_c_()))) {
                return true;
            }
            if ((BI.f$M((Entity)entityLivingBase) || BI.f$C((Entity)entityLivingBase)) && 0.f$E.f$E().booleanValue()) {
                return true;
            }
            if (BI.f$j((Entity)entityLivingBase) && 0.f$H.f$E().booleanValue()) {
                return true;
            }
            if (BI.f$I((Entity)entityLivingBase) && 0.f$m.f$E().booleanValue()) {
                return true;
            }
            return false;
        }
        return false;
    }


    //TODO найти кристал в руке
    private int f$a() {
        int n;
        if (hF.f$d.player.field_71071_by.getCurrentItem().getItem() instanceof ItemEndCrystal) {
            return hF.f$d.player.field_71071_by.currentItem;
        }
        int n2 = n = 0;
        while (n2 < 9) {
            if (hF.f$d.player.field_71071_by.getStackInSlot(n).getItem() instanceof ItemEndCrystal) {
                return n;
            }
            n2 = ++n;
        }
        return -1;
    }



    //TODO он дисейбл
    @Override
    public void onDisable() {
        hF hF2;
        hF hF3 = hF2;
        super.f$B();
        hF3.f$j = null;
        hF3.f$i = null;
    }



    private float getDamage(EntityEnderCrystal entityEnderCrystal, EntityLivingBase entityLivingBase) {
        return getDamage(entityEnderCrystal.getPosition().x, entityEnderCrystal.getPosition().y, entityEnderCrystal.getPosition().z, entityLivingBase);
    }




    //TODO гетать урон с позиции

    public float getDamage(double d7, double d2, double d3, EntityLivingBase entityLivingBase) {
        float f = 12.0f;
        double d5 = entityLivingBase.getDistance(d7, d2, d3) / 12.0;
        double d6 = entityLivingBase.world.getBlockDensity(new Vec3d(d7, d2, d3), entityLivingBase.getEntityBoundingBox());
        d5 = (1.0 - d5) * d6;
        f = (int)((d5 * d5 + d5) / 2.0 * 7.0 * 12.0 + 1.0);
        DamageSource dmsrc = DamageSource.causeExplosionDamage(new Explosion((World) mc.world, (Entity) mc.player, d7, d2, d3, 6.0f, false, true));
        f = CombatRules.getDamageAfterAbsorb(f, (float)entityLivingBase.getTotalArmorValue(), (float)((float)entityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
        int n = EnchantmentHelper.getEnchantmentModifierDamage(entityLivingBase.getArmorInventoryList(), dmsrc);
        if (n > 0) {
            f = CombatRules.getDamageAfterMagicAbsorb(f, (float)n);
        }
        if (entityLivingBase.getActivePotionEffect(MobEffects.RESISTANCE) != null) {
            f = f * (float)(25 - (entityLivingBase.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5) / 25.0f;
        }
        f = Math.max(f, 0.0f);
        return f;
    }

 */

}
