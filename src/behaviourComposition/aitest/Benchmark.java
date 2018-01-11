package behaviourComposition.aitest;
import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import structures.ArraySetList;

import behaviourComposition.graphviz.TransitionSystemGraph;
import behaviourComposition.parser.transitionSystem.*;
import behaviourComposition.parser.behaviour.*;
import behaviourComposition.parser.behaviour.ParseException;
import behaviourComposition.structure.*;
;
public class Benchmark {
	private static parserTS parser;
	private static parserBS bparser;
	private static ArrayList<Test> tests;
	private static ArrayList<String> outputfiles;
	private static String filePath;
	public static void main(String[] args)
	{
		String fileurl = null;
		if(args.length<1){
			fileurl="testbed/NumberOfBehaviours/KR2/KR1.xml";
		}
		else {fileurl = args[0];}
		tests = new ArrayList<Test>();
		outputfiles = new ArrayList<String>();
		//getProperties(fileurl);
		parsexml(fileurl);
		run();
	}
	private static void getProperties(String fileurl)
	{
		String[] blocks = fileurl.split("/");
		String[] filename = blocks[blocks.length-1].split("\\.");
		System.out.println("Starting tests. output would be in "+filename[0]+".txt");
		redirectToFile(filename[0]+".txt");
		System.out.println(Calendar.getInstance().getTime());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();		
		try {
			
			File file = new File(fileurl);
			filePath = file.getParent() +"/";
			DocumentBuilder db = factory.newDocumentBuilder();
			Document dom = db.parse(fileurl);
			
			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("testCase");
			for(int i =0;i<nl.getLength();i++)
			{
				//get the testcase element
				Element el = (Element)nl.item(i);
				NodeList pl = el.getElementsByTagName("param");
				Element paramNode = (Element)pl.item(0);
				String testparam = paramNode.getAttribute("name");
				String testxml = "";
				if(testparam.equalsIgnoreCase("inputfile"))
				{
					testxml = paramNode.getAttribute("value");
				}
				tests = new ArrayList<Test>();
				parsexml(testxml);
				run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		redirectToConsole();
		System.out.println("All done");
	}
	private static void run() {
		for(int i = 0;i<tests.size();i++)
		{
			Test t = tests.get(i);
			String outputfile = outputfiles.get(i);
			File file = new File(filePath+outputfile);
			try {
				if(!file.exists())
				{
					file.delete();
				}
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				writer.write(t.run());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	private static ArrayList<Behaviour> createNonSolutionChain(int length,int number)
	{
		Action a = new Action("a");
		Action b = new Action("a");
		// lets build the target first
		Behaviour target = new Behaviour();
		int i =0;
		// lets build the behaviour
		Behaviour beh = new Behaviour();
		int bstartstate = beh.addState("b0");
		beh.setStartState(bstartstate);
		for(i = 1;i<length-1;i++)
		{
			beh.addState("b"+i);
			beh.addTransition(i-1, i, a);
		}
		//last transition will break the loop
		beh.addState("b"+i);
		beh.addTransition(i-1,i,b);
		beh.setFinalState(i);
		beh.setFinalState(0);
		
		ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
		for(int j=0;j<number;j++)
		{
			//Behaviour behcopy = (Behaviour) beh.deepCopy();
			behaviours.add(beh);
		}	
		return behaviours;
	}
	private static void createMultiChain(int sl, int sn, int nle, int nn) {
		//create the environment
		Action a = new Action("a");
		Action b = new Action("b");
		TransitionSystem env = new TransitionSystem();
		int estart = env.addState("e");
		env.setStartState(estart);
		env.addTransition(estart, estart, a);
		env.addTransition(estart, estart, b);
		
		// lets build the target first
		Behaviour target = new Behaviour();
		//add the start state
		int startstate = target.addState("t0");
		target.setStartState(startstate);
		int i =0;
		for(i =1;i<sl;i++)
		{
			target.addState("t"+i);
			target.addTransition(i-1, i, a);
		}
		target.setFinalState(i-1);
		
		ArrayList<Behaviour> avail = new ArrayList<Behaviour>();
		avail.addAll(createNonSolutionChain(nle, nn));
		avail.addAll(createSolutionChain(sl, sn));
		
		tests.add(new Test(env,avail,target));
		outputfiles.add("test.txt");
	}
	private static ArrayList<Behaviour> createSolutionChain(int length,int number)
	{
		Action a = new Action("a");
		//Action b = new Action("b");
		
		// lets build the behaviour
		Behaviour beh = new Behaviour();
		int bstartstate = beh.addState("b0");
		beh.setStartState(bstartstate);
		int i =0;
		for(i = 1;i<length;i++)
		{
			beh.addState("b"+i);
			beh.addTransition(i-1, i, a);
		}
		
		ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
		for(int j=0;j<number;j++)
		{
			//Behaviour behcopy = (Behaviour) beh.deepCopy();
			behaviours.add(beh);
		}	
		return behaviours;
	}
	private static void createChain2(int length,int number)
	{
		Action b = new Action("b");
		// lets build the target first
		Behaviour target = new Behaviour();
		//add the start state
		int startstate = target.addState("t0");
		target.setStartState(startstate);
		int i =0;
		for(i =1;i<length;i++)
		{
			Action a = new Action("a"+i);
			target.addState("t"+i);
			target.addTransition(i-1, i, a);
		}
		target.setFinalState(i-1);
		// lets build the behaviour
		Behaviour beh = new Behaviour();
		int bstartstate = beh.addState("b0");
		beh.setStartState(bstartstate);
		for(i = 1;i<length-1;i++)
		{
			Action a = new Action("a"+i);
			beh.addState("b"+i);
			beh.addTransition(i-1, i, a);
		}
		//last transition will break the loop
		beh.addState("b"+i);
		beh.addTransition(i-1,i,b);
		beh.setFinalState(i);
		
		//create the environment
		TransitionSystem env = new TransitionSystem();
		int estart = env.addState("e");
		env.setFinalState(estart);
		env.setStartState(estart);
		for(i=1;i<length-1;i++)
		{
			Action a = new Action("a"+i);
			env.addTransition(estart, estart, a);
		}

		env.addTransition(estart, estart, b);
		
		ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
		for(int j=0;j<number;j++)
		{
			behaviours.add(beh);
		}		
		tests.add(new Test(env,behaviours,target));
		outputfiles.add("test.txt");
	}
	private static void createChain(int length,int number)
	{
		Action a = new Action("a");
		Action b = new Action("b");
		// lets build the target first
		Behaviour target = new Behaviour();
		//add the start state
		int startstate = target.addState("t0");
		target.setStartState(startstate);
		int i =0;
		for(i =1;i<length;i++)
		{
			target.addState("t"+i);
			target.addTransition(i-1, i, a);
		}
		target.setFinalState(i-1);
		// lets build the behaviour
		Behaviour beh = new Behaviour();
		int bstartstate = beh.addState("b0");
		beh.setStartState(bstartstate);
		for(i = 1;i<length-1;i++)
		{
			beh.addState("b"+i);
			beh.addTransition(i-1, i, a);
		}
		//last transition will break the loop
		beh.addState("b"+i);
		beh.addTransition(i-1,i,b);
		beh.setFinalState(i);
		
		//create the environment
		TransitionSystem env = new TransitionSystem();
		int estart = env.addState("e");
		env.setFinalState(estart);
		env.setStartState(estart);
		env.addTransition(estart, estart, a);
		env.addTransition(estart, estart, b);
		
		ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
		for(int j=0;j<number;j++)
		{
			behaviours.add(beh);
		}		
		tests.add(new Test(env,behaviours,target));
		outputfiles.add("test.txt");
	}
	private static void parsexml(String fileurl) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			File file = new File(fileurl);
			filePath = file.getParent() +"/";
			DocumentBuilder db = factory.newDocumentBuilder();
			Document dom = db.parse(fileurl);
			parsedom(dom);
		}catch(Exception e){e.printStackTrace();}
	}
	private static void parsedom(Document dom) {
		Element docEle = dom.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("test");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				//get the test element
				Element el = (Element)nl.item(i);
				//check what type of test is it
				String type = el.getAttribute("type");
				if(type.equalsIgnoreCase("simplechain"))
				{
					int chainlength = Integer.parseInt(el.getAttribute("length"));
					int behnumber = Integer.parseInt(el.getAttribute("number"));
					createChain(chainlength,behnumber);
				}else if(type.equalsIgnoreCase("multitarget"))
				{
					setMultiTest(el);
				}
				else if(type.equalsIgnoreCase("multichain"))
				{
					int sl = Integer.parseInt(el.getAttribute("slength"));
					int sn = Integer.parseInt(el.getAttribute("snumber"));
					int nle = Integer.parseInt(el.getAttribute("nlength"));
					int nn = Integer.parseInt(el.getAttribute("nnumber")); 
					createMultiChain(sl,sn,nle,nn);
				}
				else{ //default case
					//get the test object
					Test e = getTest(el);
					//add it to list
					tests.add(e);
				}
			}
		}	
	}
	
	private static void setMultiTest(Element el) {
		//for each <employee> element get text or int values of
		//name ,id, age and name
		String env = getTextValue(el,"environment");
		String tgt = getTextValue(el,"target");
		ArrayList<String> behs = new ArrayList<String>();
		NodeList nl = el.getElementsByTagName("behaviour");
		ArrayList<Integer> behaviourmultiplier = new ArrayList<Integer>();
		ArrayList<Integer> ndmultiplier = new ArrayList<Integer>();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element b = (Element)nl.item(i);
				behs.add(b.getFirstChild().getNodeValue());
				String timess = b.getAttribute("times");
				String nds = b.getAttribute("nd");
				int times = 1;
				int nd = 0;
				if(!nds.isEmpty()){nd = Integer.parseInt(nds);}
				if(!timess.isEmpty()){times = Integer.parseInt(b.getAttribute("times"));}
				behaviourmultiplier.add(times);
				ndmultiplier.add(nd);
			}
		}	
		int environmentAmplification = getAmplification(el,"environment");
		int targetAmplification = getAmplification(el,"target");
		
