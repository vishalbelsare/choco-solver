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

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;

/**
 *
 * <p>
 * Project: choco-solver.
 * @author Charles Prud'homme
 * @since 30/10/2018.
 */
public class MapVal implements IVal {

    private final Int2DoubleMap Av;
    private final Int2DoubleMap mAv;
    private final int os;  // offset

    public MapVal(int os) {
        this.os = os;
        this.Av = new Int2DoubleOpenHashMap();
        this.Av.defaultReturnValue(0);
        this.mAv = new Int2DoubleOpenHashMap();
        this.mAv.defaultReturnValue(0);
    }

    @Override
    public double activity(int value) {
        return Av.get(value - os);
    }

    @Override
    public void setactivity(int value, double activity) {
        Av.put(value - os, activity);
    }

    @Override
    public void update(int nb_probes) {
        double activity, oldmA, U;
        for (int k : Av.keySet()) {
            activity = Av.get(k);
            oldmA = mAv.get(k);
            U = activity - oldmA;
            mAv.put(k, U / nb_probes);
        }
    }

    @Override
    public void transfer() {
        Av.clear();
        Av.putAll(mAv);
    }
}