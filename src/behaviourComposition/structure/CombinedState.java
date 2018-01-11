package behaviourComposition.structure;

import java.util.ArrayList;
import java.util.List;

import structures.DeepCopiable;

public class CombinedState extends AbstractState implements DeepCopiable<CombinedState>
{
	private ArrayList<State> states;
	private ArrayList<String> stateNames;
	/*public CombinedState()
	{
		super("cs");
		states = new ArrayList<State>();
		stateNames = new ArrayList<String>();
	}*/
	public CombinedState(ArrayList<State> states)
	{
		super(states.toString());
		this.states = states;
		stateNames = new ArrayList<String>();
		for(State s:states)
		{
			stateNames.add(s.getName());
		}
	}
	public CombinedState(State s) {
		super(s.toString());
		this.states = new ArrayList<State>();
		states.add(s);
		stateNames = new ArrayList<String>();
		stateNames.add(s.getName());
	}
	/**checks if the combined state containes given states list
	 * */
	public boolean containsStates(ArrayList<State> cstates) {
		for(State s:cstates)
		{
			if(!stateNames.contains(s.getName()))
			{
				return false;
			}
		}
		return true;
	}
	public ArrayList<State> getStates() {
		return states;
	}
	public boolean hasState(String from) {
		for(State s:states)
		{
			if(s.getName().equals(from)){
				return true;
			}
		}
		return false;
	}
	public ArrayList<State> getCombinedState()
	{
		return (ArrayList<State>) states;
	}
	public void addState(State state)
	{
		states.add(state);
		stateNames.add(state.getName());
	}
	@Override
	public CombinedState deepCopy() {
		CombinedState copy = new CombinedState((ArrayList<State>)this.states.clone());
		return copy;
	}
	public String toString()
	{
		return stateNames.toString();
	}
	@Override
	public boolean isFinalState() {
		for(State s:states)
		{
			if(!s.isFinalState())
			{
				return false;
			}
		}
		return true;
	}
	@Override
	public boolean isStartState() {
		for(State s:states)
		{
			if(!s.isStartState())
			{
				return false;
			}
		}
		return true;
	}
	public String getName()
	{
		StringBuilder builder = new StringBuilder();
		for(State s:states)
		{
			builder.append(s.getName());
		}
		return builder.toString();
	}
	public boolean equals(Object o)
	{
		if(o instanceof CombinedState)
		{
			CombinedState cs =(CombinedState)o;
			if(cs.getStates().size() == getStates().size())
			{
				for(State s:cs.getStates())
				{
					if(!contains(states,s))
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	private boolean contains(List<State> states,State s)
	{
		for(State state:states)
		{
			if(state.equals(s))
			{
				return true;
			}
		}
		return false;
	}
	public int hashCode()
	{
		return toString().hashCode();
	}
	public State getState(Behaviour beh) {
		for(State s:states)
		{
			if(beh.containsState(s))
			{
				return s;
			}
		}
		return null;
	}
}
