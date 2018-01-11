package structures.automaton;

import java.util.*;

/**
 * LinkedDFA.<br><br>
 * 
 * Created: 19.05.2008<br>
 * Last modified: 19.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class LinkedDFA<T> extends AbstractDFA<T> {

    private static final int STATE_NOT_FOUND = 0;
    private static final int OK = 0;
    private String name;
    private LinkedState start;
    
    public LinkedDFA() {
        this.start = null;
    }
    
    public LinkedDFA(State startState) {
        this.start = new LinkedState(startState);
    }
    
    public boolean isEmpty() {
        return this.start == null;
    }
    
    public String toString() {
        return this.name + ": " + (this.isEmpty() ? "empty" : this.start.toString(new LinkedHashSet<State>()));
    }
    
    public int setTransition(State start, State end, T transition) {
        if (start == null || end == null || transition == null) {
            throw new NullPointerException();
        }
        LinkedState from = this.findState(start);
        if (from == null) {
            return STATE_NOT_FOUND;
        }
        LinkedState to = this.findState(end);
        if (to == null) {
            return STATE_NOT_FOUND;
        }
        //TODO check reachability
        from.setLink(transition, to);
        return OK;
    }
    
    /**
     * @param state
     * @return
     */
    private LinkedState findState(State state) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean contains(State state) {
        //TODO
        return false;
    }
    
    private class LinkedState {
        
        private State state;
        private Map<T, LinkedState> links;
        
        private LinkedState(State state) {
            if (state == null) {
                throw new NullPointerException();
            }
            this.state = state;
            this.links = new LinkedHashMap<T, LinkedState>();
        }
        
        private void setLink(T transition, LinkedState target) {
            this.links.put(transition, target);
        }
        
        private String toString(Set<State> processed) {
            if (processed.contains(this.state)) {
                return "";
            } else {
                String res = this.state.toString();
                //TODO
                return res;
            }
        }
        
    }
    
}
