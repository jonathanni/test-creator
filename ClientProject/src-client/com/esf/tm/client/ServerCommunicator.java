package com.esf.tm.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class ServerCommunicator
{

	private Socket socket;

	private ServerReader reader;
	private ServerWriter writer;

	public ServerCommunicator(String ip)
	{
		try
		{
			socket = SocketFactory.getDefault().createSocket();
			socket.setReuseAddress(true);
			socket.connect(new InetSocketAddress(ip, TestTaker.PORT), 1000);
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		new Thread(writer = new ServerWriter(socket)).start();
		new Thread(reader = new ServerReader(socket)).start();
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
