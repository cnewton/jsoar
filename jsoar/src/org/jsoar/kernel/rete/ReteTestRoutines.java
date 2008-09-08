/*
 * Copyright (c) 2008  Dave Ray <daveray@gmail.com>
 *
 * Created on Sep 6, 2008
 */
package org.jsoar.kernel.rete;

import org.jsoar.kernel.memory.Wme;
import org.jsoar.kernel.symbols.Symbol;

/**
 * Implementations of XXX_rete_test_routine functions in rete.cpp. Separated out
 * for sanity.
 * 
 * <p>TODO: These should probably be polymorphic methods on ReteTest
 * 
 * @author ray
 */
public class ReteTestRoutines
{
    /**
     * rete.cpp:4485:disjunction_rete_test_routine
     */
    private static final ReteTestRoutine disjunction_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            Symbol sym = VarLocation.field_from_wme(w, rt.right_field_num);
            return rt.disjunction_list.contains(sym);
        }};
        
    /**
     * rete.cpp:4477:id_is_goal_rete_test_routine
     */
    private static final ReteTestRoutine id_is_goal_rete_test_routine =  new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return w.id.isa_goal;
        }};

    /**
     * rete.cpp:4481:id_is_impasse_rete_test_routine
     */
    private static final ReteTestRoutine id_is_impasse_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return w.id.isa_impasse;
        }};

    /**
     * rete.cpp:4495:constant_equal_rete_test_routine
     */
    private static final ReteTestRoutine constant_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return equalTest(rt, w, rt.constant_referent);
        }};

    /**
     * rete.cpp:4503:constant_not_equal_rete_test_routine
     */
    private static final ReteTestRoutine constant_not_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return !equalTest(rt, w, rt.constant_referent);
        }};


    /**
     * rete.cpp:4546:constant_same_type_rete_test_routine
     */
    private static final ReteTestRoutine constant_same_type_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return sameTypeTest(rt, w, rt.constant_referent);
        }};

    /**
     * rete.cpp:4512:constant_less_rete_test_routine
     */
    private static final ReteTestRoutine constant_less_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return lessTest(rt, w, rt.constant_referent);
        }};
        
    /**
     * rete.cpp:4520:constant_greater_rete_test_routine
     */
    private static final ReteTestRoutine constant_greater_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return greaterTest(rt, w, rt.constant_referent);
        }};

    /**
     * rete.cpp:4528:constant_less_or_equal_rete_test_routine
     */
    private static final ReteTestRoutine constant_less_or_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return lessEqualTest(rt, w, rt.constant_referent);
        }};

    /**
     * rete.cpp:4537:constant_greater_or_equal_rete_test_routine
     */
    private static final ReteTestRoutine constant_greater_or_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return greaterEqualTest(rt, w, rt.constant_referent);
        }};

    /**
     * rete.cpp:4555:variable_equal_rete_test_routine
     */
    private static final ReteTestRoutine variable_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return equalTest(rt, w, getVariableSymbol(rt, left, w));
        }};

    /**
     * rete.cpp:4474:variable_not_equal_rete_test_routine
     */
    private static final ReteTestRoutine variable_not_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return !equalTest(rt, w, getVariableSymbol(rt, left, w));
        }};

    /**
     * rete.cpp:4594:variable_less_rete_test_routine
     */
    private static final ReteTestRoutine variable_less_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return lessTest(rt, w, getVariableSymbol(rt, left, w));
        }};

    /**
     * rete.cpp:4613:variable_greater_rete_test_routine
     */
    private static final ReteTestRoutine variable_greater_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return greaterTest(rt, w, getVariableSymbol(rt, left, w));
        }};

    /**
     * rete.cpp:4632:variable_less_or_equal_rete_test_routine
     */
    private static final ReteTestRoutine variable_less_or_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return lessEqualTest(rt, w, getVariableSymbol(rt, left, w));
        }};

    /**
     * rete.cpp:4652:variable_greater_or_equal_rete_test_routine
     */
    private static final ReteTestRoutine variable_greater_or_equal_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return greaterEqualTest(rt, w, getVariableSymbol(rt, left, w));
        }};

    /**
     * rete.cpp:4672:variable_same_type_rete_test_routine
     */
    private static final ReteTestRoutine variable_same_type_rete_test_routine = new ReteTestRoutine() {

        @Override
        public boolean execute(Rete rete, ReteTest rt, LeftToken left, Wme w)
        {
            return sameTypeTest(rt, w, getVariableSymbol(rt, left, w));
        }};
    

    private static boolean equalTest(ReteTest rt, Wme w, Symbol s2)
    {
        Symbol s1 = VarLocation.field_from_wme(w, rt.right_field_num);

        return s1 == s2;
    }
    private static boolean lessTest(ReteTest rt, Wme w, Symbol s2)
    {
        Symbol s1 = VarLocation.field_from_wme(w, rt.right_field_num);

        return s1.numericLess(s2);
    }
    private static boolean lessEqualTest(ReteTest rt, Wme w, Symbol s2)
    {
        Symbol s1 = VarLocation.field_from_wme(w, rt.right_field_num);

        return s1.numericLessOrEqual(s2);
    }
    private static boolean greaterTest(ReteTest rt, Wme w, Symbol s2)
    {
        Symbol s1 = VarLocation.field_from_wme(w, rt.right_field_num);

        return s1.numericGreater(s2);
    }
    private static boolean greaterEqualTest(ReteTest rt, Wme w, Symbol s2)
    {
        Symbol s1 = VarLocation.field_from_wme(w, rt.right_field_num);

        return s1.numericGreaterOrEqual(s2);
    }
    private static boolean sameTypeTest(ReteTest rt, Wme w, Symbol s2)
    {
        Symbol s1 = VarLocation.field_from_wme(w, rt.right_field_num);

        return s1.isSameTypeAs(s2);
    }

    /**
     * Retrieves the symbol for a variable referent in a rete test.
     * 
     * <p>Method extracted from code duplicated in all variable_XXX_rete_test_routine
     * functions in rete.cpp
     * 
     * @param rt The test
     * @param left The token
     * @param w The wme
     * @return The symbol for the test's variable_referent
     */
    private static Symbol getVariableSymbol(ReteTest rt, Token left, Wme w)
    {
        if (rt.variable_referent.levels_up!=0) {
            int i = rt.variable_referent.levels_up - 1;
            while (i!=0) {
              left = left.parent;
              i--;
            }
            w = left.w;
          }
          return VarLocation.field_from_wme (w, rt.variable_referent.field_num);

    }
    
    /**
     * The rete test routine table. Package private so it can be accessed by {@link Rete}
     * 
     * rete.cpp:4417:rete_test_routines
     */
    /*package*/ static final ReteTestRoutine table[] = new ReteTestRoutine[256];
    static
    {
        table[ReteTest.DISJUNCTION_RETE_TEST] = disjunction_rete_test_routine;
        table[ReteTest.ID_IS_GOAL_RETE_TEST] = id_is_goal_rete_test_routine;            
        table[ReteTest.ID_IS_IMPASSE_RETE_TEST]= id_is_impasse_rete_test_routine;            
        table[ReteTest.CONSTANT_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_EQUAL_RETE_TEST] = constant_equal_rete_test_routine;            
        table[ReteTest.CONSTANT_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_NOT_EQUAL_RETE_TEST] = constant_not_equal_rete_test_routine;           
        table[ReteTest.CONSTANT_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_LESS_RETE_TEST] = constant_less_rete_test_routine;
        table[ReteTest.CONSTANT_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_GREATER_RETE_TEST] = constant_greater_rete_test_routine;
        table[ReteTest.CONSTANT_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_LESS_OR_EQUAL_RETE_TEST] = constant_less_or_equal_rete_test_routine;
        table[ReteTest.CONSTANT_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_GREATER_OR_EQUAL_RETE_TEST] = constant_greater_or_equal_rete_test_routine;
        table[ReteTest.CONSTANT_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_SAME_TYPE_RETE_TEST] = constant_same_type_rete_test_routine;
        table[ReteTest.VARIABLE_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_EQUAL_RETE_TEST] = variable_equal_rete_test_routine;
        table[ReteTest.VARIABLE_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_NOT_EQUAL_RETE_TEST] = variable_not_equal_rete_test_routine;
        table[ReteTest.VARIABLE_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_LESS_RETE_TEST] = variable_less_rete_test_routine;
        table[ReteTest.VARIABLE_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_GREATER_RETE_TEST] = variable_greater_rete_test_routine;
        table[ReteTest.VARIABLE_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_LESS_OR_EQUAL_RETE_TEST] = variable_less_or_equal_rete_test_routine;
        table[ReteTest.VARIABLE_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_GREATER_OR_EQUAL_RETE_TEST] = variable_greater_or_equal_rete_test_routine;
        table[ReteTest.VARIABLE_RELATIONAL_RETE_TEST + ReteTest.RELATIONAL_SAME_TYPE_RETE_TEST] = variable_same_type_rete_test_routine;
    }

}