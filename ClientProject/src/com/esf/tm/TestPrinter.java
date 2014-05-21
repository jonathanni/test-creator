package com.esf.tm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * Prints the tests out to text files so that the client may pick them up.
 * 
 * @author Jonathan Ni
 * @since 5/21/14
 * @version 0.0r1
 * 
 */

public class TestPrinter
{
    private Test test;
    private int index;

    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	    .toCharArray();

    /**
     * 
     * Creates a new TestPrinter. Binds the test to the TestPrinter.
     * 
     * @param test
     *            the Test
     */

    public TestPrinter(Test test, int index)
    {
	System.out.println(test);
	this.test = test;
	this.index = index;
    }

    void printTest() throws IOException
    {
	System.out.println(test);

	File testFolder = new File("test/");

	if (!testFolder.exists())
	    testFolder.mkdir();

	File keyFolder = new File("keys/");

	if (!keyFolder.exists())
	    keyFolder.mkdir();

	File testFile = new File("test/" + Util.encodeURI(test.getTestTitle())
		+ ".test." + index + ".txt");

	BufferedWriter testOut = new BufferedWriter(new FileWriter(testFile));

	for (int i = 0; i < test.getQuestionCount(); i++)
	{
	    Question q = test.getQuestion(i);

	    testOut.write(i + ". " + q.getMessage() + "\n");

	    if (q instanceof MCQuestion)
		for (int j = 0; j < ((MCQuestion) q).getChoiceCount(); j++)
		    testOut.write("  ( ) "
			    + (j < ALPHABET.length ? String
				    .valueOf(ALPHABET[j]) : "" + j) + " "
			    + ((MCQuestion) q).getChoice(j));
	    else if (q instanceof TFQuestion)
		testOut.write("  ( ) True\n  ( ) False\n");

	    testOut.write("\n");
	}

	testOut.close();

	File keyFile = new File("keys/" + Util.encodeURI(test.getTestTitle())
		+ ".key." + index + ".txt");

	BufferedWriter keyOut = new BufferedWriter(new FileWriter(keyFile));

	for (int i = 0; i < test.getQuestionCount(); i++)
	{
	    Question q = test.getQuestion(i);

	    keyOut.write(i + ". " + q.getMessage() + "\n");

	    if (q instanceof MCQuestion)
		for (int j = 0; j < ((MCQuestion) q).getChoiceCount(); j++)
		    keyOut.write((((MCQuestion) q).getChoice(j).getChoiceID() == ((MCQuestion) q)
			    .getCorrectAnswer() ? "  (*) " : "  ( ) ")
			    + (j < ALPHABET.length ? String
				    .valueOf(ALPHABET[j]) : "" + j)
			    + " "
			    + ((MCQuestion) q).getChoice(j));
	    else if (q instanceof TFQuestion)
		if (((TFQuestion) q).getCorrectAnswer())
		    keyOut.write("  (*) True\n  ( ) False\n");
		else
		    keyOut.write("  ( ) True\n  (*) False\n");
	    else
		for (int j = 0; j < ((FIBQuestion) q).getBlankSpaces(); j++)
		{
		    keyOut.write("  Blank " + j + " Correct Answers:\n");
		    for (String k : ((FIBQuestion) q).getCorrectAnswers()
			    .get(j))
			keyOut.write("    " + k + "\n");
		}

	    keyOut.write("\n");
	}

	keyOut.close();
    }
}
