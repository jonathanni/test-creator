package com.esf.tm.client;

import com.esf.tm.serializable.Message;
import com.esf.tm.serializable.Status;

/**
 * 
 * Continuously checks the status of the test taker and sends status packets.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class StatusChecker implements Runnable
{
	public volatile boolean isRunning;

	/**
	 * 
	 * Creates a new status checker.
	 * 
	 */

	public StatusChecker()
	{
		isRunning = true;
	}

	/**
	 * 
	 * Run the status checking loop.
	 * 
	 */

	@Override
	public void run()
	{
		try
		{
			while (isRunning)
			{
				TestTaker
						.getInstance()
						.getCommunicator()
						.getWriter()
						.getQueue()
						.add(new Message("status", TestTaker.getInstance()
								.getCurrentQuestion()));
				Thread.sleep(1000);
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
