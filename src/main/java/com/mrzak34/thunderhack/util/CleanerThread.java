package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.command.Command;

public class CleanerThread implements Runnable {

    public CleanerThread() {

    }

    @Override
    public void run() {
        Command.sendMessage("Memory cleaner thread started!");
        System.gc();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
        System.gc();
        Command.sendMessage("Memory cleaner thread finished!");
    }
}
