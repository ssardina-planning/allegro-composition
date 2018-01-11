import japexDriver.utils.DriverHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import behaviourComposition.aitest.Test;
import behaviourComposition.structure.Behaviour;
import behaviourComposition.structure.TransitionSystem;


public class Main {
	
	private static String outputfile;
	private static String inputfile;
	private static ArrayList<Test> tests;
	private static ArrayList<Behaviour> behaviors;
	private static TransitionSystem environment;
	private static Behaviour target;
	public static void main(String[] args)
	{
		if(!processConfig(args))
		{
			System.exit(0);
		}
		
		if(outputfile!=null)
		{
			showMessage("Starting computing... please be patient. Output will be in "+outputfile);
			redirectToFile(outputfile);
		}
		
		DriverHelper helper = new DriverHelper(inputfile);
		behaviors = helper.getAvailableBehaviours();
		environment = helper.getEnvironment();
		target = helper.getTarget();
		run();
		redirectToConsole();
		System.out.println("All done!");
	}
	private static void run() {
		Test test = new Test(environment, behaviors, target);
		test.run();
	}
	private static boolean processConfig(String[] args) {
		boolean found= false;
		if(args.length<1)
		{
			showMessage("Usage: java Main --i=<inputfile.xml> --o=<outputfile>");
			return false;
		}
		for(String s:args)
		{
			if(s.contains("--i="))
			{
				inputfile = s.substring(("--i=".length()));
				found=true;
			}
			if(s.contains("--o="))
			{
				outputfile = s.substring(("--o=".length()));
			}
		}
		if(!found)
		{
			showMessage("Usage: java Main --i=<inputfile.xml> --o=<outputfile>");
			return false;

		}
		return true;
	}
	private static void showMessage(String string) {
		System.out.println(string);		
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
         //System.out.println("Redirecting file output back to console");  
	}
}
