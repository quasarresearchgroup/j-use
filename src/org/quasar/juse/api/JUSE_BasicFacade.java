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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.quasar.juse.api;

import org.tzi.use.uml.sys.MSystem;

/***********************************************************
 * @author fba
 *
 ***********************************************************/
public interface JUSE_BasicFacade
{

    /***********************************************************
     * @return the current system
     ********************************/
    public MSystem getSystem();

    /***********************************************************
     * @param args           Arguments passed either at command line or in Eclipse
     *                       "Run As/Run Configurations/Arguments"
     * @param modelDirectory Directory where the model to be compiled is
     * @return the initialized Basic Facade
     ***********************************************************/
    public JUSE_BasicFacade initialize(String[] args, String modelDirectory);

    /**********************************************************
     * @param specificationFilename The specification filename (*.use file)
     * @param verbose               If true sends compilation info to console
     * @return the system containing the model
     ***********************************************************/
    public MSystem compileSpecification(String specificationFilename, boolean verbose);

    /***********************************************************
     * Processes a SOIL file
     * 
     * @param modelInstancesDirectory Directory where the SOIL file resides
     * @param modelInstancesFilename  The model instances filename (*.soil file)
     * @param verbose                 If true sends commands to console as well
     * @return <code>true</code> if it was able to read the file correctly or <code>false</code> otherwise
     ***********************************************************/
    public boolean readSOIL(String modelInstancesDirectory, String modelInstancesFilename, boolean verbose);

    /***********************************************************
     * Generates a CMD file containing objects, their setup and links among them
     * 
     * @param author        The author of the specification
     * @param javaWorkspace Workspace directory where the generated commands file is
     *                      to be created
     * @param cmdFile       Name of the commands file
     * @param verbose       If true sends commands to console as well
     * @return <code>true</code> if the model was previously compiled (and therefore was able to dump the state), or <code>false</code> otherwise
     ***********************************************************/
    public boolean dumpState(String author, String javaWorkspace, String cmdFile, boolean verbose);

    /***********************************************************
     * @param commandLine Command line to be processed by shell
     ***********************************************************/
    public void command(String commandLine);

    /***********************************************************
     * Run USE shell; thread gets hung until the shell exits This should be the last
     * call in the program.
     ***********************************************************/
    public void createShell();
}