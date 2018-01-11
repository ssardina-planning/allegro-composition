package behaviourComposition.ai;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import structures.ArraySetList;

import behaviourComposition.structure.*;
public class BehaviourSimulatorMorpheus {

private List<Behaviour> availableBehaviours;
private TransitionSystem environment;
private Behaviour targetBehaviour; 
private int numberOfBehaviours;
private int targetStartState;
private Set<Integer> environmentStartStates;
private List<Set<Integer>> behaviourStartStates;
private HashMap<List<Integer>,ArrayList<SimulationLink>> linksMap;
private HashSet<SimulationLink> links;
private HashMap<List<Integer>,StateLinks> system;
private HashMap<List<Integer>,StateLinks> target;
Set<ArrayList<Integer>> sysinitialstates = new HashSet<ArrayList<Integer>>();
Set<ArrayList<Integer>> tarinitialstates = new HashSet<ArrayList<Integer>>();
 Set<ArrayList<Integer>> behStartStates = new HashSet<ArrayList<Integer>>();
public BehaviourSimulatorMorpheus(List<Behaviour> availableBehaviours, TransitionSystem environment, Behaviour targetBehaviour) throws NullPointerException, IllegalArgumentException {
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
        linksMap = new HashMap<List<Integer>, ArrayList<SimulationLink>>();
        links = new HashSet<SimulationLink>();
        
        system = new HashMap<List<Integer>, StateLinks>();
        target = new HashMap<List<Integer>, StateLinks>();
    }
	public void changeTarget(Behaviour tg)
	{
		this.targetBehaviour = tg;
		this.targetStartState = this.targetBehaviour.getDeterministicStartStateNumber();
        if (this.targetStartState == -1) {
            throw new IllegalArgumentException("Target behaviour has no start state!");
        }
        target = new HashMap<List<Integer>, StateLinks>();
        linksMap = new HashMap<List<Integer>, ArrayList<SimulationLink>>();
        links = new HashSet<SimulationLink>();
	}
    public void compute()
    {
    	//compute the synchronous product for enacted behaviours first
    	if(system.keySet().size()<1){
    		buildEnactedBehaviour(availableBehaviours,environment);
    	}
    	buildEnactedBehaviour(target,environment);
    	//all done lets start building the simulation links and iterate through them
    	// Step1: assume each system states simulates every target state
    	Set<List<Integer>> systemStates = system.keySet();
    	Set<List<Integer>> targetStates = target.keySet();
    	for(List<Integer> systemState:systemStates)
    	{
			ArrayList<SimulationLink> fromLinks = new ArrayList<SimulationLink>();
    		for(List<Integer> targetState:targetStates)
    		{
    			SimulationLink link = new SimulationLink(systemState,targetState);
    			links.add(link);
    			fromLinks.add(link);
    			linksMap.put(systemState, fromLinks);
    		}
    	}
    	
    	// Step2: iterate over each link and remove the illegal ones as per the simulation definition
    	boolean linkRemoved = true;
    	while(linkRemoved && !links.isEmpty())
    	{
    		linkRemoved = false;
    		List<SimulationLink> toRemove = new ArrayList<SimulationLink>();
    		for(SimulationLink link:links)
    		{
    			List<Integer> fromState = link.getFromState();
    			List<Integer> targetState = link.getSimulatedState();
     			if(!matchesFinalState(fromState,targetState))
    			{
    				toRemove.add(link);
    				linksMap.get(fromState).remove(link);
    				linkRemoved = true; 
    			}
    			else if(!simulates(fromState,targetState)) // can the system state do all what the target can do ?
    			{
    				toRemove.add(link);
    				linksMap.get(fromState).remove(link);
    				linkRemoved = true; 
    			}else if(!isNextStateInSimulation(fromState,targetState)) // can the system do an action and still remain in simulation ?
    			{
    				toRemove.add(link);
    				linksMap.get(fromState).remove(link);
    				linkRemoved = true;
    			}/*else
    			{
    				linkRemoved = false;
    			}*/
    		}
    		links.removeAll(toRemove);
    	}
    	//System.out.println("Total Links:"+links.size());
    }

