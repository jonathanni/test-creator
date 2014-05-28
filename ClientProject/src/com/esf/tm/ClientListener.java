package com.esf.tm;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class ClientListener implements Runnable
{
	public volatile boolean isRunning;

	public ClientListener()
	{
		isRunning = true;
	}

	@Override
	public void run()
	{	
		ServerSocket server = null;
		try
		{
			server = SSLServerSocketFactory.getDefault().createServerSocket(
					TestGenerator.PORT);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			while (isRunning)
			{
				SSLSocket socket = (SSLSocket) server.accept();
				TestGenerator.getInstance().getClients()
						.add(new ClientCommunicator(socket));
				Thread.sleep(10);
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
