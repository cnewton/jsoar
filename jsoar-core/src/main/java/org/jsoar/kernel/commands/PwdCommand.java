/*
 * Copyright (c) 2008  Dave Ray <daveray@gmail.com>
 *
 * Created on Oct 30, 2008
 */
package org.jsoar.kernel.commands;

import java.util.Arrays;

import org.jsoar.kernel.SoarException;
import org.jsoar.util.commands.SoarCommand;
import org.jsoar.util.commands.SoarCommandContext;

/**
 * Implementation of the "pwd" command.
 * 
 * @author ray
 */
public class PwdCommand implements SoarCommand
{
    private final SourceCommand sourceCommand;
    
    /**
     * @param sourceCommand
     */
    public PwdCommand(SourceCommand sourceCommand)
    {
        this.sourceCommand = sourceCommand;
    }


    /* (non-Javadoc)
     * @see org.jsoar.util.commands.SoarCommand#execute(java.lang.String[])
     */
    @Override
    public String execute(SoarCommandContext commandContext, String[] args) throws SoarException
    {
        if(args.length != 1)
        {
            throw new SoarException("Expected 0 args, got " + Arrays.asList(args));
        }
        
        return sourceCommand.getWorkingDirectory().replace('\\', '/');
    }

}
