package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.PostPlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HighJump extends Module {


    public Setting<Float> b = register(new Setting("Motion Y", 1.5f, 1.0f, 5.0f));
    public Setting<Boolean> c = register(new Setting<>("Only damage", true));
    boolean abobka = false;
    int ticks = 0;
    private final Setting<rotmod> mode = register(new Setting("Mode", rotmod.Matrix));

    public HighJump() {
        super("HighJump", "HighJump", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mode.getValue() == rotmod.Matrix) {
            EntityPlayerSP entityPlayerSP = mc.player;
            if (entityPlayerSP.onGround) {
                entityPlayerSP.jump();
            }
            new HJThread(entityPlayerSP).start();
        }
        if (mode.getValue() == rotmod.Jump) {
            EntityPlayerSP entityPlayerSP = mc.player;
            if (entityPlayerSP.onGround) {
                entityPlayerSP.jump();
                ticks = 0;
            }
        }
    }

    @Override
    public void onDisable() {
        ticks = 0;
        abobka = false;
    }

    @Override
    public void onUpdate() {


        ++ticks;
        if (ticks == 5) {
            abobka = true;
        }
        if (mode.getValue() == rotmod.Jump) {
            if (abobka) {
                new HJThread(mc.player).start();
                toggle();
            }
        }


        if (mode.getValue() == rotmod.Default) {
            EntityPlayerSP entityPlayerSP = mc.player;
            if (!entityPlayerSP.onGround) {
                return;
            }
            if (this.c.getValue() && entityPlayerSP.hurtTime <= 0) {
                return;
            }
            if (!((Boolean) this.c.getValue()) && !mc.gameSettings.keyBindJump.isKeyDown()) {
                return;
            }
            entityPlayerSP.motionY = this.b.getValue();
        }
    }

    @SubscribeEvent
    public void onPostPlayerUpdate(PostPlayerUpdateEvent event) {
        if (mode.getValue() == rotmod.NexusGrief && mc.player.hurtTime < 5) {
            event.setCanceled(true);
            event.setIterations(2);
            new HJThread(mc.player).start();
            this.toggle();
        }
    }

    public enum rotmod {
        Matrix, Default, Jump, NexusGrief
    }

    public class HJThread extends Thread {
        public EntityPlayerSP player;

        public HJThread(EntityPlayerSP entityPlayerSP) {
            this.player = entityPlayerSP;
        }

        @Override
        public void run() {
            player.motionY = 9.0;
            try {
                HJThread.sleep(240L);
            } catch (Exception ignored) {
            }
            player.motionY = 8.742f;
            super.run();
        }
    }
}
