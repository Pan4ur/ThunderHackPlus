package com.mrzak34.thunderhack.events;

import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventPlayer extends Event {

    private final SPacketPlayerListItem.AddPlayerData addPlayerData;
    private final SPacketPlayerListItem.Action action;

    public EventPlayer(SPacketPlayerListItem.AddPlayerData addPlayerData, SPacketPlayerListItem.Action action) {
        this.addPlayerData = addPlayerData;
        this.action = action;
    }

    public SPacketPlayerListItem.AddPlayerData getPlayerData() {
        return this.addPlayerData;
    }

    public SPacketPlayerListItem.Action getAction() {
        return this.action;
    }

}