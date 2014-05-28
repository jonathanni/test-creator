package com.esf.tm.client;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerCommunicator
{

	private Socket socket;

	private ServerReader reader;
	private ServerWriter writer;

	public ServerCommunicator(String ip)
	{
		try
		{
			socket = new Socket(ip, TestTaker.PORT);
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		reader = new ServerReader(socket);
		writer = new ServerWriter(socket);
	}

	public ServerReader getReader()
	{
		return reader;
	}

	public ServerWriter getWriter()
	{
		return writer;
	}
}
