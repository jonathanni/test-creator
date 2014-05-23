package com.esf.tm;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientWriter implements Runnable
{
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	private ObjectOutputStream out;

	public volatile boolean isRunning;

	public ClientWriter(Socket socket)
	{
		try
		{
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		isRunning = true;
	}

	@Override
	public void run()
	{
		try
		{
			while (isRunning)
			{
				out.writeObject(queue.poll());
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

	LinkedBlockingQueue<Message> getQueue()
	{
		return queue;
	}
}