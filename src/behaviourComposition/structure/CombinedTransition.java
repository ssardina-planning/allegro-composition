package behaviourComposition.structure;

public class CombinedTransition extends Transition
{
	private AbstractState from;
	private AbstractState to;
	private AbstractTransitionSystem behaviour;
	private Transition transition;
	public CombinedTransition(AbstractState from, AbstractState to, AbstractTransitionSystem beh,Transition t)
	{
		super(from.getName(),to.getName(),t.getLabel());
		this.from = from;
		this.to = to;
		this.behaviour = beh;
		this.transition= t;
	}
	public Transition getTransition()
	{
		return transition;
	}
	public String getFrom()
	{
		return from.getName();
	}
	public String getTo()
	{
		return to.getName();
	}
	public AbstractState getFromState()
	{
		return from;
	}
	public AbstractState getToState()
	{
		return to;
	}
	public AbstractTransitionSystem getBehaviour()
	{
		return behaviour;
	}
	public String toString()
	{
		return from.toString() + " ---"+transition.getAction().getName()+"--->"+to.toString();
	}
	public boolean equals(Object o)
	{
		if(o instanceof CombinedTransition)
		{
			CombinedTransition t = (CombinedTransition) o;
			if(t.getAction()==this.getAction() && t.getFrom() == this.getFrom() && t.getTo()==this.getTo())
			{
				return true;
			}
		}
		return false;
	}
	public int hashCode() {
	        return 7 * this.label.action.hashCode() + 5 * this.getFrom().hashCode() + 3 * this.getTo().hashCode() + 2;
	}
}