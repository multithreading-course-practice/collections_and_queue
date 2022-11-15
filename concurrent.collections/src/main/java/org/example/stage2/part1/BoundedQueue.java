package org.example.stage2.part1;

import org.example.stage2.Queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> implements Queue<T> {

    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    AtomicInteger size;
    volatile Node<T> head, tail;
    final int capacity;


    public BoundedQueue(final int capacity) {
        this.capacity = capacity;
        head = new Node<>(null);
        tail = head;
        size = new AtomicInteger(0);
        enqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();
        deqLock = new ReentrantLock();
        notEmptyCondition = deqLock.newCondition();
    }

    @Override
    public void enq(T item) {
        boolean mustWakeDequeuers = false;
        Node<T> e = new Node<>(item);
        enqLock.lock();
        try {
            while (size.get() == capacity) {
                try {
                    notFullCondition.await();
                } catch (InterruptedException ex) {
                    //ignore
                }
            }
            tail.next = e;
            tail = e;
            if (size.getAndIncrement() == 0) {
                mustWakeDequeuers = true;
            }
        } finally {
            enqLock.unlock();
        }
        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

    @Override
    public T deq() {
        T result;
        boolean mustWakeEnqueuers = false;
        deqLock.lock();
        try {
            while (head.next == null) {
                try {
                    notEmptyCondition.await();
                } catch (InterruptedException e) {
                    //ignore
                }
            }
            result = head.next.value;
            head = head.next;
            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
            }
        } finally {
            deqLock.unlock();
        }
        if (mustWakeEnqueuers) {
            enqLock.lock();
            try {
                notFullCondition.signalAll();
            } finally {
                enqLock.unlock();
            }
        }
        return result;
    }


    private static class Node<T> {
        T value;
        volatile Node<T> next;

        public Node(T value) {
            this.value = value;
            this.next = null;
        }
    }
}
