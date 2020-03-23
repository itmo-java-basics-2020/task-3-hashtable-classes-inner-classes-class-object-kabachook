package ru.itmo.java;

public class HashTable {
    private static final int DEFAULT_CAPACITY = 500;
    private static final float DEFAULT_LOAD_FACTOR = .5f;
    private static final int HASH_GAP = 127;
    private Entry[] elements;
    private float loadFactor;
    private int size = 0;
    private int capacity;
    private int threshold;

    public HashTable(int capacity, float loadFactor) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative number.");
        }
        if (loadFactor <= 0f || loadFactor > 1f) {
            throw new IllegalArgumentException("Load factor must be in range from 0 to 1");
        }

        if (capacity < DEFAULT_CAPACITY) {
            capacity = DEFAULT_CAPACITY;
        }

        this.capacity = capacity;
        this.elements = new Entry[capacity];
        this.loadFactor = loadFactor;
        this.threshold = (int) (this.capacity * this.loadFactor);
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    private int hash(Object obj) {
        return (obj.hashCode() % this.capacity + this.capacity) % this.capacity;
    }

    private int nextIteration(int hash) {
        return (hash + HASH_GAP) % this.capacity;
    }

    // Searches for element
    private int probe(Object key) {
        int hashCode = this.hash(key);

        int i = 0;
        while (i < this.capacity && this.elements[hashCode] != null && (!key.equals(this.elements[hashCode].key) || this.elements[hashCode].deleted)) {
            hashCode = nextIteration(hashCode);
            i++;
        }

        return hashCode;
    }

    // Searches for place to put
    private int probePut(Object key) {
        int hashCode = this.hash(key);

        while (this.elements[hashCode] != null && !this.elements[hashCode].deleted) {
            hashCode = nextIteration(hashCode);
        }

        return hashCode;
    }

    private void rehash() {
        var oldElements = this.elements;

        this.elements = new Entry[this.capacity * 2];
        this.threshold = (int) (loadFactor * this.capacity * 2);
        this.size = 0;
        this.capacity *= 2;

        for (var element : oldElements) {
            if (element != null && !element.deleted) {
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


            this.elements[idx] = new Entry(key, value);
            size++;
            return null;
        }

        if (this.elements[idx].deleted) size++;

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
        this.elements[idx] = new Entry(null, null, true);

        return deletedElement.value;
    }

    public int size() {
        return this.size;
    }

    private class Entry {
        Object key;
        Object value;
        boolean deleted;

        public Entry(Object key, Object value, boolean deleted) {
            this.key = key;
            this.value = value;
            this.deleted = deleted;
        }

        public Entry(Object key, Object value) {
            this(key, value, false);
        }
    }
}
