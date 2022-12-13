package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class EZbowPOP extends Module {


    // from 3arthH4ck
    public EZbowPOP() { super("EZbowPOP", "Шотает с лука", Category.COMBAT, true, false, false); }

    public Setting <Boolean> confirmTeleport = this.register ( new Setting <> ( "confirmTeleport", false));
    public Setting <Integer> customruns = this.register ( new Setting <> ( "CustomRuns", 8, 1, 250 ) );
    public Setting <Integer> teleports = this.register ( new Setting <> ( "Teleports", 0, 0, 200 ) );
    public Setting <Integer> interval = this.register ( new Setting <> ( "ConfirmInterval", 25, 0, 100 ) );

    public Setting <Float> XYMultiplier = this.register ( new Setting <> ( "XZMultiplier", 2.0f, -3.0f, 3.0f) );


    public  Setting<directionModeEn> directionMode = this.register(new Setting<>("Direction", directionModeEn.only_Y));
    private enum directionModeEn {
        X_and_Z, only_Y, Both
    }

    public  Setting<packetModeEn> packetMode = this.register(new Setting<>("PacketMode", packetModeEn.Double));
    private enum packetModeEn {
        Default, Double, DefaultBypass
    }

    public  Setting<ModeEn> Mode = this.register(new Setting<>("Mode", ModeEn.Strong));
    private enum ModeEn {
        Fast, Strong, Custom
    }

    @Override
    public void onEnable() {

    }


    public static boolean Ready = true;
    public static int ticks = 0;

    @SubscribeEvent
    protected void onPacketSend(PacketEvent.Send event) {
        if(fullNullCheck()){return;}
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            if(!Ready) return;
            if (!mc.player.collidedVertically)
                return;
            if (((CPacketPlayerDigging) event.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && mc.player.getActiveItemStack().getItem() == Items.BOW) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    for (int i = 0; i < getRuns(); i++) {

                        if (i != 0 && i % interval.getValue() == 0 && confirmTeleport.getValue()) {
                            int id = teleportID;
                            for (int j = 0; j < teleports.getValue(); j++) {
                                mc.player.connection.sendPacket(new CPacketConfirmTeleport(++id));
                            }
                        }


                        double[] dir = MovementUtil.strafe(0.001);

                        if(packetMode.getValue() == packetModeEn.Default){
                            if(directionMode.getValue() == directionModeEn.only_Y){
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                            } else if(directionMode.getValue() == directionModeEn.X_and_Z){
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1], mc.player.rotationYaw, mc.player.rotationPitch, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0] * XYMultiplier.getValue(), mc.player.posY, mc.player.posZ + dir[1] * XYMultiplier.getValue(), mc.player.rotationYaw, mc.player.rotationPitch, false));
                            }else if(directionMode.getValue() == directionModeEn.Both){
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY - 1e-10, mc.player.posZ +  dir[1], true));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0] * XYMultiplier.getValue(), mc.player.posY + 1e-10, mc.player.posZ +  dir[1] * XYMultiplier.getValue() , false));
                            }
                        } else if(packetMode.getValue() == packetModeEn.DefaultBypass ){
                            if(directionMode.getValue() == directionModeEn.only_Y){
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                            } else if(directionMode.getValue() == directionModeEn.X_and_Z){
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0] * XYMultiplier.getValue(), mc.player.posY, mc.player.posZ + dir[1] * 2, mc.player.rotationYaw, mc.player.rotationPitch, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1], mc.player.rotationYaw, mc.player.rotationPitch, true));
                            }else if(directionMode.getValue() == directionModeEn.Both){
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0] * XYMultiplier.getValue(), mc.player.posY + 1e-10, mc.player.posZ +  dir[1] * XYMultiplier.getValue() , false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY - 1e-10, mc.player.posZ +  dir[1], true));
                            }
                        } else if(packetMode.getValue() == packetModeEn.Double){
                            if(directionMode.getValue() == directionModeEn.only_Y){
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + 0.00000000000013, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + 0.00000000000027, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
                            } else if(directionMode.getValue() == directionModeEn.X_and_Z){
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1], mc.player.rotationYaw, mc.player.rotationPitch, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0] * XYMultiplier.getValue(), mc.player.posY, mc.player.posZ + dir[1] * XYMultiplier.getValue(), mc.player.rotationYaw, mc.player.rotationPitch, false));
                            }else if(directionMode.getValue() == directionModeEn.Both){
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0], mc.player.posY + 0.00000000000013, mc.player.posZ + dir[1], mc.player.rotationYaw, mc.player.rotationPitch, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dir[0] * XYMultiplier.getValue(), mc.player.posY + 0.00000000000027, mc.player.posZ + dir[1] * XYMultiplier.getValue(), mc.player.rotationYaw, mc.player.rotationPitch, false));
                            }

                        }
                        Ready = false;


                    }
            }
        }
    }

    @Override
    public void onUpdate(){
        if(Ready){
            switch (Mode.getValue()){
                case Fast: {ticks = 65; break;}
                case Strong: {ticks = 100;break;}
                case Custom:{ticks = (int)(customruns.getValue() * 0.6f);break;}
            }
        } else {
            ticks--;
            if(ticks < 1){
                Ready = true;
            }
        }
    }


    int getRuns(){
        switch (Mode.getValue()){
            case Fast: return 106;
            case Strong: return 144;
            case Custom: return customruns.getValue();
        }
        return 106;
    }

    public int getMaxDelay(){
        switch (Mode.getValue()){
            case Fast: return 65;
            case Strong: return 100;
            case Custom: return (int)(customruns.getValue() * 0.6f);
        }
        return 106;
    }

    int teleportID = 0;
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof SPacketPlayerPosLook){
            SPacketPlayerPosLook packet = e.getPacket();
            teleportID = packet.getTeleportId();
        }
    }
}