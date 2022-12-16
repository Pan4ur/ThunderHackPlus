package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.EntityUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class TargetStrafe extends Module {
    public TargetStrafe() { super("TargetStrafe", "Вращаться вокруг цели", Category.COMBAT, true, false, false); }


    private float wrap = 0F;
    private boolean switchDir = true;
    public final Setting<Float> reversedDistance = this.register(new Setting<Float>("Reversed Distance", 3.0f, 1.0f, 6.0f));
    public final Setting<Float> speedIfUsing = this.register(new Setting<Float>("Speed if using", 0.1f, 0.1f, 2.0f));
    public final Setting<Float> range = this.register(new Setting<Float>("Strafe Distance", 2.4f, 0.1f, 6.0f));
    public final Setting<Float> spd = this.register(new Setting<Float>("Strafe Speed", 0.23f, 0.1f, 2.0f));
    public Setting <Boolean> reversed = this.register ( new Setting <> ( "Reversed", false));
    public Setting <Boolean> autoJump  = this.register ( new Setting <> ( "AutoJump", true));
    public Setting <Boolean> smartStrafe = this.register ( new Setting <> ( "Smart Strafe", true));
    public Setting <Boolean> usingItemCheck = this.register ( new Setting <> ( "Speed if using", false));
    public Setting <Boolean> speedpot = this.register ( new Setting <> ( "Speed if Potion ", true));
    public final Setting<Float> spdd = this.register(new Setting<Float>("PotionSpeed", 0.45f, 0.1f, 2.0f,v -> speedpot.getValue()));
    public Setting<Boolean> SwitchIfMiss = register(new Setting("SwitchIfMiss", true));
    public Setting<Boolean> autoThirdPerson = register(new Setting("AutoThirdPers", Boolean.TRUE));
    public Setting<Float> trgrange = register(new Setting("TrgtRange", 3.8F, 0.1F, 7.0F));
    public Setting <Boolean> drawradius = this.register ( new Setting <> ( "drawradius", true));
    public Setting <Boolean> strafeBoost = this.register ( new Setting <> ( "StrafeBoost", false));
    public Setting <Boolean> addddd = this.register ( new Setting <> ( "add", false));

    EntityPlayer strafeTarget = null;
    public Setting<Integer> bticks = register(new Setting("BoostTicks", 5, 0, 60));
    public Setting<Integer> velocitydecrement = register(new Setting("BoostDecr", 5, 0, 5000));

    int boostticks = 0;

    @Override
    public void onEnable() {
        this.wrap = 0F;
        this.switchDir = true;
    }
    @Override
    public void onDisable(){
        if (autoThirdPerson.getValue()) {
            mc.gameSettings.thirdPersonView = 0;
        }
    }

    public boolean needToSwitch(double x, double z) {
        if (mc.gameSettings.keyBindLeft.isPressed() || mc.gameSettings.keyBindRight.isPressed()) {
            return true;
        }
        for (int i = (int) (mc.player.posY + 4); i >= 0; --i) {
            BlockPos playerPos;
            blockFIRE:
            {
                blockLAVA:
                {
                    playerPos = new BlockPos(x, i, z);
                    if (mc.world.getBlockState(playerPos).getBlock().equals(Blocks.LAVA)) break blockLAVA;
                    if (!mc.world.getBlockState(playerPos).getBlock().equals(Blocks.FIRE)) break blockFIRE;
                }
                return true;
            }
            if (mc.world.isAirBlock(playerPos)) continue;
            return false;
        }
        return true;
    }

    private float toDegree(double x, double z) {
        return (float) (Math.atan2(z - mc.player.posZ, x - mc.player.posX) * 180.0 / Math.PI) - 90F;
    }

    @Override
    public void onUpdate() {
        if(Aura.target != null){
            if(!(Aura.target instanceof EntityPlayer)){
                return;
            }

            strafeTarget = (EntityPlayer) Aura.target;

        } else if(DeadCodeAura.target != null){
            if(DeadCodeAura.target instanceof EntityPlayer) {
                strafeTarget = (EntityPlayer) DeadCodeAura.target;
            }
        } else {
            strafeTarget = null;
        }
    }

    public void onToggle ( ) {
        Thunderhack.TICK_TIMER = 1.0f;
        velocity = 0;
    }


    float speedy = 1f;
    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion event) {
        if (strafeTarget == null)
            return;
        if(mc.player.getDistanceSq(strafeTarget) < 0.2){
            return;
        }

        if (autoThirdPerson.getValue()) {
            if (strafeTarget.getHealth() > 0.0f && mc.player.getDistance(strafeTarget) <= trgrange.getValue() && mc.player.getHealth() > 0.0f) {
                if (Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled()) {
                    mc.gameSettings.thirdPersonView = 1;
                }
            } else {
                mc.gameSettings.thirdPersonView = 0;
            }
        }

        if (mc.player.getDistance(strafeTarget) <= trgrange.getValue()) {
            if (EntityUtil.getHealth(strafeTarget) > 0) {
                if (autoJump.getValue() && ( Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled())) {
                    if (mc.player.onGround) {
                        mc.player.jump();
                    }
                }
            }
            if (EntityUtil.getHealth(strafeTarget) > 0) {
                EntityLivingBase target = strafeTarget;
                if (target == null || mc.player.ticksExisted < 20)
                    return;



                if(speedpot.getValue()){
                    if (TargetStrafe.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation("speed")))) {
                        speedy = spdd.getValue();
                    }else {
                        speedy = spd.getValue();
                    }
                }else {
                    speedy = spd.getValue();
                }

                float speed = (mc.gameSettings.keyBindUseItem.isKeyDown()) && usingItemCheck.getValue() ? speedIfUsing.getValue() : speedy;

                if (velocity > velocityUse.getValue() && strafeBoost.getValue()) {
                    if(velocity < 0){
                        velocity = 0;
                    }
                    if(addddd.getValue()){
                        speed += (velocity / 8000f) / reduction.getValue();
                    } else {
                        speed = (velocity / 8000f) / reduction.getValue();
                    }
                    boostticks++;
                    velocity = velocity - velocitydecrement.getValue();
                }

                if(boostticks >= bticks.getValue()){
                    boostticks = 0;
                    velocity = 0;
                }

                this.wrap = (float) Math.atan2((mc.player.posZ - target.posZ), (mc.player.posX - target.posX));
                this.wrap += this.switchDir ? speed / mc.player.getDistance(target) : -(speed / mc.player.getDistance(target));
                double x = target.posX + range.getValue() * Math.cos(this.wrap);
                double z = (target.posZ + range.getValue() * Math.sin(this.wrap));
                if (smartStrafe.getValue() && this.needToSwitch(x, z)) {
                    this.switchDir = !this.switchDir;
                    this.wrap += 2 * (this.switchDir ? speed / mc.player.getDistance(target) : -(speed / mc.player.getDistance(target)));
                    x = target.posX + range.getValue() * Math.cos(this.wrap);
                    z = target.posZ + range.getValue() * Math.sin(this.wrap);
                }
                float searchValue =  reversed.getValue() && mc.player.getDistance(strafeTarget) < reversedDistance.getValue() ? -90 : 0;
                float reversedValue = (!mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() ? searchValue : 0);

                mc.player.motionX = speed * -Math.sin((float) Math.toRadians(toDegree(x + reversedValue, z + reversedValue)));
                mc.player.motionZ = speed * Math.cos((float) Math.toRadians(toDegree(x + reversedValue, z + reversedValue)));

            }
        }


    }





    @SubscribeEvent
    public void onRender3D(Render3DEvent e){
        if (Aura.target != null && drawradius.getValue()) {
            EntityLivingBase entity = Aura.target;
            double calcX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks()
                    - mc.getRenderManager().renderPosX;
            double calcY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks()
                    - mc.getRenderManager().renderPosY;
            double calcZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks()
                    - mc.getRenderManager().renderPosZ;
            float radius = range.getValue();
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glBegin(GL11.GL_LINE_STRIP);

            for (int i = 0; i <= 360; i++) {
                int rainbow = PaletteHelper.rainbow(300, 1, 1).getRGB();
                GlStateManager.color(((rainbow >> 16) & 255) / 255F, ((rainbow >> 8) & 255) / 255f,
                        (rainbow & 255) / 255F);
                GL11.glVertex3d(calcX + radius * Math.cos(Math.toRadians(i)), calcY,
                        calcZ + radius * Math.sin(Math.toRadians(i)));
            }

            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
            GlStateManager.resetColor();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()){
            return;
        }
        if (e.getPacket() instanceof SPacketEntityVelocity) {
            if(((SPacketEntityVelocity) e.getPacket()).getEntityID() == mc.player.getEntityId()) {
                SPacketEntityVelocity pack = e.getPacket();
                int vX = pack.getMotionX();
                int vZ = pack.getMotionZ();
                if (vX < 0) vX *= -1;
                if (vZ < 0) vZ *= -1;
                velocity = vX + vZ;
            }
        }
        if(!SwitchIfMiss.getValue()){
            return;
        }
        if(e.getPacket() instanceof SPacketSoundEffect){
            SPacketSoundEffect pac = e.getPacket();
            if(pac.posX < mc.player.posX + 1 && pac.posX > mc.player.posX - 1){
                if(pac.posY < mc.player.posY + 4 && pac.posY > mc.player.posY - 4){
                    if(pac.posZ < mc.player.posZ + 1 && pac.posZ > mc.player.posZ - 1){
                        if(pac.sound.getSoundName().toString().contains("nodamage"))
                            this.switchDir = !this.switchDir;
                    }
                }
            }
        }
    }

    public Setting<Float> reduction  = this.register(new Setting<>("reduction ", 2f, 1f, 5f));
    public Setting<Float> velocityUse  = this.register(new Setting<>("velocityUse ", 50000f, 0.1f, 100000f));

    int velocity = 0;


}
