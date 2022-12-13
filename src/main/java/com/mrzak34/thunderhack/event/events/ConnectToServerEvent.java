package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.entity.Entity;


public class ConnectToServerEvent extends EventStage {
    public ConnectToServerEvent(String ip) {
        super(1);
        this.ip = ip;
    }

    String ip;


    public String getIp() {
        return ip;
    }
}
