package com.esf.tm.client;


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

class Question
{
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

    String getMessage()
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

    void setMessage(String message)
    {
	this.message = message;
    }

    /**
     * 
     * Gets the unique ID associated with this Question.
     * 
     * @return the ID
     */

    int getQuestionID()
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

    void setQuestionID(int questionID)
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
