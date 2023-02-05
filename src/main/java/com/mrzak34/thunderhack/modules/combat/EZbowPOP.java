package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Parent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;


public class EZbowPOP extends Module {


    /*

        Best solution for mcfunny.su
        PacketLogged from Konas 1.0.2 :P

    */

    public EZbowPOP() { super("EZbowPOP", "Шотает с лука", Category.COMBAT); }


    public Setting< Boolean > rotation = this.register ( new Setting <> ( "Rotation" , false) );
    public  Setting<ModeEn> Mode = this.register(new Setting<>("Mode", ModeEn.Maximum));
    public Setting <Float> factor = this.register ( new Setting <> ( "Factor", 1f, 1f, 20f) );
    public  Setting<exploitEn> exploit = this.register(new Setting<>("Exploit", exploitEn.Strong));
    public Setting <Float> scale = this.register ( new Setting <> ( "Scale", 0.01f, 0.01f, 0.4f) );
    public Setting< Boolean > minimize = this.register ( new Setting <> ( "Minimize" , false) );
    public Setting <Float> delay = this.register ( new Setting <> ( "Delay", 5f, 0f, 10f) );
    public final  Setting<Parent> selection = register(new Setting<>("Selection", new Parent(false)));
    public final Setting<Boolean> bow = register(new Setting<>("Bows", true)).withParent(selection);
    public final Setting<Boolean> pearls = register(new Setting<>("EPearls", true)).withParent(selection);
    public final Setting<Boolean> xp = register(new Setting<>("XP", true)).withParent(selection);
    public final Setting<Boolean> eggs = register(new Setting<>("Eggs", true)).withParent(selection);
    public final Setting<Boolean> potions = register(new Setting<>("SplashPotions", true)).withParent(selection);
    public final Setting<Boolean> snowballs = register(new Setting<>("Snowballs", true)).withParent(selection);


    private Random rnd = new Random();
    public static Timer delayTimer = new Timer();


    @SubscribeEvent
    protected void onPacketSend(PacketEvent.Send event) {
        if(fullNullCheck() || !delayTimer.passedMs((long) (delay.getValue() * 1000))) return;
        if (event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging) event.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && (mc.player.getActiveItemStack().getItem() == Items.BOW && bow.getValue())
                || event.getPacket() instanceof CPacketPlayerTryUseItem && ((CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.MAIN_HAND &&  ((mc.player.getHeldItemMainhand().getItem() == Items.ENDER_PEARL && pearls.getValue()) || (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && xp.getValue()) || (mc.player.getHeldItemMainhand().getItem() == Items.EGG && eggs.getValue()) || (mc.player.getHeldItemMainhand().getItem() == Items.SPLASH_POTION && potions.getValue()) || (mc.player.getHeldItemMainhand().getItem() == Items.SNOWBALL && snowballs.getValue()))) {

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));

                double[] strict_direction = new double[]{ 100f * -Math.sin(Math.toRadians(mc.player.rotationYaw)),100f * Math.cos(Math.toRadians(mc.player.rotationYaw))};

                if(exploit.getValue() == exploitEn.Fast){
                    for (int i = 0; i < getRuns(); i++) {
                        spoof(mc.player.posX,  minimize.getValue() ? mc.player.posY : mc.player.posY - 1e-10, mc.player.posZ, true);
                        spoof(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false);
                    }
                }
                if(exploit.getValue() == exploitEn.Strong){
                    for (int i = 0; i < getRuns(); i++) {
                        spoof(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false);
                        spoof(mc.player.posX, minimize.getValue() ? mc.player.posY : mc.player.posY - 1e-10, mc.player.posZ, true);
                    }
                }
                if(exploit.getValue() == exploitEn.Phobos){
                    for (int i = 0; i < getRuns(); i++) {
                        spoof(mc.player.posX, mc.player.posY + 0.00000000000013, mc.player.posZ, true);
                        spoof(mc.player.posX, mc.player.posY + 0.00000000000027, mc.player.posZ,  false);
                    }
                }
                if(exploit.getValue() == exploitEn.Strict){
                    for (int i = 0; i < getRuns(); i++) {
                        if(rnd.nextBoolean()){
                            spoof(mc.player.posX - strict_direction[0], mc.player.posY, mc.player.posZ - strict_direction[1], false);
                        } else {
                            spoof(mc.player.posX + strict_direction[0], mc.player.posY, mc.player.posZ + strict_direction[1], true);
                        }
                    }
                }

            delayTimer.reset();
        }
    }

    private void spoof(double x, double y , double z, boolean ground){
        if(rotation.getValue()){
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(x, y, z, mc.player.rotationYaw, mc.player.rotationPitch, ground));
        } else {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, ground));
        }
    }

    private int getRuns(){
        if(Mode.getValue() == ModeEn.Factorised){
            return 10 + (int)((factor.getValue() - 1));
        }
        if(Mode.getValue() == ModeEn.Normal){
            return (int) Math.floor(factor.getValue());
        }
        if(Mode.getValue() == ModeEn.Maximum){
            return (int) (30f * factor.getValue());
        }
        return  1;
    }

    private enum exploitEn {
        Strong, Fast, Strict, Phobos
    }

    private enum ModeEn {
        Normal, Maximum, Factorised
    }
}