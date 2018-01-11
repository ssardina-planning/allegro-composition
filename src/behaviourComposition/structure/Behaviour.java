package behaviourComposition.structure;

import java.util.*;

/**
 * Behaviour.<br><br>
 * 
 * Created: 16.06.2008<br>
 * Last modified: 16.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Behaviour extends TransitionSystem implements IRestrictable {

//    private Map<Transition, Set<State>> guard;
    
    /**
     * Constructs a new instance of Behaviour.
     */
    public Behaviour() {
        super();
//        this.guard = new LinkedHashMap<Transition, Set<State>>();
    }

    /**
     * Constructs a new instance of Behaviour.
     * @param states
     */
    public Behaviour(Collection<String> states) {
        super(states);
//        this.guard = new LinkedHashMap<Transition, Set<State>>();
    }

    /**
     * Constructs a new instance of Behaviour.
     * @param startCapacity
     */
    public Behaviour(int startCapacity) {
        super(startCapacity);
//        this.guard = new LinkedHashMap<Transition, Set<State>>();
    }

    protected Behaviour create() {
        return new Behaviour();
    }
    
    public void addApplicableState(Transition t, State s) {
        if (t == null || s == null) {
            throw new NullPointerException(); //TODO nice message
        }
        ((BehaviourLabel)t.label).environmentRestriction.add(s);
//        if (this.hasRestrictions(t)) {
//            ((EnvironmentRestriction)t.getRestriction()).restrictedStates.add(s);
//            this.guard.get(t).add(s);
//        } else {
//            Set<State> set = new LinkedHashSet<State>();
//            set.add(s);
//            this.guard.put(t, set);
//        }
    }

    public void addApplicableStates(Transition t, Collection<State> states) {
        if (t == null || states == null) {
            throw new NullPointerException(); //TODO nice message
        }
        ((BehaviourLabel)t.label).environmentRestriction.addAll(states);
//        if (this.hasRestrictions(t)) {
//            this.guard.get(t).addAll(states);
//        } else {
//            this.guard.put(t, new LinkedHashSet<State>(states));
//        }
    }

    public void changeTo(TransitionSystem behaviour) {
        if (behaviour instanceof Behaviour) {
            super.changeTo(behaviour);
        } else {
            throw new ClassCastException("Argument must be a Behaviour!");
        }
//        this.guard = behaviour.guard;
    }

//    public void clear() {
//        super.clear();
//        this.guard.clear();
//    }
    
