/*
 * Copyright (c) 2008  Dave Ray <daveray@gmail.com>
 *
 * Created on Aug 24, 2008
 */
package org.jsoar.kernel;

import org.jsoar.kernel.lhs.Condition;
import org.jsoar.kernel.rete.Token;
import org.jsoar.kernel.symbols.Symbol;
import org.jsoar.util.AsListItem;
import org.jsoar.util.ListHead;

/**
 * instantiations.h:84
 * 
 * @author ray
 */
public class Instantiation
{
    public Production prod; 
    public AsListItem<Instantiation> inProdList = new AsListItem<Instantiation>(this); // dll of inst's from same prod
    public Token rete_token; // used by rete for retractions
    public Wme rete_wme;     // used by rete for retractions
    public Condition top_of_instantiated_conditions;
    public Condition bottom_of_instantiated_conditions;

    // TODO not_struct *nots;
    public ListHead<Preference>  preferences_generated;    // header for dll of prefs
    public Symbol match_goal;                   // symbol, or NIL if none
    public int /*goal_stack_level*/ match_goal_level;    // level, or ATTRIBUTE_IMPASSE_LEVEL
    public boolean okay_to_variablize;
    public boolean in_ms;  // TRUE iff this inst. is still in the match set
    public int /*tc_number*/ backtrace_number;
    public boolean GDS_evaluated_already;

}