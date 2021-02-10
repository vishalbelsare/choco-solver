/*
 * This file is part of choco-sat, http://choco-solver.org/
 *
 * Copyright (c) 2020, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 *
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.sat;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <p>
 * Project: choco-sat.
 *
 * @author Charles Prud'homme
 * @since 07/03/2016.
 */
public class SatSolverTest {

    private SatSolver sat;
    private int a, b, c, d;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        sat = new SatSolver();
        a = sat.newVariable();
        b = sat.newVariable();
        c = sat.newVariable();
        d = sat.newVariable();
    }

    @Test(groups = "1s")
    public void testNewVariable() {
        Assert.assertEquals(a, 0);
        Assert.assertEquals(b, 1);
        Assert.assertEquals(c, 2);
        Assert.assertEquals(d, 3);
        Assert.assertEquals(SatSolver.makeLiteral(a, true), 1);
        Assert.assertEquals(SatSolver.makeLiteral(a, false), 0);
        Assert.assertEquals(SatSolver.makeLiteral(b, true), 3);
        Assert.assertEquals(SatSolver.makeLiteral(b, false), 2);
        Assert.assertEquals(SatSolver.makeLiteral(c, true), 5);
        Assert.assertEquals(SatSolver.makeLiteral(c, false), 4);
        Assert.assertEquals(SatSolver.makeLiteral(d, true), 7);
        Assert.assertEquals(SatSolver.makeLiteral(d, false), 6);
    }

    @Test(groups = "1s")
    public void testAddClause() {
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(d), SatSolver.Boolean.kUndefined);

        Assert.assertTrue(sat.addClause(SatSolver.makeLiteral(a, true)));
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kFalse);
        Assert.assertTrue(sat.addClause(SatSolver.makeLiteral(b, false)));
        Assert.assertEquals(sat.assignment_.get(b), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.qhead_, 2);
        Assert.assertEquals(sat.clauses.size(), 0);
        Assert.assertEquals(sat.implies_.size(), 0);
    }

    @Test(groups = "1s")
    public void testAddEmptyClause() {
        Assert.assertFalse(sat.addEmptyClause());
    }

    @Test(groups = "1s")
    public void testAddClause1() {
        int ap = SatSolver.makeLiteral(a, true);
        int bp = SatSolver.makeLiteral(b, true);
        Assert.assertTrue(sat.addClause(ap,bp));
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.clauses.size(), 0);
        Assert.assertEquals(sat.implies_.size(), 2);
        Assert.assertEquals(sat.qhead_, 0);
        Assert.assertNull(sat.implies_.get(ap));
        Assert.assertNull(sat.implies_.get(bp));
        Assert.assertEquals(sat.implies_.get(SatSolver.negated(ap)).size(), 1);
        Assert.assertEquals(sat.implies_.get(SatSolver.negated(ap)).getInt(0), bp);
        Assert.assertEquals(sat.implies_.get(SatSolver.negated(bp)).size(), 1);
        Assert.assertEquals(sat.implies_.get(SatSolver.negated(bp)).getInt(0), ap);
    }

    @Test(groups = "1s")
    public void testAddClause2() {
        int ap = SatSolver.makeLiteral(a, true);
        int bp = SatSolver.makeLiteral(b, true);
        int cp = SatSolver.makeLiteral(c, true);
        Assert.assertTrue(sat.addClause(ap,bp, cp));
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 0);
        Assert.assertEquals(sat.implies_.size(), 0);
        Assert.assertEquals(sat.clauses.size(), 1);
    }

    @Test(groups = "1s")
    public void testAddClause3() {
        int ap = SatSolver.makeLiteral(a, true);
        int bp = SatSolver.makeLiteral(b, true);
        int cp = SatSolver.makeLiteral(c, true);
        int dp = SatSolver.makeLiteral(d, true);
        Assert.assertTrue(sat.addClause(new IntArrayList(new int[]{ap,bp, cp, dp})));
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 0);
        Assert.assertEquals(sat.implies_.size(), 0);
        Assert.assertEquals(sat.clauses.size(), 1);
    }

    @Test(groups = "1s")
    public void testAddClause4() {
        int a1 = SatSolver.makeLiteral(a, true);
        Assert.assertTrue(sat.addClause(new IntArrayList(new int[]{a1,a1,a1, a1})));
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.assignment_.get(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 1);
        Assert.assertEquals(sat.implies_.size(), 0);
        Assert.assertEquals(sat.clauses.size(), 0);
    }

    @Test(groups = "1s")
    public void testAddClause5() {
        int a1 = SatSolver.makeLiteral(a, true);
        int a2 = SatSolver.makeLiteral(a, false);
        Assert.assertTrue(sat.addClause(new IntArrayList(new int[]{a1,a2})));
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.assignment_.get(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 0);
        Assert.assertEquals(sat.implies_.size(), 0);
        Assert.assertEquals(sat.clauses.size(), 0);
    }

    @Test(groups = "1s")
    public void testAddClause6() {
        int ap = SatSolver.makeLiteral(a, true);
        sat.uncheckedEnqueue(ap);
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kFalse);
        int an = SatSolver.makeLiteral(a, false);
        Assert.assertFalse(sat.addClause(an));
        sat.propagate();
        Assert.assertEquals(sat.assignment_.get(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.qhead_, 1);
        Assert.assertFalse(sat.ok_);
    }

    @Test(groups = "1s")
    public void testInitPropagator() {
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.touched_variables_.size(), 1);
        Assert.assertFalse(sat.initPropagator());
        Assert.assertEquals(sat.touched_variables_.size(), 0);
    }

    @Test(groups = "1s")
    public void testCancelUntil() {
        int ap = SatSolver.makeLiteral(a, true);
        int an = SatSolver.makeLiteral(a, false);
        int bp = SatSolver.makeLiteral(b, true);
        int cp = SatSolver.makeLiteral(c, true);
        int cn = SatSolver.makeLiteral(c, false);
        int dp = SatSolver.makeLiteral(d, true);
        sat.addClause(an, bp);
        sat.addClause(cn, dp);
        sat.propagateOneLiteral(ap);
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 2);
        Assert.assertEquals(sat.trail_.size(), 2);
        Assert.assertEquals(sat.trail_.getInt(0), 1);
        Assert.assertEquals(sat.trail_.getInt(1), 3);
        Assert.assertEquals(sat.trail_markers_.size(), 1);
        Assert.assertEquals(sat.trailMarker(), 1);
        Assert.assertEquals(sat.trail_markers_.getInt(0), 0);
        sat.propagateOneLiteral(cp);
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.qhead_, 4);
        Assert.assertEquals(sat.trail_.size(), 4);
        Assert.assertEquals(sat.trail_.getInt(0), 1);
        Assert.assertEquals(sat.trail_.getInt(1), 3);
        Assert.assertEquals(sat.trail_.getInt(2), 5);
        Assert.assertEquals(sat.trail_.getInt(3), 7);
        Assert.assertEquals(sat.trail_markers_.size(), 2);
        Assert.assertEquals(sat.trailMarker(), 2);
        Assert.assertEquals(sat.trail_markers_.getInt(0), 0);
        Assert.assertEquals(sat.trail_markers_.getInt(1), 2);
        sat.cancelUntil(1);
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 2);
        Assert.assertEquals(sat.trail_.size(), 2);
        Assert.assertEquals(sat.trail_.getInt(0), 1);
        Assert.assertEquals(sat.trail_.getInt(1), 3);
        Assert.assertEquals(sat.trail_markers_.size(), 1);
        Assert.assertEquals(sat.trailMarker(), 1);
        Assert.assertEquals(sat.trail_markers_.getInt(0), 0);
        sat.cancelUntil(0);
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 0);
        Assert.assertEquals(sat.trail_.size(), 0);
        Assert.assertEquals(sat.trail_markers_.size(), 0);
        Assert.assertEquals(sat.trailMarker(), 0);
    }

    @Test(groups = "1s")
    public void testValueVar() {
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        sat.propagateOneLiteral(SatSolver.makeLiteral(c, true));
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);

        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
        sat.propagateOneLiteral(SatSolver.makeLiteral(d, false));
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kTrue);
    }

    @Test(groups = "1s")
    public void testValueLit() {
        int cp = SatSolver.makeLiteral(c, true);
        int cn = SatSolver.makeLiteral(c, false);
        int dp = SatSolver.makeLiteral(d, true);
        int dn = SatSolver.makeLiteral(d, false);

        Assert.assertEquals(sat.valueLit(cp), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueLit(cn), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueLit(dp), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueLit(dn), SatSolver.Boolean.kUndefined);

        sat.propagateOneLiteral(SatSolver.makeLiteral(c, true));
        Assert.assertEquals(sat.valueLit(cp), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueLit(cn), SatSolver.Boolean.kFalse);

        sat.propagateOneLiteral(SatSolver.makeLiteral(d, false));
        Assert.assertEquals(sat.valueLit(dp), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueLit(dn), SatSolver.Boolean.kTrue);
    }
    
    @Test(groups = "1s")
    public void testPropagateOneLiteral() {
        int ap = SatSolver.makeLiteral(a, true);
        int an = SatSolver.makeLiteral(a, false);
        int bp = SatSolver.makeLiteral(b, true);
        int bn = SatSolver.makeLiteral(b, false);
        int cp = SatSolver.makeLiteral(c, true);
        int cn = SatSolver.makeLiteral(c, false);
        int dp = SatSolver.makeLiteral(d, true);
        int dn = SatSolver.makeLiteral(d, false);
        sat.addClause(an, bp);
        sat.addClause(cn, dp);
        sat.addClause(cp, dp);

        Assert.assertTrue(sat.propagateOneLiteral(ap));
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 2);
        Assert.assertEquals(sat.trail_.size(), 2);
        Assert.assertEquals(sat.trail_.getInt(0), 1);
        Assert.assertEquals(sat.trail_.getInt(1), 3);
        Assert.assertEquals(sat.trail_markers_.size(), 1);
        Assert.assertEquals(sat.trailMarker(), 1);
        Assert.assertEquals(sat.trail_markers_.getInt(0), 0);

        Assert.assertFalse(sat.propagateOneLiteral(an));
        Assert.assertTrue(sat.propagateOneLiteral(bp));
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.qhead_, 2);
        Assert.assertEquals(sat.trail_.size(), 2);
        Assert.assertEquals(sat.trail_.getInt(0), 1);
        Assert.assertEquals(sat.trail_.getInt(1), 3);
        Assert.assertEquals(sat.trail_markers_.size(), 2);
        Assert.assertEquals(sat.trailMarker(), 2);
        Assert.assertEquals(sat.trail_markers_.getInt(0), 0);
        Assert.assertEquals(sat.trail_markers_.getInt(1), 2);
        Assert.assertFalse(sat.propagateOneLiteral(bn));

        sat.uncheckedEnqueue(dn);
        Assert.assertFalse(sat.propagateOneLiteral(cn));

    }

    @Test(groups = "1s")
    public void testPropagate() {
        sat.addBoolOrArrayEqualTrue(a, b, c, d);
        sat.addBoolOrArrayEqualTrue(b, c, d);
        sat.addBoolOrArrayEqualTrue(a, c, d);
        Assert.assertTrue(sat.propagate());
        Assert.assertTrue(sat.propagateOneLiteral(SatSolver.makeLiteral(a, false)));
        Assert.assertTrue(sat.propagateOneLiteral(SatSolver.makeLiteral(b, false)));
        Assert.assertTrue(sat.propagateOneLiteral(SatSolver.makeLiteral(c, false)));
        Assert.assertFalse(sat.propagateOneLiteral(SatSolver.makeLiteral(d, false)));
        sat.cancelUntil(2);
        Assert.assertTrue(sat.propagateOneLiteral(SatSolver.makeLiteral(d, true)));
    }

    @Test(groups = "1s")
    public void testSign() {
        Assert.assertTrue(SatSolver.sign(3));
        Assert.assertTrue(SatSolver.sign(1));
        Assert.assertFalse(SatSolver.sign(0));
        Assert.assertFalse(SatSolver.sign(2));

        int ta = SatSolver.makeLiteral(a,true);
        Assert.assertEquals(ta, 1);
        Assert.assertTrue(SatSolver.sign(ta));

        int fa = SatSolver.makeLiteral(a,false);
        Assert.assertEquals(fa, 0);
        Assert.assertFalse(SatSolver.sign(fa));
    }

    @Test(groups = "1s")
    public void testNumvars() {
        Assert.assertEquals(sat.num_vars_, 4);
    }

    @Test(groups = "1s")
    public void testAddClause_() {
        sat.addClause(new int[]{a,b},new int[]{c,d});
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddTrue() {
        sat.addTrue(a);
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddFalse() {
        sat.addFalse(a);
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
    }

    @Test(groups = "1s")
    public void testAddBoolEq() {
        sat.addBoolEq(a, b);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolLe1() {
        sat.addBoolLe(a, b);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolLe2() {
        sat.addBoolLe(a, b);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddBoolLe3() {
        sat.addBoolLe(a, b);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(b, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kTrue);
    }

    @Test(groups = "1s")
    public void testAddBoolLt() {
        sat.addBoolLt(a, b);
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolNot() {
        sat.addBoolNot(a, b);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kTrue);
    }

    @Test(groups = "1s")
    public void testAddBoolOrArrayEqVar1() {
        sat.addBoolOrArrayEqVar(new int[]{a,b,c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(d, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kTrue);
    }

    @Test(groups = "1s")
    public void testAddBoolOrArrayEqVar2() {
        sat.addBoolOrArrayEqVar(new int[]{a,b,c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolAndArrayEqVar1() {
        sat.addBoolAndArrayEqVar(new int[]{a,b,c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(d, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolAndArrayEqVar2() {
        sat.addBoolAndArrayEqVar(new int[]{a,b,c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(d, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kTrue);
    }

    @Test(groups = "1s")
    public void testAddBoolAndArrayEqVar3() {
        sat.addBoolAndArrayEqVar(new int[]{a,b,c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(b, true));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolOrEqVar1() {
        sat.addBoolOrEqVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolOrEqVar2() {
        sat.addBoolOrEqVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolOrEqVar3() {
        sat.addBoolOrEqVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(b, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kTrue);
    }

    @Test(groups = "1s")
    public void testAddBoolAndEqVar() {
        sat.addBoolAndEqVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolXorEqVar() {
        sat.addBoolXorEqVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolIsEqVar() {
        sat.addBoolIsEqVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolIsNeqVar() {
        sat.addBoolIsNeqVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolIsLeVar() {
        sat.addBoolIsLeVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolIsLtVar() {
        sat.addBoolIsLtVar(a,b,c);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.uncheckedEnqueue(SatSolver.makeLiteral(c, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kFalse);
    }

    @Test(groups = "1s")
    public void testAddBoolOrArrayEqualTrue() {
        sat.addBoolOrArrayEqualTrue(a, b, c, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, true));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kFalse);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddBoolAndArrayEqualFalse() {
        sat.addBoolAndArrayEqualFalse(a, b, c, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddAtMostOne() {
        sat.addAtMostOne(a, b, c, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddAtMostNMinusOne() {
        sat.addAtMostNMinusOne(a, b, c, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddSumBoolArrayGreaterEqVar() {
        sat.addSumBoolArrayGreaterEqVar(new int[]{a, b, c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddMaxBoolArrayLessEqVar() {
        sat.addMaxBoolArrayLessEqVar(new int[]{a, b, c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }

    @Test(groups = "1s")
    public void testAddSumBoolArrayLessEqVar() {
        sat.addSumBoolArrayLessEqVar(new int[]{a, b, c}, d);
        sat.uncheckedEnqueue(SatSolver.makeLiteral(a, false));
        sat.propagate();
        Assert.assertEquals(sat.valueVar(a), SatSolver.Boolean.kTrue);
        Assert.assertEquals(sat.valueVar(b), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(c), SatSolver.Boolean.kUndefined);
        Assert.assertEquals(sat.valueVar(d), SatSolver.Boolean.kUndefined);
    }
}