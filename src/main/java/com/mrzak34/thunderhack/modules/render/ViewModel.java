package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.event.events.RenderItemEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.combat.DeadCodeAura;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public
class ViewModel extends Module {
    private static ViewModel INSTANCE = new ViewModel ( );
    public Setting < Settings > settings = this.register ( new Setting <> ( "Settings" , Settings.TRANSLATE ) );
    public Setting < Boolean > noEatAnimation = this.register ( new Setting <> ( "NoEatAnimation" , false , v -> settings.getValue ( ) == Settings.TWEAKS ) );
    public Setting < Float > eatX = this.register ( new Setting <> ( "EatX" , 1.0f , - 2.0f , 5.0f , v -> settings.getValue ( ) == Settings.TWEAKS && ! this.noEatAnimation.getValue ( ) ) );
    public Setting < Float > eatY = this.register ( new Setting <> ( "EatY" , 1.0f , - 2.0f , 5.0f , v -> settings.getValue ( ) == Settings.TWEAKS && ! this.noEatAnimation.getValue ( ) ) );
    public Setting < Boolean > doBob = this.register ( new Setting <> ( "ItemBob" , true , v -> settings.getValue ( ) == Settings.TWEAKS ) );
    public Setting < Boolean > XBob = this.register ( new Setting <> ( "ZBob" , true , v -> settings.getValue ( ) == Settings.TWEAKS ) );
    public Setting < Float > zbobcorr = this.register ( new Setting <> ( "ZBobCorr" , 0.6f ,  0.1f , 2.0f , v -> settings.getValue ( ) == Settings.TWEAKS ) );

    public Setting < Float > mainX = this.register ( new Setting <> ( "MainX" , 1.2f , - 2.0f , 4.0f , v -> settings.getValue ( ) == Settings.TRANSLATE ) );
    public Setting < Float > mainY = this.register ( new Setting <> ( "MainY" , - 0.95f , - 3.0f , 3.0f , v -> settings.getValue ( ) == Settings.TRANSLATE ) );
    public Setting < Float > mainZ = this.register ( new Setting <> ( "MainZ" , - 1.45f , - 5.0f, 5.0f , v -> settings.getValue ( ) == Settings.TRANSLATE ) );
    public Setting < Float > offX = this.register ( new Setting <> ( "OffX" , 1.2f , - 2.0f , 4.0f , v -> settings.getValue ( ) == Settings.TRANSLATE ) );
    public Setting < Float > offY = this.register ( new Setting <> ( "OffY" , - 0.95f , - 3.0f , 3.0f , v -> settings.getValue ( ) == Settings.TRANSLATE ) );
    public Setting < Float > offZ = this.register ( new Setting <> ( "OffZ" , - 1.45f , - 5.0f , 5.0f , v -> settings.getValue ( ) == Settings.TRANSLATE ) );
    public Setting < Float > mainRotX = this.register ( new Setting <> ( "MainRotationX" , 0f , - 36f , 36f , v -> settings.getValue ( ) == Settings.ROTATE ) );
    public Setting < Float > mainRotY = this.register ( new Setting <> ( "MainRotationY" , 0f , - 36f , 36f , v -> settings.getValue ( ) == Settings.ROTATE ) );
    public Setting < Float > mainRotZ = this.register ( new Setting <> ( "MainRotationZ" , 0f , - 36f , 36f , v -> settings.getValue ( ) == Settings.ROTATE ) );
    public Setting < Float > offRotX = this.register ( new Setting <> ( "OffRotationX" , 0f , - 36f , 36f , v -> settings.getValue ( ) == Settings.ROTATE ) );
    public Setting < Float > offRotY = this.register ( new Setting <> ( "OffRotationY" , 0f , - 36f , 36f , v -> settings.getValue ( ) == Settings.ROTATE ) );
    public Setting < Float > offRotZ = this.register ( new Setting <> ( "OffRotationZ" , 0f , - 36f , 36f , v -> settings.getValue ( ) == Settings.ROTATE ) );
    public Setting < Float > mainScaleX = this.register ( new Setting <> ( "MainScaleX" , 1.0f , 0.1f , 5.0f , v -> settings.getValue ( ) == Settings.SCALE ) );
    public Setting < Float > mainScaleY = this.register ( new Setting <> ( "MainScaleY" , 1.0f , 0.1f , 5.0f , v -> settings.getValue ( ) == Settings.SCALE ) );
    public Setting < Float > mainScaleZ = this.register ( new Setting <> ( "MainScaleZ" , 1.0f , 0.1f , 5.0f , v -> settings.getValue ( ) == Settings.SCALE ) );
    public Setting < Float > offScaleX = this.register ( new Setting <> ( "OffScaleX" , 1.0f , 0.1f , 5.0f , v -> settings.getValue ( ) == Settings.SCALE ) );
    public Setting < Float > offScaleY = this.register ( new Setting <> ( "OffScaleY" , 1.0f , 0.1f , 5.0f , v -> settings.getValue ( ) == Settings.SCALE ) );
    public Setting < Float > offScaleZ = this.register ( new Setting <> ( "OffScaleZ" , 1.0f , 0.1f , 5.0f , v -> settings.getValue ( ) == Settings.SCALE ) );


