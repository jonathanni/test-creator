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
				boolean flag = true;
				for (ClientCommunicator i : TestGenerator.getInstance()
						.getClients())
				{
					if (i.isRunning && !i.getReader().getQueue().isEmpty())
					{
						processMessage(i, i.getReader().getQueue().poll());
						flag = false;
					}
				}

				if (flag)
					isRunning = false;

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
			if (((String[]) payload)[1].equals(TestGenerator.PASSWORD))
			{
				// TODO accept username
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
		else if (header.equals("status"))
		{
			// TODO figure out status checks
		} else if (header.equals("sendTest"))
		{
			// TODO check test
		} else if (header.equals("exit"))
		{
			cc.getWriter().getQueue().add(new Message("exit", null));

			cc.isRunning = false;

			cc.getReader().isRunning = false;
			cc.getWriter().isRunning = false;
		}
	}
}
