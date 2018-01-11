package behaviourComposition.structure;

import structures.DeepCopiable;

/**
 * Action.<br><br>
 * 
 * Created: 30.04.2008<br>
 * Last modified: 30.04.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Action implements DeepCopiable<Action> {

    public static final Action NOOP = new Action("noop");
    public static final Action EMPTY_ACTION = new Action("");
    
    private String name;
    
    public Action(String name) {
        this.name = name;
    }
    
    public boolean equals(Object o) {
        if (o instanceof Action) {
            return ((Action)o).name.equals(this.name);
        }
        return false;
    }
    
    public int hashCode() {
        return 13 * this.name.hashCode();
    }
    
    public String toString() {
        return this.name;
    }

    /**
     * @return
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see structures.DeepCopiable#deepCopy()
     */
    public Action deepCopy() {
        return new Action(this.name);
    }
    
}
