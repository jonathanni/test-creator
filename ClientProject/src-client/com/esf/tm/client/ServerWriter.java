package com.esf.tm.client;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.esf.tm.message.Message;

public class ServerWriter implements Runnable
{
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	private ObjectOutputStream out;

	public volatile boolean isRunning;

	public ServerWriter(Socket socket)
	{
		try
		{
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
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
				out.writeObject(queue.take());
				out.flush();
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
