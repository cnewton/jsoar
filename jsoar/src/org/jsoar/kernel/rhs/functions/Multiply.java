package org.jsoar.kernel.rhs.functions;

import java.util.List;

import org.jsoar.kernel.symbols.IntConstant;
import org.jsoar.kernel.symbols.Symbol;
import org.jsoar.kernel.symbols.SymbolFactory;

/**
 * Takes any number of int_constant or float_constant arguments, and
   returns their product.
 *  
 * rhsfun_math.cpp:82:times_rhs_function_code
 */
public final class Multiply extends AbstractRhsFunctionHandler
{
    /**
     * @param name
     */
    public Multiply()
    {
        super("*");
    }

    @Override
    public Symbol execute(SymbolFactory syms, List<Symbol> arguments) throws RhsFunctionException
    {
        RhsFunctionTools.checkAllArgumentsAreNumeric(getName(), arguments);

        int i = 1;
        double f = 1.0;
        boolean float_found = false;
        for(Symbol arg : arguments)
        {
            IntConstant ic = arg.asIntConstant();
            if(ic != null)
            {
                if(float_found)
                {
                    f *= ic.value;
                }
                else
                {
                    i *= ic.value;
                }
            }
            else
            {
                if(float_found)
                {
                    f *= arg.asFloatConstant().value;
                }
                else
                {
                    float_found = true;
                    f = i * arg.asFloatConstant().value;
                }
            }
        }
        
        return float_found ? syms.make_float_constant(f) : syms.make_int_constant(i);
    }
}