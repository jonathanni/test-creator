package com.esf.tm;

public class Message
{
	private String header;
	private Object payload;

	public Message(String header, Object payload)
	{
		this.header = header;
		this.payload = payload;
	}
}
