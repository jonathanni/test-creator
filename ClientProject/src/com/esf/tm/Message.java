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
}
