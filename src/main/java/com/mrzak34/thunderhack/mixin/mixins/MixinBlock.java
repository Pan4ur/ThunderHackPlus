package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventBlockInteract;
import com.mrzak34.thunderhack.modules.player.AutoTool;
import com.mrzak34.thunderhack.modules.player.NoClip;
import com.mrzak34.thunderhack.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.state.BlockStateContainer;
import java.util.List;
import java.util.Objects;

import static com.mrzak34.thunderhack.util.Util.mc;
import static net.minecraft.init.Enchantments.EFFICIENCY;

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
        if (entityIn != null
                && mc.player != null
                && (entityIn.equals(mc.player)
                && Thunderhack.moduleManager.getModuleByClass(NoClip.class).isOn()
                && Thunderhack.moduleManager.getModuleByClass(NoClip.class).canNoClip())
                && entityIn.equals(mc.player) && Thunderhack.moduleManager.getModuleByClass(NoClip.class).mode.getValue() != NoClip.Mode.CC
                && (mc.gameSettings.keyBindSneak.isKeyDown() || (!Objects.equals(pos, new BlockPos(mc.player).add(0, -1, 0)) && !Objects.equals(pos, new BlockPos(mc.player).add(0, -2, 0))))) {
            info.cancel();
        }
    }



    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("HEAD"), cancellable = true)
    public void getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos, CallbackInfoReturnable<Float> ci) {

        AutoTool autoTool = Thunderhack.moduleManager.getModuleByClass(AutoTool.class);
        if(autoTool.isEnabled() && autoTool.silent.getValue()) {
            float f = state.getBlockHardness(worldIn, pos);
            if (f < 0.0F) {
                ci.setReturnValue(0.0f);
            } else {
                ci.setReturnValue(
                        !canHarvestBlock(state,player.inventory.getStackInSlot(autoTool.itemIndex)) ?
                                getDigSpeed(state, player.inventory.getStackInSlot(autoTool.itemIndex)) / f / 100.0F
                                : getDigSpeed(state, player.inventory.getStackInSlot(autoTool.itemIndex)) / f / 30.0F);
            }
        }


        NoClip nclip = Thunderhack.moduleManager.getModuleByClass(NoClip.class);
        if(nclip.isEnabled()) {
            float f = state.getBlockHardness(worldIn, pos);
            if (f < 0.0F) {
                ci.setReturnValue(0.0f);
            } else {
                ci.setReturnValue(
                        !canHarvestBlock(state,player.inventory.getStackInSlot(nclip.itemIndex)) ?
                                getDigSpeed(state, player.inventory.getStackInSlot(nclip.itemIndex)) / f / 100.0F
                                : getDigSpeed(state, player.inventory.getStackInSlot(nclip.itemIndex)) / f / 30.0F);
            }
        }
    }

    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    public void canCollideCheck(IBlockState state, boolean hitIfLiquid, CallbackInfoReturnable<Boolean> cir) {
        Block block = state.getBlock();
        EventBlockInteract event = new EventBlockInteract(block);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }

    public float getDigSpeed(IBlockState state, ItemStack stack)
    {
        double str = stack.getDestroySpeed(state);
        int effect = EnchantmentHelper.getEnchantmentLevel(EFFICIENCY, stack);
        return (float) Math.max(str + (str > 1.0 ? (effect * effect + 1.0) : 0.0), 0.0);
    }

    private boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }
        String tool = state.getBlock().getHarvestTool(state);
        if (stack.isEmpty() || tool == null) {
            return mc.player.canHarvestBlock(state);
        }
        final int toolLevel = stack.getItem().getHarvestLevel(stack, tool, mc.player, state);
        if (toolLevel < 0) {
            return mc.player.canHarvestBlock(state);
        }
        return toolLevel >= state.getBlock().getHarvestLevel(state);
    }


}