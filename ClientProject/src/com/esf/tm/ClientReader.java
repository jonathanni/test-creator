package com.esf.tm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.esf.tm.serializable.Message;

/**
 * 
 * Reader to read from a client.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class ClientReader implements Runnable
{
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	private ObjectInputStream in;

	public volatile boolean isRunning;

	/**
	 * 
	 * Creates a new client reader bound to a socket.
	 * 
	 * @param socket
	 *            the socket
	 * @throws IOException
	 */

	public ClientReader(Socket socket) throws IOException
	{
		in = new ObjectInputStream(socket.getInputStream());

		isRunning = true;
	}

	/**
	 * 
	 * Runs the client reading loop. Blocks to try to read from the input
	 * stream.
	 * 
	 */

	@Override
	public void run()
	{
		try
		{
			while (isRunning)
			{
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

	/**
	 * 
	 * Gets the queue associated with the reader.
	 * 
	 * @return the queue
	 */

	LinkedBlockingQueue<Message> getQueue()
	{
		return queue;
	}
}
