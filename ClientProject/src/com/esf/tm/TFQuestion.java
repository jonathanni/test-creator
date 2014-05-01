package com.esf.tm;

/**
 * 
 * True/False question.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r1
 * 
 */

public class TFQuestion extends Question
{

    private boolean correctAnswer;

    /**
     * 
     * See {@link Question#Question(String, int)}
     * 
     * @param message
     * @param ID
     */

    public TFQuestion(String message, int ID)
    {
	super(message, ID);
    }

    /**
     * 
     * Gets the correct answer as a boolean, since this is a T/F question.
     * 
     * @return the answer
     */

    public boolean isCorrectAnswer()
    {
	return correctAnswer;
    }

    /**
     * 
     * Sets the correct answer to this question as a boolean, since this is a
     * T/F question.
     * 
     * @param correctAnswer
     *            the answer
     */

    public void setCorrectAnswer(boolean correctAnswer)
    {
	this.correctAnswer = correctAnswer;
    }
}
