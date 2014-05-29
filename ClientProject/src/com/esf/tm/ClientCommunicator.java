package com.esf.tm;

import java.io.IOException;
import java.net.Socket;

public class ClientCommunicator
{
	private ClientReader reader;
	private ClientWriter writer;

	private Test test;

	public volatile boolean isRunning = true;

	public ClientCommunicator(Socket socket) throws IOException
	{
		new Thread(writer = new ClientWriter(socket)).start();
		new Thread(reader = new ClientReader(socket)).start();
	}

	public ClientReader getReader()
	{
		return reader;
	}

	public ClientWriter getWriter()
	{
		return writer;
	}

	public Test getTest()
	{
		return test;
	}

	public Test setTest(Test test)
	{
		return this.test = test;
	}
}
