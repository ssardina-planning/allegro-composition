package behaviourComposition.structure;

import java.util.*;

/**
 * Scheduler.<br><br>
 * 
 * Created: 16.06.2008<br>
 * Last modified: 16.06.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class Scheduler extends Behaviour {

//    private List<Map<Transition, Set<State>>> behaviourGuards;
//    private Map<Transition, Integer> scheduledBehaviour;
    
    private List<Behaviour> behaviours;
    
    /**
     * Constructs a new instance of Scheduler.
     */
    public Scheduler(List<Behaviour> behaviours) {
        super();
        init(behaviours);
//        init(numberOfBehaviours);
    }

    /**
     * @param numberOfBehaviours
     */
    private void init(List<Behaviour> behaviours) {
        if (behaviours == null) {
            throw new NullPointerException();
        }
        this.behaviours = behaviours;
//        this.behaviourGuards = new ArrayList<Map<Transition, Set<State>>>();
//        this.scheduledBehaviour = new LinkedHashMap<Transition, Integer>();
//        for (int i = 0; i < numberOfBehaviours; i++) {
//            this.behaviourGuards.add(new LinkedHashMap<Transition, Set<State>>());
//        }
    }

    /**
     * Constructs a new instance of Scheduler.
     * @param states
     */
    public Scheduler(Collection<String> states, List<Behaviour> behaviours) {
        super(states);
        init(behaviours);
//        init(numberOfBehaviours);
    }

    /**
     * Constructs a new instance of Scheduler.
     * @param startCapacity
     */
    public Scheduler(int startCapacity, List<Behaviour> behaviours) {
        super(startCapacity);
        init(behaviours);
//        init(numberOfBehaviours);
    }

    public Transition addTransition(int from, int to, Action action) {
        throw new UnsupportedOperationException();
    }
    
    public Transition addTransition(int from, int to, Action action, int behaviour) {
        Transition t = super.addTransition(from, to, action);
        ((SchedulerLabel)t.label).scheduledBehaviour = behaviour;
//        this.scheduledBehaviour.put(t, behaviour);
        return t;
    }
    
    public void addApplicableBehaviourState(int behaviour, Transition t, State state) {
        ((SchedulerLabel)t.label).behaviourRestriction.get(behaviour).add(state);
//        Map<Transition, Set<State>> guard = this.behaviourGuards.get(behaviour);
//        if (guard.containsKey(t)) {
//            guard.get(t).add(state);
//        } else {
//            Set<State> set = new LinkedHashSet<State>();
//            set.add(state);
//            guard.put(t, set);
//        }
    }
    
    public void removeApplicableBehaviourState(int behaviour, Transition t, State state) {
        ((SchedulerLabel)t.label).behaviourRestriction.get(behaviour).remove(state);
//        Map<Transition, Set<State>> guard = this.behaviourGuards.get(behaviour);
//        if (guard.containsKey(t)) {
//            guard.get(t).remove(state);
//            if (guard.get(t).isEmpty()) {
//                guard.remove(t);
//            }
//        }
    }
    
    public void removeApplicableBehaviourState(int behaviour, Transition t, int stateNumber) {
        this.removeApplicableBehaviourState(behaviour, t, this.getState(stateNumber));
    }
    
    public boolean hasBehaviourRestrictions(int behaviour, Transition t) {
        return !((SchedulerLabel)t.label).behaviourRestriction.get(behaviour).isEmpty();
//        return this.behaviourGuards.get(behaviour).containsKey(t);
    }
    
    public Set<State> getBehaviourRestrictions(int behaviour, Transition t) {
        return ((SchedulerLabel)t.label).behaviourRestriction.get(behaviour);
//        return this.behaviourGuards.get(behaviour).get(t);
    }
    
    public int getScheduledBehaviour(Transition t) {
        return ((SchedulerLabel)t.label).scheduledBehaviour;
//        return this.scheduledBehaviour.get(t);
    }
    
    public int getNumberOfBehaviours() {
        return this.behaviours.size();
//        return this.behaviourGuards.size();
    }

    protected SchedulerLabel createLabel(Action action, Set<State> environmentRestriction) {
        return new SchedulerLabel(action, environmentRestriction, this.behaviours.size());
    }
    
    /**
     * @param behaviour
     * @param t
     * @param states
     */
    public void addApplicableBehaviourStates(int behaviour, Transition t, Collection<State> states) {
        ((SchedulerLabel)t.label).behaviourRestriction.get(behaviour).addAll(states);
//        Map<Transition, Set<State>> guard = this.behaviourGuards.get(behaviour);
//        if (guard.containsKey(t)) {
//            guard.get(t).addAll(states);
//        } else {
//            guard.put(t, new LinkedHashSet<State>(states));
//        }
    }
    
    protected class SchedulerLabel extends BehaviourLabel {

        private List<Set<State>> behaviourRestriction;
        private int scheduledBehaviour;
        
        /**
         * Constructs a new instance of SchedulerLabel.
         * @param action
         */
        protected SchedulerLabel(Action action, int behaviours) {
            this(action, new LinkedHashSet<State>(), behaviours);
        }

        /**
         * Constructs a new instance of SchedulerLabel.
         * @param action
         * @param name
         * @param behaviours
         */
        protected SchedulerLabel(Action action, Set<State> environmentRestriction, int behaviours) {
            super(action, environmentRestriction);
            this.behaviourRestriction = new ArrayList<Set<State>>();
            for (int i = 0; i < behaviours; i++) {
                this.behaviourRestriction.add(new LinkedHashSet<State>());
            }
        }

        /* (non-Javadoc)
         * @see src.structure.TransitionSystem.Label#deepCopy()
         */
        @Override
        public SchedulerLabel deepCopy() {
            Set<State> newEnvironmentRestriction = new LinkedHashSet<State>();
            for (State s : this.environmentRestriction) {
                newEnvironmentRestriction.add(s.deepCopy());
            }
            SchedulerLabel res = new SchedulerLabel(this.action.deepCopy(), newEnvironmentRestriction, this.behaviourRestriction.size());
            res.scheduledBehaviour = this.scheduledBehaviour;
            for (int i = 0; i < this.behaviourRestriction.size(); i++) {
                Set<State> thisSet = this.behaviourRestriction.get(i),
                           resSet = this.behaviourRestriction.get(i);
                for (State s : thisSet) {
                    resSet.add(s.deepCopy());
                }
            }
            return res;
        }

        /* (non-Javadoc)
         * @see src.structure.TransitionSystem.Label#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            if (o instanceof SchedulerLabel) {
                SchedulerLabel label = (SchedulerLabel)o;
                if (label.action.equals(this.action) && label.environmentRestriction.containsAll(this.environmentRestriction) && this.environmentRestriction.containsAll(label.environmentRestriction) && label.scheduledBehaviour == this.scheduledBehaviour && label.behaviourRestriction.size() == this.behaviourRestriction.size()) {
                    for (int i = 0; i < this.behaviourRestriction.size(); i++) {
                        Set<State> labelSet = label.behaviourRestriction.get(i),
                                   thisSet = this.behaviourRestriction.get(i);
                        if (!(labelSet.containsAll(thisSet) && thisSet.containsAll(labelSet))) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        /* (non-Javadoc)
         * @see src.structure.TransitionSystem.Label#hashCode()
         */
        @Override
        public int hashCode() {
            return super.hashCode() + this.scheduledBehaviour * 7 + this.behaviourRestriction.hashCode() * 3;
        }
        
    }
    
}
