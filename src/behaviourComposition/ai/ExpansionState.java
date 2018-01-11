package behaviourComposition.ai;

import java.util.List;
import behaviourComposition.structure.Action;
import structures.Triple;

/**
 * ExpansionState.<br><br>
 * 
 * Created: 04.10.2009<br>
 * Last modified: 04.10.2009
 * 
 * @author Thomas Str�der
 * @version 1.0
 */
public class ExpansionState {
    
    public static final int NO_STATE = -1;
    
    private static int currentStateNumber = 1;
    
    private Action action;
    private int number, targetState, behaviour, environmentState;
    private int[] behaviourStates;
    private List<Obligation> obligations;
    private ExpansionState instanceFather;
    
    public ExpansionState(int tState, int b, int eState, int[] bStates, Action a, List<Obligation> o) {
        this.number = ExpansionState.currentStateNumber++;
        this.targetState = tState;
        this.behaviour = b;
        this.environmentState = eState;
        this.behaviourStates = bStates;
        this.action = a;
        this.obligations = o;
        this.instanceFather = null;
    }

    /**
     * Returns the currentStateNumber.
     * @return The currentStateNumber.
     */
    public static int getCurrentStateNumber() {
        return currentStateNumber;
    }

    /**
     * Returns the action.
     * @return The action.
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Returns the number.
     * @return The number.
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Returns the targetState.
     * @return The targetState.
     */
    public int getTargetState() {
        return this.targetState;
    }

    /**
     * Returns the behaviour.
     * @return The behaviour.
     */
    public int getBehaviour() {
        return this.behaviour;
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
     * Returns the obligations.
     * @return The obligations.
     */
    public List<Obligation> getObligations() {
        return this.obligations;
    }
    
    public int hashCode() {
        return this.number;
    }
    
    public boolean equals(Object o) {
        if (o instanceof ExpansionState) {
            return ((ExpansionState)o).number == this.number;
        } else {
            return false;
        }
    }
    
    public String toString() {
        String behaviourString = ", [";
        if (this.behaviourStates == null) {
            behaviourString += "null";
        } else {
            boolean first = true;
            for (int i = 0; i < this.behaviourStates.length; i++) {
                if (first) {
                    first = false;
                } else {
                    behaviourString += ",";
                }
                behaviourString += this.behaviourStates[i];
            }
        }
        return "" + this.number + ": t" + this.targetState + ", e" + this.environmentState + behaviourString + "], b" + this.behaviour + ", a: " + (this.action == null ? "null" : this.action.toString())
        + ", obligations: "+this.obligations;
    }

    /**
     * Returns the instanceFather.
     * @return The instanceFather.
     */
    public ExpansionState getInstanceFather() {
        return this.instanceFather;
    }

    /**
     * Sets the instanceFather.
     * @param instanceFather The instanceFather to set.
     */
    public void setInstanceFather(ExpansionState instanceFather) {
        this.instanceFather = instanceFather;
    }
    
    public boolean isInstanceChild() {
        return this.instanceFather != null;
    }

    /**
     * @param number2
     */
    public void removeChild(ExpansionState child) {
        for (Obligation o : this.obligations) {
            for (Triple<Action, Integer, List<ExpansionState>> triple : o.getActions()) {
                triple.getZ().remove(child);
            }
        }
    }
    
}
