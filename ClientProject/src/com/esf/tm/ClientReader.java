package com.esf.tm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.esf.tm.serializable.Message;

public class ClientReader implements Runnable
{
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	private ObjectInputStream in;

	public volatile boolean isRunning;

	public ClientReader(Socket socket) throws IOException
	{
		in = new ObjectInputStream(socket.getInputStream());

		isRunning = true;
	}

	@Override
	public void run()
	{
		try
		{
			while (isRunning)
			{// TODO parse input
				System.out.println("READ");
				queue.add((Message) in.readObject());
				System.out.println("RUN");
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
