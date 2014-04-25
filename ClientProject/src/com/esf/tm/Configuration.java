package com.esf.tm;

/**
 * 
 * Configuration options for the test generator.
 * 
 * @author Jonathan Ni
 * @since 4/25/14
 * @version 0.0r1
 * 
 */

class Configuration
{
    private static boolean isLetterChoice;

    /**
     * 
     * Gets if the test should be given with letter choices instead of #
     * choices.
     * 
     * @return the flag
     */

    public static boolean isLetterChoice()
    {
	return isLetterChoice;
    }

    /**
     * 
     * Sets if the test should be given with letter choices instead of #
     * choices.
     * 
     * @param isLetterChoice
     *            the flag
     */

    public static void setLetterChoice(boolean isLetterChoice)
    {
	Configuration.isLetterChoice = isLetterChoice;
    }
}
