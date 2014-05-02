package com.esf.tm;

import java.util.ArrayList;
import java.util.Collections;

import ec.util.MersenneTwisterFast;

/**
 * 
 * A test, containing multiple questions.
 * 
 * @author Jonathan Ni
 * @since 5/2/14
 * @version 0.0r1
 * 
 */

public class Test
{
    private ArrayList<Question> questionList = new ArrayList<Question>();

    /**
     * 
     * Default constructor.
     * 
     */

    public Test()
    {

    }

    /**
     * 
     * Copy constructor. Copies all the questions into the new test.
     * 
     * @param other
     *            the test
     */

    public Test(Test other)
    {
	for (Question i : other.questionList)
	    questionList.add(new Question(i));
    }

    /**
     * 
     * Adds a question to the test.
     * 
     * @param question
     *            the question
     */

    void addQuestion(Question question)
    {
	questionList.add(question);
    }

    /**
     * 
     * Sets a question in the question list to a new question. If the index is
     * out of bounds the method throws an IllegalArgumentException.
     * 
     * @param index
     *            the index of the question
     * @param question
     *            the new question
     * 
     * @throws IllegalArgumentException
     */

    void setQuestion(int index, Question question)
    {
	if (index < 0 || index >= questionList.size())
	    try
	    {
		throw new IllegalArgumentException("Index out of bounds");
	    } catch (IllegalArgumentException e)
	    {
		ErrorReporter.reportError("question index out of bounds",
			Util.stackTraceToString(e));
	    }

	questionList.set(index, question);
    }

    /**
     * 
     * Removes a question from the list of questions. If the index is out of
     * bounds the method throws an IllegalArgumentException.
     * 
     * @param index
     *            the index of the question
     * 
     * @throws IllegalArgumentException
     */

    void removeQuestion(int index)
    {
	if (index < 0 || index >= questionList.size())
	    try
	    {
		throw new IllegalArgumentException("Index out of bounds");
	    } catch (IllegalArgumentException e)
	    {
		ErrorReporter.reportError("question index out of bounds",
			Util.stackTraceToString(e));
	    }

	questionList.remove(index);
    }

    /**
     * 
     * Assigns the IDs to the questions in order. This is necessary because when
     * questions are added to the test, their ID should be default 0. Right
     * before mixing, the driver program needs to assign the IDs to the
     * questions, then mix the test as many times as it wants. This ensures that
     * the original ordering is not lost.
     * 
     */

    void assignIDs()
    {
	for (int i = 0; i < questionList.size(); i++)
	    questionList.get(i).setQuestionID(i);
    }

    /**
     * 
     * Shuffle the ordering of the questions, preserving their IDs. The function
     * {@link #assignIDs()} should be called before this function to ensure that
     * IDs are properly assigned.
     * 
     * Also shuffles the order of each question.
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

    Test mix()
    {
	Test mixed = new Test(this);

	MersenneTwisterFast prng = new MersenneTwisterFast();

	for (int i = mixed.questionList.size() - 1; i > 0; i--)
	{
	    int j = prng.nextInt(i + 1);
	    Collections.swap(mixed.questionList, i, j);
	}

	for (int i = 0; i < mixed.questionList.size(); i++)
	{
	    Question entry = mixed.questionList.get(i);
	    if (entry instanceof MCQuestion)
		((MCQuestion) entry).mix();
	}

	return mixed;
    }
}
