package structures.automaton;

/**
 * State.<br><br>
 * 
 * Created: 19.05.2008<br>
 * Last modified: 19.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class State {

    private String name;
    private boolean isFinal;
    
    public State(String name) {
        this(name, false);
    }
    
    public State(String name, boolean isFinal) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.isFinal = isFinal;
    }
    
    public boolean isFinal() {
        return this.isFinal;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String toString() {
        return this.name;
    }
    
    public boolean equals(Object o) {
        if (o instanceof State) {
            State s = (State)o;
            return s.isFinal == this.isFinal && s.name.equals(this.name);
        }
        return false;
    }
    
}
