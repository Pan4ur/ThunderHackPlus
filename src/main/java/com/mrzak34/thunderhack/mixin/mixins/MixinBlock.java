package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.block.state.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;

@Mixin({ Block.class })
public abstract class MixinBlock
{
    @Shadow
    @Deprecated
    public abstract float getBlockHardness(final IBlockState p0,  final World p1,  final BlockPos p2);

    /*
    @Inject(method = { "addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V" },  at = { @At("HEAD") },  cancellable = true)
    public void addCollisionBoxToListHook(final IBlockState state,  final World worldIn,  final BlockPos pos,  final AxisAlignedBB entityBox,  final List<AxisAlignedBB> collidingBoxes,  @Nullable final Entity entityIn,  final boolean isActualState,  final CallbackInfo info) {
        if (entityIn != null && Util.mc.player != null && (entityIn.equals((Object)Util.mc.player) || (Util.mc.player.getRidingEntity() != null && entityIn.equals((Object)Util.mc.player.getRidingEntity()))) && Thunderhack.moduleManager.getModuleByClass(Phase.class).isOn()) {
            info.cancel();
        }
    }

    @Inject(method = { "isFullCube" },  at = { @At("HEAD") },  cancellable = true)
    public void isFullCubeHook(final IBlockState blockState,  final CallbackInfoReturnable<Boolean> info) {
        try {
            if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn() && Thunderhack.moduleManager.getModuleByClass(XRay.class).wh.getValue() ) {
              //  info.setReturnValue((Object)XRay.getInstance().shouldRender((Block)Block.class.cast(this)));
                info.cancel();
            }
        }
        catch (Exception ex) {}
    }

     */






}