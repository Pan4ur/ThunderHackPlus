package com.mrzak34.thunderhack.util.surround;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.combat.Surround;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.mrzak34.thunderhack.modules.combat.Surround.Mode.*;
import static com.mrzak34.thunderhack.util.Util.mc;

public final class ModeUtil {

    private final Vec3d[] NormalVecArray = new Vec3d[]{new Vec3d(1.0, -1.0, 0.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 0.0, -1.0)};
    public final SurroundMode MormalMode = new SurroundMode(Normal, NormalVecArray);

    private final Vec3d[] StrictModeArray = new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 0.0, -1.0)};
    public final SurroundMode StrictMode = new SurroundMode(Strict, StrictModeArray);

    private final Vec3d[] SemiSafeModeArray = new Vec3d[]{new Vec3d(1.0, -1.0, 0.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 2.0), new Vec3d(0.0, 0.0,-2)};
    public final SurroundMode SemiSafeMode = new SurroundMode(SemiSafe, SemiSafeModeArray);

    private final Vec3d[] SafeArray = new Vec3d[]{new Vec3d(1.0, -1, 0.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, -1.0, 1.0), new Vec3d(1.0, -1.0, -1.0), new Vec3d(-1.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 2.0),new Vec3d(0.0, 0.0, -2.0)};
    public final SurroundMode SafeMode = new SurroundMode(Safe, SafeArray);

    private final Vec3d[] CubicModeArray = new Vec3d[]{new Vec3d(1.0, -1.0, 0.0), new Vec3d(-1.0, -1.0 , 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, -1.0 , 1.0), new Vec3d(1.0, -1.0 , -1.0), new Vec3d(-1.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0)};
    public final SurroundMode CubicMode = new SurroundMode(Cubic, CubicModeArray);

    private final Vec3d[] HighModeArray = new Vec3d[]{new Vec3d(1.0, -1.0, 0.0), new Vec3d(-1.0 , -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0)};
    public final SurroundMode HighMode = new SurroundMode(High, HighModeArray);

    public final SurroundMode AntiFacePlaceMode = new SurroundMode(AntiFacePlace, null);
    public final SurroundMode DynamicMode = new SurroundMode(Dynamic, null);


    public List<BlockPos> getBlockPositions(Surround.Mode mode) {
        Surround surround = Thunderhack.moduleManager.getModuleByClass(Surround.class);
        ArrayList<BlockPos> arrayList = new ArrayList<>(64);
        if (mode == DynamicMode.name) {
            return surround.getDynamicPositions();
        }
        if (mode == AntiFacePlaceMode.name) {
            return surround.getAntiFacePlacePositions();
        }
        if (surround.feetBlocks.getValue()) {
            arrayList.addAll(surround.checkHitBoxes(mc.player, mc.player.posY, -1));
        }
        if (surround.down.getValue()) {
            arrayList.addAll(surround.checkHitBoxes(mc.player, mc.player.posY, -2));
        }
        Vec3d vec3d = mc.player.getPositionVector();
        Vec3d[] vec3dArray = getMode(mode).vecArray;
        int n = vec3dArray.length;
        int n2 = 0;
        while (n2 < n) {
            Vec3d vec3d2 = vec3dArray[n2];
            BlockPos blockPos = new BlockPos(vec3d2.add(vec3d));
            if (!surround.smartHelping.getValue() || !(vec3d2.y < 0.0) || getFacing(blockPos).isEmpty()) {
                arrayList.add(blockPos);
            }
            ++n2;
        }
        return arrayList;
    }


    public static List<EnumFacing> getFacing(BlockPos blockPos) {
        ArrayList<EnumFacing> arrayList = new ArrayList<>();
        if (mc.world == null) return arrayList;
        if (blockPos == null) {
            return arrayList;
        }
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing enumFacing = enumFacingArray[n2];
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);
            if (iBlockState != null && iBlockState.getBlock().canCollideCheck(iBlockState, false) && !iBlockState.getMaterial().isReplaceable()) {
                arrayList.add(enumFacing);
            }
            ++n2;
        }
        return arrayList;
    }

    private SurroundMode getMode(Surround.Mode mode){
        List<SurroundMode> list = new ArrayList<>();
        list.add(MormalMode);
        list.add(StrictMode);
        list.add(SemiSafeMode);
        list.add(SafeMode);
        list.add(CubicMode);
        list.add(HighMode);
        list.add(AntiFacePlaceMode);
        list.add(DynamicMode);

        for(SurroundMode mode2 : list){
            if(mode2.name == mode){
                return mode2;
            }
        }
        return MormalMode;
    }

    public static class SurroundMode {
        Surround.Mode name;
        Vec3d[] vecArray;

        public SurroundMode(Surround.Mode name, Vec3d[] vec3dArray ){
            this.name = name;
            this.vecArray = vec3dArray;
        }
    }
}