	public void compute1()
    {
    	//compute the synchronous product for enacted behaviours first
		if(system.keySet().size()<1){
    		buildEnactedBehaviour(availableBehaviours,environment);
    	}
    	buildEnactedBehaviour(target,environment);
    	//all done lets start building the simulation links and iterate through them
    	
    	// Step1: assume each system states simulates every target state but add links only if the state can do all what target can do
    	Set<List<Integer>> systemStates = system.keySet();
    	Set<List<Integer>> targetStates = target.keySet();
    	for(List<Integer> systemState:systemStates)
    	{
			ArrayList<SimulationLink> fromLinks = new ArrayList<SimulationLink>();
    		for(List<Integer> targetState:targetStates)
    		{
    			if(simulates(systemState,targetState)) // can the system state do all what the target can do ?
    			{    			
    				if(matchesFinalState(systemState, targetState)){
		    			SimulationLink link = new SimulationLink(systemState,targetState);
		    			links.add(link);
		    			fromLinks.add(link);
		    			linksMap.put(systemState, fromLinks);
    				}
    			}
    		}
    	}
    	
    	// Step2: iterate over each link and remove the illegal ones as per the simulation definition
    	boolean linkRemoved = true;
    	while(linkRemoved && !links.isEmpty())
    	{
    		linkRemoved = false;
    		List<SimulationLink> toRemove = new ArrayList<SimulationLink>();
    		for(SimulationLink link:links)
    		{
    			List<Integer> fromState = link.getFromState();
    			List<Integer> targetState = link.getSimulatedState();
    			if(!isNextStateInSimulation(fromState,targetState)) // can the system do an action and still remain in simulation ?
    			{
    				toRemove.add(link);
    				linksMap.get(fromState).remove(link);
    				linkRemoved = true;
    			}/*else
    			{
    				linkRemoved = false;
    			}*/
    		}
    		links.removeAll(toRemove);
    	}
    	//System.out.println("Total Links:"+links.size());
    }
	/** the reverse check method to avoid checking all nextinsim links **/
	public void compute2()
    {
     	//compute the synchronous product for enacted behaviours first
		if(system.keySet().size()<1){
    		buildEnactedBehaviour(availableBehaviours,environment);
    	}
    	buildEnactedBehaviour(target,environment);
    	//all done lets start building the simulation links and iterate through them
    	
    	// Step1: assume each system states simulates every target state but add links only if the state can do all what target can do
    	Set<List<Integer>> systemStates = system.keySet();
    	Set<List<Integer>> targetStates = target.keySet();
    	
    	for(List<Integer> systemState:systemStates)
    	{
			ArrayList<SimulationLink> fromLinks = new ArrayList<SimulationLink>();
    		for(List<Integer> targetState:targetStates)
    		{
    			if(simulates(systemState,targetState)) // can the system state do all what the target can do ?
    			{    			
    				if(matchesFinalState(systemState, targetState)){
		    			SimulationLink link = new SimulationLink(systemState,targetState);
		    			links.add(link);
		    			fromLinks.add(link);
    				}
    			}
    		}
    		linksMap.put(systemState, fromLinks);
    	}
    	
    	// we do not need to check all links. we will check the parent links of the link removed
    	HashSet<SimulationLink> toCheck = (HashSet<SimulationLink>) links.clone();
    	while(!toCheck.isEmpty())
    	{
    		HashSet<SimulationLink> toRemove = new HashSet<SimulationLink>();
    		HashSet<SimulationLink> toAdd = new HashSet<SimulationLink>();
    		HashSet<SimulationLink> bad = new HashSet<SimulationLink>(); // need this so that we dont add the bad links again
    		for(SimulationLink link:toCheck)
    		{
    			List<Integer> fromState = link.getFromState();
    			List<Integer> targetState = link.getSimulatedState();   
    			if(!isNextStateInSimulation(fromState, targetState))
    			{
    				linksMap.get(fromState).remove(link);
    				links.remove(link);

    				HashSet<ArrayList<Integer>> fromSysStates = system.get(link.getFromState()).getFromStates();
    				HashSet<ArrayList<Integer>> fromtarStates = target.get(link.getSimulatedState()).getFromStates();
    				bad.add(link);
    				for(List<Integer> state:fromSysStates)
    				{
    					List<SimulationLink> links = linksMap.get(state);
    				    //toAdd.addAll(links);
    					for(SimulationLink l:links)
    				    {
    				    	if(fromtarStates.contains(l.getSimulatedState()))
    				    	{
    				    		toAdd.add(l);
    				    	}
    				    }
    				}
    				
    			}
    			toRemove.add(link);
    		}
    		toCheck.removeAll(toRemove);
    		toAdd.removeAll(bad);
    		toCheck.addAll(toAdd);
    	}
    }	
	