    public Timer timer2 = new Timer();




    public Setting < Boolean > killauraattack = this.register ( new Setting <> ( "KillAura" , false ));
    public Setting < Float > kmainScaleX = this.register ( new Setting <> ( "KMainScaleX" , 1.0f , 0.1f , 5.0f , v -> killauraattack.getValue() ) );
    public Setting < Float > kmainScaleY = this.register ( new Setting <> ( "KMainScaleY" , 1.0f , 0.1f , 5.0f , v -> killauraattack.getValue()) );
    public Setting < Float > kmainScaleZ = this.register ( new Setting <> ( "KMainScaleZ" , 1.0f , 0.1f , 5.0f , v -> killauraattack.getValue()) );
    public Setting < Float > kmainRotX = this.register ( new Setting <> ( "KMainRotationX" , 0f , - 36f , 36f , v -> killauraattack.getValue()) );
    public Setting < Float > kmainRotY = this.register ( new Setting <> ( "KMainRotationY" , 0f , - 36f , 36f , v -> killauraattack.getValue()) );
    public Setting < Float > kmainRotZ = this.register ( new Setting <> ( "kMainRotationZ" , 0f , - 36f , 36f , v -> killauraattack.getValue()) );
    public Setting < Float > kmainX = this.register ( new Setting <> ( "KMainX" , 1.2f , - 2.0f , 4.0f , v -> killauraattack.getValue()) );
    public Setting < Float > kmainY = this.register ( new Setting <> ( "KMainY" , - 0.95f , - 3.0f , 3.0f , v -> killauraattack.getValue()) );
    public Setting < Float > kmainZ = this.register ( new Setting <> ( "KMainZ" , - 1.45f , - 5.0f , 5.0f , v -> killauraattack.getValue()) );



    public Setting < Boolean > rotatexo = this.register ( new Setting <> ( "RotateX" , false));
    public Setting < Boolean > rotateyo = this.register ( new Setting <> ( "RotateY" , false));
    public Setting < Boolean > rotatezo = this.register ( new Setting <> ( "RotateZ" , false));



    public Setting < Boolean > krotatex = this.register ( new Setting <> ( "KRotateX" , false,v -> killauraattack.getValue() ));
    public Setting < Boolean > krotatey = this.register ( new Setting <> ( "KRotateY" , false,v -> killauraattack.getValue() ));
    public Setting < Boolean > krotatez = this.register ( new Setting <> ( "KRotateZ" , false,v -> killauraattack.getValue() ));

    public Setting < Boolean > rotatex = this.register ( new Setting <> ( "RotateXOff" , false));
    public Setting < Boolean > rotatey = this.register ( new Setting <> ( "RotateYOff" , false));
    public Setting < Boolean > rotatez = this.register ( new Setting <> ( "RotateZOff" , false));

