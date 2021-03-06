package com.esf.tm.serializable;

import java.io.Serializable;

/**
 * 
 * Base question class. Since choices for questions can vary a lot, this is left
 * for the subclasses to implement.
 * 
 * This class is MUTABLE.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r2
 * 
 */

public class Question implements Serializable
{
	private static final long serialVersionUID = -3380460923523617609L;
	private String message;
	private int questionID;

	/**
	 * 
	 * Creates a new Question from a message and an ID (which would be
	 * internal).
	 * 
	 * @param message
	 *            the message of the question
	 * @param ID
	 *            the ID number, which will be unique
	 */

	public Question(String message, int ID)
	{
		setMessage(message);
		setQuestionID(ID);
	}

	/**
	 * 
	 * Copy constructor.
	 * 
	 * @param other
	 *            the question
	 */

	public Question(Question other)
	{
		setMessage(other.message);
		setQuestionID(other.questionID);
	}

	/**
	 * 
	 * Gets the message associated with this Question.
	 * 
	 * @return the message
	 */

	public String getMessage()
	{
		return message;
	}

	/**
	 * 
	 * Sets the message associated with this Question.
	 * 
	 * @param message
	 *            the message
	 */

	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * 
	 * Gets the unique ID associated with this Question.
	 * 
	 * @return the ID
	 */

	public int getQuestionID()
	{
		return questionID;
	}

	/**
	 * 
	 * Gets the unique ID associated with this Question.
	 * 
	 * @param questionID
	 *            the ID
	 */

	public void setQuestionID(int questionID)
	{
		this.questionID = questionID;
	}

	@Override
	public String toString()
	{
		return "Question ID: " + questionID + "\nQuestion Description: "
				+ message + "\n";
	}
}
