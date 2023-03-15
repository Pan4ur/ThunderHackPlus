package com.mrzak34.thunderhack.util.phobos;

import java.util.Set;
import java.util.TreeSet;

public class ForceData {
    private final Set<ForcePosition> forceData = new TreeSet<>();
    private boolean possibleHighDamage;
    private boolean possibleAntiTotem;

    public boolean hasPossibleHighDamage() {
        return possibleHighDamage;
    }

    public void setPossibleHighDamage(boolean possibleHighDamage) {
        this.possibleHighDamage = possibleHighDamage;
    }

    public boolean hasPossibleAntiTotem() {
        return possibleAntiTotem;
    }

    public void setPossibleAntiTotem(boolean possibleAntiTotem) {
        this.possibleAntiTotem = possibleAntiTotem;
    }

    public Set<ForcePosition> getForceData() {
        return forceData;
    }

}