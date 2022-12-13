package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.mixin.ducks.IEntityPlayer;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

public class HelperEntityBlocksPlace {
    private final AutoCrystal module;

    public HelperEntityBlocksPlace(AutoCrystal module) {
        this.module = module;
    }

    public boolean blocksBlock(AxisAlignedBB bb, Entity entity) {
        if (entity instanceof IEntityPlayer
                && module.blockExtrapol.getValue() != 0) {
            MotionTracker tracker =
                    ((IEntityPlayer) entity).getBlockMotionTracker();
            if (tracker != null && tracker.active) {
                switch (module.blockExtraMode.getValue()) {
                    case Extrapolated:
                        return tracker.getEntityBoundingBox().intersects(bb);
                    case Pessimistic:
                        return tracker.getEntityBoundingBox().intersects(bb)
                                || entity.getEntityBoundingBox().intersects(bb);
                    case Optimistic:
                    default:
                        return tracker.getEntityBoundingBox().intersects(bb)
                                && entity.getEntityBoundingBox().intersects(bb);
                }
            }
        }

        return entity.getEntityBoundingBox().intersects(bb);
    }

}