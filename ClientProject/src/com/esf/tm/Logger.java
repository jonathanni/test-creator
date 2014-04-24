package com.esf.tm;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * Internal class used to output information to streams.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r1
 * 
 */

class Logger
{
	private OutputStream normStream, errorStream;

	/**
	 * Creates a default Logger. Usually, parts of this software would use the
	 * default logger, which uses System.out as the non-error stream and
	 * System.err as the error stream.
	 */

	public Logger()
	{
		this(System.out, System.err);
	}

	/**
	 * 
	 * Creates a Logger and allows you to specify the streams for urgent and
	 * non-urgent writing.
	 * 
	 * @param normalStream
	 *            the non-urgent stream
	 * @param errorStream
	 *            the urgent stream
	 */
	public Logger(OutputStream normalStream, OutputStream errorStream)
	{
		this.normStream = normalStream;
		this.errorStream = errorStream;
	}

	/**
	 * 
	 * Write information non-urgently.
	 * 
	 * @param o
	 *            the information being written
	 */

	void info(Object o)
	{
		// Write to generic stream
		try
		{
			normStream.write(("[I] " + o.toString()).getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Write information urgently, but one should not stop the program after
	 * this.
	 * 
	 * @param o
	 *            the information being written
	 */

	void warn(Object o)
	{
		try
		{
			errorStream.write(("[W] " + o.toString()).getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Write information urgently. The program should terminate after this, but
	 * not as a result of calling this function.
	 * 
	 * @param o
	 *            the information being written
	 */
	void error(Object o)
	{
		try
		{
			errorStream.write(("[E] " + o.toString()).getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
