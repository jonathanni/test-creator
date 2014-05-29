package com.esf.tm.client;

import com.esf.tm.message.Message;


public class StatusChecker implements Runnable
{
    public volatile boolean isRunning;

    public StatusChecker()
    {
	isRunning = true;
    }

    @Override
    public void run()
    {
	Status status = new Status();

	try
	{
	    while (isRunning)
	    {
		status.setCurrentQuestion(TestTaker.getInstance()
			.getCurrentQuestion());
		TestTaker.getInstance().getCommunicator().getWriter()
			.getQueue().add(new Message("status", status));
		Thread.sleep(1000);
	    }
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }
}
