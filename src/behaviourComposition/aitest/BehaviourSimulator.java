package behaviourComposition.aitest;

//import gui.GUIToolkit;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.JFrame;
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
public class BehaviourSimulator {

    /**
     * Unique state number for DecisionStates. For debugging purposes only.
     */
    private static int stateNumber = 0;
    
    /**
     * A flag to be set for debugging that enables some
     * additional output during the computation.
     */
    private static final boolean DEBUG = false;
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
    private int decisionStatesCount=0;
    public BehaviourSimulator(List<Behaviour> availableBehaviours, TransitionSystem environment, Behaviour targetBehaviour) throws NullPointerException, IllegalArgumentException {
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
        DecisionState startState = new DecisionState(-1, this.targetStartState);
        for (Integer stateNumber : this.environmentStartStates) {
            EnvironmentState envState = new EnvironmentState(stateNumber);
            for (int[] states : behStartStates) {
                BehaviourState behState = new BehaviourState(states);
                for (Transition t : this.targetBehaviour.getOutgoingTransitions(this.targetStartState)) {
                    ActionState actionState = new ActionState(t.getAction(), t.getToNumber());
                    if (!this.targetBehaviour.hasRestrictions(t) || this.targetBehaviour.getApplicableStates(t).contains(this.environment.getState(stateNumber))) {
//                        if (this.targetBehaviour.hasRestrictions(t)) { TODO restrictions
//                            actionState.restriction = this.targetBehaviour.getApplicableStates(t);
//                        }
                        behState.children.add(actionState);
                    }
                }
                envState.children.add(behState);
            }
            startState.children.add(envState);
        }
        Map<Integer, Set<DecisionState>> targetStates = new LinkedHashMap<Integer, Set<DecisionState>>();
        this.expand(startState, new LinkedHashSet<DecisionState>(), targetStates);
  
        if (DEBUG) {
            long stop = System.currentTimeMillis();
            this.showExpansion(startState);
            time += System.currentTimeMillis() - stop;
        }
        if (this.cut(startState, targetStates)) {
            if (TIME) {
                System.out.println((System.currentTimeMillis()-time));
            }
            return null;
        }
        if (DEBUG) {
            long stop = System.currentTimeMillis();
            this.showExpansion(startState);
            time += System.currentTimeMillis() - stop;
        }
        Scheduler res = this.toScheduler(startState);
        if (TIME) {
            System.out.println((System.currentTimeMillis()-time));
        }
        return res;
    }
    
    /**
     * @param startState
     * @param targetStates
     */
    private void showExpansion(DecisionState startState) {
        TransitionSystem system = new TransitionSystem();
        int start = this.toSystem(startState, new LinkedHashMap<DecisionState, Integer>(), system);
        system.setStartState(start);
        //JFrame frame = GUIToolkit.createFrame("Expansion");
        //GUIToolkit.createScrollableView(frame).add(new TransitionSystemView(system));
        //GUIToolkit.showCentral(frame);
    }

    /**
     * @param state
     * @param targetStates
     * @param systemStates 
     * @param system
     */
    private int toSystem(DecisionState state, Map<DecisionState, Integer> systemStates, TransitionSystem system) {
        if (!systemStates.containsKey(state)) {
            int number = system.addState("" + /*system.getNumberOfStates()*/ state.number + ": Decision " + state.behaviour + " -> " + state.currentTargetState);
            systemStates.put(state, number);
            if (state.isInstanceChild()) {
                int to = this.toSystem(state.instanceFather, systemStates, system);
                system.addTransition(number, to, new Action("INSTANCE"));
            } else {
                for (EnvironmentState eChild : state.children) {
                    for (BehaviourState bChild : eChild.children) {
                        for (ActionState aChild : bChild.children) {
                            for (DecisionState child : aChild.children) {
                                int to = this.toSystem(child, systemStates, system);
                                system.addTransition(number, to, new Action(eChild.toString() + "," + bChild.toString() + "," + aChild.toString()));
                            }
                        }
                    }
                }
            }
            return number;
        } else {
            return systemStates.get(state);
        }
    }

