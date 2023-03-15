package com.mrzak34.thunderhack.util.phobos;

/**
 * Duck interface for {@link net.minecraft.entity.EntityLivingBase}.
 */
public interface IEntityLivingBase {


    /**
     * @return the ticksSinceLastSwing field.
     */
    int getTicksSinceLastSwing();

    void setTicksSinceLastSwing(int ticks);

    int getActiveItemStackUseCount();

    void setActiveItemStackUseCount(int count);

    boolean getElytraFlag();

    void setLowestDura(float lowest);

    float getLowestDurability();

}