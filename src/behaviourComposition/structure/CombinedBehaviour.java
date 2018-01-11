package behaviourComposition.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import behaviourComposition.structure.Behaviour.BehaviourLabel;
import structures.ArraySetList;
public class CombinedBehaviour extends AbstractTransitionSystem implements IRestrictable
{
	private List<Behaviour> behaviours;
	private ArraySetList<CombinedState> states;
	private List<CombinedTransition> transitions;
	private HashMap<CombinedState, ArraySetList<CombinedTransition>> transitionMap;
	private HashMap<String,CombinedState> statesMap;
	public CombinedBehaviour(List<Behaviour> availBehaviours)
	{
		behaviours= availBehaviours;
		states = new ArraySetList<CombinedState>();
		transitions = new ArrayList<CombinedTransition>();
		transitionMap = new HashMap<CombinedState, ArraySetList<CombinedTransition>>();
		statesMap = new HashMap<String, CombinedState>();
		compute();
	}
	public int getStateNumber(IState s)
	{
		return states.indexOf(s);
	}
	public void compute()
	{
		//take each behaviour and combine with permutations of others
		states = getAllCombinations();
		for(Behaviour b:behaviours)
		{				
			for(State s : b.getStates())
			{
				for(Transition t : b.getOutgoingTransitions(s))
				{
					addTransition(b,t);
				}
			}
		}
	}
	/** adds combinedtransition to the combinedstates
	 *  important : all state names should be unique.
	 * */
	public void addTransition(Behaviour b, Transition t) {
		String from = t.getFrom();
		String to = t.getTo();
		for(CombinedState cs:states)
		{
			if(cs.hasState(from))
			{
				CombinedState tostate = getToState(cs,from,to);
				CombinedTransition transition = new CombinedTransition(cs,tostate,b,t);
				transitions.add(transition);
				if(transitionMap.containsKey(cs))
				{
					transitionMap.get(cs).add(transition);
				}else
				{
					ArraySetList<CombinedTransition> tlist = new ArraySetList<CombinedTransition>();
					tlist.add(transition);
					transitionMap.put(cs,tlist);
				}
			}
		}
	}
	/** returns the to combined state give the from
	 * */
	private CombinedState getToState(CombinedState cs,String from, String to) {
		ArrayList<State> csstates = (ArrayList<State>)cs.getStates().clone();
		removeStateFromArray(csstates,from); //get the unchanged states
		for(CombinedState s:states)
		{
			ArrayList<CombinedState> applicablestates = getStatesContaining(csstates);
			return getStateContaining(applicablestates,to);
		}
		return null;
	}
	/** takes the states and returns the first state containing the to state
	 * */
	private CombinedState getStateContaining(
			ArrayList<CombinedState> applicablestates, String to) {
		for(CombinedState s:applicablestates)
		{
			if(s.hasState(to))
			{
				return s;
			}
		}
		return null;
	}
	/** returns the combinedstates contating a list of states
	 * */
	private ArrayList<CombinedState> getStatesContaining(ArrayList<State> cstates) {
		ArrayList<CombinedState> containingStates = new ArrayList<CombinedState>();
		for(CombinedState s:states)
		{
			if(s.containsStates(cstates))
			{
				containingStates.add(s);
			}
		}
		return containingStates;
	}
	private void removeStateFromArray(ArrayList<State> states, String from) {
		State toremove =null;
		for(State s:states)
		{
			if(s.getName().equals(from))
			{
				toremove = s;
			}
		}
		states.remove(toremove);
		
	}
	/** Calculate the cross product of the  states of the available behaviours
	 **/
	private ArraySetList<CombinedState> getAllCombinations() {
		ArraySetList<CombinedState> states = new ArraySetList<CombinedState>();
		List<Behaviour> otherbehaviours = (List<Behaviour>) ((ArrayList)behaviours).clone();
		for(Behaviour b:otherbehaviours)
		{
			states = addStatesForBehaviour(states,b);
		}
		return states;
	}
	/** Calculate the cross product of all the available behaviour except for the given behaviour
	 * */
	private List<CombinedState> getAllCombinationsFor(Behaviour beh) {
		ArraySetList<CombinedState> states = new ArraySetList<CombinedState>();
		List<Behaviour> otherbehaviours = (List<Behaviour>) ((ArrayList)behaviours).clone();
		otherbehaviours.remove(beh);
		for(Behaviour b:otherbehaviours)
		{
			states = addStatesForBehaviour(states,b);
		}
		return states;
	}
	/** adds all the permutations for the given behaviour to the given combined states
	 * */
	private ArraySetList<CombinedState> addStatesForBehaviour(ArraySetList<CombinedState> states2,Behaviour b) {
		ArraySetList<CombinedState> newstates = new ArraySetList<CombinedState>();
		if(states2.isEmpty())
		{
			for(State s:b.getStates())
			{
				CombinedState cb = new CombinedState(s);
				newstates.add(cb);
			}
		}
		else
		{
			for(CombinedState cb:states2)
			{
				for(State s:b.getStates())
				{
					CombinedState cbnew = cb.deepCopy();
					cbnew.addState(s);
					newstates.add(cbnew);
				}
			}
		}
		return newstates;
	}
	public boolean hasRestrictions(ITransition trans) {
		Transition t = ((CombinedTransition)trans).getTransition();
        return !((BehaviourLabel)(t.getLabel())).environmentRestriction.isEmpty();
//        return this.guard.containsKey(t);
    }
	public Set<State> getApplicableStates(ITransition trans) {
		Transition t = ((CombinedTransition)trans).getTransition();
	        return ((BehaviourLabel)t.getLabel()).environmentRestriction;
	}
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("--- Available Behaviours ---\n");
		for(Behaviour b:behaviours)
		{
			sb.append(b.toString()+"\n");
		}
		sb.append("--- Combined Behaviour ---\n");
		for(CombinedState c:states)
		{
			sb.append(c.toString()+"\n");
		}
		for(Transition t:transitions)
		{
			sb.append(t.toString()+"\n");
		}
		return sb.toString();
	}
	@Override
	public ArraySetList<CombinedState> getStates() {
		return states;
	}
	@Override
	public Set<CombinedState> getStartStates() {
		Set<CombinedState> sstates = new HashSet<CombinedState>();
		for(CombinedState s:states)
		{
			if(s.isStartState())
			{
				sstates.add(s);
			}
		}
		return sstates;
	}
	public List<CombinedTransition> getOutgoingTransitions(IState fromState) {
		/*List<CombinedTransition> outgoing = new ArrayList<CombinedTransition>();
		for(CombinedTransition t:transitions)
		{
			if(((CombinedState)t.getFromState()).equals(fromState))
			{
				outgoing.add(t);
			}
		}*/
		List<CombinedTransition> outgoing = transitionMap.get(fromState);
		return outgoing;
	}
	public List<CombinedTransition> getOutgoingTransitions(int i) {
		List<CombinedTransition> outgoing = new ArrayList<CombinedTransition>();
		CombinedState fromState = states.get(i);
		for(CombinedTransition t:transitions)
		{
			if(t.getFromState().equals(fromState))
			{
				outgoing.add(t);
			}
		}
		return outgoing;
	}
	public CombinedState getState(int i) {
		return states.get(i);
	}
	@Override
	public List<CombinedState> getToStates(ITransition trans, IState behaviourState) {	
		List<CombinedState> states = new ArrayList<CombinedState>();
		for(CombinedTransition t:getOutgoingTransitionsWithAction((CombinedState)behaviourState,((CombinedTransition)trans).getAction()))
		{
			states.add((CombinedState) t.getToState());
		}
		return states;
	}
	private List<CombinedTransition> getOutgoingTransitionsWithAction(
			CombinedState behaviourState, Action action) {
		List<CombinedTransition> outgoing = new ArrayList<CombinedTransition>();
		for(CombinedTransition t:transitions)
		{
			if(t.getFromState().equals(behaviourState) && t.getAction().equals(action))
			{
				outgoing.add(t);
			}
		}
		return outgoing;
	}
	@Override
	public List<? extends Transition> getTransitions() {
		return transitions;
	}
	@Override
	public AbstractState getState(String to) {
		for(CombinedState s:states)
		{
			if(s.getName().equals(to))
			{
				return s;
			}
		}
		return null;
	}
	public List<Behaviour> getBehaviours() {
		// TODO Auto-generated method stub
		return behaviours;
	}
	
}