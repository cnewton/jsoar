/*
 * (c) 2008  Dave Ray
 *
 * Created on Aug 15, 2008
 */
package org.jsoar.kernel.memory;

import java.util.EnumMap;
import java.util.Iterator;

import org.jsoar.kernel.ImpasseType;
import org.jsoar.kernel.PredefinedSymbols;
import org.jsoar.kernel.symbols.IdentifierImpl;
import org.jsoar.kernel.symbols.StringSymbolImpl;
import org.jsoar.kernel.symbols.Symbol;
import org.jsoar.kernel.symbols.SymbolImpl;

import com.google.common.collect.Iterators;

/**
 * <em>This is an internal interface. Don't use it unless you know what you're doing.</em>
 * 
 * <p>Fields in a slot:
 * <ul>
 *    <li>next, prev:  used for a doubly-linked list of all slots for a certain
 *      identifier.
 *
 *    <li>id, attr:   identifier and attribute of the slot
 *
 *    <li>wmes:  header of a doubly-linked list of all wmes in the slot
 *
 *    <li>acceptable_preference_wmes:  header of doubly-linked list of all
 *      acceptable preference wmes in the slot.  (This is only used for
 *      context slots.)
 *
 *    <li>all_preferences:  header of a doubly-linked list of all preferences
 *      currently in the slot
 *
 *    <li>preferences[NUM_PREFERENCE_TYPES]: array of headers of doubly-linked
 *      lists, one for each possible type of preference.  These store
 *      all the preferences, sorted into lists according to their types.
 *      Within each list, the preferences are sorted according to their
 *      match goal, with the pref. supported by the highest goal at the
 *      head of the list.
 *
 *    <li>impasse_id:  points to the identifier of the attribute impasse object
 *      for this slot.  (NIL if the slot isn't impassed.)
 *
 *    <li>isa_context_slot:  TRUE iff this is a context slot
 *
 *    <li>impasse_type:  indicates the type of the impasse for this slot.  This
 *      is one of NONE_IMPASSE_TYPE, CONSTRAINT_FAILURE_IMPASSE_TYPE, etc.
 *
 *    <li>marked_for_possible_removal:  TRUE iff this slot is on the list of
 *      slots that might be deallocated at the end of the current top-level
 *      phases.
 *
 *    <li>changed:  indicates whether the preferences for this slot have changed.
 *      For non-context slots, this is either NIL or a pointer to the
 *      corresponding dl_cons in changed_slots (see decide.c); for context
 *      slots, it's just a zero/nonzero flag.
 *
 *    <li>acceptable_preference_changed:  for context slots only; this is zero
 *      if no acceptable or require preference in this slot has changed;
 *      if one has changed, it points to a dl_cons.
 * </ul>
 * 
 * <p>gdatastructs.h:288
 * 
 * @author ray
 */
public class Slot
{
    public Slot next, prev;// dll of slots for id
    
    public final IdentifierImpl id; 
    public final SymbolImpl attr;

    private WmeImpl wmes; // dll of wmes in the slot
    private WmeImpl acceptable_preference_wmes;  // dll of acceptable pref. wmes
    
    private Preference all_preferences; // dll of all pref's in the slot
    
    private EnumMap<PreferenceType, Preference> preferencesByType;

    public IdentifierImpl impasse_id = null;               // null if slot is not impassed
    public final boolean isa_context_slot;            
    public ImpasseType impasse_type = ImpasseType.NONE;
    public boolean marked_for_possible_removal = false;
    
    /**
     * for non-context slots: points to the corresponding
     * dl_cons in changed_slots;  for context slots: just
     * zero/nonzero flag indicating slot changed
     * 
     * TODO Sub-class instead of using this for two things
     */
    public Object changed;
    
    /**
     * for context slots: either zero, or points to dl_cons if the slot has
     * changed + or ! pref's
     * 
     * TODO Sub-class instead of using this for two things
     */
    public Object acceptable_preference_changed;

