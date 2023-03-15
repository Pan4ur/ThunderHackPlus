package com.mrzak34.thunderhack.util.phobos;

public interface IEntityNoInterp {
    double getNoInterpX();

    void setNoInterpX(double x);

    double getNoInterpY();

    void setNoInterpY(double y);

    double getNoInterpZ();

    void setNoInterpZ(double z);

    int getPosIncrements();

    void setPosIncrements(int posIncrements);

    float getNoInterpSwingAmount();

    void setNoInterpSwingAmount(float noInterpSwingAmount);

    float getNoInterpSwing();

    void setNoInterpSwing(float noInterpSwing);

    float getNoInterpPrevSwing();

    void setNoInterpPrevSwing(float noInterpPrevSwing);

    /**
     * @return <tt>true</tt> unless this Entity is an EntityPlayerSP.
     */
    boolean isNoInterping();

    void setNoInterping(boolean noInterping);

}