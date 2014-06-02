package com.esf.tm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

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

public class Util
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

	public static String stackTraceToString(Exception e)
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

	/**
	 * 
	 * Gets the median of an array.
	 * 
	 * @param numArray
	 *            the array
	 * @return the median
	 */

	static double median(int[] numArray)
	{
		int[] sortedArray = Arrays.copyOf(numArray, numArray.length);
		Arrays.sort(sortedArray);

		double median;
		if (numArray.length % 2 == 0)
			median = ((double) numArray[numArray.length / 2] + (double) numArray[numArray.length / 2 - 1]) / 2;
		else
			median = (double) numArray[numArray.length / 2];

		return median;
	}
}
