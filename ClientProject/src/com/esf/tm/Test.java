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
 * @version 0.0r5
 * 
 */

public class Test
{
	private ArrayList<Question> questionList = new ArrayList<Question>();
	private String testTitle, testDescription;

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
		setTestTitle(other.getTestTitle());
		setTestDescription(other.getTestDescription());

		for (Question i : other.questionList)
			if (i instanceof MCQuestion)
				questionList.add(new MCQuestion((MCQuestion) i));
			else if (i instanceof TFQuestion)
				questionList.add(new TFQuestion((TFQuestion) i));
			else if (i instanceof FIBQuestion)
				questionList.add(new FIBQuestion((FIBQuestion) i));
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
	 * Gets a question from the list of questions. If the index is out of bounds
	 * the method throws an IllegalArgumentException.
	 * 
	 * @param index
	 *            the index of the question
	 * @return the question at the index
	 * 
	 * @throws IllegalArgumentException
	 */

	Question getQuestion(int index)
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

		return questionList.get(index);
	}

	/**
	 * 
	 * Gets the title of this test.
	 * 
	 * @return the title
	 */

	public String getTestTitle()
	{
		return testTitle;
	}

	/**
	 * 
	 * Sets the title of this test.
	 * 
	 * @param testTitle
	 *            the title
	 */

	public void setTestTitle(String testTitle)
	{
		this.testTitle = testTitle;
	}

	/**
	 * 
	 * Gets the description of this test.
	 * 
	 * @return the description.
	 */

	public String getTestDescription()
	{
		return testDescription;
	}

	/**
	 * 
	 * Sets the description of this test.
	 * 
	 * @param testDescription
	 *            the description
	 */

	public void setTestDescription(String testDescription)
	{
		this.testDescription = testDescription;
	}

	/**
	 * 
	 * Gets how many questions are in the test.
	 * 
	 * @return the question count
	 */

	public int getQuestionCount()
	{
		return questionList.size();
	}

	int getPointWorth()
	{
		int points = 0;
		for (Question i : questionList)
			points += i instanceof FIBQuestion ? ((FIBQuestion) i)
					.getBlankSpaces() : 1;

		return points;
	}

	/**
	 * 
	 * Assigns the IDs to the questions in order. This is necessary because when
	 * questions are added to the test, their ID should be default 0. Right
	 * before mixing, the driver program needs to assign the IDs to the
	 * questions, then mix the test as many times as it wants. This ensures that
	 * the original ordering is not lost.
	 * 
	 * Additionally, if the Question is an MCQuestion, assign IDs there.
	 * 
	 */

	void assignIDs()
	{
		for (int i = 0; i < questionList.size(); i++)
		{
			Question q = questionList.get(i);

			q.setQuestionID(i);
			if (q instanceof MCQuestion)
				((MCQuestion) q).assignIDs();
		}
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

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();

		string.append("Test title: " + testTitle + "\nTest description: "
				+ testDescription + "\n");

		for (Question i : questionList)
			string.append(i.toString());

		return string.toString();
	}
}
