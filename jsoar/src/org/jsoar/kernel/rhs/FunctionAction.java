/*
 * (c) 2008  Dave Ray
 *
 * Created on Aug 16, 2008
 */
package org.jsoar.kernel.rhs;

import java.util.List;

import org.jsoar.kernel.symbols.Variable;

/**
 * @author ray
 */
public class FunctionAction extends Action
{
    public RhsFunctionCall call;
    
    
    /**
     * @param call
     */
    public FunctionAction(RhsFunctionCall call)
    {
        this.call = call;
    }

    /**
     * @return the call
     */
    public RhsFunctionCall getCall()
    {
        return call;
    }

    /* (non-Javadoc)
     * @see org.jsoar.kernel.Action#asFunctionAction()
     */
    @Override
    public FunctionAction asFunctionAction()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see org.jsoar.kernel.Action#addAllVariables(int, java.util.List)
     */
    @Override
    public void addAllVariables(int tc_number, List<Variable> var_list)
    {
        call.addAllVariables(tc_number, var_list);
    }
    
    
}