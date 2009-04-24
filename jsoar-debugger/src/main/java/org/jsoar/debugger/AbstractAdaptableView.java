/*
 * Copyright (c) 2008  Dave Ray <daveray@gmail.com>
 *
 * Created on Oct 23, 2008
 */
package org.jsoar.debugger;

import org.flexdock.view.View;
import org.jsoar.util.adaptables.Adaptable;
import org.jsoar.util.adaptables.Adaptables;

/**
 * @author ray
 */
public abstract class AbstractAdaptableView extends View implements Adaptable
{
    private static final long serialVersionUID = 8049528094231200441L;

    public AbstractAdaptableView(String persistentId, String title)
    {
        super(persistentId, title);
    }
    
    /* (non-Javadoc)
     * @see org.jsoar.util.adaptables.AbstractAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(Class<?> klass)
    {
        return Adaptables.adapt(this, klass, false);
    }
}