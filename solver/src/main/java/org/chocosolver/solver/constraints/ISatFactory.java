/*
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.chocosolver.solver.ISelf;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.nary.cnf.ILogical;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.constraints.nary.cnf.LogicTreeToolBox;
import org.chocosolver.solver.constraints.nary.sat.PropSat;
import org.chocosolver.solver.constraints.reification.LocalConstructiveDisjunction;
import org.chocosolver.solver.variables.BoolVar;

/**
 * A factory dedicated to SAT.
 * <p/>
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 15/07/13
 */
public interface ISatFactory extends ISelf<Model> {

    /**
     * Ensures that the clauses defined in the Boolean logic formula TREE are satisfied.
     *
     * @param TREE   the syntactic tree
     */
    default void addClauses(LogOp TREE) {
        ILogical tree = LogicTreeToolBox.toCNF(TREE, ref());
        if (ref().boolVar(true).equals(tree)) {
            addClauseTrue(ref().boolVar(true));
        } else if (ref().boolVar(false).equals(tree)) {
            addClauseTrue(ref().boolVar(false));
        } else {
            ILogical[] clauses;
            if (!tree.isLit() && ((LogOp) tree).is(LogOp.Operator.AND)) {
                clauses = ((LogOp) tree).getChildren();
            } else {
                clauses = new ILogical[]{tree};
            }
            for (int i = 0; i < clauses.length; i++) {
                ILogical clause = clauses[i];
                if (clause.isLit()) {
                    BoolVar bv = (BoolVar) clause;
                    addClauseTrue(bv);
                } else {
                    LogOp n = (LogOp) clause;
                    BoolVar[] bvars = n.flattenBoolVar();
                    if (ref().getSettings().enableSAT()) {
                        IntArrayList lits = new IntArrayList(bvars.length);
                        PropSat sat = ref().getMinisat().getPropSat();
                        // init internal structures
                        sat.beforeAddingClauses();
                        for (int j = 0; j < bvars.length; j++) {
                            lits.add(sat.makeLiteral(bvars[j], true));
                        }
                        // TODO: pass by satsolver directly
                        sat.addClause(lits);
                        sat.afterAddingClauses();
                    }else{
                        ref().sum(bvars, ">", 0).post();
                    }
                }
            }
        }
    }

