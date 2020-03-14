package ru.itmo.java;

import java.util.Map;

public class HashTable {
    private static final int DEFAULT_CAPACITY = 500;
    private static final float DEFAULT_LOAD_FACTOR = .5f;
    private static final int HASH_GAP = 127;
    private Entry[] elements;
    private boolean[] deleted;
    private float loadFactor;
    private int size = 0;
    private int capacity;
    private int threshold;

    private class Entry {
        Object key;
        Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    public HashTable(int capacity, float loadFactor) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative number.");
        }
        if (loadFactor <= 0f || loadFactor > 1f) {
            throw new IllegalArgumentException("Load factor must be in range from 0 to 1");
        }

        if (capacity < DEFAULT_CAPACITY){
            capacity = DEFAULT_CAPACITY;
        }

        this.capacity = capacity;
        this.elements = new Entry[capacity];
        this.deleted = new boolean[capacity];
        this.loadFactor = loadFactor;
        this.threshold = (int) (this.capacity * this.loadFactor);
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    private int hash(Object obj) {
        return (obj.hashCode() % this.capacity + this.capacity) % this.capacity;
    }

    private int nextIteration(int hash){
        return (hash + HASH_GAP) % this.capacity;
    }

    private int probe(Object key) {
        int hashCode = this.hash(key);

        while (this.deleted[hashCode] || this.elements[hashCode] != null && !this.elements[hashCode].key.equals(key)) {
            hashCode = nextIteration(hashCode);
        }
        return hashCode;
    }

    private int probePut(Object key) {
        int hashCode = this.hash(key);

        while (this.elements[hashCode] != null) {
            hashCode = nextIteration(hashCode);
        }
        return hashCode;
    }

    private void rehash() {
        var oldElements = this.elements;

        this.elements = new Entry[this.capacity * 2];
        this.deleted = new boolean[this.capacity * 2];
        this.threshold = (int) (loadFactor * this.capacity * 2);
        this.size = 0;
        this.capacity *= 2;

        for (var element : oldElements) {
            if (element != null) {
                this.put(element.key, element.value);
            }
        }
    }

    public Object put(Object key, Object value) {
        if (this.size >= this.threshold) {
            this.rehash();
        }

        int idx = probe(key);

        if (this.elements[idx] == null) {
            idx = probePut(key);

            if (this.deleted[idx]) {
                this.deleted[idx] = false;
            }

            this.elements[idx] = new Entry(key, value);
            size++;

            return null;
        }

        Entry oldElement = this.elements[idx];
        this.elements[idx] = new Entry(key, value);

        return oldElement.value;
    }

    Object get(Object key) {
        int idx = probe(key);

        return this.elements[idx] == null ? null : this.elements[idx].value;
    }

    Object remove(Object key) {
        int idx = probe(key);
        if (this.elements[idx] == null) return null;

        size--;
        var deletedElement = this.elements[idx];

        this.deleted[idx] = true;
        this.elements[idx] = null;

        return deletedElement.value;
    }

    public int size() {
        return this.size;
    }
}
