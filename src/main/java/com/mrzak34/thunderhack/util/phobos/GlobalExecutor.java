package com.mrzak34.thunderhack.util.phobos;


import java.util.concurrent.ExecutorService;


public interface GlobalExecutor
{

    ExecutorService EXECUTOR = ThreadUtil.newDaemonCachedThreadPool();
    ExecutorService FIXED_EXECUTOR = ThreadUtil.newFixedThreadPool(
            (int)(Runtime.getRuntime().availableProcessors() / 1.5));
}