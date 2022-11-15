package org.example.stage1.part2;

import org.example.stage1.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class FineGrainedSet<T> implements Set<T> {

    private final Node<T> head;

    public FineGrainedSet() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        Node<T> pred = head;
        pred.lock();
        try {
            Node<T> curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (key == curr.key) {
                    return false;
                }
                Node<T> node = new Node<>(item);
                node.next = curr;
                pred.next = node;
            }finally {
                curr.unlock();
            }
        }finally {
            pred.unlock();
        }
        return true;

    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        head.lock();
        Node<T> pred = head;
        try {
            Node<T> curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (key == curr.key) {
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }

        } finally {
            pred.unlock();
        }
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        head.lock();
        Node<T> pred = head;
        try {
            Node<T> curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                return key == curr.key;
            } finally {
                curr.unlock();
            }

        } finally {
            pred.unlock();
        }
    }

    private static class Node<T> {
        T item;
        final int key;
        Node<T> next;
        Lock lock = new ReentrantLock();

        public Node(final int key) {
            this.key = key;
        }

        public Node(final T item) {
            this.item = item;
            this.key = item.hashCode();
        }

        public void lock() {
            lock.lock();
        }

        public void unlock() {
            lock.unlock();
        }
    }
}
