package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.SolidWeb;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(value = {BlockWeb.class})
public class MixinBlockWeb extends Block {
    protected MixinBlockWeb() {
        super(Material.WEB);
    }

    /**
     * @author
     * @reason
     */
    @Nullable
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        if (Thunderhack.moduleManager.getModuleByClass(SolidWeb.class).isEnabled()) {
            return FULL_BLOCK_AABB;
        }
        return NULL_AABB;
    }
}
