package behaviourComposition.ai;

//import gui.GUIToolkit;
import java.util.*;
import java.util.Map.Entry;
//import javax.swing.JFrame;
//import src.gui.TransitionSystemView;
import behaviourComposition.structure.*;
import behaviourComposition.structure.TransitionSystem.*;
import structures.*;

/**
 * A BehaviourSimulator can compute a Scheduler to realize
 * a given deterministic target Behaviour by scheduling
 * a number of given (possibly nondeterministic) available
 * Behaviours. To do that, it simulates the target
 * Behaviour in a given environment TransistionSystem by
 * evaluating all possible schedules for the available
 * Behaviours.<br><br>
 * 
 * Created: 12.06.2008<br>
 * Last modified: 22.06.2008
 * 
 * @author Thomas Str�der
 * @version 1.0
 */
public class NewBehaviourSimulator {

    /**
     * Unique state number for DecisionStates. For debugging purposes only.
     */
    private static int stateNumber = 0;
    
    /**
     * A flag to be set for debugging that enables some
     * additional output during the computation.
     */
//    private static final boolean DEBUG = false;
    /**
     * A flag to be set for measuring the time for the
     * Scheduler computation and printing it to the
     * standard output.
     */
    private static final boolean TIME = false;
    /**
     * A constant to symbolize a new state in the
     * resulting Scheduler which has no state number yet.
     */
    private static final int NEW_STATE = -1;
    /**
     * A constant symbolizing the environment to access
     * both available Behaviours and environment by
     * Integers.
     */
    private static final int ENVIRONMENT = -1;
    
    /**
     * A list of all available Behaviours.<br>
     * Their order is important since they will be accessed
     * and represented by their position numbers.
     */
    private List<Behaviour> availableBehaviours;
    /**
     * The environment TransistionSystem.
     */
    private TransitionSystem environment;
    /**
     * The target Behaviour.
     */
    private Behaviour targetBehaviour;
    /**
     * The number of all available Behaviours.<br>
     * This is only a cache and should be equal to
     * <code>this.availableBehaviours.size()</code>.
     */
    private int numberOfBehaviours;
    /**
     * The state number of the target Behaviour's start
     * state.<br>
     * Since the start state is checked during construction
     * it is stored to avoid multiple calculation.
     */
    private int targetStartState;
    /**
     * A non-empty set containing all start state numbers
     * of the environment.<br>
     * Since this set is checked during construction it is
     * stored to avoid multiple calculation.
     */
    private Set<Integer> environmentStartStates;
    /**
     * A list of non-empty sets containing all start state
     * numbers of the available Behaviours.<br>
     * Since these sets are checked during construction
     * they are stored to avoid multiple calculation. This
     * list has a length equal to the list of available
     * Behaviours and the order of the sets in this list is
     * according to the order in the list of available
     * Behaviours.
     */
    private List<Set<Integer>> behaviourStartStates;
    
    /**
     * Constructs a new BehaviourSimulator with the
     * specified available Behaviours, environment and
     * target Behaviour.
     * @param availableBehaviours The available Behaviours
     *                            to simulate the target
     *                            Behaviour.
     * @param environment The environment in which the
     *                    target Behaviour has to be
     *                    simulated.
     * @param targetBehaviour The deterministic Behaviour
     *                        that should be realized by
     *                        the available Behaviours.
     * @throws IllegalArgumentException
     *         If the target Behaviour is nondeterministic
     *         or any of the specified transition systems
     *         has no start state.
     * @throws NullPointerException
     *         If any of the parameters is null.
     */
    public NewBehaviourSimulator(List<Behaviour> availableBehaviours, TransitionSystem environment, Behaviour targetBehaviour) throws NullPointerException, IllegalArgumentException {
        if (availableBehaviours == null || environment == null || targetBehaviour == null) {
            throw new NullPointerException("All parameters must not be null!");
        }
        this.availableBehaviours = availableBehaviours;
        this.environment = environment;
        this.targetBehaviour = targetBehaviour;
        this.numberOfBehaviours = this.availableBehaviours.size();
        if (!this.targetBehaviour.isDeterministic()) {
            throw new IllegalArgumentException("Target behaviour must be deterministic!");
        }
        this.targetStartState = this.targetBehaviour.getDeterministicStartStateNumber();
        if (this.targetStartState == -1) {
            throw new IllegalArgumentException("Target behaviour has no start state!");
        }
        this.environmentStartStates = this.environment.getStartStateNumbers();
        if (this.environmentStartStates.isEmpty()) {
            throw new IllegalArgumentException("The environment has no start state!");
        }
        this.behaviourStartStates = new ArrayList<Set<Integer>>();
        for (int i = 0; i < this.numberOfBehaviours; i++) {
            Set<Integer> set = this.availableBehaviours.get(i).getStartStateNumbers();
            if (set.isEmpty()) {
                throw new IllegalArgumentException("Behaviour " + i + " has no start state!");
            }
            this.behaviourStartStates.add(set);
        }
    }
    
