package behaviourComposition.structure;

import java.util.*;

import structures.*;

public class EnactedBehaviour extends AbstractTransitionSystem {

	private ArraySetList<EnactedState> states;
	private ArrayList<CombinedTransition> transitions;
	private AbstractTransitionSystem environment;
	private AbstractTransitionSystem behaviour;
	private HashMap<EnactedState, ArraySetList<CombinedTransition>> transitionMap;
	public EnactedBehaviour(AbstractTransitionSystem env, AbstractTransitionSystem beh)
	{
		this.environment = env;
		this.behaviour = beh;
		states = new ArraySetList<EnactedState>();
		transitions = new ArrayList<CombinedTransition>();
		transitionMap = new HashMap<EnactedState, ArraySetList<CombinedTransition>>();
		compute();
	}
	private void compute() {
		//compute the start states for the synchronous product
		Set<EnactedState> startStates = getStartSates();
		states.addAll(startStates);
		for(EnactedState s:startStates)
		{
			transitionMap.put(s, new ArraySetList<CombinedTransition>());
		}
		//lets keep a list of the states to expand
		ArraySetList<EnactedState>toExpand = new ArraySetList<EnactedState>();
		toExpand.addAll(startStates);
		IRestrictable restrictableBehaviour = (IRestrictable)behaviour;//behaviours should implement IRestrictable
		//expand states till none left
		while(!toExpand.isEmpty())
		{
			ArraySetList<EnactedState> toAdd = new ArraySetList<EnactedState>();
			ArraySetList<EnactedState> toRemove = new ArraySetList<EnactedState>();
			for(EnactedState s:toExpand)
			{
				// get all the outgoing transitions for the behaviour
				List<? extends Transition> outgoing = behaviour.getOutgoingTransitions(s.getBehaviourState()); 
				// for each transition evolve the environment and see if its legit
				for(Transition t:outgoing)
				{			
					if(((TransitionSystem)environment).hasOutgoingTransitionWithAction(environment.getStateNumber(s.getEnvironmentState()), t.getAction()))
					{
						// get the possible evnironment 'to' states for this action
						List<AbstractState> toEnvStates = (List<AbstractState>) environment.getToStates(t, s.getEnvironmentState());
							for(AbstractState es:toEnvStates)
							{
								//Set<State> appstates = restrictableBehaviour.getApplicableStates(t);
								// check for guards on the behaviour
								if(!restrictableBehaviour.hasRestrictions(t) || contains(restrictableBehaviour.getApplicableStates(t),(s.getEnvironmentState())))
								{
									/*EnactedState estate = new EnactedState(es,behaviour.getState(t.getTo()));
									boolean statePresent = isStateFound(expanded,estate);
									//addUnique(toAdd, estate);
									if(!statePresent){
									toAdd.add(estate);*/
									
									EnactedState estate = addState(toAdd,es,behaviour.getState(t.getTo()));
									CombinedTransition newTransition;
									if(t instanceof CombinedTransition)
									{
										CombinedTransition ct = (CombinedTransition)t;
										newTransition = new CombinedTransition(s,estate,ct.getBehaviour(),t);
									}else{
										newTransition = new CombinedTransition(s,estate,behaviour,t);
									}
									//addUnique(transitions,newTransition);
									transitions.add(newTransition);
									transitionMap.get(s).add(newTransition);
									//}
								}
							}
					}
					
				}
				toRemove.add(s);
			}
			toExpand.addAll(toAdd);
			toExpand.removeAll(toRemove);
			states.addAll(toAdd);
		}		

	}

