package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.events.GameZaloopEvent;
import com.mrzak34.thunderhack.modules.Feature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Helps with scheduling Tasks.
 */
public class Scheduler extends Feature {
    private static final Scheduler INSTANCE = new Scheduler();

    private final Queue<Runnable> scheduled = new LinkedList<>();
    private final Queue<Runnable> toSchedule = new LinkedList<>();
    private boolean executing;
    private int gameLoop;

    public Scheduler() {

    }

    /**
     * @return the Singleton Instance of the Scheduler.
     */
    public static Scheduler getInstance() {
        return INSTANCE;
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onGameZaloop(GameZaloopEvent e) {
        gameLoop = ((InterfaceMinecraft) mc).getGameLoop();

        executing = true;
        CollectionUtil.emptyQueue(scheduled, Runnable::run);
        executing = false;

        CollectionUtil.emptyQueue(toSchedule, scheduled::add);
    }

    public void scheduleAsynchronously(Runnable runnable) {
        mc.addScheduledTask(() -> schedule(runnable, false));
    }


    public void schedule(Runnable runnable, boolean checkGameLoop) {
        if (mc.isCallingFromMinecraftThread()) {
            if (executing || checkGameLoop
                    && gameLoop != ((InterfaceMinecraft) mc).getGameLoop()) {
                toSchedule.add(runnable);
            } else {
                scheduled.add(runnable);
            }
        } else {
            mc.addScheduledTask(runnable);
        }
    }

}