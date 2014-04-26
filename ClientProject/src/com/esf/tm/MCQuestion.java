package com.esf.tm;

import java.util.ArrayList;

/**
 * 
 * Multiple choice question.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r3
 * 
 */

class MCQuestion extends Question
{
	private ArrayList<Choice> choiceList = new ArrayList<Choice>();
	private int correctAnswer;

	/**
	 * 
	 * See {@link Question#Question(String, int)}
	 * 
	 * @param message
	 * @param ID
	 */

	public MCQuestion(String message, int ID)
	{
		super(message, ID);
	}

	/**
	 * 
	 * Adds a choice to the question.
	 * 
	 * @param choice
	 *            the choice
	 */

	void addChoice(Choice choice)
	{

	}

	/**
	 * 
	 * Sets a choice in the choice list to a new choice.
	 * 
	 * @param index
	 *            the index of the choice
	 * @param choice
	 *            the new choice
	 */

	void setChoice(int index, Choice choice)
	{

	}

	/**
	 * 
	 * Removes a choice from the list of choices
	 * 
	 * @param index
	 *            the index of the choice
	 */

	void removeChoice(int index)
	{

	}

	/**
	 * 
	 * Assigns the IDs to the choices in order. This is necessary because when
	 * choices are added to the question, their ID should be default 0. Right
	 * before mixing, the driver program needs to assign the IDs to the choices,
	 * then mix the question as many times as it wants. This ensures that the
	 * original ordering is not lost.
	 * 
	 */

	void assignIDs()
	{
		for (int i = 0; i < choiceList.size(); i++)
			choiceList.get(i).setChoiceID(i);
	}

	void mix()
	{

	}
}
