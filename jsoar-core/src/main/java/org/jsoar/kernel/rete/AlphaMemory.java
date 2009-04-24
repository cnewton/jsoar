/*
 * (c) 2008  Dave Ray
 *
 * Created on Aug 22, 2008
 */
package org.jsoar.kernel.rete;

import org.jsoar.kernel.memory.WmeImpl;
import org.jsoar.kernel.symbols.SymbolImpl;
import org.jsoar.util.HashFunction;
import org.jsoar.util.HashTableItem;
import org.jsoar.util.ListHead;
import org.jsoar.util.HashTable;

/**
 * rete.cpp:179
 * 
 * @author ray
 */
public class AlphaMemory extends HashTableItem
{
    final int am_id;            /* id for hashing */
    final SymbolImpl id;                  /* constants tested by this alpha mem */
    final SymbolImpl attr;                /* (NIL if this alpha mem ignores that field) */
    final SymbolImpl value;
    final boolean acceptable;             /* does it test for acceptable pref? */
    
    final ListHead<RightMemory> right_mems = ListHead.newInstance(); // dll of right memory structures
    ReteNode beta_nodes;  /* list of attached beta nodes */
    ReteNode last_beta_node; /* tail of above dll */
    
    int reference_count = 1;  /* number of beta nodes using this mem */
    int retesave_amindex;
    
    public static HashFunction<AlphaMemory> HASH_FUNCTION = new HashFunction<AlphaMemory>()
    {

        @Override
        public int calculate(AlphaMemory item, int num_bits)
        {
            AlphaMemory am = (AlphaMemory) item;
            return alpha_hash_value(am.id, am.attr, am.value, num_bits);
        }
        
    };
    
    
    /**
     * @param am_id
     * @param id
     * @param attr
     * @param value
     * @param acceptable
     */
    public AlphaMemory(int am_id, SymbolImpl id, SymbolImpl attr, SymbolImpl value, boolean acceptable)
    {
        this.am_id = am_id;
        this.id = id;
        this.attr = attr;
        this.value = value;
        this.acceptable = acceptable;
    }

    /**
     * rete.cpp:1358:wme_matches_alpha_mem
     * 
     * @param w
     * @return
     */
    boolean wme_matches_alpha_mem(WmeImpl w)
    {
      return (this.id==null || this.id==w.id) &&
        (this.attr==null || this.attr==w.attr) &&
        (this.value==null || this.value==w.value) &&
        (this.acceptable==(w).acceptable);
    }

    /**
     * rete.cpp:1372:alpha_hash_value
     * 
     * @param i
     * @param a
     * @param v
     * @param num_bits
     * @return
     */
    static int alpha_hash_value(SymbolImpl i, SymbolImpl a, SymbolImpl v, int num_bits)
    {
      return ( ( ((i != null) ? (i).hash_id : 0) ^
        ((a != null) ? (a).hash_id : 0) ^
        ((v != null) ? (v).hash_id : 0) ) &
        HashTable.masks_for_n_low_order_bits[num_bits] );
    }

    /**
     * Decrements reference count, deallocates alpha memory if unused.
     * 
     * rete.cpp:1656:remove_ref_to_alpha_mem
     * 
     * @param rete
     */
    void remove_ref_to_alpha_mem(Rete rete)
    {
        this.reference_count--;
        if (this.reference_count != 0)
        {
            return;
        }
        /* --- remove from hash table, and deallocate the alpha_mem --- */
        HashTable<AlphaMemory> ht = rete.table_for_tests(id, attr, value, acceptable);
        ht.remove_from_hash_table(this);
        // if (am->id) symbol_remove_ref (thisAgent, am->id);
        // if (am->attr) symbol_remove_ref (thisAgent, am->attr);
        // if (am->value) symbol_remove_ref (thisAgent, am->value);
        while (!right_mems.isEmpty())
        {
            rete.remove_wme_from_alpha_mem(right_mems.first.item);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return am_id + ": <" + id + "," + attr + "," + value + ">";
    }

    
}