    /**
     * Find or create a new slot
     * 
     * <p>tempmem.cpp:64:make_slot
     * 
     * @param id the id of the slot
     * @param attr the attribute of the slot
     * @param operator_symbol the operator symbol from {@link PredefinedSymbols}
     * @return the slot. A new one is constructed if a slot for the given id/attr doesn't already
     *  exist.
     */
    public static Slot make_slot(IdentifierImpl id, SymbolImpl attr, StringSymbolImpl operator_symbol)
    {
        // Search for a slot first.  If it exists for the given symbol, then just return it
        final Slot s = find_slot(id, attr);
        if(s != null)
        {
            return s;
        }
        
        return new Slot(id, attr, operator_symbol);
    }
    
    /**
     * Construct a new slot
     * 
     * <p>tempmem.cpp:64:make_slot
     * 
     * @param id the slot identifier
     * @param attr the slot attribute
     * @param operator_symbol the operator symbol from {@link PredefinedSymbols}. May be null
     *          if you know that {@code attr} is not "operator". TODO get rid of this param
     */
    private Slot(IdentifierImpl id, SymbolImpl attr, StringSymbolImpl operator_symbol)
    {
        id.addSlot(this);

        /*
         * Context slots are goals and operators; operator slots get created
         * with a goal (see create_new_context).
         */
        if ((id.isGoal()) && (attr == operator_symbol))
        {
            this.isa_context_slot = true;
        }
        else
        {
            this.isa_context_slot = false;
        }

        // s->changed = NIL;
        // s->acceptable_preference_changed = NIL;
        this.id = id;
        this.attr = attr;
    }

    /**
     * Find_slot() looks for an existing slot for a given id/attr pair, and
     * returns it if found.  If no such slot exists, it returns NIL.
     * 
     * <p>tempmem.cpp:55:find_slot
     * 
     * @param id the slot identifier
     * @param attr the slot attribute
     * @return the slot, or {@code null} if not found
     */
    public static Slot find_slot(IdentifierImpl id, Symbol attr)
    {
        if (id == null)
        {
            return null; // fixes bug #135 kjh
        } 
        for (Slot s = id.slots; s != null; s = s.next)
        {
            if (s.attr == attr)
            {
                return s;
            }
        }
        return null;
    }
    
    /**
     * Returns the head of the list of preferences with the given type. When
     * iterating over this list, you should use {@link Preference#next}.
     * 
     * @param type The type of preference
     * @return The head of the list
     */
    public Preference getPreferencesByType(PreferenceType type)
    {
        if(preferencesByType == null)
        {
            return null;
        }
        return preferencesByType.get(type);
    }
    
    
    /**
     * @return the head of the list of WMEs in this slot
     */
    public WmeImpl getWmes()
    {
        return this.wmes;
    }
    
    /**
     * Add a WME to the head of the list of WMEs in this slot
     * 
     * @param w the WME to add
     */
    public void addWme(WmeImpl w)
    {
        this.wmes = w.addToList(this.wmes);
    }
    
    /**
     * Remove a WME from the list of WMEs in this slot.
     * 
     * @param w the WME to remove
     */
    public void removeWme(WmeImpl w)
    {
        this.wmes = w.removeFromList(this.wmes);
    }
    
    /**
     * Remove all WMEs from this slot 
     */
    public void removeAllWmes()
    {
        this.wmes = null;
    }
    
    /**
     * Returns an iterator over all the WMEs in this slot. Note that this should 
     * not be used for performance critical code.
     * 
     * @return An iterator over the wmes in this slot
     */
    public Iterator<Wme> getWmeIterator()
    {
        return Iterators.concat(new WmeIterator(this.acceptable_preference_wmes), new WmeIterator(this.wmes));
    }
    
    public WmeImpl getAcceptablePreferenceWmes()
    {
        return acceptable_preference_wmes;
    }
    
    public void addAcceptablePreferenceWme(WmeImpl wme)
    {
        this.acceptable_preference_wmes = wme.addToList(this.acceptable_preference_wmes);
    }
    
