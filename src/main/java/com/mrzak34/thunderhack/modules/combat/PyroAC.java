package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.modules.Module;

public class PyroAC extends Module {
    public PyroAC() {
        super("AutoCrystal", "AutoCrystal", Category.COMBAT, true, false, false);

      //  this.Field6130 = new Timer();
      //  this.Field6132 = -1337.0f;
      //  this.Field6133 = -1337.0f;
      //  this.Field6136 = new CopyOnWriteArrayList();
     //   this.Field6137 = new Timer();
      //  this.Field6138 = -1;
      //  this.Field6140 = new ConcurrentLinkedQueue();
      //  this.Field6141 = new Timer();
    }


    /*
    public BindSetting Field6102 = new BindSetting("forceFacePlace", "ForceFacePlace", "Force's you to faceplace when this key is held down", -1);
    public f0l Field6125 = new f0l("targetColor", "Target", "Tint to render the targeted entity", new f00(0.85f, 0.85f, 1.0f, 0.166666f));
    public f0l Field6128 = new f0l("placeOverlayColor", "Overlay Color", "Tint to render underneath the block being placed", new f00(0.0f, 1.0f, 0.5f, 0.3f));
    public f0l Field6129 = new f0l("placeOutlineColor", "Outline", "Tint to render underneath the block being placed", new f00(0.0f, 1.0f, 0.5f, 1.0f));

     */