//    public Behaviour deepCopy() {
//        int capacity = 2,
//            numOfStates = this.getNumberOfStates();
//        while (capacity < numOfStates) {
//            capacity += capacity;
//        }
//        Behaviour res = new Behaviour(capacity);
//        for (State s : this.getStates()) {
//            res.addState(s.getName());
//        }
//        for (State s : this.getStartStates()) {
//            res.setStartState(s.getName(), true);
//        }
//        for (State s : this.getFinalStates()) {
//            res.setFinalState(s.getName(), true);
//        }
//        for (Transition t : this.getTransitions()) {
//            res.addTransition(t.getFrom(), t.getTo(), new Action(t.getAction().getName()));
//        }
//        for (Map.Entry<Transition, Set<State>> entry : this.guard.entrySet()) {
//            Transition t = entry.getKey();
//            Set<State> set = entry.getValue(),
//                       newSet = new LinkedHashSet<State>();
//            for (State s : set) {
//                newSet.add(new State(s.getName(), s.getSort()));
//            }
//            res.guard.put(new Transition(t.getFromNumber(), t.getFrom(), t.getToNumber(), t.getTo(), new Label(new Action(t.getAction().getName()))), newSet);
//        }
//        return res;
//    }
    
    public Set<State> getApplicableStates(ITransition t) {
        return ((BehaviourLabel)((Transition)t).label).environmentRestriction;
    }
    
    public int getDeterministicStateNumber(int from, Action action, State currentEnvironmentState) {
        List<Transition> outs = this.getOutgoingTransitionsWithAction(from, action);
        for (Transition t : outs) {
            if (!this.hasRestrictions(t) || this.getApplicableStates(t).contains(currentEnvironmentState)) {
                return this.getStateNumber(t.getTo());
            }
        }
        return -1;
    }

    public List<Transition> getGuardedOutgoingTransitions(State state, State environmentState) {
        return this.getGuardedOutgoingTransitions(this.getStateNumber(state), environmentState);
    }
    
    /**
     * @param stateNumber
     * @param environmentState
     * @return
     */
    public List<Transition> getGuardedOutgoingTransitions(int stateNumber, State environmentState) {
        List<Transition> res = new ArrayList<Transition>(),
                         outs = this.getOutgoingTransitions(stateNumber);
        for (Transition t : outs) {
            if (((BehaviourLabel)t.label).environmentRestriction.contains(environmentState)) {
                res.add(t);
            }
//            if (this.guard.containsKey(t)) {
//                if (this.guard.get(t).contains(environmentState)) {
//                    res.add(t);
//                }
//            } else {
//                res.add(t);
//            }
        }
        return res;
    }

    public List<Transition> getGuardedOutgoingTransitionsWithAction(State state, State environmentState, Action action) {
        return this.getGuardedOutgoingTransitionsWithAction(this.getStateNumber(state), environmentState, action);
    }
    
    /**
     * @param stateNumber
     * @param environmentState
     * @return
     */
    public List<Transition> getGuardedOutgoingTransitionsWithAction(int stateNumber, State environmentState, Action action) {
        List<Transition> res = new ArrayList<Transition>(),
                         outs = this.getOutgoingTransitionsWithAction(stateNumber, action);
        for (Transition t : outs) {
            if (((BehaviourLabel)t.label).environmentRestriction.contains(environmentState)) {
                res.add(t);
            }
//            if (this.guard.containsKey(t)) {
//                if (this.guard.get(t).contains(environmentState)) {
//                    res.add(t);
//                }
//            } else {
//                res.add(t);
//            }
        }
        return res;
    }

    public boolean hasRestrictions(ITransition t) {
        return !((BehaviourLabel)((Transition)t).label).environmentRestriction.isEmpty();
//        return this.guard.containsKey(t);
    }

    /**
     * @return
     */
    public boolean isDeterministic() { //TODO check reachability of every state!
        if (this.getStartStates().size() > 1) {
            return false;
        }
        for (State s : this.getStates()) {
            List<Transition> outs = this.getOutgoingTransitions(s);
            Set<Action> used = new LinkedHashSet<Action>();
            Map<Action, Set<Transition>> conflicts = new LinkedHashMap<Action, Set<Transition>>();
            for (Transition t : outs) {
                Action a = t.getAction();
                if (used.contains(a)) {
                    if (conflicts.containsKey(a)) {
                        conflicts.get(a).add(t);
                    } else {
                        Set<Transition> set = new LinkedHashSet<Transition>();
                        set.add(t);
                        conflicts.put(a, set);
                    }
                } else {
                    used.add(a);
                }
            }
            if (!conflicts.isEmpty()) {
                for (Set<Transition> tSet : conflicts.values()) {
                    Set<State> usedStates = new LinkedHashSet<State>();
                    for (Transition t : tSet) {
                        if (!this.hasRestrictions(t)) {
                            return false;
                        }
                        Set<State> usedStatesCopy = new LinkedHashSet<State>(usedStates),
                                   restriction = this.getApplicableStates(t);
                        usedStatesCopy.retainAll(restriction);
                        if (usedStatesCopy.isEmpty()) {
                            usedStates.addAll(restriction);
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * @return
     */
    public boolean isTransitionSystem() {
        for (Transition t : this.getTransitions()) {
            if (!((BehaviourLabel)t.label).environmentRestriction.isEmpty()) {
                return false;
            }
        }
        return true;
//        return this.guard.isEmpty();
    }
    
    public void removeAllApplicableStates(Transition t, Collection<State> states) {
        if (t == null || states == null || !this.hasRestrictions(t)) {
            throw new IllegalArgumentException(); //TODO nice message
        }
        ((BehaviourLabel)t.label).environmentRestriction.removeAll(states);
//        this.guard.get(t).removeAll(states);
//        if (this.guard.get(t).isEmpty()) {
//            this.guard.remove(t);
//        }
    }

    public void removeApplicableState(Transition t, State s) {
        if (t == null || s == null || !this.hasRestrictions(t)) {
            throw new IllegalArgumentException(); //TODO nice message
        }
        ((BehaviourLabel)t.label).environmentRestriction.remove(s);
//        this.guard.get(t).remove(s);
//        if (this.guard.get(t).isEmpty()) {
//            this.guard.remove(t);
//        }
    }
    
    public void removeRestriction(Transition t) {
        if (t == null) {
            throw new NullPointerException();
        }
        ((BehaviourLabel)t.label).environmentRestriction.clear();
//        this.guard.remove(t);
    }
    
    public void setRestricted(Transition t, Set<State> applicableStates) {
        if (t == null || applicableStates == null) {
            throw new IllegalArgumentException(); //TODO nice message
        }
        ((BehaviourLabel)t.label).environmentRestriction = applicableStates;
//        this.guard.put(t, applicableStates);
    }

    protected Label createLabel(Action action) {
        return createLabel(action, null);
    }
    
    /**
     * @param action
     * @param object
     * @return
     */
    protected BehaviourLabel createLabel(Action action, Set<State> environmentRestriction) {
        return environmentRestriction == null ? new BehaviourLabel(action) : new BehaviourLabel(action, environmentRestriction);
    }

    protected class BehaviourLabel extends Label {

        protected Set<State> environmentRestriction;
        
        protected BehaviourLabel(Action action) {
            this(action, new LinkedHashSet<State>());
        }
        
        /**
         * Constructs a new instance of BehaviourLabel.
         * @param action
         */
        protected BehaviourLabel(Action action, Set<State> environmentRestriction) {
            super(action);
            if (environmentRestriction == null) {
                environmentRestriction = new LinkedHashSet<State>();
            }
            this.environmentRestriction = environmentRestriction;
        }
        
        /* (non-Javadoc)
         * @see src.structure.TransitionSystem.Label#deepCopy()
         */
        @Override
        public BehaviourLabel deepCopy() {
            Set<State> newRestriction = new LinkedHashSet<State>();
            for (State s : this.environmentRestriction) {
                newRestriction.add(s.deepCopy());
            }
            return new BehaviourLabel(new Action(this.action.getName()), newRestriction);
        }

        /* (non-Javadoc)
         * @see src.structure.TransitionSystem.Label#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            if (o instanceof BehaviourLabel) {
                BehaviourLabel label = (BehaviourLabel)o;
                return label.action.equals(this.action) && label.environmentRestriction.containsAll(this.environmentRestriction) && this.environmentRestriction.containsAll(label.environmentRestriction);
            }
            return false;
        }

        /* (non-Javadoc)
         * @see src.structure.TransitionSystem.Label#hashCode()
         */
        @Override
        public int hashCode() {
            return super.hashCode() + this.environmentRestriction.hashCode() * 5;
        }

    }

	
    
}