    public void removeAcceptablePreferenceWme(WmeImpl w)
    {
        this.acceptable_preference_wmes = w.removeFromList(this.acceptable_preference_wmes);
    }
    
    /**
     * @return Head of list of all preferences. Iterate with {@link Preference#nextOfSlot}.
     */
    public Preference getAllPreferences()
    {
        return all_preferences;
    }
    
    public void addPreference(Preference pref)
    {
        pref.slot = this;
        
        pref.nextOfSlot = all_preferences;
        pref.previousOfSlot = null;
        if(all_preferences != null)
        {
            all_preferences.previousOfSlot = pref;
        }
        all_preferences = pref;
        
        addPreferenceToCorrectTypeList(pref);
    }
    
    public void removePreference(Preference pref)
    {
        pref.slot = null;
        
        removePreferenceByType(pref);
        
        if(pref.nextOfSlot != null)
        {
            pref.nextOfSlot.previousOfSlot = pref.previousOfSlot;
        }
        if(pref.previousOfSlot != null)
        {
            pref.previousOfSlot.nextOfSlot = pref.nextOfSlot;
        }
        else
        {
            all_preferences = pref.nextOfSlot;
        }
        pref.nextOfSlot = null;
        pref.previousOfSlot = null;

    }
    
    /**
     * Adds a new preference to the correct type list, in the correct position.
     * 
     * <p>This method is extracted from prefmem.cpp:add_preference_to_tm
     * 
     * @param pref
     */
    private void addPreferenceToCorrectTypeList(Preference pref)
    {
        // add preference to the list (in the right place, according to match
        // goal level of the instantiations) for the slot
        Preference s_prefs = this.getPreferencesByType(pref.type);
        if (s_prefs == null)
        {
            // this is the only pref. of its type, just put it at the head
            this.addPreferenceByType(pref, null);
        }
        else if (s_prefs.inst.match_goal_level >= pref.inst.match_goal_level)
        {
            // it belongs at the head of the list, so put it there
            this.addPreferenceByType(pref, null);
        }
        else
        {
            // scan through the pref. list, find the one to insert after
            Preference it = s_prefs;
            for (; it.next != null; it = it.next)
            {
                if (it.inst.match_goal_level >= pref.inst.match_goal_level)
                {
                    break;
                }
            }

            // insert pref after it
            this.addPreferenceByType(pref, it);
        }        
    }
    
    /**
     * Add a preference to this slot by type. This adds the preference to the
     * appropriate list as returned by {@link #getPreferencesByType(PreferenceType)}.
     * 
     * @param pref The preference to add
     * @param after If non-null, the preference is added <b>after</b> this one.
     */
    private void addPreferenceByType(Preference pref, Preference after)
    {
        if(preferencesByType == null)
        {
            preferencesByType = new EnumMap<PreferenceType, Preference>(PreferenceType.class);
        }
        
        if(after == null)
        {
            final Preference head = preferencesByType.get(pref.type);
            pref.next = head;
            pref.previous = null;
            if(head != null)
            {
                head.previous = pref;
            }
            preferencesByType.put(pref.type, pref);
        }
        else
        {
            assert preferencesByType.get(pref.type) != null;
            
            pref.next = after.next;
            pref.previous = after;
            after.next = pref;
            if(pref.next != null)
            {
                pref.next.previous = pref;
            }
        }
    }
    
    private void removePreferenceByType(Preference pref)
    {
        if(preferencesByType == null)
        {
            return;
        }
        if(pref.next != null)
        {
            pref.next.previous = pref.previous;
        }
        if(pref.previous != null)
        {
            pref.previous.next = pref.next;
        }
        else
        {
            if(pref.next == null)
            {
                preferencesByType.remove(pref.type);
            }
            else
            {
                preferencesByType.put(pref.type, pref.next);
            }
        }
        pref.next = null;
        pref.previous = null;
        
    }

}
