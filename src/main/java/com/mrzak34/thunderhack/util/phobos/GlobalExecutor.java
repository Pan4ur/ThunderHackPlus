package com.mrzak34.thunderhack.util.phobos;


import java.util.concurrent.ExecutorService;


public interface GlobalExecutor {
    ExecutorService EXECUTOR = ThreadUtil.newDaemonCachedThreadPool();
}