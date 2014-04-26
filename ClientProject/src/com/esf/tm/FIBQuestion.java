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

	}

	/**
	 * 
	 * Sets an answer in the list to another.
	 * 
	 * @param index
	 *            the index of the answer
	 * @param answer
	 *            the new answer
	 */

	void setAnswer(int index, String answer)
	{

	}

	/**
	 * 
	 * Removes an answer from the list of correct answers.
	 * 
	 * @param index
	 *            the index of the answer
	 */

	void removeAnswer(int index)
	{

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
}
