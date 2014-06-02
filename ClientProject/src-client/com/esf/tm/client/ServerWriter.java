package com.esf.tm.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.esf.tm.serializable.Message;

/**
 * 
 * Writer to write to a server.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class ServerWriter implements Runnable
{
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	private ObjectOutputStream out;

	public volatile boolean isRunning;

	/**
	 * 
	 * Creates a new server writer bound to a socket.
	 * 
	 * @param socket
	 *            the socket
	 */

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

	/**
	 * 
	 * Runs the server writing loop. Blocks to try to write an object from the
	 * queue.
	 * 
	 */

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
