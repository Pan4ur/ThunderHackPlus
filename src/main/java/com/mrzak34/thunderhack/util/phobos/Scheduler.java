package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.event.events.GameZaloopEvent;
import com.mrzak34.thunderhack.mixin.mixins.AccessorMinecraft;
import com.mrzak34.thunderhack.modules.Feature;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Helps with scheduling Tasks.
 */
public class Scheduler extends Feature
{
    private static final Scheduler INSTANCE = new Scheduler();

    private final Queue<Runnable> scheduled  = new LinkedList<>();
    private final Queue<Runnable> toSchedule = new LinkedList<>();
    private boolean executing;
    private int gameLoop;

    public Scheduler()
    {

    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGameZaloop(GameZaloopEvent e){
        gameLoop  = ((IMinecraft) mc).getGameLoop();

        executing = true;
        CollectionUtil.emptyQueue(scheduled, Runnable::run);
        executing = false;

        CollectionUtil.emptyQueue(toSchedule, scheduled::add);
    }

    /** @return the Singleton Instance of the Scheduler. */
    public static Scheduler getInstance()
    {
        return INSTANCE;
    }


    public void schedule(Runnable runnable)
    {
        schedule(runnable, true);
    }


    public void scheduleAsynchronously(Runnable runnable)
    {
        mc.addScheduledTask(() -> schedule(runnable, false));
    }


    public void schedule(Runnable runnable, boolean checkGameLoop)
    {
        if (mc.isCallingFromMinecraftThread())
        {
            if (executing || checkGameLoop
                    && gameLoop !=  ((IMinecraft) mc).getGameLoop())
            {
                toSchedule.add(runnable);
            }
            else
            {
                scheduled.add(runnable);
            }
        }
        else
        {
            mc.addScheduledTask(runnable);
        }
    }

}