	private EnactedState addState(ArraySetList<EnactedState> toAdd, AbstractState es, AbstractState state) {
		EnactedState newState = new EnactedState(es,state);
		int index = states.indexOf(newState);
		if(index>=0)
		{
			return states.get(index);
		}
		toAdd.add(newState);
		//add to hashmap also
		transitionMap.put(newState, new ArraySetList<CombinedTransition>());
		return newState;
	}
	public Set<Action> getActionsFor(EnactedState state)
	{
		Set<Action> actions = new HashSet<Action>();
		/*for(ITransition t:transitions)
		{
			CombinedTransition ct = (CombinedTransition)t;
			if(ct.getFromState().equals(state))
			{
				actions.add(ct.getAction());
			}
		}*/
		ArraySetList<CombinedTransition> transitions = transitionMap.get(state);
		for(CombinedTransition t:transitions)
		{
			actions.add(t.getAction());
		}
		return actions;
	}
	private boolean contains(Set<State> applicableStates,AbstractState abstractState) {
		for(State s:applicableStates)
		{
			if(s.equals(abstractState))
			{
				return true;
			}
		}
		return false;
	}
	
	
	private Set<EnactedState> getStartSates() {
		Set<EnactedState> startstates = new HashSet<EnactedState>();
		//combine each start state of the environment with each start state of the behaviour
		for(AbstractState s:(Set<AbstractState>)environment.getStartStates())
		{
			for(AbstractState sb:(Set<AbstractState>)behaviour.getStartStates())
			{
				startstates.add(new EnactedState(s,sb));
			}
		}
		return startstates;
	}
	@Override
	public ArraySetList<EnactedState> getStates() {
		return states;
	}
	@Override
	public Set<EnactedState> getStartStates() {
		Set<EnactedState> sstates = new HashSet<EnactedState>();
		for(AbstractState s:(Set<AbstractState>)behaviour.getStartStates())
		{
			if(s.isStartState())
			{
				sstates.addAll(getStatesContaining(s));
				//sstates.add(getStatesContaining(s));
			}
		}
		return sstates;
	}
	private Set<EnactedState> getStatesContaining(AbstractState state) {
		Set<EnactedState> cstates = new HashSet<EnactedState>();
		for(EnactedState s:states)
		{
			if(s.containsState(state))
			{
				cstates.add(s);
			}
		}
		return cstates;
	}
	@Override
	public List<CombinedTransition> getOutgoingTransitions(IState state) {
		//List<CombinedTransition> outgoing = new ArrayList<CombinedTransition>();
		List<CombinedTransition> otransitions = transitionMap.get(state);
		/*for(ITransition t:transitions)
		{
			CombinedTransition ct = (CombinedTransition)t;
			if(ct.getFromState().equals(state))
			{
				outgoing.add(ct);
			}
		}*/
		return otransitions;
	}
	@Override
	public int getStateNumber(IState state) {
		return states.indexOf(state);
	}
	/*@Override
	public List<EnactedState> getToStates(ITransition t, IState behaviourState) {
		List<EnactedState> states = new ArrayList<EnactedState>();
		for(ITransition transition:transitions)
		{
			CombinedTransition ct = (CombinedTransition)t;
			CombinedTransition st = (CombinedTransition)transition;
			if(ct.getAction().equals(st.getAction()))
			{
				states.add((EnactedState) st.getFromState());
			}
		}
		return states;
	}*/
	@Override
	public List<EnactedState> getToStates(ITransition t, IState behaviourState) {
		List<EnactedState> states = new ArrayList<EnactedState>();
		/*for(ITransition transition:transitions)
		{
			CombinedTransition ct = (CombinedTransition)t;
			CombinedTransition st = (CombinedTransition)transition;
			if(ct.getAction().equals(st.getAction()))
			{
				states.add((EnactedState) st.getFromState());
			}
		}*/
		List<CombinedTransition> outgoing = transitionMap.get(behaviourState);
		for(CombinedTransition ct:outgoing)
		{
			if(ct.getAction().equals(t))
			{
				states.add((EnactedState) ct.getToState());
			}
		}
		return states;
	}
	public List<EnactedState> getToStates(EnactedState fromState, Action action) {
		List<EnactedState> states = new ArrayList<EnactedState>();
		/*List<CombinedTransition> outgoing = (List<CombinedTransition>) getOutgoingTransitions(fromState);
		for(CombinedTransition t:outgoing)
		{
			if(t.getAction().equals(action))
			{
				states.add((EnactedState) t.getToState());
			}
		}*/
		List<CombinedTransition> outgoing = transitionMap.get(fromState);
		for(CombinedTransition t:outgoing)
		{
			if(t.getAction()==action)
			{
				states.add((EnactedState) t.getToState());
			}
		}
		return states;
	}
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		string.append("--- Enacted States----"+"\n");
		for(EnactedState state:states)
		{
			string.append(state.toString()+"\n");
		}
		for(ITransition t:transitions)
		{
			string.append(((CombinedTransition)t).toString()+"\n");
		}

