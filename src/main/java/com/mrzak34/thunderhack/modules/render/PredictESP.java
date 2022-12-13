package com.mrzak34.thunderhack.modules.render;


import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.HoleUtil;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class PredictESP extends Module{
    public PredictESP( ) {
        super ( "PredictESP" , "Предугадывать есп" , Module.Category.RENDER , true , false , false );
    }




    public Setting< Boolean > manualOutHole = this.register ( new Setting <> ( "ManualOutHole" , false ) );
    public Setting< Boolean > showPredictions = this.register ( new Setting <> ( "ShowPredictions" , false ) );
    public Setting< Boolean > debug = this.register ( new Setting <> ( "Debug" , false ) );
    public Setting< Boolean > hideSelf = this.register ( new Setting <> ( "Hide Self" , false ) );
    public Setting< Boolean > splitXZ = this.register ( new Setting <> ( "SplitXZ" , true ) );
    public Setting< Boolean > calculateYPredict = this.register ( new Setting <> ( "Calculate Y Predict" , true ) );
    public Setting <Integer> width = this.register ( new Setting <> ( "Line Width", 2, 1, 5 ) );
    public Setting <Integer> exponentIncreaseY = this.register ( new Setting <> ( "ExponentIncreaseY", 2, 1, 3,v-> calculateYPredict.getValue() ) );
    public Setting <Integer> increaseY = this.register ( new Setting <> ( "IncreaseY", 3, 1, 5,v-> calculateYPredict.getValue() ) );
    public Setting <Integer> exponentDecreaseY = this.register ( new Setting <> ( "ExponentDecreaseY", 1, 1, 3,v-> calculateYPredict.getValue( ) ));
    public Setting <Integer> decreaseY = this.register ( new Setting <> ( "Decrease Y", 2, 1, 5,v-> calculateYPredict.getValue() ) );
    public Setting <Integer> expnentStartDecrease = this.register ( new Setting <> ( "Exponent Start", 2, 1, 5,v-> calculateYPredict.getValue() ) );
    public Setting <Integer> startDecrease = this.register ( new Setting <> ( "Start Decrease", 39, 0, 200,v-> calculateYPredict.getValue() ) );
    public Setting <Integer> tickPredict = this.register ( new Setting <> ( "Tick Predict", 8, 0, 30 ) );
    public Setting <Integer> range = this.register ( new Setting <> ( "Range", 10, 0, 100 ) );
    public final Setting<ColorSetting> mainColor = this.register(new Setting<>("color", new ColorSetting(0x8800FF00)));
    public Setting< Boolean > aboveHoleManual = this.register ( new Setting <> ( "AboveHoleManual" , false,v-> manualOutHole.getValue() ) );



    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {

        mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer && (!hideSelf.getValue() || entity != mc.player)).filter(this::rangeEntityCheck).forEach(entity -> {
            double[] posVec = new double[] {entity.posX, entity.posY, entity.posZ};
            double[] newPosVec = posVec.clone();
            double motionX = entity.posX - entity.prevPosX;
            double motionY = entity.posY - entity.prevPosY;
            double motionZ = entity.posZ - entity.prevPosZ;
            boolean goingUp = false;
            boolean start = true;
            int up = 0, down = 0;
            if (debug.getValue())
                Command.sendMessage(String.format("Values: %f %f %f", newPosVec[0], newPosVec[1], newPosVec[2]));

            boolean isHole = false;
            if (manualOutHole.getValue() && motionY > 0) {
                if (HoleUtil.isHole(new BlockPos(entity.posX,entity.posY,entity.posZ), false, true).getType() != HoleUtil.HoleType.NONE)
                    isHole = true;
                else if (aboveHoleManual.getValue() && HoleUtil.isHole(new BlockPos(entity.posX,entity.posY - 1,entity.posZ), false, true).getType() != HoleUtil.HoleType.NONE)
                    isHole = true;
                if (isHole)
                    posVec[1] += 1;
            }

            for(int i = 0; i < tickPredict.getValue(); i++) {
                RayTraceResult result;
                if (splitXZ.getValue()) {
                    newPosVec = posVec.clone();
                    newPosVec[0] += motionX;
                    result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], posVec[2]));
                    if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) {
                        posVec = newPosVec.clone();
                    }
                    newPosVec = posVec.clone();
                    newPosVec[2] += motionZ;
                    result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], newPosVec[2]));
                    if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) {
                        posVec = newPosVec.clone();
                    }
                } else {
                    newPosVec = posVec.clone();
                    newPosVec[0] += motionX;
                    newPosVec[2] += motionZ;
                    result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], newPosVec[2]));
                    if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) {
                        posVec = newPosVec.clone();
                    }
                }
                if (calculateYPredict.getValue() && !isHole) {
                    newPosVec = posVec.clone();
                    if (!entity.onGround && motionY != -0.0784000015258789) {
                        if (start) {
                            if (motionY == 0)
                                motionY = startDecrease.getValue() / Math.pow(10, expnentStartDecrease.getValue());
                            goingUp = false;
                            start = false;
                            if (debug.getValue())
                                Command.sendMessage("Start motionY: " + motionY);
                        }
                        motionY += goingUp ? increaseY.getValue() / Math.pow(10, exponentIncreaseY.getValue()) : decreaseY.getValue() / Math.pow(10, exponentDecreaseY.getValue());
                        if (Math.abs(motionY) > startDecrease.getValue() / Math.pow(10, expnentStartDecrease.getValue())) {
                            goingUp = false;
                            if (debug.getValue())
                                up++;
                            motionY = decreaseY.getValue() / Math.pow(10, exponentDecreaseY.getValue());
                        }
                        newPosVec[1] += (goingUp ? 1 : -1) * motionY;
                        result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]),
                                new Vec3d(newPosVec[0], newPosVec[1], newPosVec[2]));

                        if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) {
                            posVec = newPosVec.clone();
                        } else {
                            if (!goingUp) {
                                goingUp = true;
                                newPosVec[1] += (increaseY.getValue() / Math.pow(10, exponentIncreaseY.getValue()));
                                motionY = increaseY.getValue() / Math.pow(10, exponentIncreaseY.getValue());
                                newPosVec[1] += motionY;
                                if (debug.getValue())
                                    down++;
                            }
                        }


                    }
                }


                if (showPredictions.getValue())
                    Command.sendMessage(String.format("Values: %f %f %f", posVec[0], posVec[1], posVec[2]));

            }
            if (debug.getValue()) {
                Command.sendMessage(String.format("Player: %s Total ticks: %d Up: %d Down: %d", ((EntityPlayer) entity).getGameProfile().getName(), tickPredict.getValue(), up, down));
            }
            EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), "Fit"));
            clonedPlayer.setPosition(posVec[0], posVec[1], posVec[2]);

            RenderUtil.drawBlockOutline(new BlockPos(posVec[0], posVec[1], posVec[2]), mainColor.getValue().getColorObject(), 3f, true);
        });
    }

    private boolean rangeEntityCheck(Entity entity) {
        return entity.getDistance(mc.player) <= range.getValue();
    }
}
