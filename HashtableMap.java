package application;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    protected LinkedList<Pair>[] table;

    protected class Pair {

        public KeyType key;
        public ValueType value;
    
        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    
    }

    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }

        this.table = new LinkedList[capacity];
    }

    // with default capacity = 64
    @SuppressWarnings("unchecked")
    public HashtableMap() {
        this.table = new LinkedList[64];
    }

    /**
     * Adds a new key,value pair/mapping to this collection.
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        if (containsKey(key)) {
            throw new IllegalArgumentException("Key already exists.");
        }

        int index = Math.abs(key.hashCode()) % table.length;

        // create a new linked list if the index is empty
        if (table[index] == null) {
            table[index] = new LinkedList<Pair>();
        }

        table[index].add(new Pair(key, value));

        // resize if the load factor is greater than or equal to 0.8
        if ((double) getSize() / getCapacity() >= 0.8) {
            resize();
        }
    }

    /**
     * Resizes the hashtable by doubling its capacity.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        // create a new table with double the capacity
        LinkedList<Pair>[] oldTable = table.clone();
        table = new LinkedList[oldTable.length * 2];

        // rehash all key-value pairs
        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null) {
                for (Pair pair : oldTable[i]) {
                    int index = Math.abs(pair.key.hashCode()) % table.length;

                    if (table[index] == null) {
                        table[index] = new LinkedList<Pair>();
                    }

                    table[index].add(pair);
                }
            }
        }
    }

    /**
     * Checks whether a key maps to a value in this collection.
     */
    @Override
    public boolean containsKey(KeyType key) {
        int index = Math.abs(key.hashCode()) % table.length;

        // check if the index is empty
        if (table[index] == null) {
            return false;
        }

        // search for the key in the linked list
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves the specific value that a key maps to.
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        int index = Math.abs(key.hashCode()) % table.length;

        // check if the index is empty
        if (table[index] == null) {
            throw new NoSuchElementException("Key not found.");
        }

        // search for the key in the linked list
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return pair.value;
            }
        }

        throw new NoSuchElementException("Key not found.");
    }

    /**
     * Remove the mapping for a key from this collection.
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        int index = Math.abs(key.hashCode()) % table.length;

        if (table[index] == null) {
            throw new NoSuchElementException("Key not found.");
        }

        // remove the key-value pair from the linked list
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                ValueType value = pair.value;
                table[index].remove(pair);

                return value;
            }
        }

        throw new NoSuchElementException("Key not found.");
    }

    /**
     * Removes all key,value pairs from this collection.
     */
    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
    }

    /**
     * Retrieves the number of keys stored in this collection.
     */
    @Override
    public int getSize() {
        int size = 0;

        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                size += table[i].size();
            }
        }

        return size;
    }

    /**
     * Gets the capacity of the table. 
     */
    @Override
    public int getCapacity() {
        return table.length;
    }
}