	/** the method check if solution exists in each iteration ***/
	public void compute3()
    {
    	//compute the synchronous product for enacted behaviours first
		if(system.keySet().size()<1){
    		buildEnactedBehaviour(availableBehaviours,environment);
    	}
    	buildEnactedBehaviour(target,environment);
    	//all done lets start building the simulation links and iterate through them
    	
    	//store the start states inorder to check the existence of a solution in each iteration
        behStartStates = this.computeAllCombinations(new ArrayList<Integer>(), this.behaviourStartStates);
    	for(Integer e:environmentStartStates)
        {
               ArrayList<Integer> statesList = new ArrayList<Integer>();
               statesList.add(targetStartState);
               statesList.add(e);
               tarinitialstates.add(statesList);
        }   

        for(ArrayList<Integer> states : behStartStates)
        {
        	for(Integer e:environmentStartStates)
        	{
                       ArrayList<Integer> statesList = new ArrayList<Integer>();
                       statesList.addAll(states);
                       statesList.add(e);
                       sysinitialstates.add(statesList);
        	}
        }

    	// Step1: assume each system states simulates every target state but add links only if the state can do all what target can do
    	Set<List<Integer>> systemStates = system.keySet();
    	Set<List<Integer>> targetStates = target.keySet();
    	for(List<Integer> systemState:systemStates)
    	{
			ArrayList<SimulationLink> fromLinks = new ArrayList<SimulationLink>();
    		for(List<Integer> targetState:targetStates)
    		{
    			if(simulates(systemState,targetState)) // can the system state do all what the target can do ?
    			{    			
    				if(matchesFinalState(systemState, targetState)){
		    			SimulationLink link = new SimulationLink(systemState,targetState);
		    			links.add(link);
		    			fromLinks.add(link);
		    			linksMap.put(systemState, fromLinks);
    				}
    			}
    		}
    	}
    	
    	// Step2: iterate over each link and remove the illegal ones as per the simulation definition
    	boolean linkRemoved = true;
    	while(linkRemoved && !links.isEmpty() && solutionExists())
    	{
    		linkRemoved = false;
    		List<SimulationLink> toRemove = new ArrayList<SimulationLink>();
    		for(SimulationLink link:links)
    		{
    			List<Integer> fromState = link.getFromState();
    			List<Integer> targetState = link.getSimulatedState();
    			if(!isNextStateInSimulation(fromState,targetState)) // can the system do an action and still remain in simulation ?
    			{
    				toRemove.add(link);
    				linksMap.get(fromState).remove(link);
    				linkRemoved = true;
    			}/*else
    			{
    				linkRemoved = false;
    			}*/
    		}
    		links.removeAll(toRemove);
    	}
    	//System.out.println("TotalLinksRemaining: "+links.size());
    }
	/** the reverse check method to avoid checking all nextinsim links with solution check in each iteration**/
	public void compute4()
    {
     	//compute the synchronous product for enacted behaviours first
		if(system.keySet().size()<1){
    		buildEnactedBehaviour(availableBehaviours,environment);
    	}
    	buildEnactedBehaviour(target,environment);
    	//all done lets start building the simulation links and iterate through them
    	
    	// Step1: assume each system states simulates every target state but add links only if the state can do all what target can do
    	Set<List<Integer>> systemStates = system.keySet();
    	Set<List<Integer>> targetStates = target.keySet();
    	
    	//linksMap = new HashMap<List<Integer>, ArrayList<SimulationLink>>(system.keySet().size(),1.0f);
    	//HashMap<SimulationLink,HashSet<SimulationLink>> linktree = new HashMap<SimulationLink, HashSet<SimulationLink>>();
    	
    	for(List<Integer> systemState:systemStates)
    	{
			ArrayList<SimulationLink> fromLinks = new ArrayList<SimulationLink>();
    		for(List<Integer> targetState:targetStates)
    		{
    			if(simulates(systemState,targetState)) // can the system state do all what the target can do ?
    			{    			
    				if(matchesFinalState(systemState, targetState)){
		    			SimulationLink link = new SimulationLink(systemState,targetState);
		    			links.add(link);
		    			fromLinks.add(link);
    				}
    			}
    		}
    		linksMap.put(systemState, fromLinks);
    	}
    	
    	//store the start states inorder to check the existence of a solution in each iteration
        behStartStates = this.computeAllCombinations(new ArrayList<Integer>(), this.behaviourStartStates);
    	for(Integer e:environmentStartStates)
        {
               ArrayList<Integer> statesList = new ArrayList<Integer>();
               statesList.add(targetStartState);
               statesList.add(e);
               tarinitialstates.add(statesList);
        }   
        for(ArrayList<Integer> states : behStartStates)
        {
        	for(Integer e:environmentStartStates)
        	{
                       ArrayList<Integer> statesList = new ArrayList<Integer>();
                       statesList.addAll(states);
                       statesList.add(e);
                       sysinitialstates.add(statesList);
        	}
        }

    	// we do not need to check all links. we will check the parent links of the link removed
    	HashSet<SimulationLink> toCheck = (HashSet<SimulationLink>) links.clone();
 
    	while(!toCheck.isEmpty() && solutionExists())
    	{
    		HashSet<SimulationLink> toRemove = new HashSet<SimulationLink>();
    		HashSet<SimulationLink> toAdd = new HashSet<SimulationLink>();
    		HashSet<SimulationLink> bad = new HashSet<SimulationLink>();
    		for(SimulationLink link:toCheck)
    		{
        		List<Integer> fromState = link.getFromState();
    			List<Integer> targetState = link.getSimulatedState();  
    			if(!isNextStateInSimulation(fromState, targetState))
    			{
    				linksMap.get(fromState).remove(link);
    				links.remove(link);
    				bad.add(link);
    				HashSet<ArrayList<Integer>> fromSysStates = system.get(link.getFromState()).getFromStates();
    				HashSet<ArrayList<Integer>> fromtarStates = target.get(link.getSimulatedState()).getFromStates();
    				for(List<Integer> state:fromSysStates)
    				{
    					List<SimulationLink> links = linksMap.get(state);
    				    for(SimulationLink l:links)
    				    {
    				    	if(fromtarStates.contains(l.getSimulatedState()))
    				    	{
    				    		toAdd.add(l);
    				    	}
    				    }
    				}
    			}
    			toRemove.add(link);
    		}
    		toCheck.removeAll(toRemove);
    		toAdd.removeAll(bad);
    		toCheck.addAll(toAdd);
    	}
    }	
	private boolean solutionExists() {
        for(ArrayList<Integer> ts:tarinitialstates){
        	if(!simulatedByAny(ts,sysinitialstates))
        	{
        		return false;
        	}
        }
		return true;
	}

