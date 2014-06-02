package com.esf.tm.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * 
 * Scans a local network to try to find available nodes.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class NodeScanner implements Runnable
{
    public volatile boolean isRunning;

    /**
     * 
     * Creates a new node scanner.
     * 
     */

    public NodeScanner()
    {
	isRunning = true;
    }

    /**
     * 
     * Runs the node scanning loop. Goes through 0 thru 255 on the subnet
     * (hopefully XXX.XXX.XXX.XXX/24) and creates a new single node scanner for
     * each IP. Loops through all the single node scanners and picks the ones
     * that succeed.
     * 
     */

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

	// DEBUG
	subnet = "192.168.88";
	
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

		populateList(hosts);

		System.out.println(hosts);
		Thread.sleep(15000);
	    }
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * 
     * Populates the JList of the TestTaker with the nodes.
     * 
     * @param hosts
     *            all the available hosts
     */

    private void populateList(ArrayList<String> hosts)
    {
	TestTaker.getInstance().clearList();

	for (String ip : hosts)
	    TestTaker.getInstance().appendIP(ip);
    }
}
