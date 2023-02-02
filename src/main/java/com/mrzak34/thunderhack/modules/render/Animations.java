package com.mrzak34.thunderhack.modules.render;


import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public
class Animations extends Module {


    public Animations ( ) {
        super ( "Animations" , "анимации удара" , Module.Category.RENDER );
        this.setInstance();
    }

    public static Animations getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Animations();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private static Animations INSTANCE = new Animations();




   // public static BooleanSetting itemAnimation = new BooleanSetting("Item Animation", false, () -> true);
   // smallItem = new BooleanSetting("Mini Item", false, () -> true);
/*

    public  Setting<Float> x = this.register(new Setting<Float>("X", 0f, -1f, 1f));
    public  Setting<Float> y = this.register(new Setting<Float>("Y", 0f, -1f, 1f));
    public  Setting<Float> z = this.register(new Setting<Float>("Z", 0f, -1f, 1f));

    public Setting <Integer> spinSpeed = this.register ( new Setting <> ( "Spin Speed", 4, 1, 10 ) );
    public Setting <Integer> speed = this.register ( new Setting <> ( "Smooth Attack", 8, 1, 20 ) );
    public Setting <Integer> smooth = this.register ( new Setting <> ( "Smooth", 3, -10, 10 ) );
    public Setting <Integer> scale = this.register ( new Setting <> ( "Scale", 1, -10, 10 ) );

    public Setting <Integer> angle = this.register ( new Setting <> ( "Angle", 0, -50, 100 ) );
    public Setting <Integer> rotate3 = this.register ( new Setting <> ( "Rotate3", 0, -360, 360 ) );
    public Setting <Integer> rotate2 = this.register ( new Setting <> ( "Rotate2", 0, -360, 360 ) );
    public Setting <Integer> rotate = this.register ( new Setting <> ( "Rotate", 360, -360, 360 ) );

   // public Setting <Integer> slowamplifier = this.register ( new Setting <> ( "Slow Amplifier", 2, 0, 50 ) );
   // public Setting <Integer> slowamplifier = this.register ( new Setting <> ( "Slow Amplifier", 2, 0, 50 ) );
   // public Setting <Integer> slowamplifier = this.register ( new Setting <> ( "Slow Amplifier", 2, 0, 50 ) );

    public Setting<Boolean> animation = register(new Setting("BlockAnim", Boolean.valueOf(true)));
    public Setting<Boolean> itemAnimation = register(new Setting("Item Animation", Boolean.valueOf(true)));

*/
    public Setting<Boolean> ed = register(new Setting("EquipDisable", Boolean.valueOf(true)));
    public Setting<Boolean> auraOnly = register(new Setting("auraOnly", Boolean.valueOf(false)));
    public  Setting<Float> fapSmooth = this.register(new Setting<Float>("fapSmooth", 4f, 0.5f, 15f));
    public  Setting<Integer> slowValue = this.register(new Setting<>("SlowValue", 6, 1, 50));



    public Setting<rmode> rMode = register(new Setting("SwordMode", rmode.Swipe));
    public enum rmode {
         Swipe, Rich, Glide,Default,New,Oblique, Fap, Slow
    }

  //  public Setting<rmode2> rMode2 = register(new Setting("ItemMode", rmode2.ALL));
   // public enum rmode2 {
  //      Spin, ALL;
  //  }


    public float shitfix = 1;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        if(mc.world!= null && mc.player != null) {
            shitfix = mc.player.getSwingProgress(mc.getRenderPartialTicks());
        }
    }

    public boolean abobka228 = false;

    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        abobka228 = mc.itemRenderer.equippedProgressMainHand < 1f;

        if (ed.getValue() && Animations.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            Animations.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            Animations.mc.entityRenderer.itemRenderer.itemStackMainHand = Animations.mc.player.getHeldItemMainhand();
        }
    }

}