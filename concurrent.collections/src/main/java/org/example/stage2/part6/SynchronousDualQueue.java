package org.example.stage2.part6;

import org.example.stage2.Queue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("NonAtomicOperationOnVolatileField")
public class SynchronousDualQueue<T> implements Queue<T> {

    AtomicReference<Node<T>> head, tail;

    public SynchronousDualQueue() {
        Node<T> sentinel = new Node<>(NodeType.ITEM, null);
        head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    @Override
    public void enq(T value) {
        Node<T> offer = new Node<>(NodeType.ITEM, value);
        while (true) {
            Node<T> last = tail.get();
            Node<T> first = head.get();
            if (first == last || last.type == NodeType.ITEM) {
                Node<T> next = last.next.get();
                if (last == tail.get()) {
                    if (next != null) {
                        tail.compareAndSet(last, next);
                    } else if (last.next.compareAndSet(null, offer)) {
                        tail.compareAndSet(last, offer);
                        while (offer.item.get() != null);
                        first = head.get();
                        if(first.next.get() == offer){
                            head.compareAndSet(first, offer);
                        }
                        return;
                    }
                }
            } else {
                Node<T> next = first.next.get();
                if (last != tail.get() || first != head.get() || next == null) {
                    continue;
                }
                boolean success = next.item.compareAndSet(null, value);
                if(success){
                    return;
                }
            }
        }
    }

    @Override
    public T deq() {
        Node<T> reserve = new Node<>(NodeType.RESERVATION, null);
        while (true) {
            Node<T> last = tail.get();
            Node<T> first = head.get();
            if (first == last || last.type == NodeType.RESERVATION) {
                Node<T> next = last.next.get();
                if (last == tail.get()) {
                    if (next != null) {
                        tail.compareAndSet(last, next);
                    } else if (last.next.compareAndSet(next, reserve)) {
                        tail.compareAndSet(last, reserve);
                        while (reserve.item.get() == null) ;
                        T result = reserve.item.get();
                        first = head.get();
                        if (reserve == first.next.get()) {
                            head.compareAndSet(first, reserve);
                        }
                        return result;
                    }
                }
            } else {
                Node<T> next = first.next.get();
                if (last != tail.get() || first != head.get() || next == null) {
                    continue;
                }
                T result = next.item.get();
                boolean success = next.item.compareAndSet(result, null);
                head.compareAndSet(first, next);
                if (success) {
                    return result;
                }
            }
        }
    }

    private enum NodeType {ITEM, RESERVATION}

    private static class Node<T> {
        final NodeType type;
        final AtomicReference<T> item;
        final AtomicReference<Node<T>> next;

        public Node(NodeType type, T item) {
            this.type = type;
            this.item = new AtomicReference<>(item);
            this.next = new AtomicReference<>(null);
        }
    }

}

