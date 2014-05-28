package com.esf.tm.client;


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
    public static String RESTRICTED_CHARACTERS = "[<>:\"/\\\\\\|\\?\\*]";

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

    /**
     * 
     * Count all occurrences of non-overlapping subStr in str.
     * http://rosettacode.org/wiki/Count_occurrences_of_a_substring#Java
     * 
     * @param subStr
     *            the substring
     * @param str
     *            the string
     * @return the number of occurences
     */

    static int countSubstring(String subStr, String str)
    {
	return (str.length() - str.replace(subStr, "").length())
		/ subStr.length();
    }

    /**
     * 
     * Modify the URI so that it is ready to be put as a file name.
     * 
     * @param URI
     *            the file name
     * @return the converted file name
     */

    static String encodeURI(String URI)
    {
	return URI.replaceAll(" ", "_");
    }
}
