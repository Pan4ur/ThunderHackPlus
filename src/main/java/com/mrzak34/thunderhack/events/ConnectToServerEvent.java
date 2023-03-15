package com.mrzak34.thunderhack.events;


import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectToServerEvent extends Event {
    String ip;

    public ConnectToServerEvent(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
