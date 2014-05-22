package com.esf.tm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLServerSocketFactory;

public class ClientCommunicator
{

	private ServerSocket socket;

	private ClientReader reader;
	private ClientWriter writer;

	public ClientCommunicator(String ip)
	{
		try
		{
			socket = SSLServerSocketFactory.getDefault().createServerSocket(
					TestGenerator.PORT);
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		Socket acceptedSocket = null;
		try
		{
			acceptedSocket = socket.accept();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		reader = new ClientReader(acceptedSocket);
		writer = new ClientWriter(acceptedSocket);
	}

	public ClientReader getReader()
	{
		return reader;
	}

	public ClientWriter getWriter()
	{
		return writer;
	}
}