    /*
    private Setting<bmodeEn> Field6087 = register(new Setting("breakMode", bmodeEn.Smart));
    private Setting<switchmodeEn> Field6105 = register(new Setting("switchMode", switchmodeEn.Auto));

    public enum bmodeEn {
        Smart,
        Semi,

        OnlyOwn
    }
    public enum switchmodeEn {
        Auto,
        None,
        Packet
    }

    public Setting<Integer> Field6088 = this.register(new Setting<Integer>("breakTickDelay",0, 0, 10));
    public Setting<Integer> Field6089 = this.register(new Setting<Integer>("placeTickDelay",  0, 0, 10));
    public Setting<Integer> Field6101 = this.register(new Setting<Integer>("facePlaceHP", 9, 0, 20));
    public Setting<Integer> Field6103 = this.register(new Setting<Integer>("lethalMult", 0, 0, 10));

    public Setting<Boolean> Field6127 = this.register(new Setting<Boolean>("renderDamage", false));
    public Setting<Boolean> Field6126 = this.register(new Setting<Boolean>("blockOverlay", false));
    public Setting<Boolean> Field6124 = this.register(new Setting<Boolean>("chams", false));
    public Setting<Boolean> Field6123 = this.register(new Setting<Boolean>("fpsFix", false));
    public Setting<Boolean> Field6122 = this.register(new Setting<Boolean>("strict", false));
    public Setting<Boolean> Field6121 = this.register(new Setting<Boolean>("searchOffline", false));
    public Setting<Boolean> Field6120 = this.register(new Setting<Boolean>("placeInLiquid", false));
    public Setting<Boolean> Field6119 = this.register(new Setting<Boolean>("1.13", false));
    public Setting<Boolean> Field6118 = this.register(new Setting<Boolean>("autoPlace", false));
    public Setting<Boolean> Field6116 = this.register(new Setting<Boolean>("pauseWhileMining", false));
    public Setting<Boolean> Field6115 = this.register(new Setting<Boolean>("pauseWhileXPing", false));
    public Setting<Boolean> Field6114 = this.register(new Setting<Boolean>("pauseWhileEating", false));
    public Setting<Boolean> Field6113 = this.register(new Setting<Boolean>("armorCheck", false));
    public Setting<Boolean> Field6111 = this.register(new Setting<Boolean>("players", false));
    public Setting<Boolean> Field6112 = this.register(new Setting<Boolean>("onlyInFrustram", false));
    public Setting<Boolean> Field6110 = this.register(new Setting<Boolean>("offhandBreak", false));
    public Setting<Boolean> Field6109 = this.register(new Setting<Boolean>("rotate", false));
    public Setting<Boolean> Field6108 = this.register(new Setting<Boolean>("antiweakness", false));
    public Setting<Boolean> Field6106 = this.register(new Setting<Boolean>("armorBreaker", false));
    public Setting<Boolean> Field6104 = this.register(new Setting<Boolean>("throughWalls", false));
    public Setting<Boolean> Field6094 = this.register(new Setting<Boolean>("NCP", false));
    public Setting<Boolean> Field6096 = this.register(new Setting<Boolean>("AK47", false));
    public Setting<Boolean> Field6099 = this.register(new Setting<Boolean>("NoSuicide", false));
    public Setting<Boolean> Field6100 = this.register(new Setting<Boolean>("IgnoreSelfDamage", false));


    public Setting<Float> Field6090 = this.register(new Setting<Float>("randomDelay", 0.0f, 0.0f, 1.0f));
    public Setting<Float> Field6091 = this.register(new Setting<Float>("breakRange", 6.0f, 1.0f, 6.0f));
    public Setting<Float> Field6092 = this.register(new Setting<Float>("placeRange", 6.0f, 1.0f, 6.0f));
    public Setting<Float> Field6093 = this.register(new Setting<Float>("enemyRange", 6.0f, 0.0f, 6.0f));
    public Setting<Float> Field6095 = this.register(new Setting<Float>("wallsRange", 3.0f, 0.0f, 6.0f));
    public Setting<Float> Field6097 = this.register(new Setting<Float>("minDmg", 6.0f, 0.1f, 20.0f));
    public Setting<Float> Field6098 = this.register(new Setting<Float>("maxSelfDMG", 6.0f, 0.1f, 20.0f));
    public Setting<Float> Field6107 = this.register(new Setting<Float>("armorBreakerPct", 20.0f, 0.0f, 100.0f));
    public Setting<Float> Field6117 = this.register(new Setting<Float>("pauseWhenBelow", 0.0f, 0.0f, 20.0f));



    public Timer Field6130;
    public boolean Field6131;
    public float Field6132;
    public float Field6133;

    public f6n Field6134;

    public EntityLivingBase Field6135;

    public CopyOnWriteArrayList<Integer> Field6136;

    public Timer Field6137;
    public int Field6138;
    public boolean Field6139;

    public ConcurrentLinkedQueue<fe3<Long, BlockPos>> Field6140;

    public Timer Field6141;
    public int Field6142;
    public int Field6143;
    public double Field6144;
    public double Field6145;


    @f0g
    public void Method187(f4J f4J2) {
        this.Field6136.clear();
        this.Field6135 = null;
    }



    @SubscribeEvent
    public void Method203( EventPreMotion f4u2) {
        if (!Field6109.getValue()) {
            return;
        }
        if (this.Field6131) {
            f4u2.Method7948();
            f4u2.Method5647(this.Field6132);
            f4u2.Method5653(this.Field6133);
        }
    }



    @f0g
    public void Method277( f43 f432) {
        block29: {
            block28: {
                if (mc.player == null) {
                    return;
                }
                Ref.LongRef longRef = new Ref.LongRef();
                longRef.Field4347 = System.currentTimeMillis();
                this.Field6140.removeIf(new f6s(longRef));
                this.Field6142 += -1;
                this.Field6143 += -1;
                if (!(this.Field6121.getValue() && !((Boolean)this.Field5236.Method5264()).booleanValue())) {
                    return;
                }
                if (!this.Field6141.passedMs((long)Field6144)) {
                    return;
                }
                if (((Boolean)this.Field5236.Method5264()).booleanValue() && this.Field6130.passedMs(1000) && this.Field6131) {
                    this.Field6131 = false;
                }
                if (this.Field6137.passedMs(500) && !this.Field6136.isEmpty()) {
                    this.Field6136.clear();
                }
                if (!((Boolean)this.Field5236.Method5264()).booleanValue() || this.Method4876()) break block28;
                if (this.Method2689()) break block29;
            }
            if (((Boolean)this.Field6118).booleanValue()) {
                this.Method8858();
                if (!this.Method4876()) {
                    if (((Boolean)this.Field5236.Method5264()).booleanValue() && this.Field6134 != null && this.Field6143 <= 0) {
                        Object object;
                        if (!this.Method8872()) {
                            return;
                        }
                        EnumFacing enumFacing = null;
                        boolean bl = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
                        f6n f6n2 = this.Field6134;
                        if (f6n2.Method5106() != null) {
                            f6n f6n3 = this.Field6134;
                            feh feh2 = f6n3.Method5106();
                            object = feh2.Method786().Method891();
                            this.Field6133 = ((Rotation)object).Method6942();
                            this.Field6132 = ((Rotation)object).Method6936();
                            this.Field6131 = true;
                            f6n f6n4 = this.Field6134;
                            feh feh3 = f6n4.Method5106();
                            enumFacing = feh3.Method783();
                        } else {
                            enumFacing = few.Method848(this.Field6134, true);
                            object = few.Method835().Method850(this.Field6134, enumFacing);
                            this.Field6133 = (float)object[0];
                            this.Field6132 = (float)object[1];
                            this.Field6131 = true;
                        }
                        float f = mc.player.rotationPitch;
                        float f2 = mc.player.rotationYaw;
                        float f3 = mc.getRenderPartialTicks();
                        mc.player.rotationPitch = this.Field6132;
                        mc.player.rotationYaw = this.Field6133;
                        RayTraceResult rayTraceResult = mc.player.rayTrace((double)mc.playerController.getBlockReachDistance(), f3);
                        float f4 = 0.0f;
                        float f5 = 0.0f;
                        float f6 = 0.0f;
                        if (rayTraceResult != null) {
                            f6n f6n5 = this.Field6134;
                            f4 = (float)(rayTraceResult.hitVec.x - (double)f6n5.getX());
                            f6n f6n6 = this.Field6134;
                            f5 = (float)(rayTraceResult.hitVec.y - (double)f6n6.getY());
                            f6n f6n7 = this.Field6134;
                            f6 = (float)(rayTraceResult.hitVec.z - (double)f6n7.getZ());
                        }
                        mc.player.rotationPitch = f;
                        mc.player.rotationYaw = f2;
                        NetHandlerPlayClient netHandlerPlayClient = mc.getConnection();
                        netHandlerPlayClient.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.Field6134, enumFacing, bl ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, f4, f5, f6));
                        NetHandlerPlayClient netHandlerPlayClient2 = mc.getConnection();

                        netHandlerPlayClient2.sendPacket((Packet)new CPacketAnimation(bl ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                        this.Field6143 = Field6089.getValue();
                        this.Field6144 = RangesKt.Method4268(this.Field6090.getValue() * (double)1000, Math.random() * (double)1000);
                        if (this.Field6138 != -1 && this.Field6105.getValue() == switchmodeEn.Packet) {
                            if (this.Field6138 != mc.player.inventory.currentItem) {
                                NetHandlerPlayClient netHandlerPlayClient3 = mc.getConnection();
                                netHandlerPlayClient3.sendPacket((Packet)new CPacketHeldItemChange(mc.player.inventory.currentItem));
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void Method205(boolean bl, EntityPlayerSP entityPlayerSP, World world) {
        this.Field6136.clear();
        this.Field6140.clear();
        this.Field6142 = 0;
        this.Field6143 = 0;
    }



    /*
     * Exception decompiling
     */
    public void Method8858() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 15[UNCONDITIONALDOLOOP]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:429)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:478)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:728)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:806)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:258)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:192)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:521)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:922)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:253)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:135)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
    }


    /*
    public static boolean Method8860(AutoCrystal autoCrystal, EntityEnderCrystal entityEnderCrystal, boolean bl, int n, Object object) {
        if ((n & 2) != 0) {
            bl = false;
        }
        return autoCrystal.Method8874(entityEnderCrystal, bl);
    }

    


    public boolean Method2689() {
        boolean bl;
        Entity entity;
        block16: {
            block17: {
                if ((f6o)(this.Field6087) == f6o.Smart && this.Field6135 == null) {
                    return false;
                }
                Iterator iterator2 = mc.world.loadedEntityList.iterator();
                do {
                    if (!iterator2.hasNext()) return false;
                } while (!((entity = (Entity)iterator2.next()) instanceof EntityEnderCrystal) || entity.isDead || !AutoCrystal.Method8860(this, (EntityEnderCrystal)entity, false, 2, null) || !fdN.Method348(entity));
                boolean bl2 = few.Method835().Method852(entity, ((Number)this.Field6091).doubleValue());
                fex fex2 = null;
                if (!bl2 && !((Boolean)this.Field6109).booleanValue()) {
                    bl2 = true;
                }
                if (!bl2) {
                    fex2 = few.Method835().Method843(((EntityEnderCrystal)entity).getEntityBoundingBox(), false, false, false, (Boolean)this.Field6104);
                }
                if (fex2 == null) {
                    if (!bl2) return true;
                }
                if (!bl2) {
                    if (fex2 == null) return true;
                    this.Field6133 = fex2.Method891().Method6942();
                    this.Field6132 = fex2.Method891().Method6936();
                    this.Field6131 = true;
                    this.Field6130.Method490();
                    return true;
                }
                bl = true;
                if (!((Boolean)this.Field6108).booleanValue()) break block16;
                double d = mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                ItemStack itemStack = mc.player.getHeldItemMainhand();
                if (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemTool) {
                    d = 3.0;
                } else if (mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                    d = 0.0;
                }
                PotionEffect potionEffect = mc.player.getActivePotionEffect(MobEffects.STRENGTH);
                if (potionEffect != null && (float)potionEffect.getAmplifier() >= 1.0f) {
                    d = 1.0;
                }
                float f = EnchantmentHelper.getModifierForCreature((ItemStack)mc.player.getHeldItemMainhand(), (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED);
                float f2 = mc.player.getCooledAttackStrength(0.5f);
                boolean bl3 = bl = (d *= (double)(0.2f + f2 * f2 * 0.8f)) > (double)0.0f || (f *= f2) > 0.0f;
                if (bl) break block16;
                if (mc.player.getHeldItemMainhand().isEmpty()) break block17;
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) break block16;
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemTool) break block16;
            }
            int n = 8;
            for (int i = 0; i <= n; ++i) {
                ItemStack itemStack;
                mc.player.inventory.getStackInSlot(i);
                if (itemStack.isEmpty() || !(itemStack.getItem() instanceof ItemTool) && !(itemStack.getItem() instanceof ItemSword)) continue;
                mc.player.inventory.currentItem = i;
                mc.playerController.updateController();
                break;
            }
        }
        if (this.Field6142 > 0) return true;
        if (!bl) return true;
        this.Field6130.Method490();
        mc.playerController.attackEntity((EntityPlayer)mc.player, entity);
        if (((Boolean)this.Field6110).booleanValue()) {
            mc.player.swingArm(EnumHand.OFF_HAND);
        } else {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (((Boolean)this.Field6096).booleanValue()) {
            this.Field6136.add(((EntityEnderCrystal)entity).getEntityId());
        }
        this.Field6137.reset();
        this.Field6142 = Field6088.getValue();
        this.Field6144 = RangesKt.Method4268((Field6090.getValue()) * (double)1000, Math.random() * (double)1000);
        return true;
    }

    public boolean Method8864(EntityEnderCrystal entityEnderCrystal) {
        for (fe3<Long, BlockPos> fe32 : this.Field6140) {
            Object object = fe32.Method465();
            Object object2 = fe32.Method465();
            Object object3 = fe32.Method465();
            if (!(entityEnderCrystal.getDistance((double) ((BlockPos) object).getX() + 0.5, (double) ((BlockPos) object2).getY() + 0.5, (double) ((BlockPos) object3).getZ() + 0.5) <= (double) 3))
                continue;
            return true;
        }
        return false;
    }

    /*
     * Exception decompiling
     */


    /*
    public ArrayList Method8866() {
        ArrayList<f6n> arrayList;
        block9: {
            arrayList = new ArrayList<f6n>();
            BlockPos blockPos = new BlockPos(mc.player.getPositionVector());
            int n = (int)((float)blockPos.getY() + mc.player.getEyeHeight() - (float)10);
            int n2 = (int)((float)blockPos.getY() + mc.player.getEyeHeight() + (float)10);
            int n3 = blockPos.getX() - 10;
            int n4 = blockPos.getX() + 10;
            if (n3 > n4) break block9;
            while (true) {
                block10: {
                    int n5;
                    int n6;
                    if ((n6 = n) > (n5 = n2)) break block10;
                    while (true) {
                        block11: {
                            int n7;
                            int n8;
                            if ((n8 = blockPos.getZ() - 10) > (n7 = blockPos.getZ() + 10)) break block11;
                            while (true) {
                                block12: {
                                    BlockPos blockPos2;
                                    block14: {
                                        boolean bl;
                                        float f;
                                        feh feh2;
                                        block15: {
                                            block13: {
                                                float f2;
                                                IBlockState iBlockState;
                                                if (!this.Method4842(blockPos2 = new BlockPos(n3, n6, n8)) || (iBlockState = feg.Method701(blockPos2)).getBlock() != Blocks.OBSIDIAN && iBlockState.getBlock() != Blocks.BEDROCK || !this.Method8865(blockPos2, iBlockState, true)) break block12;
                                                float f3 = f2 = Field6100.getValue() != false ? 0.0f : fdM.Method339(blockPos2, (EntityLivingBase)mc.player);
                                                if ((double)f2 > Field6098.getValue()) break block12;
                                                if (!Field6099.getValue()) break block13;
                                                if (f2 + 1.0f >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) break block12;
                                            }
                                            if (!Field6122.getValue()) break block14;
                                            boolean bl2 = false;
                                            feh2 = null;
                                            Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.getEntityBoundingBox().minY + (double)mc.player.getEyeHeight(), mc.player.posZ);
                                            double[] arrd = new double[]{0.05, 0.95};
                                            double[] arrd2 = new double[]{0.05, 0.95};
                                            double[] arrd3 = new double[]{0.05, 0.95};
                                            for (double d : arrd) {
                                                block4: for (double d2 : arrd2) {
                                                    for (double d3 : arrd3) {
                                                        Vec3d vec3d2 = new Vec3d(blockPos2).add(d, d2, d3);
                                                        double d4 = vec3d.distanceTo(vec3d2);
                                                        double d5 = vec3d2.x - vec3d.x;
                                                        double d6 = vec3d2.y - vec3d.y;
                                                        double d7 = vec3d2.z - vec3d.z;
                                                        float f4 = MathHelper.sqrt((double)(d5 * d5 + d7 * d7));
                                                        Rotation rotation = new Rotation(MathHelper.wrapDegrees((float)((float)Math.toDegrees(MathHelper.atan2((double)d7, (double)d5)) - 90.0f)), MathHelper.wrapDegrees((float)((float)(-Math.toDegrees(MathHelper.atan2((double)d6, (double)f4))))));
                                                        Vec3d vec3d3 = few.Method835().Method834(rotation);
                                                        Vec3d vec3d4 = vec3d.add(vec3d3.x * d4, vec3d3.y * d4, vec3d3.z * d4);
                                                        RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(vec3d, vec3d4, false, false, true);
                                                        if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals(blockPos2)) continue;
                                                        bl2 = true;
                                                        feh2 = new feh(blockPos2, rayTraceResult.sideHit, rayTraceResult.hitVec.subtract((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ()), new fex(vec3d2, rotation, rayTraceResult.sideHit));
                                                        continue block4;
                                                    }
                                                }
                                            }
                                            f = 36.0f;
                                            bl = false;
                                            if (bl2) break block15;
                                            f = (float)((Field6095.getValue() * this.Field6095.getValue()));
                                            if (!this.Field6104.getValue()) break block12;
                                            bl = true;
                                        }
                                        if (!(mc.player.getDistanceSq(blockPos2) > (double)f)) {
                                            arrayList.add(new f6n(blockPos2, feh2, bl));
                                        }
                                        break block12;
                                    }
                                    arrayList.add(new f6n(blockPos2, null, false));
                                }
                                if (n8 == n7) break;
                                ++n8;
                            }
                        }
                        if (n6 == n5) break;
                        ++n6;
                    }
                }
                if (n3 == n4) break;
                ++n3;
            }
        }
        return arrayList;
    }
    

    public boolean Method2425(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) {
            return false;
        }
        if (!((EntityLivingBase)entity).isEntityAlive()) {
            return false;
        }
        if (Field6112.getValue() && !PyroRenderUtil.Field7388.isBoundingBoxInFrustum(((EntityLivingBase)entity).getEntityBoundingBox())) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (this.Field6113.getValue()) {
                boolean bl = false;
                Iterator iterator2 = ((EntityPlayer)entity).inventory.armorInventory.iterator();
                while (iterator2.hasNext()) {
                    ItemStack itemStack = (ItemStack)iterator2.next();
                    if (itemStack.isEmpty()) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    return false;
                }
            }
            if (!(this.Field6111.getValue())) return false;
            return fe4.Field250.Method387((EntityPlayer) entity);
        }
        return mc.player.getDistanceSq(entity) <= 300.0;
    }


    /*
    @Override
    public void Method195(Vec3d vec3d, float f) {
        super.Method195(vec3d, f);
        if (mc.player != null && this.Field6134 != null) {
            if (this.Field6126.getValue()) {
                fe5.Field252.Method408(7);
                f6n f6n2 = this.Field6134;
                if (f6n2 == null) {
                   // Intrinsics.Method6551();
                }
                fe5.Field252.Method410(f6n2, ((f00)this.Field6128).Method7515(), 63);
                fe5.Field252.Method405();
                PyroRenderUtil.Method12305(this.Field6134, ((f00)this.Field6129).Method7515(), 1.5f, false);
                if (this.Field6135 != null) {
                    if (Field6127.getValue()) {
                        GlStateManager.pushMatrix();
                        EntityPlayer entityPlayer = mc.getRenderViewEntity() instanceof EntityPlayer ? (EntityPlayer)mc.getRenderViewEntity() : (EntityPlayer)mc.player;
                        f6n f6n3 = this.Field6134;
                        if (f6n3 == null) {
                          //  Intrinsics.Method6551();
                        }
                        float f2 = (float)f6n3.getX() + 0.5f;
                        f6n f6n4 = this.Field6134;
                        if (f6n4 == null) {
                          //  Intrinsics.Method6551();
                        }
                        float f3 = (float)f6n4.getY() + 0.5f;
                        f6n f6n5 = this.Field6134;
                        if (f6n5 == null) {
                          //  Intrinsics.Method6551();
                        }
                        PyroRenderUtil.Method12315(f2, f3, (float)f6n5.getZ() + 0.5f, (Entity)entityPlayer, 1.0f);
                        StringCompanionObject stringCompanionObject = StringCompanionObject.Field4618;
                        String string = "%.1f";
                        Object[] arrobject = new Object[]{this.Field6145};
                        boolean bl = false;
                        String string2 = String.format(string, Arrays.copyOf(arrobject, arrobject.length));
                        GlStateManager.disableDepth();
                        GlStateManager.translate((double)(-((double)PyroRenderUtil.Method12314(string2) / 2.0)), (double)0.0, (double)0.0);
                        PyroRenderUtil.Method12313(string2, 0.0f, 0.0f, -1);
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }


    //TODO MEGAPON RENDER

    public boolean Method8872() {
        if ((mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL)) {
            if ((mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)) {
                if (Field6105.getValue() != switchmodeEn.None) {
                    int n;
                    this.Field6138 = n = fdX.Field311.Method497(Items.END_CRYSTAL);
                    if (n != -1) {
                        if (Field6105.getValue() == switchmodeEn.Auto) {
                            mc.player.inventory.currentItem = n;
                            mc.playerController.updateController();
                        } else if (Field6105.getValue() == switchmodeEn.Packet) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(n));
                        }
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public boolean Method8874(EntityEnderCrystal entityEnderCrystal, boolean bl) {
        if (!bl) {
            if (this.Field6136.contains(entityEnderCrystal.getEntityId())) {
                return false;
            }
        }
        double d = mc.player.getDistanceSq((Entity)entityEnderCrystal);
        switch (Field6087.getValue()) {
            case Smart: {
                if (this.Field6135 == null) {
                    return false;
                }
                float f = fdM.Method341(entityEnderCrystal, (EntityLivingBase)mc.player);
                float f2 = fdM.Method341(entityEnderCrystal, this.Field6135);
                if ((double)f >= Field6098.getValue() + 1.0) {
                    return false;
                }
                if (!(!this.Field6139 ? (double)f2 <= Field6097.getValue() - 1.0 : (double)f2 < 1.0)) break;
                return false;
            }
            case Semi: {
                if (this.Field6135 == null) {
                    return false;
                }
                float f = fdM.Method341(entityEnderCrystal, (EntityLivingBase)mc.player);
                float f3 = fdM.Method341(entityEnderCrystal, this.Field6135);
                if ((double)f >= Field6098.getValue() + 1.0) {
                    return false;
                }
                if (!((double)f3 < 1.0)) break;
                return false;
            }
            case OnlyOwn: {
                if (Method8864(entityEnderCrystal)) break;
                return false;
            }
        }
        return Field6091.getValue() * Field6091.getValue() >= d;
    }



    public boolean Method4876() {
        if (this.Field6114.getValue()) {
            if (mc.player.isHandActive()) {
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood) {
                    return true;
                }
            }
        }
        if (Field6116.getValue()) {
            if (mc.playerController.getIsHittingBlock()) {
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                    return true;
                }
            }
        }
        if (Field6115.getValue()) {
            if (mc.gameSettings.keyBindRight.isKeyDown()) {
                if (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) {
                    return true;
                }
            }
        }
        if (Field6117.getValue() > 0.0) {
            EntityPlayerSP entityPlayerSP = mc.player;
            EntityPlayerSP entityPlayerSP2 = mc.player;
            if ((double)(entityPlayerSP.getHealth() + entityPlayerSP2.getAbsorptionAmount()) < this.Field6117.getValue()) {
                return true;
            }
        }
        return false;
    }


    public boolean Method4842(BlockPos blockPos) {
        double d = mc.player.posX - ((double)blockPos.getX() + 0.5);
        double d2 = mc.player.posY - ((double)blockPos.getY() + 0.5) + 1.5;
        double d3 = mc.player.posZ - ((double)blockPos.getZ() + 0.5);
        double d4 = d * d + d2 * d2 + d3 * d3;
        if (d4 > 36.0) {
            return false;
        }
        if (d4 > (Field6092.getValue() * Field6092.getValue())) {
            return false;
        }
        if (this.Field6094.getValue()) {
            boolean bl = mc.playerController.gameIsSurvivalOrAdventure();
            double d5 = bl ? 27.0 : 31.0;
            if (d4 >= d5) {
                return false;
            }
        }
        return true;
    }
    

    @f0g
    @LauncherEventHide
    public void Method5022( f4I f4I2) {
        if (f4I2.Method5702() instanceof EntityEnderCrystal && this.Field6136.contains(f4I2.Method5702().getEntityId())) {
            this.Field6136.remove(f4I2.Method5702().getEntityId());
        }
    }

    


    @SubscribeEvent
    public void onPacketSend( PacketEvent.Send f492) {
        if (f492.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            Packet packet = f492.getPacket();

            CPacketPlayerTryUseItemOnBlock cPacketPlayerTryUseItemOnBlock = (CPacketPlayerTryUseItemOnBlock)packet;
            ItemStack itemStack = mc.player.getHeldItem(cPacketPlayerTryUseItemOnBlock.getHand());
            if ((itemStack.getItem() == Items.END_CRYSTAL)) {
                this.Field6140.removeIf(new f6r(cPacketPlayerTryUseItemOnBlock));
                this.Field6140.add(new fe3(System.currentTimeMillis() + (long)500, cPacketPlayerTryUseItemOnBlock.getPos()));
                if (cPacketPlayerTryUseItemOnBlock.getPos().getY() >= mc.world.getHeight() - 1 && cPacketPlayerTryUseItemOnBlock.getDirection() == EnumFacing.UP) {
                    CPacketPlayerTryUseItemOnBlock cPacketPlayerTryUseItemOnBlock2 = cPacketPlayerTryUseItemOnBlock;

                  //  ((CPacketPlayerTryUseItemOnBlockAccessor)cPacketPlayerTryUseItemOnBlock2).Method6416(EnumFacing.DOWN);
                    cPacketPlayerTryUseItemOnBlock2.placedBlockDirection = EnumFacing.DOWN;
                }
            }
        }
    }
    */
}
