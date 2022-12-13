package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;

public
class RenderItemEvent extends EventStage {
    float mainX, mainY, mainZ,
            offX, offY, offZ,
            mainRotX, mainRotY, mainRotZ,
            offRotX, offRotY, offRotZ,
            mainHandScaleX, mainHandScaleY, mainHandScaleZ, /*mainHandItemWidth,*/
            offHandScaleX, offHandScaleY, offHandScaleZ/*, offHandItemWidth*/;


    public
    RenderItemEvent ( float mainX , float mainY , float mainZ ,
                      float offX , float offY , float offZ ,
                      float mainRotX , float mainRotY , float mainRotZ ,
                      float offRotX , float offRotY , float offRotZ ,
                      float mainHandScaleX , float mainHandScaleY , float mainHandScaleZ , /*float mainHandItemWidth ,*/
                      float offHandScaleX , float offHandScaleY , float offHandScaleZ /*, float offHandItemWidth*/ ) {
        this.mainX = mainX;
        this.mainY = mainY;
        this.mainZ = mainZ;
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        this.mainRotX = mainRotX;
        this.mainRotY = mainRotY;
        this.mainRotZ = mainRotZ;
        this.offRotX = offRotX;
        this.offRotY = offRotY;
        this.offRotZ = offRotZ;
        this.mainHandScaleX = mainHandScaleX;
        this.mainHandScaleY = mainHandScaleY;
        this.mainHandScaleZ = mainHandScaleZ;
        //this.mainHandItemWidth = mainHandItemWidth;
        this.offHandScaleX = offHandScaleX;
        this.offHandScaleY = offHandScaleY;
        this.offHandScaleZ = offHandScaleZ;
        //this.offHandItemWidth = offHandItemWidth;
    }

    public
    float getMainX ( ) {
        return mainX;
    }

    public
    void setMainX ( float v ) {
        this.mainX = v;
    }

    public
    float getMainY ( ) {
        return mainY;
    }

    public
    void setMainY ( float v ) {
        this.mainY = v;
    }

    public
    float getMainZ ( ) {
        return mainZ;
    }

    public
    void setMainZ ( float v ) {
        this.mainZ = v;
    }

    public
    float getOffX ( ) {
        return offX;
    }

    public
    void setOffX ( float v ) {
        this.offX = v;
    }

    public
    float getOffY ( ) {
        return offY;
    }

    public
    void setOffY ( float v ) {
        this.offY = v;
    }

    public
    float getOffZ ( ) {
        return offZ;
    }

    public
    void setOffZ ( float v ) {
        this.offZ = v;
    }

    public
    float getMainRotX ( ) {
        return mainRotX;
    }

    public
    void setMainRotX ( float v ) {
        this.mainRotX = v;
    }

    public
    float getMainRotY ( ) {
        return mainRotY;
    }

    public
    void setMainRotY ( float v ) {
        this.mainRotY = v;
    }

    public
    float getMainRotZ ( ) {
        return mainRotZ;
    }

    public
    void setMainRotZ ( float v ) {
        this.mainRotZ = v;
    }

    public
    float getOffRotX ( ) {
        return offRotX;
    }

    public
    void setOffRotX ( float v ) {
        this.offRotX = v;
    }

    public
    float getOffRotY ( ) {
        return offRotY;
    }

    public
    void setOffRotY ( float v ) {
        this.offRotY = v;
    }

    public
    float getOffRotZ ( ) {
        return offRotZ;
    }

    public
    void setOffRotZ ( float v ) {
        this.offRotZ = v;
    }

    public
    float getMainHandScaleX ( ) {
        return mainHandScaleX;
    }

    public
    void setMainHandScaleX ( float v ) {
        this.mainHandScaleX = v;
    }

    public
    float getMainHandScaleY ( ) {
        return mainHandScaleY;
    }

    public
    void setMainHandScaleY ( float v ) {
        this.mainHandScaleY = v;
    }

    public
    float getMainHandScaleZ ( ) {
        return mainHandScaleZ;
    }

    public
    void setMainHandScaleZ ( float v ) {
        this.mainHandScaleZ = v;
    }

    public
    float getOffHandScaleX ( ) {
        return offHandScaleX;
    }

    public
    void setOffHandScaleX ( float v ) {
        this.offHandScaleX = v;
    }

    public
    float getOffHandScaleY ( ) {
        return offHandScaleY;
    }

    public
    void setOffHandScaleY ( float v ) {
        this.offHandScaleY = v;
    }

    public
    float getOffHandScaleZ ( ) {
        return offHandScaleZ;
    }

    public
    void setOffHandScaleZ ( float v ) {
        this.offHandScaleZ = v;
    }
}