    public Setting<Integer> animdelay = this.register(new Setting<>("RotateSpeed", 36, 1, 1200, v -> killauraattack.getValue() || rotatex.getValue() || rotatey.getValue() || rotatez.getValue()));





    public
    ViewModel ( ) {
        super ( "ViewModel" , "Cool" , Category.RENDER , true , false , false );
        this.setInstance ( );
    }

    public static
    ViewModel getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new ViewModel ( );
        }
        return INSTANCE;
    }
    int negripidari = -180;

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @SubscribeEvent
    public
    void onItemRender ( RenderItemEvent event ) {

        event.setOffX ( - offX.getValue ( ) );
        event.setOffY ( offY.getValue ( ) );
        event.setOffZ ( offZ.getValue ( ) );


        if (timer2.passedMs(1000 / animdelay.getValue())){
            ++negripidari;

            if(negripidari > 180){negripidari = -180;}
            timer2.reset();
        }




        if(!rotatex.getValue()) {
            event.setOffRotX((float)offRotX.getValue() * 5f);
        } else {
            event.setOffRotX((float)negripidari);
        }
        if(!rotatey.getValue()) {
            event.setOffRotY((float)offRotY.getValue() * 5f);
        } else {
            event.setOffRotY((float)negripidari);
        }
        if(!rotatez.getValue()) {
            event.setOffRotZ((float)offRotZ.getValue() * 5f);
        } else {
            event.setOffRotZ((float)negripidari);
        }

        event.setOffHandScaleX ( offScaleX.getValue ( ) );
        event.setOffHandScaleY ( offScaleY.getValue ( ) );
        event.setOffHandScaleZ ( offScaleZ.getValue ( ) );


        if(killauraattack.getValue() && (DeadCodeAura.target != null || Aura.target != null)){
            event.setMainHandScaleX ( kmainScaleX.getValue ( ) );
            event.setMainHandScaleY ( kmainScaleY.getValue ( ) );
            event.setMainHandScaleZ ( kmainScaleZ.getValue ( ) );


            if(!krotatex.getValue()) {
                event.setMainRotX(kmainRotX.getValue() * 5);
            } else  {
                event.setMainRotX(negripidari);
            }
            if(!krotatey.getValue()){
                event.setMainRotY ( kmainRotY.getValue ( ) * 5 );
            } else {
                event.setMainRotY (negripidari);
            }
            if(!krotatez.getValue()) {
                event.setMainRotZ(kmainRotZ.getValue() * 5);
            } else {
                event.setMainRotZ(negripidari);
            }




            event.setMainX ( kmainX.getValue ( ) );
            event.setMainY ( kmainY.getValue ( ) );
            event.setMainZ ( kmainZ.getValue ( ) );
        } else {
            event.setMainHandScaleX ( mainScaleX.getValue ( ) );
            event.setMainHandScaleY ( mainScaleY.getValue ( ) );
            event.setMainHandScaleZ ( mainScaleZ.getValue ( ) );



            if(!rotatexo.getValue()) {
                event.setMainRotX ( mainRotX.getValue ( ) * 5 );
            } else {
                event.setMainRotX (negripidari);
            }
            if(!rotateyo.getValue()) {
                event.setMainRotY  ( mainRotY.getValue ( ) * 5 );
            } else {
                event.setMainRotY (negripidari);
            }
            if(!rotatezo.getValue()) {
                event.setMainRotZ  ( mainRotZ.getValue ( ) * 5 );
            } else {
                event.setMainRotZ (negripidari);
            }








            event.setMainX ( mainX.getValue ( ) );
            event.setMainY ( mainY.getValue ( ) );
            event.setMainZ ( mainZ.getValue ( ) );

        }




    }

    private
    enum Settings {
        TRANSLATE,
        ROTATE,
        SCALE,
        TWEAKS
    }


}