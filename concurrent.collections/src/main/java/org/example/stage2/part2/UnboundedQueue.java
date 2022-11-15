package org.example.stage2.part2;

import org.example.stage2.EmptyException;
import org.example.stage2.Queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("NonAtomicOperationOnVolatileField")
public class UnboundedQueue<T> implements Queue<T> {

    ReentrantLock enqLock, deqLock;
    volatile Node<T> head, tail;


    public UnboundedQueue() {
        head = new Node<>(null);
        tail = head;
        enqLock = new ReentrantLock();
        deqLock = new ReentrantLock();
    }

    @Override
    public void enq(T item) {
        Node<T> e = new Node<>(item);
        enqLock.lock();
        try {
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
            if(head.next == null){
                throw new EmptyException();
            }
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