    /**
     * Computes a Scheduler which schedules the available
     * Behaviours specified in the construction of this
     * BehaviourSimulator to realize the specified target
     * Behaviour in the specified environment.
     * If the computation fails, null is returned.
     * Otherwise the resulting Scheduler contains all
     * possible schedules for the target Behaviour in form
     * of nondeterministic choices.
     * @return A Scheduler realizing the target Behaviour
     *         by scheduling the available Behaviours or
     *         null, if the computation fails.
     */
    public Scheduler computeScheduler() {
        long time = System.currentTimeMillis();
        // set up initial state
        Set<int[]> behStartStates = this.computeAllCombinations(new ArrayList<Integer>(), this.behaviourStartStates);
        List<Obligation> obligations = new ArrayList<Obligation>();
        for (Integer eState : this.environmentStartStates) {
            for (int[] bStates : behStartStates) {
                List<Triple<Action, Integer, List<ExpansionState>>> actions = new ArrayList<Triple<Action, Integer, List<ExpansionState>>>();
                for (Transition t : this.targetBehaviour.getOutgoingTransitions(this.targetStartState)) {
                    if (!this.targetBehaviour.hasRestrictions(t) || this.targetBehaviour.getApplicableStates(t).contains(this.environment.getState(stateNumber))) {
//                        if (this.targetBehaviour.hasRestrictions(t)) { TODO restrictions
//                            actionState.restriction = this.targetBehaviour.getApplicableStates(t);
//                        }
                        actions.add(new Triple<Action, Integer, List<ExpansionState>>(t.getAction(), t.getToNumber(), new ArrayList<ExpansionState>()));
                    }
                }
                obligations.add(new Obligation(eState, bStates, actions));
            }
        }
        ExpansionState sRoot = new ExpansionState(this.targetStartState, ExpansionState.NO_STATE, ExpansionState.NO_STATE, null, null, obligations);
        Map<Integer, ExpansionState> predecessors = new LinkedHashMap<Integer, ExpansionState>(); // stores the predecessor of each node
        Queue<ExpansionState> todo = new LinkedList<ExpansionState>();
        todo.offer(sRoot);
        Map<Integer, List<ExpansionState>> targetStates = new LinkedHashMap<Integer, List<ExpansionState>>(); // stores a list of each node representing the key target state except for instance children of another state
        Map<Integer, List<ExpansionState>> instanceStates = new LinkedHashMap<Integer, List<ExpansionState>>(); // stores all instance fathers and their instance children
        List<ExpansionState> list = new ArrayList<ExpansionState>();
        list.add(sRoot);
        targetStates.put(this.targetStartState, list);
        while (!todo.isEmpty()) {
            ExpansionState state = todo.poll();
            this.expansionStep(state, predecessors, targetStates, instanceStates, todo);
            if (this.markerStep(state)) {
                ExpansionState marked = state;
                while (marked != null) {
                    ExpansionState father = predecessors.get(marked.getNumber());
                    if (father == null) { // we marked sRoot
                        System.out.println((System.currentTimeMillis()-time));
                        return null;
                    }
                    // remove the marked state from all structures
                    father.removeChild(marked); // remove it from its predecessor
                    predecessors.remove(marked.getNumber()); // remove it as descendant
                    targetStates.get(marked.getTargetState()).remove(marked); // remove it as representative for a target state
                    List<ExpansionState> instances = instanceStates.get(marked.getNumber()); // find possible instance children
                    if (instances != null) { // there are instance children
                        for (ExpansionState instanceChild : instances) {
                            instanceChild.setInstanceFather(null); // remove link to instance father and process again
                            todo.offer(instanceChild);
                        }
                    }
                    instanceStates.remove(marked.getNumber()); // remove it as instance father
                    this.removeChildren(marked, predecessors, targetStates, instanceStates, todo); // propagation downwards
                    if (this.markerStep(father)) { // propagation upwards
                        marked = father;
                    } else {
                        marked = null;
                    }
                }
            }
        }
       // System.out.println((System.currentTimeMillis()-time));
        //Scheduler res = this.toScheduler(sRoot);
        //if (TIME) {
        //    System.out.println((System.currentTimeMillis()-time));
        //}
        return null;
    }
    
    /**
     * @param marked
     * @param predecessors
     * @param targetStates
     * @param instanceStates
     * @param todo 
     */
    private void removeChildren(ExpansionState marked,
                                Map<Integer, ExpansionState> predecessors,
                                Map<Integer, List<ExpansionState>> targetStates,
                                Map<Integer, List<ExpansionState>> instanceStates,
                                Queue<ExpansionState> todo) {
        for (Obligation obl : marked.getObligations()) {
            for (Triple<Action, Integer, List<ExpansionState>> triple : obl.getActions()) {
                for (ExpansionState toRemove : triple.getZ()) {
                    // remove child from all structures
                    todo.remove(toRemove); // remove it from further expansion
                    predecessors.remove(toRemove.getNumber()); // remove it as descendant
                    List<ExpansionState> targetStateList = targetStates.get(toRemove.getTargetState());
                    if (targetStateList != null) {
                        targetStateList.remove(toRemove); // remove it as representative for a target state
                    }
                    List<ExpansionState> instances = instanceStates.get(toRemove.getNumber()); // find possible instance children
                    if (instances != null) { // there are instance children
                        for (ExpansionState instanceChild : instances) {
                            instanceChild.setInstanceFather(null); // remove link to instance father and process again
                            todo.offer(instanceChild);
                        }
                    }
                    instanceStates.remove(toRemove.getNumber()); // remove it as instance father
                    for (Map.Entry<Integer, List<ExpansionState>> entry : instanceStates.entrySet()) { // remove it as instance child
                        entry.getValue().remove(toRemove);
                    }
                    this.removeChildren(toRemove, predecessors, targetStates, instanceStates, todo); // propagation downwards
                }
            }
        }
    }

