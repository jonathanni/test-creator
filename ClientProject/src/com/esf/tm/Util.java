package com.esf.tm;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * Internal class used by other classes within this package. Contains utility
 * functions.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r1
 * 
 */

class Util
{
	/**
	 * 
	 * Gets the stack trace from an Exception and puts it into a String.
	 * 
	 * @param e
	 *            the exception
	 * @return the stack trace as a String
	 */

	static String stackTraceToString(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		e.printStackTrace(pw);
		return sw.toString();
	}
}
