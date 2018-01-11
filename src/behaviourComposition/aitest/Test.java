package behaviourComposition.aitest;

import java.io.IOException;
import java.util.ArrayList;

import behaviourComposition.aitest.*;
import behaviourComposition.structure.*;

public class Test {

	private TransitionSystem environment;
	private Behaviour target;
	private ArrayList<Behaviour> behaviours;
	private int times;
	public Test(TransitionSystem env,ArrayList<Behaviour> behaviours,Behaviour target)
	{
		this.environment = env;
		this.behaviours = behaviours;
		this.target = target;
		times = 1;
	}
	public Test(TransitionSystem env,ArrayList<Behaviour> behaviours,Behaviour target,int times)
	{
		this.environment = env;
		this.behaviours = behaviours;
		this.target = target;
		this.times = times;
	}
	public String run()
	{
		return run(times);
	}
	public String run(int times)
	{
		System.out.println("============================================================");
		System.out.println("------Problem Characteristics----------");
		System.out.println("NumberofStatesInEnvironment: "+ environment.getStates().size());
		System.out.println("NumberofStatesInTarget: "+ target.getStates().size());
		System.out.println("NumberofBehaviours: "+ behaviours.size());
		int i =1;
		for(Behaviour b:behaviours)
		{
			System.out.println("NumberofStatesInBehaviour"+ (i++) +": "+ b.getStates().size());
		}
		System.out.println("------Allegro (Simulation-based regression) ----------");
		computeRegressionOriginal();	
		System.out.println("------Allegro-Opt1 ----------");
		computeRegressionOptimised();
		System.out.println("------Allegro-Opt1+2----------");
		computeRegressionOptimised2();
		System.out.println("------Search-based progression----------");
		computeProgression();
		System.out.println("============================================================");
		System.out.println();
		return "";
	}
	private long computeRegressionOptimised2() {
		 long timem1 = System.currentTimeMillis();
		 BehaviourSimulatorMorpheus morpheus1 = new BehaviourSimulatorMorpheus(behaviours,environment,target);
	     morpheus1.compute2();
	     long time = System.currentTimeMillis()-timem1;
	     System.out.println("InitialLinks: "+morpheus1.getRegressionOptimizedLinks());
	     System.out.println("RegressionOptimizedTime(ms): "+time);
	     return (time);		
	}
	private long computeRegressionOptimised3() {
		 long timem1 = System.currentTimeMillis();
		 BehaviourSimulatorMorpheus morpheus2 = new BehaviourSimulatorMorpheus(behaviours,environment,target);
	     morpheus2.compute3();
	     long time = System.currentTimeMillis()-timem1;
	     //System.out.println("RegressionOptimized3Links: "+morpheus2.getRegressionOptimizedLinks());
	     System.out.println("RegressionOptimized3Time(ms): "+time);
	     return (time);		
	}
	private long computeRegressionOptimised4() {
		 long timem1 = System.currentTimeMillis();
		 BehaviourSimulatorMorpheus morpheus4 = new BehaviourSimulatorMorpheus(behaviours,environment,target);
	     morpheus4.compute4();
	     long time = System.currentTimeMillis()-timem1;
	     //System.out.println("RegressionOptimized3Links: "+morpheus2.getRegressionOptimizedLinks());
	     System.out.println("RegressionOptimized4Time(ms): "+time);
	     return (time);		
	}
	private long getAverage(ArrayList<Long> regressiontime) {
		Long sum = 0L;
		for(Long l :regressiontime)
		{
			sum+=l;
		}
		return (sum/regressiontime.size());
	}
	private long computeRegressionOptimised()
	{
		 long timem1 = System.currentTimeMillis();
		 BehaviourSimulatorMorpheus morpheus1 = new BehaviourSimulatorMorpheus(behaviours,environment,target);
	     morpheus1.compute1();
	     long time = System.currentTimeMillis()-timem1;
	     System.out.println("InitialLinks: "+morpheus1.getRegressionOptimizedLinks());
	     System.out.println("RegressionOptimizedTime(ms): "+time);
	     return (time);
	}
	private long computeRegressionOriginal()
	{
		 long timem = System.currentTimeMillis();
	     BehaviourSimulatorMorpheus morpheus = new BehaviourSimulatorMorpheus(behaviours,environment,target);
	     morpheus.compute();
	     long time = System.currentTimeMillis()-timem;
	     System.out.println("InitialLinks: "+morpheus.getRegressionLinks());
	     System.out.println("SimulationLinks: "+morpheus.getSimulationLinks());
	     System.out.println("SystemStates: "+morpheus.getSystemStatesCount());
	     System.out.println("TargetStates: "+morpheus.getTargetStatesCount());
	     System.out.println("RegressionTime(ms): "+time);
	     System.out.println("Simulation Links :");
	     morpheus.printLinks();
	     return (time);
	}
	private long computeProgression()
	{
	    long time2 = System.currentTimeMillis();
		NewBehaviourSimulator sim = new NewBehaviourSimulator(behaviours, environment, target);
		System.out.println("Expansion states:");
		Scheduler scheduler = sim.computeScheduler();
		//System.out.println("DecisionStates: "+sim.getDecisionStateCount());
		System.out.println("ProgressionTime(ms): "+(System.currentTimeMillis() - time2));
		return System.currentTimeMillis() - time2;
	}
}
