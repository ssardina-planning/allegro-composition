package behaviourComposition.structure;

import java.util.*;

import behaviourComposition.structure.*;
import structures.*;

/**
 * TransitionSystem.<br><br>
 * 
 * Created: 28.04.2008<br>
 * Last modified: 28.04.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class TransitionSystem extends AbstractTransitionSystem implements DeepCopiable<TransitionSystem> {

    
    
    private static final int STANDARD_START_CAPACITY = 4096;
//    
//    private int numberOfSameTransitions;
//    private int numberOfStates;
//    private State[] states;
//    private Action[][][] transitions;
    private ArraySetList<State> states;
    protected LabelList[][] transitions;
    private int length;
    private int size=0;
    
//------------------- constructors --------------------
    
    public TransitionSystem() {
        this(TransitionSystem.STANDARD_START_CAPACITY);
    }
    
    public TransitionSystem(Collection<String> states) {
        this((int)Math.pow(Math.ceil(Math.log(states.size())/Math.log(2)),2));
        this.addAllStates(states);
//        this((states.size() / 2 + 1) * 2);
//        this.addAllStates(states);
    }
    
    public TransitionSystem(int startCapacity) {
        this.states = new ArraySetList<State>();
        this.transitions = new LabelList[startCapacity][startCapacity];
        this.length = startCapacity;
//        this.numberOfSameTransitions = 1;
//        this.transitions = new Action[startCapacity][startCapacity][this.numberOfSameTransitions];
//        this.states = new State[startCapacity];
//        this.numberOfStates = 0;
    }
    
    
//------------------ public methods -------------------
    
    public List<String> addAllStates(Collection<String> toAdd) {
        List<String> errors = new ArrayList<String>();
        for (String s : toAdd) {
            try {
                this.addState(s);
                size++;
            } catch (IllegalArgumentException e) {
                errors.add(s);
            }
        }
        return errors;
//        Set<State> states = this.getStates();
//        Set<String> used = new LinkedHashSet<String>();
//        for (State s : states) {
//            used.add(s.getName());
//        }
//        for (String name : toAdd) {
//            if (used.contains(name)) {
//                throw new IllegalArgumentException(name + " is an already existing or doubled state name!");
//            }
//            used.add(name);
//        }
//        this.ensureCapacity(this.numberOfStates + toAdd.size());
//        for (String name : toAdd) {
//            this.states[this.numberOfStates++] = new State(name);
//        }
    }
    
    public int addState() {
        int num = this.getNumberOfStates(),
            res = -1;
        boolean notAdded = true;
        while (notAdded) {
            try {
                res = this.addState("" + num);
                size++;
                notAdded = false;
            } catch (IllegalArgumentException e) {
                num++;
            }
        }
        return res;
    }
    
    public int addState(String name) {
        if (this.containsState(name)) {
            throw new IllegalArgumentException("Name already exists!");
        }
        this.ensureCapacity(this.getNumberOfStates() + 1);
        int res = this.getNumberOfStates();
        this.states.add(new State(name));
        size++;
        return res;
//        if (this.containsState(name)) {
//            throw new IllegalArgumentException("Name already exists!");
//        }
//        this.ensureCapacity(this.numberOfStates + 1);
//        int res = this.numberOfStates;
//        this.states[this.numberOfStates++] = new State(name);
//        return res;
    }
    
    public Set<Integer> addStates(int number) {
        Set<Integer> res = new LinkedHashSet<Integer>();
        for (int i = 0; i < number; i++) {
            res.add(this.addState());
        }
        return res;
    }
    
    public Transition addTransition(int from, int to, Action action) {
        this.checkTransition(from, to);
        if (action == null) {
            throw new NullPointerException("Action may not be null!");
        }
        Label label = this.createLabel(action);
        List<Label> transitions = this.transitions[from][to];
        int number = transitions.size();
        transitions.add(label);
        return new Transition(from, this.getStateName(from), to, this.getStateName(to), label, number);
//      this.checkStateNumber(from);
//      this.checkStateNumber(to);
//      if (action == null) {
//          throw new NullPointerException("Action may not be null!");
//      }
//      boolean notAdded = true;
//      for (int i = 0; i < this.numberOfSameTransitions; i++) {
//          if (this.transitions[from][to][i] == null) {
//              this.transitions[from][to][i] = action;
//              notAdded = false;
//              break;
//          }
//      }
//      if (notAdded) {
//          increaseNumberOfSameTransitions();
//          this.transitions[from][to][this.numberOfSameTransitions-1] = action;
//      }
//      return new Transition(from, this.getStateName(from), to, this.getStateName(to), action);
    }
    


    /**
     * @param state
     * @param state2
     * @param action
     */
    public Transition addTransition(State from, State to, Action action) {
        return this.addTransition(this.getStateNumber(from), this.getStateNumber(to), action);
    }
    
    public Transition addTransition(String from, String to, Action action) {
        return this.addTransition(this.getStateNumber(from), this.getStateNumber(to), action);
    }
    
    public Transition addTransition(Transition t) {
        return this.addTransition(t.getFrom(), t.getTo(), t.label.action);
//        return this.addTransition(t.from, t.to, t.action);
    }
    
    /**
     * @param system
     */
    public void changeTo(TransitionSystem system) {
        this.length = system.length;
        this.states = system.states;
        this.transitions = system.transitions;
//        this.numberOfStates = system.numberOfStates;
//        this.states = system.states;
//        this.transitions = system.transitions;
    }
    
    public void checkCapacity() {
        this.ensureCapacity(this.getNumberOfStates());
    }
    
    public boolean containsState(State state) {
        return this.states.contains(state);
    }
    
    public boolean containsState(String name) {
        for (int i = 0; i < this.getNumberOfStates(); i++) {
            if (this.states.get(i).getName().equals(name)) {
                return true;
            }
        }
        return false;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            if (this.states[i].name.equals(name)) {
//                return true;
//            }
//        }
//        return false;
    }
    
    protected TransitionSystem create() {
        return new TransitionSystem(0);
    }
    
    /**
     * @return
     */
    public TransitionSystem deepCopy() {
        TransitionSystem res = this.create();
        res.length = this.length;
        res.allocateNewArrays(this.length);
        for (int i = 0; i < this.getNumberOfStates(); i++) {
            res.states.add(this.states.get(i).deepCopy());
            for (int j = 0; j < this.getNumberOfStates(); j++) {
                LabelList transitions = this.transitions[i][j];
                if (transitions != null) {
                    LabelList newTransitions = new LabelList();
                    for (Label l : transitions) {
                        newTransitions.add(l.deepCopy());
                    }
                    res.transitions[i][j] = newTransitions;
                }
            }
        }
        return res;
//        TransitionSystem res = new TransitionSystem(0);
//        res.numberOfSameTransitions = this.numberOfSameTransitions;
//        res.allocateNewArrays(this.states.length);
//        res.numberOfStates = this.numberOfStates;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            res.states[i] = this.states[i];
//            for (int j = 0; j < this.numberOfStates; j++) {
//                for (int k = 0; k < this.numberOfSameTransitions; k++) {
//                    res.transitions[i][j][k] = this.transitions[i][j][k] == null ? null : new Action(this.transitions[i][j][k].getName());
//                }
//            }
//        }
//        return res;
    }

    public void ensureCapacity(int numberOfStates) {
        int currentLength = this.length,
            arrayLength = currentLength;
        while (numberOfStates > arrayLength) {
            arrayLength *= 2;
        }
        if (arrayLength > currentLength) {
            this.allocateNewArrays(arrayLength);
        }
    }
    
    public boolean equals(Object o) {
        if (o instanceof TransitionSystem) {
            TransitionSystem sys = (TransitionSystem)o;
            if (sys.states.equals(this.states)) {
                for (int i = 0; i < this.getNumberOfStates(); i++) {
                    for (int j = 0; j < this.getNumberOfStates(); j++) {
                        List<Label> thisList = this.transitions[i][j],
                                    sysList = sys.transitions[i][j];
                        if (thisList == null) {
                            if (sysList != null && !sysList.isEmpty()) {
                                return false;
                            }
                        } else {
                            if (thisList.size() != sysList.size()) {
                                return false;
                            }
                            for (int k = 0; k < thisList.size(); k++) {
                                if (!thisList.get(k).equals(sysList.get(k))) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
//        if (o instanceof TransitionSystem) {
//            TransitionSystem sys = (TransitionSystem)o;
//            if (sys.numberOfStates == this.numberOfStates && sys.numberOfSameTransitions == this.numberOfSameTransitions) {
//                for (int i = 0; i < this.numberOfStates; i++) {
//                    if (!sys.states[i].equals(this.states[i])) {
//                        return false;
//                    }
//                    for (int j = 0; j < this.numberOfStates; j++) {
//                        for (int k = 0; k < this.numberOfSameTransitions; k++) {
//                            Action thisAction = this.transitions[i][j][k],
//                                   sysAction = sys.transitions[i][j][k];
//                            if (thisAction == null) {
//                                if (sysAction != null) {
//                                    return false;
//                                }
//                            } else if (sysAction == null || !thisAction.equals(sysAction)) {
//                                return false;
//                            }
//                        }
//                    }
//                }
//                return true;
//            }
//        }
//        return false;
    }

    public List<Transition> getAllTransitions(int from, int to) {
        List<Transition> res = new ArrayList<Transition>();
        List<Label> transitions = this.transitions[from][to];
        if (transitions != null) {
            for (int i = 0; i < transitions.size(); i++) {
                res.add(new Transition(from, this.states.get(from).getName(), to, this.states.get(to).getName(), transitions.get(i), i));
            }
        }
        return res;
//        Set<Transition> res = new LinkedHashSet<Transition>();
//        for (int i = 0; i < this.numberOfSameTransitions; i++) {
//            Action a = this.transitions[from][to][i];
//            if (a == null) {
//                break;
//            }
//            res.add(new Transition(from, this.states[from].name, to, this.states[to].name, a));
//        }
//        return res;
    }

    public List<Transition> getAllTransitionsWithAction(int from, int to, Action action) {
        List<Transition> res = new ArrayList<Transition>();
        List<Label> transitions = this.transitions[from][to];
        if (transitions != null) {
            for (int i = 0; i < transitions.size(); i++) {
                Label l = transitions.get(i);
                if (l.action.equals(action)) {
                    res.add(new Transition(from, this.states.get(from).getName(), to, this.states.get(to).getName(), l, i));
                }
            }
        }
        return res;
//        Set<Transition> res = new LinkedHashSet<Transition>();
//        for (int i = 0; i < this.numberOfSameTransitions; i++) {
//            Action a = this.transitions[from][to][i];
//            if (a == null) {
//                break;
//            }
//            if (a.equals(action)) {
//                res.add(new Transition(from, this.states[from].name, to, this.states[to].name, a));
//            }
//        }
//        return res;
    }

    public List<Pair<Integer, Transition>> getAllNumberedTransitionsWithAction(int from, int to, Action action) {
        List<Pair<Integer, Transition>> res = new ArrayList<Pair<Integer, Transition>>();
        List<Label> transitions = this.transitions[from][to];
        if (transitions != null) {
            for (int i = 0; i < transitions.size(); i++) {
                Label l = transitions.get(i);
                if (l.action.equals(action)) {
                    res.add(new Pair<Integer, Transition>(i, new Transition(from, this.states.get(from).getName(), to, this.states.get(to).getName(), l, i)));
                }
            }
        }
        return res;
    }
    
    public State getDeterministicStartState() {
        Set<State> starts = this.getStartStates();
        if (starts.size() == 1) {
            return starts.iterator().next();
        }
        return null;
    }
    
    public int getDeterministicStartStateNumber() {
        Set<Integer> starts = this.getStartStateNumbers();
        if (starts.size() == 1) {
            return starts.iterator().next();
        }
        return -1;
    }
    
    public Set<Integer> getFinalStateNumbers() {
        Set<Integer> res = new LinkedHashSet<Integer>();
        for (int i = 0; i < this.getNumberOfStates(); i++) {
            if (this.states.get(i).isFinalState()) {
                res.add(i);
            }
        }
        return res;
//        Set<Integer> res = new LinkedHashSet<Integer>();
//        for (int i = 0; i < this.numberOfStates; i++) {
//            State state = this.states[i];
//            if (state.isFinalState()) {
//                res.add(i);
//            }
//        }
//        return res;
    }
    
    public Set<State> getFinalStates() {
        return new LinkedHashSet<State>(FilteredIterator.filterCollection(this.states, new Filter<State>() {

            public boolean filterOut(State t) {
                return !t.isFinalState();
            }
            
        }));
//        Set<State> res = new LinkedHashSet<State>();
//        for (int i = 0; i < this.numberOfStates; i++) {
//            State state = this.states[i];
//            if (state.isFinalState()) {
//                res.add(state);
//            }
//        }
//        return res;
    }
    
//    public int getMaxNumberOfSameTransitions() {
//        return this.numberOfSameTransitions;
//    }
    
    public int getNumberOfStates() {
        return this.states.size();
//        return this.numberOfStates;
    }
    
    public List<Transition> getOutgoingTransitions(int stateNumber) {
        this.checkStateNumber(stateNumber);
        List<Transition> res = new ArrayList<Transition>();
        String stateName = this.states.get(stateNumber).getName();
        for (int i = 0; i < /*this.getNumberOfStates()*/size; i++) {
            List<Label> transitions = this.transitions[stateNumber][i];
            if (transitions != null) {
                for (int j = 0; j < transitions.size(); j++) {
                    res.add(new Transition(stateNumber, stateName, i, this.states.get(i).getName(), transitions.get(j), j));
                }
            }
        }
        return res;
//        this.checkStateNumber(stateNumber);
//        Set<Transition> res = new LinkedHashSet<Transition>();
//        String stateName = this.states[stateNumber].name;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            innerLoop: for (int j = 0; j < this.numberOfSameTransitions; j++) {
//                Action a = this.transitions[stateNumber][i][j];
//                if (a == null) {
//                    break innerLoop;
//                }
//                res.add(new Transition(stateNumber, stateName, i, this.states[i].name, a));
//            }
//        }
//        return res;
    }
    
    /**
     * @param state
     * @return
     */
    public List<Transition> getOutgoingTransitions(IState state) {
        return this.getOutgoingTransitions(this.getStateNumber(state));
    }

    public List<Transition> getOutgoingTransitionsWithAction(int stateNumber, Action action) {
        this.checkStateNumber(stateNumber);
        List<Transition> res = new ArrayList<Transition>();
        String stateName = this.states.get(stateNumber).getName();
        for (int i = 0; i < size /*this.getNumberOfStates()*/; i++) {
            List<Label> transitions = this.transitions[stateNumber][i];
            if (transitions != null) {
                for (int j = 0; j < transitions.size(); j++) {
                    Label l = transitions.get(j);
                    if (l.action.equals(action)) {
                        res.add(new Transition(stateNumber, stateName, i, this.states.get(i).getName(), l, j));
                    }
                }
            }
        }
        return res;
//        this.checkStateNumber(stateNumber);
//        Set<Transition> res = new LinkedHashSet<Transition>();
//        String stateName = this.states[stateNumber].name;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            innerLoop: for (int j = 0; j < this.numberOfSameTransitions; j++) {
//                Action a = this.transitions[stateNumber][i][j];
//                if (a == null) {
//                    break innerLoop;
//                }
//                if (a.equals(action)) {
//                    res.add(new Transition(stateNumber, stateName, i, this.states[i].name, a));
//                }
//            }
//        }
//        return res;
    }
    
    /**
     * @param state
     * @return
     */
    public List<Transition> getOutgoingTransitionsWithAction(State state, Action action) {
        return this.getOutgoingTransitionsWithAction(this.getStateNumber(state), action);
    }

    public Set<Integer> getStartStateNumbers() {
        Set<Integer> res = new LinkedHashSet<Integer>();
        for (int i = 0; i < this.getNumberOfStates(); i++) {
            if (this.states.get(i).isStartState()) {
                res.add(i);
            }
        }
        return res;
//        Set<Integer> res = new LinkedHashSet<Integer>();
//        for (int i = 0; i < this.numberOfStates; i++) {
//            State state = this.states[i];
//            if (state.isStartState()) {
//                res.add(i);
//            }
//        }
//        return res;
    }
    
    public Set<State> getStartStates() {
        return new LinkedHashSet<State>(FilteredIterator.filterCollection(this.states, new Filter<State>() {

            public boolean filterOut(State t) {
                return !t.isStartState();
            }
            
        }));
//        Set<State> res = new LinkedHashSet<State>();
//        for (int i = 0; i < this.numberOfStates; i++) {
//            State state = this.states[i];
//            if (state.isStartState()) {
//                res.add(state);
//            }
//        }
//        return res;
    }
    
    public State getState(int stateNumber) {
        return this.states.get(stateNumber);
//        this.checkStateNumber(stateNumber);
//        return this.states[stateNumber];
    }
    
    public String getStateName(int stateNumber) {
        return this.states.get(stateNumber).getName();
//        this.checkStateNumber(stateNumber);
//        return this.states[stateNumber].getName();
    }

    /**
     * @param state
     * @return
     */
    public int getStateNumber(IState state) {
        return this.states.indexOf(state);
//        for (int i = 0; i < this.numberOfStates; i++) {
//            if (this.states[i].equals(state)) {
//                return i;
//            }
//        }
//        return -1;
    }
    
    public int getStateNumber(String name) {
        for (int i = 0; i < this.states.size(); i++) {
            if (this.states.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            if (this.states[i].name.equals(name)) {
//                return i;
//            }
//        }
//        return -1;
    }
    
    public Set<Integer> getStateNumbers() {
        Set<Integer> res = new LinkedHashSet<Integer>();
        for (int i = 0; i < this.states.size(); i++) {
            res.add(i);
        }
        return res;
//        Set<Integer> res = new LinkedHashSet<Integer>();
//        for (int i = 0; i < this.numberOfStates; i++) {
//            res.add(i);
//        }
//        return res;
    }
    
    public ArraySetList<State> getStates() {
        return this.states;
//        Set<State> res = new LinkedHashSet<State>();
//        for (int i = 0; i < this.numberOfStates; i++) {
//            res.add(this.states[i]);
//        }
//        return res;
    }

//    public Action getTransition(int from, int to) {
//        return this.getTransition(from, to, 0);
//    }

    public Action getTransition(int from, int to, int number) {
        this.checkTransitionNumber(from, to, number);
        return this.transitions[from][to].get(number).action;
//        this.checkStateNumber(from);
//        this.checkStateNumber(to);
//        this.checkTransitionNumber(number);
//        return this.transitions[from][to][number];
    }

    /**
     * @param from
     * @param to
     * @param number
     */
    private void checkTransitionNumber(int from, int to, int number) {
        this.checkTransition(from, to);
        if (number < 0 || number >= this.transitions[from][to].size()) {
            throw new IllegalArgumentException("Transition number does not exist!");
        }
    }

    public List<Transition> getTransitions() {
        List<Transition> res = new ArrayList<Transition>();
        for (int i = 0; i < this.getNumberOfStates(); i++) {
            for (int j = 0; j < this.getNumberOfStates(); j++) {
                List<Label> transitions = this.transitions[i][j];
                if (transitions != null) {
                    for (int k = 0; k < transitions.size(); k++) {
                        res.add(new Transition(i, this.getStateName(i), j, this.getStateName(j), transitions.get(k), k));
                    }
                }
            }
        }
        return res;
//        Set<Transition> res = new LinkedHashSet<Transition>();
//        for (int i = 0; i < this.numberOfStates; i++) {
//            for (int j = 0; j < this.numberOfStates; j++) {
//                for (int k = 0; k < this.numberOfSameTransitions; k++) {
//                    Action action = this.transitions[i][j][k];
//                    if (action != null) {
//                        res.add(new Transition(i, this.getStateName(i), j, this.getStateName(j), action));
//                    }
//                }
//            }
//        }
//        return res;
    }

    public boolean hasOutgoingTransitionWithAction(int stateNumber, Action action) {
        for (int i = 0; i < this.getNumberOfStates(); i++) {
            List<Label> transitions = this.transitions[stateNumber][i];
            if (transitions != null) {
                for (Label l : transitions) {
                    if (l.action.equals(action)) {
                        return true;
                    }
                }
            }
        }
        return false;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            for (int j = 0; j < this.numberOfSameTransitions; j++) {
//                if (this.transitions[stateNumber][i][j].equals(action)) {
//                    return true;
//                }
//            }
//        }
//        return false;
    }

    public boolean hasTransition(int from, int to) {
        this.checkTransition(from, to);
        LabelList transitions = this.transitions[from][to];
        return !transitions.isEmpty();
//        return this.transitions[from][to][0] != null;
    }
    
    /**
     * @param from
     * @param to
     */
    private void checkTransition(int from, int to) {
        this.checkStateNumber(from);
        this.checkStateNumber(to);
        if (this.transitions[from][to] == null) {
            this.transitions[from][to] = new LabelList();
        }
    }

    /**
     * @return
     */
    public boolean isDeterministic() {
        if (this.getStartStates().size() > 1) {
            return false;
        }
        for (int i = 0; i < this.getNumberOfStates(); i++) {
            List<Action> used = new ArrayList<Action>();
            for (int j = 0; j < this.getNumberOfStates(); j++) {
                List<Label> transitions = this.transitions[i][j];
                if (transitions != null) {
                    for (Label l : transitions) {
                        if (used.contains(l.action)) {
                            return false;
                        } else {
                            used.add(l.action);
                        }
                    }
                }
            }
        }
        return true;
//        boolean startFound = false;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            if (this.states[i].isStartState()) {
//                if (startFound) {
//                    return false;
//                } else {
//                    startFound = true;
//                }
//            }
//            Set<Action> used = new LinkedHashSet<Action>();
//            for (int j = 0; j < this.numberOfStates; j++) {
//                innerLoop: for (int k = 0; k < this.numberOfSameTransitions; k++) {
//                    Action a = this.transitions[i][j][k];
//                    if (a == null) {
//                        break innerLoop;
//                    }
//                    if (used.contains(a)) {
//                        return false;
//                    } else {
//                        used.add(a);
//                    }
//                }
//            }
//        }
//        return true;
    }

    /**
     * @param currentTargetState
     * @return
     */
    public boolean isFinalState(int stateNumber) {
        this.checkStateNumber(stateNumber);
        return this.states.get(stateNumber).isFinalState();
//        this.checkStateNumber(stateNumber);
//        return this.states[stateNumber].isFinalState();
    }

    /**
     * @param stateNumber
     */
    public void removeState(int stateNumber) {
        this.checkStateNumber(stateNumber);
        this.states.remove(stateNumber);
        int numberOfStates = this.getNumberOfStates();
        for (int i = 0; i < numberOfStates; i++) {
            if (i >= stateNumber) {
                this.transitions[i] = this.transitions[i+1];
            }
            for (int j = stateNumber; j < numberOfStates; j++) {
                this.transitions[i][j] = this.transitions[i][j+1];
            }
            this.transitions[i][numberOfStates] = new LabelList();
        }
        this.transitions[numberOfStates] = new LabelList[this.length];
//        this.checkStateNumber(stateNumber);
//        this.numberOfStates--;
//        for (int i = 0; i < this.numberOfStates; i++) {
//            if (i >= stateNumber) {
//                this.states[i] = this.states[i+1];
//                this.transitions[i] = this.transitions[i+1];
//            }
//            for (int j = stateNumber; j < this.numberOfStates; j++) {
//                this.transitions[i][j] = this.transitions[i][j+1];
//            }
//            this.transitions[i][this.numberOfStates] = new Action[this.states.length];
//        }
//        this.states[this.numberOfStates] = null;
//        this.transitions[this.numberOfStates] = new Action[this.states.length][this.states.length];
    }

    /**
     * @param state
     */
    public void removeState(State state) {
        this.removeState(this.getStateNumber(state));
    }
    
    /**
     * @param string
     */
    public void removeState(String name) {
        this.removeState(this.getStateNumber(name));
    }
    
//    public void removeTransition(int from, int to) {
//        this.removeTransition(from, to, 0);
//    }

//    /**
//     * @param stateNumber
//     * @param stateNumber2
//     * @param action
//     */
//    public void removeTransition(int from, int to, Action action) {
//        this.checkStateNumber(from);
//        this.checkStateNumber(to);
//        if (action == null) {
//            throw new NullPointerException("Action must not be null!");
//        }
//        if (this.numberOfSameTransitions > 1) {
//            this.removeTransition(from, to, this.findTransitionNumber(from, to, action));
//        } else {
//            if (action.equals(this.transitions[from][to][0])) {
//                this.transitions[from][to][0] = null;
//            }
//        }
//    }

    /**
     * @param from
     * @param to
     * @param i
     */
    public void removeTransition(int from, int to, int number) {
        this.checkTransitionNumber(from, to, number);
        this.transitions[from][to].remove(number);
//        this.checkStateNumber(from);
//        this.checkStateNumber(to);
//        this.checkTransitionNumber(number);
//        boolean full = this.transitions[from][to][this.numberOfSameTransitions-1] != null;
//        for (int i = number; i < this.numberOfSameTransitions-1; i++) {
//            this.transitions[from][to][i] = this.transitions[from][to][i+1];
//        }
//        this.transitions[from][to][this.numberOfSameTransitions-1] = null;
//        if (this.numberOfSameTransitions > 1 && full) {
//            this.checkForDecreaseOfMaxTransitionNumber();
//        }
    }

    /**
     * @param transition
     */
    public void removeTransition(Transition transition) {
        this.removeTransition(this.getStateNumber(transition.getFrom()), this.getStateNumber(transition.getTo()), transition.getToNumber());
//        this.removeTransition(this.getStateNumber(transition.from), this.getStateNumber(transition.to), transition.action);
    }

    public void removeTransitions(int from, int to) {
        this.checkTransition(from, to);
        this.transitions[from][to].clear();
//        this.checkStateNumber(from);
//        this.checkStateNumber(to);
//        if (this.numberOfSameTransitions > 1) {
//            for (int i = 0; i < this.numberOfSameTransitions - 1; i++) {
//                this.transitions[from][to][i] = null;
//            }
//            if (this.transitions[from][to][this.numberOfSameTransitions-1] != null) {
//                this.transitions[from][to][this.numberOfSameTransitions-1] = null;
//                checkForDecreaseOfMaxTransitionNumber();
//            }
//        } else {
//            this.transitions[from][to][0] = null;
//        }
    }
    
    public void setFinalState(int stateNumber) {
        this.setFinalState(stateNumber, true);
    }

    public void setFinalState(int stateNumber, boolean fin) {
        this.checkStateNumber(stateNumber);
        this.states.get(stateNumber).setFinal(fin);
//        this.checkStateNumber(stateNumber);
//        this.states[stateNumber].setFinal(fin);
    }

    public void setFinalState(String state, boolean fin) {
        this.setFinalState(this.getStateNumber(state), fin);
    }

    public void setStartState(int stateNumber) {
        this.setStartState(stateNumber, true);
    }

    public void setStartState(int stateNumber, boolean start) {
        this.checkStateNumber(stateNumber);
        this.states.get(stateNumber).setStart(start);
//        this.checkStateNumber(stateNumber);
//        this.states[stateNumber].setStart(start);
    }

    public void setStartState(String state, boolean start) {
        this.setStartState(this.getStateNumber(state), start);
    }

    /**
     * @param stateNumber
     */
    public void toggleFinalState(int stateNumber) {
        this.checkStateNumber(stateNumber);
        this.states.get(stateNumber).toggleFinal();
//        this.checkStateNumber(stateNumber);
//        this.states[stateNumber].toggleFinal();
    }
    
    public void toggleStartState(int stateNumber) {
        this.checkStateNumber(stateNumber);
        this.states.get(stateNumber).toggleStart();
//        this.checkStateNumber(stateNumber);
//        this.states[stateNumber].toggleStart();
    }
    
    public HashSet<State> getNDStates()
    {
    	HashSet<State> ndstates = new HashSet<State>();
		//step1 find the ND state
		for(State state:getStates())
		{
			List<Transition> transitions = getOutgoingTransitions(getStateNumber(state));
			for(Transition t:transitions)
			{
				List<State> states = getToStates(t, state);
				if(states.size()>1)
				{
					ndstates.add(state);
				}
			}
		}
		return ndstates;
    }
//---------------- private methods --------------
    
    /**
     * @param i
     */
    private void allocateNewArrays(int arrayLength) {
        int limit = this.getNumberOfStates();
        LabelList[][] newTransitions = new LabelList[arrayLength][arrayLength];
        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < limit; j++) {
                newTransitions[i][j] = this.transitions[i][j];
            }
        }
        this.transitions = newTransitions;
//        int limit = this.numberOfStates;
//        Action[][][] newTransitions = new Action[arrayLength][arrayLength][this.numberOfSameTransitions];
//        State[] newStates = new State[arrayLength];
//        for (int i = 0; i < limit; i++) {
//            newStates[i] = this.states[i];
//            for (int j = 0; j < limit; j++) {
//                for (int k = 0; k < this.numberOfSameTransitions-1; k++) {
//                    newTransitions[i][j][k] = this.transitions[i][j][k];
//                }
//            }
//        }
//        this.states = newStates;
//        this.transitions = newTransitions;
    }

//    /**
//     * 
//     */
//    private void checkForDecreaseOfMaxTransitionNumber() {
//        for (int i = 0; i < this.numberOfStates; i++) {
//            for (int j = 0; j < this.numberOfStates; j++) {
//                if (this.transitions[i][j][this.numberOfSameTransitions-1] != null) {
//                    return;
//                }
//            }
//        }
//        this.numberOfSameTransitions--;
//        this.allocateNewArrays(this.states.length);
//    }
    
    /**
     * @param from
     */
    private void checkStateNumber(int number) {
        if (number < 0 || number >= this.getNumberOfStates()) {
            throw new IllegalArgumentException("Undefined state!");
        }
    }
    
//    /**
//     * @param number
//     */
//    private void checkTransitionNumber(int number) {
//        if (number < 0 || number >= this.numberOfSameTransitions) {
//            throw new IllegalArgumentException("Undefined transition!");
//        }
//    }
    
//    /**
//     * @param from
//     * @param to
//     * @param action
//     * @return
//     */
//    private int findTransitionNumber(int from, int to, Action action) {
//        for (int i = 0; i < this.numberOfSameTransitions; i++) {
//            if (action.equals(this.transitions[from][to][i])) {
//                return i;
//            }
//        }
//        return -1;
//    }
    
//    /**
//     * 
//     */
//    private void increaseNumberOfSameTransitions() {
//        this.numberOfSameTransitions++;
//        this.allocateNewArrays(this.states.length);
//    }
    /** function added for debugging and string output
     * */
    public String toString()
    {
    	StringBuilder sb = new StringBuilder();
    	sb.append("--- States ---\n");
    	for(State s:states)
    	{
    		sb.append(s.toString()+"\n");
    	}
    	sb.append("--- Transitions --- \n");
    	for(Transition t:getTransitions())
    	{
    		sb.append(t.toString()+"\n");
    	}
    	return sb.toString();
    }
 
    /**
     * 
     */
    public void clear() {
        this.states.clear();
        this.transitions = new LabelList[TransitionSystem.STANDARD_START_CAPACITY][TransitionSystem.STANDARD_START_CAPACITY];
        this.length = TransitionSystem.STANDARD_START_CAPACITY;
//        this.numberOfSameTransitions = 1;
//        this.numberOfStates = 0;
//        this.states = new State[TransitionSystem.STANDARD_START_CAPACITY];
//        this.transitions = new Action[TransitionSystem.STANDARD_START_CAPACITY][TransitionSystem.STANDARD_START_CAPACITY][1];
    }
    /**
     * @param action
     * @return
     */
    protected Label createLabel(Action action) {
        return new Label(action);
    }

	@Override
	public List<State> getToStates(ITransition trans, IState behaviourState) {
		List<State> states = new ArrayList<State>();
		for(Transition t:getOutgoingTransitionsWithAction((State)behaviourState, ((Transition)trans).getAction()))
		{
			states.add(getState(getStateNumber(t.getTo())));
		}
		return states;
	}
	public List<Integer> getToStateNumbers(Transition trans, State behaviourState)
	{
		List<Integer> states = new ArrayList<Integer>();
		for(Transition t:getOutgoingTransitionsWithAction((State)behaviourState, ((Transition)trans).getAction()))
		{
			states.add(t.getToNumber());
		}
		return states;
	}
	public List<State> getToStates(State st, Action action) {
		List<State> toStates = new ArrayList<State>();
		for(Transition t:getOutgoingTransitionsWithAction(getStateNumber(st), action))
		{
			toStates.add(getState(t.getToNumber()));
		}
		return toStates;
	}
	@Override
	public AbstractState getState(String to) {
		for(State s:states)
		{
			if(s.getName().equals(to))
			{
				return s;
			}
		}
		return null;
	}
   
}