	private boolean simulatedByAny(ArrayList<Integer> ts, Set<ArrayList<Integer>> sysinitialstates) {
		for(ArrayList<Integer> s:sysinitialstates)
		{
			ArrayList<SimulationLink> links = linksMap.get(s);
			for(SimulationLink link:links)
			{
				if(link.getSimulatedState().equals(ts))
				{
					return true;
				}
			}
		}
		return false;
	}
    private boolean matchesFinalState(List<Integer> fromState,List<Integer> targetState) {
		int tState = targetState.get(0);
		if(!targetBehaviour.isFinalState(tState))
		{
			return true;
		}
		for(int i=0;i<availableBehaviours.size();i++)
		{
			if(!availableBehaviours.get(i).isFinalState(fromState.get(i)))
			{
				return false;
			}
		}
		return true;
	}
    private boolean isNextStateInSimulation(List<Integer> fromState,List<Integer> targetState) {
		ArraySetList<Transition> targetCanDo = target.get(targetState).getLinksFrom();
		if(targetCanDo.isEmpty())
		{
			return true;
		}
		for(Transition t:targetCanDo)
		{
			HashSet<ArrayList<Integer>> tstate = target.get(targetState).getToStates(t).get(0);

			for(ArrayList<Integer> tostate:tstate){
				if(!isNextStateInSimulation(fromState, t,tostate))
				{
					return false;
				}
			}
		}
		return true;
	}
	private boolean isNextStateInSimulation(List<Integer>fromState,Transition t, ArrayList<Integer> tostate)
	{
		HashMap<Integer, HashSet<ArrayList<Integer>>> toStates = system.get(fromState).getToStates(t);
		if(toStates==null)
		{
			return false;
		}
		//lets get the buckets !
		Set<Integer> behaviourKeys = toStates.keySet();
		for(Integer key:behaviourKeys)
		{
			//fetch the bucket states for this behaviour
			HashSet<ArrayList<Integer>> behStates = toStates.get(key);
			if(statesInSimulation(behStates,tostate))
			{
				return true;
			}
		}
		return false;
	}
	private boolean isNextStateInSimulationOpt(List<Integer> fromState,List<Integer> targetState) {
		ArraySetList<Transition> targetCanDo = target.get(targetState).getLinksFrom();
		if(targetCanDo.isEmpty())
		{
			return true;
		}
		for(Transition t:targetCanDo)
		{
			HashSet<ArrayList<Integer>> tstate = target.get(targetState).getToStates(t).get(0);
			for(ArrayList<Integer> tostate:tstate){
				if(!isNextStateInSimulationOpt(fromState, t,tostate))
				{
					return false;
				}
			}
		}
		return true;
	}
	private boolean isNextStateInSimulationOpt(List<Integer>fromState,Transition t, ArrayList<Integer> tostate)
	{
		HashMap<Integer, HashSet<ArrayList<Integer>>> toStates = system.get(fromState).getToStates(t);
		if(toStates==null)
		{
			return false;
		}
		//lets get the buckets !
		Set<Integer> behaviourKeys = toStates.keySet();
		List<Integer> toRemove = new ArrayList<Integer>();
		boolean insim = false;
		for(Integer key:behaviourKeys)
		{
			//fetch the bucket states for this behaviour
			HashSet<ArrayList<Integer>> behStates = toStates.get(key);
			if(statesInSimulation(behStates,tostate))
			{
				insim = true;
			}else // remove the bucket !
			{
				toRemove.add(key);
			}
		}
		for(Integer r:toRemove){toStates.remove(r);}
		return insim;
	}
	private boolean statesInSimulation(Set<ArrayList<Integer>> ndStates,ArrayList<Integer> tostate) {
		ArrayList<ArrayList<Integer>> sameEnvStates = getSameEnvironmentStateS(ndStates,tostate);
		for(List<Integer> ndState:sameEnvStates)
		{
			ArrayList<SimulationLink> simlinks = linksMap.get(ndState);
			if(simlinks==null)
			{
				return false;
			}
			if(!hasToState(simlinks,tostate))
			{
				return false;
			}
			
		}
		return true;
	}
	private ArrayList<ArrayList<Integer>> getSameEnvironmentStateS(
			Set<ArrayList<Integer>> ndStates, ArrayList<Integer> tostate) {
		ArrayList<ArrayList<Integer>> states = new ArrayList<ArrayList<Integer>>();
		for(ArrayList<Integer> state:ndStates)
		{
			if(state.get(numberOfBehaviours)==tostate.get(1)){states.add(state);}
		}
		return states;
	}
	private boolean hasToState(ArrayList<SimulationLink> simlinks,
			ArrayList<Integer> tostate) {
		for(SimulationLink link:simlinks)
		{
			if(link.to.equals(tostate)){return true;}
		}
		return false;
	}
	private boolean statesInSimulation(Set<ArrayList<Integer>> ndStates) {
		for(List<Integer> ndState:ndStates)
		{
			ArrayList<SimulationLink> simlinks = linksMap.get(ndState);
			if(simlinks==null)
			{
				return false;
			}
			if(simlinks.size()<1)
			{
				return false;
			}
		}
		return true;
	}

