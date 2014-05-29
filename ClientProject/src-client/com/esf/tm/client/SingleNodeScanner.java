package com.esf.tm.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

/**
 * 
 * Scans a single node (IP) to see if it's up.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class SingleNodeScanner implements Runnable
{
    public volatile boolean isDone, isUp;

    private String host;

    /**
     * 
     * Creates a new single node scanner for an IP
     * 
     * @param host
     *            the IP
     */

    public SingleNodeScanner(String host)
    {
	this.host = host;
    }

    /**
     * 
     * Runs a scan on one IP. Tries to connect to it with a socket, and if it
     * times out, it is not up. If it cannot connect, it also is not up. Sets
     * the status isUp and then sets that it isDone.
     * 
     */

    @Override
    public void run()
    {
	Socket s = null;
	try
	{
	    s = SocketFactory.getDefault().createSocket();
	    s.setReuseAddress(true);
	    s.connect(new InetSocketAddress(InetAddress.getByName(host),
		    TestTaker.PORT), 1000);
	} catch (UnknownHostException e)
	{
	} catch (IOException e)
	{
	    // No server on the other side...
	    if (e instanceof SocketTimeoutException)
		isUp = false;
	} finally
	{
	    if (s != null)
	    {
		if (s.isConnected())
		{
		    ObjectOutputStream out;
		    try
		    {
			out = new ObjectOutputStream(s.getOutputStream());
			out.flush();
			out.close();
		    } catch (IOException e)
		    {
			e.printStackTrace();
		    }
		    isUp = true;
		} else
		    isUp = false;

		try
		{
		    Thread.sleep(1000);
		} catch (InterruptedException e1)
		{
		    e1.printStackTrace();
		}

		try
		{
		    s.close();
		} catch (IOException e)
		{
		}
	    }

	    isDone = true;
	}
    }

    public String getHost()
    {
	return host;
    }
}
