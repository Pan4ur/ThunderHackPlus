package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;


/**
 * @author megyn
 * fixed bugs with old version, now accurate within ~5 ms if start of tick is counted as when the time update packet is sent
 * TODO: use average time between packets being sent to more accurately approximate TPS, this will increase accuracy
 */
public class ServerTickManager extends Feature {

    private final Timer serverTickTimer = new Timer();
    private final ArrayDeque<Integer> spawnObjectTimes = new ArrayDeque<>();
    private final Map<BlockPos, Long> timeMap = new HashMap<>();
    private final boolean flag = true;
    private int serverTicks;
    private boolean initialized = false; // will be used for checks in the future
    private int averageSpawnObjectTime; // around 8-9 in vanilla

    public ServerTickManager() {



/*
        this.listeners.add(new EventListener<DisconnectEvent>(DisconnectEvent.class) {
            @Override
            public void invoke(DisconnectEvent event)
            {
                initialized = false;
            }
        });

 */
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onConnect(ConnectToServerEvent e) {
        initialized = false;
        resetTickManager();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (fullNullCheck()) return;
        if (e.getPacket() instanceof SPacketTimeUpdate) {
            if (mc.world != null
                    && mc.world.isRemote) {
                resetTickManager();
            }
        }
        if (e.getPacket() instanceof SPacketSpawnObject) {
            if (mc.world != null
                    && mc.world.isRemote) {
                onSpawnObject();
            }
        }
    }

    /**
     * Retrieves the time into the current server tick
     *
     * @return time into the current server tick
     */
    public int getTickTime() {
        if (serverTickTimer.getTime() < 50) return (int) serverTickTimer.getTime();
        return (int) (serverTickTimer.getTime() % getServerTickLengthMS());
    }

    /**
     * Retrieves the time into a tick that the server will receive a sent packet (experimental)
     *
     * @return time that sent packets will be received by the client
     */
    public int getTickTimeAdjusted() {
        int time = getTickTime() + (Thunderhack.serverManager.getPing() / 2);
        if (time < getServerTickLengthMS()) return time; // redundant? idrk how modulus works in java
        return time % getServerTickLengthMS();
    }

    /**
     * Get the time into a tick that a packet was sent by the server
     *
     * @return tick time adjusted for server packets
     */
    public int getTickTimeAdjustedForServerPackets() {
        int time = getTickTime() - (Thunderhack.serverManager.getPing() / 2);
        if (time < getServerTickLengthMS() && time > 0) return time; // redundant? idrk how modulus works in java
        if (time < 0) return time + getServerTickLengthMS();
        return time % getServerTickLengthMS();
    }

    public void resetTickManager() {
        serverTickTimer.reset();
        serverTickTimer.adjust(Thunderhack.serverManager.getPing() / 2);
        initialized = true;
    }

    public int getServerTickLengthMS() {
        if (Thunderhack.serverManager.getTPS() == 0) return 50;
        return (int) (50 * (20.0f / Thunderhack.serverManager.getTPS()));
    }

    public void onSpawnObject() {
        int time = getTickTimeAdjustedForServerPackets();
        if (spawnObjectTimes.size() > 10) spawnObjectTimes.poll();
        spawnObjectTimes.add(time);
        int totalTime = 0;
        for (int spawnTime : spawnObjectTimes) {
            totalTime += spawnTime;
        }
        averageSpawnObjectTime = totalTime / spawnObjectTimes.size();
    }

    public int normalize(int toNormalize) {
        while (toNormalize < 0) {
            toNormalize += getServerTickLengthMS();
        }
        while (toNormalize > getServerTickLengthMS()) {
            toNormalize -= getServerTickLengthMS();
        }
        return toNormalize;
    }

    public boolean valid(int currentTime, int minTime, int maxTime) {
        if (minTime > maxTime) {
            return currentTime >= minTime || currentTime <= maxTime;
        } else {
            return currentTime >= minTime && currentTime <= maxTime;
        }
    }

    public int getSpawnTime() {
        return averageSpawnObjectTime;
    }

}