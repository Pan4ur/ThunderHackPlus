package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;

public class HighJump extends Module {


    public HighJump() {
        super("HighJump", "HighJump", Category.MOVEMENT, true, false, false);
    }

    public Setting<Float> b = register(new Setting("Motion Y", 1.5f, 1.0f, 5.0f));
    private Setting<rotmod> a = register(new Setting("Mode", rotmod.Matrix));
    public Setting<Boolean> c = register(new Setting<>("Only damage", true));

    public enum rotmod {
        Matrix, Default, NexusGrief;
    }

    boolean abobka = false;
    int ticks = 0;

    @Override
    public void onEnable() {
        if (a.getValue() == rotmod.Matrix) {
            EntityPlayerSP entityPlayerSP = mc.player;
            if (entityPlayerSP.onGround) {
                entityPlayerSP.jump();
            }
            new HJThread(this, entityPlayerSP).start();
        }
        if (a.getValue() == rotmod.NexusGrief) {
            EntityPlayerSP entityPlayerSP = mc.player;
            if (entityPlayerSP.onGround) {
                entityPlayerSP.jump();
                ticks = 0;
            }
        }
    }

    @Override
    public void onDisable(){
        ticks = 0;
        abobka = false;
    }

    @Override
    public void onUpdate() {


        ++ticks;
        if(ticks == 5){
            abobka = true;
        }
        if (a.getValue() == rotmod.NexusGrief) {
            if(abobka) {
                new HJThread(this, mc.player).start();
                toggle();
            }
        }


        if (this.a.getValue() == rotmod.Default) {
            EntityPlayerSP entityPlayerSP = mc.player;
            if (!entityPlayerSP.onGround) {
                return;
            }
            if (((Boolean)this.c.getValue()) && entityPlayerSP.hurtTime <= 0) {
                return;
            }
            if (!((Boolean)this.c.getValue()) && !mc.gameSettings.keyBindJump.isKeyDown()) {
                return;
            }
            entityPlayerSP.motionY = this.b.getValue();
          //  KeyBinding.setKeyBindState((int)keyBinding.getKeyCode(), (boolean)false);
        }
    }
}
