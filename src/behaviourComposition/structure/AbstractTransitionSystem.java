package behaviourComposition.structure;

import java.util.*;

import structures.ArraySetList;

public abstract class AbstractTransitionSystem {

	public abstract ArraySetList<?extends AbstractState> getStates();
	public abstract Set<?> getStartStates();
	public abstract List<?extends Transition> getOutgoingTransitions(IState state);
	public abstract int getStateNumber(IState state);
	public abstract List<?extends AbstractState> getToStates(ITransition t, IState behaviourState); 
	public abstract List<?extends Transition> getTransitions();
	public abstract AbstractState getState(int stateNumber);
	public abstract AbstractState getState(String to);
}
