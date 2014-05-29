package com.esf.tm.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

/**
 * 
 * Wrapper for the reader and writer to the server.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class ServerCommunicator
{

    private Socket socket;

    private ServerReader reader;
    private ServerWriter writer;

    /**
     * 
     * Creates a new server communicator bound to an IP.
     * 
     * @param ip
     *            the IP
     */

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

    /**
     * 
     * Gets the reader associated with the communicator.
     * 
     * @return the reader
     */

    public ServerReader getReader()
    {
	return reader;
    }

    /**
     * 
     * Gets the writer associated with the communicator.
     * 
     * @return the writer
     */

    public ServerWriter getWriter()
    {
	return writer;
    }
}
