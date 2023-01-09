package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.player.AutoTool;
import com.mrzak34.thunderhack.modules.render.XRay;
import net.minecraft.block.state.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.util.Util.mc;

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
*/
    @Inject(method = { "isFullCube" },  at = { @At("HEAD") },  cancellable = true)
    public void isFullCubeHook(final IBlockState blockState,  final CallbackInfoReturnable<Boolean> info) {
    try{
            if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn() && Thunderhack.moduleManager.getModuleByClass(XRay.class).wh.getValue() ) {
                info.setReturnValue(Thunderhack.moduleManager.getModuleByClass(XRay.class).shouldRender(Block.class.cast(this)));
            }
    }
        catch (Exception ex) {}
    }





    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("HEAD"), cancellable = true)
    public void getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos, CallbackInfoReturnable<Float> ci) {
        float f = state.getBlockHardness(worldIn, pos);
        if (f < 0.0F)
        {
            ci.setReturnValue(0.0f);;
        }
        else
        {
            AutoTool autoTool = Thunderhack.moduleManager.getModuleByClass(AutoTool.class);
            ci.setReturnValue(!player.canHarvestBlock(state) ? getDigSpeed(state, (autoTool.isEnabled() && autoTool.silent.getValue()) ? player.inventory.getStackInSlot(autoTool.itemIndex) : player.getHeldItemMainhand()) / f / 100.0F : getDigSpeed(state,(autoTool.isEnabled() && autoTool.silent.getValue()) ? player.inventory.getStackInSlot(autoTool.itemIndex) : player.getHeldItemMainhand()) / f / 30.0F);
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


}