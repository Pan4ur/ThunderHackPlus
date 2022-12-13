package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.DeadCodeUtils.MathUtils;
import com.mrzak34.thunderhack.util.DeadCodeUtils.RaytraceUtils;
import com.mrzak34.thunderhack.util.DeadCodeUtils.ef;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static com.mrzak34.thunderhack.modules.combat.Aura.absSinAnimation;

public class DeadCodeAura extends Module {

    public DeadCodeAura() {
        super("DeadCodeAura", "DeadCodeAura", Category.COMBAT, true, false ,false);
    }

    
    public static Timer D = new Timer();
    public Timer E = new Timer();
    public Timer F = new Timer();
    public Timer G = new Timer();
    public Timer H = new Timer();
    public Timer I = new Timer();
    public static EntityLivingBase target;
    public static float[] K = new float[2];
    public float L = 0.1f;
    public boolean M;
    public double N;
    public int O = -2;
    public boolean P;
    public float Q;
    public float R;

    public float serverYaw = 0;
    public float serverPitch = 0;





    public  Setting<rotMode> rotationMode = this.register(new Setting<>("Rotation", rotMode.Default));
    private enum rotMode {
        Default, Matrix, AAC, Static, SunRise
        // 0        1     2      3       4
    }

    public  Setting<Mode2> targetMode = this.register(new Setting<>("Mode", Mode2.Switch));
    private enum Mode2 {
        Switch, Single
    }

    public  Setting<Mode3> targetPrio = this.register(new Setting<>("TargetPrio", Mode3.Closest));
    private enum Mode3 {
        Closest, Health,Equip
    }

    public  Setting<Mode4> aimMode = this.register(new Setting<>("Aim", Mode4.Body));
    private enum Mode4 {
        Head, Body, Tighs, All
    }

    public  Setting<Mode5> e = this.register(new Setting<>("ShieldBreakMode", Mode5.Old));
    private enum Mode5 {
        Old, New
    }
    
    
    
    
    public Setting <Float> distance = this.register ( new Setting <> ( "Distance", 3.8f, 1.0f, 7.0f) );
    
    public Setting <Integer> fov = this.register ( new Setting <> ( "FOV", 360, 0, 360) );
    public Setting <Integer> minCPS = this.register ( new Setting <> ( "MinCPS", 6, 1, 30) );
    public Setting <Integer> maxCPS = this.register ( new Setting <> ( "MaxCPS", 12, 1, 30) );

    public Setting <Boolean> autocrit = this.register ( new Setting <> ( "Autocrit", true ) );
    public Setting <Boolean> Raytrace = this.register ( new Setting <> ( "RTX", false ) );
    public Setting <Boolean> look = this.register ( new Setting <> ( "Look", false ) );
    public Setting <Boolean> stopSprinting = this.register ( new Setting <> ( "StopSprinting", false ) );
    public Setting <Boolean> onlyWeapon = this.register ( new Setting <> ( "OnlyWeapon", false ) );
    public Setting <Boolean> shieldBlock = this.register ( new Setting <> ( "ShieldBlock", false ) );
    public Setting <Boolean> shieldBreak = this.register ( new Setting <> ( "ShieldBreak", false ) );
    public Setting <Boolean> autoDisable = this.register ( new Setting <> ( "AutoDisable", false ) );
    public Setting <Boolean> throughWalls = this.register ( new Setting <> ( "ThroughWalls", false ) );
    public Setting <Boolean> teleport  = this.register ( new Setting <> ( "Teleport", false ) );
    public Setting <Boolean> swing = this.register ( new Setting <> ( "Swing", false ) );
    public Setting <Boolean> cooldown = this.register ( new Setting <> ( "Cooldown", false ) );
    public final Setting<ColorSetting> shitcollor = this.register(new Setting<>("TargetColor", new ColorSetting(-2009289807)));



    public static double prevCircleStep, circleStep;


