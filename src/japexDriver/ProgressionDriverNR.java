package japexDriver;

import japexDriver.utils.DriverHelper;
import java.util.*;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

import behaviourComposition.ai.*;
import behaviourComposition.structure.*;

public class ProgressionDriverNR extends JapexDriverBase {

	private ArrayList<Behaviour> available;
	private TransitionSystem environment;
	private Behaviour target;
	@Override
	public void prepare(TestCase arg0) {
		String fileurl = arg0.getParam("inputfile");
		//String fileurl = "/home/nitin/workspace/BehaviourCompositionProblem/testbed/KRExampleExtended/KRExtended.xml";
		DriverHelper helper = new DriverHelper(fileurl);
		available = helper.getAvailableBehaviours();
		environment = helper.getEnvironment();
		target = helper.getTarget();
	}

	@Override
	public void run(TestCase arg0) {
		simulate();
	}

	@Override
	public void warmup(TestCase arg0) {
		simulate();
	}

	private void simulate() {
		NewBehaviourSimulator simulator = new NewBehaviourSimulator(available, environment, target);
		simulator.computeScheduler();
	}
	
}
