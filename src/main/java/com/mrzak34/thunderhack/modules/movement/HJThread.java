package com.mrzak34.thunderhack.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;

public class HJThread extends Thread {
    public EntityPlayerSP a;
    public HighJump b;

    public HJThread(HighJump highJump, EntityPlayerSP entityPlayerSP) {
        this.b = highJump;
        this.a = entityPlayerSP;
    }

    @Override
    public void run() {
        this.a.motionY = 9.0;
        try {
            HJThread.sleep(240L);
        }  catch (Exception ignored) {}
        this.a.motionY = 8.742f;
        super.run();
    }
}
