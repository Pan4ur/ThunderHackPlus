package com.mrzak34.thunderhack.events;


import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectToServerEvent extends Event {
    public ConnectToServerEvent(String ip) {
        this.ip = ip;
    }

    String ip;


    public String getIp() {
        return ip;
    }
}