    /**
     * @param state
     * @return
     */
    private boolean markerStep(ExpansionState state) {
        if (state.isInstanceChild()) {
            return false;
        }
        for (Obligation obl : state.getObligations()) {
            for (Triple<Action, Integer, List<ExpansionState>> triple : obl.getActions()) {
                if (triple.getZ().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param state
     * @param predecessors 
     * @param targetStates
     * @param instanceStates 
     * @param todo
     */
    private void expansionStep(ExpansionState state,
                               Map<Integer, ExpansionState> predecessors,
                               Map<Integer, List<ExpansionState>> targetStates,
                               Map<Integer, List<ExpansionState>> instanceStates,
                               Queue<ExpansionState> todo) {
        // first check if current state is an instance of another state
        if (targetStates.containsKey(state.getTargetState())) { // there exist states representing the same target state
            List<ExpansionState> toCheck = targetStates.get(state.getTargetState());
            for (ExpansionState instanceFather : toCheck) {
//                if (instanceFather.isInstanceChild()) {
//                    continue;
//                }
                if (this.isInstance(state, instanceFather)) {
                    state.setInstanceFather(instanceFather);
                    // add connection to instanceStates
                    if (instanceStates.containsKey(instanceFather.getNumber())) {
                        instanceStates.get(instanceFather.getNumber()).add(state);
                    } else {
                        List<ExpansionState> list = new ArrayList<ExpansionState>();
                        list.add(state);
                        instanceStates.put(instanceFather.getNumber(), list);
                    }
                    return; // we don't have to add the current state to the targetStates map, since it contains the instance father already
                }
            }
            toCheck.add(state); // current state is no instance of another, so it must be checked as instance father for further states
        } else {
            List<ExpansionState> list = new ArrayList<ExpansionState>();
            list.add(state);
            targetStates.put(state.getTargetState(), list);
        }
        for (Obligation obl : state.getObligations()) {
            State currentEnvironmentState = this.environment.getState(obl.getEnvironmentState()); // for restriction checks
            for (Triple<Action, Integer, List<ExpansionState>> triple : obl.getActions()) {
                // calculate all environment states reachable by performing this action
                List<Integer> reachableEnvironmentStates = new ArrayList<Integer>();
                int targetToState = triple.getY(); // the state of the target behaviour reached by performing this action
                for (Transition t : this.environment.getOutgoingTransitionsWithAction(obl.getEnvironmentState(), triple.getX())) {
                    reachableEnvironmentStates.add(this.environment.getStateNumber(t.getTo()));
                }
                // calculate all reachable states of the behaviour executing this action (for all available behaviours) 
                for (int i = 0; i < this.numberOfBehaviours; i++) {
                    Behaviour behaviour = this.availableBehaviours.get(i);
                    Set<Integer> reachableBehaviourStates = new LinkedHashSet<Integer>();
                    for (Transition t : behaviour.getOutgoingTransitionsWithAction(obl.getBehaviourStates()[i], triple.getX())) {
                        if (!behaviour.hasRestrictions(t) || behaviour.getApplicableStates(t).contains(currentEnvironmentState)) {
                            reachableBehaviourStates.add(behaviour.getStateNumber(t.getTo()));
                        }
                    }
                    if (!reachableEnvironmentStates.isEmpty() && !reachableBehaviourStates.isEmpty()) { // the action can be performed in the current environment state by the current behaviour
                        //this.targetBehaviour.getDeterministicStateNumber(state.currentTargetState, aState.action, currentEnvironmentState);
                        List<Obligation> obligations = new ArrayList<Obligation>();
                        for (Integer e : reachableEnvironmentStates) {
                            for (Integer b : reachableBehaviourStates) {
                                // all behaviours remain in their states except for the executing behaviour
                                int[] states = new int[this.numberOfBehaviours];
                                System.arraycopy(obl.getBehaviourStates(),0,states,0,this.numberOfBehaviours); // copy all behaviour states
                                states[i] = b; // change only the executing behaviour
                                // calculate all actions which have to be performable in the target state reached by this action
                                List<Triple<Action, Integer, List<ExpansionState>>> actions = new ArrayList<Triple<Action, Integer, List<ExpansionState>>>();
                                for (Transition t : this.targetBehaviour.getOutgoingTransitions(targetToState)) {
                                    if (!this.targetBehaviour.hasRestrictions(t) || this.targetBehaviour.getApplicableStates(t).contains(this.environment.getState(e))) {
//                                        if (this.targetBehaviour.hasRestrictions(t)) { TODO restrictions
//                                            aChild.restriction = this.targetBehaviour.getApplicableStates(t);
//                                        }
                                        actions.add(new Triple<Action, Integer, List<ExpansionState>>(t.getAction(), t.getToNumber(), new ArrayList<ExpansionState>()));
                                    }
                                }
                                obligations.add(new Obligation(e,states,actions));
                            }
                        }
                        ExpansionState child = new ExpansionState(triple.getY(), i, obl.getEnvironmentState(), obl.getBehaviourStates(), triple.getX(), obligations);
                        triple.getZ().add(child);
                        predecessors.put(child.getNumber(), state);
                        todo.offer(child);
                    }
                }
            }
        }
    }

//    /**
//     * @param startState
//     * @param targetStates
//     */
//    private void showExpansion(DecisionState startState) {
//        TransitionSystem system = new TransitionSystem();
//        int start = this.toSystem(startState, new LinkedHashMap<DecisionState, Integer>(), system);
//        system.setStartState(start);
//        JFrame frame = GUIToolkit.createFrame("Expansion");
//        GUIToolkit.createScrollableView(frame).add(new TransitionSystemView(system));
//        GUIToolkit.showCentral(frame);
//    }

//    /**
//     * @param state
//     * @param targetStates
//     * @param systemStates 
//     * @param system
//     */
//    private int toSystem(DecisionState state, Map<DecisionState, Integer> systemStates, TransitionSystem system) {
//        if (!systemStates.containsKey(state)) {
//            int number = system.addState("" + /*system.getNumberOfStates()*/ state.number + ": Decision " + state.behaviour + " -> " + state.currentTargetState);
//            systemStates.put(state, number);
//            if (state.isInstanceChild()) {
//                int to = this.toSystem(state.instanceFather, systemStates, system);
//                system.addTransition(number, to, new Action("INSTANCE"));
//            } else {
//                for (EnvironmentState eChild : state.children) {
//                    for (BehaviourState bChild : eChild.children) {
//                        for (ActionState aChild : bChild.children) {
//                            for (DecisionState child : aChild.children) {
//                                int to = this.toSystem(child, systemStates, system);
//                                system.addTransition(number, to, new Action(eChild.toString() + "," + bChild.toString() + "," + aChild.toString()));
//                            }
//                        }
//                    }
//                }
//            }
//            return number;
//        } else {
//            return systemStates.get(state);
//        }
//    }

    /**
     * Computes a set of integer arrays of a length equal to the number of available behaviours. These arrays contain the state numbers of the available behaviours in
     * which they start. The computed set contains all possible start configurations.
     * @param choice The current choice of start states for the already processed behaviours (empty in the beginning).
     * @param todo The list of possible start states for the unprocessed behaviours.
     * @return A set of all start configurations for the available behaviours.
     */
    private Set<int[]> computeAllCombinations(List<Integer> choice, List<Set<Integer>> todo) {
        Set<int[]> res = new LinkedHashSet<int[]>();
        if (todo.isEmpty()) { // we processed all behaviours - a configuration is complete
            int size = choice.size();
            int[] finalChoice = new int[size];
            for (int i = 0; i < size; i++) {
                finalChoice[i] = choice.get(i);
            }
            res.add(finalChoice);
        } else { // there are behaviours left to process
            for (Integer chosen : todo.get(0)) { // choose a start state for the first behaviour in the list
                List<Integer> nextChoice = new ArrayList<Integer>(choice);
                nextChoice.add(chosen); // add the chosen start state to the current configuration
                List<Set<Integer>> nextTodo = new ArrayList<Set<Integer>>(todo);
                nextTodo.remove(0); // remove the processed behaviour from the list
                res.addAll(this.computeAllCombinations(nextChoice, nextTodo));
            }
        }
        return res;
    }

    /**
     * @param startState
     * @return
     */
    private Scheduler toScheduler(ExpansionState startState) {
        Scheduler res = new Scheduler(this.availableBehaviours);
        int start = res.addState();
        res.toggleStartState(start);
        Map<ExpansionState, Integer> stateMap = new LinkedHashMap<ExpansionState, Integer>(); // link between ExpansionStates and states in Scheduler
        stateMap.put(startState, start);
        ExpansionStateSet states = new ExpansionStateSet(startState.getTargetState());
        states.add(startState);
        this.toScheduler(start, this.targetStartState, states, stateMap, res);
        return res;
    }

    /**
     * @param stateNumber The number of the state in the scheduler.
     * @param states A set representing all states mapped to the state in the scheduler.
     * @param stateMap Link between ExpansionStates and states in the scheduler.
     * @param res The resulting scheduler.
     */
    private void toScheduler(int stateNumber, int targetStateNumber, ExpansionStateSet states, Map<ExpansionState, Integer> stateMap, Scheduler res) {
        if (this.targetBehaviour.isFinalState(states.targetState)) {
            res.setFinalState(stateNumber, true);
        }
        /*
         * Divide by actions - treat as independend! //TODO also divide by restriction
         * Divide then by scheduled behaviour and reached state in scheduler -
         *   if there are more than one: calculate differences and add restrictions accordingly
         *   otherwise: only one new state!
         */
        Map<Action, Map<Pair<Integer, Integer>, List<ComputationState>>> computationStates = new LinkedHashMap<Action, Map<Pair<Integer, Integer>, List<ComputationState>>>();
        for (ExpansionState state : states) {
            for (Obligation obl : state.getObligations()) {
                for (Triple<Action, Integer, List<ExpansionState>> triple : obl.getActions()) {
                    for (ExpansionState child : triple.getZ()) {
                            int behaviour = child.getBehaviour(); // store current behaviour - otherwise it will get lost by instances!
                            if (!computationStates.containsKey(triple.getX())) {
                                Map<Pair<Integer, Integer>, List<ComputationState>> map = new LinkedHashMap<Pair<Integer, Integer>, List<ComputationState>>();
                                computationStates.put(triple.getX(), map);
                            }
//                          Set<ComputationState> compStates = new LinkedHashSet<ComputationState>();
//                          for (EnvironmentState eChild : nextState.children) {
//                          for (BehaviourState bChild : eChild.children) {
//                          compStates.add(new ComputationState(bChild.currentBehaviourStates, eChild.currentEnvironmentState, nextState));
//                          }
//                          }
                            Map<Pair<Integer, Integer>, List<ComputationState>> scheduledMap = computationStates.get(triple.getX());
                            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(behaviour, NEW_STATE);
                            while (child.isInstanceChild()) {
                                child = child.getInstanceFather();
                            }
                            if (stateMap.containsKey(child)) { // we already mapped the instance father
                                pair.setLast(stateMap.get(child)); // update the already existing state number
                            }
                            if (scheduledMap.containsKey(pair)) {
//                              scheduledMap.get(pair).addAll(compStates);
                                scheduledMap.get(pair).add(new ComputationState(obl.getBehaviourStates(), obl.getEnvironmentState(), child));
                            } else {
                                List<ComputationState> list = new ArrayList<ComputationState>();
                                list.add(new ComputationState(obl.getBehaviourStates(), obl.getEnvironmentState(), child));
                                scheduledMap.put(pair, list);
//                              scheduledMap.put(pair, compStates);
                            }
                        }
                    }
                }
            }
//        }
        for (Map.Entry<Action, Map<Pair<Integer, Integer>, List<ComputationState>>> actionEntry : computationStates.entrySet()) {
            Action action = actionEntry.getKey();
            Map<Pair<Integer, Integer>, List<ComputationState>> scheduledMap = actionEntry.getValue(); // contains all scheduled behaviours for executing the current action
            if (scheduledMap.size() > 1) { // more than one transition - consider difference criteria!
                DecisionTree tree = this.computeDecisionTree(scheduledMap);
                for (Pair<Integer, ExpansionStateSet> todo : this.addTransitions(stateNumber, action, tree, stateMap, res)) {
                    this.toScheduler(todo.getFirst(), todo.getLast().targetState, todo.getLast(), stateMap, res);
                }
//                for (Map.Entry<Pair<Integer, Integer>, Set<ComputationState>> entry : scheduledMap.entrySet()) {
//                    int behaviour = entry.getKey().getFirst();
//                    int newStateNumber = entry.getKey().getLast();
//                    boolean added = false;
//                    int to = newStateNumber;
//                    for (ComputationState s : entry.getValue()) {
//                        if (newStateNumber != NEW_STATE) {
////                            int to = newStateNumber;
//                            Transition t = res.addTransition(stateNumber, to, action, behaviour);
//                            res.addApplicableState(t, this.environment.getState(s.currentEnvironmentState));
//                            for (int i = 0; i < this.numberOfBehaviours; i++) {
//                                res.addApplicableBehaviourState(i, t, s.currentStates[i]);
//                            }
//                        } else {
//                            if (!added) {
//                                to = res.addState();
//                                added = true;
//                            }
//                            stateMap.put(s.state, to);
//                            Transition t = res.addTransition(stateNumber, to, action, behaviour);
//                            res.addApplicableState(t, this.environment.getState(s.currentEnvironmentState));
//                            for (int i = 0; i < this.numberOfBehaviours; i++) {
//                                res.addApplicableBehaviourState(i, t, s.currentStates[i]);
//                            }
//                            this.toScheduler(to, s.state, stateMap, res);
//                        }
//                    }
//                }
            } else { // only one transition - only restrictions from target behaviour!
                Map.Entry<Pair<Integer, Integer>, List<ComputationState>> entry = scheduledMap.entrySet().iterator().next();
                int behaviour = entry.getKey().getFirst();
                int newStateNumber = entry.getKey().getLast();
                List<ComputationState> compStates = entry.getValue();
                int targetToStateNumber = compStates.get(0).state.getTargetState();
                if (newStateNumber == NEW_STATE) {
                    newStateNumber = res.addState();
                    this.addTransitionWithoutDifference(stateNumber, newStateNumber, targetStateNumber, targetToStateNumber, action, behaviour, compStates, stateMap, res);
                    ExpansionStateSet nextStates = null;
                    for (ComputationState s : compStates) {
                        if (nextStates == null) {
                            nextStates = new ExpansionStateSet(s.state.getTargetState());
                        }
                        nextStates.add(s.state);
                    }
                    if (nextStates == null) {
                        throw new IllegalStateException("There is no ComputationState for this ExpansionStateSet!");
                    }
                    this.toScheduler(newStateNumber, targetToStateNumber, nextStates, stateMap, res);
                } else {
                    this.addTransitionWithoutDifference(stateNumber, newStateNumber, targetStateNumber, targetToStateNumber, action, behaviour, compStates, stateMap, res);
                }
//                for (ComputationState s : entry.getValue()) {
//                    if (stateMap.containsKey(s.state)) {
//                        int to = stateMap.get(s.state);
//                        Transition t = res.addTransition(stateNumber, to, action, behaviour);
//                        res.addApplicableState(t, this.environment.getState(s.currentEnvironmentState));
//                        for (int i = 0; i < this.numberOfBehaviours; i++) {
//                            res.addApplicableBehaviourState(i, t, s.currentStates[i]);
//                        }
//                    } else {
//                        if (!added) {
//                            newStateNumber = res.addState();
//                            added = true;
//                        }
//                        Transition t = res.addTransition(stateNumber, newStateNumber, action, behaviour);
//                        res.addApplicableState(t, this.environment.getState(s.currentEnvironmentState));
//                        for (int i = 0; i < this.numberOfBehaviours; i++) {
//                            res.addApplicableBehaviourState(i, t, s.currentStates[i]);
//                        }
//                        this.toScheduler(newStateNumber, s.state, stateMap, res);
//                    }
//                }
            }
        }
    }

    /**
     * @param from 
     * @param action 
     * @param tree
     * @param stateMap
     * @param res
     */
    private List<Pair<Integer, ExpansionStateSet>> addTransitions(int from, Action action, DecisionTree tree, Map<ExpansionState, Integer> stateMap, Scheduler res) {
        Map<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>> newStates = this.addTransitionsToInstances(from, action, tree, new ArrayList<Pair<Integer, Integer>>(), new ArrayList<Pair<Integer, Integer>>(), res);
        List<Pair<Integer, ExpansionStateSet>> todo = new ArrayList<Pair<Integer, ExpansionStateSet>>();
        for (Map.Entry<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>> entry : newStates.entrySet()) {
            int newStateNumber = res.addState();
            for (Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> restrictionPair : entry.getValue().getFirst()) {
                List<Pair<Integer, Integer>> restriction = restrictionPair.getFirst(),
                                             nonRestrictions = restrictionPair.getLast();
                Transition t = res.addTransition(from, newStateNumber, action, entry.getKey());
                //TODO restrictions from target behaviour?
                for (Pair<Integer, Integer> pair : restriction) {
                    if (pair.getFirst() == ENVIRONMENT) {
                        res.addApplicableState(t, this.environment.getState(pair.getLast()));
                    } else {
                        res.addApplicableBehaviourState(pair.getFirst(), t, this.availableBehaviours.get(pair.getFirst()).getState(pair.getLast()));
                    }
                }
                for (Pair<Integer, Integer> nonRestriction : nonRestrictions) {
                    int restrictedBehaviour = nonRestriction.getFirst();
                    if (restrictedBehaviour == ENVIRONMENT) {
                        if (!res.hasRestrictions(t)) {
                            res.addApplicableStates(t, this.environment.getStates());
                        }
                        res.removeApplicableState(t, this.environment.getState(nonRestriction.getLast()));
                    } else {
                        if (!res.hasBehaviourRestrictions(restrictedBehaviour, t)) {
                            res.addApplicableBehaviourStates(restrictedBehaviour, t, this.availableBehaviours.get(restrictedBehaviour).getStates());
                        }
                        res.removeApplicableBehaviourState(restrictedBehaviour, t, this.availableBehaviours.get(restrictedBehaviour).getState(nonRestriction.getLast()));
                    }
                }
            }
            ExpansionStateSet todoStates = null;
            for (ExpansionState s : entry.getValue().getLast()) {
                if (todoStates == null) {
                    todoStates = new ExpansionStateSet(s.getTargetState());
                }
                stateMap.put(s, newStateNumber);
                todoStates.add(s);
            }
            todo.add(new Pair<Integer, ExpansionStateSet>(newStateNumber, todoStates));
        }
        return todo;
    }

    /**
     * @param tree
     * @param restrictions
     * @param stateMap
     * @param res
     * @return
     */
    private Map<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>> addTransitionsToInstances(int from, Action action, DecisionTree tree, List<Pair<Integer, Integer>> restrictions, List<Pair<Integer, Integer>> nonRestrictions, Scheduler res) {
        Map<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>> newStates = new LinkedHashMap<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>>();
        if (tree.isTest()) {
            TestNode node = (TestNode)tree;
            List<Pair<Integer, Integer>> newRestrictions = new ArrayList<Pair<Integer, Integer>>(restrictions),
                                         newNonRestrictions = new ArrayList<Pair<Integer, Integer>>(nonRestrictions);
            newRestrictions.add(new Pair<Integer, Integer>(node.criterium, node.value));
            newNonRestrictions.add(new Pair<Integer, Integer>(node.criterium, node.value));
            Map<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>> yesRes = this.addTransitionsToInstances(from, action, node.yes, newRestrictions, nonRestrictions, res),
                                                                                                                             noRes = this.addTransitionsToInstances(from, action, node.no, restrictions, newNonRestrictions, res);
            for (Map.Entry<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>> entry : yesRes.entrySet()) {
                if (newStates.containsKey(entry.getKey())) {
                    Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>> pair = newStates.get(entry.getKey());
                    pair.getFirst().addAll(entry.getValue().getFirst());
                    pair.getLast().addAll(entry.getValue().getLast());
                } else {
                    newStates.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Integer, Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>> entry : noRes.entrySet()) {
                if (newStates.containsKey(entry.getKey())) {
                    Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>> pair = newStates.get(entry.getKey());
                    pair.getFirst().addAll(entry.getValue().getFirst());
                    pair.getLast().addAll(entry.getValue().getLast());
                } else {
                    newStates.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            ListNode node = (ListNode)tree;
            for (Pair<Pair<Integer, Integer>, List<ComputationState>> pair : node.states) {
                if (pair.getLast().isEmpty()) {
                    continue;
                }
                int behaviour = pair.getFirst().getFirst();
                if (pair.getFirst().getLast() == NEW_STATE) {
                    if (!newStates.containsKey(behaviour)) {
                        newStates.put(behaviour, new Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>>(new ArrayList<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>(), new ArrayList<ExpansionState>()));
                    }
                    Pair<List<Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>>, List<ExpansionState>> value = newStates.get(behaviour);
                    value.getFirst().add(new Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>(restrictions, nonRestrictions));
                    for (ComputationState s : pair.getLast()) {
                        value.getLast().add(s.state);
                    }
                } else {
                    Transition t = res.addTransition(from, pair.getFirst().getLast(), action, behaviour);
                    //TODO restrictions from target behaviour?
                    for (Pair<Integer, Integer> restriction : restrictions) {
                        if (restriction.getFirst() == ENVIRONMENT) {
                            res.addApplicableState(t, this.environment.getState(restriction.getLast()));
                        } else {
                            res.addApplicableBehaviourState(restriction.getFirst(), t, this.availableBehaviours.get(restriction.getFirst()).getState(restriction.getLast()));
                        }
                    }
                    for (Pair<Integer, Integer> nonRestriction : nonRestrictions) {
                        int restrictedBehaviour = nonRestriction.getFirst();
                        if (restrictedBehaviour == ENVIRONMENT) {
                            if (!res.hasRestrictions(t)) {
                                res.addApplicableStates(t, this.environment.getStates());
                            }
                            res.removeApplicableState(t, this.environment.getState(nonRestriction.getLast()));
                        } else {
                            if (!res.hasBehaviourRestrictions(restrictedBehaviour, t)) {
                                res.addApplicableBehaviourStates(restrictedBehaviour, t, this.availableBehaviours.get(restrictedBehaviour).getStates());
                            }
                            res.removeApplicableBehaviourState(restrictedBehaviour, t, this.availableBehaviours.get(restrictedBehaviour).getState(nonRestriction.getLast()));
                        }
                    }
                }
            }
        }
        return newStates;
    }

    /**
     * @param scheduledMap
     * @return
     */
    private DecisionTree computeDecisionTree(Map<Pair<Integer, Integer>, List<ComputationState>> scheduledMap) {
        List<Pair<Integer, Integer>> attributes = new ArrayList<Pair<Integer, Integer>>();
        for (Integer eNum : this.environment.getStateNumbers()) {
            attributes.add(new Pair<Integer, Integer>(ENVIRONMENT, eNum));
        }
        for (int i = 0; i < this.numberOfBehaviours; i++) {
            for (Integer bNum : this.availableBehaviours.get(i).getStateNumbers()) {
                attributes.add(new Pair<Integer, Integer>(i, bNum));
            }
        }
        List<Pair<Pair<Integer, Integer>, List<ComputationState>>> examples = new ArrayList<Pair<Pair<Integer, Integer>, List<ComputationState>>>();
        int sum = 0;
        for (Entry<Pair<Integer, Integer>, List<ComputationState>> entry : scheduledMap.entrySet()) {
            examples.add(new Pair<Pair<Integer, Integer>, List<ComputationState>>(entry.getKey(), entry.getValue()));
            sum += entry.getValue().size();
        }
        return this.computeDecisionTree(sum, this.calculateEntropy(sum, examples), examples, attributes);
    }

    /**
     * @param entropy 
     * @param examples
     * @param attributes
     * @return
     */
    private DecisionTree computeDecisionTree(int sum, double entropy, List<Pair<Pair<Integer, Integer>, List<ComputationState>>> examples, List<Pair<Integer, Integer>> attributes) {
        // quick test for only one category
        boolean nonEmpty = false;
        for (Pair<Pair<Integer, Integer>, List<ComputationState>> entry : examples) {
            if (!entry.getLast().isEmpty()) {
                if (nonEmpty) {
                    nonEmpty = false;
                    break;
                } else {
                    nonEmpty = true;
                }
            }
        }
        if (nonEmpty) {
            return new ListNode(examples);
        }
        // we have more than one category in the examples
        double maxGain = 0.0;
        Pair<Integer, Integer> bestAttribute = null;
        double yesEntropy = 0.0,
               noEntropy = 0.0;
        int yesSum = 0,
            noSum = 0;
        List<Pair<Pair<Integer, Integer>, List<ComputationState>>> yes = null,
                                                                   no = null;
        for (Pair<Integer, Integer> attribute : attributes) {
            List<Pair<Pair<Integer, Integer>, List<ComputationState>>> positive = new ArrayList<Pair<Pair<Integer, Integer>, List<ComputationState>>>(),
                                                                       negative = new ArrayList<Pair<Pair<Integer, Integer>, List<ComputationState>>>();
            int posSum = 0,
                negSum = 0;
            if (attribute.getFirst() == ENVIRONMENT) {
                for (Pair<Pair<Integer, Integer>, List<ComputationState>> entry : examples) {
                    Pair<Pair<Integer, Integer>, List<ComputationState>> posPair = new Pair<Pair<Integer, Integer>, List<ComputationState>>(entry.getFirst(), new ArrayList<ComputationState>()),
                                                                         negPair = new Pair<Pair<Integer, Integer>, List<ComputationState>>(entry.getFirst(), new ArrayList<ComputationState>());
                    for (ComputationState s : entry.getLast()) {
                        if (s.currentEnvironmentState == attribute.getLast()) {
                            posPair.getLast().add(s);
                            posSum++;
                        } else {
                            negPair.getLast().add(s);
                            negSum++;
                        }
                    }
                    positive.add(posPair);
                    negative.add(negPair);
                }
            } else {
                for (Pair<Pair<Integer, Integer>, List<ComputationState>> entry : examples) {
                    Pair<Pair<Integer, Integer>, List<ComputationState>> posPair = new Pair<Pair<Integer, Integer>, List<ComputationState>>(entry.getFirst(), new ArrayList<ComputationState>()),
                                                                        negPair = new Pair<Pair<Integer, Integer>, List<ComputationState>>(entry.getFirst(), new ArrayList<ComputationState>());
                    for (ComputationState s : entry.getLast()) {
                        if (s.currentStates[attribute.getFirst()] == attribute.getLast()) {
                            posPair.getLast().add(s);
                            posSum++;
                        } else {
                            negPair.getLast().add(s);
                            negSum++;
                        }
                    }
                    positive.add(posPair);
                    negative.add(negPair);
                }
            }
            double posEntropy = this.calculateEntropy(posSum, positive),
                   negEntropy = this.calculateEntropy(negSum, negative);
            double gain = entropy - ((double)posSum / sum) * posEntropy - ((double)negSum / sum) * negEntropy;
            if (gain > maxGain) {
                maxGain = gain;
                yesEntropy = posEntropy;
                noEntropy = negEntropy;
                yes = positive;
                no = negative;
                yesSum = posSum;
                noSum = posSum;
                bestAttribute = attribute;
            }
        }
        if (bestAttribute == null) {
            return new ListNode(examples);
        }
        List<Pair<Integer, Integer>> newAttributes = new ArrayList<Pair<Integer, Integer>>(attributes);
        newAttributes.remove(bestAttribute);
        return new TestNode(bestAttribute.getFirst(), bestAttribute.getLast(), this.computeDecisionTree(yesSum, yesEntropy, yes, newAttributes), this.computeDecisionTree(noSum, noEntropy, no, newAttributes));
    }

    /**
     * @param examples
     * @return
     */
    private double calculateEntropy(int sum, List<Pair<Pair<Integer, Integer>, List<ComputationState>>> examples) {
        if (sum == 0) {
            return 0.0;
        }
        double res = 0.0;
        for (Pair<Pair<Integer, Integer>, List<ComputationState>> entry : examples) {
            double p = (double)entry.getLast().size() / sum;
            if (p > 0.0) {
                res -= p * Math.log(p) / Math.log(2);
            }
        }
        return res;
    }

    /**
     * @param stateNumber
     * @param newStateNumber
     * @param action
     * @param behaviour
     * @param compStates
     * @param stateMap
     * @param res
     */
    private void addTransitionWithoutDifference(int stateNumber, int newStateNumber, int targetFromStateNumber, int targetToStateNumber, Action action, int behaviour, List<ComputationState> compStates, Map<ExpansionState, Integer> stateMap, Scheduler res) {
        List<Transition> targetTransitions = this.targetBehaviour.getAllTransitionsWithAction(targetFromStateNumber, targetToStateNumber, action);
        Set<State> restrictions = new LinkedHashSet<State>();
        for (Transition t : targetTransitions) {
            if (!this.targetBehaviour.hasRestrictions(t)) {
                restrictions.clear();
                break;
            }
            restrictions.addAll(this.targetBehaviour.getApplicableStates(t));
        }
        Transition t = res.addTransition(stateNumber, newStateNumber, action, behaviour);
        if (!restrictions.isEmpty()) {
            res.addApplicableStates(t, restrictions);
        }
        for (ComputationState s : compStates) {
            stateMap.put(s.state, newStateNumber);
        }
    }

    /**
     * @param state
     * @param toCheck
     * @return
     */
    private boolean isInstance(ExpansionState instanceChild, ExpansionState instanceFather) {
        if (instanceChild.equals(instanceFather)) {
            return false;
        }
        oblLoop: for (Obligation obl : instanceChild.getObligations()) {
            for (Obligation fullfillment : instanceFather.getObligations()) {
                if (obl.equals(fullfillment)) {
                    tripleLoop: for (Triple<Action, Integer, List<ExpansionState>> triple : obl.getActions()) {
                        for (Triple<Action, Integer, List<ExpansionState>> match : fullfillment.getActions()) {
                            if (triple.getX().equals(match.getX()) && triple.getY().equals(match.getY())) {
                                continue tripleLoop;
                            }
                        }
                        return false;
                    }
                    continue oblLoop;
                }
            }
            return false;
        }
        return true;
    }

    private class ComputationState {
        
        private int[] currentStates;
        private int currentEnvironmentState;
        private ExpansionState state;
        
        private ComputationState(int[] states, int envState, ExpansionState s) {
            this.currentStates = states;
            this.currentEnvironmentState = envState;
            this.state = s;
        }
        
        public String toString() {
            String res = "Decision: " + state.toString() + ", Environment: " + this.currentEnvironmentState + ", Behaviours: ";
            for (int i = 0; i < this.currentStates.length; i++) {
                res += this.currentStates[i] + ",";
            }
            return res.substring(0, res.length()-1);
        }
        
    }
    
    private class DecisionTree {
        
        private boolean isTest() {
            return this instanceof TestNode;
        }

    }

    private class TestNode extends DecisionTree {
        
        private int criterium;
        private int value;
        private DecisionTree yes, no;
        
        private TestNode(int criterium, int value, DecisionTree yes, DecisionTree no) {
            this.criterium = criterium;
            this.value = value;
            this.no = no;
            this.yes = yes;
        }
        
    }
    
    private class ListNode extends DecisionTree {
        
        private List<Pair<Pair<Integer, Integer>, List<ComputationState>>> states;

        private ListNode(List<Pair<Pair<Integer, Integer>, List<ComputationState>>> states) {
            this.states = states;
        }
        
    }
    
    private class ExpansionStateSet extends LinkedHashSet<ExpansionState> {

        /**
         * 
         */
        private static final long serialVersionUID = -5418585859690999714L;
        private int targetState;
        
        private ExpansionStateSet(int targetState) {
            super();
            this.targetState = targetState;
        }
        
        public boolean add(ExpansionState s) {
            if (s.getTargetState() == this.targetState) {
                return super.add(s);
            }
            return false;
        }
        
        public boolean addAll(Collection<? extends ExpansionState> c) {
            boolean res = false;
            for (ExpansionState s : c) {
                res |= this.add(s);
            }
            return res;
        }
        
    }
    
}
