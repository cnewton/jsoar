/*
 * (c) 2008  Dave Ray
 *
 * Created on Aug 15, 2008
 */
package org.jsoar.kernel.rete;

import org.jsoar.kernel.memory.WmeImpl;
import org.jsoar.kernel.symbols.IdentifierImpl;
import org.jsoar.util.AsListItem;

/**
 * explain.h:81
 * 
 * @author ray
 */
public class MatchSetChange
{
    public final  AsListItem<MatchSetChange> next_prev = new AsListItem<MatchSetChange>(this); // dll for all p nodes
    public final AsListItem<MatchSetChange> of_node = new AsListItem<MatchSetChange>(this); // dll for just this p node
    
    public ReteNode p_node; // for retractions, this can be null if the p node has been excised
    public final Token tok; // for assertions only
    public final WmeImpl w; // for assertions only
    
    
    public final Instantiation inst;   // for retractions only

    public IdentifierImpl goal;
    public int level;              // Level of the match of the assertion or retraction
    public final AsListItem<MatchSetChange> in_level = new AsListItem<MatchSetChange>(this); // dll for goal level

    public static MatchSetChange createAssertion(ReteNode p_node, Token tok, WmeImpl w)
    {
        return new MatchSetChange(p_node, tok, w);
    }
    
    /**
     * Extracted from rete.cpp:5953
     * 
     * @param p_node
     * @param inst
     * @return
     */
    public static MatchSetChange createRetraction(ReteNode p_node, Instantiation inst)
    {
        return new MatchSetChange(p_node, inst);
    }
    
    public static MatchSetChange createRefracted(ReteNode p_node, Instantiation inst)
    {
        return new MatchSetChange(p_node, inst);
    }
    
    private MatchSetChange(ReteNode p_node, Token tok, WmeImpl w)
    {
        assert p_node.node_type == ReteNodeType.P_BNODE && p_node.b_p != null;
        
        this.p_node = p_node;
        this.tok = tok;
        this.w = w;
        this.inst = null;
    }
    
    private MatchSetChange(ReteNode p_node, Instantiation inst)
    {
        assert p_node.node_type == ReteNodeType.P_BNODE && p_node.b_p != null;
        assert inst != null;
        
        this.p_node = p_node;
        this.inst = inst;
        
        this.w = null;
        this.tok = null;
    }
    
    /**
     * rete.cpp:1011:find_goal_for_match_set_change_assertion
     * @param msc
     * @return
     */
    public IdentifierImpl find_goal_for_match_set_change_assertion(Token dummy_top_token) {

//      #ifdef DEBUG_WATERFALL
//        print_with_symbols(thisAgent, "\nMatch goal for assertion: %y", msc->p_node->b.p.prod->name); 
//      #endif

        WmeImpl lowest_goal_wme = null;
        //int lowest_level_so_far = -1;

        if (this.w != null) {
            if (this.w.id.isa_goal) {
              lowest_goal_wme = this.w;
              //lowest_level_so_far = this.w.id.level;
            }
        }

        for (Token tok=this.tok; tok!=dummy_top_token; tok=tok.parent) {
          if (tok.w != null) {
            /* print_wme(tok->w); */
            if (tok.w.id.isa_goal) {

              if (lowest_goal_wme == null)
                lowest_goal_wme = tok.w;
              
              else {
                if (tok.w.id.level > lowest_goal_wme.id.level)
                  lowest_goal_wme = tok.w;
              }
            }
             
          }
        } 

        if (lowest_goal_wme != null) {
//      #ifdef DEBUG_WATERFALL
//          print_with_symbols(thisAgent, " is [%y]", lowest_goal_wme->id);
//      #endif
             return lowest_goal_wme.id;
        }
            throw new IllegalStateException("\nError: Did not find goal for ms_change assertion: " + this.p_node.b_p.prod.name);
      }
    
    /**
     * rete.cpp:1065:find_goal_for_match_set_change_retraction
     * 
     * @param msc
     * @return
     */
    public IdentifierImpl find_goal_for_match_set_change_retraction()
    {
        // #ifdef DEBUG_WATERFALL
        // print_with_symbols(thisAgent, "\nMatch goal level for retraction:
        // %y", msc->inst->prod->name);
        // #endif

        if (this.inst.match_goal != null)
        {
            // If there is a goal, just return the goal
            // #ifdef DEBUG_WATERFALL
            // print_with_symbols(thisAgent, " is [%y]", msc->inst->match_goal);
            // #endif
            return this.inst.match_goal;
        }
        else
        {
            // #ifdef DEBUG_WATERFALL
            // print(" is NIL (nil goal retraction)");
            //        #endif 
            return null;
        }
    }
}
