package com.esf.tm.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SingleNodeScanner implements Runnable
{
    public volatile boolean isDone, isUp;

    private String host;

    public SingleNodeScanner(String host)
    {
	this.host = host;
    }

    @Override
    public void run()
    {
	Socket s = null;
	try
	{
	    s = new Socket(InetAddress.getByName(host), TestTaker.PORT);
	    System.out.println("HI");
	    isDone = true;
	    return;
	} catch (UnknownHostException e)
	{
	} catch (IOException e)
	{
	    isDone = true;
	} finally
	{
	    if (s != null)
		try
		{
		    s.close();
		} catch (IOException e)
		{
		}
	}
    }

    public String getHost()
    {
	return host;
    }
}
