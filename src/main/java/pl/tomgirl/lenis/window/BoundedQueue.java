package pl.tomgirl.lenis.window;

import java.util.function.Supplier;

public class BoundedQueue<E> {
    private final Object[] entries;
    private int head;
    private int size;

    BoundedQueue(int capacity, Supplier<E> factory) {
        entries = new Object[capacity];
        for (int i = 0; i < capacity; i++) {
            entries[i] = factory.get();
        }
    }

    E claim() {
        if (size == entries.length) {
            return null;
        }

        E entry = entryAt(size);
        size++;
        return entry;
    }

    E poll() {
        if (size == 0) {
            return null;
        }

        E entry = entryAt(0);
        head = (head + 1) % entries.length;
        size--;
        return entry;
    }

    E peekLast() {
        return size == 0 ? null : get(size - 1);
    }

    E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }
        return entryAt(index);
    }

    @SuppressWarnings("unchecked")
    private E entryAt(int index) {
        return (E) entries[(head + index) % entries.length];
    }

    int size() {
        return size;
    }

    void clear() {
        head = size = 0;
    }
}
