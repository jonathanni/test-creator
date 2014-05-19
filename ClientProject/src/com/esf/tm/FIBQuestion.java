package com.esf.tm;

import java.util.ArrayList;

/**
 * 
 * Fill-in-the-blank question.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r2
 * 
 */

public class FIBQuestion extends Question
{
	private ArrayList<ArrayList<String>> correctAnswers = new ArrayList<ArrayList<String>>();

	private boolean isCaseSensitive, isWhitespaceSensitive;
	private int blankSpaces;

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
	 * Copy constructor.
	 * 
	 * @param other
	 *            the question
	 */

	public FIBQuestion(FIBQuestion other)
	{
		this(other.getMessage(), other.getQuestionID());

		for (ArrayList<String> i : other.correctAnswers)
			correctAnswers.add(new ArrayList<String>(i));

		setCaseSensitive(other.isCaseSensitive());
		setWhitespaceSensitive(other.isWhitespaceSensitive());
	}

	/**
	 * 
	 * Copy constructor from Question.
	 * 
	 * @param other
	 *            the question
	 */

	public FIBQuestion(Question other)
	{
		super(other);
	}

	/**
	 * 
	 * Adds an answer to the list of correct answers for a specific blank space.
	 * 
	 * @param blankIndex
	 *            an index of a blank space
	 * @param answer
	 *            a correct answer
	 */

	void addAnswer(int blankIndex, String answer)
	{
		correctAnswers.get(blankIndex).add(answer);
	}

	/**
	 * 
	 * Sets an answer in the list to another. If the index is out of bounds the
	 * method throws an IllegalArgumentException.
	 * 
	 * @param blankIndex
	 *            an index of a blank space
	 * @param index
	 *            the index of the answer
	 * @param answer
	 *            the new answer
	 * 
	 * @throws IllegalArgumentException
	 */

	void setAnswer(int blankIndex, int index, String answer)
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

		correctAnswers.get(blankIndex).set(index, answer);
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
	 * Gets the 2D ArrayList of correct answers.
	 * 
	 * @return the correct answers
	 */

	public ArrayList<ArrayList<String>> getCorrectAnswers()
	{
		return correctAnswers;
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

	/**
	 * 
	 * Gets the number of blank spaces that this question will contain.
	 * 
	 * @return the number of blank spaces
	 */

	public int getBlankSpaces()
	{
		return blankSpaces;
	}

	/**
	 * 
	 * Sets the number of blank spaces that this question will contain.
	 * 
	 * @param blankSpaces
	 *            the number of blank spaces
	 * 
	 * @throws IllegalArgumentException
	 */

	public void setBlankSpaces(int blankSpaces)
	{
		if (blankSpaces < 1)
			try
			{
				throw new IllegalArgumentException(
						"Blank space # must be positive");
			} catch (IllegalArgumentException e)
			{
				ErrorReporter.reportError("Blank space # must be positive",
						Util.stackTraceToString(e));
			}

		this.blankSpaces = blankSpaces;
	}
}
