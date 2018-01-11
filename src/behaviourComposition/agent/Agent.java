package behaviourComposition.agent;

import java.util.LinkedHashSet;
import java.util.Set;

import behaviourComposition.structure.*;

/**
 * Agent.<br><br>
 * 
 * Created: 30.04.2008<br>
 * Last modified: 30.04.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public abstract class Agent {

    public abstract TransitionSystem getBehaviour();
    public abstract void performAction(Action a);
    public abstract State getCurrentState();
    
    public Set<Action> getAllPossibleActions() {
        Set<Action> res = new LinkedHashSet<Action>();
        for (Transition t : this.getBehaviour().getTransitions()) {
            res.add(t.getAction());
        }
        return res;
    }
    
    public Set<Action> getPossibleActionsAtState(State state) {
        Set<Action> res = new LinkedHashSet<Action>();
        for (Transition t : this.getBehaviour().getOutgoingTransitions(state)) {
            res.add(t.getAction());
        }
        return res;
    }

    public Set<Action> getPossibleActionsAtCurrentState() {
        return this.getPossibleActionsAtState(this.getCurrentState());
    }
    
}
