/*
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.extension.nary;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;

/**
 * A LargeRelation for cases where domain are too big to be stored in a single array. Then, we store
 * it in a chunk bitsets <br/>
 *
 * @author Charles Prud'homme
 * @since 08/06/11
 */
@SuppressWarnings("rawtypes")
public class TuplesVeryLargeTable extends LargeRelation {

    /**
     * the number of dimensions of the considered tuples
     */
    private final int n;

    private final boolean feasible;

    private final Int2ObjectMap<Int2ObjectMap> supports;

    public TuplesVeryLargeTable(Tuples tuples, IntVar[] vars) {
        n = vars.length;
        feasible = tuples.isFeasible();
        supports = new Int2ObjectOpenHashMap<>();
        int nt = tuples.nbTuples();
        for (int i = 0; i < nt; i++) {
            int[] tuple = tuples.get(i);
            if (valid(tuple, vars)) {
                setTuple(tuple);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean checkTuple(int[] tuple) {
        Int2ObjectMap<Int2ObjectMap> current = supports;
        int i = 0;
        while (i < n - 1) {
            current = current.get(tuple[i++]);
            if (current == null) {
                return false;
            }
        }
        current = current.get(tuple[i]);
        return current != null;
    }

    public boolean isConsistent(int[] tuple) {
        return checkTuple(tuple) == feasible;
    }

    @SuppressWarnings("unchecked")
    private void setTuple(int[] tuple) {
        Int2ObjectMap<Int2ObjectMap> current = supports;
        for (int i = 0; i < tuple.length; i++) {
            Int2ObjectMap<Int2ObjectMap> _current = current.get(tuple[i]);
            if (_current == null) {
                _current = new Int2ObjectOpenHashMap<>();
                current.put(tuple[i], _current);
            }
            current = _current;
        }
    }

    @Override
    public Tuples convert() {
        Tuples tuples = new Tuples(feasible);
        int[] tt = new int[n];
        tuple(supports, tt, 0, tuples);
        return tuples;
    }

    @SuppressWarnings("unchecked")
    private void tuple(Int2ObjectMap<Int2ObjectMap> current, int[] tt, int p, Tuples tuples) {
        if (current.isEmpty()) {
            tuples.add(tt);
        } else {
            for (int k : current.keySet()) {
                tt[p] = k;
                tuple(current.get(k), tt, p + 1, tuples);
            }
        }
    }

}