	private boolean simulates(List<Integer> fromState, List<Integer> targetState) {
		// the environment states must be the same
		if(fromState.get(fromState.size()-1)!=targetState.get(targetState.size()-1))
		{
			return false;
		}
		ArraySetList<Transition> targetCanDo = target.get(targetState).getLinksFrom();
		for(Transition t:targetCanDo)
		{
			if(!stateCanDo(system,fromState,t.getAction()))
			{
				return false;
			}
		}
		return true;
	}
	private boolean stateCanDo(HashMap<List<Integer>, StateLinks> sys, List<Integer> fromState, Action action) {
		ArraySetList<Transition> stateCanDo = sys.get(fromState).getLinksFrom();
		for(Transition t:stateCanDo)
		{
			if(t.getAction().equals(action))
			{
				return true;
			}
		}
		return false;
	}
	private void buildEnactedBehaviour(HashMap<List<Integer>, StateLinks> target, TransitionSystem env) {
		
        Set<ArrayList<Integer>> toExpand = new HashSet<ArrayList<Integer>>();
        Set<ArrayList<Integer>> expanded = new HashSet<ArrayList<Integer>>();

        /* add the initial start state for the enacted system to the toExpand list */

        for(Integer e:environmentStartStates)
        {
               ArrayList<Integer> statesList = new ArrayList<Integer>();
               statesList.add(targetStartState);
               statesList.add(e);
               toExpand.add(statesList);
        }        
        int envIndex = 1; // since target state will be index 0
        /* iterate and calculate the reachable states till the toExpand list is not empty. i.e. we have visited all the reachable parts of the system */
        while(!toExpand.isEmpty())
        {
            Set<ArrayList<Integer>> toRemove = new HashSet<ArrayList<Integer>>();
            Set<ArrayList<Integer>> toAdd = new HashSet<ArrayList<Integer>>();
        	for(ArrayList<Integer> state:toExpand)
        	{
        		//the state has the integer numbers for the behaviour states and the environment
        		int envStateNo = state.get(envIndex); // the environment state is stored in the end
        		State envState = environment.getState(envStateNo);
        		//for(int i=0;i<numberOfBehaviours;i++) // lets iterate over each of the behaviours
        		//{
        			int targetStateNumber = state.get(0);
        			State targetState = targetBehaviour.getState(targetStateNumber);
        			List<Transition> behaviourCanDo = targetBehaviour.getOutgoingTransitions(targetStateNumber);
        			for(Transition t:behaviourCanDo) // for each transition that behaviour can do
        			{
        				 if (!targetBehaviour.hasRestrictions(t) || targetBehaviour.getApplicableStates(t).contains(this.environment.getState(envStateNo))) // check for guards
        				 {
        					// List<State> behToStates = targetBehaviour.getToStates(t, targetState); //get to states for behaviour
        					// List<State> envToStates = environment.getToStates(t,envState); // get to states for the environment
        					 List<Integer> behToStates = targetBehaviour.getToStateNumbers(t, targetState);
        					 List<Integer> envToStates = environment.getToStateNumbers(t, envState);
        					 if(!behToStates.isEmpty() && !envToStates.isEmpty())
        					 {
        						// for(State bs:behToStates) // create the new states
        						 for(Integer bs:behToStates)
        						 {
        							 //for(State es:envToStates)
        							 for(Integer es:envToStates)
        							 {
        								 //create new reachable states and add to the system 
        								 ArrayList<Integer> toState = (ArrayList<Integer>) state.clone();
        								 //toState.set(0, targetBehaviour.getStateNumber(bs));
        								 //toState.set(envIndex, environment.getStateNumber(es));
        								 toState.set(0, bs);
        								 toState.set(envIndex, es);
        								 addFromTransition(target,state,t);
        								 addToTransition(target,toState,t);
        								 //add the to state
        								 target.get(state).addToState(t,0,toState);
        								 //add the from state
        								 target.get(toState).addFromState(t, 0, state);
        								 toAdd.add(toState);
        							 }
        						 }
        					 }
        				 }
        			}
					 // remove the visited state from the toExpand list
					 toRemove.add(state);
					 expanded.add(state);
        		//}        		
        	}
        	toExpand.removeAll(toRemove);
        	addNewStates(expanded,toExpand,toAdd);
        }
        //printSystem(target);
		
	}
	private void buildEnactedBehaviour(List<Behaviour> behaviours, TransitionSystem env) {
		/* *
         * the computeAllCombination function calculates the all possible start states for the system.
         * e.g. if start states in behaviour one are [0,1] and in behaviour two are [0] then the function will
         * return [0,0] and [1,0] i.e. the cross product of the start states of the behaviours 
         * */
        Set<ArrayList<Integer>> behStartStates = this.computeAllCombinations(new ArrayList<Integer>(), this.behaviourStartStates);
        Set<ArrayList<Integer>> toExpand = new HashSet<ArrayList<Integer>>();
        Set<ArrayList<Integer>> expanded = new HashSet<ArrayList<Integer>>();

        /* add the initial start state for the enacted system to the toExpand list */
        for(ArrayList<Integer> states : behStartStates)
        {
        	for(Integer e:environmentStartStates)
        	{
                       ArrayList<Integer> statesList = new ArrayList<Integer>();
                       statesList.addAll(states);
                       statesList.add(e);
                       toExpand.add(statesList);
        	}
        }
        int envIndex = numberOfBehaviours;
        /* iterate and calculate the reachable states till the toExpand list is not empty. i.e. we have visited all the reachable parts of the system */
        while(!toExpand.isEmpty())
        {
            Set<ArrayList<Integer>> toRemove = new HashSet<ArrayList<Integer>>();
            Set<ArrayList<Integer>> toAdd = new HashSet<ArrayList<Integer>>();
        	for(ArrayList<Integer> state:toExpand)
        	{
        		//the state has the integer numbers for the behaviour states and the environment
        		int envStateNo = state.get(envIndex); // the environment state is stored in the end
        		State envState = environment.getState(envStateNo);
        		for(int i=0;i<numberOfBehaviours;i++) // lets iterate over each of the behaviours
        		{
        			int behaviourStateNumber = state.get(i);
        			State behaviourState = availableBehaviours.get(i).getState(behaviourStateNumber);
        			List<Transition> behaviourCanDo = availableBehaviours.get(i).getOutgoingTransitions(behaviourStateNumber);
        			Behaviour beh = availableBehaviours.get(i);
        			for(Transition t:behaviourCanDo) // for each transition that behaviour can do
        			{
        				 if (!beh.hasRestrictions(t) || beh.getApplicableStates(t).contains(this.environment.getState(envStateNo))) // check for guards
        				 {
        					 //List<State> behToStates = beh.getToStates(t, behaviourState); //get to states for behaviour
        					 List<Integer> behToStates = beh.getToStateNumbers(t, behaviourState);
        					 //List<State> envToStates = environment.getToStates(t,envState); // get to states for the environment
        					 List<Integer> envToStates = environment.getToStateNumbers(t, envState);
        					 if(!behToStates.isEmpty() && !envToStates.isEmpty())
        					 {
        						 //for(State bs:behToStates) // create the new states
        						 for(Integer bs:behToStates)
        						 {
        							// for(State es:envToStates)
        							 for(Integer es:envToStates)
        							 {
        								 //create new reachable states and add to the system 
        								 ArrayList<Integer> toState = (ArrayList<Integer>) state.clone();
        								 //toState.set(i, beh.getStateNumber(bs));
        								 toState.set(i, bs);
        								 //toState.set(envIndex, environment.getStateNumber(es));
        								 toState.set(envIndex, es);
        								 addFromTransition(system,state,t);
        								 addToTransition(system,toState,t);
        								 system.get(state).addToState(t,i,toState);
        								 //add the from states to the new state
        								 system.get(toState).addFromState(t, i,state);
        								 // add new states to the list
        								 toAdd.add(toState);
        							 }
        						 }
        					 }
        				 }
        			}
        		}
				 // remove the visited state from the toExpand list
				 toRemove.add(state);
				 expanded.add(state);
        	}
        	toExpand.removeAll(toRemove);
        	addNewStates(expanded,toExpand,toAdd);
        }
        //printSystem(system);
	}
    private void printSystem(HashMap<List<Integer>, StateLinks> machine) {
		Set<List<Integer>> states = machine.keySet();
		for(List<Integer> state:states )
		{
			System.out.println(state);
			System.out.println("-- outgoing transitions --");
			StateLinks links = machine.get(state);
			for(Transition t:links.getLinksFrom())
			{
				System.out.println(t);
			}
			System.out.println("-- incoming transitions --");
			for(Transition t:links.getLinksTo())
			{
				System.out.println(t);
			}
		}
	}
	private void addNewStates(Set<ArrayList<Integer>> expanded,
			Set<ArrayList<Integer>> toExpand, Set<ArrayList<Integer>> toAdd) {
		for(ArrayList<Integer> state:toAdd)
		{
			if(!expanded.contains(state))
			{
				toExpand.add(state);
			}
		}		
	}
	private void addToTransition(HashMap<List<Integer>, StateLinks> system2,ArrayList<Integer> toState, Transition t) {
    	StateLinks links = system2.get(toState);
		if(links==null)
		{
			links = new StateLinks();
			system2.put(toState, links);
		}
		links.getLinksTo().add(t);		
	}
	private void addFromTransition(HashMap<List<Integer>, StateLinks> system2, ArrayList<Integer> fromState, Transition t) {
		StateLinks links = system2.get(fromState);
		if(links==null)
		{
			links = new StateLinks();
			system2.put(fromState, links);
		}
		links.getLinksFrom().add(t);
	}
	private Set<ArrayList<Integer>> computeAllCombinations(List<Integer> choice, List<Set<Integer>> todo) {
        Set<ArrayList<Integer>> res = new LinkedHashSet<ArrayList<Integer>>();
        if (todo.isEmpty()) {
            int size = choice.size();
           ArrayList<Integer>finalChoice = new ArrayList<Integer>();
            for (int i = 0; i < size; i++) {
                finalChoice.add(choice.get(i));
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
    private static class SimulationLink
	{
		private List<Integer>from;
		private List<Integer>to;
		private SimulationLink(List<Integer> from, List<Integer> to)
		{
			this.from = from;
			this.to= to;
		}
		public List<Integer> getSimulatedState() {
			return to;
		}
		public List<Integer> getFromState() {
			return from;
		}
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(from);
			sb.append("---");
			sb.append(to);
			return sb.toString();
		}
		public int hashCode()
		{
			return from.hashCode() + 11*to.hashCode();
		}
	}
    private static class StateLinks
    {
    	private ArraySetList<Transition> to;
    	private ArraySetList<Transition> from;
    	private HashMap<Action,HashMap<Integer,HashSet<ArrayList<Integer>>>> fromStatesMap;
    	private HashMap<Action,HashMap<Integer,HashSet<ArrayList<Integer>>>> toStatesMap;
    	public StateLinks()
    	{
    		to = new ArraySetList<Transition>();
    		from = new ArraySetList<Transition>();
    		toStatesMap = new HashMap<Action,HashMap<Integer,HashSet<ArrayList<Integer>>>>();
    		fromStatesMap = new HashMap<Action, HashMap<Integer,HashSet<ArrayList<Integer>>>>();
    	}
    	public ArraySetList<Transition> getLinksTo(){return to;}
    	public ArraySetList<Transition>getLinksFrom(){return from;}
    	public void addToState(Transition t,Integer behaviour, ArrayList<Integer> tostate)
    	{
    		HashMap<Integer, HashSet<ArrayList<Integer>>> toStates = toStatesMap.get(t.getAction());
    		HashSet<ArrayList<Integer>> behStates = null;
    		if(toStates==null)
    		{
    			toStates= new HashMap<Integer,HashSet<ArrayList<Integer>>>();
    			behStates = new HashSet<ArrayList<Integer>>();
    			behStates.add(tostate);
    			toStates.put(behaviour, behStates);
    			toStatesMap.put(t.getAction(), toStates);
    			return;
    		}else
    		{
    			behStates = toStates.get(behaviour);
    			if(behStates ==null)
    			{
    				behStates = new HashSet<ArrayList<Integer>>();
        			behStates.add(tostate);
        			toStates.put(behaviour, behStates);        			
        			return;
    			}
    			behStates.add(tostate);
    		}			
    		//toStates.put(behaviour, behStates);
    		//toStatesMap.get(t.getAction()).addAll(toStates);
    	} 	
    	public void addFromState(Transition t,Integer behaviour, ArrayList<Integer> tostate)
    	{
    		HashMap<Integer, HashSet<ArrayList<Integer>>> fromStates = fromStatesMap.get(t.getAction());
    		HashSet<ArrayList<Integer>> behStates = null;
    		if(fromStates==null)
    		{
    			fromStates= new HashMap<Integer,HashSet<ArrayList<Integer>>>();
    			behStates = new HashSet<ArrayList<Integer>>();
    			behStates.add(tostate);
    			fromStates.put(behaviour, behStates);
    			fromStatesMap.put(t.getAction(), fromStates);
    			return;
    		}else
    		{
    			behStates = fromStates.get(behaviour);
    			if(behStates ==null)
    			{
    				behStates = new HashSet<ArrayList<Integer>>();
        			behStates.add(tostate);
        			fromStates.put(behaviour, behStates);        			
        			return;
    			}
    			behStates.add(tostate);
    		}			
    		//toStates.put(behaviour, behStates);
    		//toStatesMap.get(t.getAction()).addAll(toStates);
    	}
    	public HashMap<Integer, HashSet<ArrayList<Integer>>> getToStates(Transition t)
    	{
    		return toStatesMap.get(t.getAction());
    	}
       	public HashMap<Integer, HashSet<ArrayList<Integer>>> getFromStates(Transition t)
    	{
    		return fromStatesMap.get(t.getAction());
    	}
       	public HashSet<ArrayList<Integer>> getFromStates()
       	{
       		HashSet<ArrayList<Integer>> fromstates = new HashSet<ArrayList<Integer>>();
       		for(Action a:fromStatesMap.keySet())
       		{
       			for(Integer i:fromStatesMap.get(a).keySet())
       			{
       				fromstates.addAll(fromStatesMap.get(a).get(i));
       			}
       		}
       		return fromstates;
       	}
    }
}
