/*
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.util.objects;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * A map which ensures key uniqueness and order over values.
 *
 * @param <E> key's type
 * @author Charles Prud'homme
 * @implSpec The value is necessarily a primitive integer greater or equal to 0. <p> Project:
 * choco-solver.
 * @since 30/01/2017.
 */
public class ValueSortedMap<E> {

    /**
     * Default value for no entry
     */
    private static final int NO_ENTRY = -1;
    /**
     * Set : values -> E
     */
    Object2IntMap<E> map;
    /**
     * Ordered set : values -> E
     */
    TreeMap<Integer, E> rmap;

    /**
     * Create a doubly-linked set E <-> value, where value is a int.
     * Keys are unique, so do values.
     * Value can be retrieved through key and key can be retrieved through value.
     * In addition, values are sorted.
     */
    public ValueSortedMap() {
        this.map = new Object2IntOpenHashMap<>();
        this.map.defaultReturnValue(NO_ENTRY);
        this.rmap = new TreeMap<>();
    }

    /**
     * Remove all entries from this map.
     */
    public void clear() {
        map.clear();
        rmap.clear();
    }

    /**
     * Insert a key <i>k</i> with the value <i>v</i> in this.
     * If the key already exists, it is replaced.
     * @param k the key
     * @param v the value
     */
    public void put(E k, int v) {
        if(map.containsKey(k)){
            replace(k, v);
        }else {
            map.put(k, v);
            rmap.put(v, k);
        }
    }

    /**
     * Replaces the value attached with key <i>k</i> with <i>v</i>.
     * @param k the key
     * @param v the value
     */
    public void replace(E k, int v) {
        int cValue = map.getInt(k);
        assert cValue != NO_ENTRY;
        if(v - cValue != 0) {
            map.replace(k, v);
            rmap.remove(cValue);
            rmap.put(v, k);
        }
        assert rmap.get(map.getInt(k)) == k;
        assert map.getInt(rmap.get(v)) == v;
    }

    /**
     * @param k a key
     * @return value attached to the key <i>k</i>, or {@link #NO_ENTRY} otherwise.
     */
    public int getValue(E k) {
        return map.getInt(k);
    }

    /**
     * @param k a key
     * @param defaultValue  value to return if <i>k</i> is not known.
     * @return value attached to the key <i>k</i>, or <i>defaultValue</i> otherwise.
     */
    public int getValueOrDefault(E k, int defaultValue) {
        int value = map.getInt(k);
        if (value == NO_ENTRY) {
            return defaultValue;
        }
        return value;
    }

    /**
     * return the largest value stored in this
     * @return the largest value stored in this
     */
    public int getLastValue() {
//        return rmap.lastValue();
        return rmap.lastKey();
    }

    /**
     * return the lowest value stored in this
     * @return the lowest value stored in this
     */
    public int getLowerValue(int value){
        Integer low = rmap.lowerKey(value);
        if(low == null){
            low = -1;
        }
        return low;
    }
    /**
     * return and remove the largest value stored in this
     * @return  the largest value stored in this
     */
    public int pollLastValue() {
        Map.Entry<Integer, E> last = rmap.pollLastEntry();
        map.removeInt(last.getValue());
        return last.getKey();
    }

    /**
     * Remove the key <k>k</k> and its value from this
     * @param k a key
     */
    public void remove(E k){
        rmap.remove(map.removeInt(k));
    }

    /**
     * Remove any key and its value that matches the predicate <i>filter</i>
     * @param filter predicate that satisfies (key,value) to remove
     */
    @SuppressWarnings("unused")
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = map.keySet().iterator();
        while (each.hasNext()) {
            E e = each.next();
            if (filter.test(e)) {
                int value = map.getInt(e);
                each.remove();
                rmap.remove(value);
                removed = true;
            }
        }
        return removed;
    }

    /**
     * @return <i>true</i> if this is empty
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

}
