package behaviourComposition.graphviz;

import java.io.*;
import behaviourComposition.structure.*;

public class TransitionSystemGraph {

	private AbstractTransitionSystem system;
	public TransitionSystemGraph(AbstractTransitionSystem system)
	{
		this.system= system;
	}
	public void draw(String filename)
	{
		GraphViz gv = new GraphViz();
	     gv.addln(gv.start_graph());
	     for(Transition t:system.getTransitions())
	     {
	    	gv.addln(t.getFrom() + "->" + t.getTo()+"[ label = \""+t.getAction().toString()+"\" ];");
	    	 
	     }
	    // gv.addln("A -> B [ label = \"SS(B)\" ];");
	    // gv.addln("A -> C;");
	     gv.addln(gv.end_graph());
	     System.out.println(gv.getDotSource());
	     
	     File out = new File(filename+".gif");
	     gv.writeGraphToFile(gv.getGraph(gv.getDotSource()), out);
	} 
	public void Test(){ 
		GraphViz gv = new GraphViz();
	     gv.addln(gv.start_graph());
	     gv.addln("A -> B [ label = \"SS(B)\" ];");
	     gv.addln("A -> C;");
	     gv.addln(gv.end_graph());
	     System.out.println(gv.getDotSource());
	     
	     File out = new File("out.gif");
	     gv.writeGraphToFile(gv.getGraph(gv.getDotSource()), out);
	}
}
