package com.esf.tm.client;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.esf.tm.serializable.Message;

public class ServerReader implements Runnable
{
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	private ObjectInputStream in;

	public volatile boolean isRunning;

	public ServerReader(Socket socket)
	{
		try
		{
			in = new ObjectInputStream(socket.getInputStream());
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
			{// TODO parse input
				queue.add((Message) in.readObject());
				Thread.sleep(10);
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	LinkedBlockingQueue<Message> getQueue()
	{
		return queue;
	}
}
