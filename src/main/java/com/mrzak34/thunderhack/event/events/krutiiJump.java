package com.mrzak34.thunderhack.event.events;

public class krutiiJump {
    /*

   // private  Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.Kangaroo));
   // private  Setting<Float> speed2 = this.register(new Setting<>("Speed2", 4.5f, 0.1f, 1.0f));

    private  Setting<Float> speed = this.register(new Setting<>("Speed", 4.5f, 0.5f, 20.0f));
    private  Setting<Float> timr = this.register(new Setting<>("TimerSpeed", 5.0F, 0.1F, 10.0F));

    public  Setting<Boolean> usetimer = this.register(new Setting<>("Timer", true));




    private enum Mode {
        Kangaroo,
        Deer,
        Fast,
        Ticks
    }



    public double Field1990;
    public double Field1991;
    public int Field1992 = 0;
    public int Field1993 = 0;

    public boolean jumped = false;

    @SubscribeEvent
    public void onMove(PyroMove f4p2) {
        if (f4p2.getStage() != 0 ) {
            return;
        }
       // if (mc.player.collidedHorizontally || this.Field1993 > 0 || mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) {
       //     if (this.Field1993 > 0) {
       //         --this.Field1993;
        //    }
        //    this.Field1992 = 0;
        //    this.Field1990 = 0.0;
        //    this.Field1991 = 0.0;
           // f4p2.set_x(0);
           // f4p2.set_z(0.0);
           // f4p2.set_x(0.0);
          ////  f4p2.setCanceled(true);
        //    return;
      //  }
        if (usetimer.getValue()) { // Timer
            Thunderhack.TICK_TIMER = timr.getValue();
        } else {
            Thunderhack.TICK_TIMER = 1.0f;
        }

        if (this.Field1992 == 1 && mc.player.collidedVertically) {
            this.Field1990 = 1.0 + Method2606() - 0.05;
        } else if (this.Field1992 == 2 && mc.player.collidedVertically) {
            mc.player.motionY = 0.415;
            f4p2.set_y(0.415);
            jumped = true;
            this.Field1990 *= speed.getValue();
        } else if (this.Field1992 == 3) {
            double d = 0.66 * (this.Field1991 - Method2606());
            this.Field1990 = this.Field1991 - d;
        } else {
            this.Field1990 = this.Field1991 - this.Field1991 / 159.0;
            if (mc.player.collidedVertically && this.Field1992 > 3) {
                this.Field1993 = 10;
                this.Field1992 = 1;
            }
        }
        this.Field1990 = Math.max(this.Field1990, Method2606());
        Method744(f4p2,this.Field1990);
        f4p2.setCanceled(true);
        ++this.Field1992;

    }

    public double Method2606(){
        double d = 0.2873f;
        if ( mc.player != null ) {
            if ( mc.player.isPotionActive( MobEffects.SPEED ) ) {
                int n = mc.player.getActivePotionEffect( MobEffects.SPEED ).getAmplifier( );
                d *= 1.0f + 0.2f * ( double ) ( n + 1 );
            }
        }
        return d;
    }

    @SubscribeEvent
    public void onPacketRecive(PacketEvent.Receive e){
        if(mc.player == null || mc.world == null){
            return;
        }
        if(e.getPacket() instanceof SPacketPlayerPosLook ){
            toggle();
        }
    }

    @Override
    public void onUpdate(){
        if(mc.player == null || mc.world == null){
            return;
        }
        if(mc.player.onGround && jumped){
            toggle();
        }
    }

    @Override
    public void onDisable(){
        jumped = false;
    }


    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent f4u2) {
        if (f4u2.getStage() != 0) {
            return;
        }
        double d = mc.player.posX - mc.player.prevPosX;
        double d2 = mc.player.posZ - mc.player.prevPosZ;
        this.Field1991 = Math.sqrt(d * d + d2 * d2);

      //  mc.player.jump();
        mc.player.onGround = true;
    }


    public void Method744( PyroMove event,double d) {
       // double d2 = mc.player.moveForward;
       // double d3 = mc.player.moveStrafing;

        MovementInput movementInput = mc.player.movementInput;
        double d2 = movementInput.moveForward;
        double d3 = movementInput.moveStrafe;

        float f = mc.player.rotationYaw;
        if (d2 == 0.0 && d3 == 0.0) {
            event.set_x(0);
            event.set_z(0);
        } else {
            if (d2 != 0.0) {
                if (d3 > 0.0) {
                    f += (float)(d2 > 0.0 ? -45 : 45);
                } else if (d3 < 0.0) {
                    f += (float)(d2 > 0.0 ? 45 : -45);
                }
                d3 = 0.0;
                if (d2 > 0.0) {
                    d2 = 1.0;
                } else if (d2 < 0.0) {
                    d2 = -1.0;
                }
            }
            event.set_x(d2 * d * Math.cos(Math.toRadians(f + 90.0f)) + d3 * d * Math.sin(Math.toRadians(f + 90.0f)));
            event.set_z(d2 * d * Math.sin(Math.toRadians(f + 90.0f)) - d3 * d * Math.cos(Math.toRadians(f + 90.0f)));
        }
    }





    public static boolean Method727() {
        return mc.player != null && (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f);
    }




    //fec.Method725() mc.player
    //f07 таймер
    //f0b настры
    // f4p2.Method7947() элитра флаинг
    //Method730 yaw
     */
}
