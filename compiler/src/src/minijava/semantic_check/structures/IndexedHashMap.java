package minijava.semantic_check.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Simple extension to the java.util.HashMap structure.
 * Adds a java.util.ArrayList to allow fast index retrieval for the HashMap keys.
 */
public class IndexedHashMap<K, V> extends HashMap<K, V> implements Iterable<K> {

    private ArrayList<K> orderedKeys;

    public IndexedHashMap() {
        this.orderedKeys = new ArrayList<>();
    }

    public IndexedHashMap(IndexedHashMap other) {
        super(other);
        this.orderedKeys = new ArrayList<>(other.orderedKeys);
    }

    public int getKeyIndex(K key) {
        return this.orderedKeys.indexOf(key);
    }

    @Override
    public V put(K key, V value) {
        super.put(key, value);
        if (!this.orderedKeys.contains(key)) {
            this.orderedKeys.add(key);
        }
        return value;
    }

    @Override
    public Iterator<K> iterator() {
        return orderedKeys.iterator();
    }

}
