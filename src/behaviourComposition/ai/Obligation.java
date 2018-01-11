package behaviourComposition.ai;

import java.util.List;
import behaviourComposition.structure.Action;
import structures.Triple;

/**
 * Obligation.<br><br>
 * 
 * Created: 04.10.2009<br>
 * Last modified: 04.10.2009
 * 
 * @author Thomas Str�der
 * @version 1.0
 */
public class Obligation {
    
    private int environmentState;
    private int[] behaviourStates;
    private List<Triple<Action, Integer, List<ExpansionState>>> actions;
    
    public Obligation(int eState, int[] bStates, List<Triple<Action, Integer, List<ExpansionState>>> a) {
        this.environmentState = eState;
        this.behaviourStates = bStates;
        this.actions = a;
    }

    /**
     * Returns the environmentState.
     * @return The environmentState.
     */
    public int getEnvironmentState() {
        return this.environmentState;
    }

    /**
     * Returns the behaviourStates.
     * @return The behaviourStates.
     */
    public int[] getBehaviourStates() {
        return this.behaviourStates;
    }

    /**
     * Returns the actions.
     * @return The actions.
     */
    public List<Triple<Action, Integer, List<ExpansionState>>> getActions() {
        return this.actions;
    }
    
    public int hashCode() {
        return this.environmentState * 7 + this.behaviourStates.hashCode();
    }
    
    public boolean equals(Object o) {
        if (o instanceof Obligation) {
            Obligation obl = (Obligation)o;
            if (obl.environmentState == this.environmentState) {
                for (int i = 0 ; i < this.behaviourStates.length; i++) {
                    if (this.behaviourStates[i] != obl.behaviourStates[i]) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public String toString() {
        String actionString = "], [",
               behaviourString = ", [";
        boolean first = true;
        for (Triple<Action, Integer, List<ExpansionState>> triple : this.actions) {
            if (first) {
                first = false;
            } else {
                actionString += ",";
            }
            actionString += triple.getX().getName();
        }
        first = true;
        for (int i = 0; i < this.behaviourStates.length; i++) {
            if (first) {
                first = false;
            } else {
                behaviourString += ",";
            }
            behaviourString += this.behaviourStates[i];
        }
        return "e" + this.environmentState + behaviourString + actionString + "]";
    }
    
}
