package org.example.stage2.part4;

import org.example.stage2.EmptyException;
import org.example.stage2.Queue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeRecycleQueue<T> implements Queue<T> {

    AtomicStampedReference<Node<T>> head, tail;

    ThreadLocal<Node<T>> freeList = ThreadLocal.withInitial(() -> null);

    public LockFreeRecycleQueue() {
        Node<T> sentinel = new Node<>(null);
        this.head = new AtomicStampedReference<>(sentinel, 0);
        tail = new AtomicStampedReference<>(sentinel, 0);
    }

    @Override
    public void enq(T item) {
        int[] lastStamp = new int[1];
        int[] nextStamp = new int[1];
        Node<T> node = reclaim(item);
        while (true) {
            Node<T> last = tail.get(lastStamp);
            Node<T> next = last.next.get(nextStamp);
            if (tail.getStamp() == lastStamp[0]) {
                if (next == null) {
                    if (last.next.compareAndSet(next, node, nextStamp[0], nextStamp[0] + 1)) {
                        tail.compareAndSet(last, node, lastStamp[0], lastStamp[0] + 1);
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next, lastStamp[0], lastStamp[0] + 1);
                }
            }
        }
    }

    private Node<T> reclaim(T item) {
        if (freeList.get() == null) {
            return new Node<T>(item);
        }

        Node<T> reclaimed = freeList.get();
        if (reclaimed.next.getReference() != null) {
            freeList.set(reclaimed.next.getReference());
            reclaimed.next.set(null, reclaimed.next.getStamp() + 1);
        }
        reclaimed.value = item;
        return reclaimed;
    }

    @Override
    public T deq() {
        int[] lastStamp = new int[1];
        int[] firstStamp = new int[1];
        int[] nextStamp = new int[1];
        while (true) {
            Node<T> first = head.get(firstStamp);
            Node<T> last = tail.get(lastStamp);
            Node<T> next = first.next.get(nextStamp);

            if (head.getStamp() == firstStamp[0]) {
                if (first == last) {
                    if (next == null) {
                        throw new EmptyException();
                    }
                    tail.compareAndSet(last, next, lastStamp[0], lastStamp[0] + 1);
                } else {
                    T result = next.value;
                    if (head.compareAndSet(first, next, firstStamp[0], firstStamp[0] + 1)) {
                        free(first);
                        return result;
                    }
                }
            }
        }
    }

    private void free(Node<T> node) {
        node.value = null;
        if (freeList.get() != null) {
            node.next.set(freeList.get(), node.next.getStamp() + 1);
        } else {
            node.next.set(null, 0);
        }
        freeList.set(node);
    }


    private static class Node<T> {
        T value;
        AtomicStampedReference<Node<T>> next;

        public Node(T value) {
            this.value = value;
            this.next = new AtomicStampedReference<>(null, 0);
        }
    }


}

