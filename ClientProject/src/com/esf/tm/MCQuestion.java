package com.esf.tm;

import java.util.ArrayList;
import java.util.Collections;

import ec.util.MersenneTwisterFast;

/**
 * 
 * Multiple choice question.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r7
 * 
 */

class MCQuestion extends Question
{
    private ArrayList<Choice> choiceList = new ArrayList<Choice>();
    private int correctAnswer;
    private boolean isMixable;

    /**
     * 
     * See {@link Question#Question(String, int)}
     * 
     * @param message
     *            the message of the question
     * @param ID
     *            the ID number, which will be unique
     */

    public MCQuestion(String message, int ID)
    {
	super(message, ID);
    }

    /**
     * 
     * Constructs a new multiple choice question, except also adds all the
     * answer choices beforehand.
     * 
     * @param message
     *            the message of the question
     * @param ID
     *            the ID number, which will be unique
     * @param choice
     *            a list of all the choices
     */

    public MCQuestion(String message, int ID, ArrayList<Choice> choice)
    {
	super(message, ID);
	choiceList.addAll(choice);
    }

    /**
     * 
     * Copy constructor.
     * 
     * @param other
     *            the question
     */

    public MCQuestion(MCQuestion other)
    {
	this(other.getMessage(), other.getQuestionID(), other.choiceList);
	setCorrectAnswer(other.getCorrectAnswer());
	setMixable(other.isMixable());
    }

    /**
     * 
     * Copy constructor from Question.
     * 
     * @param other
     *            the question
     */

    public MCQuestion(Question other)
    {
	super(other);
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
	choiceList.add(choice);
    }

    /**
     * 
     * Sets a choice in the choice list to a new choice. If the index is out of
     * bounds the method throws an IllegalArgumentException.
     * 
     * @param index
     *            the index of the choice
     * @param choice
     *            the new choice
     * 
     * @throws IllegalArgumentException
     */

    void setChoice(int index, Choice choice)
    {
	if (index < 0 || index >= choiceList.size())
	    try
	    {
		throw new IllegalArgumentException("Index out of bounds");
	    } catch (IllegalArgumentException e)
	    {
		ErrorReporter.reportError("Choice index out of bounds",
			Util.stackTraceToString(e));
	    }

	choiceList.set(index, choice);
    }

    /**
     * 
     * Removes a choice from the list of choices. If the index is out of bounds
     * the method throws an IllegalArgumentException.
     * 
     * @param index
     *            the index of the choice
     * 
     * @throws IllegalArgumentException
     */

    void removeChoice(int index)
    {
	if (index < 0 || index >= choiceList.size())
	    try
	    {
		throw new IllegalArgumentException("Index out of bounds");
	    } catch (IllegalArgumentException e)
	    {
		ErrorReporter.reportError("Choice index out of bounds",
			Util.stackTraceToString(e));
	    }

	choiceList.remove(index);
    }

    /**
     * 
     * Gets a choice from the list of choices. If the index is out of bounds the
     * method throws an IllegalArgumentException.
     * 
     * @param index
     *            the index of the choice
     * @return the choice at the index
     * 
     * @throws IllegalArgumentException
     */

    Choice getChoice(int index)
    {
	if (index < 0 || index >= choiceList.size())
	    try
	    {
		throw new IllegalArgumentException("Index out of bounds");
	    } catch (IllegalArgumentException e)
	    {
		ErrorReporter.reportError("Choice index out of bounds",
			Util.stackTraceToString(e));
	    }

	return choiceList.get(index);
    }

    /**
     * 
     * Gets the ID of the correct answer.
     * 
     * @return the ID
     */

    public int getCorrectAnswer()
    {
	return correctAnswer;
    }

    /**
     * 
     * Sets the ID of the correct answer. Should be performed after choices are
     * assigned IDs.
     * 
     * @param correctAnswer
     */

    public void setCorrectAnswer(int correctAnswer)
    {
	this.correctAnswer = correctAnswer;
    }

    /**
     * 
     * Gets if this question is mixable.
     * 
     * @return the flag
     */

    public boolean isMixable()
    {
	return isMixable;
    }

    /**
     * 
     * Sets if this question is mixable.
     * 
     * @param isMixable
     *            the flag
     */

    public void setMixable(boolean isMixable)
    {
	this.isMixable = isMixable;
    }

    public int getChoiceCount()
    {
	return choiceList.size();
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

    /**
     * 
     * Shuffle the ordering of the choices, preserving their IDs. The function
     * {@link #assignIDs()} should be called before this function to ensure that
     * IDs are properly assigned.
     * 
     * Uses the Fisher-Yates algorithm for shuffling with the Mersenne Twister
     * PRNG implementation. See ec.util.MersenneTwisterFast for copyright
     * details.
     * 
     * Fisher-Yates:
     * 
     * To shuffle an array a of n elements (indices 0..n-1): for i from n − 1
     * downto 1 do j = random integer with 0 <= j <= i exchange a[j] and a[i]
     */

    void mix()
    {
	MersenneTwisterFast prng = new MersenneTwisterFast();

	for (int i = choiceList.size() - 1; i > 0; i--)
	{
	    int j = prng.nextInt(i + 1);
	    Collections.swap(choiceList, i, j);
	}
    }

    @Override
    public String toString()
    {
	StringBuilder string = new StringBuilder();

	for (Choice i : choiceList)
	    string.append(i.toString());

	return super.toString() + string.toString();
    }
}
