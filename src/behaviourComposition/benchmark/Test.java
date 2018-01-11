package behaviourComposition.benchmark;

import java.io.IOException;
import java.util.ArrayList;

import behaviourComposition.ai.*;
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
		System.out.println("runningTest");
		ArrayList<Long> regressiontime = new ArrayList<Long>();
		ArrayList<Long> regressionopttime = new ArrayList<Long>();
		ArrayList<Long> progressiontime = new ArrayList<Long>();
		Runtime runtime = java.lang.Runtime.getRuntime();
		Process p;
		for(int i =0;i<times;i++)
		{
			try {
				p=runtime.exec("echo saibaba| sudo -S ./clearcache.sh");				
				p.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			regressiontime.add(computeRegressionOriginal());
			
			progressiontime.add(computeProgression());
			regressionopttime.add(computeRegressionOptimised());
		}
		long regavg = getAverage(regressiontime);
		long regoptavg = getAverage(regressionopttime);
		long progavg = getAverage(progressiontime);
		return "Regression: "+regavg+" | RegressionOptimized: "+regoptavg+" | "+"Progression: "+progavg;
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
	     return (System.currentTimeMillis()-timem1);
	}
	private long computeRegressionOriginal()
	{
		 long timem = System.currentTimeMillis();
	     BehaviourSimulatorMorpheus morpheus = new BehaviourSimulatorMorpheus(behaviours,environment,target);
	     morpheus.compute();
	     return (System.currentTimeMillis()-timem);
	}
	private long computeProgression()
	{
	    long time2 = System.currentTimeMillis();
		BehaviourSimulator sim = new BehaviourSimulator(behaviours, environment, target);
		Scheduler scheduler = sim.computeScheduler();
		sim = null;
		return System.currentTimeMillis() - time2;
	}
}
