package com.esf.tm.client;

import java.io.Serializable;

public class Message implements Serializable
{
	
	private static final long serialVersionUID = 32L;
	
	private String header;
	private Object payload;

	public Message(String header, Object payload)
	{
		this.setHeader(header);
		this.setPayload(payload);
	}

	public String getHeader()
	{
		return header;
	}

	public void setHeader(String header)
	{
		this.header = header;
	}

	public Object getPayload()
	{
		return payload;
	}

	public void setPayload(Object payload)
	{
		this.payload = payload;
	}

	@Override
	public String toString()
	{
		return this.header + " " + this.payload;
	}
}
