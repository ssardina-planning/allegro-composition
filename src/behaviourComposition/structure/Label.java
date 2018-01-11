package behaviourComposition.structure;

import structures.DeepCopiable;

public class Label implements DeepCopiable<Label> {
    
    protected Action action;
    
    protected Label(Action action) {
        if (action == null) {
            throw new NullPointerException();
        }
        this.action = action;
    }

    /* (non-Javadoc)
     * @see structures.DeepCopiable#deepCopy()
     */
    public Label deepCopy() {
        return createLabel(new Action(this.action.getName()));
    }
    
    public boolean equals(Object o) {
        if (o instanceof Label) {
            return this.action.equals(((Label)o).action);
        }
        return false;
    }
    
    public int hashCode() {
        return this.action.hashCode()*3+1;
    }
    /**
     * @param action
     * @return
     */
    protected Label createLabel(Action action) {
        return new Label(action);
    }
}