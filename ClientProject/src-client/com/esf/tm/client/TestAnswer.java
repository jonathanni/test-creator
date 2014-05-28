package com.esf.tm.client;


import java.util.ArrayList;

public class TestAnswer
{
	private ArrayList<Object> answers;
	
	public TestAnswer(int length)
	{
		answers = new ArrayList<Object>(length);
	}

	public ArrayList<Object> getAnswers()
	{
		return answers;
	}
}
