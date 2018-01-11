package behaviourComposition.structure;

import java.util.Set;

import behaviourComposition.structure.Behaviour.BehaviourLabel;

public interface IRestrictable {
	public boolean hasRestrictions(ITransition t) ;
	public Set<State> getApplicableStates(ITransition t) ;
}
