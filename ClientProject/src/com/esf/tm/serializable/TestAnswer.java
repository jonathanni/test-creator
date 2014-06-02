package com.esf.tm.serializable;

import java.io.Serializable;
import java.util.ArrayList;

public class TestAnswer implements Serializable
{
	private static final long serialVersionUID = -3919271939347719877L;
	private ArrayList<Object> answers;

	public TestAnswer(int length)
	{
		answers = new ArrayList<Object>(length);
		for (int i = 0; i < length; i++)
			answers.add(null);
	}

	public ArrayList<Object> getAnswers()
	{
		return answers;
	}
	
	@Override
	public String toString()
	{
		return answers.toString();
	}
}
