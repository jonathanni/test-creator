package com.esf.tm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Import
{
	String line = "";
	int questionNumber = 0;
	int latest = 0;
	Test myTest = new Test();

	/**
	 * 
	 * This is the method to call when importing a test.
	 * 
	 * @param file
	 *            the file that will be translated into a test
	 */
	Test importNew(File file)
	{
		// converts file into one string
		try
		{
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
			{
				line += scanner.nextLine();
				line += "\n";
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		while (questionNumber >= 0)
		{
			int s = line.indexOf(String.valueOf(questionNumber) + ".", latest); // gets
																				// number
																				// of
																				// question
			if (s == -1)
			{ // if no questions left, exit loop
				break;
			}
			receiveQuestion(s);
			questionNumber++;
		}

		return myTest;
	}

	/**
	 * 
	 * After a question has been found, this helper method will determine what
	 * type of question it is and call the corresponding method depending on
	 * this type.
	 * 
	 * @param start
	 *            the starting index for the question; this is also the number
	 *            of the question (ex. "1. ")
	 */
	void receiveQuestion(int start)
	{
		// gets question
		int end = line.indexOf('\n', start + 3);
		String questionstr = line.substring(start + 3, end);

		// finds type of question and creates question according to type
		int parenIndex = line.indexOf("(", end);
		int nextNumIndex = line.indexOf(String.valueOf(questionNumber + 1)
				+ ".");
		if (nextNumIndex == -1)
		{
			nextNumIndex = line.length();
		}
		int endIndex = line.indexOf("\n", parenIndex);

		if (parenIndex == -1 || parenIndex > nextNumIndex)
		{
			int blankIndex = line.indexOf("Blank", end);
			addFIB(questionstr, blankIndex);
		} else
		{
			String choicestr = line.substring(parenIndex + 4, endIndex);
			if (choicestr.equalsIgnoreCase("true")
					|| choicestr.equalsIgnoreCase("false"))
			{
				addTF(questionstr, parenIndex);
			} else
			{
				addMC(questionstr, parenIndex);
			}
		}

	}

	/**
	 * 
	 * Creates a new TFQuestion and adds it to the test; also determines whether
	 * the answer is true or false.
	 * 
	 * @param q
	 *            the string that stores the question
	 * @param p
	 *            the integer that stores the index of the first parenthesis;
	 *            this helps in determining whether the answer is true or false
	 */
	void addTF(String q, int p)
	{
		TFQuestion question = new TFQuestion(q, 0);
		// determines whether correct answer is true or false
		if (line.substring(p, p + 1).equals("*"))
		{
			question.setCorrectAnswer(true);
		} else
		{
			question.setCorrectAnswer(false);
		}

		myTest.addQuestion(question);
	}

	/**
	 * 
	 * Creates a new MCQuestion and adds it to the test; also finds all answers
	 * and determines which is correct.
	 * 
	 * @param q
	 *            the string that stores the question
	 * @param p
	 *            the integer that stores the index of the first parenthesis;
	 *            this gives a starting point for finding all answer choices
	 */
	void addMC(String q, int p)
	{
		MCQuestion question = new MCQuestion(q, 0);
		int endIndex;
		int endAllIndex = line
				.indexOf(String.valueOf(questionNumber + 1) + ".");
		int findID = 0;
		int correctID = 0;
		// gets answer choices
		while (p != -1 && p < endAllIndex)
		{
			endIndex = line.indexOf("\n", p);
			latest = endIndex;
			question.addChoice(new Choice(line.substring(p + 6, endIndex)));
			if (line.substring(p, p + 1).equals("*"))
			{
				correctID = findID;
			} else
			{
				findID++;
			}
			p = line.indexOf("(", endIndex);
		}

		// sets correct answer choice
		question.assignIDs();
		question.setCorrectAnswer(correctID);

		myTest.addQuestion(question);
	}

	/**
	 * 
	 * Creates a new FIBQuestion and adds it to the test; also finds all blanks
	 * and corresponding correct answers.
	 * 
	 * @param q
	 *            the string that stores the question
	 * @param p
	 *            the integer that stores the index of the first 'Blank'; this
	 *            gives a starting point for finding all blanks and answer
	 *            choices
	 */
	void addFIB(String q, int p)
	{
		FIBQuestion question = new FIBQuestion(q, 0);
		int startIndex = line.indexOf("\n    ", p);
		int newlineIndex = line.indexOf("Blank", p + 1);
		int endAllIndex = line
				.indexOf(String.valueOf(questionNumber + 1) + ".");
		int endIndex;
		int blankIndex = 0;
		// finds blanks

		if (endAllIndex == -1)
			endAllIndex = line.length() - 1;

		while (p != -1 && p < endAllIndex)
		{
			while (startIndex != -1 && startIndex < newlineIndex)
			{ // finds answers
				endIndex = line.indexOf("\n", startIndex + 1);
				if (endIndex == -1 || endIndex >= newlineIndex)
				{
					latest = endIndex;

					question.setBlankSpaces(blankIndex + 1);
					for (int i = 0; i < blankIndex + 1; i++)
						question.prepare(i);

					question.addAnswer(blankIndex,
							line.substring(startIndex + 5, endIndex));
					break;
				}
				question.setBlankSpaces(blankIndex + 1);
				for (int i = 0; i < blankIndex + 1; i++)
					question.prepare(i);

				question.addAnswer(blankIndex,
						line.substring(startIndex + 5, endIndex));
				// finds next starting point
				startIndex = line.indexOf("\n    ", startIndex + 1);
			}
			int temp = newlineIndex + 1;
			newlineIndex = line.indexOf("Blank", temp);
			if (newlineIndex == -1)
			{
				newlineIndex = line.indexOf("\n\n", temp);
			}
			p = line.indexOf("Blank", p + 1);
			startIndex = line.indexOf("\n    ", p);
			blankIndex++;
		}
		// C:\Users\Jonathan\git\client-project-test-creator\ClientProject\keys\asdfasdf.key.-1.txt

		myTest.addQuestion(question);
	}

}
