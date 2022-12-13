package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = {BlockVine.class})
public class MixinBlockVine extends Block {
    protected MixinBlockVine() {
        super(Material.VINE);
    }


    /*
    @Overwrite
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return !Thunderhack.moduleManager.getModuleByClass(SolidWeb.class).isEnabled();
    }

     */
}