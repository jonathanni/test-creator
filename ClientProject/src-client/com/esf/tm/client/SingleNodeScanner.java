package com.esf.tm.client;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class SingleNodeScanner implements Runnable
{
	public volatile boolean isDone, isUp;

	private String host;

	public SingleNodeScanner(String host)
	{
		this.host = host;
	}

	@Override
	public void run()
	{
		Socket s = null;
		try
		{
			s = new Socket();
			s.setReuseAddress(true);
			s.connect(new InetSocketAddress(InetAddress.getByName(host),
					TestTaker.PORT), 1000);
		} catch (UnknownHostException e)
		{
		} catch (IOException e)
		{
			// No server on the other side...
			if (e instanceof SocketTimeoutException)
				isUp = false;
		} finally
		{
			if (s != null)
			{
				if (s.isConnected())
					isUp = true;
				else
					isUp = false;

				try
				{
					s.close();
				} catch (IOException e)
				{
				}
			}

			isDone = true;
		}
	}

	public String getHost()
	{
		return host;
	}
}
