package com.shabanov.lab3.peerWire;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class PoolThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolCounter = new AtomicInteger(0);

    private final AtomicInteger threadCounter = new AtomicInteger(0);

    private final int poolNum = poolCounter.incrementAndGet();

    private final String name;

    public PoolThreadFactory (String name) {
        this.name = name;
    }

    @Override
    public Thread newThread (Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name + "-P" + poolNum + "-T" + threadCounter.incrementAndGet());
        return thread;
    }

}
