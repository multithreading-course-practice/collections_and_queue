package org.example.stage1.part1;

import org.example.stage1.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedSet<T> implements Set<T> {

    private final Node<T> head;

    private final Lock lock = new ReentrantLock();

    public CoarseGrainedSet() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        Node<T> pred, curr;
        Integer intValue = (Integer) item;
        final int key = intValue.hashCode();
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (key == curr.key) {
                return false;
            } else {
                Node<T> node = new Node<>(item);
                node.next = curr;
                pred.next = node;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(final T item) {
        Node<T> pred, curr;
        int key = item.hashCode();
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (key == curr.key) {
                pred.next = curr.next;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(T item) {
        Node<T> curr;
        int key = item.hashCode();
        lock.lock();
        try {
            curr = head.next;
            while (curr.key < key) {
                curr = curr.next;
            }
            return key == curr.key;
        } finally {
            lock.unlock();
        }
    }

    private static class Node<T> {
        T item;
        final int key;
        Node<T> next;

        public Node(final int key) {
            this.key = key;
        }

        public Node(final T item) {
            this.item = item;
            this.key = item.hashCode();
        }
    }


}
