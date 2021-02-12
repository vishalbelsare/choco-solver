/*
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.search.strategy.selectors.variables;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.loop.monitors.IMonitorInitialize;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 12/11/2020
 */
public class ClausesBased extends AbstractStrategy<IntVar> implements IMonitorInitialize {

    protected Object2DoubleMap<IntVar> activity;

    Solver solver;

    double var_inc = 1d;

    private final List<IntVar> bests;

    private final ToDoubleFunction<IntVar> tiebreaker;

    /**
     * The way value is selected for a given variable
     */
    private final ToIntFunction<IntVar> valueSelector;

    public ClausesBased(Model model,
                        IntVar[] decisions,
                        ToDoubleFunction<IntVar> tiebreaker,
                        ToIntFunction<IntVar> valueSelector) {
        super(decisions);
        solver = model.getSolver();
        this.valueSelector = valueSelector;
        activity = new Object2DoubleOpenHashMap<>();
        for(IntVar v : decisions){
            activity.put(v, 1d);
        }
        this.tiebreaker = tiebreaker;
        bests = new ArrayList<>();
        model.getSolver().plugMonitor(this);
    }

    @Override
    public boolean init() {
        solver.getModel()
                .getClauseConstraint()
                .getClauseStore()
                .declareClausesBasedStrategy(this);
        return true;
    }

    public void bump(IntVar v) {
        activity.computeDouble(v, (k, d) -> d == null ? var_inc : 1.0);
    }

    public void decayActivity() {
        if ((var_inc *= 1.05) > 1e100) {
            for (Object v : activity.keySet()) {
                double act = activity.getDouble(v);
                activity.put((IntVar)v, act * 1e-100);
            }
            var_inc *= 1e-100;
        }
    }

    @Override
    public Decision<IntVar> getDecision() {
        IntVar best = null;
        bests.clear();
        final double[] bst = {Integer.MIN_VALUE};
        activity.forEach((var,act) -> {
            if(!var.isInstantiated()){
                if (act >= bst[0]) {
                    if (act > bst[0]) {
                        bests.clear();
                        bst[0] = act;
                    }
                    bests.add(var);
                }
            }
        });
        if (bests.size() > 0) {
            best = bests.get(0);
            bst[0] = tiebreaker.applyAsDouble(best);
            double tmp;
            for(int i = 1; i < bests.size(); i++){
                if((tmp = tiebreaker.applyAsDouble(bests.get(i))) < bst[0]){
                    bst[0] = tmp;
                    best = bests.get(i);
                }
            }
        }
        return computeDecision(best);
    }

    @Override
    public Decision<IntVar> computeDecision(IntVar variable) {
        if (variable == null || variable.isInstantiated()) {
            return null;
        }
        int currentVal = valueSelector.applyAsInt(variable);
        return variable.getModel()
                .getSolver()
                .getDecisionPath()
                .makeIntDecision(variable,
                        DecisionOperatorFactory.makeIntEq(),
                        currentVal);
    }

}
