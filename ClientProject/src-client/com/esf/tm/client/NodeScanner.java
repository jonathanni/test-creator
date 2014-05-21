package com.esf.tm.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NodeScanner implements Runnable
{
    public volatile boolean isRunning;

    public NodeScanner()
    {
	isRunning = true;
    }

    @Override
    public void run()
    {
	byte[] ip = null;
	try
	{
	    ip = InetAddress.getLocalHost().getAddress();
	} catch (UnknownHostException e)
	{
	    e.printStackTrace();
	}

	String subnet = (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "."
		+ (ip[2] & 0xFF);
	try
	{
	    while (isRunning)
	    {
		ArrayList<String> hosts = new ArrayList<String>();
		SingleNodeScanner[] snScanners = new SingleNodeScanner[256];
		for (int i = 0; i < 256; i++)
		    new Thread(snScanners[i] = new SingleNodeScanner(subnet
			    + "." + i)).start();

		Thread.sleep(1000);

		// Wait until everything is done
		boolean flag = true;
		while (flag)
		{
		    flag = false;

		    for (int i = 0; i < 256; i++)
			if (!snScanners[i].isDone)
			    flag = true;
		}

		for (int i = 0; i < 256; i++)
		    if (snScanners[i].isUp)
			hosts.add(snScanners[i].getHost());

		System.out.println(hosts);
		Thread.sleep(10000);
	    }
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }
}
