package org.quasar.toolkit;

//import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

//import javax.swing.JFrame;
//import javax.swing.JOptionPane;

/**
 * @author fba Utility functions for Java source code generator
 */
public abstract class SourceFileWriter
{
    private static final String IDENTATOR = "\t";
    private static int fIndent = 0;
    private static int fIndentStep = 1;
    private static String buffer = "";
    private static PrintWriter fOut = null;

    /***********************************************************
     * @param directoryname The name of the directory where the file to open is placed
     * @param filename     The name of the file to open
     * @return <code>true</code> if the named file exists and therefore opens as requested or <code>false</code> otherwise
     ***********************************************************/
    public static boolean openSourceFile(String directoryname, String filename)
    {
	String file = directoryname + "/" + filename;
	boolean result = true;
	try
	{
	    if (fOut != null)
		fOut.close();
//			File f = new File(file);
	    // if (f.exists())
//			{
//				JFrame frame = new JFrame();
//				int answer = JOptionPane.showConfirmDialog(frame, "The file " + filename
//								+ " already exists!\nDo you want to overwrite it?", "WARNING", JOptionPane.YES_NO_OPTION);
//				frame.dispose();
//				if (answer == JOptionPane.YES_OPTION)
//					fOut = new PrintWriter(new FileWriter(file));
//				else
//					result = false;
//			}
//			else

//			fOut = new PrintWriter(new FileWriter(file));
	    fOut = new PrintWriter(file);
	} catch (IOException e)
	{
	    FileSystemUtilities.createDirectory(directoryname);
	    openSourceFile(directoryname, filename);
	}
	return result;
    }

    /***********************************************************
     * Close the current opened file
     ***********************************************************/
    public static void closeSourceFile()
    {
	if (fOut != null)
	{
	    fOut.flush();
	    fOut.close();
	}
    }

    /***********************************************************
     * @param slist List of strings to be printed, followed by a newline
     ***********************************************************/
    public static void println(String... slist)
    {
	for (int i = 0; i < fIndent; i++)
	    fOut.print(IDENTATOR);
	fOut.print(buffer);
	for (String s : slist)
	    fOut.print(s);
	fOut.println();
	buffer = "";
    }

    /***********************************************************
     * @param s String to be printed as a comment, followed by a newline
     ***********************************************************/
    public static void printlnc(String s)
    {
	println("//" + IDENTATOR + s);
    }

    /***********************************************************
     * @param s String to be printed in the currently opened file (without newline)
     ***********************************************************/
    public static void print(String s)
    {
	buffer += s;
    }

    /***********************************************************
     * Increase the current identation level
     ***********************************************************/
    public static void incIndent()
    {
	fIndent += fIndentStep;
    }

    /***********************************************************
     * Decrease the current identation level
     ***********************************************************/
    public static void decIndent()
    {
	if (fIndent < fIndentStep)
	    throw new RuntimeException("unbalanced indentation");
	fIndent -= fIndentStep;
    }

    /***********************************************************
     * @param s The original string
     * @return The string s with its first letter capitalized
     ***********************************************************/
    public static String capitalize(String s)
    {
	return s.toUpperCase().substring(0, 1) + s.substring(1);
    }

}