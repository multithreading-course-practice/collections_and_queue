package org.example.stage2;

public interface Queue<T> {
    void enq(T item);

    T deq();
}
