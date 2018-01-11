package behaviourComposition.structure;

import java.util.ArrayList;
import structures.DeepCopiable;

public class Transition implements ITransition{
    
    protected Label label;
    private String from, to;
    private int fromNumber, toNumber;
    private int number;
    
    public Transition(int fromNumber, String from, int toNumber, String to, Label label,int number) {
        if (label == null) {
            throw new NullPointerException();
        }
        this.fromNumber = fromNumber;
        this.from = from;
        this.toNumber = toNumber;
        this.to = to;
        this.label = label;
        this.number = number;
    }
    public Transition(String from, String to, Label label) {
        if (label == null) {
            throw new NullPointerException();
        }
        this.from = from;
        this.to = to;
        this.label = label;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof Transition) {
            Transition t = (Transition)o;
            return t.fromNumber == this.fromNumber && t.toNumber == this.toNumber && t.label.action.equals(this.label.action);
        }
        return false;
    }
    
    public int hashCode() {
        return 7 * this.label.action.hashCode() + 5 * this.fromNumber + 3 * this.toNumber + 2;
    }
    
    /**
     * Returns the action.
     * @return The action.
     */
    public Action getAction() {
        return this.label.action;
    }
    
    /**
     * Returns the from.
     * @return The from.
     */
    public String getFrom() {
        return this.from;
    }
    
    /**
     * Returns the to.
     * @return The to.
     */
    public String getTo() {
        return this.to;
    }

    /**
     * @return
     */
    public boolean isLoop() {
        return this.fromNumber == this.toNumber;
    }

    public String toString() {
        return this.from + " -- " + this.label.action.toString() + " -> " + this.to;
    }

    /**
     * Returns the fromNumber.
     * @return The fromNumber.
     */
    public int getFromNumber() {
        return this.fromNumber;
    }

    /**
     * Returns the toNumber.
     * @return The toNumber.
     */
    public int getToNumber() {
        return this.toNumber;
    }

    /**
     * Returns the number.
     * @return The number.
     */
    public int getNumber() {
        return this.number;
    }
	public Label getLabel() {
		return label;
	}
}



