package com.esf.tm;

import java.io.IOException;
import java.net.Socket;

import com.esf.tm.serializable.Test;

/**
 * 
 * Wrapper for the reader and writer to the client.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class ClientCommunicator
{
	private ClientReader reader;
	private ClientWriter writer;

	private Test test;

	private String name;

	public volatile boolean isRunning = true;

	/**
	 * 
	 * Creates a new communicator with the client bound to a socket. Initializes
	 * threads to run the readers and writers.
	 * 
	 * @param socket
	 *            the socket
	 * @throws IOException
	 */

	public ClientCommunicator(Socket socket) throws IOException
	{
		new Thread(writer = new ClientWriter(socket)).start();
		new Thread(reader = new ClientReader(socket)).start();
	}

	/**
	 * 
	 * Gets the reader associated with the communicator.
	 * 
	 * @return the reader
	 */

	public ClientReader getReader()
	{
		return reader;
	}

	/**
	 * 
	 * Gets the writer associated with the communicator.
	 * 
	 * @return the writer
	 */

	public ClientWriter getWriter()
	{
		return writer;
	}

	/**
	 * 
	 * Gets the test associated with the communicator.
	 * 
	 * @return the test
	 */

	public Test getTest()
	{
		return test;
	}

	/**
	 * 
	 * Sets the test associated with the communicator.
	 * 
	 * @param test
	 *            the test
	 * @return the test that was set
	 */

	public Test setTest(Test test)
	{
		return this.test = test;
	}

	/**
	 * 
	 * Gets the username of whom this is communicating to.
	 * 
	 * @return the username
	 */

	public String getName()
	{
		return name;
	}

	/**
	 * 
	 * Sets the username of whom this is communicating to.
	 * 
	 * @param name
	 *            the username
	 */

	public void setName(String name)
	{
		this.name = name;
	}
}
