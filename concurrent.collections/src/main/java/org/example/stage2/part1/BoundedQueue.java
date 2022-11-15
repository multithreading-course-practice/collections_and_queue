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
        Node<T> e = new Node<>(item);
        enqLock.lock();
        try {
            while (size.get() == capacity) {

            }
            tail.next = e;
            tail = e;

        } finally {
            enqLock.unlock();
        }

    }

    @Override
    public T deq() {
        T result;
        deqLock.lock();
        try {

            result = head.next.value;
            head = head.next;

        } finally {
            deqLock.unlock();
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