		int envNDamplification = getNDAmplification(el, "environment");
		String stimes = el.getAttribute("run");
		outputfiles.add(el.getAttribute("outputfile"));

		//parse the grammars
		TransitionSystem environment = getEnvironment(filePath+env);
		if(envNDamplification>0)
		{
			amplifyND(environment, envNDamplification);
		}
		if(environmentAmplification > 0){
			amplifyEnvironment(environment,environmentAmplification);
		}
		Behaviour target = getBehaviour(filePath+tgt,environment);
		if(targetAmplification > 0){
			amplifyEnvironment(target,targetAmplification);
		}
		ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
		for(int i =0;i<behs.size();i++)
		{
			Behaviour beh = getBehaviour(filePath+behs.get(i), environment);
			if(ndmultiplier.get(i)>0)
			{
				amplifyND(beh,ndmultiplier.get(i));
			}
			for(int j =0;j<behaviourmultiplier.get(i);j++){
				behaviours.add(beh);
			}
		}				
	}
	private static Test getTest(Element el) {
		//for each <employee> element get text or int values of
		//name ,id, age and name
		String env = getTextValue(el,"environment");
		String tgt = getTextValue(el,"target");
		ArrayList<String> behs = new ArrayList<String>();
		NodeList nl = el.getElementsByTagName("behaviour");
		ArrayList<Integer> behaviourmultiplier = new ArrayList<Integer>();
		ArrayList<Integer> ndmultiplier = new ArrayList<Integer>();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element b = (Element)nl.item(i);
				behs.add(b.getFirstChild().getNodeValue());
				String timess = b.getAttribute("times");
				String nds = b.getAttribute("nd");
				int times = 1;
				int nd = 0;
				if(!nds.isEmpty()){nd = Integer.parseInt(nds);}
				if(!timess.isEmpty()){times = Integer.parseInt(b.getAttribute("times"));}
				behaviourmultiplier.add(times);
				ndmultiplier.add(nd);
			}
		}	
		int environmentAmplification = getAmplification(el,"environment");
		int targetAmplification = getAmplification(el,"target");
		
		int envNDamplification = getNDAmplification(el, "environment");
		String stimes = el.getAttribute("run");
		outputfiles.add(el.getAttribute("outputfile"));

		//parse the grammars
		TransitionSystem environment = getEnvironment(filePath+env);
		if(envNDamplification>0)
		{
			amplifyND(environment, envNDamplification);
		}
		if(environmentAmplification > 0){
			amplifyEnvironment(environment,environmentAmplification);
		}
		Behaviour target = getBehaviour(filePath+tgt,environment);
		boolean breakTarget = getBreak(el,"target");
		int location = getLocation(el,"target");
		if(targetAmplification > 0){
			Action breakAction = null;
			if(breakTarget){
				breakAction = new Action("breakAction");
				ArraySetList<State> envsts = environment.getStates();
				for(State s:envsts)
				{
					int snumber = environment.getStateNumber(s);
					environment.addTransition(snumber,snumber,breakAction);
				}
				amplifyTargetBreak(target,targetAmplification,breakAction,location);	
			}else
			{
				amplifyEnvironment(target, targetAmplification);
			}
					
		}
		ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
		for(int i =0;i<behs.size();i++)
		{
			Behaviour beh = getBehaviour(filePath+behs.get(i), environment);
			if(ndmultiplier.get(i)>0)
			{
				amplifyND(beh,ndmultiplier.get(i));
			}
			for(int j =0;j<behaviourmultiplier.get(i);j++){
				behaviours.add(beh);
			}
		}		
		return new Test(environment,behaviours,target,Integer.parseInt(stimes));
	}
	private static Boolean getBreak(Element el, String string) {
		NodeList nl = el.getElementsByTagName(string);
		boolean brk = false;
		if(nl != null && nl.getLength() > 0) {
			Element b = (Element)nl.item(0);
			String timess = b.getAttribute("break");
			if(!timess.isEmpty()){brk = Boolean.parseBoolean(b.getAttribute("break"));}
		}	
		return brk;
	}
	private static Integer getLocation(Element el, String string) {
		NodeList nl = el.getElementsByTagName(string);
		int brk = 0;
		if(nl != null && nl.getLength() > 0) {
			Element b = (Element)nl.item(0);
			String timess = b.getAttribute("location");
			if(!timess.isEmpty()){brk = Integer.parseInt(b.getAttribute("location"));}
		}	
		return brk;
	}
	private static TransitionSystem amplifyND(TransitionSystem beh, Integer integer) {
		HashSet<State> ndstates = beh.getNDStates();
		for(State state:ndstates)
		{
			int statenumber = beh.getStateNumber(state);
			for(int i =0;i<integer;i++){
				int newstate = beh.addState(state.getName() + "_nd"+i);
				for(Transition t:beh.getTransitions())
				{
					if(t.getToNumber()==statenumber)
					{
						beh.addTransition(t.getFromNumber(),newstate, t.getAction());
					}
					if(t.getFromNumber()==statenumber)
					{
						beh.addTransition(newstate,t.getToNumber(),t.getAction());
					}
				}
			}
		}
		return beh;
	}
	private static void amplifyEnvironment(TransitionSystem environment,
			int amplification) {
			List<Transition> transitions = environment.getTransitions();
			List<Transition> removed = new ArrayList<Transition>();
			Set<Integer> initialstates = environment.getStartStateNumbers();
			int initstate=0;
			for(Integer s:initialstates)
			{
				initstate = s;
			}		
			//step 1. break the cycle where the states end in the initial state
			for(Transition t : transitions)
			{
				if(t.getToNumber()==initstate && t.getFromNumber()!=initstate)
				{
					removed.add(t);
					environment.removeTransition(t);
				}
			}
			// create a copy of the opened up fsm
			ArraySetList<State> states = new ArraySetList<State>();
			copyArraySetList(states,environment.getStates());
			HashMap<State,ArrayList<State>> similarStates = new HashMap<State, ArrayList<State>>();
			//create copies of the existing states
			for(int i =1;i<=amplification;i++)
			{
				for(State s : states)
				{
					String statename = "a"+(environment.getStateNumber(s)+1)+i;
					int statenumber = environment.addState(statename);
					addSimilarState(similarStates,s,environment.getState(statenumber));
				}
			}
			
			//create transitions between similar states
			for(Transition t:environment.getTransitions())
			{
				State from = environment.getState(t.getFromNumber());
				State to = environment.getState(t.getToNumber());
				ArrayList<State> fromstates = similarStates.get(from);
				ArrayList<State> tostates = similarStates.get(to);
				for(int i =0;i<fromstates.size();i++)
				{
					environment.addTransition(fromstates.get(i), tostates.get(i), t.getAction());
				}
			}
			
			//close the fsm back
			for(Transition t:removed)
			{
				State from = environment.getState(t.getFromNumber());
				State to = environment.getState(t.getToNumber());
				ArrayList<State> fromstates = similarStates.get(from);
				ArrayList<State> tostates = similarStates.get(to);
				//create the first connect manually
				environment.addTransition(from,tostates.get(0),t.getAction());
				//chain the rest
				for(int i =1;i<amplification;i++)
				{
					environment.addTransition(fromstates.get(i-1),tostates.get(i),t.getAction());
				}
				//create the last connect manually
				environment.addTransition(fromstates.get(amplification-1),to,t.getAction());
			}
			//ArrayList<Integer> toExpand = new ArrayList<Integer>();
			//toExpand.add(initstate);
			
	}
	private static void amplifyTargetBreak(TransitionSystem target,
			int amplification, Action breakAction, int location) {
			List<Transition> transitions = target.getTransitions();
			List<Transition> removed = new ArrayList<Transition>();
			Set<Integer> initialstates = target.getStartStateNumbers();
			Set<Integer> finalstates = target.getFinalStateNumbers();
			int initstate=0;
			for(Integer s:initialstates)
			{
				initstate = s;
			}
			//step 1. break the cycle where the states end in the initial state
			for(Transition t : transitions)
			{
				if(t.getToNumber()==initstate && t.getFromNumber()!=initstate)
				{
					removed.add(t);
					target.removeTransition(t);
				}
			}
			// create a copy of the opened up fsm
			ArraySetList<State> states = new ArraySetList<State>();
			copyArraySetList(states,target.getStates());
			HashMap<State,ArrayList<State>> similarStates = new HashMap<State, ArrayList<State>>();
			//create copies of the existing states
			for(int i =1;i<=amplification;i++)
			{
				for(State s : states)
				{
					String statename = "a"+(target.getStateNumber(s)+1)+i;
					int statenumber = target.addState(statename);
					addSimilarState(similarStates,s,target.getState(statenumber));
				}
			}
			
			//create transitions between similar states
			for(Transition t:target.getTransitions())
			{
				State from = target.getState(t.getFromNumber());
				State to = target.getState(t.getToNumber());
				ArrayList<State> fromstates = similarStates.get(from);
				ArrayList<State> tostates = similarStates.get(to);
				for(int i =0;i<fromstates.size();i++)
				{
					target.addTransition(fromstates.get(i), tostates.get(i), t.getAction());
				}
			}
			
			//close the fsm back
			for(Transition t:removed)
			{
				State from = target.getState(t.getFromNumber());
				State to = target.getState(t.getToNumber());
				ArrayList<State> fromstates = similarStates.get(from);
				ArrayList<State> tostates = similarStates.get(to);
				//create the first connect manually
				if(location==1)
				{
					target.addTransition(from,tostates.get(0),breakAction);
				}else{
				target.addTransition(from,tostates.get(0),t.getAction());
				}
				//chain the rest
				for(int i =1;i<amplification;i++)
				{
					if(i==location+1)
					{
						target.addTransition(fromstates.get(i-1),tostates.get(i),breakAction);
					}else{
						target.addTransition(fromstates.get(i-1),tostates.get(i),t.getAction());
					}
				}
				//create the last connect manually
				if(breakAction!=null){
					target.addTransition(fromstates.get(amplification-1),to,breakAction);
				}
			}
			//ArrayList<Integer> toExpand = new ArrayList<Integer>();
			//toExpand.add(initstate);
	}
	private static void copyArraySetList(ArraySetList<State> states,ArraySetList<State> states2) {
		for(State s:states2)
		{
			states.add(s);
		}		
	}
	private static void addSimilarState(
			HashMap<State, ArrayList<State>> similarStates, State s, State state) {
		ArrayList<State> array = similarStates.get(s);
		if(array==null)
		{
			array = new ArrayList<State>();
			array.add(state);
			similarStates.put(s, array);
			return;
		}
		array.add(state);
		return;
	}
	private static int getNDAmplification(Element el,String string)
	{
		NodeList nl = el.getElementsByTagName(string);
		int times = 0;
		if(nl != null && nl.getLength() > 0) {
			Element b = (Element)nl.item(0);
			String timess = b.getAttribute("nd");
			if(!timess.isEmpty()){times = Integer.parseInt(b.getAttribute("nd"));}
		}	
		return times;
	}
	private static int getAmplification(Element el, String string) {
		NodeList nl = el.getElementsByTagName(string);
		int times = 0;
		if(nl != null && nl.getLength() > 0) {
			Element b = (Element)nl.item(0);
			String timess = b.getAttribute("amplification");
			if(!timess.isEmpty()){times = Integer.parseInt(b.getAttribute("amplification"));}
		}	
		return times;
	}
	private static Behaviour getBehaviour(String tgt,TransitionSystem env) {
		FileInputStream file = null;
		Behaviour bs = null;
		HashMap<Transition,ArrayList<String>> guards = null;
		try {
			file= new FileInputStream(tgt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bparser = new parserBS(file);
		try {
			bs = bparser.createTS();
			guards = bparser.getGuards();
			Set<Transition> guardKeys = guards.keySet();
			for(Transition t :guardKeys)
			{
				for(String estate:guards.get(t))
				{
					if(!estate.isEmpty() && !estate.equals("*")){
						bs.addApplicableState(t, env.getState(env.getStateNumber(estate)));
					}
				}				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bs;
	}
	private static TransitionSystem getEnvironment(String env) {
		// TODO Auto-generated method stub
		TransitionSystem ts = null;
		FileInputStream file = null;
		try {
			file = new FileInputStream(env);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		parser = new parserTS(file);
		try {
			ts = parser.createTS();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}


	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	private static void testBehaviourParser() {
		FileInputStream file = null;
		Behaviour bs = null;
		HashMap<Transition,ArrayList<String>> guards = null;
		try {
			file= new FileInputStream("/home/nitin/workspace/BehaviourCompositionFramework/testbed/sample/armA.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bparser = new parserBS(file);
		try {
			bs = bparser.createTS();
			guards = bparser.getGuards();
			Set<Transition> guardKeys = guards.keySet();
			for(Transition t :guardKeys)
			{
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(bs);
	}
	private static void testEnvironmentParser() {
		FileInputStream file = null;
		TransitionSystem ts = null;
		HashMap<Transition,ArrayList<String>> guards = null;
		try {
			file= new FileInputStream("/home/nitin/workspace/BehaviourCompositionFramework/testbed/sample/environment.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		parser = new parserTS(file);
		try {
			ts = parser.createTS();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ts);
		TransitionSystemGraph graphview = new TransitionSystemGraph(ts);
		graphview.draw("testenvironment");
	}
	private static void redirectToFile(String filename)
	{
		//code to redirect console output to file
 
        PrintStream fileStream  = null;  
        try  
        {  
            // Saving the orginal stream  

            File file = new File(filename);
            if(!file.exists())
            {
            	file.createNewFile();
            }
            fileStream = new PrintStream(new FileOutputStream(filename,true));  
            // Redirecting console output to file  
            System.setOut(fileStream);  
            // Redirecting runtime exceptions to file  
            System.setErr(fileStream);  
//            throw new Exception("Test Exception");    
        }  
        catch (FileNotFoundException fnfEx)  
        {  
           System.out.println("Error in IO Redirection");  
            fnfEx.printStackTrace();  
        }  
       catch (Exception ex)  
        {  
            //Gets printed in the file  
            System.out.println("Redirecting output & exceptions to file");  
            ex.printStackTrace();  
        }  
        finally  
        {  
            //Restoring back to console  
           // System.setOut(orgStream);  
            //Gets printed in the console  
           // System.out.println("Redirecting file output back to console");  
  
        } 
	}
	private static void redirectToConsole()
	{
        PrintStream orgStream   = System.out; 
		//Restoring back to console  
         System.setOut(orgStream);  
         //Gets printed in the console  
         System.out.println("Redirecting file output back to console");  
	}
}
