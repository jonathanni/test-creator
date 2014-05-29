package com.esf.tm;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

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
			server = ServerSocketFactory.getDefault().createServerSocket(
					TestGenerator.PORT);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			while (isRunning)
			{
				Socket socket = server.accept();
				try
				{
					System.out.println("ADDING CLIENT");
					TestGenerator.getInstance().getClients()
							.add(new ClientCommunicator(socket));
					System.out.println("ADDED");
				} catch (IOException e)
				{
					socket.close();
					System.out.println("Dead client");
					e.printStackTrace();
				}
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
