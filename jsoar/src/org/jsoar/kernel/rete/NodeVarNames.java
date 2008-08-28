/*
 * Copyright (c) 2008  Dave Ray <daveray@gmail.com>
 *
 * Created on Aug 27, 2008
 */
package org.jsoar.kernel.rete;

import java.util.LinkedList;

import org.jsoar.kernel.lhs.Condition;
import org.jsoar.kernel.lhs.ConjunctiveNegationCondition;
import org.jsoar.kernel.lhs.NegativeCondition;
import org.jsoar.kernel.lhs.PositiveCondition;
import org.jsoar.kernel.lhs.ThreeFieldCondition;
import org.jsoar.kernel.symbols.Variable;

/**
 * rete.cpp:2508
 * 
 * @author ray
 */
public class NodeVarNames
{
    public static class ThreeFieldVarNames
    {
        Object id_varnames;
        Object attr_varnames;
        Object value_varnames;
    }
    
    NodeVarNames parent;
    //union varname_data_union {
    ThreeFieldVarNames fields; // TODO: Only allocate for non-CN_BNODE
    NodeVarNames bottom_of_subconditions;
    //} data;
    
    public NodeVarNames(NodeVarNames parent)
    {
        this.parent = parent;
    }
    
    /**
     * TODO: This method is probably unnecessary
     * 
     * rete.cpp:2553:deallocate_node_varnames
     * 
     * @param node
     * @param cutoff
     * @param nvn
     */
    static void deallocate_node_varnames(ReteNode node, ReteNode cutoff, NodeVarNames nvn)
    {

        while (node != cutoff)
        {
            if (node.node_type == ReteNode.CN_BNODE)
            {
                deallocate_node_varnames(node.b_cn.partner.parent, node.parent, nvn.bottom_of_subconditions);
            }
            else
            {
                VarNames.deallocate_varnames(nvn.fields.id_varnames);
                VarNames.deallocate_varnames(nvn.fields.attr_varnames);
                VarNames.deallocate_varnames(nvn.fields.value_varnames);
                
                // TODO: Set fields to null here?
            }
            node = node.real_parent_node();
            //NodeVarNames temp = nvn;
            nvn = nvn.parent;
            //free_with_pool (&thisAgent->node_varnames_pool, temp);
        }
    }
    
    /**
     * rete.cpp:2611:make_nvn_for_posneg_cond
     * 
     * @param cond
     * @param parent_nvn
     * @return
     */
    static NodeVarNames make_nvn_for_posneg_cond(ThreeFieldCondition cond, NodeVarNames parent_nvn)
    {
        NodeVarNames New = new NodeVarNames(parent_nvn);
        LinkedList<Variable> vars_bound = new LinkedList<Variable>();

        /* --- fill in varnames for id test --- */
        New.fields.id_varnames = VarNames.add_unbound_varnames_in_test(cond.id_test, null);

        /* --- add sparse bindings for id, then get attr field varnames --- */
        Rete.bind_variables_in_test(cond.id_test, 0, 0, false, vars_bound);
        New.fields.attr_varnames = VarNames.add_unbound_varnames_in_test(cond.attr_test, null);

        /* --- add sparse bindings for attr, then get value field varnames --- */
        Rete.bind_variables_in_test(cond.attr_test, 0, 0, false, vars_bound);
        New.fields.value_varnames = VarNames.add_unbound_varnames_in_test(cond.value_test, null);

        /* --- Pop the variable bindings for these conditions --- */
        Rete.pop_bindings_and_deallocate_list_of_variables(vars_bound);

        return New;
    }  

    
    /**
     * rete.cpp:2642:get_nvn_for_condition_list
     * 
     * @param cond_list
     * @param parent_nvn
     * @return
     */
    static NodeVarNames get_nvn_for_condition_list(Condition cond_list, NodeVarNames parent_nvn)
    {
        NodeVarNames New = null;
        LinkedList<Variable> vars = new LinkedList<Variable>();

        for (Condition cond = cond_list; cond != null; cond = cond.next)
        {
            PositiveCondition pc = cond.asPositiveCondition();
            if (pc != null)
            {
                New = make_nvn_for_posneg_cond(pc, parent_nvn);

                /* --- Add sparse variable bindings for this condition --- */
                Rete.bind_variables_in_test(pc.id_test, 0, 0, false, vars);
                Rete.bind_variables_in_test(pc.attr_test, 0, 0, false, vars);
                Rete.bind_variables_in_test(pc.value_test, 0, 0, false, vars);

            }
            NegativeCondition nc = cond.asNegativeCondition();
            if (nc != null)
            {
                New = make_nvn_for_posneg_cond(nc, parent_nvn);

            }
            ConjunctiveNegationCondition ncc = cond.asConjunctiveNegationCondition();
            if (ncc != null)
            {
                New = new NodeVarNames(parent_nvn);
                New.bottom_of_subconditions = get_nvn_for_condition_list(ncc.top, parent_nvn);
            }

            parent_nvn = New;
        }

        /* --- Pop the variable bindings for these conditions --- */
        Rete.pop_bindings_and_deallocate_list_of_variables(vars);

        return parent_nvn;
    }

}