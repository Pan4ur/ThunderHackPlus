package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClientEvent
        extends EventStage {
    private Feature feature;
    private Setting setting;

    public ClientEvent(int stage, Feature feature) {
        super(stage);
        this.feature = feature;
    }

    public ClientEvent(Setting setting) {
        super(2);
        this.setting = setting;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public Setting getSetting() {
        return this.setting;
    }
}