    /**
     * Performs the marking algorithm on the graph with the specified root node and removes all
     * marked states from it if the root is not marked.
     * @param state The DecisionState representing the root node.
     * @param stateMap A map for instance checks needed in the marking algorithm.
     * @return True if the root is marked, false otherwise.
     */
    private boolean cut(DecisionState state, Map<Integer, Set<DecisionState>> stateMap) {
        Set<DecisionState> marks = new LinkedHashSet<DecisionState>();
        while (this.mark(state, marks, new LinkedHashSet<DecisionState>(), stateMap, state));
        if (marks.contains(state)) {
            return true;
        }
        this.cut(state, marks);
        return false;
    }
    public int getDecisionStateCount() {
		// TODO Auto-generated method stub
		return decisionStatesCount;
	}
    
    /**
     * Removes all states in the specified set from the (sub-)tree with the specified root node.
     * @param state The DecisionState representing the root node of the (sub-)tree.
     * @param marks The set of states which have to be removed.
     */
    private void cut(DecisionState state, Set<DecisionState> marks) {
        if (state.isInstanceChild()) {
            return;
        }
        for (EnvironmentState eChild : state.children) {
            for (BehaviourState bChild : eChild.children) {
                for (ActionState aChild : bChild.children) {
                    Set<DecisionState> toDel = new LinkedHashSet<DecisionState>();
                    for (DecisionState dChild : aChild.children) {
                        if (marks.contains(dChild)) {
                            toDel.add(dChild);
                        }
                    }
                    aChild.children.removeAll(toDel);
                    for (DecisionState dChild : aChild.children) {
                        this.cut(dChild, marks);
                    }
                }
            }
        }
    }