    @SubscribeEvent
    public void onRender3D(Render3DEvent e){
            EntityLivingBase entity = target;
            if (entity != null) {
                double cs = prevCircleStep + (circleStep - prevCircleStep) * mc.getRenderPartialTicks();
                double prevSinAnim = absSinAnimation(cs - 0.15);
                double sinAnim = absSinAnimation(cs);
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().renderPosY + prevSinAnim * 1.4f;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().renderPosZ;
                double nextY = entity.lastTickPosY
                        + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().renderPosY + sinAnim * 1.4f;

                float red = shitcollor.getValue().getRed() / 255F;
                float green = shitcollor.getValue().getGreen() / 255F;
                float blue = shitcollor.getValue().getBlue() / 255F;

                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glBegin(GL11.GL_QUAD_STRIP);
                for (int i = 0; i <= 360; i++) {
                    GL11.glColor4f(red, green, blue, 0.6F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY,
                            z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                    GL11.glColor4f(red, green, blue, 0.01F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, y,
                            z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
                }
                GL11.glEnd();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                for (int i = 0; i <= 360; i++) {
                    GL11.glColor4f(red, green, blue, 0.8F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * entity.width * 0.8, nextY,
                            z + Math.sin(Math.toRadians(i)) * entity.width * 0.8);
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


    @Override
    public void onEnable() {
        target = null;
        DeadCodeAura.K[0] = 0.0f;
        DeadCodeAura.K[1] = 0.0f;
        this.Q = this.d();
        this.R = this.e();
        if (!(look.getValue() || rotationMode.getValue() == rotMode.AAC)) {
            ef.j();
        }
        ef.l();
        this.L = rotationMode.getValue() == rotMode.SunRise ? 0.5f : 0.1f;
        this.N = 0.0;
    }

    @Override
    public void onDisable() {
        target = null;
        DeadCodeAura.K[0] = 0.0f;
        DeadCodeAura.K[1] = 0.0f;
        this.Q = this.d();
        this.R = this.e();
        if (!(look.getValue() || rotationMode.getValue() == rotMode.AAC)) {
            ef.j();
        }
        ef.l();
        this.L = rotationMode.getValue() == rotMode.SunRise ? 0.5f : 0.1f;
        this.a(false);
    }


    @Override
    public void onUpdate() {
        prevCircleStep = circleStep;
        circleStep += 0.15;
        if (!(autoDisable.getValue())) {
            return;
        }
        if ( mc.player.getHealth() == 0.0f || mc.player.isDead) {
            toggle();
        }
    }


    @SubscribeEvent
    public void onEventPreMotion(EventPreMotion e) {
        if ((rotationMode.getValue() == rotMode.Matrix || rotationMode.getValue() == rotMode.SunRise) || (rotationMode.getValue() == rotMode.Static)) {
            doRotate(e);
        }
        if ((shieldBlock.getValue())) {
            setShield();
        }
        z();
    }



    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()){
            return;
        }
        if ((e.getPacket() instanceof CPacketPlayer.PositionRotation || e.getPacket() instanceof CPacketPlayer.Rotation) && (rotationMode.getValue() == rotMode.Matrix || rotationMode.getValue() == rotMode.SunRise) && !(look.getValue() || rotationMode.getValue() == rotMode.AAC)) {
            this.z();
        }
    }



    

    public static int checkAxe() {
        int n2 = -2;
        for (int i2 = 0; i2 <= 8; ++i2) {
            if (!(mc.player.inventory.getStackInSlot(i2).getItem() instanceof ItemAxe)) continue;
            n2 = i2;
        }
        return n2;
    }

    public static boolean checkShield(EntityPlayer entityPlayer) {
        ItemStack itemStack = entityPlayer.getHeldItemOffhand();
        ItemStack itemStack2 = entityPlayer.getHeldItemMainhand();
        return !isEmpty(itemStack) && itemStack.getItem() == Items.SHIELD && entityPlayer.getItemInUseCount() > 0 || !isEmpty(itemStack2) && itemStack2.getItem() == Items.SHIELD && entityPlayer.getItemInUseCount() > 0;
    }

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }

    public void a(EntityLivingBase entityLivingBase) {
        int n2;
        EntityPlayer entityPlayer;
        if (entityLivingBase instanceof EntityPlayer && checkShield(entityPlayer = (EntityPlayer)entityLivingBase) && (n2 = checkAxe()) != -2) {
            this.O = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n2));
        }
    }

    public void b(EntityLivingBase entityLivingBase) {
        int n2;
        EntityPlayer entityPlayer;
        if (entityLivingBase instanceof EntityPlayer && (entityPlayer = (EntityPlayer)entityLivingBase).isActiveItemStackBlocking() && (n2 = checkAxe()) != -2) {
            mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(n2));
            mc.playerController.attackEntity((EntityPlayer)mc.player, (Entity)entityPlayer);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }
    public static boolean etf() {
        return !isEmpty(mc.player.getHeldItemOffhand()) && mc.player.getHeldItemOffhand().getItem() == Items.SHIELD;
    }

