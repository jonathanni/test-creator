package com.esf.tm;

public class ClientProcessor implements Runnable
{
    public volatile boolean isRunning;

    public ClientProcessor()
    {
	isRunning = true;
    }

    @Override
    public void run()
    {
	try
	{
	    while (isRunning)
	    {
		for (ClientCommunicator i : TestGenerator.getInstance()
			.getClients())
		{
		    if (!i.getReader().getQueue().isEmpty())
			processMessage(i, i.getReader().getQueue().poll());
		}
		Thread.sleep(1000);
	    }
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }

    private void processMessage(ClientCommunicator cc, Message message)
    {
	String header = message.getHeader();
	Object payload = message.getPayload();

	if (header.equals("requestLogin"))
	    cc.getWriter().getQueue().add(new Message("requestPassword", null));
	else if (header.equals("login"))
	    if (((String) payload).equals(TestGenerator.PASSWORD))
	    {
		cc.getWriter().getQueue()
			.add(new Message("loginSuccess", null));
		cc.getWriter()
			.getQueue()
			.add(new Message("receiveTest", TestGenerator
				.getInstance().getTest()));
	    } else
	    {
		cc.getWriter().getQueue()
			.add(new Message("loginFailure", null));
		cc.getWriter().getQueue()
			.add(new Message("requestPassword", null));
	    }
    }
}
