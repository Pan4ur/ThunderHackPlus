package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FakeCrystalRender
{
    private final List<EntityEnderCrystal> crystals = new ArrayList<>();
    private final Setting<Integer> simulate;

    public FakeCrystalRender(Setting<Integer> simulate)
    {
        this.simulate = simulate;
    }

    public void addFakeCrystal(EntityEnderCrystal crystal)
    {
        crystal.setShowBottom(false);
        Util.mc.addScheduledTask(() -> // TODO: why what?
        {
            if (Util.mc.world != null)
            {
                for (EntityEnderCrystal entity : Util.mc.world.getEntitiesWithinAABB(
                        EntityEnderCrystal.class,
                        crystal.getEntityBoundingBox()))
                {
                    crystal.innerRotation = entity.innerRotation;
                    break;
                }
            }

            crystals.add(crystal);
        });
    }

    public void onSpawn(EntityEnderCrystal crystal)
    {
        Iterator<EntityEnderCrystal> itr = crystals.iterator();
        while (itr.hasNext())
        {
            EntityEnderCrystal fake = itr.next();
            if (fake.getEntityBoundingBox()
                    .intersects(crystal.getEntityBoundingBox()))
            {
                crystal.innerRotation = fake.innerRotation;
                itr.remove();
            }
        }
    }

    public void tick()
    {
        if (simulate.getValue() == 0)
        {
            crystals.clear();
            return;
        }

        Iterator<EntityEnderCrystal> itr = crystals.iterator();
        while (itr.hasNext())
        {
            EntityEnderCrystal crystal = itr.next();
            crystal.onUpdate();
            if (++crystal.ticksExisted >= simulate.getValue())
            {
                crystal.setDead();
                itr.remove();
            }
        }
    }

    public void render(float partialTicks)
    {
        RenderManager manager = Util.mc.getRenderManager();
        for (EntityEnderCrystal crystal : crystals)
        {
            manager.renderEntityStatic(crystal, partialTicks, false);
        }
    }

    public void clear()
    {
        crystals.clear();
    }

}