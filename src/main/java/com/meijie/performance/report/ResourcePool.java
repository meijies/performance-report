package com.meijie.performance.report;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author meijie
 * @since 1.0
 */
public class ResourcePool<T extends AutoCloseable> implements AutoCloseable {

    private Semaphore available;
    private Queue<T> resouceQueue;

    protected volatile Boolean working = true;
    private static final AtomicReferenceFieldUpdater<ResourcePool, Boolean> WORKING_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(ResourcePool.class, Boolean.class, "working");

    public ResourcePool(final int size) {
        available = new Semaphore(size, true);
        resouceQueue = new ConcurrentLinkedQueue();
    }

    public T getItem() throws InterruptedException {
        if (working) {
            available.acquire();
            return resouceQueue.poll();
        }
        return null;
    }

    public boolean putItem(T t) {
        if (working && resouceQueue.add(t)) {
            available.release();
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        return resouceQueue.size();
    }

    @Override
    public void close() throws Exception {
        if (WORKING_UPDATER.compareAndSet(this, true, false)) {
            for (T t : resouceQueue) {
                t.close();
            }
        }
        throw new Exception("Resouce closed failure");
    }
}