		return string.toString();
	}
	@Override
	public List<? extends Transition> getTransitions() {
		return (List<? extends Transition>) transitions;
	}
	@Override
	public AbstractState getState(int stateNumber) {
		return states.get(stateNumber);
	}
	@Override
	public AbstractState getState(String to) {
		for(EnactedState s:states)
		{
			if(s.getName().equals(to))
			{
				return s;
			}
		}
		return null;
	}
	public List<EnactedState> getToStates(EnactedState fromState) {
		List<EnactedState> states = new ArrayList<EnactedState>();
		/*List<CombinedTransition> outgoing = (List<CombinedTransition>) getOutgoingTransitions(fromState);
		for(CombinedTransition t:outgoing)
		{
			states.addAll(getToStates(t, fromState));
		}*/
		List<CombinedTransition> outgoing = transitionMap.get(fromState);
		for(CombinedTransition t:outgoing)
		{
			states.add((EnactedState) t.getToState());
		}
		return states;
	}
	public boolean isFinal(EnactedState state) {
		return state.isFinalState();
	}
	public List<EnactedState> getToStates(EnactedState fromState,Set<Action> actions) {
		/*List<EnactedState> states = new ArrayList<EnactedState>();
		for(ITransition t :transitions)
		{
			CombinedTransition ct = (CombinedTransition)t;
			if(ct.getFromState().equals(fromState) && actions.contains(ct.getAction()))
			{
				states.add((EnactedState) ct.getToState());
			}
		}*/
		List<EnactedState> states2 = new ArrayList<EnactedState>();
		ArraySetList<CombinedTransition> transitions = transitionMap.get(fromState);
		for(CombinedTransition t:transitions)
		{
			if(actions.contains(t.getAction())){
			states2.add((EnactedState) t.getToState());
			}
		}
		return states2;
	}
	public List<EnactedState> getNDStates(EnactedState fromState, Action action) {
		List<EnactedState> states = new ArrayList<EnactedState>();
		List<Behaviour> behaviours = new ArrayList<Behaviour>();
		List<Behaviour> ndbehaviours = new ArrayList<Behaviour>();
		if(behaviour instanceof CombinedBehaviour)
		{
			behaviours = ((CombinedBehaviour)behaviour).getBehaviours();
			for(Behaviour beh:behaviours)
			{
				CombinedState cs = (CombinedState)fromState.getBehaviourState();
				State st = cs.getState(beh);
				if(beh.getToStates(st,action).size()>1)
				{
					ndbehaviours.add(beh);
				}				
			}
			if(ndbehaviours.size()>0){
				/*for(ITransition t :transitions)
				{
					CombinedTransition ct = (CombinedTransition)t;
					if(ct.getFromState().equals(fromState) && ct.getAction().equals(action) && ndbehaviours.contains(ct.getBehaviour()))
					{
						states.add((EnactedState) ct.getToState());
					}
				}*/
				List<CombinedTransition> outgoing = transitionMap.get(fromState);
				for(CombinedTransition t :outgoing)
				{
					if(t.getAction().equals(action) && ndbehaviours.contains(t.getBehaviour()))
					{
						states.add((EnactedState) t.getToState());
					}
				}
			}
		}
		return states;
	}

}
