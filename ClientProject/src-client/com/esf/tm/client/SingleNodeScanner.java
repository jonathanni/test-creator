package com.esf.tm.client;

import java.io.IOException;
import java.net.InetAddress;
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
	try
	{
	    if (InetAddress.getByName(host).isReachable(500))
		isUp = true;
	} catch (UnknownHostException e)
	{
	    e.printStackTrace();
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
	isDone = true;
    }

    public String getHost()
    {
	return host;
    }
}
