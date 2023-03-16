package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.player.NoClip;
import com.mrzak34.thunderhack.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.state.BlockStateContainer;
import java.util.List;
import java.util.Objects;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin({Block.class})
public abstract class MixinBlock{
    @Shadow
    @Deprecated
    public abstract float getBlockHardness(final IBlockState p0, final World p1, final BlockPos p2);


    @Inject(method = {"isFullCube"}, at = {@At("HEAD")}, cancellable = true)
    public void isFullCubeHook(final IBlockState blockState, final CallbackInfoReturnable<Boolean> info) {
        try {
            if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn() && Thunderhack.moduleManager.getModuleByClass(XRay.class).wh.getValue()) {
                info.setReturnValue(Thunderhack.moduleManager.getModuleByClass(XRay.class).shouldRender(Block.class.cast(this)));
            }
        } catch (Exception ignored) {
        }
    }
    @Shadow
    public abstract BlockStateContainer getBlockState();


    @Inject(method = {"addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void addCollisionBoxToListHook(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState, CallbackInfo info) {
        if (entityIn != null && mc.player != null && (entityIn.equals(mc.player) && Thunderhack.moduleManager.getModuleByClass(NoClip.class).isOn()) && entityIn.equals(mc.player) && Thunderhack.moduleManager.getModuleByClass(NoClip.class).mode.getValue() != NoClip.Mode.CC && (mc.gameSettings.keyBindSneak.isKeyDown() || (!Objects.equals(pos, new BlockPos(mc.player).add(0, -1, 0)) && !Objects.equals(pos, new BlockPos(mc.player).add(0, -2, 0))))) {
            info.cancel();
        }
    }


/*
    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("HEAD"), cancellable = true)
    public void getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos, CallbackInfoReturnable<Float> ci) {
        AutoTool autoTool = Thunderhack.moduleManager.getModuleByClass(AutoTool.class);
        if(autoTool.isEnabled()) {
            float f = state.getBlockHardness(worldIn, pos);
            if (f < 0.0F) {
                ci.setReturnValue(0.0f);
            } else {
                ci.setReturnValue(!player.canHarvestBlock(state) ? getDigSpeed(state, (autoTool.isEnabled() && autoTool.silent.getValue()) ? player.inventory.getStackInSlot(autoTool.itemIndex) : player.getHeldItemMainhand()) / f / 100.0F : getDigSpeed(state, (autoTool.isEnabled() && autoTool.silent.getValue()) ? player.inventory.getStackInSlot(autoTool.itemIndex) : player.getHeldItemMainhand()) / f / 30.0F);
            }
        }
    }





    public float getDigSpeed(IBlockState state, ItemStack is){
        final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, is);
        float f = is.getDestroySpeed(state);
        if (digSpeed > 0 && !is.isEmpty()) {
            f += (float) (digSpeed * digSpeed + 1);
        }
        return f;
    }
 */

}