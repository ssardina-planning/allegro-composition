package behaviourComposition.structure;

public class EnactedState extends AbstractState {

	private AbstractState environmentState;
	private AbstractState behaviourState;
	private int id;
	private String name;
	public EnactedState(AbstractState envState, AbstractState behState) {
		super(behState.getName()+"/"+envState.getName());
		environmentState = envState;
		behaviourState = behState;
		String bhname = behState.getName();
		String enname = envState.getName();
		id = 2*bhname.hashCode() + 5*enname.hashCode();
		name = bhname + enname;
	}

	@Override
	public AbstractState deepCopy() {
		AbstractState env = environmentState.deepCopy();
		AbstractState beh = behaviourState.deepCopy();
		EnactedState state = new EnactedState(env,beh);
		return state;
	}

	@Override
	public boolean isFinalState() {
		return behaviourState.isFinalState();
	}

	@Override
	public boolean isStartState() {
		return behaviourState.isStartState();
	}
	public AbstractState getEnvironmentState()
	{
		return environmentState;
	}
	public AbstractState getBehaviourState()
	{
		return behaviourState;
	}
	public boolean containsState(AbstractState s)
	{
		if(behaviourState.equals(s))
		{
			return true;
		}
		if(environmentState.equals(s))
		{
			return true;
		}
		return false;
	}
	public String toString()
	{
		return behaviourState.toString() + " / " + environmentState.toString();
	}
	public int getId(){return id;}
	public boolean equals(Object o)
	{
		if(o instanceof EnactedState)
		{
			EnactedState s = (EnactedState)o;
			/*if(s.getBehaviourState().equals(behaviourState) && s.getEnvironmentState().equals(environmentState))
			{
				return true;
			}*/
			/*if(s.hashCode()==this.hashCode())
			{
				return true;
			}*/
			if(s.getId()==id){return true;}
		}
		return false;
	}
	public int hashCode()
	{
		return behaviourState.toString().hashCode() + 5*environmentState.toString().hashCode();
	}
	public String getName()
	{
		/*StringBuilder builder = new StringBuilder();
		builder.append(behaviourState.getName());
		//builder.append(" ");
		builder.append(environmentState.getName());
		return builder.toString();*/
		return name;
	}
}
