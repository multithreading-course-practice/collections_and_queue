package org.example.stage1.part3;

import org.example.stage1.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OptimisticSet<T> implements Set<T> {

    private final Node<T> head;

    public OptimisticSet() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        if (key == curr.key) {
                            return false;
                        }

                        Node<T> node = new Node<>(item);
                        node.next = curr;
                        pred.next = node;
                        return true;
                    }
                }finally {
                    curr.unlock();
                }
            }finally {
                pred.unlock();
            }

        }
    }


    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        if (key == curr.key) {
                            pred.next = curr.next;
                            return true;
                        }
                        return false;
                    }
                } finally {
                    curr.unlock();
                }

            } finally {
                pred.unlock();
            }
        }
    }


    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        return key == curr.key;
                    }
                } finally {
                    curr.unlock();
                }

            } finally {
                pred.unlock();
            }
        }
    }

    private boolean validate(Node<T> pred, Node<T> curr) {
        Node<T> node = head;

        while (node.key <= pred.key) {
            if (node == pred) {
                return pred.next == curr;
            }
            node = node.next;
        }
        return false;
    }

    private static class Node<T> {
        T item;
        final int key;
        volatile Node<T> next;
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
