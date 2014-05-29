package com.esf.tm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

/**
 * 
 * Listener to pick up client connections.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class ClientListener implements Runnable
{
    public volatile boolean isRunning;

    /**
     * 
     * Creates a new client listener.
     * 
     */

    public ClientListener()
    {
	isRunning = true;
    }

    /**
     * 
     * Runs the client detection loop. Specifically, creates a new server
     * socket, then starts adding clients when clients connect.
     * 
     */

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
		} catch (IOException e)
		{
		    socket.close();
		    e.printStackTrace();
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
