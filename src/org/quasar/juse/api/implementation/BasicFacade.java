/*
 * J-USE - Java prototyping for the UML based specification environment (USE)
 * Copyright (C) 2021 Fernando Brito e Abreu, QUASAR research group
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.quasar.juse.api.implementation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.quasar.juse.api.JUSE_BasicFacade;
import org.quasar.toolkit.SourceFileWriter;
import org.tzi.use.config.Options;
import org.tzi.use.main.Session;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.util.Log;
import org.tzi.use.util.USEWriter;

/***********************************************************
 * @author Fernando Brito e Abreu
 * @version 19 April 2012 - Original version
 * @version October 2018 - Extended and commented version
 ***********************************************************/
public class BasicFacade implements JUSE_BasicFacade
{
    private Session session = new Session();
    private MSystem system = null;
    private Shell shell = null;

    /***********************************************************
     * Basic facade constructor
     ***********************************************************/
    public BasicFacade()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quasar.juse.api.JUSE_BasicFacade#getSystem()
     */
    public MSystem getSystem()
    {
	return system;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quasar.juse.api.JUSE_BasicFacade#initialize(java.lang.String[],
     * java.lang.String, java.lang.String)
     */
    public JUSE_BasicFacade initialize(String[] args, String modelDirectory)
    {
	// set System.out to the OldUSEWriter to protocol the output.
	System.setOut(USEWriter.getInstance().getOut());
	// set System.err to the OldUSEWriter to protocol the output.
	System.setErr(USEWriter.getInstance().getErr());

//		String[] initialArgs = { "-H=" + useBaseDirectory };

	// Command line arguments or in Eclipse "Run As/Run Configurations/Arguments"
	// are appended with
	// the initialArgs (USE instalation directory)

//		String[] args2 = new String[args.length + 1];
//		for (int i = 0; i < args.length; i++)
//			args2[i] = args[i];
//		args2[args.length] = initialArgs[0];
//
//		// read and set global options, setup application properties
//		Options.processArgs(args2);

	Options.EVAL_NUMTHREADS = 6;
	Options.doGUI = false;
	Options.doPLUGIN = false;
	Options.setDebug(false);
//		Options.quiet = true;
//	    Options.quietAndVerboseConstraintCheck = false;

	Log.setShowWarnings(false);
	Log.setDebug(false);

	// System.out.println("user.dir=" + System.getProperty("user.dir"));
	// System.out.println("lastDirectory=" + Options.getLastDirectory());

	// set current model directory
	if (modelDirectory != null)
	{
	    System.setProperty("user.dir", modelDirectory);
	    Options.setLastDirectory(Paths.get(modelDirectory));
	}
	// System.out.println("user.dir=" + System.getProperty("user.dir"));
	// System.out.println("lastDirectory=" + Options.getLastDirectory());

//		Options.quiet = false;

	// compile spec if filename given as command line argument
	if (Options.specFilename != null)
	    compileSpecification(Options.specFilename, false);

	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.quasar.juse.api.JUSE_BasicFacade#compileSpecification(java.lang.String, boolean)
     */
    public MSystem compileSpecification(String specificationFilename, boolean verbose)
    {
	MModel model = null;

	// compile spec if filename given as argument
	if (specificationFilename != null)
	{
	    specificationFilename = System.getProperty("user.dir") + "/" + specificationFilename;

	    FileInputStream specStream = null;
	    try
	    {
		if (verbose)
		    System.out.println("\nCompiling model " + specificationFilename);

		specStream = new FileInputStream(specificationFilename);
		model = USECompiler.compileSpecification(specStream, specificationFilename, new PrintWriter(System.err),
			new ModelFactory());
	    } catch (FileNotFoundException e)
	    {
		Log.error("File `" + specificationFilename + "' not found.");
		return null;
//		System.exit(1);
	    } finally
	    {
		if (specStream != null)
		    try
		    {
			specStream.close();
		    } catch (IOException ex)
		    {
			// ignored
		    }
	    }

	    // compile errors?
	    if (model == null)
	    {
		return null;
		//		System.exit(1);
	    }

	    if (Options.compileOnly)
	    {
		Log.verbose("no errors.");
		if (Options.compileAndPrint)
		{
		    model = USECompiler.compileSpecification(specStream, specificationFilename,
			    new PrintWriter(System.err), new ModelFactory());
		}
		return null;
//		System.exit(0);
	    }

	    // print some info about model
	    Log.verbose(model.getStats());
	}

	else
	{
	    model = new ModelFactory().createModel("empty model");
	    Log.verbose("using empty model.");
	}
	// create system, session and shell
	system = new MSystem(model);

	session.setSystem(system);
	Shell.createInstance(session, null);
	shell = Shell.getInstance();

	return system;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quasar.juse.api.JUSE_BasicFacade#readSOIL(java.lang.String, java.lang.String, boolean)
     */
    public boolean readSOIL(String modelInstancesDirectory, String modelInstancesFilename, boolean verbose)
    {
	boolean result = false;
	if (system == null || system.model() == null)
	{
	    System.out.println("Please compile the model first!");
	    return result;
	}
	if (verbose)
	    System.out.println("\n\t Generating USE snapshot for the " + system.model().name() + " prototype");

	String instancesFilename = modelInstancesDirectory + "/" + modelInstancesFilename;

	if (verbose)
	    System.out.println("\t - started reading SOIL file " + instancesFilename);

	// Unfortunately none of these 2 simple options work :( ...
	// command("open " + instancesFilename);
	// shell.cmdRead(instancesFilename, quiet);
	// command("info state");

	// ... so let's do it the hard way ...
	FileReader fr = null;
	try
	{
	    fr = new FileReader(instancesFilename);

	    BufferedReader br = new BufferedReader(fr);
	    String s = null;
	    try
	    {
//				System.out.print("\t ...");
		int line = 0;
		while ((s = br.readLine()) != null)
		{
		    line++;

		    if (verbose)
//						System.out.println(s);
//					else
		    {
			if (line % 200 == 0)
			    System.out.print(".");
			if (line % 10000 == 0)
			    System.out.println("\t");
		    }

		    if (!s.contains("info state") && !s.contains("check"))
			shell.processLineSafely(s);
		}
		if (verbose)
		    System.out.println("\n\t - finished reading " + line + " lines");
		fr.close();
		result = true;
	    } catch (IOException e)
	    {
		e.printStackTrace();
	    }
	} catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quasar.juse.api.JUSE_BasicFacade#dumpState(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public boolean dumpState(String author, String javaWorkspace, String cmdFile, boolean verbose)
    {
	boolean result = false;

	if (system == null || system.model() == null)
	{
	    System.out.println("Please compile the model first!");
	    return result;
	}

//		String targetDirectory = javaWorkspace + "/" + system.model().name() + "/data";
	String targetDirectory = javaWorkspace;

	MSystemState systemState = system.state();

	String command;

	if (!SourceFileWriter.openSourceFile(targetDirectory, cmdFile))
	{
	    System.out.println("ERROR: cannot dump model snapshot to " + targetDirectory + "/" + cmdFile);
	    return result;
	} else
	    System.out.println("Dumping model snapshot to " + targetDirectory + "/" + cmdFile);

	SourceFileWriter.println("-------------------------------------------------------------------");
	SourceFileWriter.println("-- author: " + author);
	SourceFileWriter.println("-------------------------------------------------------------------");
	SourceFileWriter.println("reset");
	SourceFileWriter.println();

	// generate regular objects
	int line = 0;
	for (MObject theObject : systemState.allObjects())
	    if (!(theObject instanceof MLink))
	    {
		line++;
		command = "!create " + theObject.name() + ": " + theObject.cls().name();
		SourceFileWriter.println(command);
		if (verbose)
		    System.out.println(command);
	    }
	System.out.println("\t- wrote " + line + " create object commands");

	SourceFileWriter.println("-------------------------------------------------------------------");

	line = 0;
	// generate regular link objects whose connected objects are regular objects
	for (MObject theObject : systemState.allObjects())
	    if (theObject instanceof MLink)
	    {
		MLink theLink = (MLink) theObject;

		if (!(theLink.linkedObjects().get(0) instanceof MLink)
			&& !(theLink.linkedObjects().get(1) instanceof MLink))
		{
		    line++;
		    command = "!create " + theObject.name() + ": " + theObject.cls().name() + " between ("
			    + theLink.linkedObjects().get(0).name() + ", " + theLink.linkedObjects().get(1).name()
			    + ")";
		    SourceFileWriter.println(command);
		    if (verbose)
			System.out.println(command);
		}
	    }

	// generate regular link objects whose connected objects are link objects
	for (MObject theObject : systemState.allObjects())
	    if (theObject instanceof MLink)
	    {
		MLink theLink = (MLink) theObject;

		if (theLink.linkedObjects().get(0) instanceof MLink || theLink.linkedObjects().get(1) instanceof MLink)
		{
		    line++;
		    command = "!create " + theObject.name() + ": " + theObject.cls().name() + " between ("
			    + theLink.linkedObjects().get(0).name() + ", " + theLink.linkedObjects().get(1).name()
			    + ")";
		    SourceFileWriter.println(command);
		    if (verbose)
			System.out.println(command);
		}
	    }
	System.out.println("\t- wrote " + line + " create link object commands");

	SourceFileWriter.println("-------------------------------------------------------------------");

	line = 0;
	// generate regular links
	for (MLink theLink : systemState.allLinks())
	    if (!(theLink instanceof MObject))
	    {
		line++;
		command = "!insert (" + theLink.linkedObjects().get(0).name() + ", "
			+ theLink.linkedObjects().get(1).name() + ") into " + theLink.association().name();
		SourceFileWriter.println(command);
		if (verbose)
		    System.out.println(command);
	    }
	System.out.println("\t- wrote " + line + " insert link commands");

	SourceFileWriter.println("-------------------------------------------------------------------");

	line = 0;
	// set objects state
	for (MObject theObject : systemState.allObjects())
	{
	    MObjectState objectState = theObject.state(systemState);
	    for (MAttribute attribute : theObject.cls().allAttributes())
	    {
		if (objectState.attributeValue(attribute).isDefined())
		{
		    line++;
		    command = "!set " + theObject.name() + "." + attribute.name() + " := "
			    + objectState.attributeValue(attribute);

		    command.replaceAll("\\\\", "\\\\\\\\");

		    SourceFileWriter.println(command);
		    if (verbose)
			System.out.println(command);
		}
	    }
	}
	System.out.println("\t- wrote " + line + " set object state commands");

	SourceFileWriter.closeSourceFile();

	// print some info about snapshot
	// System.out.println("Specification " + system.model().name() + " snapshot (" +
	// systemState.allObjects().size() +
	// " objects, "
	// + systemState.allLinks().size() + " links)");

	System.out.println("Model snapshot dump concluded!\n");

	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quasar.juse.api.JUSE_BasicFacade#command(java.lang.String)
     */
    public void command(String commandLine)
    {
	shell.processLineSafely(commandLine);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quasar.juse.api.JUSE_BasicFacade#createShell()
     */
    public void createShell()
    {
	Thread t = new Thread(shell);
	t.start();

	// wait on exit from shell (this thread never returns)
	try
	{
	    t.join();
	} catch (InterruptedException ex)
	{
	    // ignored
	}
    }
}