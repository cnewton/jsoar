/*
 * Copyright (c) 2010 Dave Ray <daveray@gmail.com>
 *
 * Created on Jul 1, 2010
 */
package org.jsoar.kernel.smem;

import java.util.HashSet;
import java.util.Set;

import org.jsoar.util.properties.DefaultPropertyProvider;
import org.jsoar.util.properties.PropertyKey;
import org.jsoar.util.properties.PropertyManager;
import org.jsoar.util.properties.PropertyProvider;

/**
 * 
 * <p>semantic_memory.h:92:smem_stat_container
 * @author ray
 */
class DefaultSemanticMemoryStats implements SemanticMemoryStatistics
{
    private static final String PREFIX = "smem.stats.";
    
    /**
     * Retrieve a property key for an SMEM property. Appropriately adds necessary
     * prefixes to the name to find the right key.
     * 
     * @param props the property manager
     * @param name the name of the property.
     * @return the key, or {@code null} if not found.
     */
    public static PropertyKey<?> getProperty(PropertyManager props, String name)
    {
        return props.getKey(PREFIX + name);
    }
    
    private static <T> PropertyKey.Builder<T> key(String name, Class<T> type)
    {
        return PropertyKey.builder(PREFIX + name, type);
    }

    static final PropertyKey<Long> MEM_USAGE = key("mem-usage", Long.class).defaultValue(0L).build();
    final DefaultPropertyProvider<Long> mem_usage = new DefaultPropertyProvider<Long>(MEM_USAGE);
    
    static final PropertyKey<Long> MEM_HIGH = key("mem-high", Long.class).defaultValue(0L).build();
    final DefaultPropertyProvider<Long> mem_high = new DefaultPropertyProvider<Long>(MEM_HIGH);
    
    static final PropertyKey<Long> RETRIEVES = key("retrieves", Long.class).defaultValue(0L).build();
    final DefaultPropertyProvider<Long> retrieves = new DefaultPropertyProvider<Long>(RETRIEVES);
    
    static final PropertyKey<Long> QUERIES = key("queries", Long.class).defaultValue(0L).build();
    final DefaultPropertyProvider<Long> queries = new DefaultPropertyProvider<Long>(QUERIES);
    
    static final PropertyKey<Long> STORES = key("stores", Long.class).defaultValue(0L).build();
    final DefaultPropertyProvider<Long> stores = new DefaultPropertyProvider<Long>(STORES);
    
    static final PropertyKey<Long> NODES = key("nodes", Long.class).defaultValue(0L).build();
    final DefaultPropertyProvider<Long> nodes = new DefaultPropertyProvider<Long>(NODES);
    
    static final PropertyKey<Long> EDGES = key("edges", Long.class).defaultValue(0L).build();
    final DefaultPropertyProvider<Long> edges = new DefaultPropertyProvider<Long>(EDGES);
    
    private final PropertyManager properties;
    private final Set<PropertyKey<?>> keys = new HashSet<PropertyKey<?>>();
    
    public DefaultSemanticMemoryStats(PropertyManager properties)
    {
        this.properties = properties;
        add(MEM_USAGE, mem_usage);
        add(MEM_HIGH, mem_high);
        add(RETRIEVES, retrieves);
        add(QUERIES, queries);
        add(STORES, stores);
        add(NODES, nodes);
        add(EDGES, edges);
    }

    private <T> void add(PropertyKey<T> key, PropertyProvider<T> value)
    {
        this.properties.setProvider(key, value);
    }

    @SuppressWarnings("unchecked")
    public void reset()
    {
        for(PropertyKey key : keys)
        {
            properties.set(key, key.getDefaultValue());
        }
    }

    @Override
    public long getRetrieves()
    {
        return retrieves.get();
    }

    @Override
    public long getQueries()
    {
        return queries.get();
    }

    @Override
    public long getStores()
    {
        return stores.get();
    }
}