    public void setShield() {
        EnumAction enumAction;
        if (!etf()) {
            return;
        }
        ItemStack itemStack = mc.player.getHeldItemMainhand();
        if (!(isEmpty(itemStack) || Mouse.isButtonDown(1) || (enumAction = itemStack.getItem().getItemUseAction(itemStack)) == EnumAction.NONE && !(itemStack.getItem() instanceof ItemBlock))) {
            this.a(false);
            this.M = false;
            return;
        }
        if (target == null) {
            if (this.M) {
                this.M = false;
                this.a(this.M);
            }
            return;
        }
        if (mc.player.getCooledAttackStrength(0.0f) >= 0.75f) {
            this.M = false;
        }
        if (mc.player.getCooledAttackStrength(0.0f) <= 0.1f) {
            this.M = true;
        }
        if (this.G.passedMs(RandomUtils.nextInt(0, 55))) {
            this.a(this.M);
            this.G.reset();
        }
    }

    public void a(boolean bl) {
        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemShield) {
            KeyBinding.setKeyBindState((int)mc.gameSettings.keyBindUseItem.getKeyCode(), (boolean)bl);
        }
    }

    public float d() {
        return (look.getValue() || rotationMode.getValue() == rotMode.AAC) ? mc.player.rotationPitch : ef.e();
    }

    public float e() {
        return (look.getValue() || rotationMode.getValue() == rotMode.AAC) ? mc.player.rotationYaw : ef.d();
    }

    public void doRotate(EventPreMotion e) {   //TODO ROTT
        if (target == null) {
            return;
        }
        if (rotationMode.getValue() == rotMode.Default) {
            if ((look.getValue() || rotationMode.getValue() == rotMode.AAC)) {
                K = MathUtils.a((Entity)target, getHitbox());
                mc.player.rotationYaw =(K[0]);
                mc.player.rotationPitch =(K[1]);
            }
        } else  {
            if (!(rotationMode.getValue() == rotMode.Static)) {
                float f2 = 2.2f - RandomUtils.nextFloat();
                if (rotationMode.getValue() == rotMode.SunRise) {
                    f2 = 5.0f - RandomUtils.nextFloat();
                    f2 += RandomUtils.nextFloat();
                }
                K = MathUtils.a((Entity)target, getHitbox(), f2);
                if (rotationMode.getValue() == rotMode.SunRise) {
                    this.R = MathUtils.c(this.e(), K[0], 75.0f + RandomUtils.nextFloat(0.1f, 1.0f));
                    this.Q = MathUtils.c(this.d(), K[1], 2.0f + RandomUtils.nextFloat(0.1f, 1.0f));
                    DeadCodeAura.K[0] = this.R;
                    DeadCodeAura.K[1] = this.Q;
                }
            } else {
                K = MathUtils.a((Entity)target, (look.getValue() || rotationMode.getValue() == rotMode.AAC));
            }
            ef.a(K);
            mc.player.rotationPitch =(K[1]);

            mc.player.rotationYaw =((float)((double)K[0] + 1.0E-4));
                if (this.E.passedS(1L)) {
                    mc.player.rotationYaw =((float)((double)K[0] + 1.0E-4));
                }
                if (this.E.passedS(2L)) {
                    mc.player.rotationYaw =((float)((double)K[0] - 2.0E-4));
                    this.E.reset();
                }

        }
        serverYaw = K[0];
        serverPitch = K[1];
    }


    public static boolean ete() {
        ItemStack itemStack = mc.player.getHeldItemMainhand();
        return !isEmpty(itemStack) && (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemAxe);
    }


    private void getTarget() {
        if(targetPrio.getValue() == Mode3.Closest){
            target = getClosest(distance.getValue());
        }
        if(targetPrio.getValue() == Mode3.Equip){
            target = getArmorLess();
        }
        if(targetPrio.getValue() == Mode3.Health){
            target = getHealthDown();
        }
    }
    public static ArrayList<EntityPlayer> targets = new ArrayList<>();

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

    public boolean canAttack(EntityPlayer nigger){
        if(nigger == mc.player){
            return false;
        }
        if(Thunderhack.friendManager.isFriend(nigger.getName())){
            return false;
        }
        return !(mc.player.getDistance(nigger) > distance.getValue());
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


    public Entity h() {
        Entity entity = null;
        if ((rotationMode.getValue() == rotMode.Matrix || rotationMode.getValue() == rotMode.SunRise) || rotationMode.getValue() == rotMode.AAC || (rotationMode.getValue() == rotMode.Static)) {
            float f2 = serverYaw;
            float f3 = serverPitch;
            if (!(look.getValue() || rotationMode.getValue() == rotMode.AAC)) {
                f2 = ef.d();
                f3 = ef.e();
            }
            entity = RaytraceUtils.b((float)(mc.player.isSprinting() ? (double) distance.getValue().floatValue() - 0.1 : (double) distance.getValue().floatValue()), f2, f3);
        }
        return entity;
    }

    public void z() {
        if (!this.g(target)) {
            getTarget();
        }
        if (target == null) {
            return;
        }
        if ((onlyWeapon.getValue()) && !ete()) {
            return;
        }
        EntityPlayerSP entityPlayerSP = mc.player;
        if (!this.e(target)) {
            return;
        }
        if (cooldown.getValue()) {
            float f2;
            float f3 = f2 = autocrit.getValue() && Criticals.e() ? 1.0f : 0.92f;
            if (entityPlayerSP.getCooledAttackStrength(0.0f) >= f2) {
                    this.c(target);
                    entityPlayerSP.resetCooldown();
            }
            if (this.P && this.H.passedMs(800L)) {
                this.c(target);
                entityPlayerSP.resetCooldown();
                ef.l();
                this.P = false;
                this.H.reset();
            }
        } else {
            int n2 = RandomUtils.nextInt(minCPS.getValue(), costyl(minCPS.getValue(), maxCPS.getValue()));
            if (this.D.passedMs(1000 / n2)) {
                this.c(target);
                this.D.reset();
            }
        }
        if (!this.g(target) && targetMode.getValue() == Mode2.Switch) {
            getTarget();
        }
    }
    public int costyl(int a, int b){
        if( a >= b){
            return b - 2;
        } else {
            return a;
        }

    }

    public void c(EntityLivingBase entityLivingBase) {
        boolean bl;
        EntityPlayerSP entityPlayerSP = mc.player;
        if (teleport.getValue()) {
            entityPlayerSP.setPosition(entityLivingBase.posX, entityPlayerSP.posY, entityLivingBase.posZ);
        }
        if ((shieldBlock.getValue()) && etf() && this.M) {
            return;
        }
        if ((stopSprinting.getValue())) {
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)entityPlayerSP, CPacketEntityAction.Action.STOP_SPRINTING));
        }
        if (rotationMode.getValue() == rotMode.AAC) {
            if (target != null) {
                float f2 = 2.2f - RandomUtils.nextFloat();
                K = MathUtils.a((Entity) target, getHitbox(), f2);
                mc.player.rotationYaw = K[0];
                mc.player.rotationPitch = K[1];
            }
        }
        EntityLivingBase entityLivingBase2 = entityLivingBase;
        if ((rotationMode.getValue() == rotMode.Matrix || rotationMode.getValue() == rotMode.SunRise) || rotationMode.getValue() == rotMode.AAC || (rotationMode.getValue() == rotMode.Static) && ((Raytrace.getValue()) || rotationMode.getValue() == rotMode.SunRise)) {
            Entity entity = this.h();
            if (entity == null) {
                return;
            }
            entityLivingBase2 = (EntityLivingBase)entity;
        }
        if ((shieldBreak.getValue()) && e.getValue() == Mode5.Old) {
            this.a(entityLivingBase2);
        }
        boolean bl2 = bl = entityPlayerSP.fallDistance > 0.0f && !entityPlayerSP.onGround && !entityPlayerSP.isOnLadder() && !entityPlayerSP.isInWater() && entityPlayerSP.getRidingEntity() == null;
        if (bl) {
            mc.player.onCriticalHit((Entity)entityLivingBase2);
        }
        mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)entityLivingBase2));
        if ((swing.getValue())) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        } else {
            mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        if ((shieldBreak.getValue()) && e.getValue() == Mode5.New) {
            this.b(entityLivingBase2);
        }
        if (this.O != -2) {
            mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.O));
            this.O = -2;
        }
        this.M = true;
    }

    public Hitbox getHitbox() {
        if (aimMode.getValue() == Mode4.Head) {
            return Hitbox.HEAD;
        }
        if (aimMode.getValue() == Mode4.Body) {
            return Hitbox.CHEST;
        }
        if (aimMode.getValue() == Mode4.Tighs) {
            return Hitbox.TIGHS;
        }
        if (aimMode.getValue() == Mode4.All) {
            int n2 = RandomUtils.nextInt(0, 3);
            return Hitbox.values()[n2];
        }
        return Hitbox.CHEST;
    }


    public enum Hitbox {
        HEAD, CHEST, TIGHS
    }
    

    public boolean d(EntityLivingBase entityLivingBase) {
        return  targetPrio.getValue() == Mode3.Closest
                && eQc(entityLivingBase, target)
                || targetPrio.getValue() == Mode3.Health
                && eQa(entityLivingBase, target)
                || targetPrio.getValue() == Mode3.Equip
                && eQb(entityLivingBase, target);
    }

    public static boolean eQa(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return entityLivingBase2 == null || entityLivingBase.getHealth() < entityLivingBase2.getHealth();
    }

    public static boolean eQb(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return entityLivingBase2 == null || !(entityLivingBase instanceof EntityPlayer) || !(entityLivingBase2 instanceof EntityPlayer) || etd((EntityPlayer)entityLivingBase) > etd((EntityPlayer)entityLivingBase2);
    }
    public static int etd(EntityPlayer entityPlayer) {
        ItemStack[] itemStackArray;
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        InventoryPlayer inventoryPlayer = entityPlayer.inventory;
        ItemStack itemStack = entityPlayer.getHeldItemMainhand();
        ItemStack itemStack2 = inventoryPlayer.armorItemInSlot(0);
        ItemStack itemStack3 = inventoryPlayer.armorItemInSlot(1);
        ItemStack itemStack4 = inventoryPlayer.armorItemInSlot(2);
        ItemStack itemStack5 = inventoryPlayer.armorItemInSlot(3);
        for (ItemStack itemStack6 : itemStackArray = new ItemStack[]{itemStack, itemStack5, itemStack4, itemStack3, itemStack2}) {
            if (isEmpty(itemStack6)) continue;
            arrayList.add(itemStack6);
        }
        return arrayList.size();
    }
    public static boolean eQc(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return entityLivingBase2 == null || mc.player.getDistance((Entity)entityLivingBase) < mc.player.getDistance((Entity)entityLivingBase2);
    }

    public boolean e(EntityLivingBase entityLivingBase) {
        return entityLivingBase.getDistance((Entity)mc.player) <= (float)(mc.player.isSprinting() ? (double)distance.getValue().floatValue() - 0.1 : (double) distance.getValue().floatValue()) - this.L;
    }

    public boolean f(EntityLivingBase entityLivingBase) {
        return entityLivingBase.getDistance((Entity)mc.player) <= (float)(mc.player.isSprinting() ? (double)distance.getValue().floatValue() - 0.1 : (double) distance.getValue().floatValue()) + this.L;
    }

    public boolean a(EntityLivingBase entityLivingBase, float f2) {
        return entityLivingBase.getDistance((Entity)mc.player) <= (float)(mc.player.isSprinting() ? (double)distance.getValue().floatValue() - 0.1 : (double) distance.getValue().floatValue()) + f2;
    }


    public boolean g(EntityLivingBase entityLivingBase) {
        return entityLivingBase != null && !entityLivingBase.isDead && entityLivingBase.deathTime <= 0 && (this.f(entityLivingBase) || this.targetMode.getValue() != Mode2.Switch) && (this.a(entityLivingBase, 2.0f) || targetMode.getValue() != Mode2.Single);
    }

    public boolean h(EntityLivingBase entityLivingBase) {
        return !(entityLivingBase instanceof EntityPlayerSP) && !checkent(entityLivingBase) && entityLivingBase != mc.player && !entityLivingBase.isDead && entityLivingBase.deathTime <= 0 && checkent(entityLivingBase) && checkent(entityLivingBase) && this.f(entityLivingBase) && checkent(entityLivingBase) && MathUtils.a((Entity)entityLivingBase, (double) fov.getValue().intValue()) && (throughWalls.getValue() != false || mc.player.canEntityBeSeen((Entity)entityLivingBase)) && this.d(entityLivingBase);
    }


    public static boolean checkent(EntityLivingBase entityLivingBase) {
        return !(entityLivingBase instanceof EntityPlayer) || !Thunderhack.friendManager.isFriend(entityLivingBase.getName());
    }
}
