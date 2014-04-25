package com.esf.tm;

/**
 * 
 * Choice object. Question class is responsible for disallowing duplicate
 * indexes.
 * 
 * This is an IMMUTABLE class.
 * 
 * @author Jonathan Ni
 * @since 4/25/14
 * @version 0.0r1
 * 
 */

public class Choice
{
    private int index;
    private String msg;

    /**
     * 
     * Create a new Choice object based on an index and a message.
     * 
     * Checks to see if the index is in bounds. If letter choices are turned on,
     * the bound is 0-25, otherwise it is 0+. If the index is not in bounds,
     * this method will throw an IllegalArgumentException.
     * 
     * @param index
     *            the index of this choice in the question
     * @param message
     *            the message of this choice (the text)
     * 
     * @throws IllegalArgumentException
     * 
     */

    public Choice(int index, String message)
    {
	try
	{
	    if (Configuration.isLetterChoice())
	    {
		if (index < 0 || index > 25)
		    throw new IllegalArgumentException();
	    } else if (index < 0)
		throw new IllegalArgumentException();
	} catch (IllegalArgumentException e)
	{
	    ErrorReporter.reportError("Index for choice out of bounds",
		    Util.stackTraceToString(e));
	}

	this.index = index;
	this.msg = message;
    }

    /**
     * 
     * Gets the index of the choice.
     * 
     * @return the index
     */

    public int getIndex()
    {
	return index;
    }

    /**
     * 
     * Gets the message of the choice.
     * 
     * @return the message
     */

    public String getMessage()
    {
	return msg;
    }

    /**
     * 
     * Returns a representation of the Choice object.
     * 
     * If letter choices are turned on, this will check if the index is within
     * acceptable ranges of the alphabet and return a letter prefix along with
     * the message. Otherwise, it will set the prefix to "?". Due to the check
     * for index bounds in the constructor, this should not occur.
     * 
     * If letter choices are turned off, this method will assign the index to
     * the prefix.
     * 
     */

    @Override
    public String toString()
    {
	String prefix = "";
	if (Configuration.isLetterChoice())
	    if (index > 0 && index < 26)
		prefix += "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()[index];
	    else
		prefix += "?";
	else
	    prefix += index;

	return prefix + ") " + msg;
    }
}
