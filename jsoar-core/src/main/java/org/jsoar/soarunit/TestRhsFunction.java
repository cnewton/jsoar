/*
 * Copyright (c) 2010 Dave Ray <daveray@gmail.com>
 *
 * Created on Jul 22, 2010
 */
package org.jsoar.soarunit;

import java.util.List;

import org.jsoar.kernel.Agent;
import org.jsoar.kernel.rhs.functions.RhsFunctionContext;
import org.jsoar.kernel.rhs.functions.RhsFunctionException;
import org.jsoar.kernel.rhs.functions.StandaloneRhsFunctionHandler;
import org.jsoar.kernel.symbols.Symbol;

/**
 * @author ray
 */
public class TestRhsFunction extends StandaloneRhsFunctionHandler
{
    private final Agent agent;
    private boolean called = false;
    private List<Symbol> arguments;
    
    /**
     * @param name
     */
    public TestRhsFunction(Agent agent, String name)
    {
        super(name);
        
        this.agent = agent;
    }

    /**
     * @return the called
     */
    public boolean isCalled()
    {
        return called;
    }

    /**
     * @return the arguments
     */
    public List<Symbol> getArguments()
    {
        return arguments;
    }

    /* (non-Javadoc)
     * @see org.jsoar.kernel.rhs.functions.RhsFunctionHandler#execute(org.jsoar.kernel.rhs.functions.RhsFunctionContext, java.util.List)
     */
    @Override
    public Symbol execute(RhsFunctionContext context, List<Symbol> arguments)
            throws RhsFunctionException
    {
        called = true;
        this.arguments = arguments;
        return agent.getRhsFunctions().getHandler("halt").execute(context, arguments);
    }

}
