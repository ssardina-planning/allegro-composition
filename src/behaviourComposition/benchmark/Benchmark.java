package behaviourComposition.benchmark;
import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import structures.ArraySetList;

import behaviourComposition.aitest.Test;
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
		//testEnvironmentParser();
		//testBehaviourParser();
		//String fileurl = "/home/nitin/workspace/BehaviourCompositionFramework/testbed/sample/sampleTest.xml";
		String fileurl = null;
		if(args.length<1){
		fileurl = "testbed/NumberOfBehaviours/KR/KR1xml";
		}
		else {fileurl = args[0];}
		tests = new ArrayList<Test>();
		outputfiles = new ArrayList<String>();
		parsexml(fileurl);
		run();
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
				//get the test object
				Test e = getTest(el);
				//add it to list
				tests.add(e);
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
			for(Transition t:transitions)
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
			ArrayList<Integer> toExpand = new ArrayList<Integer>();
			toExpand.add(initstate);
			
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
}
