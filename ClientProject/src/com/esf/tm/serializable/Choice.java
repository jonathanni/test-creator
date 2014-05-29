package com.esf.tm.serializable;

import java.io.Serializable;

import com.esf.tm.Configuration;
import com.esf.tm.ErrorReporter;
import com.esf.tm.Util;

/**
 * 
 * Choice object. Question class is responsible for disallowing duplicate
 * indexes.
 * 
 * This class is MUTABLE.
 * 
 * @author Jonathan Ni
 * @since 4/25/14
 * @version 0.0r2
 * 
 */

public class Choice implements Serializable
{
	private static final long serialVersionUID = -4393644679307887868L;
	private int choiceID;
    private String msg;

    /**
     * 
     * Create a new Choice object based on an index and a message.
     * 
     * Checks to see if the index is in bounds. If letter choices are turned on,
     * the bound is 0-25, otherwise it is 0+. If the index is not in bounds,
     * this method will throw an IllegalArgumentException.
     * 
     * @param message
     *            the message of this choice (the text)
     * 
     * @throws IllegalArgumentException
     * 
     */

    public Choice(String message)
    {
	setMessage(message);
    }

    /**
     * 
     * Gets the ID of this choice.
     * 
     * @return
     */

    public int getChoiceID()
    {
	return choiceID;
    }

    /**
     * 
     * Sets the ID of this choice. If letter choices are turned on, this checks
     * to see if the ID is in the range 0-25. Otherwise it checks to see if it
     * is 0+.
     * 
     * @param ID
     *            the ID
     */

    public void setChoiceID(int ID)
    {
	try
	{
	    if (Configuration.isLetterChoice())
	    {
		if (choiceID < 0 || choiceID > 25)
		    throw new IllegalArgumentException();
	    } else if (choiceID < 0)
		throw new IllegalArgumentException();
	} catch (IllegalArgumentException e)
	{
	    ErrorReporter.reportError("Index for choice out of bounds",
		    Util.stackTraceToString(e));
	}

	this.choiceID = ID;
    }

    public String getMessage()
    {
	return msg;
    }

    public void setMessage(String msg)
    {
	this.msg = msg;
    }

    /**
     * 
     * Returns a representation of the Choice object.
     * 
     * If letter choices are turned on, this will check if the ID is within
     * acceptable ranges of the alphabet and return a letter prefix along with
     * the message. Otherwise, it will set the prefix to "?". Due to the check
     * for index bounds in the Question, this should not occur.
     * 
     * If letter choices are turned off, this method will assign the index to
     * the prefix.
     * 
     */

    @Override
    public String toString()
    {
	/*
	String prefix = "";
	if (Configuration.isLetterChoice())
	    if (choiceID > 0 && choiceID < 26)
		prefix += "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()[choiceID];
	    else
		prefix += "?";
	else
	    prefix += choiceID;

	return prefix + ") " + msg + "\n";
	*/
	
	return msg + "\n";
    }
}
