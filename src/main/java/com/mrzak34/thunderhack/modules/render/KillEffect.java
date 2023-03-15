package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.DeathEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KillEffect extends Module {
    private final Setting<Boolean> sound = this.register(new Setting<>("Sound", false));
    private final Timer timer = new Timer();

    public KillEffect() {
        super("KillEffect", "KillEffect", Module.Category.RENDER);

    }

    @SubscribeEvent
    public void onDeath(DeathEvent event) {
        if (!nullCheck() && event.player != null) {
            Entity entity = event.player;
            if (entity != null) {
                if (entity.isDead || ((EntityPlayer) entity).getHealth() <= 0 && timer.passedMs(1500)) {
                    mc.world.spawnEntity(new EntityLightningBolt(mc.world, entity.posX, entity.posY, entity.posZ, true));
                    if (sound.getValue()) mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 0.5f, 1.f);
                    timer.reset();
                }
            }
        }
    }
}
