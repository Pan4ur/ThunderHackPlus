package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static com.mrzak34.thunderhack.util.Util.mc;


public class ThreadHelper {
    private final Timer threadTimer = new Timer();
    private final Setting<Boolean> multiThread;
    private final Setting<Boolean> mainThreadThreads;
    private final Setting<Integer> threadDelay;
    private final Setting<AutoCrystal.RotationThread> rotationThread;
    private final Setting<AutoCrystal.ACRotate> rotate;
    private final AutoCrystal module;

    private volatile AbstractCalculation<?> currentCalc;

    public ThreadHelper(AutoCrystal module,
                        Setting<Boolean> multiThread,
                        Setting<Boolean> mainThreadThreads,
                        Setting<Integer> threadDelay,
                        Setting<AutoCrystal.RotationThread> rotationThread,
                        Setting<AutoCrystal.ACRotate> rotate) {
        this.module = module;
        this.multiThread = multiThread;
        this.mainThreadThreads = mainThreadThreads;
        this.threadDelay = threadDelay;
        this.rotationThread = rotationThread;
        this.rotate = rotate;
    }

    public synchronized void start(AbstractCalculation<?> calculation,
                                   boolean multiThread) {
        if (threadTimer.passedMs(threadDelay.getValue()) && (currentCalc == null || currentCalc.isFinished())) {
            currentCalc = calculation;
            execute(currentCalc, multiThread);
        }
    }

    public synchronized void startThread(BlockPos... blackList) {
        if (mc.world == null
                || mc.player == null
                || !threadTimer.passedMs(threadDelay.getValue())
                || currentCalc != null && !currentCalc.isFinished()) {
            return;
        }

        if (mc.isCallingFromMinecraftThread()) {
            startThread(new ArrayList<>(mc.world.loadedEntityList),
                    new ArrayList<>(mc.world.playerEntities),
                    blackList);
        } else {
            startThread(Thunderhack.entityProvider.getEntities(),
                    Thunderhack.entityProvider.getPlayers(),
                    blackList);
        }
    }

    public synchronized void startThread(boolean breakOnly, boolean noBreak, BlockPos... blackList) {
        if (mc.world == null
                || mc.player == null
                || !threadTimer.passedMs(threadDelay.getValue())
                || currentCalc != null && !currentCalc.isFinished()) {
            return;
        }

        if (mc.isCallingFromMinecraftThread()) {
            startThread(new ArrayList<>(mc.world.loadedEntityList),
                    new ArrayList<>(mc.world.playerEntities),
                    breakOnly,
                    noBreak,
                    blackList);
        } else {
            startThread(Thunderhack.entityProvider.getEntities(),
                    Thunderhack.entityProvider.getPlayers(),
                    breakOnly,
                    noBreak,
                    blackList);
        }
    }

    private void startThread(List<Entity> entities,
                             List<EntityPlayer> players,
                             boolean breakOnly,
                             boolean noBreak,
                             BlockPos... blackList) {
        currentCalc = new Calculation(module, entities, players, breakOnly, noBreak, blackList);
        execute(currentCalc, multiThread.getValue());
    }

    private void startThread(List<Entity> entities,
                             List<EntityPlayer> players,
                             BlockPos... blackList) {
        currentCalc = new Calculation(module, entities, players, blackList);
        execute(currentCalc, multiThread.getValue());
    }

    private void execute(AbstractCalculation<?> calculation,
                         boolean multiThread) {
        if (multiThread) {
            Thunderhack.threadManager.submitRunnable(calculation);
            threadTimer.reset();
        } else {
            threadTimer.reset();
            calculation.run();
        }
    }

    public void schedulePacket(PacketEvent.Receive event) {
        if ((multiThread.getValue() || mainThreadThreads.getValue())
                && (rotate.getValue() == AutoCrystal.ACRotate.None
                || rotationThread.getValue() != AutoCrystal.RotationThread.Predict)) {
            event.addPostEvent(this::startThread);
        }
    }

    /**
     * @return the currently running, or last finished calculation.
     */
    public AbstractCalculation<?> getCurrentCalc() {
        return currentCalc;
    }

    public void resetThreadHelper() {
        currentCalc = null;
    }

}