    /**
     * Ensures that the clause defined by POSLITS and NEGLITS is satisfied.
     *
     * @param POSLITS positive literals
     * @param NEGLITS negative literals
     */
    default void addClauses(BoolVar[] POSLITS, BoolVar[] NEGLITS) {
        if (ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] pos = new int[POSLITS.length];
            for (int i = 0; i < POSLITS.length; i++) {
                pos[i] = sat.makeVar(POSLITS[i]);
            }
            int[] neg = new int[NEGLITS.length];
            for (int i = 0; i < NEGLITS.length; i++) {
                neg[i] = sat.makeVar(NEGLITS[i]);
            }
            sat.getSatSolver().addClause(pos, neg);
            sat.afterAddingClauses();
        }else{
            int PL = POSLITS.length;
            int NL = NEGLITS.length;
            BoolVar[] LITS = new BoolVar[PL + NL];
            System.arraycopy(POSLITS, 0, LITS, 0, PL);
            for (int i = 0; i < NL; i++) {
                LITS[i + PL] = NEGLITS[i].not();
            }
            ref().sum(LITS, ">", 0).post();
        }
    }

    /**
     * Add a unit clause stating that BOOLVAR must be true
     *
     * @param BOOLVAR a boolean variable
     */
    default void addClauseTrue(BoolVar BOOLVAR) {
        if (ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addTrue(sat.makeVar(BOOLVAR));
            sat.afterAddingClauses();
        }else{
            ref().arithm(BOOLVAR, "=", 1).post();
        }
    }

    /**
     * Add a unit clause stating that BOOLVAR must be false
     *
     * @param BOOLVAR a boolean variable
     */
    default void addClauseFalse(BoolVar BOOLVAR) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addFalse(sat.makeVar(BOOLVAR));
            sat.afterAddingClauses();
        }else{
            ref().arithm(BOOLVAR, "=", 0).post();
        }
    }

    /**
     * Add a clause stating that: LEFT == RIGHT
     *
     * @param LEFT  a boolean variable
     * @param RIGHT another boolean variable
     */
    default void addClausesBoolEq(BoolVar LEFT, BoolVar RIGHT) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolEq(sat.makeVar(LEFT), sat.makeVar(RIGHT));
            sat.afterAddingClauses();
        }else{
            ref().arithm(LEFT, "=", RIGHT).post();
        }
    }

    /**
     * Add a clause stating that: LEFT &le; RIGHT
     *
     * @param LEFT  a boolean variable
     * @param RIGHT another boolean variable
     */
    default void addClausesBoolLe(BoolVar LEFT, BoolVar RIGHT) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolLe(sat.makeVar(LEFT), sat.makeVar(RIGHT));
            sat.afterAddingClauses();
        }else{
            ref().arithm(LEFT, "<=", RIGHT).post();
        }
    }

    /**
     * Add a clause stating that: LEFT < RIGHT
     *
     * @param LEFT  a boolean variable
     * @param RIGHT another boolean variable
     */
    default void addClausesBoolLt(BoolVar LEFT, BoolVar RIGHT) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolLt(sat.makeVar(LEFT), sat.makeVar(RIGHT));
            sat.afterAddingClauses();
        }else{
            ref().arithm(LEFT, "<", RIGHT).post();
        }
    }

    /**
     * Add a clause stating that: LEFT != RIGHT
     *
     * @param LEFT  a boolean variable
     * @param RIGHT another boolean variable
     */
    default void addClausesBoolNot(BoolVar LEFT, BoolVar RIGHT) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolNot(sat.makeVar(LEFT), sat.makeVar(RIGHT));
            sat.afterAddingClauses();
        }else{
            ref().arithm(LEFT, "!=", RIGHT).post();
        }
    }

    /**
     * Add a clause stating that: (BOOLVARS<sub>1</sub>&or;BOOLVARS<sub>2</sub>&or;...&or;BOOLVARS<sub>n</sub>) &hArr; TARGET
     *
     * @param BOOLVARS a list of boolean variables
     * @param TARGET   the reified boolean variable
     */
    default void addClausesBoolOrArrayEqVar(BoolVar[] BOOLVARS, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addBoolOrArrayEqVar(vars, sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            ref().max(TARGET, BOOLVARS).post();
        }
    }

    /**
     * Add a clause stating that: (BOOLVARS<sub>1</sub>&and;BOOLVARS<sub>2</sub>&and;...&and;BOOLVARS<sub>n</sub>) &hArr; TARGET
     *
     * @param BOOLVARS a list of boolean variables
     * @param TARGET   the reified boolean variable
     */
    default void addClausesBoolAndArrayEqVar(BoolVar[] BOOLVARS, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addBoolAndArrayEqVar(vars, sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            ref().min(TARGET, BOOLVARS).post();
        }
    }

    /**
     * Add a clause stating that: (LEFT &or; RIGHT) &hArr; TARGET
     *
     * @param LEFT   a boolean variable
     * @param RIGHT  another boolean variable
     * @param TARGET the reified boolean variable
     */
    default void addClausesBoolOrEqVar(BoolVar LEFT, BoolVar RIGHT, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolOrEqVar(sat.makeVar(LEFT), sat.makeVar(RIGHT), sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            ref().arithm(LEFT, "+", RIGHT, ">", 0).reifyWith(TARGET);
        }
    }

    /**
     * Add a clause stating that: (LEFT &and; RIGHT) &hArr; TARGET
     *
     * @param LEFT   a boolean variable
     * @param RIGHT  another boolean variable
     * @param TARGET the reified boolean variable
     */
    default void addClausesBoolAndEqVar(BoolVar LEFT, BoolVar RIGHT, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolAndEqVar(sat.makeVar(LEFT), sat.makeVar(RIGHT), sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            ref().arithm(LEFT, "+", RIGHT, "=", 2).reifyWith(TARGET);
        }
    }

    /**
     * Add a clause stating that: (LEFT &oplus; RIGHT) &hArr; TARGET
     *
     * @param LEFT   a boolean variable
     * @param RIGHT  another boolean variable
     * @param TARGET the reified boolean variable
     */
    default void addClausesBoolXorEqVar(BoolVar LEFT, BoolVar RIGHT, BoolVar TARGET) {
        addClausesBoolIsNeqVar(LEFT, RIGHT, TARGET);
    }

    /**
     * Add a clause stating that: (LEFT == RIGHT) &hArr; TARGET
     *
     * @param LEFT   a boolean variable
     * @param RIGHT  another boolean variable
     * @param TARGET the reified boolean variable
     * @return true if the clause has been added to the clause store
     */
    default void addClausesBoolIsEqVar(BoolVar LEFT, BoolVar RIGHT, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolIsEqVar(sat.makeVar(LEFT), sat.makeVar(RIGHT), sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            //noinspection SuspiciousNameCombination
            ref().reifyXeqY(LEFT, RIGHT, TARGET);
        }
    }

    /**
     * Add a clause stating that: (LEFT &ne; RIGHT) &hArr; TARGET
     *
     * @param LEFT   a boolean variable
     * @param RIGHT  another boolean variable
     * @param TARGET the reified boolean variable
     */
    default void addClausesBoolIsNeqVar(BoolVar LEFT, BoolVar RIGHT, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolIsNeqVar(sat.makeVar(LEFT), sat.makeVar(RIGHT), sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            //noinspection SuspiciousNameCombination
            ref().reifyXneY(LEFT, RIGHT, TARGET);
        }
    }

    /**
     * Add a clause stating that: (LEFT &le; RIGHT) &hArr; TARGET
     *
     * @param LEFT   a boolean variable
     * @param RIGHT  another boolean variable
     * @param TARGET the reified boolean variable
     */
    default void addClausesBoolIsLeVar(BoolVar LEFT, BoolVar RIGHT, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolIsLeVar(sat.makeVar(LEFT), sat.makeVar(RIGHT), sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            //noinspection SuspiciousNameCombination
            ref().reifyXleY(LEFT, RIGHT, TARGET);
        }
    }

    /**
     * Add a clause stating that: (LEFT < RIGHT) &hArr; TARGET
     *
     * @param LEFT   a boolean variable
     * @param RIGHT  another boolean variable
     * @param TARGET the reified boolean variable
     */
    default void addClausesBoolIsLtVar(BoolVar LEFT, BoolVar RIGHT, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            sat.getSatSolver().addBoolIsLtVar(sat.makeVar(LEFT), sat.makeVar(RIGHT), sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            //noinspection SuspiciousNameCombination
            ref().reifyXltY(LEFT, RIGHT, TARGET);
        }
    }

    /**
     * Add a clause stating that: BOOLVARS<sub>1</sub>&or;BOOLVARS<sub>2</sub>&or;...&or;BOOLVARS<sub>n</sub>
     *
     * @param BOOLVARS a list of boolean variables
     */
    default void addClausesBoolOrArrayEqualTrue(BoolVar[] BOOLVARS) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addBoolOrArrayEqualTrue(vars);
            sat.afterAddingClauses();
        }else{
            ref().sum(BOOLVARS, ">", 0).post();
        }
    }

    /**
     * Add a clause stating that: BOOLVARS<sub>1</sub>&and;BOOLVARS<sub>2</sub>&and;...&and;BOOLVARS<sub>n</sub>
     *
     * @param BOOLVARS a list of boolean variables
     */
    default void addClausesBoolAndArrayEqualFalse(BoolVar[] BOOLVARS) {
        addClausesAtMostNMinusOne(BOOLVARS);
    }

    /**
     * Add a clause stating that: &sum; BOOLVARS<sub>i</sub> &le; 1
     *
     * @param BOOLVARS a list of boolean variables
     */
    @SuppressWarnings("unused")
    default void addClausesAtMostOne(BoolVar[] BOOLVARS) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addAtMostOne(vars);
            sat.afterAddingClauses();
        }else{
            ref().sum(BOOLVARS, "<", 2).post();
        }
    }

    /**
     * Add a clause stating that: &sum; BOOLVARS<sub>i</sub> &le; n-1
     *
     * @param BOOLVARS a list of boolean variables
     */
    default void addClausesAtMostNMinusOne(BoolVar[] BOOLVARS) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addAtMostNMinusOne(vars);
            sat.afterAddingClauses();
        }else{
            ref().sum(BOOLVARS, "<", BOOLVARS.length).post();
        }
    }

    /**
     * Add a clause stating that: sum(BOOLVARS<sub>i</sub>) &ge; TARGET
     *
     * @param BOOLVARS a list of boolean variables
     * @param TARGET a boolean variable
     */
    default void addClausesSumBoolArrayGreaterEqVar(BoolVar[] BOOLVARS, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addSumBoolArrayGreaterEqVar(vars, sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            ref().sum(BOOLVARS, ">=", TARGET).post();
        }
    }

    /**
     * Add a clause stating that: max(BOOLVARS<sub>i</sub>) &le; TARGET
     *
     * @param BOOLVARS a list of boolean variables
     * @param TARGET a boolean variable
     */
    default void addClausesMaxBoolArrayLessEqVar(BoolVar[] BOOLVARS, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addMaxBoolArrayLessEqVar(vars, sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            BoolVar max  = ref().boolVar(ref().generateName("bool_max"));
            ref().max(max, BOOLVARS).post();
            max.le(TARGET).post();
        }
    }

    /**
     * Add a clause stating that: sum(BOOLVARS<sub>i</sub>) &le; TARGET
     *
     * @param BOOLVARS a list of boolean variables
     * @param TARGET a boolean variable
     */
    default void addClausesSumBoolArrayLessEqVar(BoolVar[] BOOLVARS, BoolVar TARGET) {
        if(ref().getSettings().enableSAT()) {
            PropSat sat = ref().getMinisat().getPropSat();
            sat.beforeAddingClauses();
            if (BOOLVARS.length == 1) {
                addClausesBoolLe(BOOLVARS[0], TARGET);
            }
            int[] vars = new int[BOOLVARS.length];
            for (int i = 0; i < BOOLVARS.length; i++) {
                vars[i] = sat.makeVar(BOOLVARS[i]);
            }
            sat.getSatSolver().addSumBoolArrayLessEqVar(vars, sat.makeVar(TARGET));
            sat.afterAddingClauses();
        }else{
            ref().sum(BOOLVARS, "<=", TARGET).post();
        }
    }

    /**
     * Make a constructive disjunction constraint
     *
     * @param cstrs constraint in disjunction
     */
    default void addConstructiveDisjunction(Constraint... cstrs) {
        new LocalConstructiveDisjunction(cstrs).post();
    }

}
