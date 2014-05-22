package com.esf.tm.client;

/**
 * 
 * True/False question.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r2
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
     * Copy constructor.
     * 
     * @param other
     *            the question
     */

    public TFQuestion(TFQuestion other)
    {
	this(other.getMessage(), other.getQuestionID());
	setCorrectAnswer(other.getCorrectAnswer());
    }

    /**
     * 
     * Copy constructor from Question.
     * 
     * @param other
     *            the question
     */

    public TFQuestion(Question other)
    {
	super(other);
    }

    /**
     * 
     * Gets the correct answer as a boolean, since this is a T/F question.
     * 
     * @return the answer
     */

    public boolean getCorrectAnswer()
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

    @Override
    public String toString()
    {
	return super.toString() + "Correct answer: " + correctAnswer + "\n";
    }
}
