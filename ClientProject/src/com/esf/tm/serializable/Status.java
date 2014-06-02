package com.esf.tm.serializable;

import java.io.Serializable;

/**
 * 
 * Status container. Mainly used for serialization.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class Status implements Serializable
{
	private static final long serialVersionUID = -1027946968773323655L;
	private int currentQuestion;

	/**
	 * 
	 * Gets the current question the client is on.
	 * 
	 * @return the current question
	 */

	public int getCurrentQuestion()
	{
		return currentQuestion;
	}

	/**
	 * 
	 * Sets the current question the client is on.
	 * 
	 * @param currentQuestion
	 *            the current question
	 */

	public void setCurrentQuestion(int currentQuestion)
	{
		this.currentQuestion = currentQuestion;
	}
}
