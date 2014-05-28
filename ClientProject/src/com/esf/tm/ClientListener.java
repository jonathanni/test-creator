package com.esf.tm;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

public class ClientListener implements Runnable
{
    public volatile boolean isRunning;

    public ClientListener()
    {
	isRunning = true;
    }

    @Override
    public void run()
    {
	ServerSocket server = null;
	try
	{
	    server = ServerSocketFactory.getDefault().createServerSocket(
		    TestGenerator.PORT);
	} catch (IOException e)
	{
	    e.printStackTrace();
	}

	try
	{
	    while (isRunning)
	    {
		Socket socket = server.accept();
		try
		{
		    TestGenerator.getInstance().getClients()
			    .add(new ClientCommunicator(socket));
		} catch (EOFException e)
		{
		    TestGenerator
			    .getInstance()
			    .getClients()
			    .remove(TestGenerator.getInstance().getClients()
				    .size() - 1);
		}
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

}
