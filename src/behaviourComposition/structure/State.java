package behaviourComposition.structure;

import structures.DeepCopiable;

public class State extends AbstractState implements DeepCopiable<State> {
	
    
    public State(String name) {
        super(name, StateSort.NONE);
    }
    
    public State(String name, StateSort sort) {
    	 super(name, sort);
    }
    
    public boolean equals(Object o) {
        if (o instanceof State) {
            State s = (State)o;
            return s.name.equals(this.name) && s.sort == this.sort;
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see structures.DeepCopiable#deepCopy()
     */
    public State deepCopy() {
        State res = new State(this.name);
        res.sort = this.sort;
        return res;
    }
    
    
    public int hashCode() {
        int code = 0;
        switch (this.sort) {
            case START:
                code = 1;
                break;
            case FINAL:
                code = 2;
                break;
            case BOTH:
                code = 3;
                break;
        }
        return 11 * this.name.hashCode() + code;
    }
    
    public boolean isFinalState() {
        return this.sort == StateSort.FINAL || this.sort == StateSort.BOTH;
    }
    
    public boolean isStartState() {
        return this.sort == StateSort.START || this.sort == StateSort.BOTH;
    }
    
    public String toString() {
        String res = this.name;
        switch (this.sort) {
            case START:
                res += " (s)";
                break;
            case FINAL:
                res += " (f)";
                break;
            case BOTH:
                res += " (s/f)";
                break;
        }
        return res;
    }
}