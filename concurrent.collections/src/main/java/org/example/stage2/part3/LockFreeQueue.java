package org.example.stage2.part3;

import org.example.stage2.EmptyException;
import org.example.stage2.Queue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class LockFreeQueue<T> implements Queue<T> {

    AtomicReference<Node<T>> head, tail;

    public LockFreeQueue() {
        Node<T> sentinel = new Node<>(null);
        this.head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    @Override
    public void enq(T item) {
        Node<T> node = new Node<>(item);
        while (true) {
            Node<T> last = tail.get();
            Node<T> next = last.next.get();
            if (last == tail.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(null, node)) {
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    @Override
    public T deq() {
        while (true) {
            Node<T> first = head.get();
            Node<T> last = tail.get();
            Node<T> next = first.next.get();
            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        throw new EmptyException();
                    }
                    tail.compareAndSet(last, next);

                } else {
                    T result = next.value;
                    if (head.compareAndSet(first, next)) {
                        return result;
                    }
                }
            }
        }
    }


    private static class Node<T> {
        T value;
        AtomicReference<Node<T>> next;

        public Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }


}

