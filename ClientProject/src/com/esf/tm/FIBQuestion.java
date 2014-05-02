package com.esf.tm;

import java.util.ArrayList;

/**
 * 
 * Fill-in-the-blank question.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r1
 * 
 */

public class FIBQuestion extends Question
{
    private ArrayList<String> correctAnswers = new ArrayList<String>();

    private boolean isCaseSensitive, isWhitespaceSensitive;

    /**
     * 
     * See {@link Question#Question(String, int)}
     * 
     * @param message
     * @param ID
     */

    public FIBQuestion(String message, int ID)
    {
	super(message, ID);
    }

    /**
     * 
     * Adds an answer to the list of correct answers.
     * 
     * @param answer
     *            a correct answer
     */

    void addAnswer(String answer)
    {
	correctAnswers.add(answer);
    }

    /**
     * 
     * Sets an answer in the list to another. If the index is out of bounds the
     * method throws an IllegalArgumentException.
     * 
     * @param index
     *            the index of the answer
     * @param answer
     *            the new answer
     * 
     * @throws IllegalArgumentException
     */

    void setAnswer(int index, String answer)
    {
	if (index < 0 || index >= correctAnswers.size())
	    try
	    {
		throw new IllegalArgumentException("Index out of bounds");
	    } catch (IllegalArgumentException e)
	    {
		ErrorReporter.reportError("Answer index out of bounds",
			Util.stackTraceToString(e));
	    }

	correctAnswers.add(answer);
    }

    /**
     * 
     * Removes an answer from the list of correct answers. If the index is out
     * of bounds the method throws an IllegalArgumentException.
     * 
     * @param index
     *            the index of the answer
     * 
     * @throws IllegalArgumentException
     */

    void removeAnswer(int index)
    {
	if (index < 0 || index >= correctAnswers.size())
	    try
	    {
		throw new IllegalArgumentException("Index out of bounds");
	    } catch (IllegalArgumentException e)
	    {
		ErrorReporter.reportError("Answer index out of bounds",
			Util.stackTraceToString(e));
	    }
    }

    /**
     * 
     * Gets an array copy containing the list of correct answers.
     * 
     * @return the correct answers
     */

    public String[] getCorrectAnswers()
    {
	String[] list = new String[correctAnswers.size()];
	correctAnswers.toArray(list);
	return list;
    }

    /**
     * 
     * Gets if input to this question is case sensitive.
     * 
     * @return the flag
     */

    public boolean isCaseSensitive()
    {
	return isCaseSensitive;
    }

    /**
     * 
     * Sets if input to this question is case sensitive.
     * 
     * @param isCaseSensitive
     *            the flag
     */

    public void setCaseSensitive(boolean isCaseSensitive)
    {
	this.isCaseSensitive = isCaseSensitive;
    }

    /**
     * 
     * Gets if input to this question is whitespace sensitive, including
     * whitespace in the middle of the answer.
     * 
     * @return the flag
     */

    public boolean isWhitespaceSensitive()
    {
	return isWhitespaceSensitive;
    }

    /**
     * 
     * Sets if input to this question is whitespace sensitive, including
     * whitespace in the middle of the answer.
     * 
     * @param isWhitespaceSensitive
     *            the flag
     */

    public void setWhitespaceSensitive(boolean isWhitespaceSensitive)
    {
	this.isWhitespaceSensitive = isWhitespaceSensitive;
    }
}
