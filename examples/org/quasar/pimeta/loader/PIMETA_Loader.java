/***********************************************************
 * Filename: MainExample.java
 * Created:  24 de Mar de 2012
 ***********************************************************/
package org.quasar.pimeta.loader;


import org.quasar.juse.api.JUSE_ProgramingFacade;
import org.quasar.juse.api.implementation.ProgramingFacade;
import org.tzi.use.uml.mm.MClass;


/***********************************************************
 * @author fba 24 de Mar de 2012
 * 
 ***********************************************************/
public final class PIMETA_Loader
{
	 private final static String MODEL_DIRECTORY = "metamodels/pimeta";
	 private final static String MODEL_FILE = "pimeta.use";
	 private final static String SOIL_FILE = "java.cmd";

	

	/***********************************************************
	 * @param args command line arguments
	 ***********************************************************/
	public static void main(String[] args)
	{
		loadPIMETA(args);
	}


	/***********************************************************
	* 
	***********************************************************/
	static void loadPIMETA(String[] args)
	{
		JUSE_ProgramingFacade api = new ProgramingFacade();

		api.initialize(args, MODEL_DIRECTORY);

		api.compileSpecification(MODEL_FILE, true);

		api.command("check");
		
		api.readSOIL(MODEL_DIRECTORY, SOIL_FILE, true);

//		api.command("info state");

//		api.command("info vars");
		
		api.command("? DependencyType.allInstances->size");
		
		for (MClass aClass: api.allClasses())
		{
			System.out.println(aClass + " -> " + api.allObjects(aClass).size());
		}

	}

}