package com.shabanov.lab3.peerWire;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class PeerSessionPool implements Closeable {

    static final int EVENT_EXECUTOR_SHUTDOWN_TIME = 1500;

    static final int MISC_EXECUTOR_SHUTDOWN_TIME = 250;

    static final int BACKGROUND_SLEEP_TIME = 30000;

    final Set<PeerSession> peers = Collections.synchronizedSet(new HashSet<PeerSession>());

    final ExecutorService eventExecutor = Executors.newSingleThreadExecutor(new PoolThreadFactory(
            "EventThread"));

    final ExecutorService miscExecutor = Executors
            .newCachedThreadPool(new PoolThreadFactory("MiscThread"));

    private final Set<PeerListener> listeners = new CopyOnWriteArraySet<PeerListener>();

    private volatile boolean closed = false;

    final Object sync = new Object();

    public PeerSessionPool () {
        miscExecutor.submit(new BackgroundThread());
    }

    public void addPeerListener (PeerListener listener) {
        synchronized (peers) {
            listeners.add(listener);
            for (PeerSession peer : peers) {
                peer.addPeerListener(listener);
            }
        }
    }

    public void removePeerListener (PeerListener listener) {
        synchronized (peers) {
            listeners.remove(listener);
            for (PeerSession peer : peers) {
                peer.removePeerListener(listener);
            }
        }
    }

    public PeerSession newSession (PeerConnection connection) {
        PeerSession peer = PeerSession.newSession(connection, eventExecutor, miscExecutor);
        synchronized (peers) {
            peers.add(peer);
            for (PeerListener listener : listeners) {
                peer.addPeerListener(listener);
            }
        }
        return peer;
    }

    @Override
    public void close () {
        synchronized (this) {
            if (!closed) {
                closed = true;

                ExecutorService exec = Executors.newSingleThreadExecutor(new PoolThreadFactory("CloseThread"));
                exec.submit(new CloseTask());
                exec.shutdown();
            }
        }
    }

    public boolean isClosed () {
        return closed;
    }

    final class BackgroundThread implements Runnable {

        @Override
        public void run () {
            try {
                while (!isClosed()) {
                    synchronized (sync) {
                        sync.wait(BACKGROUND_SLEEP_TIME);
                    }

                    synchronized (peers) {
                        Iterator<PeerSession> it = peers.iterator();
                        while (it.hasNext()) {
                            PeerSession peer = it.next();
                            if (peer.isClosed()) {
                                it.remove();
                            }
                        }
                    }
                }
            } catch (InterruptedException exc) {
                Thread.currentThread().interrupt();

            } finally {
                close();
            }

        }

    }

    final class CloseTask implements Runnable {

        @Override
        public void run () {
            for (PeerSession peer : peers) {
                try {
                    peer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            synchronized (sync) {
                sync.notifyAll();
            }
            eventExecutor.shutdown();
            miscExecutor.shutdown();

            try {
                miscExecutor.awaitTermination(MISC_EXECUTOR_SHUTDOWN_TIME, TimeUnit.MILLISECONDS);
                eventExecutor.awaitTermination(EVENT_EXECUTOR_SHUTDOWN_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (!eventExecutor.isTerminated()) {
                eventExecutor.shutdownNow();
            }

            if (!miscExecutor.isTerminated()) {
                miscExecutor.shutdownNow();
            }
        }
    }

    public int size(){
        int i = 0;
        for (PeerSession peer: peers){
            if(!peer.isClosed()) i++;
        }
        return i;
    }

}
