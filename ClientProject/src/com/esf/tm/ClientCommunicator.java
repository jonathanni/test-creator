package com.esf.tm;

import java.net.Socket;

public class ClientCommunicator
{
    private ClientReader reader;
    private ClientWriter writer;

    public ClientCommunicator(Socket socket)
    {
	reader = new ClientReader(socket);
	writer = new ClientWriter(socket);
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
