package org.example.stage2.part5;

import org.example.stage2.EmptyException;
import org.example.stage2.Queue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("NonAtomicOperationOnVolatileField")
public class SynchronousQueue<T> implements Queue<T> {

    T item = null;
    ReentrantLock lock;
    boolean enqueuing;
    Condition condition;


    public SynchronousQueue() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    @Override
    public void enq(T value) {
        lock.lock();
        try {
            item = value;

        } finally {
            lock.unlock();
        }
    }

    @Override
    public T deq() {
        lock.lock();
        try {

            T result = item;
            item = null;

            return result;
        } finally {
            lock.unlock();
        }
    }


}