    /**
     * Performs the marking algorithm on the (sub-)tree with the specified root node.
     * @param state The current state in question to be marked and the root node for
     *              the currently processed (sub-)tree.
     * @param marks The set of marked states.
     * @param done The set of already processed states.
     * @param stateMap A map for instance checks.
     * @return
     */
    private boolean mark(DecisionState state, Set<DecisionState> marks, Set<DecisionState> done, Map<Integer, Set<DecisionState>> stateMap, DecisionState start) {
        if (done.contains(state)) { // we already processed this state and don't mark it possibly again
            return false;
        }
        done.add(state);
        if (marks.contains(state)) { // we already marked this state and don't mark it again
            return false;
        }
        if (state.isInstanceChild()) { // current state is instance state - check instance father
            if (!done.contains(state.instanceFather)) { // if we didn't process the instance father yet, do it now
                this.mark(state.instanceFather, marks, done, stateMap, start);
            }
            if (!marks.contains(state.instanceFather)) { // we didn't mark the instance father - current state will not be marked
                return false;
            }
            // we marked the instance father - we need to check if there is another unmarked instance father
            for (DecisionState toCheck : stateMap.get(state.currentTargetState)) {
                if (marks.contains(toCheck)) {
                    continue;
                }
                if (this.isInstance(state, toCheck)) { // we found another instance father
                    state.instanceFather = toCheck;
                    if (!done.contains(toCheck)) { // we have to process the new instance father
                        this.mark(toCheck, marks, done, stateMap, start);
                    }
                    if (marks.contains(toCheck)) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
            // we didn't find another instance father - we have to expand the state again
            state.removeInstance();
            this.expand(state, marks, stateMap);
            done.remove(state);
            return this.mark(state, marks, done, stateMap, start);
//            this.addAllDescendants(state, marks);
//            return true;
        }
        // current state is no instance state
        if (this.targetBehaviour.isFinalState(state.currentTargetState)) { // current state must be final for all behaviours
            for (EnvironmentState eState : state.children) {
                for (BehaviourState bState : eState.children) {
                    for (int i = 0; i < bState.currentBehaviourStates.length; i++) {
                        if (!this.availableBehaviours.get(i).isFinalState(bState.currentBehaviourStates[i])) { // no final state - mark state and all its descendants
                            this.addAllDescendants(state, marks);
                            return true;
                        }
                    }
                }
            }
        }
        // check if all actions performable in the target behaviour can be performed by some available behaviour
        for (EnvironmentState eState : state.children) {
            for (BehaviourState bState : eState.children) {
                for (ActionState aState : bState.children) {
                    Set<DecisionState> toDel = new LinkedHashSet<DecisionState>();
                    for (DecisionState dState : aState.children) {
                        if (!marks.contains(dState)) {
                            this.mark(dState, marks, done, stateMap, start);
                        }
                        if (marks.contains(dState)) {
                            toDel.add(dState);
                        }
                    }
                    Set<DecisionState> children = new LinkedHashSet<DecisionState>(aState.children);
                    children.removeAll(toDel);
                    if (children.isEmpty()) { // there is an action which is not performable by any available behaviour
                        this.addAllDescendants(state, marks);
                        return true;
                    }
                }
            }
        }
        // all actions are performable - don't mark the current state
        return false;
    }

    /**
     * Marks all descendants of the specified state (including the state itself).
     * @param state The state representing the subtree to be marked completely.
     * @param marks The set of marked states.
     */
    private void addAllDescendants(DecisionState state, Set<DecisionState> marks) {
        this.addAllDescendants(state, marks, new LinkedHashSet<DecisionState>());
    }

    /**
     * Marks all descendants of the specified state (including the state itself).
     * @param state The state representing the subtree to be marked completely.
     * @param marks The set of marked states.
     * @param done The set of already processed states for the complete marking.
     */
    private void addAllDescendants(DecisionState state, Set<DecisionState> marks, Set<DecisionState> done) {
        if (done.contains(state)) {
            return;
        }
        marks.add(state);
        done.add(state);
        if (!state.isInstanceChild()) {
            for (EnvironmentState eChild : state.children) {
                for (BehaviourState bChild : eChild.children) {
                    for (ActionState aChild : bChild.children) {
                        for (DecisionState dChild : aChild.children) {
                            this.addAllDescendants(dChild, marks, done);
                        }
                    }
                }
            }
        }
    }

    private Set<int[]> computeAllCombinations(List<Integer> choice, List<Set<Integer>> todo) {
        Set<int[]> res = new LinkedHashSet<int[]>();
        if (todo.isEmpty()) {
            int size = choice.size();
            int[] finalChoice = new int[size];
            for (int i = 0; i < size; i++) {
                finalChoice[i] = choice.get(i);
            }
            res.add(finalChoice);
        } else {
            for (Integer chosen : todo.get(0)) {
                List<Integer> nextChoice = new ArrayList<Integer>(choice);
                nextChoice.add(chosen);
                List<Set<Integer>> nextTodo = new ArrayList<Set<Integer>>(todo);
                nextTodo.remove(0);
                res.addAll(this.computeAllCombinations(nextChoice, nextTodo));
            }
        }
        return res;
    }

    /**
     * @param startState
     * @return
     */
    private Scheduler toScheduler(DecisionState startState) {
        Scheduler res = new Scheduler(this.availableBehaviours);
        int start = res.addState();
        res.toggleStartState(start);
        Map<DecisionState, Integer> stateMap = new LinkedHashMap<DecisionState, Integer>();
        stateMap.put(startState, start);
        DecisionStateSet states = new DecisionStateSet(startState.currentTargetState);
        states.add(startState);
        this.toScheduler(start, states, stateMap, res);
        return res;
    }

    /**
     * @param stateNumber
     * @param state
     * @param stateMap
     * @param res
     */
    private void toScheduler(int stateNumber, DecisionStateSet states, Map<DecisionState, Integer> stateMap, Scheduler res) {
        if (this.targetBehaviour.isFinalState(states.targetState)) {
            res.setFinalState(stateNumber, true);
        }
        /*
         * Divide by actions - treat as independend! //TODO also divide by restriction
         * Divide then by scheduled behaviour and reached state in scheduler -
         *   if there are more than one: calculate differences and add restrictions accordingly
         *   otherwise: only one new state!
         */
        Map<Action, Map<Pair<Integer, Integer>, Set<ComputationState>>> computationStates = new LinkedHashMap<Action, Map<Pair<Integer, Integer>, Set<ComputationState>>>();
        for (DecisionState state : states) {
            for (EnvironmentState eState : state.children) {
                for (BehaviourState bState : eState.children) {
                    for (ActionState aState : bState.children) {
                        for (DecisionState nextState : aState.children) {
                            int behaviour = nextState.behaviour; // store current behaviour - otherwise it will get lost by instances!
                            if (!computationStates.containsKey(aState.action)) {
                                Map<Pair<Integer, Integer>, Set<ComputationState>> map = new LinkedHashMap<Pair<Integer, Integer>, Set<ComputationState>>();
                                computationStates.put(aState.action, map);
                            }
//                          Set<ComputationState> compStates = new LinkedHashSet<ComputationState>();
//                          for (EnvironmentState eChild : nextState.children) {
//                          for (BehaviourState bChild : eChild.children) {
//                          compStates.add(new ComputationState(bChild.currentBehaviourStates, eChild.currentEnvironmentState, nextState));
//                          }
//                          }
                            Map<Pair<Integer, Integer>, Set<ComputationState>> scheduledMap = computationStates.get(aState.action);
                            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(behaviour, NEW_STATE);
                            while (nextState.isInstanceChild()) {
                                nextState = nextState.instanceFather;
                            }
                            if (stateMap.containsKey(nextState)) {
                                pair.setLast(stateMap.get(nextState));
                            }
                            if (scheduledMap.containsKey(pair)) {
//                              scheduledMap.get(pair).addAll(compStates);
                                scheduledMap.get(pair).add(new ComputationState(bState.currentBehaviourStates, eState.currentEnvironmentState, nextState));
                            } else {
                                Set<ComputationState> set = new LinkedHashSet<ComputationState>();
                                set.add(new ComputationState(bState.currentBehaviourStates, eState.currentEnvironmentState, nextState));
                                scheduledMap.put(pair, set);
//                              scheduledMap.put(pair, compStates);
                            }
                        }
                    }
                }
            }
        }
        for (Map.Entry<Action, Map<Pair<Integer, Integer>, Set<ComputationState>>> actionEntry : computationStates.entrySet()) {
            Action action = actionEntry.getKey();
            Map<Pair<Integer, Integer>, Set<ComputationState>> scheduledMap = actionEntry.getValue();
            if (scheduledMap.size() > 1) { // more than one transition - consider difference criteria!
                DecisionTree tree = this.computeDecisionTree(scheduledMap);
                for (Pair<Integer, DecisionStateSet> todo : this.addTransitions(stateNumber, action, tree, stateMap, res)) {
                    this.toScheduler(todo.getFirst(), todo.getLast(), stateMap, res);
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
                Map.Entry<Pair<Integer, Integer>, Set<ComputationState>> entry = scheduledMap.entrySet().iterator().next();
                int behaviour = entry.getKey().getFirst();
                int newStateNumber = entry.getKey().getLast();
                Set<ComputationState> compStates = entry.getValue();
                if (newStateNumber == NEW_STATE) {
                    newStateNumber = res.addState();
                    this.addTransitionWithoutDifference(stateNumber, newStateNumber, action, behaviour, compStates, stateMap, res);
                    DecisionStateSet nextStates = null;
                    for (ComputationState s : compStates) {
                        if (nextStates == null) {
                            nextStates = new DecisionStateSet(s.state.currentTargetState);
                        }
                        nextStates.add(s.state);
                    }
                    this.toScheduler(newStateNumber, nextStates, stateMap, res);
                } else {
                    this.addTransitionWithoutDifference(stateNumber, newStateNumber, action, behaviour, compStates, stateMap, res);
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
    private Set<Pair<Integer, DecisionStateSet>> addTransitions(int from, Action action, DecisionTree tree, Map<DecisionState, Integer> stateMap, Scheduler res) {
        Map<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>> newStates = this.addTransitionsToInstances(from, action, tree, new LinkedHashSet<Pair<Integer, Integer>>(), new LinkedHashSet<Pair<Integer, Integer>>(), res);
        Set<Pair<Integer, DecisionStateSet>> todo = new LinkedHashSet<Pair<Integer, DecisionStateSet>>();
        for (Map.Entry<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>> entry : newStates.entrySet()) {
            int newStateNumber = res.addState();
            for (Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>> restrictionPair : entry.getValue().getFirst()) {
                Set<Pair<Integer, Integer>> restriction = restrictionPair.getFirst(),
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
            DecisionStateSet todoStates = null;
            for (DecisionState s : entry.getValue().getLast()) {
                if (todoStates == null) {
                    todoStates = new DecisionStateSet(s.currentTargetState);
                }
                stateMap.put(s, newStateNumber);
                todoStates.add(s);
            }
            todo.add(new Pair<Integer, DecisionStateSet>(newStateNumber, todoStates));
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
    private Map<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>> addTransitionsToInstances(int from, Action action, DecisionTree tree, Set<Pair<Integer, Integer>> restrictions, Set<Pair<Integer, Integer>> nonRestrictions, Scheduler res) {
        Map<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>> newStates = new LinkedHashMap<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>>();
        if (tree.isTest()) {
            TestNode node = (TestNode)tree;
            Set<Pair<Integer, Integer>> newRestrictions = new LinkedHashSet<Pair<Integer, Integer>>(restrictions),
                                        newNonRestrictions = new LinkedHashSet<Pair<Integer, Integer>>(nonRestrictions);
            newRestrictions.add(new Pair<Integer, Integer>(node.criterium, node.value));
            newNonRestrictions.add(new Pair<Integer, Integer>(node.criterium, node.value));
            Map<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>> yesRes = this.addTransitionsToInstances(from, action, node.yes, newRestrictions, nonRestrictions, res),
                                                                                                                        noRes = this.addTransitionsToInstances(from, action, node.no, restrictions, newNonRestrictions, res);
            for (Map.Entry<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>> entry : yesRes.entrySet()) {
                if (newStates.containsKey(entry.getKey())) {
                    Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>> pair = newStates.get(entry.getKey());
                    pair.getFirst().addAll(entry.getValue().getFirst());
                    pair.getLast().addAll(entry.getValue().getLast());
                } else {
                    newStates.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Integer, Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>> entry : noRes.entrySet()) {
                if (newStates.containsKey(entry.getKey())) {
                    Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>> pair = newStates.get(entry.getKey());
                    pair.getFirst().addAll(entry.getValue().getFirst());
                    pair.getLast().addAll(entry.getValue().getLast());
                } else {
                    newStates.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            SetNode node = (SetNode)tree;
            for (Pair<Pair<Integer, Integer>, Set<ComputationState>> pair : node.states) {
                if (pair.getLast().isEmpty()) {
                    continue;
                }
                int behaviour = pair.getFirst().getFirst();
                if (pair.getFirst().getLast() == NEW_STATE) {
                    if (!newStates.containsKey(behaviour)) {
                        newStates.put(behaviour, new Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>>(new LinkedHashSet<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>(), new LinkedHashSet<DecisionState>()));
                    }
                    Pair<Set<Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>>, Set<DecisionState>> value = newStates.get(behaviour);
                    value.getFirst().add(new Pair<Set<Pair<Integer, Integer>>, Set<Pair<Integer, Integer>>>(restrictions, nonRestrictions));
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
    private DecisionTree computeDecisionTree(Map<Pair<Integer, Integer>, Set<ComputationState>> scheduledMap) {
        Set<Pair<Integer, Integer>> attributes = new LinkedHashSet<Pair<Integer, Integer>>();
        for (Integer eNum : this.environment.getStateNumbers()) {
            attributes.add(new Pair<Integer, Integer>(ENVIRONMENT, eNum));
        }
        for (int i = 0; i < this.numberOfBehaviours; i++) {
            for (Integer bNum : this.availableBehaviours.get(i).getStateNumbers()) {
                attributes.add(new Pair<Integer, Integer>(i, bNum));
            }
        }
        Set<Pair<Pair<Integer, Integer>, Set<ComputationState>>> examples = new LinkedHashSet<Pair<Pair<Integer, Integer>, Set<ComputationState>>>();
        int sum = 0;
        for (Entry<Pair<Integer, Integer>, Set<ComputationState>> entry : scheduledMap.entrySet()) {
            examples.add(new Pair<Pair<Integer, Integer>, Set<ComputationState>>(entry.getKey(), entry.getValue()));
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
    private DecisionTree computeDecisionTree(int sum, double entropy, Set<Pair<Pair<Integer, Integer>, Set<ComputationState>>> examples, Set<Pair<Integer, Integer>> attributes) {
        // quick test for only one category
        boolean nonEmpty = false;
        for (Pair<Pair<Integer, Integer>, Set<ComputationState>> entry : examples) {
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
            return new SetNode(examples);
        }
        // we have more than one category in the examples
        double maxGain = 0.0;
        Pair<Integer, Integer> bestAttribute = null;
        double yesEntropy = 0.0,
               noEntropy = 0.0;
        int yesSum = 0,
            noSum = 0;
        Set<Pair<Pair<Integer, Integer>, Set<ComputationState>>> yes = null,
                                                                 no = null;
        for (Pair<Integer, Integer> attribute : attributes) {
            Set<Pair<Pair<Integer, Integer>, Set<ComputationState>>> positive = new LinkedHashSet<Pair<Pair<Integer, Integer>, Set<ComputationState>>>(),
                                                                      negative = new LinkedHashSet<Pair<Pair<Integer, Integer>, Set<ComputationState>>>();
            int posSum = 0,
                negSum = 0;
            if (attribute.getFirst() == ENVIRONMENT) {
                for (Pair<Pair<Integer, Integer>, Set<ComputationState>> entry : examples) {
                    Pair<Pair<Integer, Integer>, Set<ComputationState>> posPair = new Pair<Pair<Integer, Integer>, Set<ComputationState>>(entry.getFirst(), new LinkedHashSet<ComputationState>()),
                                                                        negPair = new Pair<Pair<Integer, Integer>, Set<ComputationState>>(entry.getFirst(), new LinkedHashSet<ComputationState>());
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
                for (Pair<Pair<Integer, Integer>, Set<ComputationState>> entry : examples) {
                    Pair<Pair<Integer, Integer>, Set<ComputationState>> posPair = new Pair<Pair<Integer, Integer>, Set<ComputationState>>(entry.getFirst(), new LinkedHashSet<ComputationState>()),
                                                                        negPair = new Pair<Pair<Integer, Integer>, Set<ComputationState>>(entry.getFirst(), new LinkedHashSet<ComputationState>());
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
            return new SetNode(examples);
        }
        Set<Pair<Integer, Integer>> newAttributes = new LinkedHashSet<Pair<Integer, Integer>>(attributes);
        newAttributes.remove(bestAttribute);
        return new TestNode(bestAttribute.getFirst(), bestAttribute.getLast(), this.computeDecisionTree(yesSum, yesEntropy, yes, newAttributes), this.computeDecisionTree(noSum, noEntropy, no, newAttributes));
    }

    /**
     * @param examples
     * @return
     */
    private double calculateEntropy(int sum, Set<Pair<Pair<Integer, Integer>, Set<ComputationState>>> examples) {
        if (sum == 0) {
            return 0.0;
        }
        double res = 0.0;
        for (Pair<Pair<Integer, Integer>, Set<ComputationState>> entry : examples) {
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
    private void addTransitionWithoutDifference(int stateNumber, int newStateNumber, Action action, int behaviour, Set<ComputationState> compStates, Map<DecisionState, Integer> stateMap, Scheduler res) {
        List<Transition> targetTransitions = this.targetBehaviour.getAllTransitionsWithAction(stateNumber, newStateNumber, action);
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
     * @param state The state to be expanded.
     * @param marks The set of marked states.
     * @param targetStates A map from target state numbers to DecisionStates representing the same target state.
     *                     This map is needed for instance checks and contains no DecisionStates which are an
     *                     instance of another state. By this we avoid paths of instance edges in the graph with
     *                     a length greater than one. Also we have all possible instance fathers for any state.
     */
    private void expand(DecisionState state, Set<DecisionState> marks, Map<Integer, Set<DecisionState>> targetStates) {
        // first check if current state is an instance of another state
        if (targetStates.containsKey(state.currentTargetState)) { // there exist states representing the same target state
            Set<DecisionState> toCheck = targetStates.get(state.currentTargetState);
            for (DecisionState instanceFather : toCheck) {
                if (!marks.contains(instanceFather) && this.isInstance(state, instanceFather)) {
                    state.instanceFather = instanceFather;
                    return; // we don't have to add the current state to the targetStates map, since it contains the instance father already
                }
            }
            toCheck.add(state); // current state is no instance of another, so it must be checked as instance father for further states
        } else {
            Set<DecisionState> set = new LinkedHashSet<DecisionState>();
            set.add(state);
            targetStates.put(state.currentTargetState, set);
        }
        Set<DecisionState> toExpand = new LinkedHashSet<DecisionState>();
        for (EnvironmentState eState : state.children) {
            State currentEnvironmentState = this.environment.getState(eState.currentEnvironmentState); // for restriction checks
            for (BehaviourState bState : eState.children) {
                for (ActionState aState : bState.children) {
                    // calculate all environment states reachable by performing this action
                    Set<Integer> reachableEnvironmentStates = new LinkedHashSet<Integer>();
                    int targetToState = aState.targetToState; // the state of the target behaviour reached by performing this action
                    for (Transition t : this.environment.getOutgoingTransitionsWithAction(eState.currentEnvironmentState, aState.action)) {
                        reachableEnvironmentStates.add(this.environment.getStateNumber(t.getTo()));
                    }
                    // calculate all reachable states of the behaviour executing this action (for all available behaviours) 
                    for (int i = 0; i < this.numberOfBehaviours; i++) {
                        Behaviour behaviour = this.availableBehaviours.get(i);
                        Set<Integer> reachableBehaviourStates = new LinkedHashSet<Integer>();
                        for (Transition t : behaviour.getOutgoingTransitionsWithAction(bState.currentBehaviourStates[i], aState.action)) {
                            if (!behaviour.hasRestrictions(t) || behaviour.getApplicableStates(t).contains(currentEnvironmentState)) {
                                reachableBehaviourStates.add(behaviour.getStateNumber(t.getTo()));
                            }
                        }
                        if (!reachableEnvironmentStates.isEmpty() && !reachableBehaviourStates.isEmpty()) { // the action can be performed in the current environment state by the current behaviour
                            //this.targetBehaviour.getDeterministicStateNumber(state.currentTargetState, aState.action, currentEnvironmentState);
                            DecisionState dState = new DecisionState(i, targetToState); // with behaviour i we reach target state targetToState
                            decisionStatesCount++;
                            for (Integer e : reachableEnvironmentStates) {
                                EnvironmentState eChild = new EnvironmentState(e);
                                for (Integer b : reachableBehaviourStates) {
                                    // all behaviours remain in their states except for the executing behaviour
                                    int[] states = new int[this.numberOfBehaviours];
                                    System.arraycopy(bState.currentBehaviourStates,0,states,0,this.numberOfBehaviours); // copy all behaviour states
                                    states[i] = b; // change only the executing behaviour
                                    BehaviourState bChild = new BehaviourState(states);
                                    // calculate all actions which have to be performable in the target state reached by this action
                                    for (Transition t : this.targetBehaviour.getOutgoingTransitions(targetToState)) {
                                        ActionState aChild = new ActionState(t.getAction(), t.getToNumber());
                                        if (!this.targetBehaviour.hasRestrictions(t) || this.targetBehaviour.getApplicableStates(t).contains(this.environment.getState(e))) {
//                                            if (this.targetBehaviour.hasRestrictions(t)) { TODO restrictions
//                                                aChild.restriction = this.targetBehaviour.getApplicableStates(t);
//                                            }
                                            bChild.children.add(aChild);
                                        }
                                    }
                                    eChild.children.add(bChild);
                                }
                                dState.children.add(eChild);
                            }
                            aState.children.add(dState);
                            toExpand.add(dState);
                        }
                    }
                }
            }
        }
        for (DecisionState s : toExpand) {
            this.expand(s, marks, targetStates);
        }
    }

    /**
     * @param state
     * @param toCheck
     * @return
     */
    private boolean isInstance(DecisionState state, DecisionState toCheck) {
        if (state == toCheck) {
            return false;
        }
        envLoop: for (EnvironmentState eState : state.children) {
            for (EnvironmentState eToCheck : toCheck.children) {
                if (eToCheck.currentEnvironmentState == eState.currentEnvironmentState) {
                    behLoop: for (BehaviourState bState : eState.children) {
                        for (BehaviourState bToCheck : eToCheck.children) {
                            if (Arrays.equals(bToCheck.currentBehaviourStates, bState.currentBehaviourStates)) {
                                continue behLoop;
                            }
                        }
                        return false;
                    }
                    continue envLoop;
                }
            }
            return false;
        }
        return true;
    }

    private class DecisionState {
        
        private int number;
        private int behaviour;
        private int currentTargetState;
        private Set<EnvironmentState> children;
        private DecisionState instanceFather;
        
        private DecisionState(int behaviour, int target) {
            this.number = BehaviourSimulator.stateNumber++;
            this.currentTargetState = target;
            this.behaviour = behaviour;
            this.children = new LinkedHashSet<EnvironmentState>();
            this.instanceFather = null;
        }
        
        /**
         * 
         */
        public void removeInstance() {
            this.instanceFather = null;
        }

        private boolean isInstanceChild() {
            return this.instanceFather != null;
        }
        
        public String toString() {
            if (this.isInstanceChild()) {
                return "" + this.number + ": INSTANCE with " + this.behaviour + " to " + this.instanceFather.currentTargetState;
            }
            return "" + this.number + ": With " + this.behaviour + " to " + this.currentTargetState;
        }
        
    }
    
    private class EnvironmentState {
        
        private int currentEnvironmentState;
        private Set<BehaviourState> children;
        
        private EnvironmentState(int environmentState) {
            this.currentEnvironmentState = environmentState;
            this.children = new LinkedHashSet<BehaviourState>();
        }
        
        public String toString() {
            return "Environment " + this.currentEnvironmentState;
        }
        
    }
    
    private class BehaviourState {
        
        private int[] currentBehaviourStates;
        private Set<ActionState> children;
        
        private BehaviourState(int[] states) {
            this.currentBehaviourStates = states;
            this.children = new LinkedHashSet<ActionState>();
        }
        
        public String toString() {
            String res = "Behaviours ";
            for (int i = 0; i < this.currentBehaviourStates.length; i++) {
                res += this.currentBehaviourStates[i] + ",";
            }
            return res.substring(0, res.length()-1);
        }
        
    }
    
    private class ActionState {
        
        private Action action;
//        private Set<State> restriction; TODO restrictions
        private int targetToState;
        private Set<DecisionState> children;
        
        private ActionState(Action action, int targetToState) {
            this.action = action;
            this.targetToState = targetToState;
//            this.restriction = null;
            this.children = new LinkedHashSet<DecisionState>();
        }
        
        public String toString() {
            return "Action " + this.action.toString();
        }
        
    }
    
    private class ComputationState {
        
        private int[] currentStates;
        private int currentEnvironmentState;
        private DecisionState state;
        
        private ComputationState(int[] states, int envState, DecisionState s) {
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
    
    private class SetNode extends DecisionTree {
        
        private Set<Pair<Pair<Integer, Integer>, Set<ComputationState>>> states;

        private SetNode(Set<Pair<Pair<Integer, Integer>, Set<ComputationState>>> states) {
            this.states = states;
        }
        
    }
    
    private class DecisionStateSet extends LinkedHashSet<DecisionState> {

        /**
         * 
         */
        private static final long serialVersionUID = -5418585859690999714L;
        private int targetState;
        
        private DecisionStateSet(int targetState) {
            super();
            this.targetState = targetState;
        }
        
        public boolean add(DecisionState s) {
            if (s.currentTargetState == this.targetState) {
                return super.add(s);
            }
            return false;
        }
        
        public boolean addAll(Collection<? extends DecisionState> c) {
            boolean res = false;
            for (DecisionState s : c) {
                this.add(s);
            }
            return res;
        }
        
    }

	
}
